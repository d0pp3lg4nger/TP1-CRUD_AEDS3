package aed3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;

public class Arquivo<T extends Registro> {

    RandomAccessFile arquivo;
    String nomeArquivo;
    final int TAM_CABECALHO = 4;
    Constructor<T> construtor;
    HashExtensivel<ParIDEndereco> indiceDireto;

    public Arquivo(Constructor<T> c, String n) throws Exception {
        // Cria o diretório, se necessário
        File f = new File(".\\dados");
        if(!f.exists())
            f.mkdir();

        this.nomeArquivo = n;
        this.construtor = c;
        arquivo = new RandomAccessFile(".\\dados\\"+n+".db", "rw");
        if(arquivo.length()<TAM_CABECALHO)
            arquivo.writeInt(0);

        indiceDireto = new HashExtensivel<>(
            ParIDEndereco.class.getConstructor(), 
            4,   // tamanho do cesto
            ".\\dados\\"+n+".hash_d.db",   // arquivo para o diretório
            ".\\dados\\"+n+".hash_c.db"    // arquivo de cestos
        );
        
    }

    public void create(T entidade) throws Exception {

        // Determina o ID
        arquivo.seek(0);
        int novoId = arquivo.readInt() + 1;
        entidade.setId(novoId);
        arquivo.seek(0);
        arquivo.writeInt(novoId);

        // Grava o novo registro
        arquivo.seek(arquivo.length());
        long endereco = arquivo.getFilePointer();
        byte[] b = entidade.toByteArray();
        arquivo.writeByte(' ');
        arquivo.writeShort((short)b.length);
        arquivo.write(b);

        // Insere o registro no índice
        indiceDireto.create(new ParIDEndereco(novoId, endereco));
    }

    public T read(int id) throws Exception {

        short tam;
        byte[] b;
        byte lapide;
        T entidade = this.construtor.newInstance();

        ParIDEndereco pie = indiceDireto.read(id);
        if(pie!=null) {
            arquivo.seek(pie.getEndereco());
            lapide = arquivo.readByte();
            if(lapide==' ') {
                tam = arquivo.readShort();
                b = new byte[tam];
                arquivo.read(b);
                entidade.fromByteArray(b);
                if(entidade.getId()==id)
                    return entidade; 
            }
        }
        return null;
    }
    

    public boolean delete(int id) throws Exception {

        short tam;
        byte[] b;
        byte lapide;
        T entidade = this.construtor.newInstance();
        
        ParIDEndereco pie = indiceDireto.read(id);
        if(pie!=null) {
            arquivo.seek(pie.getEndereco());
            lapide = arquivo.readByte();
            if(lapide==' ') {               
                tam = arquivo.readShort();
                b = new byte[tam];
                arquivo.read(b);
                entidade.fromByteArray(b);
                if(entidade.getId()==id) {
                    arquivo.seek(pie.getEndereco());
                    arquivo.writeByte('*');
                    indiceDireto.delete(id);
                    return true;
                }
            }
        }
        return false; 
    }
    

    public boolean update(T novaEntidade) throws Exception {

        short tam;
        byte[] b;
        byte lapide;
        T entidade = this.construtor.newInstance();
        ParIDEndereco pie = indiceDireto.read(novaEntidade.getId());
        if(pie != null) {
            arquivo.seek(pie.getEndereco());
            lapide = arquivo.readByte();
            if(lapide==' ') {                
                tam = arquivo.readShort();
                b = new byte[tam];
                arquivo.read(b);
                entidade.fromByteArray(b);
                if(entidade.getId()==novaEntidade.getId()) {
                    byte[] b2 = novaEntidade.toByteArray();
                    int tam2 = b2.length;
                    if(tam2<=tam){
                        arquivo.seek(pie.getEndereco()+3);
                        arquivo.write(b2);
                    } else {
                        arquivo.seek(pie.getEndereco());
                        arquivo.writeByte('*');  
                        arquivo.seek(arquivo.length());
                        long novoEndereco = arquivo.getFilePointer();
                        arquivo.writeByte(' ');
                        arquivo.writeShort(tam2);
                        arquivo.write(b2); 
                        indiceDireto.update(new ParIDEndereco(novaEntidade.getId(), novoEndereco));                     
                    }
                    return true;
                }
            }
        }
        return false; 
    }

    // Intercalação balanceada de 3 caminhos
    public void reorganizar() throws Exception {

        byte lapide;
        short tam;
        byte[] dados;
        T entidade;

        // Armazena o cabeçalho do arquivo
        arquivo.seek(0);
        int ultimoID = arquivo.readInt();
        

        // Primeira etapa
        // Distribuição
        int tamanhoLista = 3;
        ArrayList<T> lista = new ArrayList<>(tamanhoLista);

        DataOutputStream out1 = new DataOutputStream(new FileOutputStream(".\\dados\\temp1.db"));
        DataOutputStream out2 = new DataOutputStream(new FileOutputStream(".\\dados\\temp2.db"));
        DataOutputStream out3 = new DataOutputStream(new FileOutputStream(".\\dados\\temp3.db"));
        DataOutputStream out = out1;  // aponta para um dos DOS anterioes
        int seletor = 0; // aponta para o arquivo temporário em que os dados serão escritos

        while(arquivo.getFilePointer()<arquivo.length()) {

            // enche a lista
            while(lista.size()<tamanhoLista && arquivo.getFilePointer()<arquivo.length()) {
                lapide = arquivo.readByte();
                tam = arquivo.readShort();
                dados = new byte[tam];
                arquivo.read(dados);
                if(lapide == ' ') {
                    entidade = construtor.newInstance();
                    entidade.fromByteArray(dados);
                    lista.add(entidade);
                }
            }
            Collections.sort(lista);

            // retira o elemento da lista e coloca no arquivo temporário
            while(lista.size()>0) {
                entidade = lista.remove(0);
                dados = entidade.toByteArray();
                out.writeShort(dados.length);
                out.write(dados);
            }

            seletor++;
            if(seletor>2) seletor = 0;
            switch (seletor) {
                case 0: out = out1; 
                        break;
                case 1: out = out2;
                        break;
                case 2: out = out3;
                        break;
            }
        }
        out1.close();
        out2.close();
        out3.close();

        // Segunda fase
        // Intercalações
        DataInputStream in1, in2, in3;
        boolean maisIntercalacoes = true;
        boolean sentido = true;
        boolean terminou1,terminou2,terminou3;
        boolean compara1,compara2,compara3;
        boolean mudou1, mudou2, mudou3;
        T ent1 = construtor.newInstance(),
          ent2 = construtor.newInstance(),
          ent3 = construtor.newInstance();
          ent1.setId(-1);
          ent2.setId(-1);
          ent3.setId(-1);
        T entAnterior1 = construtor.newInstance(),
          entAnterior2 = construtor.newInstance(),
          entAnterior3 = construtor.newInstance();
          entAnterior1.setId(-1);
          entAnterior2.setId(-1);
          entAnterior3.setId(-1);

        while(maisIntercalacoes) {
            maisIntercalacoes = false;
            if(sentido) {
                in1 = new DataInputStream(new FileInputStream(".\\dados\\temp1.db"));
                in2 = new DataInputStream(new FileInputStream(".\\dados\\temp2.db"));
                in3 = new DataInputStream(new FileInputStream(".\\dados\\temp3.db"));
                out1 = new DataOutputStream(new FileOutputStream(".\\dados\\temp4.db"));
                out2 = new DataOutputStream(new FileOutputStream(".\\dados\\temp5.db"));
                out3 = new DataOutputStream(new FileOutputStream(".\\dados\\temp6.db"));
            } else {
                in1 = new DataInputStream(new FileInputStream(".\\dados\\temp4.db"));
                in2 = new DataInputStream(new FileInputStream(".\\dados\\temp5.db"));
                in3 = new DataInputStream(new FileInputStream(".\\dados\\temp6.db"));
                out1 = new DataOutputStream(new FileOutputStream(".\\dados\\temp1.db"));
                out2 = new DataOutputStream(new FileOutputStream(".\\dados\\temp2.db"));
                out3 = new DataOutputStream(new FileOutputStream(".\\dados\\temp3.db"));
            }
            out = out1;
            seletor = 0;

            // intercalação
            mudou1 = true;
            mudou2 = true;
            mudou3 = true;
            terminou1 = false;
            terminou2 = false;
            terminou3 = false;
            compara1 = true;
            compara2 = true;
            compara3 = true;
            while(!terminou1 || !terminou2 || !terminou3) {
                if(!terminou1) 
                    compara1 = true;
                if(!terminou2) 
                    compara2 = true;
                if(!terminou3) 
                    compara3 = true;

                while(compara1 || compara2 || compara3) {
                    if(mudou1) {
                        mudou1 = false;
                        ent1 = construtor.newInstance();
                        try {
                            tam = in1.readShort();
                            dados = new byte[tam];
                            in1.read(dados);
                            ent1.fromByteArray(dados);
                            if(ent1.compareTo(entAnterior1)<0) {
                                compara1 = false;
                            }
                            entAnterior1 = ent1;
                        } catch(EOFException e) {
                            terminou1 = true;
                            compara1 = false;
                            ent1 = null;
                        }
                    }
                    if(mudou2) {
                        mudou2 = false;
                        ent2 = construtor.newInstance();
                        try {
                            tam = in2.readShort();
                            dados = new byte[tam];
                            in2.read(dados);
                            ent2.fromByteArray(dados);
                            if(ent2.compareTo(entAnterior2)<0) {
                                compara2 = false;
                            }
                            entAnterior2 = ent2;
                        } catch(EOFException e) {
                            terminou2 = true;
                            compara2 = false;
                            ent2 = null;
                        }
                    }
                    if(mudou3) {
                        mudou3 = false;
                        ent3 = construtor.newInstance();
                        try {
                            tam = in3.readShort();
                            dados = new byte[tam];
                            in3.read(dados);
                            ent3.fromByteArray(dados);
                            if(ent3.compareTo(entAnterior3)<0) {
                                compara3 = false;
                            }
                            entAnterior3 = ent3;
                        } catch(EOFException e) {
                            terminou3 = true;
                            compara3 = false;
                            ent3 = null;
                        }
                    }

                    // primeira entidade é a menor
                    if(compara1 && (!compara2 || ent1.compareTo(ent2)<0) && (!compara3 || ent1.compareTo(ent3)<0)) {
                        dados = ent1.toByteArray();
                        out.writeShort(dados.length);
                        out.write(dados);
                        mudou1 = true;
                    }
                    // segunda entidade é a menor
                    else if(compara2 && (!compara1 || ent2.compareTo(ent1)<0) && (!compara3 || ent2.compareTo(ent3)<0)) {
                        dados = ent2.toByteArray();
                        out.writeShort(dados.length);
                        out.write(dados);
                        mudou2 = true;
                    }
                    // terceira entidade é a menor
                    else if(compara3) {
                        dados = ent3.toByteArray();
                        out.writeShort(dados.length);
                        out.write(dados);
                        mudou3 = true;
                    }

                    if(!compara1 && !compara2 && !compara3) {
                        seletor++;
                        if(seletor>2) seletor = 0;
                        switch (seletor) {
                            case 0: out = out1; 
                                    break;
                            case 1: out = out2;
                                    if(!terminou1 || !terminou2 || !terminou3)
                                        maisIntercalacoes = true;
                                    break;
                            case 2: out = out3;
                                    break;
                        }
                    }

                }
            }
            sentido = !sentido;
            in1.close(); in2.close(); in3.close(); 
            out1.close(); out2.close(); out3.close(); 
        }

        // Cria o arquivo original de volta
        arquivo.close();
        indiceDireto.close();
        (new File(".\\dados\\"+this.nomeArquivo+".db")).delete();
        (new File(".\\dados\\"+this.nomeArquivo+".hash_d.db")).delete();
        (new File(".\\dados\\"+this.nomeArquivo+".hash_c.db")).delete();

        arquivo = new RandomAccessFile(".\\dados\\"+this.nomeArquivo+".db", "rw");
        indiceDireto = new HashExtensivel<>(
            ParIDEndereco.class.getConstructor(), 
            4,   // tamanho do cesto
            ".\\dados\\"+this.nomeArquivo+".hash_d.db",   // arquivo para o diretório
            ".\\dados\\"+this.nomeArquivo+".hash_c.db"    // arquivo de cestos
        );

        arquivo.seek(0);
        arquivo.writeInt(ultimoID);

        // Cópia dos registros
        in1 = null;
        if(!sentido)
            in1 = new DataInputStream(new FileInputStream(".\\dados\\temp1.db"));
        else
            in1 = new DataInputStream(new FileInputStream(".\\dados\\temp4.db"));

        long endereco;
        ent1 = construtor.newInstance();
        while(true) {
            try{
                // Lê
                tam = in1.readShort();
                dados = new byte[tam];
                in1.read(dados);
                ent1.fromByteArray(dados);
                // Escreve
                endereco = arquivo.getFilePointer();
                arquivo.writeByte(' ');
                arquivo.writeShort(tam);
                arquivo.write(dados);
                indiceDireto.create(new ParIDEndereco(ent1.getId(), endereco));
            } catch(EOFException e) {
                break;
            }
        }

        // Apaga os arquivos temporários
        in1.close();
        (new File(".\\dados\\temp1.db")).delete();
        (new File(".\\dados\\temp2.db")).delete();
        (new File(".\\dados\\temp3.db")).delete();
        (new File(".\\dados\\temp4.db")).delete();
        (new File(".\\dados\\temp5.db")).delete();
        (new File(".\\dados\\temp6.db")).delete();

    }
    
}

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDate;

import aed3.Registro;

public class Tarefa implements Registro {
    int id;
    String nome;
    LocalDate dataCriacao;
    LocalDate dataConclusao;
    byte status;

    public Tarefa() {
        this("", LocalDate.of(1970, 1, 1), LocalDate.of(1970, 1, 1), (byte) 0);
    }

    public Tarefa(String n, LocalDate d, LocalDate c, byte s) {
        this.nome = n;
        this.dataCriacao = d;
        this.dataConclusao = c;
        this.status = s;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getNome() {
        return nome;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public void setDataConclusao(LocalDate dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    public LocalDate getDataConclusao() {
        return dataConclusao;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public byte getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public byte[] toByteArray() throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.id);
        dos.writeUTF(this.nome);
        dos.writeInt((int)this.dataCriacao.toEpochDay());
        dos.writeInt((int)this.dataConclusao.toEpochDay());
        dos.write(this.status);

        return baos.toByteArray();
    }

    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);
        this.id = dis.readInt();
        this.nome = dis.readUTF();
        this.dataCriacao = LocalDate.ofEpochDay(dis.readInt());
        this.dataConclusao = LocalDate.ofEpochDay(dis.readInt());
        this.status = dis.readByte();
    }

    public String toString() {
        String resp = "\nID........: " + this.id +
             "\nNome.............: " + this.nome +
             "\nData de Criacao..: " + this.dataCriacao +
             "\nData de Conclusao: " + this.dataConclusao +
             "\nStatus...........: "; 
        if (this.status == 0) {
            resp += "Pendente";
        } else if(this.status == 1) {
            resp += "Em progresso";
        }else{
            resp += "Concluida";
        }

        return resp;
    }

    public int compareTo(Object p) {
        return this.id - ((Pessoa)p).id;
    }



}


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDate;

import aed3.Registro;

public class Pet implements Registro {
    
    int id;
    String nome;
    LocalDate nascimento;
    int idPessoa;

    public Pet() {
        this(-1, "", LocalDate.of(1970, 1, 1), -1);
    }

    public Pet(String n, LocalDate d, int p) {  
        this(-1, n, d, p);
    }

    public Pet(int i, String n, LocalDate d, int p) {  
        this.id = i;
        this.nome = n;
        this.nascimento = d; 
        this.idPessoa = p;     
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
        dos.writeInt((int)this.nascimento.toEpochDay());
        dos.writeInt(this.idPessoa);

        return baos.toByteArray();
    }

    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);
        this.id = dis.readInt();
        this.nome = dis.readUTF();
        this.nascimento = LocalDate.ofEpochDay(dis.readInt());
        this.idPessoa = dis.readInt();
    }

    public String toString() {
        return "\nID........: " + this.id +
               "\nNome......: " + this.nome +
               "\nNascimento: " + this.nascimento + 
               "\nTutor.....: " + this.idPessoa;
    }

    public int compareTo(Object p) {
        return this.id - ((Pet)p).id;
    }

}

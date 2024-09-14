import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDate;

import aed3.Registro;

public class Pessoa implements Registro {
    
    int id;
    String nome;
    float salario;
    LocalDate nascimento;

    public Pessoa() {
        this(-1, "", 0F, LocalDate.of(1970, 1, 1));
    }

    public Pessoa(String n, float e, LocalDate d) {  
        this(-1, n, e, d);
    }

    public Pessoa(int i, String n, float s, LocalDate d) {  
        this.id = i;
        this.nome = n;
        this.salario = s;
        this.nascimento = d;      
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
        dos.writeFloat(this.salario);
        dos.writeInt((int)this.nascimento.toEpochDay());

        return baos.toByteArray();
    }

    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);
        this.id = dis.readInt();
        this.nome = dis.readUTF();
        this.salario = dis.readFloat();
        this.nascimento = LocalDate.ofEpochDay(dis.readInt());
    }

    public String toString() {
        return "\nID........: " + this.id +
             "\nNome......: " + this.nome +
             "\nSalario...: " + this.salario +
             "\nNascimento: " + this.nascimento;
    }

    public int compareTo(Object p) {
        return this.id - ((Pessoa)p).id;
    }

}

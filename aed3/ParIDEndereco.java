package aed3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ParIDEndereco implements RegistroHashExtensivel<ParIDEndereco>{
    
    private int ID;
    private long endereco;
    private final short TAMANHO = 12;

    public ParIDEndereco() {
        this.ID = -1;
        this.endereco = -1;
    }

    public ParIDEndereco(int id, long end) {
        this.ID = id;
        this.endereco = end;
    }

    public int getID() {
        return ID;
    }

    public long getEndereco() {
        return endereco;
    }

    public short size() {
        return this.TAMANHO;
    }

    @Override
    public int hashCode() {
        return this.ID;
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.ID);
        dos.writeLong(this.endereco);
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.ID = dis.readInt();
        this.endereco = dis.readLong();
    }

    public String toString() {
        return "("+this.ID+";"+this.endereco+")";
    }

}

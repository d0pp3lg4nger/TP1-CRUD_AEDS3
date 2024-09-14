package aed3;

import java.io.IOException;

public interface Registro extends Comparable<Object> {
    public void setId(int id);
    public int getId();
    public byte[] toByteArray() throws IOException;
    public void fromByteArray(byte[] b) throws IOException;
    public int compareTo(Object b);
}

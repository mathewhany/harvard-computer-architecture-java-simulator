package ca.memory;

public class DataMemory {
    final private byte[] memoryArray;

    public DataMemory(int size) {

        memoryArray = new byte[size];
    }

    public byte read(int address) {

        return memoryArray[address];
    }

    public void write(int address, byte value) {

        memoryArray[address] = value;
    }
}

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
        System.out.println("Value " + value + " was written to address " + address +
                           " in data memory during execute stage");
    }

    public void printDataMemory() {
        System.out.println();
        System.out.println();
        System.out.println("###### Data memory #####");
        for (int i = 0; i < memoryArray.length; i++) {
            System.out.println("# " + i + " # => " + read(i));
        }
        System.out.println("########################");
        System.out.println();
        System.out.println();
    }
}

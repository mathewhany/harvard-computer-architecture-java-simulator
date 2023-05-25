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
        System.out.println("Value" + value + "was written to" + "address" + address);
    }
    public void printDataMemory(){
        for(int i = 0; i<memoryArray.length;i++){
            System.out.println("Data memory address : " + i + " Value : " + read(i));
        }
    }
}

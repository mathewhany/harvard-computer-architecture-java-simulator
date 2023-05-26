package ca.memory;

import java.util.Arrays;

public class InstructionMemory {
    private final short[] memoryArray;

    public InstructionMemory(int size) {
        memoryArray = new short[size];

        // Initialize memory with -1 so we can detect uninitialized memory
        Arrays.fill(memoryArray, (short) -1);
    }

    public short read(int address) {
        return memoryArray[address];
    }

    public void write(int address, short value) {
        memoryArray[address] = value;
        System.out.println("Value" + value + "was written to" + "address" + address);
    }

    public void loadProgram(short[] instructions) {
        if (instructions.length > memoryArray.length) {
            System.out.println("Program too long");
            return;
        }

        clearInstructionMemory();

        for (int i = 0; i < instructions.length; i++) {
            memoryArray[i] = instructions[i];
        }
    }

    public void clearInstructionMemory() {
        Arrays.fill(memoryArray, (short) -1);
    }

    public void printDataMemory() {
        for (int i = 0; i < memoryArray.length; i++) {
            System.out.println("Instruction memory address : " + i + " Value : " + read(i));
        }
    }
}

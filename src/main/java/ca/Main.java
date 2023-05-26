package ca;

import ca.memory.DataMemory;
import ca.memory.InstructionMemory;
import ca.memory.RegisterFile;
import ca.parser.AssemblyInstructionParser;
import ca.parser.FileProgramLoader;

public class Main {
    public static void main(String[] args) {
        int instructionMemorySize = 1024;
        int dataMemorySize = 2048;
        int generalPurposeRegisterCount = 64;
        String programPath = "src/main/resources/program.txt";

        Computer computer = new Computer(
            new InstructionMemory(instructionMemorySize),
            new DataMemory(dataMemorySize),
            new RegisterFile(generalPurposeRegisterCount),
            new AssemblyInstructionParser()
        );

        try {
            computer.runProgram(new FileProgramLoader(programPath));
        } catch (CaException e) {
            System.err.println(e.getMessage());
        }
    }
}

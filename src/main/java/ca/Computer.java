package ca;

import ca.memory.DataMemory;
import ca.memory.InstructionMemory;
import ca.memory.RegisterFile;
import ca.parser.ProgramLoader;

public class Computer {
    private final InstructionMemory instructionMemory;
    private final DataMemory dataMemory;
    private final RegisterFile registerFile;

    public Computer(
        InstructionMemory instructionMemory,
        DataMemory dataMemory,
        RegisterFile registerFile
    ) {
        this.instructionMemory = instructionMemory;
        this.dataMemory = dataMemory;
        this.registerFile = registerFile;
    }

    public void runProgram(ProgramLoader programLoader) throws CaException {
        short[] instructions = programLoader.loadProgram();
        instructionMemory.loadProgram(instructions);

        FetchOutput fetchOutput = null;
        DecodeOutput decodeOutput = null;

        while (registerFile.getProgramCounter() < instructions.length) {
            FetchOutput nextFetchOutput = fetch();
            DecodeOutput nextDecodeOutput = decode(fetchOutput);
            execute(decodeOutput);

            fetchOutput = nextFetchOutput;
            decodeOutput = nextDecodeOutput;
        }
    }

    private FetchOutput fetch() {
        return null;
    }

    public static class FetchOutput {
        public short instruction;
    }

    private DecodeOutput decode(FetchOutput fetchOutput) {
        return null;
    }

    public static class DecodeOutput {
        public short opcode;
        public short r1;
        public short r2;
        public short immediate;
    }

    private void execute(DecodeOutput decodeOutput) {

    }
}

package ca;

import ca.memory.DataMemory;
import ca.memory.InstructionMemory;
import ca.memory.RegisterFile;
import ca.parser.AssemblyInstructionParser;
import ca.parser.InstructionParser;
import ca.parser.ProgramLoader;

import java.util.List;

public class Computer {
    private final InstructionMemory instructionMemory;
    private final DataMemory dataMemory;
    private final RegisterFile registerFile;
    private final InstructionParser instructionParser;
    private final ALU alu;

    private FetchDecodePipelineRegister fetchDecode;
    private DecodeExecutePipelineRegister decodeExecute;

    public Computer(
        InstructionMemory instructionMemory,
        DataMemory dataMemory,
        RegisterFile registerFile,
        InstructionParser parser
    ) {
        this.instructionMemory = instructionMemory;
        this.dataMemory = dataMemory;
        this.registerFile = registerFile;
        this.instructionParser = parser;

        this.alu = new ALU(registerFile);
    }

    public void runProgram(ProgramLoader programLoader) throws CaException {
        int programSize = loadProgram(programLoader);

        fetchDecode = null;
        decodeExecute = null;

        int clock = 1;

        while (true) {
            System.out.println();
            System.out.println();
            System.out.println("#################### Clock " + clock + " ####################");

            printInstructionsAtStages();

            printPipelineRegisters("Pipeline registers at start of cycle " + clock);

            FetchDecodePipelineRegister nextFetchOutput = fetch();
            DecodeExecutePipelineRegister nextDecodeExecute = decode();

            execute();

            fetchDecode = nextFetchOutput;
            decodeExecute = nextDecodeExecute;

            printPipelineRegisters("Pipeline registers after cycle " + clock);

            System.out.println(
                "#################### End of cycle " + clock + " ####################");
            System.out.println();
            System.out.println();
            clock++;

            if (fetchDecode == null && decodeExecute == null) {
                System.out.println("Program finished");
                break;
            }
        }

        dataMemory.printDataMemory();
        registerFile.printAllRegisters();
    }

    private void printInstructionsAtStages() {
        System.out.println("Instruction At Fetch: " + registerFile.getProgramCounter());
        System.out.println("Instruction At Decode: " +
                           (fetchDecode != null ? fetchDecode.instructionAddress : "None"));
        System.out.println("Instruction At Execute: " +
                           (decodeExecute != null ? decodeExecute.instructionAddress : "None"));
    }

    private void printPipelineRegisters(String title) {
        System.out.println(title);
        System.out.println(
            fetchDecode == null ? "Fetch Decode register is empty" : fetchDecode);
        System.out.println(
            decodeExecute == null ? "Decode Execute register is empty" : decodeExecute);
    }

    private int loadProgram(ProgramLoader programLoader) throws CaException {
        List<String> unparsedInstructions = programLoader.loadProgram();
        short[] parsedInstructions = new short[unparsedInstructions.size()];
        for (int i = 0; i < unparsedInstructions.size(); i++) {
            parsedInstructions[i] = instructionParser.parse(unparsedInstructions.get(i));
        }
        instructionMemory.loadProgram(parsedInstructions);
        return parsedInstructions.length;
    }

    private FetchDecodePipelineRegister fetch() {
        short pc = registerFile.getProgramCounter();
        short instruction = instructionMemory.read(pc);

        if (instruction == -1) return null;

        FetchDecodePipelineRegister out = new FetchDecodePipelineRegister(pc, instruction);
        System.out.println("Instruction #" + pc + " fetched");
        System.out.println("Instruction Binary: " + BitUtils.toBinaryString(instruction, 16));
        registerFile.incrementProgramCounter();
        return out;
    }

    public static class FetchDecodePipelineRegister {
        public short instruction;
        public int instructionAddress;

        public FetchDecodePipelineRegister(int instructionAddress, short instruction) {
            this.instructionAddress = instructionAddress;
            this.instruction = instruction;
        }

        @Override
        public String toString() {
            return "Fetch Decode Pipeline Register { " +
                   "instruction=" + Integer.toBinaryString(instruction) +
                   " }";
        }
    }

    private DecodeExecutePipelineRegister decode() {
        if (fetchDecode == null) return null;

        boolean isBranch = false;
        short instruction = fetchDecode.instruction;
        short opcode = (short) BitUtils.getBits(instruction, 12, 15);
        short r1 = (short) BitUtils.getBits(instruction, 6, 11);
        short r2 = (short) BitUtils.getBits(instruction, 0, 5);
        short immediate = (short) BitUtils.getBits(instruction, 0, 5);

        if (opcode == Opcode.BEQZ) {
            isBranch = true;
        }

        byte r1Data = registerFile.getGeneralPurposeRegister(r1);
        byte r2Data = registerFile.getGeneralPurposeRegister(r2);

        /**
         * True for Opcode.JR only
         */
        boolean isJump = opcode == Opcode.JR;

        /**
         * Opcode.ADD -> ALU.ADD
         * Opcode.SUB -> ALU.SUB
         * Opcode.MUL -> ALU.MUL
         * Opcode.AND -> ALU.AND
         * Opcode.OR -> ALU.OR
         * Opcode.SLC -> ALU.SLC
         * Opcode.SRL -> ALU.SRL
         * Opcode.SB, Opcode.LB, Opcode.LDI, Opcode.BEQZ -> ALU.TRANSFER
         * Opcode.JR -> ALU.CONCAT
         */
        int aluOpcode = ALU.ADD;

        /**
         * True for Opcode.SB only
         */
        boolean memoryWrite = false;

        /**
         * True for Opcode.LB only
         */
        boolean memoryRead = false;

        /**
         * The ALU will always be given R1 as a first operand,
         * and aluSrc as the second operand.
         *
         * For Opcode.ADD, Opcode.SUB, Opcode.MUL, Opcode.AND, Opcode.OR, Opcode.JR -> aluSrc = R2
         * For Opcode.SLC, Opcode.SRC, Opcode.LDI, Opcode.BEQZ, Opcode.SB, Opcode.LB -> aluSrc = immediate
         */
        short aluSrc = 0;

        /**
         * True for Opcode.SB only
         */
        boolean writeMemoryToRegister = false;


        /**
         * True for Opcode.ADD, Opcode.SUB, Opcode.MUL, Opcode.AND, Opcode.OR, Opcode.SLC, Opcode.SRL, Opcode.LDI, Opcode.LB -> regWrite = true
         * True for Opcode.JR, Opcode.SB, Opcode.BEQZ -> regWrite = false
         */
        boolean regWrite = true;

        return new DecodeExecutePipelineRegister(
            fetchDecode.instructionAddress,
            opcode,
            r1Data,
            r2Data,
            immediate,
            isBranch,
            isJump,
            r1,
            r2,
            aluOpcode,
            memoryWrite,
            memoryRead,
            aluSrc,
            writeMemoryToRegister,
            regWrite
        );

    }

    public static class DecodeExecutePipelineRegister {
        public int instructionAddress;
        public short opcode;
        public byte r1Data;
        public byte r2Data;
        public short immediate;
        public boolean isBranch;
        public boolean isJump;
        public short r1;
        public short r2;
        public int aluOpcode;
        public boolean memoryWrite;
        public boolean memoryRead;
        public short aluSrc;
        public boolean writeMemoryToRegister;
        public boolean regWrite;

        public DecodeExecutePipelineRegister(
            int instructionAddress,
            short opcode,
            byte r1Data,
            byte r2Data,
            short immediate,
            boolean isBranch,
            boolean isJump,
            short r1,
            short r2,
            int aluOpcode,
            boolean memoryWrite,
            boolean memoryRead,
            short aluSrc,
            boolean writeMemoryToRegister,
            boolean regWrite
        ) {
            this.instructionAddress = instructionAddress;
            this.opcode = opcode;
            this.r1Data = r1Data;
            this.r2Data = r2Data;
            this.immediate = immediate;
            this.isBranch = isBranch;
            this.isJump = isJump;
            this.r1 = r1;
            this.r2 = r2;
            this.aluOpcode = aluOpcode;
            this.memoryWrite = memoryWrite;
            this.memoryRead = memoryRead;
            this.aluSrc = aluSrc;
            this.writeMemoryToRegister = writeMemoryToRegister;
            this.regWrite = regWrite;
        }


        @Override
        public String toString() {
            return "Decode Execute Pipeline Register {" +
                   "opcode=" + opcode +
                   ", r1Data=" + r1Data +
                   ", r2Data=" + r2Data +
                   ", immediate=" + immediate +
                   ", isBranch=" + isBranch +
                   ", isJump=" + isJump +
                   ", r1=" + r1 +
                   ", r2=" + r2 +
                   ", aluOpcode=" + aluOpcode +
                   ", memoryWrite=" + memoryWrite +
                   ", memoryRead=" + memoryRead +
                   ", aluSrc=" + aluSrc +
                   ", writeMemoryToRegister=" + writeMemoryToRegister +
                   ", regWrite=" + regWrite +
                   " }";
        }
    }

    private void execute() throws CaException {
        if (decodeExecute == null) return;

        short aluResult =
            alu.execute(decodeExecute.opcode, decodeExecute.r1Data, decodeExecute.aluSrc);

        byte memoryData = 0;

        if (decodeExecute.memoryRead) {
            memoryData = dataMemory.read(aluResult);
        }

        if (decodeExecute.memoryWrite) {
            dataMemory.write(aluResult, decodeExecute.r1Data);
        }

        if (decodeExecute.regWrite) {
            if (decodeExecute.writeMemoryToRegister) {
                registerFile.setGeneralPurposeRegister(decodeExecute.r1, memoryData);
            } else {
                registerFile.setGeneralPurposeRegister(decodeExecute.r1, decodeExecute.r2Data);
            }
        }


        if (decodeExecute.isJump) {
            registerFile.setProgramCounter(aluResult);

            // Flush the pipeline
            fetchDecode = null;
            decodeExecute = null;
        } else if (decodeExecute.isBranch && decodeExecute.r1Data == 0) {
            registerFile.setProgramCounter(aluResult);

            // Flush the pipeline
            fetchDecode = null;
            decodeExecute = null;
        }
    }
}

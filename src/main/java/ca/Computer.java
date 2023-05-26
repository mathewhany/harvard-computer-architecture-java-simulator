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

        return new DecodeExecutePipelineRegister(
            fetchDecode.instructionAddress,
            opcode,
            r1Data,
            r2Data,
            immediate,
            isBranch,
            r1,
            r2
        );

    }

    public static class DecodeExecutePipelineRegister {
        public int instructionAddress;
        public short opcode;
        public short r1Data;
        public short r2Data;
        public short immediate;
        public boolean isBranch;
        public short r1;
        public short r2;

        public DecodeExecutePipelineRegister(
            int instructionAddress,
            short opcode,
            short r1Data,
            short r2Data,
            short immediate,
            boolean isBranch,
            short r1,
            short r2
        ) {
            this.instructionAddress = instructionAddress;
            this.opcode = opcode;
            this.r1Data = r1Data;
            this.r2Data = r2Data;
            this.immediate = immediate;
            this.isBranch = isBranch;
            this.r1 = r1;
            this.r2 = r2;
        }

        @Override
        public String toString() {
            return "Decode Execute Pipeline Register { " +
                   "opcode=" + opcode +
                   ", R1 Data=" + r1Data +
                   ", R2 Data=" + r2Data +
                   ", Immediate=" + immediate +
                   ", Is Branch=" + isBranch +
                   ", R1 =" + r1 +
                   ", R2 =" + r2 +
                   " }";
        }

    }

    private void execute() {
        if (decodeExecute == null) return;

        int r = 0;
        switch (decodeExecute.opcode) {
            case Opcode.ADD:
                r = decodeExecute.r1Data + decodeExecute.r2Data;
                registerFile.setGeneralPurposeRegister(decodeExecute.r1, (byte) r);
                break;
            case Opcode.SUB:
                r = decodeExecute.r1Data - decodeExecute.r2Data;
                registerFile.setGeneralPurposeRegister(decodeExecute.r1, (byte) r);
                break;
            case Opcode.MUL:
                r = decodeExecute.r1Data * decodeExecute.r2Data;
                registerFile.setGeneralPurposeRegister(decodeExecute.r1, (byte) r);
                break;
            case Opcode.LDI:
                registerFile.setGeneralPurposeRegister(
                    decodeExecute.r1,
                    (byte) (decodeExecute.immediate)
                );
                break;
            case Opcode.BEQZ:
                if (decodeExecute.r1Data == 0) {
                    registerFile.setProgramCounter((short) (
                        registerFile.getProgramCounter() + 1 + decodeExecute.immediate
                    ));

                    fetchDecode = null;
                    decodeExecute = null;
                }
                return;
            case Opcode.AND:
                r = decodeExecute.r1Data & decodeExecute.r2Data;
                registerFile.setGeneralPurposeRegister(decodeExecute.r1, (byte) r);
                break;
            case Opcode.OR:
                r = decodeExecute.r1Data | decodeExecute.r2Data;
                registerFile.setGeneralPurposeRegister(decodeExecute.r1, (byte) r);
                break;
            case Opcode.JR:
                registerFile.setProgramCounter(
                    Short.parseShort(
                        String.valueOf(decodeExecute.r1Data)
                              .concat(String.valueOf(decodeExecute.r2Data))
                    )
                );
                break;
            case Opcode.SLC:
                r = (decodeExecute.r1Data << decodeExecute.immediate) |
                    (decodeExecute.r1Data >>> (8 - decodeExecute.immediate));
                registerFile.setGeneralPurposeRegister(decodeExecute.r1, (byte) r);
                break;
            case Opcode.SRC:
                r = (decodeExecute.r1Data >>> decodeExecute.immediate) |
                    (decodeExecute.r1Data << (8 - decodeExecute.immediate));
                registerFile.setGeneralPurposeRegister(decodeExecute.r1, (byte) r);
                break;
            case Opcode.LB:
                registerFile.setGeneralPurposeRegister(
                    decodeExecute.r1,
                    dataMemory.read(decodeExecute.immediate)
                );
                break;
            case Opcode.SB:
                dataMemory.write(decodeExecute.immediate, (byte) (decodeExecute.r1Data));
                break;
            default:
                System.out.println("Invalid opcode " + decodeExecute.opcode);
                return;
        }

        if (decodeExecute.opcode == Opcode.ADD ||
            decodeExecute.opcode == Opcode.SUB ||
            decodeExecute.opcode == Opcode.MUL ||
            decodeExecute.opcode == Opcode.AND ||
            decodeExecute.opcode == Opcode.OR ||
            decodeExecute.opcode == Opcode.SLC ||
            decodeExecute.opcode == Opcode.SRC) {
            registerFile.setZeroFlag(r == 0);
            registerFile.setNegativeFlag(r < 0);
        }

        if (decodeExecute.opcode == Opcode.ADD) {
            registerFile.setCarryFlag(BitUtils.getBit(r, 8) == 1);

            if ((decodeExecute.r1Data > 0 && decodeExecute.r2Data > 0) ||
                (decodeExecute.r1Data < 0 && decodeExecute.r2Data < 0)) {
                if ((decodeExecute.r2Data > 0 && r < 0) || (decodeExecute.r2Data < 0 && r > 0)) {
                    registerFile.set2sComplementOverflowFlag(true);
                }
                registerFile.setSignFlag(
                    registerFile.getNegativeFlag() ^ registerFile.get2sComplementOverflowFlag());
            }
        }

        if (decodeExecute.opcode == Opcode.SUB) {
            if ((decodeExecute.r1Data > 0 && decodeExecute.r2Data < 0) ||
                (decodeExecute.r1Data < 0 && decodeExecute.r2Data > 0)) {
                if ((decodeExecute.r2Data >= 0 && r >= 0) ||
                    (decodeExecute.r2Data < 0 && r < 0)) {
                    registerFile.set2sComplementOverflowFlag(true);
                }
            }

            registerFile.setSignFlag(
                registerFile.getNegativeFlag() ^ registerFile.get2sComplementOverflowFlag());
        }
    }
}

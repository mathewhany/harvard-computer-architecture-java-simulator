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
        InstructionMemory instructionMemory, DataMemory dataMemory, RegisterFile registerFile
    ) {
        this.instructionMemory = instructionMemory;
        this.dataMemory = dataMemory;
        this.registerFile = registerFile;
    }

    public void runProgram(ProgramLoader programLoader) throws CaException {
        short[] instructions = programLoader.loadProgram();
        instructionMemory.loadProgram(instructions);

        FetchDecodePipelineRegister fetchDecode = null;
        DecodeExecutePipelineRegister decodeExecute = null;

        while (registerFile.getProgramCounter() < instructions.length) {
            FetchDecodePipelineRegister nextFetchOutput = fetch();

            DecodeExecutePipelineRegister nextDecodeOutput = null;
            if (fetchDecode != null) {
                nextDecodeOutput = decode(fetchDecode);
            }

            if (decodeExecute != null) {
                execute(decodeExecute);
            }

            fetchDecode = nextFetchOutput;
            decodeExecute = nextDecodeOutput;
        }
    }

    private FetchDecodePipelineRegister fetch() {
        if (registerFile.getProgramCounter() == null) {
            System.out.println("All instructions fetched");
            return null;
        } else {
            short instruction = instructionMemory.read(registerFile.getProgramCounter());
            FetchDecodePipelineRegister out = new FetchDecodePipelineRegister(instruction);
            System.out.println("Instruction" + registerFile.getProgramCounter() + "fetched");
            System.out.println("Instruction : " + Integer.toBinaryString(instruction));
            registerFile.incrementProgramCounter();
            return out;
        }
    }

    public static class FetchDecodePipelineRegister {
        public short instruction;

        public FetchDecodePipelineRegister(short instruction) {
            this.instruction = instruction;
        }
    }

    private DecodeExecutePipelineRegister decode(FetchDecodePipelineRegister fetchOutput) {
        short opcode;
        short r1 = 0;
        short r2 = 0;
        short immediate = 0;
        boolean isBranch = false;
        short instruction = fetchOutput.instruction;
        short maskOpCode = (short) 0b1111000000000000;
        opcode = (short) (instruction & maskOpCode);
        opcode = (short) (opcode >> 12);
        short maskR1Code = (short) 0b0000111111000000;
        r1 = (short) (instruction & maskR1Code);
        r1 = (short) (r1 >> 6);

        switch (opcode) {
            case 4:
                isBranch = true;
            case 3:
            case 8:
            case 9:
            case 10:
            case 11:
                immediate = (short) (0b0000000000111111 & instruction);
                break;
            default:
                r2 = (short) (0b0000000000111111 & instruction);
                break;
        }

        byte r1Data = registerFile.getGeneralPurposeRegister(r1);
        byte r2Data = registerFile.getGeneralPurposeRegister(r2);

        return new DecodeExecutePipelineRegister(
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
        public short opcode;
        public short r1Data;
        public short r2Data;
        public short immediate;
        public boolean isBranch;
        public short r1;
        public short r2;

        public DecodeExecutePipelineRegister(
            short opcode,
            short r1Data,
            short r2Data,
            short immediate,
            boolean isBranch,
            short r1,
            short r2
        ) {
            this.opcode = opcode;
            this.r1Data = r1Data;
            this.r2Data = r2Data;
            this.immediate = immediate;
            this.isBranch = isBranch;
            this.r1 = r1;
            this.r2 = r2;

        }


    }

    private void execute(DecodeExecutePipelineRegister decodeOutput) {
        int r = 0;
        switch (decodeOutput.opcode) {
            case Opcode.ADD:
                r = decodeOutput.r1Data + decodeOutput.r2Data;
                registerFile.setGeneralPurposeRegister(decodeOutput.r1, (byte) r);
                break;
            case Opcode.SUB:
                r = decodeOutput.r1Data - decodeOutput.r2Data;
                registerFile.setGeneralPurposeRegister(decodeOutput.r1, (byte) r);
                break;
            case Opcode.MUL:
                r = decodeOutput.r1Data * decodeOutput.r2Data;
                registerFile.setGeneralPurposeRegister(decodeOutput.r1, (byte) r);
                break;
            case Opcode.LDI:
                registerFile.setGeneralPurposeRegister(
                    decodeOutput.r1,
                    (byte) (decodeOutput.immediate)
                );
                break;
            case Opcode.BEQZ:
                if (decodeOutput.r1Data == 0) {
                    registerFile.setProgramCounter((short) (
                        registerFile.getProgramCounter() + 1 + decodeOutput.immediate
                    ));
                }
                break;
            case Opcode.AND:
                r = decodeOutput.r1Data & decodeOutput.r2Data;
                registerFile.setGeneralPurposeRegister(decodeOutput.r1, (byte) r);
                break;
            case Opcode.OR:
                r = decodeOutput.r1Data | decodeOutput.r2Data;
                registerFile.setGeneralPurposeRegister(decodeOutput.r1, (byte) r);
                break;
            case Opcode.JR:
                registerFile.setProgramCounter(
                    Short.parseShort(
                        String.valueOf(decodeOutput.r1Data)
                              .concat(String.valueOf(decodeOutput.r2Data))
                    )
                );
                break;
            case Opcode.SLC:
                r = (decodeOutput.r1Data << decodeOutput.immediate) |
                    (decodeOutput.r1Data >>> (8 - decodeOutput.immediate));
                registerFile.setGeneralPurposeRegister(decodeOutput.r1, (byte) r);
                break;
            case Opcode.SRC:
                r = (decodeOutput.r1Data >>> decodeOutput.immediate) |
                    (decodeOutput.r1Data << (8 - decodeOutput.immediate));
                registerFile.setGeneralPurposeRegister(decodeOutput.r1, (byte) r);
                break;
            case Opcode.LB:
                registerFile.setGeneralPurposeRegister(
                    decodeOutput.r1,
                    dataMemory.read(decodeOutput.immediate)
                );
                break;
            case Opcode.SB:
                dataMemory.write(decodeOutput.immediate, (byte) (decodeOutput.r1Data));
                break;
            default:
                System.out.println("Invalid opcode " + decodeOutput.opcode);
                return;
        }

        if (decodeOutput.opcode == Opcode.ADD ||
            decodeOutput.opcode == Opcode.SUB ||
            decodeOutput.opcode == Opcode.MUL ||
            decodeOutput.opcode == Opcode.AND ||
            decodeOutput.opcode == Opcode.OR ||
            decodeOutput.opcode == Opcode.SLC ||
            decodeOutput.opcode == Opcode.SRC) {
            registerFile.setZeroFlag(r == 0);
            registerFile.setNegativeFlag(r < 0);
        }

        if (decodeOutput.opcode == Opcode.ADD) {
            registerFile.setCarryFlag(BitUtils.getBit(r, 8) == 1);

            if ((decodeOutput.r1Data > 0 && decodeOutput.r2Data > 0) ||
                (decodeOutput.r1Data < 0 && decodeOutput.r2Data < 0)) {
                if ((decodeOutput.r2Data > 0 && r < 0) || (decodeOutput.r2Data < 0 && r > 0)) {
                    registerFile.set2sComplementOverflowFlag(true);
                }
                registerFile.setSignFlag(
                    registerFile.getNegativeFlag() ^ registerFile.get2sComplementOverflowFlag());
            }
        }

        if (decodeOutput.opcode == Opcode.SUB) {
            if ((decodeOutput.r1Data > 0 && decodeOutput.r2Data < 0) ||
                (decodeOutput.r1Data < 0 && decodeOutput.r2Data > 0)) {
                if ((decodeOutput.r2Data >= 0 && r >= 0) ||
                    (decodeOutput.r2Data < 0 && r < 0)) {
                    registerFile.set2sComplementOverflowFlag(true);
                }
            }

            registerFile.setSignFlag(
                registerFile.getNegativeFlag() ^ registerFile.get2sComplementOverflowFlag());
        }
    }
}

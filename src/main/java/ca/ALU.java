package ca;

import ca.memory.RegisterFile;

public class ALU {
    public static final int ADD = 0;
    public static final int SUB = 1;
    public static final int MUL = 2;
    public static final int TRANSFER = 3;
    public static final int AND = 4;
    public static final int OR = 5;
    public static final int CONCAT = 6;
    public static final int SLC = 7;
    public static final int SRC = 8;

    private final RegisterFile registerFile;

    public ALU(RegisterFile registerFile) {
        this.registerFile = registerFile;
    }

    public short execute(int aluOpcode, short a, short b) throws CaException {
        int r;
        switch (aluOpcode) {
            case ADD:
                r = a + b;
                break;
            case SUB:
                r = a - b;
                break;
            case MUL:
                r = a * b;
                break;
            case TRANSFER:
                r = b;
                break;
            case AND:
                r = a & b;
                break;
            case OR:
                r = a | b;
                break;
            case CONCAT:
                r = (a << 8) | b;
                break;
            case SLC:
                r = (a << b) | (a >>> (8 - b));
                break;
            case SRC:
                r = (a >>> b) | (a << (8 - b));
                break;
            default:
                System.out.println("Invalid ALU opcode " + aluOpcode);
                throw new CaException("Invalid ALU opcode " + aluOpcode);
        }

        if (aluOpcode == ADD ||
            aluOpcode == SUB ||
            aluOpcode == MUL ||
            aluOpcode == AND ||
            aluOpcode == OR ||
            aluOpcode == SLC ||
            aluOpcode == SRC) {
            registerFile.setZeroFlag(r == 0);
            registerFile.setNegativeFlag(r < 0);
        }

        if (aluOpcode == ADD) {
            registerFile.setCarryFlag(BitUtils.getBit(r, 8) == 1);

            if ((a > 0 && b > 0) ||
                (a < 0 && b < 0)) {
                if ((a > 0 && r < 0) || (a < 0 && r > 0)) {
                    registerFile.set2sComplementOverflowFlag(true);
                }
                registerFile.setSignFlag(
                    registerFile.getNegativeFlag() ^ registerFile.get2sComplementOverflowFlag());
            }
        }

        if (aluOpcode == SUB) {
            if ((a > 0 && b < 0) ||
                (a < 0 && b > 0)) {
                if ((a > 0 && r > 0) ||
                    (a < 0 && r < 0)) {
                    registerFile.set2sComplementOverflowFlag(true);
                }
            }

            registerFile.setSignFlag(
                registerFile.getNegativeFlag() ^ registerFile.get2sComplementOverflowFlag());
        }

        return (short) r;
    }
}

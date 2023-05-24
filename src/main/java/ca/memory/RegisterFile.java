package ca.memory;

public class RegisterFile {
    final private byte[] GPRegisters;
    private SREG sreg;
    private static short PC;

    public RegisterFile(int numberOfGeneralPurposeRegisters) {
        GPRegisters = new byte[numberOfGeneralPurposeRegisters];
    }

    public byte getGeneralPurposeRegister(int registerNumber) {

        return GPRegisters[registerNumber];
    }

    public void setGeneralPurposeRegister(int registerNumber, byte value) {
        GPRegisters[registerNumber] = value;
    }

    public static short getProgramCounter() {
        return PC;
    }

    public static void setProgramCounter(short value) {
        PC = value;
    }

    public void incrementProgramCounter() {
        PC++;
    }

    public SREG getStatusRegister() {
        return sreg;
    }

    public boolean getCarryFlag() {
        return SREG.isCarryFlag();
    }

    public void setCarryFlag(boolean value) {
        SREG.setCarryFlag(value);
    }

    public boolean get2sComplementOverflowFlag() {
        return SREG.isOverflowFlag();
    }

    public void set2sComplementOverflowFlag(boolean value) {
        SREG.setOverflowFlag(value);
    }

    public boolean getSignFlag() {
        return SREG.isSignFLag();
    }

    public void setSignFlag(boolean value) {
        SREG.setSignFLag(value);
    }

    public boolean getZeroFlag() {
        return SREG.isZeroFlag();
    }

    public void setZeroFlag(boolean value) {
        SREG.setZeroFlag(value);
    }
}

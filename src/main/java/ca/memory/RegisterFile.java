package ca.memory;

public class RegisterFile {
    final private byte[] gpRegisters;
    private SREG sreg;
    private short pc;

    public RegisterFile(int numberOfGeneralPurposeRegisters) {
        gpRegisters = new byte[numberOfGeneralPurposeRegisters];
    }

    public byte getGeneralPurposeRegister(int registerNumber) {
        return gpRegisters[registerNumber];
    }

    public void setGeneralPurposeRegister(int registerNumber, byte value) {
        gpRegisters[registerNumber] = value;
        System.out.println("Register R" + registerNumber + " set to " + value);
    }

    public Short getProgramCounter() {
        return pc;
    }

    public void setProgramCounter(short value) {
        pc = value;
        System.out.println("PC new value: " + value);
    }

    public void incrementProgramCounter() {
        setProgramCounter((short) (getProgramCounter() + 1));
    }

    public SREG getStatusRegister() {
        return sreg;
    }

    public boolean getCarryFlag() {
        return SREG.isCarryFlag();
    }

    public void setCarryFlag(boolean value) {
        SREG.setCarryFlag(value);
        System.out.println("Carry flag set to: " + value);
    }

    public boolean get2sComplementOverflowFlag() {
        return SREG.isOverflowFlag();
    }

    public void set2sComplementOverflowFlag(boolean value) {
        SREG.setOverflowFlag(value);
        System.out.println("Overflow flag set to: " + value);
    }

    public boolean getNegativeFlag() {
        return SREG.isNegativeFlag();
    }

    public void setNegativeFlag(boolean value) {
        SREG.setNegativeFlag(value);
        System.out.println("Negative flag set to: " + value);
    }

    public boolean getSignFlag() {
        return SREG.isSignFLag();
    }

    public void setSignFlag(boolean value) {
        SREG.setSignFLag(value);
        System.out.println("Sign flag set to: " + value);
    }

    public boolean getZeroFlag() {
        return SREG.isZeroFlag();
    }

    public void setZeroFlag(boolean value) {
        SREG.setZeroFlag(value);
        System.out.println("Zero flag set to: " + value);
    }

    public void printAllRegisters() {
        System.out.println();
        System.out.println();
        System.out.println("############## Register file ##################");
        for (int i = 0; i < gpRegisters.length; i++) {
            System.out.println("R" + i + " = " + getGeneralPurposeRegister(i));
        }
        System.out.println("PC = " + getProgramCounter());
//        System.out.println("SREG = " + getStatusRegister());
        System.out.println("################################################");
        System.out.println();
        System.out.println();
    }
}

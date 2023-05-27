package ca.memory;

import ca.BitUtils;

public class RegisterFile {
    final private byte[] gpRegisters;
    private byte statusReg;
    private short pc;

    public RegisterFile(int numberOfGeneralPurposeRegisters) {
        gpRegisters = new byte[numberOfGeneralPurposeRegisters];
    }

    public byte getGeneralPurposeRegister(int registerNumber) {
        return gpRegisters[registerNumber];
    }

    public void setGeneralPurposeRegister(int registerNumber, byte value) {
        gpRegisters[registerNumber] = value;
        System.out.println(
            "Register R" + registerNumber + " set to " + value + " during execute stage");
    }

    public Short getProgramCounter() {
        return pc;
    }

    public void setProgramCounter(short value) {
        pc = value;
    }

    public void incrementProgramCounter() {
        setProgramCounter((short) (getProgramCounter() + 1));
    }

    public byte getStatusRegister() {
        return statusReg;
    }

    public boolean getCarryFlag() {

        return (BitUtils.getBit(statusReg, 4) == 1);
    }

    public void setCarryFlag(boolean value) {
        statusReg = BitUtils.setBit(statusReg, 4, value);
        System.out.println(
            "Carry flag set to: " + value + " during execute stage, SREG=" +
            BitUtils.toBinaryString(statusReg, 8));
    }

    public boolean get2sComplementOverflowFlag() {
        return (BitUtils.getBit(statusReg, 3) == 1);
    }

    public void set2sComplementOverflowFlag(boolean value) {
        statusReg = BitUtils.setBit(statusReg, 3, value);
        System.out.println(
            "Overflow flag set to: " + value + " during execute stage, SREG=" +
            BitUtils.toBinaryString(statusReg, 8));
    }

    public boolean getNegativeFlag() {
        return (BitUtils.getBit(statusReg, 2) == 1);
    }

    public void setNegativeFlag(boolean value) {
        statusReg = BitUtils.setBit(statusReg, 2, value);
        System.out.println(
            "Negative flag set to: " + value + " during execute stage, SREG=" +
            BitUtils.toBinaryString(statusReg, 8));
    }

    public boolean getSignFlag() {
        return (BitUtils.getBit(statusReg, 1) == 1);
    }

    public void setSignFlag(boolean value) {
        statusReg = BitUtils.setBit(statusReg, 1, value);
        System.out.println(
            "Sign flag set to: " + value + " during execute stage, SREG=" +
            BitUtils.toBinaryString(statusReg, 8));
    }

    public boolean getZeroFlag() {
        return (BitUtils.getBit(statusReg, 0) == 1);
    }

    public void setZeroFlag(boolean value) {
        statusReg = BitUtils.setBit(statusReg, 0, value);
        System.out.println(
            "Zero flag set to: " + value + " during execute stage, SREG=" +
            BitUtils.toBinaryString(statusReg, 8));
    }

    public void printAllRegisters() {
        System.out.println();
        System.out.println();
        System.out.println("############## Register file ##################");
        for (int i = 0; i < gpRegisters.length; i++) {
            System.out.println("R" + i + " = " + getGeneralPurposeRegister(i));
        }
        System.out.println("PC = " + getProgramCounter());
        System.out.println("SREG = " + BitUtils.toBinaryString(getStatusRegister(), 8));
        System.out.println("################################################");
        System.out.println();
        System.out.println();
    }

    public void resetFlags() {
        statusReg = 0;
    }
}

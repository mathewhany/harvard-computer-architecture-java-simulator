package ca.pipelining;

public class DecodeExecutePipelineRegister {
    public int instructionAddress;
    public short opcode;
    public byte r1Data;
    public byte r2Data;
    public byte immediate;
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
        byte immediate,
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

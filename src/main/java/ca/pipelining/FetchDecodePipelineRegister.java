package ca.pipelining;

import ca.BitUtils;

public class FetchDecodePipelineRegister {
    public short instruction;
    public int instructionAddress;

    public FetchDecodePipelineRegister(int instructionAddress, short instruction) {
        this.instructionAddress = instructionAddress;
        this.instruction = instruction;
    }

    @Override
    public String toString() {
        return "Fetch Decode Pipeline Register { " +
               "instruction=" + BitUtils.toBinaryString(instruction, 16) +
               " }";
    }
}

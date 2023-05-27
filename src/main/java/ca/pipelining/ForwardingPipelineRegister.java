package ca.pipelining;

public class ForwardingPipelineRegister {
    public final short r1;
    public final byte r1NewData;
    public final boolean regWrite;

    public ForwardingPipelineRegister(short r1, byte r1NewData, boolean regWrite) {
        this.r1 = r1;
        this.r1NewData = r1NewData;
        this.regWrite = regWrite;
    }

    @Override
    public String toString() {
        return "ForwardingPipelineRegister{" +
               "r1=" + r1 +
               ", r1NewData=" + r1NewData +
               ", regWrite=" + regWrite +
               '}';
    }
}

package ca.memory;

public class PipelineReg {
    private boolean ready;
    private short PCvalue;
    private Short currentInstruction;

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public void setPCvalue(short PCvalue) {
        this.PCvalue = PCvalue;
    }

    public boolean isReady() {
        return ready;
    }

    public short getPCvalue() {
        return PCvalue;
    }

    public short getCurrentInstruction(){
        return currentInstruction;
    }

    public void setCurrentInstruction(short instruction){
        currentInstruction = instruction;
    }

}

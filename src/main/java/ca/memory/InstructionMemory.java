package ca.memory;

public class InstructionMemory {
    static private Short[] memoryArray;
    private PipelineReg fetchDecode;
    private PipelineReg decodeExecute;

    public InstructionMemory(int size) {

        memoryArray = new Short[size];
    }

    public short read(int address) {

        return memoryArray[address];
    }

    public void write(int address, short value) {

        memoryArray[address] = value;
    }

    public void loadProgram(short[] instructions) {
        if (instructions.length > memoryArray.length) {
            System.out.println("Program too long");
            return;
        }
        clearInstructionMemory();
        for(int i = 0; i<instructions.length;i++){
            memoryArray[i] = instructions[i];
        }
    }
    public void clearInstructionMemory(){
        int size = memoryArray.length;
        memoryArray = new Short[size];
    }

    public void fetch(){
        if(memoryArray[RegisterFile.getProgramCounter()] == null)
        {
            System.out.println("All instructions fetched");
            fetchDecode.setReady(false);
            return;
        }
        else
        {
            fetchDecode.setCurrentInstruction(memoryArray[RegisterFile.getProgramCounter()]);
            fetchDecode.setReady(true);
            System.out.println("Instruction" + RegisterFile.getProgramCounter() + "fetched");
            RegisterFile.setProgramCounter((short) (RegisterFile.getProgramCounter()+1));
        }

    }

}

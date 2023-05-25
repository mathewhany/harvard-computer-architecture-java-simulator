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
        InstructionMemory instructionMemory,
        DataMemory dataMemory,
        RegisterFile registerFile
    ) {
        this.instructionMemory = instructionMemory;
        this.dataMemory = dataMemory;
        this.registerFile = registerFile;
    }

    public void runProgram(ProgramLoader programLoader) throws CaException {
        short[] instructions = programLoader.loadProgram();
        instructionMemory.loadProgram(instructions);

        FetchOutput fetchOutput = null;
        DecodeOutput decodeOutput = null;

        while (registerFile.getProgramCounter() < instructions.length) {
            FetchOutput nextFetchOutput = fetch();
            DecodeOutput nextDecodeOutput = decode(fetchOutput);
            execute(decodeOutput);

            fetchOutput = nextFetchOutput;
            decodeOutput = nextDecodeOutput;
        }
    }

    private FetchOutput fetch() {
          if(registerFile.getProgramCounter() == null)
           {
               System.out.println("All instructions fetched");
                 return null;
           }
              else
            {

                FetchOutput out = new FetchOutput();
                out.instruction = instructionMemory.read(registerFile.getProgramCounter());
                System.out.println("Instruction" + registerFile.getProgramCounter() + "fetched");
                System.out.println("Instruction : " + Integer.toBinaryString(out.instruction));
                registerFile.setProgramCounter((short) (registerFile.getProgramCounter() + 1));
                return out;
            }
    }

    public static class FetchOutput {
        public short instruction;
    }

    private DecodeOutput decode(FetchOutput fetchOutput) {
        short opcode ;
        short r1 = 0  ;
        short r2= 0 ;
        short immediate  =  0 ;
        boolean isBranch =false  ;
        short instruction = fetchOutput.instruction;
        short maskOpCode = (short)0b1111000000000000 ;
        opcode = (short) (instruction & maskOpCode);
        opcode =(short) (opcode >> 12) ;
        short  maskR1Code  = (short) 0b0000111111000000;
        r1 = (short) (instruction & maskR1Code);
        r1 = (short)(r1>>6);
        switch (opcode) {
            case 4 : isBranch = true ;
            case 3 :
            case 8 :
            case 9 :
            case 10 :
            case 11 : immediate =(short)(0b0000000000111111 & instruction);break;
            default:r2 = (short)(0b0000000000111111 & instruction);break;
        }
        byte r1Data = registerFile.getGeneralPurposeRegister(r1);
        byte r2Data = registerFile.getGeneralPurposeRegister(r2);
        return new DecodeOutput(opcode,r1Data,r2Data,immediate,isBranch);

    }

    public static class DecodeOutput {
        public short opcode;
        public short r1Data;
        public short r2Data;
        public short immediate;
        public boolean isBranch ;
        public DecodeOutput
                (short opcode
                , short r1Data
                , short r2Data
                , short immediate
                , boolean isBranch
                )
        {
            this.opcode = opcode ;
            this.r1Data  = r1Data;
            this.r2Data = r2Data;
            this.immediate = immediate;
            this.isBranch = isBranch;

        }



    }

    private void execute(DecodeOutput decodeOutput) {

    }


}

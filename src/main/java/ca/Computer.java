package ca;

import ca.memory.DataMemory;
import ca.memory.InstructionMemory;
import ca.memory.RegisterFile;
import ca.parser.InstructionParser;
import ca.parser.ProgramLoader;
import ca.pipelining.DecodeExecutePipelineRegister;
import ca.pipelining.ForwardingPipelineRegister;
import ca.pipelining.FetchDecodePipelineRegister;

import java.util.List;

public class Computer {
    private static final short ALU_SRC_R2 = 0;
    private static final short ALU_SRC_IMMEDIATE = 1;

    private final InstructionMemory instructionMemory;
    private final DataMemory dataMemory;
    private final RegisterFile registerFile;
    private final InstructionParser instructionParser;
    private final ALU alu;

    private FetchDecodePipelineRegister fetchDecode;
    private DecodeExecutePipelineRegister decodeExecute;
    private ForwardingPipelineRegister forwardingPipelineRegister;

    public Computer(
        InstructionMemory instructionMemory,
        DataMemory dataMemory,
        RegisterFile registerFile,
        InstructionParser parser
    ) {
        this.instructionMemory = instructionMemory;
        this.dataMemory = dataMemory;
        this.registerFile = registerFile;
        this.instructionParser = parser;

        this.alu = new ALU(registerFile);
    }

    public void runProgram(ProgramLoader programLoader) throws CaException {
        loadProgram(programLoader);

        fetchDecode = null;
        decodeExecute = null;
        forwardingPipelineRegister = null;

        int clock = 1;

        while (true) {
            System.out.println();
            System.out.println();
            System.out.println("#################### Clock " + clock + " ####################");

            printInstructionsAtStages();

            printPipelineRegisters("Pipeline registers at start of cycle " + clock);

            FetchDecodePipelineRegister nextFetchOutput = fetch();
            DecodeExecutePipelineRegister nextDecodeExecute = decode();
            ForwardingPipelineRegister nextForwarding = execute();


            fetchDecode = nextFetchOutput;
            decodeExecute = nextDecodeExecute;
            forwardingPipelineRegister = nextForwarding;

            printPipelineRegisters("Pipeline registers after cycle " + clock);

            System.out.println(
                "#################### End of cycle " + clock + " ####################");
            System.out.println();
            System.out.println();
            clock++;

            if (fetchDecode == null && decodeExecute == null) {
                System.out.println("Program finished");
                break;
            }
        }

        dataMemory.printDataMemory();
        registerFile.printAllRegisters();
    }

    private void printInstructionsAtStages() {
        boolean hasFetch = instructionMemory.read(registerFile.getProgramCounter()) != null;

        System.out.println(
            "Instruction At Fetch: " + (hasFetch ? registerFile.getProgramCounter() : "None"));
        System.out.println("Instruction At Decode: " +
                           (fetchDecode != null ? fetchDecode.instructionAddress : "None"));
        System.out.println("Instruction At Execute: " +
                           (decodeExecute != null ? decodeExecute.instructionAddress : "None"));
    }

    private void printPipelineRegisters(String title) {
        System.out.println();
        System.out.println(title);
        System.out.println(
            fetchDecode == null ? "Fetch Decode register is empty" : fetchDecode);
        System.out.println(
            decodeExecute == null ? "Decode Execute register is empty" : decodeExecute);
        System.out.println(
            forwardingPipelineRegister ==
            null ? "Forwarding register is empty" : forwardingPipelineRegister);
        System.out.println();
    }

    private void loadProgram(ProgramLoader programLoader) throws CaException {
        List<String> unparsedInstructions = programLoader.loadProgram();
        short[] parsedInstructions = new short[unparsedInstructions.size()];
        for (int i = 0; i < unparsedInstructions.size(); i++) {
            parsedInstructions[i] = instructionParser.parse(unparsedInstructions.get(i));
        }
        instructionMemory.loadProgram(parsedInstructions);
    }

    private FetchDecodePipelineRegister fetch() {
        System.out.println();
        System.out.println("Fetch Stage Inputs: PC = " + registerFile.getProgramCounter());
        short pc = registerFile.getProgramCounter();
        Short instruction = instructionMemory.read(pc);

        if (instruction == null) return null;

        FetchDecodePipelineRegister out = new FetchDecodePipelineRegister(pc, instruction);
        System.out.println("Instruction #" + pc + " fetched");
        System.out.println("Instruction Binary: " + BitUtils.toBinaryString(instruction, 16));
        System.out.println("Incremented PC to " + (pc + 1) + " in Fetch stage");
        registerFile.incrementProgramCounter();

        System.out.println("Fetch Stage Outputs: " + out);
        System.out.println();

        return out;
    }

    private DecodeExecutePipelineRegister decode() throws CaException {
        System.out.println();
        System.out.println("Decode stage Inputs: " + fetchDecode);

        if (fetchDecode == null) return null;

        boolean isBranch = false;
        short instruction = fetchDecode.instruction;
        short opcode = (short) BitUtils.getBits(instruction, 12, 15);
        short r1 = (short) BitUtils.getBits(instruction, 6, 11);
        short r2 = (short) BitUtils.getBits(instruction, 0, 5);
        short immediate = (short) BitUtils.signExtend(BitUtils.getBits(instruction, 0, 5), 6);

        if (opcode == Opcode.BEQZ) {
            isBranch = true;
        }

        byte r1Data = registerFile.getGeneralPurposeRegister(r1);
        byte r2Data = registerFile.getGeneralPurposeRegister(r2);

        // True for Opcode.JR only
        boolean isJump = opcode == Opcode.JR;

        /*
         Opcode.ADD -> ALU.ADD
         Opcode.SUB -> ALU.SUB
         Opcode.MUL -> ALU.MUL
         Opcode.AND -> ALU.AND
         Opcode.OR -> ALU.OR
         Opcode.SLC -> ALU.SLC
         Opcode.SRL -> ALU.SRL
         Opcode.SB, Opcode.LB, Opcode.LDI, Opcode.BEQZ -> ALU.TRANSFER
         Opcode.JR -> ALU.CONCAT
        */
        int aluOpcode = opcode;
        switch (opcode) {
            case Opcode.SB:
            case Opcode.LB:
            case Opcode.LDI:
            case Opcode.BEQZ:
                aluOpcode = ALU.TRANSFER;
                break;
            case Opcode.ADD:
                aluOpcode = ALU.ADD;
                break;
            case Opcode.SUB:
                aluOpcode = ALU.SUB;
                break;
            case Opcode.MUL:
                aluOpcode = ALU.MUL;
                break;
            case Opcode.AND:
                aluOpcode = ALU.ADD;
                break;
            case Opcode.OR:
                aluOpcode = ALU.OR;
                break;
            case Opcode.SLC:
                aluOpcode = ALU.SLC;
                break;
            case Opcode.SRC:
                aluOpcode = ALU.SRC;
                break;
            case Opcode.JR:
                aluOpcode = ALU.CONCAT;
                break;
            default:
                throw new CaException("Invalid opcode: " + opcode);
        }

        // True for Opcode.SB only
        boolean memoryWrite = opcode == Opcode.SB;

        // True for Opcode.LB only
        boolean memoryRead = opcode == Opcode.LB;

        /*
          The ALU will always be given R1 as a first operand,
          and aluSrc as the second operand.

          For Opcode.ADD, Opcode.SUB, Opcode.MUL, Opcode.AND, Opcode.OR, Opcode.JR -> aluSrc = R2
          For Opcode.SLC, Opcode.SRC, Opcode.LDI, Opcode.BEQZ, Opcode.SB, Opcode.LB -> aluSrc = immediate
         */
        short aluSrc = 0;
        switch (opcode) {
            case Opcode.AND:
            case Opcode.SUB:
            case Opcode.MUL:
            case Opcode.ADD:
            case Opcode.OR:
            case Opcode.JR:
                aluSrc = ALU_SRC_R2;
                break;
            default:
                aluSrc = ALU_SRC_IMMEDIATE;
                break;
        }

        // True for Opcode.SB only
        boolean writeMemoryToRegister = opcode == Opcode.LB;


        /*
          True for Opcode.ADD, Opcode.SUB, Opcode.MUL, Opcode.AND, Opcode.OR, Opcode.SLC, Opcode.SRL, Opcode.LDI, Opcode.LB -> regWrite = true
          True for Opcode.JR, Opcode.SB, Opcode.BEQZ -> regWrite = false
         */
        boolean regWrite = true;
        switch (opcode) {
            case Opcode.JR:
            case Opcode.SB:
            case Opcode.BEQZ:
                regWrite = false;
                break;
        }

        DecodeExecutePipelineRegister decodeExecute = new DecodeExecutePipelineRegister(
            fetchDecode.instructionAddress,
            opcode,
            r1Data,
            r2Data,
            immediate,
            isBranch,
            isJump,
            r1,
            r2,
            aluOpcode,
            memoryWrite,
            memoryRead,
            aluSrc,
            writeMemoryToRegister,
            regWrite
        );

        System.out.println("Decode stage Outputs: " + decodeExecute);
        System.out.println();

        return decodeExecute;
    }

    private ForwardingPipelineRegister execute() throws CaException {
        System.out.println();
        System.out.println("Execute Stage Inputs: " + decodeExecute);

        if (decodeExecute == null) return null;

        byte r1Data = decodeExecute.r1Data;
        byte r2Data = decodeExecute.r2Data;

        if (forwardingPipelineRegister != null && forwardingPipelineRegister.regWrite) {
            if (forwardingPipelineRegister.r1 == decodeExecute.r1) {
                r1Data = forwardingPipelineRegister.r1NewData;
            }

            if (forwardingPipelineRegister.r1 == decodeExecute.r2) {
                r2Data = forwardingPipelineRegister.r1NewData;
            }
        }

        short aluSrc = decodeExecute.aluSrc == ALU_SRC_R2 ? r2Data : decodeExecute.immediate;

        short aluResult =
            alu.execute(decodeExecute.aluOpcode, r1Data, aluSrc);

        byte memoryData = 0;

        if (decodeExecute.memoryRead) {
            memoryData = dataMemory.read(aluResult);
        }

        if (decodeExecute.memoryWrite) {
            dataMemory.write(aluResult, r1Data);
        }

        byte r1NewData = decodeExecute.writeMemoryToRegister ? memoryData : (byte) aluResult;

        if (decodeExecute.regWrite) {
            registerFile.setGeneralPurposeRegister(decodeExecute.r1, r1NewData);
        }

        ForwardingPipelineRegister forwardingPipelineRegister = new ForwardingPipelineRegister(
            decodeExecute.r1,
            r1NewData,
            decodeExecute.regWrite
        );

        if (decodeExecute.isJump) {
            registerFile.setProgramCounter(aluResult);
            System.out.println("Set PC to " + aluResult + " in execute stage");
            // Flush the pipeline
            fetchDecode = null;
            decodeExecute = null;
        } else if (decodeExecute.isBranch && r1Data == 0) {
            registerFile.setProgramCounter((short) (
                decodeExecute.instructionAddress + 1 + decodeExecute.immediate
            ));

            System.out.println(
                "Set PC to " + registerFile.getProgramCounter() +
                " in execute stage");

            // Flush the pipeline
            fetchDecode = null;
            decodeExecute = null;
        }

        System.out.println(forwardingPipelineRegister);
        System.out.println();

        return forwardingPipelineRegister;
    }
}

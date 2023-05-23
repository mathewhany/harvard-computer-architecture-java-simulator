package ca.parser;

public class AssemblyInstructionParser implements InstructionParser {
    public short parse(String instruction) {
//        return parseMIPSAssembly(instruction);
        return 0;
    }

    private static short parseMIPSAssembly(String assemblyCode) {
        String[] assemblyCodeArray = assemblyCode.split(" ");
        short binaryCode = 0;
        switch (assemblyCodeArray[0]) {
            case "ADD":
                binaryCode = 0;
                break;
            case "SUB":
                binaryCode = 1;
                break;
            case "MUL":
                binaryCode = 2;
                break;
            case "LDI":
                binaryCode = 3;
                break;
            case "BEQZ":
                binaryCode = 4;
                break;
            case "AND":
                binaryCode = 5;
                break;
            case "OR":
                binaryCode = 6;
                break;
            case "JR":
                binaryCode = 7;
                break;
            case "SLC":
                binaryCode = 8;
                break;
            case "SRC":
                binaryCode = 9;
                break;
            case "LB":
                binaryCode = 10;
                break;
            case "SB":
                binaryCode = 11;
                break;
        }
        int firstOperand = Integer.parseInt(assemblyCodeArray[1].substring(1));
        int secondOperand = 0;
        if(assemblyCodeArray[2].charAt(0) == 'R'){
            secondOperand = Integer.parseInt(assemblyCodeArray[2].substring(1));
        }else{
            secondOperand = Integer.parseInt(assemblyCodeArray[2]);
        }
        binaryCode = (short) (binaryCode << 6 | firstOperand);
        binaryCode = (short) (binaryCode << 6 | secondOperand);
        return binaryCode;
    }



    private static void test() {
        String assemblyCode1 = "ADD R0 R1";
        String assemblyCode2 = "SUB R2 R3";
        String assemblyCode3 = "MUL R10 R11";
        String assemblyCode4 = "LDI R20 21";
        String assemblyCode5 = "BEQZ R25 26";
        String assemblyCode6 = "AND R30 R31";
        String assemblyCode7 = "OR R40 R41";
        String assemblyCode8 = "JR R45 R46";
        String assemblyCode9 = "SLC R49 50";
        String assemblyCode10 = "SRC R55 56";
        String assemblyCode11 = "LB R60 61";
        String assemblyCode12 = "SB R63 63";
        System.out.println(parseMIPSAssembly(assemblyCode1));
        System.out.println(parseMIPSAssembly(assemblyCode2));
        System.out.println(parseMIPSAssembly(assemblyCode3));
        System.out.println(parseMIPSAssembly(assemblyCode4));
        System.out.println(parseMIPSAssembly(assemblyCode5));
        System.out.println(parseMIPSAssembly(assemblyCode6));
        System.out.println(parseMIPSAssembly(assemblyCode7));
        System.out.println(parseMIPSAssembly(assemblyCode8));
        System.out.println(parseMIPSAssembly(assemblyCode9));
        System.out.println(parseMIPSAssembly(assemblyCode10));
        System.out.println(parseMIPSAssembly(assemblyCode11));
        System.out.println(parseMIPSAssembly(assemblyCode12));
    }
}

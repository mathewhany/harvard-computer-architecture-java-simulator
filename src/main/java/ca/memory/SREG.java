package ca.memory;

public class SREG {
    private static boolean carryFlag;
    private static boolean overflowFlag;
    private static boolean negativeFlag;
    private static boolean signFLag;
    private static boolean zeroFlag;


    public static boolean isCarryFlag() {
        return carryFlag;
    }

    public static boolean isOverflowFlag() {
        return overflowFlag;
    }

    public static boolean isNegativeFlag() {
        return negativeFlag;
    }

    public static boolean isSignFLag() {
        return signFLag;
    }

    public static boolean isZeroFlag() {
        return zeroFlag;
    }

    public static void setCarryFlag(boolean carryFlag) {
        SREG.carryFlag = carryFlag;
    }

    public static void setOverflowFlag(boolean overflowFlag) {
        SREG.overflowFlag = overflowFlag;
    }

    public static void setNegativeFlag(boolean negativeFlag) {
        SREG.negativeFlag = negativeFlag;
    }

    public static void setSignFLag(boolean signFLag) {
        SREG.signFLag = signFLag;
    }

    public static void setZeroFlag(boolean zeroFlag) {
        SREG.zeroFlag = zeroFlag;
    }

}

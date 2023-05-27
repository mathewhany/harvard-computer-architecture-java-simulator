package ca;

public class BitUtils {
    public static int getBits(int value, int start, int end) {
        int mask = 0;

        for (int i = start; i <= end; i++) {
            mask |= 1 << i;
        }

        return (value & mask) >> start;
    }

    public static int getBit(int value, int index) {
        return getBits(value, index, index);
    }

    public static byte setBit(byte value, int index, boolean bit) {
        if (bit) {
            return (byte) (value | (1 << index));
        } else {
            return (byte) (value & ~(1 << index));
        }
    }

    public static String toBinaryString(int value, int length) {
        String binaryString = Integer.toBinaryString(value);

        if (binaryString.length() > length) {
            return binaryString.substring(binaryString.length() - length);
        }

        while (binaryString.length() < length) {
            binaryString = "0" + binaryString;
        }

        return binaryString;
    }

    public static int signExtend(int bits, int length) {
        int signBit = getBit(bits, length - 1);

        if (signBit == 0) {
            return bits;
        }

        int mask = 0;

        for (int i = 0; i < length; i++) {
            mask |= 1 << i;
        }

        return bits | ~mask;
    }

    public static boolean hasCarry(int a, int b) {
        int aInt = a;
        int bInt = b;
        aInt &= 0x0000_00FF;
        bInt &= 0x0000_00FF;

        int rInt = aInt + bInt;

        return getBit(rInt, 8) == 1;
    }

    public static boolean has2sComplementOverflow(byte a, byte b, int result) {
        boolean lastCarry = hasCarry(a, b);
        boolean secondLastCarry = (getBit(a, 7) == 1) ^ (getBit(b, 7) == 1) ^ (getBit(result, 7) == 1);

        return lastCarry ^ secondLastCarry;
    }
}

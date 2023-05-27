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
}

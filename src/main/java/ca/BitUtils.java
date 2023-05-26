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

    public static short setBit(short value, int index, boolean bit) {
        if (bit) {
            return (short) (value | (1 << index));
        } else {
            return (short) (value & ~(1 << index));
        }
    }

    public static String toBinaryString(int value, int length) {
        String binaryString = Integer.toBinaryString(value);

        while (binaryString.length() < length) {
            binaryString = "0" + binaryString;
        }

        return binaryString;
    }
}

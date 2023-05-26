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
}

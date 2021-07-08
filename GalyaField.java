import java.util.ArrayList;
import java.util.Arrays;

public class GalyaField {
    private static final int m = 293;
    private static boolean[] generator = new boolean[m + 1];
    private static boolean[] a = new boolean[m];
    private static boolean[] b = new boolean[m];
    private static String n = "11100101110001101100001001010010000110001010110010101000010000010001101000100000111011000111111101101000110100100100000010010011001100100101111001010010001101110101111010101010011010111001010101110011011100111111000100110100111011010100010001011000101110101111011011000001111001001001110101001";


    public static boolean[] toBoolean(int... numbs) {
        boolean[] b = new boolean[m];
        for (int i : numbs) {
            b[m - i - 1] = true;
        }
        return b;
    }

    public static boolean[] fromBit(String str) {
        boolean[] b = new boolean[m];
        for (int i = 0; i < m; i++) {
            if (str.charAt(i) == '1') {
                b[i] = true;
            } else b[i] = false;
        }
        return b;
    }

    public static String toBit(boolean[] a) {
        String str = "";
        for (int i = 0; i < m; i++) {
            if (a[i]) {
                str = str.concat("1");
            } else str = str.concat("0");
        }
        return str;
    }

    public static String fromBoolean(boolean[] a) {
        ArrayList<Integer> ints = new ArrayList<>();
        for (int i = 0; i < a.length; i++) {
            if (a[i]) ints.add(a.length - i - 1);
        }
        return ints.toString();
    }

    public static void setGenerator(int... numbs) {
        for (int i : numbs) {
            generator[m - i] = true;
        }
    }

    public static boolean[] polyAdd(boolean[] first, boolean[] second) {
        boolean[] third = new boolean[first.length];
        for (int i = 0; i < first.length; i++) {
            third[i] = first[i] ^ second[i];
        }
        return third;
    }

    public static boolean[] polyMul(boolean[] first, boolean[] second) {
        boolean[] third = new boolean[2 * m - 1];
        for (int i = 0; i < m; i++) {
            if (!first[i]) continue;
            int k = i;
            for (int j = 0; j < m; j++) {
                third[k] = second[j] ^ third[k];
                k++;
            }
        }
        while (bitLength(third) <= bitLength(toLength(generator, third.length))) {
            boolean[] temp = shiftToHigh(generator, bitLength(third));
            third = polyAdd(third, temp);
        }
        boolean[] rat = new boolean[m];
        System.arraycopy(third, m - 1, rat, 0, m);
        return rat;
    }

    public static boolean[] trace(boolean[] a) {
        boolean[] b = new boolean[m];
        boolean[] temp = polySqrt(a);
        b = polyAdd(b, a);
        a = polySqrt(a);
        for (int i = 1; i < m - 1; i++) {
            b = polyAdd(b, a);
            a = polyMul(a, temp);
        }
        return b;
    }

    public static boolean[] polySqrt(boolean[] a) {
        return polyMul(a, a);
    }

    public static boolean[] polyPow(boolean[] a, String n) {
        char[] chars = n.toCharArray();
        boolean flag = true;
        boolean[] b = new boolean[m];
        boolean[] temp = a;
        for (int i = chars.length - 1; i >= 0; i--) {
            if (flag && chars[i] == '1') {
                b = temp;
                flag = false;
            } else if (!flag && chars[i] == '1') {
                b = polyMul(b, temp);
            }
            temp = polySqrt(temp);
        }
        return b;
    }

    public static boolean[] polyVerse(boolean[] a) {
        boolean[] b = polyMul(a, a);
        for (int i = 0; i < m - 2; i++) {
            a = polyMul(a, b);
            b = polySqrt( b);
        }
        return polySqrt(a);
    }


    public static boolean[] shiftToHigh(boolean[] a, int n) {
        if (bitLength(a) == -1) return new boolean[]{false};
        boolean[] c = new boolean[2 * m - 1];
        System.arraycopy(a, bitLength(a), c, n, a.length - bitLength(a));
        return c;
    }

    public static int bitLength(boolean[] a) {
        for (int i = 0; i < a.length; i++) if (a[i]) return i;
        return -1;
    }

    public static boolean[] toLength(boolean[] a, int n) {
        boolean[] b = new boolean[n];
        System.arraycopy(a, 0, b, b.length - a.length, a.length);
        return b;
    }

    public static char[] toBinary(int n) {
        String str = Integer.toBinaryString(n);
        return str.toCharArray();
    }

    public static boolean[] getGenerator() {
        return generator;
    }

    public static void setA(boolean[] a) {
        GalyaField.a = a;
    }

    public static void setB(boolean[] b) {
        GalyaField.b = b;
    }


    public static void main(String[] args) {
        setGenerator(293, 11, 6, 1, 0);
        setA(fromBit("10110111100010011101010011000011010001011000110000111001010010010100111000000101000100100101101011101011001111000011110000011111001010101111110111100100011100000011101110001011000110100001101011011010100011101101101001001100111010100001010011101110001010111011111001100110001010110101000000100"));
        setB(fromBit("10011010101000000100111101011100001000001011100001111010101011110100011111110010011010110010101110111001110110111001011001110000001101100011101011000111001001001101110010010101001010001111010011001111101000010110001101101000101110101110000000111100010111110001101101110001100110101101000000001"));
        System.out.println(toBit(polyAdd(GalyaField.a, GalyaField.b)));
        System.out.println(toBit(polyMul(GalyaField.a, GalyaField.b)));
        System.out.println(toBit(polySqrt(a)));
        System.out.println(toBit(polyVerse(GalyaField.a)));
        System.out.println(toBit(polyPow(GalyaField.a, n)));
    }
}

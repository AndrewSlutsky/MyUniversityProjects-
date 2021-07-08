import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Scanner;

public class MyLittleInt {
    private int[] firstBigNumb;
    private int[] secondBigNumb;
    private int[] thirdBigNumb;

    MyLittleInt() {
        firstBigNumb = read();
        secondBigNumb = read();
        thirdBigNumb = read();
        //System.out.println();
        // System.out.println(to16b(to32b(secondBigNumb)));
    }

    public static int[] read() {
        Scanner word = new Scanner(System.in);
        char[] wo = word.next().toCharArray();
        int[] bit16 = new int[wo.length / 8 + 1];
        int j = wo.length - 8;
        for (int i = bit16.length - 1; i >= 0; i--) {
            if (j > 0) {
                bit16[i] = Integer.parseUnsignedInt(String.valueOf(wo, j, 8), 16);
            } else {
                j += 8;
                bit16[i] = Integer.parseUnsignedInt(String.valueOf(wo, 0, j), 16);
                break;
            }
            j -= 8;
        }
        return bit16;
    }

    public static int[] longAdd(int[] first, int[] second) {
        if (first.length > second.length) second = toLength(second, first.length);
        if (second.length > first.length) first = toLength(first, second.length);
        int[] c = new int[first.length];
        System.arraycopy(second, 0, c, first.length - second.length, second.length);
        int carry = 0;
        long temp = 0;
        int[] third = new int[first.length + 1];
        for (int i = first.length - 1; i >= 0; i--) {
            temp = Integer.toUnsignedLong(first[i]) + Integer.toUnsignedLong(c[i]) + carry;
            third[i + 1] = (int) temp;
            carry = (int) (temp / Math.pow(2, 32));
        }
        return third;
    }

    public static int[] longSub(int[] first, int[] second) {
        if (first.length > second.length) second = toLength(second, first.length);
        if (second.length > first.length) first = toLength(first, second.length);
        int borrow = 0;
        long temp = 0;
        int[] third = new int[first.length];
        for (int i = first.length - 1; i >= 0; i--) {
            temp = Integer.toUnsignedLong(first[i]) - Integer.toUnsignedLong(second[i]) - borrow;
            if (temp >= 0) {
                borrow = 0;
            } else {
                temp = (long) (Math.pow(2, 32) + temp);
                borrow = 1;
            }

            third[i] = (int) temp;
        }
        return third;
    }


    public static int[] longMul(int[] first, int[] second) {
        if (first.length > second.length) second = toLength(second, first.length);
        if (second.length > first.length) first = toLength(first, second.length);
        int[] third = new int[2 * first.length];
        int[] temp1 = new int[first.length + 1];
        int k = 0;
        for (int i = first.length - 1; i >= 0; i--) {
            long carry = 0;
            for (int j = first.length - 1; j >= 0; j--) {
                long temp2 = Integer.toUnsignedLong(first[j]) * Integer.toUnsignedLong(second[i]) + carry;
                temp1[j + 1] = (int) temp2;
                carry = temp2 >>> 32;
            }
            temp1[0] = (int) carry;
            third = longAdd(third, shiftIntLeft(temp1, k++));
        }
        return third;
    }

    public static DivModRes longDivMod(int[] a, int[] b) {
        if (a.length > b.length) b = toLength(b, a.length);
        if (b.length > a.length) a = toLength(a, b.length);
        int[] r = Arrays.copyOf(a, a.length);
        int[] q = retZERO();
        while (compare(r, b) >= 0) {
            int k = bitLength(b);
            int t = bitLength(r);
            int[] c = longShiftBitsToHeight(b, k - t);
            if (compare(r, c) < 0) {
                t = t + 1;
                c = longShiftBitsToHeight(b, k - t);
            }
            r = longSub(to32b(r), to32b(c));
            r = toBit(r);
            q = longAdd(to32b(q), to32b(longShiftBitsToHeight(retONE(), k - t)));
            q = toBit(q);
            if (r.length > b.length) b = toLength(b, r.length);
            if (b.length > r.length) r = toLength(r, b.length);
        }
        r = to32b(r);
        q = to32b(q);
        return new DivModRes(r, q);
    }

    public static int[] longPower(int[] a, int[] b) {
        int[] c = retONE();
        b = toBit(b);
        for (int i = b.length - 1; i >= bitLength(b) - 1; i--) {
            if (b[i] == 1) {
                c = (longMul(c, a));
            }
            a = longMul(a, a);
        }
        for (int k : c) {
            System.out.print(k);
        }
        System.out.println();
        return c;
    }

    public static int[] gcd(int[] a, int[] b) {
        if (compare(a, retZERO()) == 0) return b;
        if (compare(b, retZERO()) == 0) return a;
        if (compare(a, b) == 0) return b;
        boolean v1 = false;
        boolean v2 = false;
        if (a[a.length - 1] == 0) v1 = true;
        if (b[b.length - 1] == 0) v2 = true;
        if (v1 && v2) {
            return shiftIntLeft(gcd(shiftIntRight(a, 1), shiftIntRight(b, 1)), 1);
        } else if (v1) {
            return gcd(shiftIntRight(a, 1), b);
        } else if (v2) {
            return gcd(a, shiftIntRight(b, 1));
        } else if (compare(a, b) > 0) {
            return gcd(shiftIntRight(toBit(longSub(to32b(a), to32b(b))), 1), b);
        } else {
            return gcd(a, shiftIntRight(toBit(longSub(to32b(b), to32b(a))), 1));
        }
    }

    public static int[] lcm(int[] a, int[] b) {
        int[] ab = longMul(a, b);
        ab = toBit(ab);
        DivModRes divModRes = longDivMod(ab, gcd(toBit(a), toBit(b)));
        return divModRes.getQ();
    }

    static int[] muFunction(int[] m) {
        return longDivMod(longShiftBitsToHeight(retONE(), toBit(m).length * 2), toBit(m)).getQ();
    }

    static int[] barrettReduction(int[] a, int[] n, int[] mu) {
        if(mu == null ) mu = muFunction(n);
        int[] q = shiftIntRight(a, n.length - 1);
        q = longMul(q, mu);
        q = shiftIntRight(q, n.length + 1);
        int[] r = longSub(a, longMul(q, n));
        while (compare(r, n) >= 0) {
            r = longSub(r, n);
        }
        return r;
    }

    static int[] modAdd(int[] a, int[] b, int[] n) {
        int[] c = longAdd(a,b);
        return barrettReduction(c, n, null);
    }

    static int[] modSub(int[] a, int[] b, int[] n) {
        return barrettReduction(longSub(a, b), n, null);
    }

    static int[] modMul(int[] a, int[] b, int[] n) {
        return barrettReduction(longMul(a, b), n, null);
    }

    static int[] barrettModPower(int[] a, int[] b, int[] n) {
        int[] c = retONE();
        int[] mu = muFunction(n);
        int k = bitLength(b);
        System.out.println(Arrays.toString(b));
        System.out.println(k + " " + b.length);
        for (int i = b.length - 1; i > k - 1; i--) {
            c = Arrays.copyOfRange(c, bitLength(c), c.length);
            a = Arrays.copyOfRange(a, bitLength(a), a.length);
            if (b[i] == 1) c = barrettReduction(longMul(c, a), n, mu);
            System.out.println(i);
            a = barrettReduction(longMul(a, a), n, mu);
            System.out.println(Arrays.toString(a));
        }
        return c;
    }

    public static String to16b(int[] bigNumb) {
        String str = "";
        str = str.concat("0");
        for (int i = bitLength(bigNumb) - 1; i < bigNumb.length; i++) {
            if (Integer.toUnsignedString(bigNumb[i], 16).length() == 8) {
                str = str.concat(Integer.toUnsignedString(bigNumb[i], 16));
            } else {
                for (int j = 0; j < 8 - Integer.toUnsignedString(bigNumb[i], 16).length(); j++) str = str.concat("0");
                str = str.concat(Integer.toUnsignedString(bigNumb[i], 16));
            }

        }
        return str.toUpperCase();
    }

    public static int[] shiftIntRight(int[] a, int n) {
        int check = a.length - n;
        if (check <= 0) return retZERO();
        int[] c = new int[a.length];
        System.arraycopy(a, 0, c, n, a.length - n);
        return c;
    }

    public static int[] shiftIntLeft(int[] a, int n) {
        int check = a.length - n;
        if (check <= 0) return new int[a.length];
        int[] c = new int[a.length + n];
        System.arraycopy(a, 0, c, 0, a.length);
        return c;
    }

    public static int[] longShiftBitsToHeight(int[] a, int n) {
        if (n == 0) return a;
        int[] c = new int[a.length + n];
        System.arraycopy(a, 0, c, 0, a.length);
        return c;
    }

    public static int[] toBit(int[] a) {
        String str = "";
        for (int b : a) {
            int length = Long.toBinaryString(Integer.toUnsignedLong(b)).length();
            if (length == 32) {
                str = str.concat(Long.toBinaryString(Integer.toUnsignedLong(b)));
            } else {
                for (int i = length; i < 32; i++) str = str.concat("0");
                str = str.concat(Long.toBinaryString(Integer.toUnsignedLong(b)));
            }
        }
        int[] c = new int[str.length()];
        char[] ch = str.toCharArray();
        for (int i = 0; i < c.length; i++) {
            c[i] = Integer.parseUnsignedInt(String.valueOf(ch[i]));
        }
        return c;
    }

    public static int[] to32b(int[] a) {
        char[] wo = new char[a.length];
        for (int i = 0; i < a.length; i++) {
            wo[i] = Character.forDigit(a[i], 2);
        }
        int[] bit16 = new int[wo.length / 32 + 1];
        int j = wo.length - 32;
        for (int i = bit16.length - 1; i >= 0; i--) {
            if (j > 0) {
                bit16[i] = Integer.parseUnsignedInt(String.valueOf(wo, j, 32), 2);
            } else {
                j += 32;
                bit16[i] = Integer.parseUnsignedInt(String.valueOf(wo, 0, j), 2);
                break;
            }
            j -= 32;
        }
        return bit16;

    }

    public static int bitLength(int[] a) {
        int i = 0;
        while (i < a.length - 1 && a[i] == 0) i++;
        return i;
    }

    public static int[] toLength(int[] a, int n) {
        int[] c = new int[n];
        System.arraycopy(a, 0, c, n - a.length, a.length);
        return c;
    }

    public static int compare(int[] first, int[] second) {
        if (second.length > first.length) {
            first = toLength(first, second.length);
        } else if (first.length > second.length) {
            second = toLength(second, first.length);
        }
        for (int i = 0; i < first.length; i++) {
            if (first[i] > second[i]) {
                return 1;
            } else if (first[i] < second[i]) return -1;
        }
        return 0;
    }

    public static int[] retZERO() {
        return new int[1];
    }


    public static int[] retONE() {
        return new int[]{1};
    }

    public static class DivModRes {
        int[] q;
        int[] r;

        DivModRes(int[] r, int[] q) {
            this.r = r;
            this.q = q;
        }

        public int[] getQ() {
            return q;
        }

        public int[] getR() {
            return r;
        }

    }

    public static void main(String[] args) {
        MyLittleInt ha = new MyLittleInt();/*
        System.out.println(to16b(longMul(ha.thirdBigNumb, longAdd(ha.firstBigNumb, ha.secondBigNumb))));
        System.out.println(to16b(longAdd(longMul(ha.firstBigNumb, ha.thirdBigNumb), longMul(ha.secondBigNumb, ha.thirdBigNumb))));
        System.out.println(to16b(longMul(longAdd(ha.firstBigNumb, ha.secondBigNumb), ha.thirdBigNumb)));
*/
        System.out.println(to16b(to32b(gcd(toBit(ha.firstBigNumb), toBit(ha.secondBigNumb)))));
        System.out.println(Arrays.toString(lcm(ha.firstBigNumb, ha.secondBigNumb)));
        System.out.println(to16b(barrettReduction(ha.firstBigNumb, ha.secondBigNumb, null)));
        System.out.println(to16b(modAdd(ha.firstBigNumb, ha.secondBigNumb, ha.thirdBigNumb)));
        System.out.println(to16b(modSub(ha.firstBigNumb, ha.secondBigNumb, ha.thirdBigNumb)));
        System.out.println(to16b(modMul(ha.firstBigNumb, ha.secondBigNumb, ha.thirdBigNumb)));
        System.out.println((to16b(barrettModPower(ha.firstBigNumb, toBit(ha.secondBigNumb), ha.thirdBigNumb))));

        /*      int[] temp = new int[]{0};
        System.out.println(to16b(longMul(new int[]{100}, ha.firstBigNumb)));
        for (int i = 0; i < 100; i++) {
            temp = longAdd(ha.firstBigNumb, temp);
        }
        System.out.println(to16b(temp));
  */      // System.out.println(to16b(ha.longAdd(ha.firstBigNumb, ha.secondBigNumb)));
        //    System.out.println(to16b(ha.longSub(ha.firstBigNumb, ha.secondBigNumb)));
    }
}

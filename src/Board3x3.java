import java.io.*;
import java.util.*;

public class Board3x3 {

    public static void main(String[] args) {

        Board3x3 test = new Board3x3(9,8,1,7,6,1,5,4,4);
        System.out.println(Board3x3.getMove2(test));

    }

    long name;
    double finalSuccessRate = -1.0;
    double[] successRates = new double[4];
    int zeroCt = -1;

    public Board3x3(long name) {
        this.name = name;
    }

    public Board3x3(int... arr) {
        for (int i = 0; i < arr.length; i++) {
            name |= (long) arr[i] << (4 * i);
        }
    }

    public Map<Long, Integer> getChildren(int direction, int val) {
        HashMap<Long, Integer> hi = new HashMap<>();
        long merged = merge(direction);
        if (merged != name)
            for (int place = 0; place < 9; place++) {
                if ((merged & (0b1111L << (4 * place))) == 0) {
                    long thing = merged;
                    thing |= (long) val << (4 * place);
                    long ss = getSmallestSymmetry(thing);
                    if (!hi.containsKey(ss))
                        hi.put(ss, 1);
                    else
                        hi.put(ss, hi.get(ss) + 1);
                }
            }
        return hi;
    }

    public void spawn(int x, int y, int val) {
        int[] tiles = getTiles();
        tiles[3 * (2 - y) + (x)] = val + 1;
        name = 0;
        for (int i = 0; i < tiles.length; i++) {
            name |= (long) tiles[i] << (4 * i);
        }
    }

    public HashMap<Board3x3, Double> moveSpawns(int direction, int... vals) {

        direction = (direction + 1) % 4;

        HashMap<Board3x3, Double> hi = new HashMap<>();
        long merged = merge(direction);
        if (merged != name) {
            int zc = new Board3x3(merged).getZeroCt();
            for (int val : vals) {
                double divisor = vals.length == 1 ? zc : zc / (1.7 - 0.8 * val);
                for (int place = 0; place < 9; place++) {
                    if ((merged & (0b1111L << (4 * place))) == 0) {
                        long thing = merged;
                        thing |= (long) val << (4 * place);
                        Board3x3 ss = new Board3x3(thing);
                        if (!hi.containsKey(ss))
                            hi.put(ss, 1.0 / divisor);
                    }
                }
            }
        }
        return hi;
    }

    public long merge(int direction) {
        long ret = 0;
        switch (direction) {
            case 0: { // down
                int row1 = (int) ((name & (0b1111L))) + 16 * (int) ((name & (0b1111L << 12)) >> 12) + 256 * (int) ((name & (0b1111L << 24)) >> 24);
                int row2 = (int) ((name & (0b1111L << 4)) >> 4) + 16 * (int) ((name & (0b1111L << 16)) >> 16) + 256 * (int) ((name & (0b1111L << 28)) >> 28);
                int row3 = (int) ((name & (0b1111L << 8)) >> 8) + 16 * (int) ((name & (0b1111L << 20)) >> 20) + 256 * (int) ((name & (0b1111L << 32)) >> 32);
                int[] merge1;
                int[] merge2;
                int[] merge3;
                merge1 = merges[row1];
                merge2 = merges[row2];
                merge3 = merges[row3];
                ret |= (long) merge1[0] << 24;
                ret |= (long) merge1[1] << 12;
                ret |= merge1[2];
                ret |= (long) merge2[0] << 28;
                ret |= (long) merge2[1] << 16;
                ret |= (long) merge2[2] << 4;
                ret |= (long) merge3[0] << 32;
                ret |= (long) merge3[1] << 20;
                ret |= (long) merge3[2] << 8;
                break;
            }
            case 2: { // up
                int row1 = (int) ((name & (0b1111L << 24)) >> 24) + 16 * (int) ((name & (0b1111L << 12)) >> 12) + 256 * (int) ((name & (0b1111L)));
                int row2 = (int) ((name & (0b1111L << 28)) >> 28) + 16 * (int) ((name & (0b1111L << 16)) >> 16) + 256 * (int) ((name & (0b1111L << 4)) >> 4);
                int row3 = (int) ((name & (0b1111L << 32)) >> 32) + 16 * (int) ((name & (0b1111L << 20)) >> 20) + 256 * (int) ((name & (0b1111L << 8)) >> 8);
                int[] merge1;
                int[] merge2;
                int[] merge3;
                merge1 = merges[row1];
                merge2 = merges[row2];
                merge3 = merges[row3];
                ret |= merge1[0];
                ret |= (long) merge1[1] << 12;
                ret |= (long) merge1[2] << 24;
                ret |= (long) merge2[0] << 4;
                ret |= (long) merge2[1] << 16;
                ret |= (long) merge2[2] << 28;
                ret |= (long) merge3[0] << 8;
                ret |= (long) merge3[1] << 20;
                ret |= (long) merge3[2] << 32;
                break;
            }
            case 1: { // left
                int row1 = (int) ((name & (0b1111L))) + 16 * (int) ((name & (0b1111L << 4)) >> 4) + 256 * (int) ((name & (0b1111L << 8)) >> 8);
                int row2 = (int) ((name & (0b1111L << 12)) >> 12) + 16 * (int) ((name & (0b1111L << 16)) >> 16) + 256 * (int) ((name & (0b1111L << 20)) >> 20);
                int row3 = (int) ((name & (0b1111L << 24)) >> 24) + 16 * (int) ((name & (0b1111L << 28)) >> 28) + 256 * (int) ((name & (0b1111L << 32)) >> 32);
                int[] merge1;
                int[] merge2;
                int[] merge3;
                merge1 = merges[row1];
                merge2 = merges[row2];
                merge3 = merges[row3];
                ret |= (long) merge1[0] << 8;
                ret |= (long) merge1[1] << 4;
                ret |= merge1[2];
                ret |= (long) merge2[0] << 20;
                ret |= (long) merge2[1] << 16;
                ret |= (long) merge2[2] << 12;
                ret |= (long) merge3[0] << 32;
                ret |= (long) merge3[1] << 28;
                ret |= (long) merge3[2] << 24;
                break;
            }
            case 3: { // right
                int row1 = (int) ((name & (0b1111L << 8)) >> 8) + 16 * (int) ((name & (0b1111L << 4)) >> 4) + 256 * (int) ((name & (0b1111L)));
                int row2 = (int) ((name & (0b1111L << 20)) >> 20) + 16 * (int) ((name & (0b1111L << 16)) >> 16) + 256 * (int) ((name & (0b1111L << 12)) >> 12);
                int row3 = (int) ((name & (0b1111L << 32)) >> 32) + 16 * (int) ((name & (0b1111L << 28)) >> 28) + 256 * (int) ((name & (0b1111L << 24)) >> 24);
                int[] merge1;
                int[] merge2;
                int[] merge3;
                merge1 = merges[row1];
                merge2 = merges[row2];
                merge3 = merges[row3];
                ret |= merge1[0];
                ret |= (long) merge1[1] << 4;
                ret |= (long) merge1[2] << 8;
                ret |= (long) merge2[0] << 12;
                ret |= (long) merge2[1] << 16;
                ret |= (long) merge2[2] << 20;
                ret |= (long) merge3[0] << 24;
                ret |= (long) merge3[1] << 28;
                ret |= (long) merge3[2] << 32;
                break;
            }
        }
        return ret;
    }

    public static long merge(long name, int direction) {
        long ret = 0;
        switch (direction) {
            case 0: { // down
                int row1 = (int) ((name & (0b1111L))) + 16 * (int) ((name & (0b1111L << 12)) >> 12) + 256 * (int) ((name & (0b1111L << 24)) >> 24);
                int row2 = (int) ((name & (0b1111L << 4)) >> 4) + 16 * (int) ((name & (0b1111L << 16)) >> 16) + 256 * (int) ((name & (0b1111L << 28)) >> 28);
                int row3 = (int) ((name & (0b1111L << 8)) >> 8) + 16 * (int) ((name & (0b1111L << 20)) >> 20) + 256 * (int) ((name & (0b1111L << 32)) >> 32);
                int[] merge1;
                int[] merge2;
                int[] merge3;
                merge1 = merges[row1];
                merge2 = merges[row2];
                merge3 = merges[row3];
                ret |= (long) merge1[0] << 24;
                ret |= (long) merge1[1] << 12;
                ret |= merge1[2];
                ret |= (long) merge2[0] << 28;
                ret |= (long) merge2[1] << 16;
                ret |= (long) merge2[2] << 4;
                ret |= (long) merge3[0] << 32;
                ret |= (long) merge3[1] << 20;
                ret |= (long) merge3[2] << 8;
                break;
            }
            case 2: { // up
                int row1 = (int) ((name & (0b1111L << 24)) >> 24) + 16 * (int) ((name & (0b1111L << 12)) >> 12) + 256 * (int) ((name & (0b1111L)));
                int row2 = (int) ((name & (0b1111L << 28)) >> 28) + 16 * (int) ((name & (0b1111L << 16)) >> 16) + 256 * (int) ((name & (0b1111L << 4)) >> 4);
                int row3 = (int) ((name & (0b1111L << 32)) >> 32) + 16 * (int) ((name & (0b1111L << 20)) >> 20) + 256 * (int) ((name & (0b1111L << 8)) >> 8);
                int[] merge1;
                int[] merge2;
                int[] merge3;
                merge1 = merges[row1];
                merge2 = merges[row2];
                merge3 = merges[row3];
                ret |= merge1[0];
                ret |= (long) merge1[1] << 12;
                ret |= (long) merge1[2] << 24;
                ret |= (long) merge2[0] << 4;
                ret |= (long) merge2[1] << 16;
                ret |= (long) merge2[2] << 28;
                ret |= (long) merge3[0] << 8;
                ret |= (long) merge3[1] << 20;
                ret |= (long) merge3[2] << 32;
                break;
            }
            case 1: { // left
                int row1 = (int) ((name & (0b1111L))) + 16 * (int) ((name & (0b1111L << 4)) >> 4) + 256 * (int) ((name & (0b1111L << 8)) >> 8);
                int row2 = (int) ((name & (0b1111L << 12)) >> 12) + 16 * (int) ((name & (0b1111L << 16)) >> 16) + 256 * (int) ((name & (0b1111L << 20)) >> 20);
                int row3 = (int) ((name & (0b1111L << 24)) >> 24) + 16 * (int) ((name & (0b1111L << 28)) >> 28) + 256 * (int) ((name & (0b1111L << 32)) >> 32);
                int[] merge1;
                int[] merge2;
                int[] merge3;
                merge1 = merges[row1];
                merge2 = merges[row2];
                merge3 = merges[row3];
                ret |= (long) merge1[0] << 8;
                ret |= (long) merge1[1] << 4;
                ret |= merge1[2];
                ret |= (long) merge2[0] << 20;
                ret |= (long) merge2[1] << 16;
                ret |= (long) merge2[2] << 12;
                ret |= (long) merge3[0] << 32;
                ret |= (long) merge3[1] << 28;
                ret |= (long) merge3[2] << 24;
                break;
            }
            case 3: { // right
                int row1 = (int) ((name & (0b1111L << 8)) >> 8) + 16 * (int) ((name & (0b1111L << 4)) >> 4) + 256 * (int) ((name & (0b1111L)));
                int row2 = (int) ((name & (0b1111L << 20)) >> 20) + 16 * (int) ((name & (0b1111L << 16)) >> 16) + 256 * (int) ((name & (0b1111L << 12)) >> 12);
                int row3 = (int) ((name & (0b1111L << 32)) >> 32) + 16 * (int) ((name & (0b1111L << 28)) >> 28) + 256 * (int) ((name & (0b1111L << 24)) >> 24);
                int[] merge1;
                int[] merge2;
                int[] merge3;
                merge1 = merges[row1];
                merge2 = merges[row2];
                merge3 = merges[row3];
                ret |= merge1[0];
                ret |= (long) merge1[1] << 4;
                ret |= (long) merge1[2] << 8;
                ret |= (long) merge2[0] << 12;
                ret |= (long) merge2[1] << 16;
                ret |= (long) merge2[2] << 20;
                ret |= (long) merge3[0] << 24;
                ret |= (long) merge3[1] << 28;
                ret |= (long) merge3[2] << 32;
                break;
            }
        }
        return ret;
    }

    public double getSuccessRate(int direction) {
        return successRates[direction];
    }

    public double getSuccessRate() {
        return finalSuccessRate;
    }

    public void setSuccessRate(int direction, double d) {
        successRates[direction] = d;
    }

    public void addSuccessRate(int direction, double d) {
        successRates[direction] += d;
    }

    public void finalizeSuccessRate() {
        if (finalSuccessRate == -1.0)
            for (double d : successRates)
                if (d > finalSuccessRate)
                    finalSuccessRate = d;
    }

    public boolean isDead() {
        int[] tiles = new int[9];
        for (int x = 0; x < 9; x++) {
            tiles[x] = (int) ((name & (0b1111L << (4 * x))) >> (4 * x));
        }
        int sum = 0;
        for (int i : tiles)
            if (i > 0)
                sum += (int) Math.pow(2, i);
        if (getZeroCt() == 0) {
            for (int x = 0; x < 9; x++) {
                if (x % 3 > 0)
                    if (tiles[x] == tiles[x - 1])
                        return false;
                if (x > 2)
                    if (tiles[x] == tiles[x - 3])
                        return false;
            }
            return true;
        }
        return false;
    }

    public static boolean isDead(long name) {
        int[] tiles = new int[9];
        for (int x = 0; x < 9; x++) {
            tiles[x] = (int) ((name & (0b1111L << (4 * x))) >> (4 * x));
        }
        int sum = 0;
        for (int i : tiles)
            if (i > 0)
                sum += (int) Math.pow(2, i);
        if (getZeroCt(name) == 0) {
            for (int x = 0; x < 9; x++) {
                if (x % 3 > 0)
                    if (tiles[x] == tiles[x - 1])
                        return false;
                if (x > 2)
                    if (tiles[x] == tiles[x - 3])
                        return false;
            }
        }
        return false;
    }

    public int getZeroCt() {
        zeroCt = 0;
        for (int x = 0; x <= 32; x += 4) {
            if (((name >> x) & 0b1111) == 0)
                zeroCt++;
        }
        return zeroCt;
    }

    public static int getZeroCt(long name) {
        int zeroCt = 0;
        for (int x = 0; x <= 32; x += 4) {
            if (((name & (0b1111L << x)) >> x) == 0)
                zeroCt++;
        }
        return zeroCt;
    }

    public int getCt(int val) {
        return getCt(name, val);
    }

    public static int getCt(long name, int val) {
        int ct = 0;
        for (int x = 0; x <= 32; x += 4) {
            if (((name & (0b1111L << x)) >> x) == val)
                ct++;
        }
        return ct;
    }

    public int getSum() {
        int sum = 0;
        for (int x = 0; x <= 32; x += 4) {
            int oSum = sum;
            sum += (int) Math.pow(2, ((name >> x) & 0b1111));
        }
        return (sum - getZeroCt()) / 2;
    }

    public long getSmallestSymmetry() {
        int[] syms = rotationsToCheck[getTernarySymmetry(name)];
        if (syms.length == 1)
            return getRotation(name, syms[0]);
        long minSc = Long.MAX_VALUE;
        for (int sym : syms) {
            long bd = getRotation(name, sym);
            if (bd < minSc)
                minSc = bd;
        }
        return minSc;
    }

    static public long getLargestSymmetry(long name) {
        long max = name;
        for (int x = 0; x < 8; x++) {
            long l2 = getRotation(name, x);
            if (l2 > max)
                max = l2;
        }
        return max;
    }

    static public long getSmallestSymmetry(long name) {
        int[] syms = rotationsToCheck[getTernarySymmetry(name)];
        if (syms.length == 1)
            return getRotation(name, syms[0]);
        long minSc = Long.MAX_VALUE;
        for (int sym : syms) {
            long bd = getRotation(name, sym);
            if (bd < minSc)
                minSc = bd;
        }
        return minSc;
    }

    public int hashCode() {
        return (int) name;
    }

    public boolean equals(Object o) {
        if (o instanceof Board3x3 b) {
            return b.name == this.name;
        }
        return false;
    }

    public String toString() {
        StringBuilder ret = new StringBuilder("\n");
        for (int yCoord = 2; yCoord >= 0; yCoord--) {
            for (int xCoord = 0; xCoord <= 2; xCoord++)
                ret.append(((name >> (4 * (3 * yCoord + xCoord))) % 16)).append(" ");
            ret.append("\n");
        }
        return ret.toString() + finalSuccessRate + "\n";
    }


    static {
        init();
    }

    static int[][] rotationsToCheck;
    static int[][] merges;

    public static void init() {
        rotationsToCheck = new int[19683][];
        for (int x = 0; x < 19683; x++) {
            long hi = 0;
            for (int y = 0; y < 9; y++) {
                int valHere = (int) ((x / Math.pow(3, y)) % 3);
                if (valHere == 1)
                    hi |= 0b0001L << (4 * y);
                else if (valHere == 2)
                    hi |= 0b0111L << (4 * y);
            }
            long minVal = Long.MAX_VALUE;
            ArrayList<Integer> mins = new ArrayList<>();
            for (int y = 0; y < 8; y++) {
                long val = getTernarySymmetry(getRotation(hi, y));
                if (val == minVal)
                    mins.add(y);
                if (val < minVal) {
                    minVal = val;
                    mins.clear();
                    mins.add(y);
                }
            }
            int[] adding = new int[mins.size()];
            for (int z = 0; z < mins.size(); z++)
                adding[z] = mins.get(z);
            rotationsToCheck[x] = adding;
        }
        merges = new int[4096][];
        for (int x = 0; x < 16; x++)
            for (int y = 0; y < 16; y++)
                for (int z = 0; z < 16; z++)
                    merges[z + 16 * y + 256 * x] = mergeDown(new int[]{x, y, z});
    }

    static public int[] mergeDown(int[] tiles) // Merges the array and returns it. DOES MODIFY THE ARRAY!
    {
        mergeDownOnce(tiles);
        for (int x = tiles.length - 1; x > 0; x--)
            if (tiles[x] != 0)
                if (tiles[x] == tiles[x - 1]) {
                    tiles[x - 1] = 0;
                    tiles[x]++;
                }
        mergeDownOnce(tiles);
        return tiles;
    }

    private static void mergeDownOnce(int[] tiles) {
        for (int z = 0; z < tiles.length; z++)
            for (int x = tiles.length - 1; x > 0; x--)
                if (tiles[x] == 0) {
                    tiles[x] = tiles[x - 1];
                    tiles[x - 1] = 0;
                }
    }

    public static int getTernarySymmetry(long board) {
        int ret = 0;
        int mult = 1;
        for (int x = 0; x < 9; x++) {
            long l = (board & (0b1111L << (4 * x))) >> (4 * x);
            if (l != 0) {
                if (l <= 2)
                    ret += mult;
                else
                    ret += 2 * mult;
            }
            mult *= 3;
        }
        return ret;
    }

    public static long getRotation(long name, int rot) {
        return switch (rot) {
            case 0 -> name;
            case 1 -> (((name & (0b1111L << 24)) >> 24) | ((name & (0b1111L << 12)) >> 8) | ((name & (0b1111L)) << 8) |
                    ((name & (0b1111L << 28)) >> 16) | ((name & (0b1111L << 16))) | ((name & (0b1111L << 4)) << 16) |
                    ((name & (0b1111L << 32)) >> 8) | ((name & (0b1111L << 20)) << 8) | ((name & (0b1111L << 8)) << 24));

            case 2 ->
                    (((name & (0b1111L << 32)) >> 32) | ((name & (0b1111L << 28)) >> 24) | ((name & (0b1111L) << 24) >> 16) |
                            ((name & (0b1111L << 20)) >> 8) | ((name & (0b1111L << 16))) | ((name & (0b1111L << 12)) << 8) |
                            ((name & (0b1111L << 8)) << 16) | ((name & (0b1111L << 4)) << 24) | ((name & (0b1111L)) << 32));

            case 3 ->
                    (((name & (0b1111L << 8)) >> 8) | ((name & (0b1111L << 20)) >> 16) | ((name & (0b1111L << 32)) >> 24) |
                            ((name & (0b1111L << 4)) << 8) | ((name & (0b1111L << 16))) | ((name & (0b1111L << 28)) >> 8) |
                            ((name & (0b1111L)) << 24) | ((name & (0b1111L << 12)) << 16) | ((name & (0b1111L << 24)) << 8));

            case 4 -> (((name & (0b111100000000000011110000000000001111L))) | ((name & (0b1111L << 8)) << 16)
                    | ((name & (0b1111L) << 24) >> 16)
                    | ((name & (0b111100000000000011110000L)) << 8) | ((name & (0b11110000000000001111L << 12)) >> 8));

            case 5 -> (((name & (0b1111000011110000111100000000L))) | ((name & (0b1111L)) << 32)
                    | ((name & (0b1111L) << 32) >> 32)
                    | ((name & (0b1111000011110000L)) << 16) | ((name & (0b111100001111L << 20)) >> 16));

            case 6 ->
                    (((name & (0b11110000000011110000000011110000L))) | ((name & (0b1111000000001111000000001111L)) << 8)
                            | ((name & (0b111100000000111100000000111100000000L)) >> 8));

            case 7 -> (((name & (0b111111111111L) << 12)) | ((name & (0b111111111111L)) << 24)
                    | ((name & (0b111111111111L) << 24) >> 24));

            default -> 0L;
        };
    }

    public long getRotation(int rot) {
        return getRotation(name, rot);
    }

    public int[] getTiles() {
        int[] tiles = new int[9];
        for (int x = 0; x < 9; x++)
            tiles[x] = (int) ((name & (0b1111L << (4 * x))) >> (4 * x));
        return tiles;
    }


    static public Move getMove2(Board3x3 thisMove) {
        try {
            int sum = thisMove.getSum();
            double[] rates = new double[4];
            for (int mv = 0; mv < 4; mv++) {

                Map<Long, Integer> children = thisMove.getChildren(mv, 1);

                int zeroCt = new Board3x3(thisMove.merge(mv)).getZeroCt();
                for (Map.Entry<Long, Integer> entry : children.entrySet())
                    try {
                        double delta = 0.9 / zeroCt * entry.getValue() * (binSearch(files[sum + 1], Board3x3.getSmallestSymmetry(entry.getKey())));
                        rates[mv] += delta;
                    } catch (Exception _) {
                    }
                children = thisMove.getChildren(mv, 2);
                zeroCt = new Board3x3(thisMove.merge(mv)).getZeroCt();
                for (Map.Entry<Long, Integer> entry : children.entrySet())
                    try {
                        double delta = 0.1 / zeroCt * entry.getValue() * (binSearch(files[sum + 2], Board3x3.getSmallestSymmetry(entry.getKey())));
                        rates[mv] += delta;
                    } catch (Exception _) {
                    }
            }
            Move ret = new Move(namer(thisMove.getTiles()), rates);
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return null;
        }
    }
    public static String namer(int[] space)
    {
        StringBuilder ret = new StringBuilder();
        for (int tn:space)
        {
            if (tn < 10)
                ret.append(tn);
            else
                ret.append(":");
        }
        return ret.toString();
    }
    public static double binSearch(RandomAccessFile file, long l) throws IOException {
//		System.out.println("Searching for " + Input.namer(new Board3x3(new Board3x3(l).getSmallestSymmetry()).getTiles()) + " " + l);
        if (file == null)
            return 0.0;
        long left = 0;
        long right = file.length() / 16 - 1;
        while (left <= right) {
            long mid = left + (right - left) / 2;
            file.seek(mid * 16);
            long target = file.readLong();
            if (target == l)
                return file.readDouble();
            if (target < l)
                left = mid + 1;
            else
                right = mid - 1;
        }
        return 0.0;
    }
    static RandomAccessFile[] files = new RandomAccessFile[1025];
    static {
        File child = new File("C:\\Users\\diepl\\OneDrive\\Desktop\\Trainer\\Tables\\Tables1792");
        for (File f : child.listFiles()) {
            int layer = Integer.parseInt(f.getName().split("\\.")[0]);
            try {
                files[layer] = new RandomAccessFile(f, "r");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}


class Move
{
    String name = "";
    double rScore = 0;
    double lScore = 0;
    double uScore = 0;
    double dScore = 0;
    double bScore = 0;
    boolean exists = false;
    String line = "";
    public Move(String board, double[] successRates) {
        exists = true;
        name = board;
        dScore = successRates[0];
        lScore = successRates[1];
        uScore = successRates[2];
        rScore = successRates[3];
        bScore = Math.max(Math.max(dScore, uScore), Math.max(lScore, rScore));
    }
    public Move(String line)
    {
        this.line = line;
        if (!(line.equals("0")))
        {
            exists = true;
            Scanner lineIn = new Scanner(line);
            name = lineIn.next();
            bScore = lineIn.nextDouble();
            while (lineIn.hasNext())
            {
                String move = lineIn.next();
                double mv = lineIn.nextDouble();
                switch (move) {
                    case "L:" -> lScore = mv;
                    case "U:" -> uScore = mv;
                    case "D:" -> dScore = mv;
                    case "R:" -> rScore = mv;
                }
            }
        }
    }
    boolean exists()
    {
        return exists;
    }
    String getName()
    {
        return name;
    }
    double getScore(int move)
    {
        if (move == 1)
            return dScore;
        if (move == 2)
            return lScore;
        if (move == 3)
            return rScore;
        if (move == 0)
            return uScore;
        return 0;
    }
    double getBestScore()
    {
        return bScore;
    }

    String getBestMove()
    {
        double gScore = lScore;
        String ret = "";
        if (uScore > lScore)
            gScore = uScore;
        if (rScore > gScore)
            gScore = rScore;
        if (dScore > gScore)
            gScore = dScore;
        if (lScore == gScore)
            ret+="Left ";
        if (rScore == gScore)
            ret+="Right ";
        if (uScore == gScore)
            ret+="Up ";
        if (dScore == gScore)
            ret+="Down ";
        ret = ret.trim();
        while (ret.contains(" "))
            ret = ret.substring(0,ret.indexOf(" "))+", "+ret.substring(ret.indexOf(" "));
        return ret;
    }
    public String toString()
    {
        return bScore+" "+lScore+" "+uScore+" "+rScore+" "+dScore+"       "+line;
    }
}
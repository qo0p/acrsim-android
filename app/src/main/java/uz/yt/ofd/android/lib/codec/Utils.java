package uz.yt.ofd.android.lib.codec;

public class Utils {

    public static byte[] append(byte[] a, byte... b) {
        byte[] tmp = new byte[a.length + b.length];
        System.arraycopy(a, 0, tmp, 0, a.length);
        System.arraycopy(b, 0, tmp, a.length, b.length);
        return tmp;
    }

    public static String[] append(String[] a, String... b) {
        String[] tmp = new String[a.length + b.length];
        System.arraycopy(a, 0, tmp, 0, a.length);
        System.arraycopy(b, 0, tmp, a.length, b.length);
        return tmp;
    }

    public static byte[] copy(byte[] src) {
        byte[] cp = new byte[src.length];
        System.arraycopy(src, 0, cp, 0, src.length);
        return cp;
    }

    public static byte[] slice(byte[] a, int from, int to) {
        byte[] sl = new byte[to - from];
        System.arraycopy(a, from, sl, 0, sl.length);
        return sl;
    }

    public static short readShort(byte[] data, int ofs) {
        return (short) (data[ofs + 1] & 0xff | (data[ofs] & 0xff) << 8);
    }

    public static byte[] short2bytes(short data) {
        return new byte[]{(byte) ((data & 0xFF00) >> 8), (byte) (data & 0x00FF)};
    }

    public static String trim(String s, int maxLen) {
        if (s.length() > maxLen) {
            return s.substring(0, maxLen) + "...";
        }
        return s;
    }
}

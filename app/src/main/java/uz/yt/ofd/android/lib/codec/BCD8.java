package uz.yt.ofd.android.lib.codec;

import java.io.IOException;
import java.io.OutputStream;

public class BCD8 {
    final static public short SIZE = (short) 8;

    byte[] bytes;

    public byte[] getBytes() {
        return bytes;
    }

    public byte[] getBytesCompact() {
        int i = bytes.length - 1;
        while (i > 0) {
            if (bytes[i] != 0) {
                break;
            }
            i--;
        }
        byte[] c = new byte[i + 1];
        System.arraycopy(bytes, 0, c, 0, c.length);
        return c;
    }

    public void write(OutputStream os) throws IOException {
        os.write(bytes);
    }

    public static boolean isBCD(byte[] buf, short ofs, short len) {
        for (short i = ofs; i < ofs + len; i++) {
            byte n0 = (byte) (((short) (buf[i] & 0xFF) >> 4) & 0x0F);
            byte n1 = (byte) ((short) (buf[i] & 0x0F));
            if (n0 < 0 || n0 > 9 || n1 < 0 || n1 > 9) {
                return false;
            }
        }
        return true;
    }

    public static BCD8 fromLong(long l) {
        byte[] b = new byte[SIZE];
        short i = 0;
        while (l > 0) {
            byte r = (byte) (l % 10);
            if (i % 2 == 0) {
                b[i / 2] = (byte) (r << 4);
            } else {
                b[i / 2] += r;
            }
            i++;
            l /= 10;
        }
        return new BCD8(b);
    }

    public static BCD8 fromBytes(byte[] b, short ofs) {
        if (!isValid(b, ofs)) {
            return null;
        }
        BCD8 r = new BCD8();
        System.arraycopy(b, ofs, r.bytes, (short) 0, SIZE);
        return r;
    }

    public BCD8() {
        this.bytes = new byte[SIZE];
    }

    public BCD8(byte[] number) {
        this.bytes = number;
    }

    public BCD8(byte[] b, short ofs, short len) {
        if (len > SIZE) {
            throw new IllegalArgumentException("bcd number is longer than " + SIZE + " bytes");
        }
        this.bytes = new byte[SIZE];
        System.arraycopy(b, ofs, this.bytes, SIZE - len, len);
        if (!isValid()) {
            throw new IllegalArgumentException("bad bcd number");
        }
    }


    public long toLong() {
        long s = 0;
        for (int i = 0; i < SIZE; i++) {
            s += (long) Math.pow(100, i) * (10 * (bytes[i] & 0x0F) + (((short) (bytes[i] & 0xFF) >> 4) & 0x0F));
        }
        return s;
    }

    public BCD8 copy() {
        BCD8 c = new BCD8(
                bytes == null ? null : new byte[SIZE]);
        if (c.bytes != null) {
            System.arraycopy(bytes, (short) 0, c.bytes, (short) 0, SIZE);
        }
        return c;
    }

    public boolean add(BCD8 n, byte[] temp, short tempOfs) {
        System.arraycopy(bytes, (short) 0, temp, tempOfs, SIZE);
        System.arraycopy(n.bytes, (short) 0, temp, (short) (tempOfs + 8), SIZE);

        short ofsSum = (short) (tempOfs + 0);
        byte[] sum = temp;
        short ofsA = (short) (tempOfs + 8);
        byte[] a = temp;

        short add = 0;
        for (short si = ofsSum, sa = ofsA; si < (short) (ofsSum + 8); si++, sa++) {
            byte s0 = (byte) ((sum[si] >> 4) & 0x0F);
            byte s1 = (byte) ((sum[si]) & 0x0F);
            byte a0 = (byte) ((a[sa] >> 4) & 0x0F);
            byte a1 = (byte) ((a[sa]) & 0x0F);
            byte r0 = 0;
            byte r1 = 0;
            byte v = 0;
            if (s0 < (byte) 0 || s0 > (byte) 9 || s1 < (byte) 0 || s1 > (byte) 9) {
                return false;
            }
            if (a0 < (byte) 0 || a0 > (byte) 9 || a1 < (byte) 0 || a1 > (byte) 9) {
                return false;
            }
            v = (byte) (s0 + a0 + add);
            r0 = (byte) (v % 10);
            add = (byte) (v / 10);

            v = (byte) (s1 + a1 + add);
            r1 = (byte) (v % 10);
            add = (byte) (v / 10);

            sum[si] = (byte) ((r0 << 4) + r1);
        }
        boolean overflow = !(add > 0);
        if (overflow) {
            return overflow;
        }

        System.arraycopy(sum, ofsSum, bytes, (short) 0, SIZE);
        return overflow;
    }

    public short compare(BCD8 n) {
        short maxLen = SIZE;
        maxLen -= 1;
        for (short i = maxLen; i >= 0; i--) {
            byte a0 = (byte) (((short) (bytes[i] & 0xFF) >> 4) & 0x0F);
            byte a1 = (byte) ((short) (bytes[i] & 0x0F));
            byte b0 = (byte) (((short) (n.bytes[i] & 0xFF) >> 4) & 0x0F);
            byte b1 = (byte) ((short) (n.bytes[i] & 0x0F));
            if (a1 > b1) {
                return (short) 1;
            }
            if (a1 < b1) {
                return (short) -1;
            }
            if (a0 > b0) {
                return (short) 1;
            }
            if (a0 < b0) {
                return (short) -1;
            }
        }
        return (short) 0;
    }

    public boolean isEqual(BCD8 n) {
        return compare(n) == (short) 0;
    }

    public boolean isValid() {
        return isValid(bytes, (short) 0);
    }

    private static boolean isValid(byte[] buf, short ofs) {
        if (buf == null) {
            return false;
        }
        if ((short) (buf.length - ofs) < SIZE) {
            return false;
        }
        for (short i = 0; i < SIZE; i++) {
            byte n0 = (byte) (((short) (buf[(short) (i + ofs)] & 0xFF) >> 4) & 0x0F);
            byte n1 = (byte) ((short) (buf[(short) (i + ofs)] & 0x0F));
            if (n0 < 0 || n0 > 9 || n1 < 0 || n1 > 9) {
                return false;
            }
        }
        return true;
    }

    public boolean inc(byte[] temp, short tempOfs) {
        System.arraycopy(bytes, (short) 0, temp, tempOfs, SIZE);

        short ofsSum = (short) (tempOfs + 0);
        byte[] sum = temp;

        short add = 1;
        for (short i = ofsSum; i < (short) (ofsSum + 8); i++) {
            byte n0 = (byte) ((sum[i] >> 4) & 0x0F);
            byte n1 = (byte) ((sum[i]) & 0x0F);
            byte m0 = 0;
            byte m1 = 0;
            byte v = 0;
            if (n0 < 0 || n0 > 9 || n1 < 0 || n1 > 9) {
                return false;
            }
            v = (byte) (n0 + add);
            m0 = (byte) (v % 10);
            add = (byte) (v / 10);

            v = (byte) (n1 + add);
            m1 = (byte) (v % 10);
            add = (byte) (v / 10);

            sum[i] = (byte) ((m0 << 4) + m1);
        }
        boolean overflow = !(add > 0);
        if (overflow) {
            return overflow;
        }

        System.arraycopy(sum, ofsSum, bytes, (short) 0, SIZE);
        return overflow;
    }

    public boolean isZero() {
        for (short i = 0; i < SIZE; i++) {
            if (bytes[i] != 0) {
                return false;
            }
        }
        return true;
    }
}

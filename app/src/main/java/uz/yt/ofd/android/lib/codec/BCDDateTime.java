package uz.yt.ofd.android.lib.codec;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;

public class BCDDateTime {

    final static public short SIZE = (short) 8;

    public static Date fromBytes(byte[] b, short ofs) {

        if (b[ofs + 4] != 'T') {
            return null;
        }
        if (!isBCD(b, (short) (ofs + 0), (short) 8)) {
            return null;
        }
        byte[] yr = new byte[2];
        byte[] mn = new byte[1];
        byte[] dy = new byte[1];
        byte[] ho = new byte[1];
        byte[] mi = new byte[1];
        byte[] se = new byte[1];

        yr[0] = (byte) (((b[ofs + 1] & 0xF) << 4) | ((short) (b[ofs + 1] & 0xFF) >> 4));
        yr[1] = (byte) (((b[ofs + 0] & 0xF) << 4) | ((short) (b[ofs + 0] & 0xFF) >> 4));
        mn[0] = (byte) (((b[ofs + 2] & 0xF) << 4) | ((short) (b[ofs + 2] & 0xFF) >> 4));
        dy[0] = (byte) (((b[ofs + 3] & 0xF) << 4) | ((short) (b[ofs + 3] & 0xFF) >> 4));
        ho[0] = (byte) (((b[ofs + 5] & 0xF) << 4) | ((short) (b[ofs + 5] & 0xFF) >> 4));
        mi[0] = (byte) (((b[ofs + 6] & 0xF) << 4) | ((short) (b[ofs + 6] & 0xFF) >> 4));
        se[0] = (byte) (((b[ofs + 7] & 0xF) << 4) | ((short) (b[ofs + 7] & 0xFF) >> 4));

        short year = toShort(yr, (short) 0, (short) yr.length);
        if (year > 9999 || year < 1999) {
            return null;
        }
        short month = toShort(mn, (short) 0, (short) mn.length);
        if (month > 12 || month < 1) {
            return null;
        }
        short day = toShort(dy, (short) 0, (short) dy.length);
        if (day > 31 || day < 1) {
            return null;
        }
        short hour = toShort(ho, (short) 0, (short) ho.length);
        if (hour > 23 || hour < 0) {
            return null;
        }
        short min = toShort(mi, (short) 0, (short) mi.length);
        if (min > 59 || min < 0) {
            return null;
        }
        short sec = toShort(se, (short) 0, (short) se.length);
        if (sec > 60 || sec < 0) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, (int) year);
        cal.set(Calendar.MONTH, (int) month - 1);
        cal.set(Calendar.DATE, (int) day);
        cal.set(Calendar.HOUR_OF_DAY, (int) hour);
        cal.set(Calendar.MINUTE, (int) min);
        cal.set(Calendar.SECOND, (int) sec);
        return cal.getTime();
    }

    public static byte[] toBytes(Date time) {
        byte[] t = new byte[8];
        if (time == null) {
            return t;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        byte[] yr = BCD8.fromLong(cal.get(Calendar.YEAR)).getBytes();
        byte[] mn = BCD8.fromLong(cal.get(Calendar.MONTH) + 1l).getBytes();
        byte[] dy = BCD8.fromLong(cal.get(Calendar.DATE)).getBytes();
        byte[] ho = BCD8.fromLong(cal.get(Calendar.HOUR_OF_DAY)).getBytes();
        byte[] mi = BCD8.fromLong(cal.get(Calendar.MINUTE)).getBytes();
        byte[] se = BCD8.fromLong(cal.get(Calendar.SECOND)).getBytes();
        t[0] = (byte) (((yr[1] & 0xF) << 4) | ((short) (yr[1] & 0xFF) >> 4));
        t[1] = (byte) (((yr[0] & 0xF) << 4) | ((short) (yr[0] & 0xFF) >> 4));
        t[2] = (byte) (((mn[0] & 0xF) << 4) | ((short) (mn[0] & 0xFF) >> 4));
        t[3] = (byte) (((dy[0] & 0xF) << 4) | ((short) (dy[0] & 0xFF) >> 4));
        t[4] = 'T';
        t[5] = (byte) (((ho[0] & 0xF) << 4) | ((short) (ho[0] & 0xFF) >> 4));
        t[6] = (byte) (((mi[0] & 0xF) << 4) | ((short) (mi[0] & 0xFF) >> 4));
        t[7] = (byte) (((se[0] & 0xF) << 4) | ((short) (se[0] & 0xFF) >> 4));
        return t;
    }

    public void write(Date time, OutputStream os) throws IOException {
        os.write(toBytes(time));
    }

    private static short pow100(short n) {
        short p = (short) 1;
        for (short k = (short) 0; k < n; k++) {
            p *= 100;
        }
        return p;
    }

    public static short toShort(byte[] b, short ofs, short len) {
        short s = (short) 0;
        for (short i = (short) 0; i < len; i++) {
            s += pow100(i) * (10 * (b[(short) (i + ofs)] & 0x0F) + (((short) (b[(short) (i + ofs)] & 0xFF) >> 4) & 0x0F));
        }
        return s;
    }

    private static boolean isBCD(byte[] buf, short ofs, short len) {
        for (short i = ofs; i < (short) (ofs + len); i++) {
            byte n0 = (byte) (((short) (buf[i] & 0xFF) >> 4) & 0x0F);
            byte n1 = (byte) ((short) (buf[i] & 0x0F));
            if (n0 < 0 || n0 > 9 || n1 < 0 || n1 > 9) {
                return false;
            }
        }
        return true;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.codec.tlv;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import uz.yt.ofd.android.lib.codec.Utils;
import uz.yt.ofd.android.lib.codec.tlv.exception.TLVParseException;

public class TLV {

    private static final byte TLV_DATA_BIT = (byte) 0x80;
    private static final byte READ_NEXT_SIZE_BIT = (byte) 0x80;
    private static final int SIZE_LEN_LIMIT = 3;

    private static final byte TagPadding = (byte) 0x00;

    public static class Header {

        byte tag;
        int size;

        public byte getTag() {
            return tag;
        }

        public int getSize() {
            return size;
        }

    }

    public static byte[] readSizeBytes(InputStream r) throws IOException {
        byte[] buf = new byte[1];
        int sizLen = 1;
        byte[] sizes = new byte[sizLen];

        while (sizLen <= SIZE_LEN_LIMIT) {
            if (r.read(buf) == -1) {
                throw new EOFException();
            }

            byte szByte = buf[0];
            if ((szByte & READ_NEXT_SIZE_BIT) == READ_NEXT_SIZE_BIT) {
                sizes[sizLen - 1] = (byte) (szByte & ~READ_NEXT_SIZE_BIT);
                sizLen++;
                sizes = Utils.append(sizes, (byte) 0);
            } else {
                sizes[sizLen - 1] = szByte;
                break;
            }
        }
        return sizes;
    }

    public static Header readHeader(InputStream r, OutputStream w) throws TLVParseException, IOException {
        Header h = new Header();
        int ofs = 0;

        byte[] buf = new byte[1];
        if (r.read(buf) == -1) {
            throw new EOFException();
        }
        ofs++;
        h.tag = buf[0];
        w.write(buf);

        byte[] sizes = readSizeBytes(r);
        int sizLen = sizes.length;
        ofs += sizLen;
        w.write(sizes);

        if (sizLen > SIZE_LEN_LIMIT) {
            throw new TLVParseException(String.format("illegal size value length %d at offset %d", sizLen, ofs - sizLen));
        }

        h.size = bytesSize(sizes);

        return h;
    }

    public static TVS read(InputStream r) throws TLVParseException, IOException {
        ByteArrayOutputStream tlvRaw = new ByteArrayOutputStream();
        Header h = readHeader(r, tlvRaw);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        while (true) {
            int left = h.size - buf.size();
            left = Math.min(left, 1024);
            if (left == 0) {
                break;
            }
            byte[] tmp = new byte[left];
            int rd = r.read(tmp);
            if (rd == -1) {
                throw new EOFException();
            }
            buf.write(tmp, 0, rd);
        }
        tlvRaw.write(buf.toByteArray());
        return decode(tlvRaw.toByteArray());
    }

    public static TVS decode(byte[] buf) throws TLVParseException {
        TVS tvs = new TVS();
        int[] offset = new int[1];

        TV tv;
        while (true) {
            tv = readTLV(buf, offset, 0, 0);
            if (tv == null) {
                break;
            }
            tvs.add(tv);
        }
        return tvs;
    }

    private static TV readTLV(byte[] buf, int[] offset, int level, int globalOfs) throws TLVParseException {
        int ofs = offset[0];
        if (ofs + 1 > buf.length) {
            return null;
        }
        int from = globalOfs + ofs;
        byte tag = buf[ofs];
        ofs++;
        if (tag == TagPadding) {
            offset[0] = ofs;
            return null;
        }
        int sizLen = 1;
        byte[] sizes = new byte[sizLen];

        while (sizLen <= SIZE_LEN_LIMIT) {
            byte szByte = buf[ofs];
            if ((szByte & READ_NEXT_SIZE_BIT) == READ_NEXT_SIZE_BIT) {
                sizes[sizLen - 1] = (byte) (szByte & ~READ_NEXT_SIZE_BIT);
                sizLen++;
                sizes = Utils.append(sizes, (byte) 0);
                ofs++;
            } else {
                sizes[sizLen - 1] = szByte;
                break;
            }
        }
        if (sizLen > SIZE_LEN_LIMIT) {
            throw new TLVParseException(String.format("illegal size value length %d at offset %d", sizLen, ofs - sizLen));
        }

        int sz = 0;
        for (int i = 0; i < sizes.length; i++) {
            sz += Math.pow(READ_NEXT_SIZE_BIT & 0xFF, i) * (sizes[i] & 0xFF);
        }

        if (ofs + sz > buf.length) {
            throw new TLVParseException(String.format("buf offset %d is out of buf length %d on level %d", ofs + sz, buf.length, level));
        }
        ofs++;
        offset[0] = ofs + sz;
        TVS tvs = new TVS();
        int[] cofs = new int[1];
        if ((tag & TLV_DATA_BIT) == TLV_DATA_BIT) {
            TV tv0;
            while (true) {
                tv0 = readTLV(Utils.slice(buf, ofs, ofs + sz), cofs, level + 1, globalOfs + ofs);
                if (tv0 == null) {
                    break;
                }
                tvs.add(tv0);
            }
        }
        int to = globalOfs + ofs + sz;
        return new TV(tag, Utils.slice(buf, ofs, ofs + sz), tvs, from, to);
    }

    public static byte[] sizeBytes(int size) throws IllegalArgumentException {
        if (size < 0) {
            throw new IllegalArgumentException(String.format("illegal size value %d", size));
        }
        byte[] sizes = new byte[0];
        int sz = size;
        while (sz > 0) {
            int hsz = sz / (READ_NEXT_SIZE_BIT & 0xFF);
            byte lsz = (byte) (sz % (READ_NEXT_SIZE_BIT & 0xFF));
            if (hsz > 0) {
                lsz = (byte) (lsz | READ_NEXT_SIZE_BIT);
            }
            sizes = Utils.append(sizes, lsz);
            sz = hsz;
        }
        if (sizes.length > SIZE_LEN_LIMIT) {
            throw new IllegalArgumentException(String.format("illegal size value %d", size));
        }
        if (sizes.length == 0) {
            sizes = new byte[1];
        }
        return sizes;
    }

    public static int bytesSize(byte[] sizes) {
        int size = 0;
        for (int i = 0; i < sizes.length; i++) {
            size += Math.pow(READ_NEXT_SIZE_BIT & 0xFF, i) * (sizes[i] & 0xFF);
        }
        return size;
    }

    public static byte[] encode(byte tag, byte[] value) {
        byte[] sizes = sizeBytes(value.length);
        byte[] buf = new byte[1 + sizes.length + value.length];
        buf[0] = tag;
        System.arraycopy(sizes, 0, buf, 1, sizes.length);
        System.arraycopy(value, 0, buf, 1 + sizes.length, value.length);
        return buf;
    }

    public static byte[] encodeVector(byte tag, byte[]... values) {
        byte[] finval = new byte[0];
        for (byte[] value : values) {
            finval = Utils.append(finval, value);
        }
        return encode(tag, finval);
    }

}

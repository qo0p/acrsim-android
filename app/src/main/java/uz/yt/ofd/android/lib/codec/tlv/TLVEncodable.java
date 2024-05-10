/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.codec.tlv;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import uz.yt.ofd.android.lib.codec.BCD8;
import uz.yt.ofd.android.lib.codec.BCDDateTime;
import uz.yt.ofd.android.lib.codec.Utils;

public abstract class TLVEncodable {

    public abstract void write(OutputStream w) throws IOException;

    public byte[] encode() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        write(baos);
        return baos.toByteArray();
    }

    public static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static void writeString(byte tag, String value, int maxSize, OutputStream w) throws IOException {
        if (!isEmpty(value)) {
            if (value.length() > maxSize) {
                value = value.substring(0, maxSize);
            }
            w.write(TLV.encode(tag, value.getBytes()));
        }
    }

    public static void writeBytes(byte tag, byte[] value, int maxSize, OutputStream w) throws IOException {
        if (value != null && value.length > 0) {
            byte[] tv = value;
            if (tv.length > maxSize) {
                tv = Utils.slice(tv, 0, maxSize - 1);
            }
            w.write(TLV.encode(tag, tv));
        }
    }

    public static void writeBytes(byte tag, byte[] value, OutputStream w) throws IOException {
        if (value != null && value.length > 0) {
            w.write(TLV.encode(tag, value));
        }
    }

    public static void writeLong(byte tag, Long value, OutputStream w) throws IOException {
        if (value != null) {
            w.write(TLV.encode(tag, BCD8.fromLong(value).getBytesCompact()));
        }
    }

    public static void writeShort(byte tag, Short value, OutputStream w) throws IOException {
        if (value != null) {
            w.write(TLV.encode(tag, Utils.short2bytes(value)));
        }
    }

    public static void writeByte(byte tag, Byte value, OutputStream w) throws IOException {
        if (value != null) {
            w.write(TLV.encode(tag, new byte[]{value}));
        }
    }

    public static void writeDate(byte tag, Date value, OutputStream w) throws IOException {
        if (value != null) {
            w.write(TLV.encode(tag, BCDDateTime.toBytes(value)));
        }
    }

    public static void writeTlvEncodable(byte tag, TLVEncodable value, OutputStream w) throws IOException {
        if (value != null) {
            w.write(TLV.encode(tag, value.encode()));
        }
    }

}

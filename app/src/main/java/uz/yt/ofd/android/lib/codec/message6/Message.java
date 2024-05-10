/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.codec.message6;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import uz.yt.ofd.android.lib.codec.Crc32c;
import uz.yt.ofd.android.lib.codec.Crc32cInputStream;
import uz.yt.ofd.android.lib.codec.HexBin;
import uz.yt.ofd.android.lib.codec.tlv.TLV;
import uz.yt.ofd.android.lib.codec.tlv.exception.TLVParseException;

/**
 * Сообщение для отправки/приема
 *
 * @author administrator
 */
public class Message {

    public static final byte TAG_REQUEST = (byte) 0x8a;
    public static final byte TAG_RESPONSE = (byte) 0x8b;

    private byte version;
    private byte tlvTag;
    private int tlvSize;
    private byte[] tlvBody;
    private byte[] crc32;

    private Message() {
    }

    public Message(byte version, byte tlvTag, byte[] tlvBody) {
        this.version = version;
        this.tlvTag = tlvTag;
        this.tlvBody = tlvBody;
    }

    /**
     * Закодировать в TLV-структуру
     *
     * @param w поток записи
     * @throws Exception ошибка при кодировании
     */
    public void write(OutputStream w) throws IOException {
        Crc32c crcer = new Crc32c();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        baos.write(version);
        tlvSize = tlvBody.length;
        baos.write(TLV.encode(tlvTag, tlvBody));

        crcer.update(baos.toByteArray());
        crc32 = int2bytes(crcer.getValue());
        baos.write(crc32);
        w.write(baos.toByteArray());
        w.flush();
    }

    /**
     * Декодировать сообщения
     *
     * @param r поток ввода
     * @return сообщение
     * @throws IOException       ошибка ввода/вывода
     * @throws TLVParseException ошибка декодирования TLV-структуры
     */
    public static Message read(InputStream r) throws IOException, TLVParseException {
        Crc32cInputStream cis = new Crc32cInputStream(r);
        Message m = new Message();
        byte[] tmp = new byte[1];
        fillBufferCompletely(cis, tmp);
        m.version = tmp[0];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        TLV.Header h = TLV.readHeader(cis, baos);
        m.tlvTag = h.getTag();
        m.tlvSize = h.getSize();
        m.tlvBody = new byte[m.tlvSize];
        fillBufferCompletely(cis, m.tlvBody);
        byte[] realcrc32 = int2bytes(cis.getValue());
        m.crc32 = new byte[4];
        fillBufferCompletely(cis, m.crc32);
        if (!Arrays.equals(m.crc32, realcrc32)) {
            throw new IllegalArgumentException(String.format("expected crc32 is %s, but read %s", HexBin.encode(realcrc32), HexBin.encode(m.crc32)));
        }
        return m;
    }

    protected static int fillBufferCompletely(InputStream is, byte[] bytes) throws IOException {
        int size = bytes.length;
        int offset = 0;
        while (offset < size) {
            int read = is.read(bytes, offset, size - offset);
            if (read == -1) {
                if (offset == 0) {
                    throw new EOFException();
                } else {
                    return offset;
                }
            } else {
                offset += read;
            }
        }

        return size;
    }

    public byte getVersion() {
        return version;
    }

    public byte getTlvTag() {
        return tlvTag;
    }

    public byte[] getTlvBody() {
        return tlvBody;
    }

    public byte[] getCrc32() {
        return crc32;
    }

    private static byte[] int2bytes(long i) {
        return new byte[]{
                (byte) (i >> 24),
                (byte) (i >> 16),
                (byte) (i >> 8),
                (byte) i
        };
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.codec.message6;

import java.io.IOException;
import java.io.OutputStream;

import uz.yt.ofd.android.lib.codec.TlvTagDescriptions;
import uz.yt.ofd.android.lib.codec.tlv.SingleTagReader;
import uz.yt.ofd.android.lib.codec.tlv.TLVEncodable;
import uz.yt.ofd.android.lib.codec.tlv.TV;
import uz.yt.ofd.android.lib.codec.tlv.TVS;

/**
 * Файл для отправки
 *
 * @author administrator
 */
public class File extends TLVEncodable {

    public static final byte TAG_TYPE = (byte) 0x01;
    public static final byte TAG_VERSION = (byte) 0x02;
    public static final byte TAG_HEADER = (byte) 0x03;
    public static final byte TAG_BODY = (byte) 0x04;

    public static void buildTlvTagDescriptions(TlvTagDescriptions parentTlvTagDescriptions, TlvTagDescriptions.OID oid) {
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_TYPE, "Type"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_VERSION, "Version"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_HEADER, "Header"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_BODY, "Body"));
    }

    private byte type;
    private byte version;
    private byte[] header;
    private byte[] body;

    public File() {
    }

    public File(byte type, byte version, byte[] header, byte[] body) {
        this.type = type;
        this.version = version;
        this.header = header;
        this.body = body;
    }

    public static File decode(TVS tvs) throws Exception {
        File o = new File();
        SingleTagReader str = new SingleTagReader();
        for (TV tv : tvs) {
            if (tv.getTag() == TAG_TYPE) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == 1) {
                            o.type = tv.getValue()[0];
                        } else {
                            throw new IllegalArgumentException(String.format("type must be 1 byte"));
                        }
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_VERSION) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == 1) {
                            o.version = tv.getValue()[0];
                        } else {
                            throw new IllegalArgumentException(String.format("version must be 1 byte"));
                        }
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_HEADER) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        o.header = tv.getValue();
                        return true;
                    }

                });
            }
            if (tv.getTag() == TAG_BODY) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        o.body = tv.getValue();
                        return true;
                    }

                });
            }
        }
        return o;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public byte[] getHeader() {
        return header;
    }

    public void setHeader(byte[] header) {
        this.header = header;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    /**
     * Закодировать в TLV-структуру
     *
     * @param w поток записи
     * @throws Exception ошибка при кодировании
     */
    @Override
    public void write(OutputStream w) throws IOException {
        writeByte(TAG_TYPE, type, w);
        writeByte(TAG_VERSION, version, w);
        writeBytes(TAG_HEADER, header, w);
        writeBytes(TAG_BODY, body, w);
    }

}

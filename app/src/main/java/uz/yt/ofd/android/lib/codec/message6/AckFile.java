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
 * @author administrator
 */
public class AckFile extends TLVEncodable {


    public static final byte TAG_STATUS = (byte) 0x01;
    public static final byte TAG_HEADER = (byte) 0x02;
    public static final byte TAG_BODY = (byte) 0x03;
    public static final byte TAG_TAG = (byte) 0x0f;

    public static void buildTlvTagDescriptions(TlvTagDescriptions parentTlvTagDescriptions, TlvTagDescriptions.OID oid) {
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_STATUS, "Status"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_HEADER, "Header"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_BODY, "Body"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_TAG, "Tag"));
    }

    private AckFileStatus status;
    private byte[] header;
    private byte[] body;
    private byte[] tag;

    public AckFile() {
    }

    public AckFile(AckFileStatus status, byte[] header, byte[] body, byte[] tag) {
        this.status = status;
        this.header = header;
        this.body = body;
        this.tag = tag;
    }

    public AckFileStatus getStatus() {
        return status;
    }

    public void setStatus(AckFileStatus status) {
        this.status = status;
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

    public byte[] getTag() {
        return tag;
    }

    public void setTag(byte[] tag) {
        this.tag = tag;
    }

    /**
     * Закодировать в TLV-структуру
     *
     * @param w поток записи
     * @throws Exception ошибка при кодировании
     */
    @Override
    public void write(OutputStream w) throws IOException {
        writeByte(TAG_STATUS, status.value, w);
        writeBytes(TAG_HEADER, header, w);
        writeBytes(TAG_BODY, body, w);
        writeBytes(TAG_TAG, tag, 32, w);
    }

    /**
     * Декодировать из TLV-структуры
     *
     * @param tvs TLV-структура
     * @return объект после декодирования
     * @throws Exception ошибка декодирования
     */
    public static AckFile decode(TVS tvs) throws Exception {
        AckFile o = new AckFile();
        SingleTagReader str = new SingleTagReader();
        for (TV tv : tvs) {
            if (tv.getTag() == TAG_STATUS) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == 1) {
                            o.status = AckFileStatus.find(tv.getValue()[0]);
                        } else {
                            throw new IllegalArgumentException(String.format("status must be 1 byte"));
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
            if (tv.getTag() == TAG_TAG) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length > 32) {
                            throw new IllegalArgumentException(String.format("tag must be 32 bytes or less"));
                        }
                        o.tag = tv.getValue();
                        return true;
                    }

                });
            }
        }
        return o;
    }

}

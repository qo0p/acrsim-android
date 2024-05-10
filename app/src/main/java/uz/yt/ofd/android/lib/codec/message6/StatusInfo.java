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
 * Статус получения запроса
 *
 * @author administrator
 */
public class StatusInfo extends TLVEncodable {

    public static final byte TAG_STATUS_CODE = (byte) 0x01;
    public static final byte TAG_REASON_CODE = (byte) 0x02;
    public static final byte TAG_NOTICE = (byte) 0x03;

    public static void buildTlvTagDescriptions(TlvTagDescriptions parentTlvTagDescriptions, TlvTagDescriptions.OID oid) {
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_STATUS_CODE, "StatusCode"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_REASON_CODE, "ReasonCode"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_NOTICE, "Notice"));
    }

    private StatusCode statusCode;
    private byte reasonCode;
    private String notice;

    public StatusInfo() {
    }

    /**
     * @param statusCode код статуса
     * @param reasonCode код причины
     * @param notice     текст сообщения сервера
     */
    public StatusInfo(StatusCode statusCode, byte reasonCode, String notice) {
        this.statusCode = statusCode;
        this.reasonCode = reasonCode;
        this.notice = notice;
    }

    /**
     * @return код статуса
     */
    public StatusCode getStatusCode() {
        return statusCode;
    }

    /**
     * @param statusCode код статуса
     */
    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * @return код причины
     */
    public byte getReasonCode() {
        return reasonCode;
    }

    /**
     * @param reasonCode код причины
     */
    public void setReasonCode(byte reasonCode) {
        this.reasonCode = reasonCode;
    }

    /**
     * @return текст сообщения сервера
     */
    public String getNotice() {
        return notice;
    }

    /**
     * @param notice текст сообщения сервера
     */
    public void setNotice(String notice) {
        this.notice = notice;
    }

    /**
     * Закодировать в TLV-структуру
     *
     * @param w поток записи
     * @throws Exception ошибка при кодировании
     */
    @Override
    public void write(OutputStream w) throws IOException {
        writeByte(TAG_STATUS_CODE, statusCode.value, w);
        writeByte(TAG_REASON_CODE, reasonCode, w);
        writeString(TAG_NOTICE, notice, 1024, w);
    }

    /**
     * Декодировать из TLV-структуры
     *
     * @param tvs TLV-структура
     * @return объект после декодирования
     * @throws Exception ошибка декодирования
     */
    public static StatusInfo decode(TVS tvs) throws Exception {
        StatusInfo o = new StatusInfo();
        SingleTagReader str = new SingleTagReader();
        for (TV tv : tvs) {
            if (tv.getTag() == TAG_STATUS_CODE) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == 1) {
                            o.statusCode = StatusCode.find(tv.getValue()[0]);
                        } else {
                            throw new IllegalArgumentException(String.format("statusCode must be 1 byte"));
                        }
                        return true;
                    }

                });

            }
            if (tv.getTag() == TAG_REASON_CODE) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == 1) {
                            o.reasonCode = tv.getValue()[0];
                        } else {
                            throw new IllegalArgumentException(String.format("reasonCode must be 1 byte"));
                        }
                        return true;
                    }

                });
            }
            if (tv.getTag() == TAG_NOTICE) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length > 1024) {
                            throw new IllegalArgumentException(String.format("notice must be 1024 bytes or less"));
                        }
                        o.notice = new String(tv.getValue());
                        return true;
                    }

                });
            }
        }
        return o;
    }

}

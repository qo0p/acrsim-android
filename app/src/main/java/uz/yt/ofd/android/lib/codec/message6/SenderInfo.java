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
 * Информация об отправителе
 *
 * @author administrator
 */
public class SenderInfo extends TLVEncodable {

    public static final byte TAG_NAME = (byte) 0x01;
    public static final byte TAG_SN = (byte) 0x02;
    public static final byte TAG_VERSION = (byte) 0x03;

    public static void buildTlvTagDescriptions(TlvTagDescriptions parentTlvTagDescriptions, TlvTagDescriptions.OID oid) {
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_NAME, "Name"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_SN, "SN"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_VERSION, "Version"));
    }

    private String name;
    private String SN;
    private String version;

    public SenderInfo() {
    }

    /**
     * @param name    название устройства (ПО)
     * @param SN      серийный номер
     * @param version версия кассового ПО/прошивки
     */
    public SenderInfo(String name, String SN, String version) {
        this.name = name;
        this.SN = SN;
        this.version = version;
    }

    public static SenderInfo decode(TVS tvs) throws Exception {
        SenderInfo o = new SenderInfo();
        SingleTagReader str = new SingleTagReader();
        for (TV tv : tvs) {
            if (tv.getTag() == TAG_NAME) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        o.name = new String(tv.getValue());
                        return true;
                    }

                });

            }
            if (tv.getTag() == TAG_SN) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        o.SN = new String(tv.getValue());
                        return true;
                    }

                });
            }
            if (tv.getTag() == TAG_VERSION) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        o.version = new String(tv.getValue());
                        return true;
                    }

                });
            }
        }
        return o;
    }

    /**
     * @return название устройства (ПО)
     */
    public String getName() {
        return name;
    }

    /**
     * @param name название устройства (ПО)
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return серийный номер
     */
    public String getSN() {
        return SN;
    }

    /**
     * @param SN серийный номер
     */
    public void setSN(String SN) {
        this.SN = SN;
    }

    /**
     * @return версия кассового ПО/прошивки
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version версия кассового ПО/прошивки
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Закодировать в TLV-структуру
     *
     * @param w поток записи
     * @throws Exception ошибка при кодировании
     */
    @Override
    public void write(OutputStream w) throws IOException {
        writeString(TAG_NAME, name, 32, w);
        writeString(TAG_SN, SN, 64, w);
        writeString(TAG_VERSION, version, 64, w);
    }

}

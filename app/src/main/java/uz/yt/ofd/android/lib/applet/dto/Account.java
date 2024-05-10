/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.applet.dto;

import java.io.IOException;
import java.io.OutputStream;

import uz.yt.ofd.android.lib.codec.BCD8;
import uz.yt.ofd.android.lib.codec.TlvTagDescriptions;
import uz.yt.ofd.android.lib.codec.tlv.SingleTagReader;
import uz.yt.ofd.android.lib.codec.tlv.TLV;
import uz.yt.ofd.android.lib.codec.tlv.TLVEncodable;
import uz.yt.ofd.android.lib.codec.tlv.TV;
import uz.yt.ofd.android.lib.codec.tlv.TVS;

public class Account extends TLVEncodable {

    public static final byte TAG_SALE = (byte) 0x01;
    public static final byte TAG_REFUND = (byte) 0x02;

    public static void buildTlvTagDescriptions(TlvTagDescriptions parentTlvTagDescriptions, TlvTagDescriptions.OID oid) {
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_SALE, "Sale"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_REFUND, "Refund"));
    }

    Long sale;
    Long refund;

    public Account() {
    }

    @Override
    public void write(OutputStream w) throws IOException {
        if (sale != null) {
            w.write(TLV.encode(TAG_SALE, BCD8.fromLong(sale).getBytes()));
        }
        if (refund != null) {
            w.write(TLV.encode(TAG_REFUND, BCD8.fromLong(refund).getBytes()));
        }
    }

    public static void decode(TVS tvs, Account o) throws Exception {
        SingleTagReader str = new SingleTagReader();
        for (TV tv : tvs) {
            if (tv.getTag() == TAG_SALE) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length <= BCD8.SIZE) {
                            o.sale = new BCD8(tv.getValue(), (short) 0, (short) tv.getValue().length).toLong();
                        } else {
                            throw new IllegalArgumentException(String.format("sale must be %d bytes long or less", BCD8.SIZE));
                        }
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_REFUND) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length <= BCD8.SIZE) {
                            o.refund = new BCD8(tv.getValue(), (short) 0, (short) tv.getValue().length).toLong();
                        } else {
                            throw new IllegalArgumentException(String.format("refund must be %d bytes long or less", BCD8.SIZE));
                        }
                        return true;
                    }

                });
            }
        }
    }

    public static Account decode(TVS tvs) throws Exception {
        Account o = new Account();
        decode(tvs, o);
        return o;
    }

    public Long getSale() {
        return sale;
    }

    public void setSale(Long sale) {
        this.sale = sale;
    }

    public Long getRefund() {
        return refund;
    }

    public void setRefund(Long refund) {
        this.refund = refund;
    }
}

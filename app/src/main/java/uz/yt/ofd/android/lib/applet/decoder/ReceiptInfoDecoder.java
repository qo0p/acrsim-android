/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.applet.decoder;


import uz.yt.ofd.android.lib.applet.dto.ReceiptInfo;
import uz.yt.ofd.android.lib.codec.TlvTagDescriptions;
import uz.yt.ofd.android.lib.codec.tlv.TV;
import uz.yt.ofd.android.lib.codec.tlv.TVS;
import uz.yt.ofd.android.lib.decoder.AbstractDecoder;


public class ReceiptInfoDecoder extends AbstractDecoder<ReceiptInfo> {

    public static final byte TAG_RECEIPT_INFO = (byte) 0xa3;

    public ReceiptInfoDecoder(byte[] data) {
        super("RECEIPT_INFO", data);
    }

    @Override
    public ReceiptInfo decode() throws IllegalArgumentException, Exception {
        if (this.data == null) {
            throw new NullPointerException("data is null");
        }
        TlvTagDescriptions descriptions = new TlvTagDescriptions();
        TlvTagDescriptions.OID oid = new TlvTagDescriptions.OID(TAG_RECEIPT_INFO, "ReceiptInfo");
        ReceiptInfo.buildTlvTagDescriptions(descriptions, oid);
        descriptions.addTagDesciption(oid);
        tvs = dumpDescriptor.readTLV("ReceiptInfo TLV", descriptions);
        TV tv = tvs.find(TAG_RECEIPT_INFO);
        if (tv == null) {
            throw new IllegalArgumentException("RECEIPT_INFO is not found");
        }
        ReceiptInfo info = ReceiptInfo.decode(tv.getTvs());
        return info;

    }

}

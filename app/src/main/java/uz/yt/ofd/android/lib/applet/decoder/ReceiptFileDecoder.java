/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.applet.decoder;


import uz.yt.ofd.android.lib.applet.dto.ReceiptFile;
import uz.yt.ofd.android.lib.codec.TlvTagDescriptions;
import uz.yt.ofd.android.lib.codec.tlv.TV;
import uz.yt.ofd.android.lib.codec.tlv.TVS;
import uz.yt.ofd.android.lib.decoder.AbstractDecoder;


public class ReceiptFileDecoder extends AbstractDecoder<ReceiptFile> {

    public static final byte TAG_RECEIPT_FILE = (byte) 0xa5;

    public ReceiptFileDecoder(byte[] data) {
        super("RECEIPT_FILE", data);
    }

    @Override
    public ReceiptFile decode() throws IllegalArgumentException, Exception {
        if (this.data == null) {
            throw new NullPointerException("data is null");
        }
        TlvTagDescriptions descriptions = new TlvTagDescriptions();
        TlvTagDescriptions.OID oid = new TlvTagDescriptions.OID(TAG_RECEIPT_FILE, "ReceiptFile");
        ReceiptFile.buildTlvTagDescriptions(descriptions, oid);
        descriptions.addTagDesciption(oid);
        tvs = dumpDescriptor.readTLV("ReceiptFile TLV", descriptions);
        TV tv = tvs.find(TAG_RECEIPT_FILE);
        if (tv == null) {
            throw new IllegalArgumentException("RECEIPT_FILE is not found");
        }
        ReceiptFile info = ReceiptFile.decode(tv.getTvs());
        info.setFile(this.data);
        return info;

    }

}

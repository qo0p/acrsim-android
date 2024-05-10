/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.applet.decoder;

import uz.yt.ofd.android.lib.applet.dto.ZReportFile;
import uz.yt.ofd.android.lib.codec.TlvTagDescriptions;
import uz.yt.ofd.android.lib.codec.tlv.TV;
import uz.yt.ofd.android.lib.codec.tlv.TVS;
import uz.yt.ofd.android.lib.decoder.AbstractDecoder;

public class ZReportFileDecoder extends AbstractDecoder<ZReportFile> {

    public static final byte TAG_ZREPORT_FILE = (byte) 0xa4;

    public ZReportFileDecoder(byte[] data) {
        super("ZREPORT_FILE", data);
    }



    @Override
    public ZReportFile decode() throws Exception {
        if (this.data == null) {
            throw new NullPointerException("data is null");
        }
        TlvTagDescriptions descriptions = new TlvTagDescriptions();
        TlvTagDescriptions.OID oid = new TlvTagDescriptions.OID(TAG_ZREPORT_FILE, "ZReportFile");
        ZReportFile.buildTlvTagDescriptions(descriptions, oid);
        descriptions.addTagDesciption(oid);
        tvs = dumpDescriptor.readTLV("ZReportFile TLV", descriptions);
        TV tv = tvs.find(TAG_ZREPORT_FILE);
        if (tv == null) {
            throw new IllegalArgumentException("ZREPORT_FILE is not found");
        }
        ZReportFile info = ZReportFile.decode(tv.getTvs());
        info.setFile(this.data);
        return info;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.applet.decoder;

import uz.yt.ofd.android.lib.applet.dto.ZReportInfo;
import uz.yt.ofd.android.lib.codec.TlvTagDescriptions;
import uz.yt.ofd.android.lib.codec.tlv.TV;
import uz.yt.ofd.android.lib.codec.tlv.TVS;
import uz.yt.ofd.android.lib.decoder.AbstractDecoder;

public class ZReportInfoDecoder extends AbstractDecoder<ZReportInfo> {

    public static final byte TAG_ZREPORT_INFO = (byte) 0xa2;

    public ZReportInfoDecoder(byte[] data) {
        super("ZREPORT_INFO", data);
    }

    @Override
    public ZReportInfo decode() throws Exception {
        if (this.data == null) {
            throw new NullPointerException("data is null");
        }
        TlvTagDescriptions descriptions = new TlvTagDescriptions();
        TlvTagDescriptions.OID oid = new TlvTagDescriptions.OID(TAG_ZREPORT_INFO, "ZReportInfo");
        ZReportInfo.buildTlvTagDescriptions(descriptions, oid);
        descriptions.addTagDesciption(oid);
        tvs = dumpDescriptor.readTLV("ZReportInfo TLV", descriptions);
        TV tv = tvs.find(TAG_ZREPORT_INFO);
        if (tv == null) {
            throw new IllegalArgumentException("ZREPORT_INFO is not found");
        }
        ZReportInfo info = ZReportInfo.decode(tv.getTvs());
        return info;
    }

}

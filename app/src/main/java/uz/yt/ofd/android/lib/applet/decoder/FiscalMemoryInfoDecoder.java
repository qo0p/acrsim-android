/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.applet.decoder;

import uz.yt.ofd.android.lib.applet.dto.FiscalMemoryInfo;
import uz.yt.ofd.android.lib.codec.TlvTagDescriptions;
import uz.yt.ofd.android.lib.codec.tlv.TV;
import uz.yt.ofd.android.lib.codec.tlv.TVS;
import uz.yt.ofd.android.lib.decoder.AbstractDecoder;

public class FiscalMemoryInfoDecoder extends AbstractDecoder<FiscalMemoryInfo> {

    public static final byte TAG_FISCAL_MEMORY = (byte) 0xa1;

    public FiscalMemoryInfoDecoder(byte[] data) {
        super("FISCAL_MEMORY_INFO", data);
    }

    @Override
    public FiscalMemoryInfo decode() throws Exception {
        if (this.data == null) {
            throw new NullPointerException("data is null");
        }
        TlvTagDescriptions descriptions = new TlvTagDescriptions();
        TlvTagDescriptions.OID oid = new TlvTagDescriptions.OID(TAG_FISCAL_MEMORY, "FiscalMemoryInfo");
        FiscalMemoryInfo.buildTlvTagDescriptions(descriptions, oid);
        descriptions.addTagDesciption(oid);
        tvs = dumpDescriptor.readTLV("FiscalMemoryInfo TLV", descriptions);
        TV tv = tvs.find(TAG_FISCAL_MEMORY);
        if (tv == null) {
            throw new IllegalArgumentException("FISCAL_MEMORY is not found");
        }
        FiscalMemoryInfo info = FiscalMemoryInfo.decode(tv.getTvs());
        return info;
    }
}

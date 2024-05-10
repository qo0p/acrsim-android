/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.applet.decoder;

import uz.yt.ofd.android.lib.applet.dto.Info;
import uz.yt.ofd.android.lib.codec.TlvTagDescriptions;
import uz.yt.ofd.android.lib.codec.tlv.TV;
import uz.yt.ofd.android.lib.codec.tlv.TVS;
import uz.yt.ofd.android.lib.decoder.AbstractDecoder;

public class InfoDecoder extends AbstractDecoder<Info> {

    public static final byte TAG_INFO = (byte) 0xa0;

    public InfoDecoder(byte[] data) {
        super("INFO", data);
    }

    @Override
    public Info decode() throws Exception {
        if (this.data == null) {
            throw new NullPointerException("data is null");
        }
        TlvTagDescriptions descriptions = new TlvTagDescriptions();
        TlvTagDescriptions.OID oid = new TlvTagDescriptions.OID(TAG_INFO, "Info");
        Info.buildTlvTagDescriptions(descriptions, oid);
        descriptions.addTagDesciption(oid);
        tvs = dumpDescriptor.readTLV("Info TLV", descriptions);
        TV tv = tvs.find(TAG_INFO);
        if (tv == null) {
            throw new IllegalArgumentException("INFO is not found");
        }
        Info info = Info.decode(tv.getTvs());
        return info;
    }

}

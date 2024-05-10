package uz.yt.ofd.android.lib.applet.decoder;

import uz.yt.ofd.android.lib.applet.dto.FiscalSignInfo;
import uz.yt.ofd.android.lib.codec.TlvTagDescriptions;
import uz.yt.ofd.android.lib.codec.tlv.TV;
import uz.yt.ofd.android.lib.codec.tlv.TVS;
import uz.yt.ofd.android.lib.decoder.AbstractDecoder;

public class FiscalSignInfoDecoder extends AbstractDecoder<FiscalSignInfo> {

    public static final byte TAG_FISCAL_SIGN_INFO = (byte) 0xa3;

    public FiscalSignInfoDecoder(byte[] data) {
        super("FISCAL_SIGN_INFO", data);
    }

    @Override
    public FiscalSignInfo decode() throws Exception {
        if (this.data == null) {
            throw new NullPointerException("data is null");
        }
        TlvTagDescriptions descriptions = new TlvTagDescriptions();
        TlvTagDescriptions.OID oid = new TlvTagDescriptions.OID(TAG_FISCAL_SIGN_INFO, "FiscalSignInfo");
        FiscalSignInfo.buildTlvTagDescriptions(descriptions, oid);
        descriptions.addTagDesciption(oid);
        tvs = dumpDescriptor.readTLV("FiscalSignInfo TLV", descriptions);
        TV tv = tvs.find(TAG_FISCAL_SIGN_INFO);
        if (tv == null) {
            throw new IllegalArgumentException("FISCAL_SIGN_INFO is not found");
        }
        FiscalSignInfo info = FiscalSignInfo.decode(tv.getTvs());
        return info;
    }
}

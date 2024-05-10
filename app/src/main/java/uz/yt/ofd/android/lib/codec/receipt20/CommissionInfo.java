/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.codec.receipt20;

import java.io.IOException;
import java.io.OutputStream;

import uz.yt.ofd.android.lib.codec.TlvTagDescriptions;
import uz.yt.ofd.android.lib.codec.tlv.TLVEncodable;

public class CommissionInfo extends TLVEncodable {

    public static final byte TAG_TIN = (byte) 0x01;
    public static final byte TAG_PINFL = (byte) 0x02;

    public static void buildTlvTagDescriptions(TlvTagDescriptions parentTlvTagDescriptions, TlvTagDescriptions.OID oid) {
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_TIN, "TIN"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_PINFL, "PINFL"));
    }

    private String tin;

    private String pinfl;

    public CommissionInfo() {
    }

    public CommissionInfo(String tin, String pinfl) {
        this.tin = tin;
        this.pinfl = pinfl;
    }

    public String getTin() {
        return tin;
    }

    public void setTin(String tin) {
        this.tin = tin;
    }

    public String getPinfl() {
        return pinfl;
    }

    public void setPinfl(String pinfl) {
        this.pinfl = pinfl;
    }


    public void write(OutputStream w) throws IOException {
        writeString(TAG_TIN, tin, ReceiptCodec.TIN_MAX_SIZE, w);
        writeString(TAG_PINFL, pinfl, ReceiptCodec.PINFL_MAX_SIZE, w);
    }
}

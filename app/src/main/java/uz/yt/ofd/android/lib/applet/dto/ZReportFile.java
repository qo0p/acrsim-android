/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.applet.dto;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import uz.yt.ofd.android.lib.codec.BCDDateTime;
import uz.yt.ofd.android.lib.codec.TerminalID;
import uz.yt.ofd.android.lib.codec.TlvTagDescriptions;
import uz.yt.ofd.android.lib.codec.tlv.SingleTagReader;
import uz.yt.ofd.android.lib.codec.tlv.TLV;
import uz.yt.ofd.android.lib.codec.tlv.TLVEncodable;
import uz.yt.ofd.android.lib.codec.tlv.TV;
import uz.yt.ofd.android.lib.codec.tlv.TVS;


public class ZReportFile extends TLVEncodable {

    public static final byte VERSION = 2;

    public static final byte TAG_TERMINAL_ID = (byte) 0x01;
    public static final byte TAG_CLOSE_TIME = (byte) 0x02;
    public static final byte TAG_SIGNATURE = (byte) 0x03;
    public static final byte TAG_ENCRYPTED_DATA = (byte) 0x04;

    public static void buildTlvTagDescriptions(TlvTagDescriptions parentTlvTagDescriptions, TlvTagDescriptions.OID oid) {
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_TERMINAL_ID, "TerminalID"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_CLOSE_TIME, "CloseTime"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_SIGNATURE, "Signature"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_ENCRYPTED_DATA, "EncryptedData"));
    }

    String terminalID;

    Date closeTime;
    byte[] signature;
    byte[] encryptedData;

    byte[] file;

    @Override
    public void write(OutputStream w) throws IOException {
        if (terminalID != null && !terminalID.isEmpty()) {
            w.write(TLV.encode(TAG_TERMINAL_ID, TerminalID.encode(terminalID)));
        }
        if (closeTime != null) {
            w.write(TLV.encode(TAG_CLOSE_TIME, BCDDateTime.toBytes(closeTime)));
        }
        if (signature != null) {
            w.write(TLV.encode(TAG_SIGNATURE, signature));
        }
        if (encryptedData != null) {
            w.write(TLV.encode(TAG_ENCRYPTED_DATA, encryptedData));
        }
    }

    public static ZReportFile decode(TVS tvs) throws Exception {
        ZReportFile o = new ZReportFile();
        SingleTagReader str = new SingleTagReader();
        for (TV tv : tvs) {
            if (tv.getTag() == TAG_TERMINAL_ID) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        o.terminalID = TerminalID.decode(tv.getValue());
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_CLOSE_TIME) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == Size.TIME_SIZE) {
                            o.closeTime = BCDDateTime.fromBytes(tv.getValue(), (short) 0);
                        } else {
                            throw new IllegalArgumentException(String.format("closeTime must be %d bytes long", Size.TIME_SIZE));
                        }
                        return true;
                    }

                });
            }
            if (tv.getTag() == TAG_SIGNATURE) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        o.signature = tv.getValue();
                        return true;
                    }

                });
            }
            if (tv.getTag() == TAG_ENCRYPTED_DATA) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        o.encryptedData = tv.getValue();
                        return true;
                    }
                });
            }
        }
        return o;
    }

    public String getTerminalID() {
        return terminalID;
    }

    public void setTerminalID(String terminalID) {
        this.terminalID = terminalID;
    }

    public Date getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Date closeTime) {
        this.closeTime = closeTime;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public byte[] getEncryptedData() {
        return encryptedData;
    }

    public void setEncryptedData(byte[] encryptedData) {
        this.encryptedData = encryptedData;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}

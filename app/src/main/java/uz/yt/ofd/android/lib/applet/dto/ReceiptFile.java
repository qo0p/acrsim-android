/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.applet.dto;

import java.io.IOException;
import java.io.OutputStream;

import uz.yt.ofd.android.lib.codec.BCD8;
import uz.yt.ofd.android.lib.codec.TerminalID;
import uz.yt.ofd.android.lib.codec.TlvTagDescriptions;
import uz.yt.ofd.android.lib.codec.receipt20.ReceiptType;
import uz.yt.ofd.android.lib.codec.tlv.SingleTagReader;
import uz.yt.ofd.android.lib.codec.tlv.TLV;
import uz.yt.ofd.android.lib.codec.tlv.TLVEncodable;
import uz.yt.ofd.android.lib.codec.tlv.TV;
import uz.yt.ofd.android.lib.codec.tlv.TVS;

public class ReceiptFile extends TLVEncodable {

    public static final byte VERSION = 2;

    public static final byte TAG_TERMINAL_ID = (byte) 0x01;
    public static final byte TAG_RECEIPT_SEQ = (byte) 0x02;
    public static final byte TAG_SIGNATURE = (byte) 0x03;
    public static final byte TAG_ENCRYPTED_DATA = (byte) 0x04;
    public static final byte TAG_TYPE = (byte) 0x05;

    public static void buildTlvTagDescriptions(TlvTagDescriptions parentTlvTagDescriptions, TlvTagDescriptions.OID oid) {
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_TERMINAL_ID, "TerminalID"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_RECEIPT_SEQ, "ReceiptSeq"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_SIGNATURE, "Signature"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_ENCRYPTED_DATA, "EncryptedData"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_TYPE, "Type"));
    }

    String terminalID;
    Long receiptSeq;
    byte[] signature;
    ReceiptType type;
    byte[] encryptedData;

    byte[] file;


    @Override
    public void write(OutputStream w) throws IOException {
        if (terminalID != null && !terminalID.isEmpty()) {
            w.write(TLV.encode(TAG_TERMINAL_ID, TerminalID.encode(terminalID)));
        }
        if (receiptSeq != null) {
            w.write(TLV.encode(TAG_RECEIPT_SEQ, BCD8.fromLong(receiptSeq).getBytes()));
        }
        if (signature != null) {
            w.write(TLV.encode(TAG_SIGNATURE, signature));
        }
        if (encryptedData != null) {
            w.write(TLV.encode(TAG_ENCRYPTED_DATA, encryptedData));
        }
        if (type != null) {
            writeByte(TAG_TYPE, type.getValue(), w);
        }
    }

    public static ReceiptFile decode(TVS tvs) throws Exception {
        ReceiptFile o = new ReceiptFile();
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
            if (tv.getTag() == TAG_RECEIPT_SEQ) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == Size.RECEIPT_SEQ_SIZE) {
                            o.receiptSeq = new BCD8(tv.getValue(), (short) 0, (short) tv.getValue().length).toLong();
                        } else {
                            throw new IllegalArgumentException(String.format("receiptSeq must be %d bytes long", Size.RECEIPT_SEQ_SIZE));
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
            if (tv.getTag() == TAG_TYPE) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == 1) {
                            boolean found = false;
                            for (ReceiptType v : ReceiptType.values()) {
                                if (v.getValue() == tv.getValue()[0]) {
                                    o.type = v;
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                throw new IllegalArgumentException(String.format("type value is illegal"));
                            }
                        } else {
                            throw new IllegalArgumentException(String.format("type must be 1 byte long"));
                        }
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

    public Long getReceiptSeq() {
        return receiptSeq;
    }

    public void setReceiptSeq(long receiptSeq) {
        this.receiptSeq = receiptSeq;
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

    public ReceiptType getType() {
        return type;
    }

    public void setType(ReceiptType type) {
        this.type = type;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}

package uz.yt.ofd.android.lib.applet.dto;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import uz.yt.ofd.android.lib.codec.BCD8;
import uz.yt.ofd.android.lib.codec.BCDDateTime;
import uz.yt.ofd.android.lib.codec.TerminalID;
import uz.yt.ofd.android.lib.codec.TlvTagDescriptions;
import uz.yt.ofd.android.lib.codec.tlv.SingleTagReader;
import uz.yt.ofd.android.lib.codec.tlv.TLV;
import uz.yt.ofd.android.lib.codec.tlv.TLVEncodable;
import uz.yt.ofd.android.lib.codec.tlv.TV;
import uz.yt.ofd.android.lib.codec.tlv.TVS;

public class FiscalSignInfo extends TLVEncodable {


    public static final byte TAG_TERMINAL_ID = (byte) 0x01;
    public static final byte TAG_RECEIPT_SEQ = (byte) 0x02;
    public static final byte TAG_TIME = (byte) 0x03;
    public static final byte TAG_FISCAL_SIGN = (byte) 0x04;
    public static final byte TAG_CIPHER_KEY = (byte) 0x0c;

    public static void buildTlvTagDescriptions(TlvTagDescriptions parentTlvTagDescriptions, TlvTagDescriptions.OID oid) {
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_TERMINAL_ID, "TerminalID"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_RECEIPT_SEQ, "ReceiptSeq"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_TIME, "Time"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_FISCAL_SIGN, "FiscalSign"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_CIPHER_KEY, "CipherKey"));
    }

    String terminalID;
    Long receiptSeq;
    Date time;
    byte[] fiscalSign;
    byte[] cipherKey;

    @Override
    public void write(OutputStream w) throws IOException {
        if (terminalID != null && !terminalID.isEmpty()) {
            w.write(TLV.encode(TAG_TERMINAL_ID, TerminalID.encode(terminalID)));
        }
        if (receiptSeq != null) {
            w.write(TLV.encode(TAG_RECEIPT_SEQ, BCD8.fromLong(receiptSeq).getBytes()));
        }
        if (time != null) {
            w.write(TLV.encode(TAG_TIME, BCDDateTime.toBytes(time)));
        }
        if (fiscalSign != null) {
            w.write(TLV.encode(TAG_FISCAL_SIGN, fiscalSign));
        }
        if (cipherKey != null) {
            w.write(TLV.encode(TAG_CIPHER_KEY, cipherKey));
        }
    }

    public static FiscalSignInfo decode(TVS tvs) throws Exception {
        FiscalSignInfo o = new FiscalSignInfo();
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
            if (tv.getTag() == TAG_TIME) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == Size.TIME_SIZE) {
                            o.time = BCDDateTime.fromBytes(tv.getValue(), (short) 0);
                        } else {
                            throw new IllegalArgumentException(String.format("time must be %d bytes long", Size.TIME_SIZE));
                        }
                        return true;
                    }

                });
            }
            if (tv.getTag() == TAG_FISCAL_SIGN) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        o.fiscalSign = tv.getValue();
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_CIPHER_KEY) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        o.cipherKey = tv.getValue();
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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public byte[] getFiscalSign() {
        return fiscalSign;
    }

    public void setFiscalSign(byte[] fiscalSign) {
        this.fiscalSign = fiscalSign;
    }

    public byte[] getCipherKey() {
        return cipherKey;
    }

    public void setCipherKey(byte[] cipherKey) {
        this.cipherKey = cipherKey;
    }
}

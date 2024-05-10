/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.codec.receipt20;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

import uz.yt.ofd.android.lib.codec.HexBin;
import uz.yt.ofd.android.lib.codec.TerminalID;
import uz.yt.ofd.android.lib.codec.TlvTagDescriptions;
import uz.yt.ofd.android.lib.codec.tlv.TLV;
import uz.yt.ofd.android.lib.codec.tlv.TLVEncodable;

public class RefundInfo extends TLVEncodable {

    public static final byte TAG_TERMINAL_ID = (byte) 0x01;
    public static final byte TAG_RECEIPT_SEQ = (byte) 0x02;
    public static final byte TAG_DATE_TIME = (byte) 0x03;
    public static final byte TAG_FISCAL_SIGN = (byte) 0x04;

    public static void buildTlvTagDescriptions(TlvTagDescriptions parentTlvTagDescriptions, TlvTagDescriptions.OID oid) {
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_TERMINAL_ID, "TerminalID"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_RECEIPT_SEQ, "ReceiptSeq"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_DATE_TIME, "DateTime"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_FISCAL_SIGN, "FiscalSign"));
    }

    private String terminalID;
    private String receiptSeq;
    private String dateTime;
    private String fiscalSign;

    @Override
    public void write(OutputStream w) throws IOException {
        try {
            w.write(TLV.encode(TAG_TERMINAL_ID, TerminalID.encode(terminalID)));
        } catch (Throwable ex) {
            throw new IOException(ex.getMessage(), ex);
        }
        try {
            writeLong(TAG_RECEIPT_SEQ, Long.parseUnsignedLong(receiptSeq), w);
        } catch (Throwable ex) {
            throw new IOException(ex.getMessage(), ex);
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            writeDate(TAG_DATE_TIME, sdf.parse(dateTime), w);
        } catch (Throwable ex) {
            throw new IOException(ex.getMessage(), ex);
        }
        try {
            w.write(TLV.encode(TAG_FISCAL_SIGN, HexBin.decode(fiscalSign)));
        } catch (Throwable ex) {
            throw new IOException(ex.getMessage(), ex);
        }
    }

    public RefundInfo() {
    }

    public RefundInfo(String terminalID, String receiptSeq, String dateTime, String fiscalSign) {
        this.terminalID = terminalID;
        this.receiptSeq = receiptSeq;
        this.dateTime = dateTime;
        this.fiscalSign = fiscalSign;
    }

    public String getTerminalID() {
        return terminalID;
    }

    public void setTerminalID(String terminalID) {
        this.terminalID = terminalID;
    }

    public String getReceiptSeq() {
        return receiptSeq;
    }

    public void setReceiptSeq(String receiptSeq) {
        this.receiptSeq = receiptSeq;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getFiscalSign() {
        return fiscalSign;
    }

    public void setFiscalSign(String fiscalSign) {
        this.fiscalSign = fiscalSign;
    }

}

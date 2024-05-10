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

public class ExtraInfo extends TLVEncodable {

    public static final byte TAG_TIN = (byte) 0x01;
    public static final byte TAG_PINFL = (byte) 0x02;
    public static final byte TAG_CAR_NUMBER = (byte) 0x03;
    public static final byte TAG_PHONE_NUMBER = (byte) 0x04;
    public static final byte TAG_QR_PAYMENT_ID = (byte) 0x05;
    public static final byte TAG_QR_PAYMENT_PROVIDER = (byte) 0x06;
    public static final byte TAG_CASHED_OUT_FROM_CARD = (byte) 0x07;
    public static final byte TAG_PPTID = (byte) 0x08;
    public static final byte TAG_CARD_TYPE = (byte) 0x09;
    public static final byte TAG_OTHER = (byte) 0x0a;

    public static void buildTlvTagDescriptions(TlvTagDescriptions parentTlvTagDescriptions, TlvTagDescriptions.OID oid) {
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_TIN, "TIN"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_PINFL, "PINFL"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_CAR_NUMBER, "CarNumber"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_PHONE_NUMBER, "PhoneNumber"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_QR_PAYMENT_ID, "QRPaymentID"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_QR_PAYMENT_PROVIDER, "QRPaymentProvider"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_CASHED_OUT_FROM_CARD, "CashedOutFromCard"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_PPTID, "PPTID"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_CARD_TYPE, "CardType"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_OTHER, "Other"));
    }

    private String tin;
    private String pinfl;
    private String carNumber;
    private String phoneNumber;
    private String qrPaymentId;
    private short qrPaymentProvider;
    private long cashedOutFromCard;
    private String pptid;
    private byte cardType;
    private String other;

    @Override
    public void write(OutputStream w) throws IOException {
        writeString(TAG_TIN, tin, ReceiptCodec.TIN_MAX_SIZE, w);
        writeString(TAG_PINFL, pinfl, ReceiptCodec.PINFL_MAX_SIZE, w);
        writeString(TAG_CAR_NUMBER, carNumber, ReceiptCodec.CAR_NUMBER_MAX_SIZE, w);
        writeString(TAG_PHONE_NUMBER, phoneNumber, ReceiptCodec.PHONE_NUMBER_MAX_SIZE, w);
        writeString(TAG_QR_PAYMENT_ID, qrPaymentId, ReceiptCodec.QR_PAYMENT_ID_MAX_SIZE, w);
        if (qrPaymentProvider > 0) {
            writeShort(TAG_QR_PAYMENT_PROVIDER, qrPaymentProvider, w);
        }
        if (cashedOutFromCard > 0) {
            writeLong(TAG_CASHED_OUT_FROM_CARD, cashedOutFromCard, w);
        }
        writeString(TAG_PPTID, pptid, ReceiptCodec.PPTID_MAX_SIZE, w);
        writeByte(TAG_CARD_TYPE, cardType, w);
        writeString(TAG_OTHER, other, ReceiptCodec.OTHER_MAX_SIZE, w);
    }

    public ExtraInfo() {
    }

    public ExtraInfo(String tin, String pinfl, String carNumber, String phoneNumber, String qrPaymentId, short qrPaymentProvider, long cashedOutFromCard, String pptid, byte cardType, String other) {
        this.tin = tin;
        this.pinfl = pinfl;
        this.carNumber = carNumber;
        this.phoneNumber = phoneNumber;
        this.qrPaymentId = qrPaymentId;
        this.qrPaymentProvider = qrPaymentProvider;
        this.cashedOutFromCard = cashedOutFromCard;
        this.pptid = pptid;
        this.cardType = cardType;
        this.other = other;
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

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getQrPaymentId() {
        return qrPaymentId;
    }

    public void setQrPaymentId(String qrPaymentId) {
        this.qrPaymentId = qrPaymentId;
    }

    public short getQrPaymentProvider() {
        return qrPaymentProvider;
    }

    public void setQrPaymentProvider(short qrPaymentProvider) {
        this.qrPaymentProvider = qrPaymentProvider;
    }

    public long getCashedOutFromCard() {
        return cashedOutFromCard;
    }

    public void setCashedOutFromCard(long cashedOutFromCard) {
        this.cashedOutFromCard = cashedOutFromCard;
    }

    public String getPptid() {
        return pptid;
    }

    public void setPptid(String pptid) {
        this.pptid = pptid;
    }

    public byte getCardType() {
        return cardType;
    }

    public void setCardType(byte cardType) {
        this.cardType = cardType;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

}

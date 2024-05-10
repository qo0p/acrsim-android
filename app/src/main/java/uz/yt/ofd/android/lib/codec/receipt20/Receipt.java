/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.codec.receipt20;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.LinkedList;

import uz.yt.ofd.android.lib.codec.TlvTagDescriptions;
import uz.yt.ofd.android.lib.codec.tlv.TLV;
import uz.yt.ofd.android.lib.codec.tlv.TLVEncodable;

public class Receipt extends TLVEncodable {

    public static final byte TAG_RECEIVED_CASH = (byte) 0x01;
    public static final byte TAG_RECEIVED_CARD = (byte) 0x02;
    public static final byte TAG_TIME = (byte) 0x03;
    public static final byte TAG_TYPE = (byte) 0x04;
    public static final byte TAG_OPERATION = (byte) 0x05;
    public static final byte TAG_REFUND_INFO = (byte) 0x8d;
    public static final byte TAG_LOCATION = (byte) 0x8e;
    public static final byte TAG_ITEMS = (byte) 0x8c;
    public static final byte TAG_EXTRA_INFO = (byte) 0x8f;

    public static void buildTlvTagDescriptions(TlvTagDescriptions parentTlvTagDescriptions, TlvTagDescriptions.OID oid) {
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_RECEIVED_CASH, "ReceivedCash"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_RECEIVED_CARD, "ReceivedCard"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_TIME, "Time"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_TYPE, "Type"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_OPERATION, "Operation"));

        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_REFUND_INFO, "RefundInfo"));
        RefundInfo.buildTlvTagDescriptions(parentTlvTagDescriptions, oid.append(TAG_REFUND_INFO, "RefundInfo"));

        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_LOCATION, "Location"));
        RefundInfo.buildTlvTagDescriptions(parentTlvTagDescriptions, oid.append(TAG_LOCATION, "Location"));

        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_ITEMS, "Items"));
        RefundInfo.buildTlvTagDescriptions(parentTlvTagDescriptions, oid.append(TAG_ITEMS, "Items"));

        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_EXTRA_INFO, "ExtraInfo"));
        RefundInfo.buildTlvTagDescriptions(parentTlvTagDescriptions, oid.append(TAG_EXTRA_INFO, "ExtraInfo"));
    }


    private LinkedList<ReceiptItem> items;

    private long receivedCash;

    private long receivedCard;

    private Date time;

    private ReceiptType type;

    private OperationType operation;

    private RefundInfo refundInfo;

    private Location location;

    private ExtraInfo extraInfo;

    @Override
    public void write(OutputStream w) throws IOException {
        if (receivedCash > ReceiptCodec.BCD8_MAX_LIMIT) {
            throw new IllegalArgumentException(String.format("receipt received cash value size %d shouldn't exceed %d", receivedCash, ReceiptCodec.BCD8_MAX_LIMIT));
        }

        if (receivedCard > ReceiptCodec.BCD8_MAX_LIMIT) {
            throw new IllegalArgumentException(String.format("receipt received card value size %d shouldn't exceed %d", receivedCard, ReceiptCodec.BCD8_MAX_LIMIT));
        }
        if (items == null || items.size() == 0) {
            throw new IllegalArgumentException("receipt has no iteems");
        }
        if (items.size() > Short.MAX_VALUE) {
            throw new IllegalArgumentException("receipt has too many iteems");
        }

        long totalVAT = 0;
        long totalPrice = 0;
        long totalDiscount = 0;

        for (ReceiptItem item : items) {
            totalPrice += item.getPrice();
            totalVAT += item.getVat();
            long diot = item.getDiscount() + item.getOther();
            if (diot - item.getPrice() > 0) {
                throw new IllegalArgumentException(String.format("item total discount %d shouldn't exceed item price %d", diot, item.getPrice()));
            }
            totalDiscount += diot;
        }

        long totalReceivedSum = receivedCash + receivedCard;

        totalPrice = totalPrice - totalDiscount;

        long diff = Math.abs(totalPrice - totalReceivedSum);
        if (diff > ReceiptCodec.MAX_ALLOWED_DIFF) {
            throw new IllegalArgumentException(String.format("difference %d between %d and %d is larger than %d", diff, totalPrice, totalReceivedSum, ReceiptCodec.MAX_ALLOWED_DIFF));
        }

        if (receivedCash > ReceiptCodec.BCD8_MAX_LIMIT) {
            throw new IllegalArgumentException(String.format("receipt received cash value size %d shouldn't exceed %d", receivedCash, ReceiptCodec.BCD8_MAX_LIMIT));
        }

        if (receivedCard > ReceiptCodec.BCD8_MAX_LIMIT) {
            throw new IllegalArgumentException(String.format("receipt received card value size %d shouldn't exceed %d", receivedCard, ReceiptCodec.BCD8_MAX_LIMIT));
        }

        if (totalVAT > ReceiptCodec.BCD8_MAX_LIMIT) {
            throw new IllegalArgumentException(String.format("receipt total vat value size %d shouldn't exceed %d", totalVAT, ReceiptCodec.BCD8_MAX_LIMIT));
        }

        writeLong(TAG_RECEIVED_CASH, receivedCash, w);
        writeLong(TAG_RECEIVED_CARD, receivedCard, w);
        writeDate(TAG_TIME, time, w);
        writeByte(TAG_TYPE, type.getValue(), w);
        writeByte(TAG_OPERATION, operation.getValue(), w);
        if (refundInfo != null) {
            w.write(TLV.encode(TAG_REFUND_INFO, refundInfo.encode()));
        }
        if (location != null) {
            w.write(TLV.encode(TAG_LOCATION, location.encode()));
        }
        for (ReceiptItem it : items) {
            w.write(TLV.encode(TAG_ITEMS, it.encode()));
        }
        if (extraInfo != null) {
            w.write(TLV.encode(TAG_EXTRA_INFO, extraInfo.encode()));
        }
    }

    public Receipt(LinkedList<ReceiptItem> items, long receivedCash, long receivedCard, Date time, ReceiptType type, OperationType operation, RefundInfo refundInfo, Location location, ExtraInfo extraInfo) {
        this.items = items;
        this.receivedCash = receivedCash;
        this.receivedCard = receivedCard;
        this.time = time;
        this.type = type;
        this.operation = operation;
        this.refundInfo = refundInfo;
        this.location = location;
        this.extraInfo = extraInfo;
    }

    public LinkedList<ReceiptItem> getItems() {
        return items;
    }

    public void setItems(LinkedList<ReceiptItem> items) {
        this.items = items;
    }

    public long getReceivedCash() {
        return receivedCash;
    }

    public void setReceivedCash(long receivedCash) {
        this.receivedCash = receivedCash;
    }

    public long getReceivedCard() {
        return receivedCard;
    }

    public void setReceivedCard(long receivedCard) {
        this.receivedCard = receivedCard;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public ReceiptType getType() {
        return type;
    }

    public void setType(ReceiptType type) {
        this.type = type;
    }

    public OperationType getOperation() {
        return operation;
    }

    public void setOperation(OperationType operation) {
        this.operation = operation;
    }

    public RefundInfo getRefundInfo() {
        return refundInfo;
    }

    public void setRefundInfo(RefundInfo refundInfo) {
        this.refundInfo = refundInfo;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ExtraInfo getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(ExtraInfo extraInfo) {
        this.extraInfo = extraInfo;
    }

    public long calcTotalVAT() {
        long vat = 0;
        for (ReceiptItem it : items) {
            vat += it.getVat();
        }
        return vat;
    }
}

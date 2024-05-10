/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.applet.dto;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import uz.yt.ofd.android.lib.codec.BCD8;
import uz.yt.ofd.android.lib.codec.BCDDateTime;
import uz.yt.ofd.android.lib.codec.TerminalID;
import uz.yt.ofd.android.lib.codec.TlvTagDescriptions;
import uz.yt.ofd.android.lib.codec.Utils;
import uz.yt.ofd.android.lib.codec.tlv.SingleTagReader;
import uz.yt.ofd.android.lib.codec.tlv.TLV;
import uz.yt.ofd.android.lib.codec.tlv.TLVEncodable;
import uz.yt.ofd.android.lib.codec.tlv.TV;
import uz.yt.ofd.android.lib.codec.tlv.TVS;

public class FiscalMemoryInfo extends TLVEncodable {

    public static final byte TAG_TERMINAL_ID = (byte) 0x01;
    public static final byte TAG_RECEIPT_SEQ = (byte) 0x02;
    public static final byte TAG_LAST_OPERATION_TIME = (byte) 0x03;
    public static final byte TAG_FIRST_UNACKNOWLEDGED_RECEIPT_TIME = (byte) 0x04;
    public static final byte TAG_ZREPORTS_COUNT = (byte) 0x05;
    public static final byte TAG_RECEIPTS_COUNT = (byte) 0x06;
    public static final byte TAG_CASH_ACCUMULATOR = (byte) 0x80;
    public static final byte TAG_CARD_ACCUMULATOR = (byte) 0x81;
    public static final byte TAG_VAT_ACCUMULATOR = (byte) 0x82;

    public static void buildTlvTagDescriptions(TlvTagDescriptions parentTlvTagDescriptions, TlvTagDescriptions.OID oid) {
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_TERMINAL_ID, "TerminalID"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_RECEIPT_SEQ, "ReceiptSeq"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_LAST_OPERATION_TIME, "LastOperationTime"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_FIRST_UNACKNOWLEDGED_RECEIPT_TIME, "FirstUnacknowledgedReceiptTime"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_ZREPORTS_COUNT, "ZReportsCount"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_RECEIPTS_COUNT, "ReceiptsCount"));

        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_CASH_ACCUMULATOR, "CashAccomulator"));
        Account.buildTlvTagDescriptions(parentTlvTagDescriptions, oid.append(TAG_CASH_ACCUMULATOR, "CashAccomulator"));

        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_CARD_ACCUMULATOR, "CardAccomulator"));
        Account.buildTlvTagDescriptions(parentTlvTagDescriptions, oid.append(TAG_CARD_ACCUMULATOR, "CardAccomulator"));

        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_VAT_ACCUMULATOR, "VATAccomulator"));
        Account.buildTlvTagDescriptions(parentTlvTagDescriptions, oid.append(TAG_VAT_ACCUMULATOR, "VATAccomulator"));
    }

    private String terminalID;
    private Long receiptSeq;
    private Date lastOperationTime;
    private Date firstUnacknowledgedReceiptTime;
    private Short zreportsCount;
    private Short receiptsCount;
    private Account cashAccumulator;
    private Account cardAccumulator;
    private Account vatAccumulator;


    @Override
    public void write(OutputStream w) throws IOException {
        if (terminalID != null && !terminalID.isEmpty()) {
            w.write(TLV.encode(TAG_TERMINAL_ID, TerminalID.encode(terminalID)));
        }
        if (receiptSeq != null && receiptSeq >= 0 && receiptSeq <= 9999999999999999l) {
            w.write(TLV.encode(TAG_RECEIPT_SEQ, BCD8.fromLong(receiptSeq).getBytes()));
        }
        if (lastOperationTime != null) {
            w.write(TLV.encode(TAG_LAST_OPERATION_TIME, BCDDateTime.toBytes(lastOperationTime)));
        }
        if (firstUnacknowledgedReceiptTime != null) {
            w.write(TLV.encode(TAG_FIRST_UNACKNOWLEDGED_RECEIPT_TIME, BCDDateTime.toBytes(firstUnacknowledgedReceiptTime)));
        }
        if (zreportsCount != null) {
            w.write(TLV.encode(TAG_ZREPORTS_COUNT, Utils.short2bytes(zreportsCount)));
        }
        if (receiptsCount != null) {
            w.write(TLV.encode(TAG_RECEIPTS_COUNT, Utils.short2bytes(receiptsCount)));
        }
        if (cashAccumulator != null) {
            w.write(TLV.encode(TAG_CASH_ACCUMULATOR, cashAccumulator.encode()));
        }
        if (cardAccumulator != null) {
            w.write(TLV.encode(TAG_CARD_ACCUMULATOR, cardAccumulator.encode()));
        }
        if (vatAccumulator != null) {
            w.write(TLV.encode(TAG_VAT_ACCUMULATOR, vatAccumulator.encode()));
        }
    }

    public static FiscalMemoryInfo decode(TVS tvs) throws Exception {
        FiscalMemoryInfo o = new FiscalMemoryInfo();
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
                        if (tv.getValue() != null && tv.getValue().length == Size.ACCUMULATOR_SIZE) {
                            o.receiptSeq = new BCD8(tv.getValue(), (short) 0, (short) tv.getValue().length).toLong();
                        } else {
                            throw new IllegalArgumentException(String.format("receiptSeq must be %d bytes long", Size.RECEIPT_SEQ_SIZE));
                        }
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_LAST_OPERATION_TIME) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == Size.TIME_SIZE) {
                            o.lastOperationTime = BCDDateTime.fromBytes(tv.getValue(), (short) 0);
                        } else {
                            throw new IllegalArgumentException(String.format("lastOperationTime must be %d bytes long", Size.TIME_SIZE));
                        }
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_FIRST_UNACKNOWLEDGED_RECEIPT_TIME) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == Size.TIME_SIZE) {
                            o.firstUnacknowledgedReceiptTime = BCDDateTime.fromBytes(tv.getValue(), (short) 0);
                        } else {
                            throw new IllegalArgumentException(String.format("firstUnacknowledgedReceiptTime must be %d bytes long", Size.TIME_SIZE));
                        }
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_ZREPORTS_COUNT) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == 2) {
                            o.zreportsCount = Utils.readShort(tv.getValue(), 0);
                        } else {
                            throw new IllegalArgumentException(String.format("zreportsArrayUsed must be 2 bytes long"));
                        }
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_RECEIPTS_COUNT) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == 2) {
                            o.receiptsCount = Utils.readShort(tv.getValue(), 0);
                        } else {
                            throw new IllegalArgumentException(String.format("receiptsArrayUsed must be 2 bytes long"));
                        }
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_CASH_ACCUMULATOR) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        o.cashAccumulator = Account.decode(tv.getTvs());
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_CARD_ACCUMULATOR) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        o.cardAccumulator = Account.decode(tv.getTvs());
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_VAT_ACCUMULATOR) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        o.vatAccumulator = Account.decode(tv.getTvs());
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

    public Date getLastOperationTime() {
        return lastOperationTime;
    }

    public void setLastOperationTime(Date lastOperationTime) {
        this.lastOperationTime = lastOperationTime;
    }

    public Date getFirstUnacknowledgedReceiptTime() {
        return firstUnacknowledgedReceiptTime;
    }

    public void setFirstUnacknowledgedReceiptTime(Date firstUnacknowledgedReceiptTime) {
        this.firstUnacknowledgedReceiptTime = firstUnacknowledgedReceiptTime;
    }

    public Short getZreportsCount() {
        return zreportsCount;
    }

    public void setZreportsCount(short zreportsCount) {
        this.zreportsCount = zreportsCount;
    }

    public Short getReceiptsCount() {
        return receiptsCount;
    }

    public void setReceiptsCount(short receiptsCount) {
        this.receiptsCount = receiptsCount;
    }

    public Account getCashAccumulator() {
        return cashAccumulator;
    }

    public void setCashAccumulator(Account cashAccumulator) {
        this.cashAccumulator = cashAccumulator;
    }

    public Account getCardAccumulator() {
        return cardAccumulator;
    }

    public void setCardAccumulator(Account cardAccumulator) {
        this.cardAccumulator = cardAccumulator;
    }

    public Account getVatAccumulator() {
        return vatAccumulator;
    }

    public void setVatAccumulator(Account vatAccumulator) {
        this.vatAccumulator = vatAccumulator;
    }


}

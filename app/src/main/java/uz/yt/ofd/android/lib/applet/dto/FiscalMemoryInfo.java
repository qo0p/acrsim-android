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
    public static final byte TAG_ZREPORTS_CAPACITY = (byte) 0x07;
    public static final byte TAG_RECEIPTS_CAPACITY = (byte) 0x08;
    public static final byte TAG_FREPORT_CURRENT_INDEX = (byte) 0x09;
    public static final byte TAG_ZREPORT_CURRENT_INDEX = (byte) 0x0a;
    public static final byte TAG_RECEIPT_CURRENT_INDEX = (byte) 0x0b;
    public static final byte TAG_ZREPORTS_ALLOCATED = (byte) 0x0c;
    public static final byte TAG_RECEIPTS_ALLOCATED = (byte) 0x0d;
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

        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_ZREPORTS_CAPACITY, "ZReportsCapacity"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_RECEIPTS_CAPACITY, "ReceiptsCapacity"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_FREPORT_CURRENT_INDEX, "FReportCurrentIndex"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_ZREPORT_CURRENT_INDEX, "ZReportCurrentIndex"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_RECEIPT_CURRENT_INDEX, "ReceiptCurrentIndex"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_ZREPORTS_ALLOCATED, "ZReportsAllocated"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_RECEIPTS_ALLOCATED, "ReceiptsAllocated"));

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
    private Short zreportsCapacity;
    private Short receiptsCapacity;
    private Short freportCurrentIndex;
    private Short zreportCurrentIndex;
    private Short receiptCurrentIndex;
    private Short zreportsAllocated;
    private Short receiptsAllocated;
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


        if (zreportsCapacity != null) {
            w.write(TLV.encode(TAG_ZREPORTS_CAPACITY, Utils.short2bytes(zreportsCapacity)));
        }
        if (receiptsCapacity != null) {
            w.write(TLV.encode(TAG_RECEIPTS_CAPACITY, Utils.short2bytes(receiptsCapacity)));
        }
        if (freportCurrentIndex != null) {
            w.write(TLV.encode(TAG_FREPORT_CURRENT_INDEX, Utils.short2bytes(freportCurrentIndex)));
        }
        if (zreportCurrentIndex != null) {
            w.write(TLV.encode(TAG_ZREPORT_CURRENT_INDEX, Utils.short2bytes(zreportCurrentIndex)));
        }
        if (receiptCurrentIndex != null) {
            w.write(TLV.encode(TAG_RECEIPT_CURRENT_INDEX, Utils.short2bytes(receiptCurrentIndex)));
        }
        if (zreportsAllocated != null) {
            w.write(TLV.encode(TAG_ZREPORTS_ALLOCATED, Utils.short2bytes(zreportsAllocated)));
        }
        if (receiptsAllocated != null) {
            w.write(TLV.encode(TAG_RECEIPTS_ALLOCATED, Utils.short2bytes(receiptsAllocated)));
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
                            throw new IllegalArgumentException(String.format("zreportsCount must be 2 bytes long"));
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
                            throw new IllegalArgumentException(String.format("receiptsCount must be 2 bytes long"));
                        }
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_ZREPORTS_CAPACITY) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == 2) {
                            o.zreportsCapacity = Utils.readShort(tv.getValue(), 0);
                        } else {
                            throw new IllegalArgumentException(String.format("zreportsCapacity must be 2 bytes long"));
                        }
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_RECEIPTS_CAPACITY) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == 2) {
                            o.receiptsCapacity = Utils.readShort(tv.getValue(), 0);
                        } else {
                            throw new IllegalArgumentException(String.format("receiptsCapacity must be 2 bytes long"));
                        }
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_FREPORT_CURRENT_INDEX) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == 2) {
                            o.freportCurrentIndex = Utils.readShort(tv.getValue(), 0);
                        } else {
                            throw new IllegalArgumentException(String.format("freportCurrentIndex must be 2 bytes long"));
                        }
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_ZREPORT_CURRENT_INDEX) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == 2) {
                            o.zreportCurrentIndex = Utils.readShort(tv.getValue(), 0);
                        } else {
                            throw new IllegalArgumentException(String.format("zreportCurrentIndex must be 2 bytes long"));
                        }
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_RECEIPT_CURRENT_INDEX) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == 2) {
                            o.receiptCurrentIndex = Utils.readShort(tv.getValue(), 0);
                        } else {
                            throw new IllegalArgumentException(String.format("receiptCurrentIndex must be 2 bytes long"));
                        }
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_ZREPORTS_ALLOCATED) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == 2) {
                            o.zreportsAllocated = Utils.readShort(tv.getValue(), 0);
                        } else {
                            throw new IllegalArgumentException(String.format("zreportsAllocated must be 2 bytes long"));
                        }
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_RECEIPTS_ALLOCATED) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == 2) {
                            o.receiptsAllocated = Utils.readShort(tv.getValue(), 0);
                        } else {
                            throw new IllegalArgumentException(String.format("receiptsAllocated must be 2 bytes long"));
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

    public void setReceiptSeq(Long receiptSeq) {
        this.receiptSeq = receiptSeq;
    }

    public void setZreportsCount(Short zreportsCount) {
        this.zreportsCount = zreportsCount;
    }

    public void setReceiptsCount(Short receiptsCount) {
        this.receiptsCount = receiptsCount;
    }

    public Short getZreportsCapacity() {
        return zreportsCapacity;
    }

    public void setZreportsCapacity(Short zreportsCapacity) {
        this.zreportsCapacity = zreportsCapacity;
    }

    public Short getReceiptsCapacity() {
        return receiptsCapacity;
    }

    public void setReceiptsCapacity(Short receiptsCapacity) {
        this.receiptsCapacity = receiptsCapacity;
    }

    public Short getFreportCurrentIndex() {
        return freportCurrentIndex;
    }

    public void setFreportCurrentIndex(Short freportCurrentIndex) {
        this.freportCurrentIndex = freportCurrentIndex;
    }

    public Short getZreportCurrentIndex() {
        return zreportCurrentIndex;
    }

    public void setZreportCurrentIndex(Short zreportCurrentIndex) {
        this.zreportCurrentIndex = zreportCurrentIndex;
    }

    public Short getReceiptCurrentIndex() {
        return receiptCurrentIndex;
    }

    public void setReceiptCurrentIndex(Short receiptCurrentIndex) {
        this.receiptCurrentIndex = receiptCurrentIndex;
    }

    public Short getZreportsAllocated() {
        return zreportsAllocated;
    }

    public void setZreportsAllocated(Short zreportsAllocated) {
        this.zreportsAllocated = zreportsAllocated;
    }

    public Short getReceiptsAllocated() {
        return receiptsAllocated;
    }

    public void setReceiptsAllocated(Short receiptsAllocated) {
        this.receiptsAllocated = receiptsAllocated;
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

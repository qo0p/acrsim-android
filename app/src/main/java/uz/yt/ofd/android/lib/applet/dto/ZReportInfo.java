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


public class ZReportInfo extends TLVEncodable {

    public static final byte TAG_TERMINAL_ID = (byte) 0x01;
    public static final byte TAG_OPEN_TIME = (byte) 0x02;
    public static final byte TAG_CLOSE_TIME = (byte) 0x03;
    public static final byte TAG_TOTAL_SALE_COUNT = (byte) 0x04;
    public static final byte TAG_TOTAL_REFUND_COUNT = (byte) 0x05;
    public static final byte TAG_LAST_RECEIPT_SEQ = (byte) 0x06;
    public static final byte TAG_ACKNOWLEDGED_TIME = (byte) 0x07;
    public static final byte TAG_FIRST_RECEIPT_SEQ = (byte) 0x08;
    public static final byte TAG_TOTAL_CASH = (byte) 0x80;
    public static final byte TAG_TOTAL_CARD = (byte) 0x81;
    public static final byte TAG_TOTAL_VAT = (byte) 0x82;

    public static void buildTlvTagDescriptions(TlvTagDescriptions parentTlvTagDescriptions, TlvTagDescriptions.OID oid) {
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_TERMINAL_ID, "TerminalID"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_OPEN_TIME, "OpenTime"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_CLOSE_TIME, "CloseTime"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_TOTAL_SALE_COUNT, "TotalSaleCount"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_TOTAL_REFUND_COUNT, "TotalRefundCount"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_FIRST_RECEIPT_SEQ, "FirstReceiptSeq"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_LAST_RECEIPT_SEQ, "LastReceiptSeq"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_ACKNOWLEDGED_TIME, "AcknowledgedTime"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_TOTAL_CASH, "TotalCash"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_TOTAL_CARD, "TotalCash"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_TOTAL_VAT, "TotalVAT"));
    }

    String terminalID;

    Date openTime;
    Date closeTime;

    Short totalSaleCount;
    Short totalRefundCount;

    Account totalCash;

    Account totalCard;

    Account totalVAT;

    Long firstReceiptSeq;
    Long lastReceiptSeq;

    Date acknowledgedTime;

    @Override
    public void write(OutputStream w) throws IOException {
        if (terminalID != null && !terminalID.isEmpty()) {
            w.write(TLV.encode(TAG_TERMINAL_ID, TerminalID.encode(terminalID)));
        }

        if (openTime != null) {
            w.write(TLV.encode(TAG_OPEN_TIME, BCDDateTime.toBytes(openTime)));
        }
        if (closeTime != null) {
            w.write(TLV.encode(TAG_CLOSE_TIME, BCDDateTime.toBytes(closeTime)));
        }
        if (totalSaleCount != null) {
            w.write(TLV.encode(TAG_TOTAL_SALE_COUNT, Utils.short2bytes(totalSaleCount)));
        }
        if (totalRefundCount != null) {
            w.write(TLV.encode(TAG_TOTAL_REFUND_COUNT, Utils.short2bytes(totalRefundCount)));
        }
        if (firstReceiptSeq != null) {
            w.write(TLV.encode(TAG_FIRST_RECEIPT_SEQ, BCD8.fromLong(firstReceiptSeq).getBytes()));
        }
        if (lastReceiptSeq != null) {
            w.write(TLV.encode(TAG_LAST_RECEIPT_SEQ, BCD8.fromLong(lastReceiptSeq).getBytes()));
        }
        if (acknowledgedTime != null) {
            w.write(TLV.encode(TAG_ACKNOWLEDGED_TIME, BCDDateTime.toBytes(acknowledgedTime)));
        }
        if (totalCash != null) {
            w.write(TLV.encode(TAG_TOTAL_CASH, totalCash.encode()));
        }
        if (totalCard != null) {
            w.write(TLV.encode(TAG_TOTAL_CARD, totalCard.encode()));
        }
        if (totalVAT != null) {
            w.write(TLV.encode(TAG_TOTAL_VAT, totalVAT.encode()));
        }
    }

    public static ZReportInfo decode(TVS tvs) throws Exception {
        ZReportInfo o = new ZReportInfo();
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
            if (tv.getTag() == TAG_OPEN_TIME) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == Size.TIME_SIZE) {
                            o.openTime = BCDDateTime.fromBytes(tv.getValue(), (short) 0);
                        } else {
                            throw new IllegalArgumentException(String.format("openTime must be %d bytes long", Size.TIME_SIZE));
                        }
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
            if (tv.getTag() == TAG_TOTAL_SALE_COUNT) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == 2) {
                            o.totalSaleCount = Utils.readShort(tv.getValue(), 0);
                        } else {
                            throw new IllegalArgumentException(String.format("totalSaleCount must be 2 bytes long"));
                        }
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_TOTAL_REFUND_COUNT) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == 2) {
                            o.totalRefundCount = Utils.readShort(tv.getValue(), 0);
                        } else {
                            throw new IllegalArgumentException(String.format("totalRefundCount must be 2 bytes long"));
                        }
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_FIRST_RECEIPT_SEQ) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == Size.RECEIPT_SEQ_SIZE) {
                            o.firstReceiptSeq = new BCD8(tv.getValue(), (short) 0, (short) tv.getValue().length).toLong();
                        } else {
                            throw new IllegalArgumentException(String.format("firstReceiptSeq must be %d bytes long", Size.RECEIPT_SEQ_SIZE));
                        }
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_LAST_RECEIPT_SEQ) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == Size.RECEIPT_SEQ_SIZE) {
                            o.lastReceiptSeq = new BCD8(tv.getValue(), (short) 0, (short) tv.getValue().length).toLong();
                        } else {
                            throw new IllegalArgumentException(String.format("lastReceiptSeq must be %d bytes long", Size.RECEIPT_SEQ_SIZE));
                        }
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_ACKNOWLEDGED_TIME) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == Size.TIME_SIZE) {
                            o.acknowledgedTime = BCDDateTime.fromBytes(tv.getValue(), (short) 0);
                        } else {
                            throw new IllegalArgumentException(String.format("acknowledgedTime must be %d bytes long", Size.TIME_SIZE));
                        }
                        return true;
                    }

                });
            }
            if (tv.getTag() == TAG_TOTAL_CASH) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        o.totalCash = new Account();
                        Account.decode(tv.getTvs(), o.totalCash);
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_TOTAL_CARD) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        o.totalCard = new Account();
                        Account.decode(tv.getTvs(), o.totalCard);
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_TOTAL_VAT) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        o.totalVAT = new Account();
                        Account.decode(tv.getTvs(), o.totalVAT);
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

    public Date getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Date openTime) {
        this.openTime = openTime;
    }

    public Date getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Date closeTime) {
        this.closeTime = closeTime;
    }

    public Short getTotalSaleCount() {
        return totalSaleCount;
    }

    public void setTotalSaleCount(short totalSaleCount) {
        this.totalSaleCount = totalSaleCount;
    }

    public Short getTotalRefundCount() {
        return totalRefundCount;
    }

    public void setTotalRefundCount(short totalRefundCount) {
        this.totalRefundCount = totalRefundCount;
    }

    public Account getTotalCash() {
        return totalCash;
    }

    public void setTotalCash(Account totalCash) {
        this.totalCash = totalCash;
    }

    public Account getTotalCard() {
        return totalCard;
    }

    public void setTotalCard(Account totalCard) {
        this.totalCard = totalCard;
    }

    public Account getTotalVAT() {
        return totalVAT;
    }

    public void setTotalVAT(Account totalVAT) {
        this.totalVAT = totalVAT;
    }

    public Long getFirstReceiptSeq() {
        return firstReceiptSeq;
    }

    public void setFirstReceiptSeq(long firstReceiptSeq) {
        this.firstReceiptSeq = firstReceiptSeq;
    }

    public Long getLastReceiptSeq() {
        return lastReceiptSeq;
    }

    public void setLastReceiptSeq(long lastReceiptSeq) {
        this.lastReceiptSeq = lastReceiptSeq;
    }

    public Date getAcknowledgedTime() {
        return acknowledgedTime;
    }

    public void setAcknowledgedTime(Date acknowledgedTime) {
        this.acknowledgedTime = acknowledgedTime;
    }

}

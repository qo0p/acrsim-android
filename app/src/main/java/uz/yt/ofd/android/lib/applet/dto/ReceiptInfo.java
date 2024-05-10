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
import uz.yt.ofd.android.lib.codec.receipt20.OperationType;
import uz.yt.ofd.android.lib.codec.receipt20.ReceiptType;
import uz.yt.ofd.android.lib.codec.tlv.SingleTagReader;
import uz.yt.ofd.android.lib.codec.tlv.TLV;
import uz.yt.ofd.android.lib.codec.tlv.TLVEncodable;
import uz.yt.ofd.android.lib.codec.tlv.TV;
import uz.yt.ofd.android.lib.codec.tlv.TVS;

public class ReceiptInfo extends TLVEncodable {


    public static final byte TAG_TERMINAL_ID = (byte) 0x01;
    public static final byte TAG_RECEIPT_SEQ = (byte) 0x02;
    public static final byte TAG_TIME = (byte) 0x03;
    public static final byte TAG_FISCAL_SIGN = (byte) 0x04;
    public static final byte TAG_TYPE = (byte) 0x05;
    public static final byte TAG_OPERATION = (byte) 0x06;

    public static final byte TAG_RECEIVED_CASH = (byte) 0x07;
    public static final byte TAG_RECEIVED_CARD = (byte) 0x08;
    public static final byte TAG_TOTAL_VAT = (byte) 0x09;
    public static final byte TAG_ITEMS_COUNT = (byte) 0x0a;
    public static final byte TAG_FISCAL_CIPHER_KEY = (byte) 0x0c;
    public static final byte TAG_EXTRA = (byte) 0x0e;
    public static final byte TAG_ITEMS_HASH = (byte) 0x0f;

    public static void buildTlvTagDescriptions(TlvTagDescriptions parentTlvTagDescriptions, TlvTagDescriptions.OID oid) {
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_TERMINAL_ID, "TerminalID"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_RECEIPT_SEQ, "ReceiptSeq"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_TIME, "Time"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_FISCAL_SIGN, "FiscalSign"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_FISCAL_CIPHER_KEY, "CipherKey"));

        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_ITEMS_HASH, "ItemsHash"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_ITEMS_COUNT, "ItemsCount"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_TYPE, "Type"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_OPERATION, "Operation"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_RECEIVED_CASH, "ReceivedCash"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_RECEIVED_CARD, "ReceivedCard"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_TOTAL_VAT, "TotalVAT"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_EXTRA, "Extra"));
    }

    String terminalID;
    Long receiptSeq;
    Date time;
    byte[] fiscalSign;
    ReceiptType type;
    OperationType operation;
    Long receivedCash;
    Long receivedCard;
    Long totalVAT;
    Short itemsCount;
    byte[] cipherKey;
    byte[] extra;
    byte[] itemsHash;


    @Override
    public void write(OutputStream w) throws IOException {
        if (terminalID != null && !terminalID.isEmpty()) {
            w.write(TLV.encode(TAG_TERMINAL_ID, TerminalID.encode(terminalID)));
        }

        w.write(TLV.encode(TAG_RECEIPT_SEQ, BCD8.fromLong(receiptSeq).getBytes()));
        if (time != null) {
            w.write(TLV.encode(TAG_TIME, BCDDateTime.toBytes(time)));
        }
        if (fiscalSign != null) {
            w.write(TLV.encode(TAG_FISCAL_SIGN, fiscalSign));
        }
        if (type != null) {
            writeByte(TAG_TYPE, type.getValue(), w);
        }
        if (operation != null) {
            writeByte(TAG_OPERATION, operation.getValue(), w);
        }
        if (receivedCash != null) {
            writeLong(TAG_RECEIVED_CASH, receivedCash, w);
        }
        if (receivedCard != null) {
            writeLong(TAG_RECEIVED_CARD, receivedCard, w);
        }
        if (totalVAT != null) {
            writeLong(TAG_TOTAL_VAT, totalVAT, w);
        }
        if (itemsCount != null) {
            writeShort(TAG_ITEMS_COUNT, itemsCount, w);
        }
        if (cipherKey != null) {
            w.write(TLV.encode(TAG_FISCAL_CIPHER_KEY, cipherKey));
        }
        if (extra != null && extra.length > 0) {
            w.write(TLV.encode(TAG_EXTRA, extra));
        }
        if (itemsHash != null) {
            w.write(TLV.encode(TAG_ITEMS_HASH, itemsHash));
        }
    }

    public static ReceiptInfo decode(TVS tvs) throws Exception {
        ReceiptInfo o = new ReceiptInfo();
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
            if (tv.getTag() == TAG_OPERATION) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == 1) {
                            boolean found = false;
                            for (OperationType v : OperationType.values()) {
                                if (v.getValue() == tv.getValue()[0]) {
                                    o.operation = v;
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                throw new IllegalArgumentException(String.format("operation value is illegal"));
                            }
                        } else {
                            throw new IllegalArgumentException(String.format("operation must be 1 byte long"));
                        }
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_RECEIVED_CASH) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == BCD8.SIZE) {
                            o.receivedCash = new BCD8(tv.getValue(), (short) 0, (short) tv.getValue().length).toLong();
                        } else {
                            throw new IllegalArgumentException(String.format("receivedCash must be %d bytes long", BCD8.SIZE));
                        }
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_RECEIVED_CARD) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == BCD8.SIZE) {
                            o.receivedCard = new BCD8(tv.getValue(), (short) 0, (short) tv.getValue().length).toLong();
                        } else {
                            throw new IllegalArgumentException(String.format("receivedCard must be %d bytes long", BCD8.SIZE));
                        }
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_TOTAL_VAT) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == BCD8.SIZE) {
                            o.totalVAT = new BCD8(tv.getValue(), (short) 0, (short) tv.getValue().length).toLong();
                        } else {
                            throw new IllegalArgumentException(String.format("totalVAT must be %d bytes long", BCD8.SIZE));
                        }
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_ITEMS_COUNT) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == 2) {
                            o.itemsCount = Utils.readShort(tv.getValue(), 0);
                        } else {
                            throw new IllegalArgumentException(String.format("itemsCount must be 2 bytes long"));
                        }
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_FISCAL_CIPHER_KEY) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        o.cipherKey = tv.getValue();
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_EXTRA) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        o.extra = tv.getValue();
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_ITEMS_HASH) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        o.itemsHash = tv.getValue();
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

    public byte[] getItemsHash() {
        return itemsHash;
    }

    public void setItemsHash(byte[] itemsHash) {
        this.itemsHash = itemsHash;
    }

    public Short getItemsCount() {
        return itemsCount;
    }

    public void setItemsCount(short itemsCount) {
        this.itemsCount = itemsCount;
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

    public Long getReceivedCash() {
        return receivedCash;
    }

    public void setReceivedCash(long receivedCash) {
        this.receivedCash = receivedCash;
    }

    public Long getReceivedCard() {
        return receivedCard;
    }

    public void setReceivedCard(long receivedCard) {
        this.receivedCard = receivedCard;
    }

    public Long getTotalVAT() {
        return totalVAT;
    }

    public void setTotalVAT(long totalVAT) {
        this.totalVAT = totalVAT;
    }

    public byte[] getExtra() {
        return extra;
    }

    public void setExtra(byte[] extra) {
        this.extra = extra;
    }
}

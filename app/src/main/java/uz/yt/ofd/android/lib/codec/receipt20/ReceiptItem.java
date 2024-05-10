/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.codec.receipt20;

import java.io.IOException;
import java.io.OutputStream;

import uz.yt.ofd.android.lib.codec.TlvTagDescriptions;
import uz.yt.ofd.android.lib.codec.text.TextCodec;
import uz.yt.ofd.android.lib.codec.text.TextCodecV2;
import uz.yt.ofd.android.lib.codec.tlv.TLV;
import uz.yt.ofd.android.lib.codec.tlv.TLVEncodable;

public class ReceiptItem extends TLVEncodable {

    public static final byte TAG_NAME = (byte) 0x01;
    public static final byte TAG_BARCODE = (byte) 0x02;
    public static final byte TAG_LABEL = (byte) 0x03;
    public static final byte TAG_SPIC = (byte) 0x04;
    public static final byte TAG_UNITS = (byte) 0x05;
    public static final byte TAG_PRICE = (byte) 0x06;
    public static final byte TAG_VAT_PERCENT = (byte) 0x07;
    public static final byte TAG_VAT = (byte) 0x08;
    public static final byte TAG_AMOUNT = (byte) 0x09;
    public static final byte TAG_DISCOUNT = (byte) 0x0a;
    public static final byte TAG_OTHER = (byte) 0x0b;
    public static final byte TAG_PACKAGE_CODE = (byte) 0x11;
    public static final byte TAG_OWNER_TYPE = (byte) 0x12;
    public static final byte TAG_COMMISSION_INFO = (byte) 0x81;

    public static void buildTlvTagDescriptions(TlvTagDescriptions parentTlvTagDescriptions, TlvTagDescriptions.OID oid) {
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_NAME, "Name"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_BARCODE, "Barcode"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_LABEL, "Label"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_SPIC, "SPIC"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_UNITS, "Units"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_PRICE, "Price"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_VAT_PERCENT, "VatPercent"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_VAT, "VAT"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_AMOUNT, "Amount"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_DISCOUNT, "Discount"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_OTHER, "Other"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_PACKAGE_CODE, "PackageCode"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_OWNER_TYPE, "OwnerType"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_COMMISSION_INFO, "CommissionInfo"));
        CommissionInfo.buildTlvTagDescriptions(parentTlvTagDescriptions, oid.append(TAG_COMMISSION_INFO, "CommissionInfo"));
    }

    private String name;

    private String barcode;

    private String label;

    private String spic;

    private long units;

    private String packageCode;

    private Byte ownerType;

    private long price;

    private short vatPercent;

    private long vat;

    private long amount;

    private long discount;

    private long other;

    private CommissionInfo commissionInfo;

    @Override
    public void write(OutputStream w) throws IOException {
        if (isEmpty(name)) {
            throw new IllegalArgumentException("field name is not found or empty");
        }

        if (units > ReceiptCodec.BCD8_MAX_LIMIT) {
            throw new IllegalArgumentException(String.format("item units value %d shouldn't exceed %d", units, ReceiptCodec.BCD8_MAX_LIMIT));
        }

        if (price > ReceiptCodec.BCD8_MAX_LIMIT) {
            throw new IllegalArgumentException(String.format("item price value %d shouldn't exceed %d", price, ReceiptCodec.BCD8_MAX_LIMIT));
        }
        if (vatPercent > 99) {
            throw new IllegalArgumentException(String.format("item vat percent value %d shouldn't exceed %d", vatPercent, 99));
        }
        if (vat > ReceiptCodec.BCD8_MAX_LIMIT) {
            throw new IllegalArgumentException(String.format("item vat value %d shouldn't exceed %d", vat, ReceiptCodec.BCD8_MAX_LIMIT));
        }
        if (amount > ReceiptCodec.BCD8_MAX_LIMIT) {
            throw new IllegalArgumentException(String.format("item amount value %d shouldn't exceed %d", amount, ReceiptCodec.BCD8_MAX_LIMIT));
        }
        if (discount > ReceiptCodec.BCD8_MAX_LIMIT) {
            throw new IllegalArgumentException(String.format("item discount value %d shouldn't exceed %d", discount, ReceiptCodec.BCD8_MAX_LIMIT));
        }
        if (other > ReceiptCodec.BCD8_MAX_LIMIT) {
            throw new IllegalArgumentException(String.format("item other value %d shouldn't exceed %d", other, ReceiptCodec.BCD8_MAX_LIMIT));
        }
        if (price < (discount + other)) {
            throw new IllegalArgumentException(String.format("item total discount %d shouldn't exceed item price %d", (discount + other), price));
        }

        TextCodec textCodec = new TextCodecV2();
        writeBytes(TAG_NAME, textCodec.encode(name), ReceiptCodec.STRING_MAX_SIZE, w);
        writeString(TAG_BARCODE, barcode, ReceiptCodec.STRING_MAX_SIZE, w);
        writeString(TAG_LABEL, label, ReceiptCodec.STRING_MAX_SIZE, w);
        writeString(TAG_SPIC, spic, ReceiptCodec.SPIC_MAX_SIZE, w);
        writeLong(TAG_UNITS, units, w);
        writeLong(TAG_PRICE, price, w);
        writeLong(TAG_VAT_PERCENT, (long)vatPercent, w);
        writeLong(TAG_VAT, vat, w);
        writeLong(TAG_AMOUNT, amount, w);
        if (discount != 0) {
            writeLong(TAG_DISCOUNT, discount, w);
        }
        if (other != 0) {
            writeLong(TAG_OTHER, other, w);
        }
        writeString(TAG_PACKAGE_CODE, packageCode, ReceiptCodec.PACKAGE_CODE_MAX_SIZE, w);
        writeByte(TAG_OWNER_TYPE, ownerType, w);
        if (commissionInfo != null) {
            w.write(TLV.encode(TAG_COMMISSION_INFO, commissionInfo.encode()));
        }
    }

    public ReceiptItem(String name, String barcode, String label, String spic, long units, String packageCode, Byte ownerType, long price, short vatPercent, long vat, long amount, long discount, long other, CommissionInfo commissionInfo) {
        this.name = name;
        this.barcode = barcode;
        this.label = label;
        this.spic = spic;
        this.units = units;
        this.packageCode = packageCode;
        this.ownerType = ownerType;
        this.price = price;
        this.vatPercent = vatPercent;
        this.vat = vat;
        this.amount = amount;
        this.discount = discount;
        this.other = other;
        this.commissionInfo = commissionInfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getSpic() {
        return spic;
    }

    public void setSpic(String spic) {
        this.spic = spic;
    }

    public long getUnits() {
        return units;
    }

    public void setUnits(long units) {
        this.units = units;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Byte getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(Byte ownerType) {
        this.ownerType = ownerType;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public short getVatPercent() {
        return vatPercent;
    }

    public void setVatPercent(short vatPercent) {
        this.vatPercent = vatPercent;
    }

    public long getVat() {
        return vat;
    }

    public void setVat(long vat) {
        this.vat = vat;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getDiscount() {
        return discount;
    }

    public void setDiscount(long discount) {
        this.discount = discount;
    }

    public long getOther() {
        return other;
    }

    public void setOther(long other) {
        this.other = other;
    }

    public CommissionInfo getCommissionInfo() {
        return commissionInfo;
    }

    public void setCommissionInfo(CommissionInfo commissionInfo) {
        this.commissionInfo = commissionInfo;
    }

}

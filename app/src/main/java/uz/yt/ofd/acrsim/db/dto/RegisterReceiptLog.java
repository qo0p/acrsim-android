package uz.yt.ofd.acrsim.db.dto;

public class RegisterReceiptLog {
    final Long id;
    final byte receiptVersion;
    final byte receiptType;
    final byte operation;
    final byte[] tlvEncodedReceiptRaw;
    final byte[] totalBlockRaw;

    public RegisterReceiptLog(Long id, byte receiptVersion, byte receiptType, byte operation, byte[] tlvEncodedReceiptRaw, byte[] totalBlockRaw) {
        this.id = id;
        this.receiptVersion = receiptVersion;
        this.receiptType = receiptType;
        this.operation = operation;
        this.tlvEncodedReceiptRaw = tlvEncodedReceiptRaw;
        this.totalBlockRaw = totalBlockRaw;
    }

    public Long getId() {
        return id;
    }

    public byte getReceiptVersion() {
        return receiptVersion;
    }

    public byte getReceiptType() {
        return receiptType;
    }

    public byte getOperation() {
        return operation;
    }

    public byte[] getTlvEncodedReceiptRaw() {
        return tlvEncodedReceiptRaw;
    }

    public byte[] getTotalBlockRaw() {
        return totalBlockRaw;
    }
}

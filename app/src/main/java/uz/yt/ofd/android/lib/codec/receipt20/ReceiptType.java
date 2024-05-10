package uz.yt.ofd.android.lib.codec.receipt20;

public enum ReceiptType {

    Purchase((byte) 0x00),

    Advance((byte) 0x01),

    Credit((byte) 0x02);

    private byte value;

    private ReceiptType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}

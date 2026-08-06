package uz.yt.ofd.android.lib.codec.receipt20;

public enum PaymentType {

    Cash((byte) 0x01),

    Card((byte) 0x02),

    QR((byte) 0x03),

    Mixed((byte) 0x04);

    private byte value;

    private PaymentType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}

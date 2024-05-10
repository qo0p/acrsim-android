package uz.yt.ofd.android.lib.codec.receipt20;

public enum OperationType {

    Sale((byte) 0x00),

    Refund((byte) 0x01);

    private byte value;

    private OperationType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}

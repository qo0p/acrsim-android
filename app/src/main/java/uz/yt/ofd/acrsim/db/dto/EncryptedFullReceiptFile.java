package uz.yt.ofd.acrsim.db.dto;

public class EncryptedFullReceiptFile {

    private final byte[] file;
    private final int version;
    private final int type;

    public EncryptedFullReceiptFile(byte[] file, int version, int type) {
        this.file = file;
        this.version = version;
        this.type = type;
    }

    public byte[] getFile() {
        return file;
    }

    public int getVersion() {
        return version;
    }

    public int getType() {
        return type;
    }
}

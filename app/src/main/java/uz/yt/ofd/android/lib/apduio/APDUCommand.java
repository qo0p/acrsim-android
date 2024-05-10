package uz.yt.ofd.android.lib.apduio;

import uz.yt.ofd.android.lib.codec.HexBin;
import uz.yt.ofd.android.lib.codec.Utils;

public class APDUCommand {

    private final String name;

    private final byte cla;
    private final byte ins;
    private final byte p1;
    private final byte p2;
    private final byte le;
    private final byte[] data;


    public APDUCommand(String name, byte cla, byte ins, byte p1, byte p2) {
        this.name = name;
        this.cla = cla;
        this.ins = ins;
        this.p1 = p1;
        this.p2 = p2;
        this.data = new byte[0];
        this.le = (byte) data.length;
    }


    public APDUCommand(String name, byte cla, byte ins, byte p1, byte p2, byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException("bad data");
        }
        this.name = name;
        this.cla = cla;
        this.ins = ins;
        this.p1 = p1;
        this.p2 = p2;
        this.data = data;
        this.le = (byte) data.length;
    }

    @Override
    public String toString() {
        return String.format("%s\n<< %02x%02x%02x%02x %02x%s", name, cla, ins, p1, p2, le, data.length == 0 ? "" : " " + HexBin.encode(data));
    }


    public byte[] getBytes() {
        return Utils.append(new byte[]{cla, ins, p1, p2, le}, data);
    }
}

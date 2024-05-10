package uz.yt.ofd.android.lib.apduio;

import uz.yt.ofd.android.lib.codec.HexBin;

public class APDUResponse {

    short sw;

    byte[] data;

    public APDUResponse(byte[] response) {
        if (response == null || response.length < 2) {
            throw new IllegalArgumentException("bad apdu response");
        }
        sw = (short) ((response[response.length - 1] & 0xff) | (response[response.length - 2] & 0xff) << 8);
        data = new byte[response.length - 2];
        System.arraycopy(response, 0, data, 0, data.length);
    }

    public short getSw() {
        return sw;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return String.format(">> %s%04x\n", data.length == 0 ? "" : HexBin.encode(data) + " ", sw);
    }
}

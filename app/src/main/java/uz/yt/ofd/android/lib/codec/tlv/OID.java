/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.codec.tlv;

public class OID {

    private byte[] value;

    public OID(byte[] value) {
        this.value = value;
    }

    @Override
    public String toString() {
        if (value == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < value.length; i++) {
            sb.append(String.format("%02x", value[i]));
            if (i + 1 < value.length) {
                sb.append(".");
            }
        }
        return sb.toString();
    }

    public boolean isEmpty() {
        return (value == null) || (value.length == 0);
    }

    public byte[] getValue() {
        return value;
    }

    protected OID copy() {
        byte[] tmp = new byte[value.length];
        System.arraycopy(value, 0, tmp, 0, value.length);
        return new OID(tmp);
    }

}

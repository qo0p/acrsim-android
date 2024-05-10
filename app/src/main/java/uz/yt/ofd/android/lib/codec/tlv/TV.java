/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.codec.tlv;


import uz.yt.ofd.android.lib.codec.HexBin;

public class TV {

    private byte tag;
    private byte[] value;
    private TVS tvs;

    private int from;
    private int to;

    public TV(byte tag, byte[] value, TVS tvs, int from, int to) {
        this.tag = tag;
        this.value = value;
        this.tvs = tvs;
        this.from = from;
        this.to = to;
    }

    public byte getTag() {
        return tag;
    }

    public void setTag(byte tag) {
        this.tag = tag;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public TVS getTvs() {
        return tvs;
    }

    public void setTvs(TVS tvs) {
        this.tvs = tvs;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    @Override
    public String toString() {
        return String.format("Tag: %02x, Len: %7d, Value: %s", tag, (value == null ? 0 : value.length), HexBin.encode(value));
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.decoder;

import uz.yt.ofd.android.lib.codec.DumpDescriptor;
import uz.yt.ofd.android.lib.codec.tlv.TVS;

public abstract class AbstractDecoder<T> {

    protected DumpDescriptor dumpDescriptor;
    protected final String name;
    protected final byte[] data;
    protected TVS tvs;

    //protected final int offset;

    public AbstractDecoder(String name, byte[] data) {
        this.name = name;
        this.data = data;
//        this.offset = 0;
        this.dumpDescriptor = new DumpDescriptor(this.name, this.data);
    }

//    public AbstractDecoder(String name, byte[] data, int offset) {
//        this.name = name;
//        this.data = data;
//        this.offset = offset;
//        this.dumpDescriptor = new DumpDescriptor(this.name, this.data);
//    }

    public DumpDescriptor getDumpDescriptor() {
        return dumpDescriptor;
    }

    public abstract T decode() throws IllegalArgumentException, Exception;

    public byte[] getData() {
        return data;
    }

    public TVS getTvs() {
        return tvs;
    }

//    public int getOffset() {
//        return offset;
//    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.applet.decoder;

import uz.yt.ofd.android.lib.decoder.AbstractDecoder;

public class ByteArrayDecoder extends AbstractDecoder<byte[]> {

    public ByteArrayDecoder(byte[] data) {
        super("BYTE_ARRAY", data);
    }

    @Override
    public byte[] decode() throws IllegalArgumentException, Exception {
        return data;
    }

}

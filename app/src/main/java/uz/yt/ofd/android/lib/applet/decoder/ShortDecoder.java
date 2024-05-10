/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.applet.decoder;

import uz.yt.ofd.android.lib.decoder.AbstractDecoder;

public abstract class ShortDecoder extends AbstractDecoder<Short> {

    public ShortDecoder(String name, byte[] data) {
        super(name, data);
    }

    @Override
    public Short decode() throws IllegalArgumentException, Exception {
        return dumpDescriptor.readShort("Short", 0);
    }

}

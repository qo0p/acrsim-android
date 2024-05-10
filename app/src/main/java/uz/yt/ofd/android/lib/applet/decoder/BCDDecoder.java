/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.applet.decoder;

import uz.yt.ofd.android.lib.decoder.AbstractDecoder;

public abstract class BCDDecoder extends AbstractDecoder<Long> {

    public BCDDecoder(String name, byte[] data) {
        super(name, data);
    }

    @Override
    public Long decode() throws IllegalArgumentException, Exception {
        return dumpDescriptor.readBCD("BCD", 0, data.length);
    }

}

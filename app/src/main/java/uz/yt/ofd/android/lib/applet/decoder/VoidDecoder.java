/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.applet.decoder;

import uz.yt.ofd.android.lib.decoder.AbstractDecoder;

public class VoidDecoder extends AbstractDecoder<Void> {

    public VoidDecoder(byte[] data) {
        super("VOID", data);
    }

    @Override
    public Void decode() throws IllegalArgumentException, Exception {
        return null;
    }

}

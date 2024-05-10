/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.applet.decoder;


import uz.yt.ofd.android.lib.decoder.AbstractDecoder;

public class VersionDecoder extends AbstractDecoder<String> {

    public VersionDecoder(byte[] data) {
        super("VERSION", data);
    }

    @Override
    public String decode() throws IllegalArgumentException, Exception {
        return dumpDescriptor.readHex("Version", 0, 2);
    }

}

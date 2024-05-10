/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.applet.decoder;

import java.util.Date;

import uz.yt.ofd.android.lib.decoder.AbstractDecoder;

public class DateDecoder extends AbstractDecoder<Date> {

    public DateDecoder(byte[] data) {
        super("DATE_TIME", data);
    }

    @Override
    public Date decode() throws IllegalArgumentException, Exception {
        return dumpDescriptor.readDate("DateTime", 0);
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.codec;

import java.io.IOException;
import java.io.InputStream;

/**
 * CRC32 вычислитель потока ввода
 *
 * @author administrator
 */
public class Crc32cInputStream extends InputStream {

    private final InputStream is;
    private final Crc32c crc;

    public Crc32cInputStream(InputStream is) {
        this.is = is;
        this.crc = new Crc32c();
    }

    @Override
    public int read() throws IOException {
        int i = is.read();
        crc.update((byte) i);
        return i;
    }

    public long getValue() {
        return crc.getValue();
    }

    public void reset() {
        crc.reset();
    }

}

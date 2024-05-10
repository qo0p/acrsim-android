/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.applet.decoder;

import uz.yt.ofd.android.lib.applet.dto.UnackowledgedZReportsIndexes;
import uz.yt.ofd.android.lib.decoder.AbstractDecoder;

public class UnackowledgedZReportsIndexesDecoder extends AbstractDecoder<UnackowledgedZReportsIndexes> {

    public UnackowledgedZReportsIndexesDecoder(byte[] data) {
        super("ZREPORTS_INDEXES", data);
    }

    @Override
    public UnackowledgedZReportsIndexes decode() {
        if (data.length < 2 || data.length > (2 + 16 * 2)) {
            throw new IllegalArgumentException("bad zreports indexes data length");
        }
        UnackowledgedZReportsIndexes indexes = new UnackowledgedZReportsIndexes();
        indexes.setCount(dumpDescriptor.readShort("Count", 0));
        indexes.setIndexes(new short[indexes.getCount()]);
        for (int k = 0; k < indexes.getCount(); k++) {
            indexes.getIndexes()[k] = dumpDescriptor.readShort(String.format("Index %d", k), 2 + 2 * k);
        }
        return indexes;
    }
}

package uz.yt.ofd.acrsim;

import uz.yt.ofd.android.lib.apduio.APDUIO;

public interface SAMSlot extends APDUIO {

    byte getNumber();

    byte[] connect() throws Exception;

    void close() throws Exception;
}

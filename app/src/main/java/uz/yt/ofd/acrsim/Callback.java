package uz.yt.ofd.acrsim;

import uz.yt.ofd.android.lib.apduio.APDUIO;

public interface Callback {

    void run(APDUIO apduio, byte[] CPLC) throws Exception;
}

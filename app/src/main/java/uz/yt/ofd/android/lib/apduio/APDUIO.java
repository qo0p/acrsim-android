package uz.yt.ofd.android.lib.apduio;

public interface APDUIO {

    APDUResponse transmit(APDUCommand command) throws Exception;
}

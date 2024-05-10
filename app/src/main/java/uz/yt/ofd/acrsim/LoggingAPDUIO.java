package uz.yt.ofd.acrsim;

import android.util.Log;

import uz.yt.ofd.android.lib.apduio.APDUCommand;
import uz.yt.ofd.android.lib.apduio.APDUIO;
import uz.yt.ofd.android.lib.apduio.APDUResponse;

public class LoggingAPDUIO implements APDUIO {

    private final APDUIO apduio;

    public LoggingAPDUIO(APDUIO apduio) {
        this.apduio = apduio;
    }

    @Override
    public APDUResponse transmit(APDUCommand command) throws Exception {
        String cmdStr = command.toString();
        try {
            APDUResponse response = apduio.transmit(command);
            System.out.println(cmdStr + "\n" + response.toString());
            return response;
        } catch (Throwable t) {
            System.out.println(cmdStr + "\n!! " + (t.getMessage() == null ? t.getClass().getSimpleName().replace("Exception", "") : t.getMessage()));
            throw t;
        }
    }
}

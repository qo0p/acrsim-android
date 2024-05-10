/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.applet.command;

import uz.yt.ofd.android.lib.apduio.APDUCommand;
import uz.yt.ofd.android.lib.apduio.APDUIO;
import uz.yt.ofd.android.lib.apduio.APDUResponse;
import uz.yt.ofd.android.lib.applet.SW;
import uz.yt.ofd.android.lib.decoder.AbstractDecoder;
import uz.yt.ofd.android.lib.exception.SWException;

public abstract class AbstractCommand<T extends AbstractDecoder> {

    public T parseResponse(APDUResponse response, Class<T> cls) throws SWException, Exception {
        if (response.getSw() != SW.NO_ERROR.code) {
            throw new SWException(response.getSw());
        }
        Class c0 = byte[].class;
        return cls.getDeclaredConstructor(c0).newInstance(response.getData());
    }

    public T run(APDUIO apduio, Class<T> cls) throws Exception {
        return parseResponse(apduio.transmit(makeCommand()), cls);
    }

    public abstract APDUCommand makeCommand();
}

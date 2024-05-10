package uz.yt.ofd.acrsim.develop;

import android.os.StrictMode;
import android.util.Log;

import java.io.EOFException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import uz.yt.ofd.acrsim.SAMSlot;
import uz.yt.ofd.android.lib.apduio.APDUCommand;
import uz.yt.ofd.android.lib.apduio.APDUResponse;

public class FiscalDriveEmulator implements SAMSlot {
    static int TIMEOUT = 15000;
    final InetSocketAddress address;
    Socket socket = null;
    OutputStream os;
    InputStream is;

    static {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public FiscalDriveEmulator(InetSocketAddress address) {
        this.address = address;
    }


    @Override
    public byte getNumber() {
        return 0;
    }

    private byte[] readSizeData() throws Exception {
        int sz = is.read();
        if (sz == -1) {
            throw new EOFException();
        }
        byte[] buf = new byte[sz];
        int i = 0;
        while (i < sz) {
            int b = is.read();
            if (b == -1) {
                throw new EOFException();
            }
            buf[i] = (byte) b;
            i++;
        }
        return buf;
    }

    private void writeSizeData(byte[] data) throws Exception {
        int sz = data.length;
        os.write(new byte[]{(byte) sz});
        os.write(data);
    }

    @Override
    public byte[] connect() throws Exception {
        if (socket != null) {
            try {
                socket.close();
            } catch (Throwable t) {
                Log.w("remote sam slot", t);
            }
        }
        socket = new Socket();
        socket.setSoTimeout(TIMEOUT);
        socket.setSoTimeout(TIMEOUT);
        socket.connect(address);

        os = socket.getOutputStream();
        is = socket.getInputStream();
        return readSizeData();
    }

    @Override
    public void close() throws Exception {
        if (socket != null) {
            try {
                socket.close();
            } catch (Throwable t) {
                Log.w("remote sam slot", t);
            }
        }
    }

    @Override
    public APDUResponse transmit(APDUCommand command) throws Exception {
        writeSizeData(command.getBytes());
        return new APDUResponse(readSizeData());
    }
}

package uz.yt.ofd.acrsim.sender;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import uz.yt.ofd.acrsim.sender.dto.SyncItem;
import uz.yt.ofd.android.lib.codec.HexBin;
import uz.yt.ofd.android.lib.codec.message6.AckFile;
import uz.yt.ofd.android.lib.codec.message6.AckFileStatus;
import uz.yt.ofd.android.lib.codec.message6.File;
import uz.yt.ofd.android.lib.codec.message6.FileType;
import uz.yt.ofd.android.lib.codec.message6.Message;
import uz.yt.ofd.android.lib.codec.message6.Request;
import uz.yt.ofd.android.lib.codec.message6.Response;
import uz.yt.ofd.android.lib.codec.message6.SenderInfo;
import uz.yt.ofd.android.lib.codec.message6.StatusCode;
import uz.yt.ofd.android.lib.codec.message6.StatusInfo;
import uz.yt.ofd.android.lib.codec.tlv.TLV;
import uz.yt.ofd.android.lib.codec.tlv.TVS;

public class TCPSender implements Sender {

    static final byte MESSAGE_VERSION = (byte) 6;

    private List<String> serverAddresses;
    private int connectTimeout;
    private final SenderInfo senderInfo;
    private int currentAddress;

    private final ReentrantLock lock;
    private List<Throwable> errors = new LinkedList();

    public TCPSender(List<String> serverAddresses, int connectTimeout, SenderInfo senderInfo) {
        this.serverAddresses = serverAddresses;
        this.currentAddress = -1;
        this.connectTimeout = connectTimeout;
        this.senderInfo = senderInfo;
        this.lock = new ReentrantLock();
    }

    @Override
    public byte[] SyncState(String terminalID, String challenge) throws Exception {
        lock.lock();
        try {
            ByteArrayOutputStream header = new ByteArrayOutputStream();
            header.write(HexBin.decode(challenge));

            Request req = new Request(terminalID, new Date(), senderInfo, new File[]{
                    new File(FileType.SyncStateQuery.value, (byte) 1, header.toByteArray(), null)
            });

            Object[] o = forceSync(req);

            Message msgRes = (Message) o[0];
            Response res = (Response) o[1];

            StatusInfo statusInfo = res.getStatusInfo();

            switch (statusInfo.getStatusCode()) {
                case OK:
                case OKNotice:
                    break;
                case NotActive:
                    throw new IOException("Fiscal drive " + terminalID + " is not active, notice: " + statusInfo.getNotice() + ", reasonCode:" + statusInfo.getReasonCode());
                case NotFound:
                    throw new IOException("Fiscal drive " + terminalID + " is not found");
                default:
                    throw new IOException("Try later");
            }

            if (res.getAckFiles().length != 1) {
                throw new IOException("Try later");
            }
            AckFile af = res.getAckFiles()[0];
            if (!af.getStatus().equals(AckFileStatus.Acknowledge)) {
                throw new IOException("Try later");
            }
            return af.getBody();
        } catch (Throwable t) {
            throw new IOException(t.getMessage(), t);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean check(String terminalID, byte[] receiptSeqRaw, byte[] dateTimeRaw, byte[] fiscalSignRaw) throws IOException {
        lock.lock();
        try {
            ByteArrayOutputStream header = new ByteArrayOutputStream();
            header.write(receiptSeqRaw);
            header.write(dateTimeRaw);
            header.write(fiscalSignRaw);

            Request req = new Request(terminalID, new Date(), senderInfo, new File[]{
                    new File(FileType.VerifyFiscalSignQuery.value, (byte) 1, header.toByteArray(), null)
            });

            Object[] o = forceSync(req);

            Message msgRes = (Message) o[0];
            Response res = (Response) o[1];

            StatusInfo statusInfo = res.getStatusInfo();

            switch (statusInfo.getStatusCode()) {
                case OK:
                case OKNotice:
                    break;
                case NotActive:
                    throw new IOException("Fiscal drive " + terminalID + " is not active, notice: " + statusInfo.getNotice() + ", reasonCode:" + statusInfo.getReasonCode());
                case NotFound:
                    throw new IOException("Fiscal drive " + terminalID + " is not found");
                default:
                    throw new IOException("Try later");
            }

            return res.getAckFiles()[0].getStatus() == AckFileStatus.Acknowledge;
        } catch (Throwable t) {
            throw new IOException(t.getMessage(), t);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public StatusInfo SyncItems(String terminalID, List<SyncItem> syncItems) throws Exception {
        if (!lock.tryLock()) {
            throw new Exception("sync is already in progress");
        }
        try {
            errors.clear();
            StatusInfo statusInfo = null;
            if (syncItems.isEmpty()) {
                return statusInfo;
            }

            List<File> sendFiles = new LinkedList();
            for (SyncItem item : syncItems) {
                sendFiles.add(item.getFile());
            }
            Request req = new Request(terminalID, new Date(), senderInfo, sendFiles.toArray(new File[0]));

            Object[] o = forceSync(req);

            Message msgRes = (Message) o[0];
            Response res = (Response) o[1];

            statusInfo = res.getStatusInfo();
            if (!statusInfo.getStatusCode().equals(StatusCode.OK) && !statusInfo.getStatusCode().equals(StatusCode.OKNotice)) {
                return statusInfo;
            }
            for (int i = 0; i < req.getFiles().length; i++) {
                AckFile af = res.getAckFiles()[i];
                syncItems.get(i).setAckFile(af);
            }
            return statusInfo;
        } finally {
            lock.unlock();
        }
    }

    private Object[] forceSync(Request req) throws Exception {
        byte[] reqTlvBodyRaw = req.encode();



        byte messageVersion = MESSAGE_VERSION;

        Message msgReq = new Message(messageVersion, Message.TAG_REQUEST, reqTlvBodyRaw);

        Response res = null;

        currentAddress %= serverAddresses.size();
        int tryCount = serverAddresses.size();
        Message msgRes = null;
        while (tryCount > 0) {
            String serverAddress = serverAddresses.get(currentAddress);
            String host = serverAddress.substring(0, serverAddress.indexOf(":"));
            int port = Integer.parseInt(serverAddress.substring(serverAddress.indexOf(":") + 1));
            msgRes = trySync(host, port, msgReq);
            if (msgRes == null) {
                currentAddress++;
                currentAddress %= serverAddresses.size();
                tryCount--;
                continue;
            }
            try {
                TVS tvs = TLV.decode(msgRes.getTlvBody());
                res = Response.decode(tvs);
                if (res.getAckFiles().length != req.getFiles().length) {
                    throw new IllegalStateException(String.format("request files count %d is not equal response files count %d", req.getFiles().length, res.getAckFiles().length));
                }
                break;
            } catch (Throwable t) {
                errors.add(t);
                currentAddress++;
                currentAddress %= serverAddresses.size();
                tryCount--;
                res = null;
                msgRes = null;
                continue;
            }
        }
        if (msgRes == null) {
            String message = "";
            if (errors.size() > 0) {
                message = ": " + errors.get(0).getMessage();
            }
            throw new Exception("failed to sync" + message);
        }
        return new Object[]{msgRes, res};
    }

    public List<Throwable> getErrors() {
        return errors;
    }

    public void setServerAddresses(List<String> serverAddresses) {
        lock.lock();
        try {
            this.serverAddresses = serverAddresses;
        } finally {
            lock.unlock();
        }
    }

    Message trySync(String host, int port, Message msgReq) {

        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(connectTimeout);
            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();


            msgReq.write(os);
            Message msgRes = Message.read(is);

            if (msgRes.getTlvTag() != Message.TAG_RESPONSE) {
                throw new IllegalArgumentException("bad response message tag");
            }


            errors.clear();
            return msgRes;
        } catch (Throwable t) {
            errors.add(t);
        }
        return null;

    }

}


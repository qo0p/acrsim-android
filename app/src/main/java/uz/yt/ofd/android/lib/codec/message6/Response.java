package uz.yt.ofd.android.lib.codec.message6;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import uz.yt.ofd.android.lib.codec.TlvTagDescriptions;
import uz.yt.ofd.android.lib.codec.tlv.SingleTagReader;
import uz.yt.ofd.android.lib.codec.tlv.TLVEncodable;
import uz.yt.ofd.android.lib.codec.tlv.TV;
import uz.yt.ofd.android.lib.codec.tlv.TVS;

/**
 * Ответ на запрос
 *
 * @author administrator
 */
public class Response extends TLVEncodable {


    public static final byte TAG_STATUS_INFO = (byte) 0x81;
    public static final byte TAG_ACK_FILE = (byte) 0x8e;

    public static void buildTlvTagDescriptions(TlvTagDescriptions parentTlvTagDescriptions, TlvTagDescriptions.OID oid) {
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_STATUS_INFO, "StatusInfo"));
        StatusInfo.buildTlvTagDescriptions(parentTlvTagDescriptions, oid.append(TAG_STATUS_INFO, "StatusInfo"));

        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_ACK_FILE, "AckFile"));
        AckFile.buildTlvTagDescriptions(parentTlvTagDescriptions, oid.append(TAG_ACK_FILE, "AckFile"));
    }

    private StatusInfo statusInfo;

    private AckFile[] ackFiles;

    public Response() {
    }

    public Response(StatusInfo statusInfo, AckFile[] ackFiles) {
        this.statusInfo = statusInfo;
        this.ackFiles = ackFiles;
    }

    public StatusInfo getStatusInfo() {
        return statusInfo;
    }

    public void setStatusInfo(StatusInfo statusInfo) {
        this.statusInfo = statusInfo;
    }

    public AckFile[] getAckFiles() {
        return ackFiles;
    }

    public void setAckFiles(AckFile[] ackFiles) {
        this.ackFiles = ackFiles;
    }

    /**
     * Закодировать в TLV-структуру
     *
     * @param w поток записи
     * @throws Exception ошибка при кодировании
     */
    @Override
    public void write(OutputStream w) throws IOException {
        writeTlvEncodable(TAG_STATUS_INFO, statusInfo, w);
        for (AckFile af : ackFiles) {
            writeTlvEncodable(TAG_ACK_FILE, af, w);
        }
    }

    public static Response decode(TVS tvs) throws Exception {
        Response o = new Response();
        List<AckFile> list = new LinkedList();
        SingleTagReader str = new SingleTagReader();
        for (TV tv : tvs) {
            if (tv.getTag() == TAG_STATUS_INFO) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        o.statusInfo = StatusInfo.decode(tv.getTvs());
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_ACK_FILE) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        list.add(AckFile.decode(tv.getTvs()));
                        return false;
                    }
                });
            }
        }
        o.setAckFiles(list.toArray(new AckFile[0]));
        return o;
    }

}

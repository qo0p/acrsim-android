package uz.yt.ofd.android.lib.codec.message6;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import uz.yt.ofd.android.lib.codec.BCDDateTime;
import uz.yt.ofd.android.lib.codec.TerminalID;
import uz.yt.ofd.android.lib.codec.TlvTagDescriptions;
import uz.yt.ofd.android.lib.codec.tlv.SingleTagReader;
import uz.yt.ofd.android.lib.codec.tlv.TLVEncodable;
import uz.yt.ofd.android.lib.codec.tlv.TV;
import uz.yt.ofd.android.lib.codec.tlv.TVS;

/**
 * Запрос на отправку файлов
 *
 * @author administrator
 */
public class Request extends TLVEncodable {

    public static final byte TAG_TERMINAL_ID = (byte) 0x01;
    public static final byte TAG_LOCAL_TIME = (byte) 0x02;
    public static final byte TAG_SENDER_INFO = (byte) 0x81;
    public static final byte TAG_FILE = (byte) 0x8f;

    public static void buildTlvTagDescriptions(TlvTagDescriptions parentTlvTagDescriptions, TlvTagDescriptions.OID oid) {
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_TERMINAL_ID, "TerminalID"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_LOCAL_TIME, "LocalTime"));

        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_SENDER_INFO, "SenderInfo"));
        SenderInfo.buildTlvTagDescriptions(parentTlvTagDescriptions, oid.append(TAG_SENDER_INFO, "SenderInfo"));

        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_FILE, "File"));
        File.buildTlvTagDescriptions(parentTlvTagDescriptions, oid.append(TAG_FILE, "File"));
    }

    private String terminalID;
    private Date localTime;
    private SenderInfo senderInfo;
    private File[] files;

    public Request() {
    }

    /**
     * Создать запрос на отправку файлов
     *
     * @param terminalID TerminalID
     * @param localTime  дата-время устройства не момент отправки
     * @param senderInfo информация об отправителе
     * @param files      файлы для отправки
     */
    public Request(String terminalID, Date localTime, SenderInfo senderInfo, File[] files) {
        this.terminalID = terminalID;
        this.localTime = localTime;
        this.senderInfo = senderInfo;
        this.files = files;
    }


    public static Request decode(TVS tvs) throws Exception {
        Request o = new Request();
        List<File> list = new LinkedList();
        SingleTagReader str = new SingleTagReader();
        for (TV tv : tvs) {
            if (tv.getTag() == TAG_TERMINAL_ID) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        o.terminalID = TerminalID.decode(tv.getValue());
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_LOCAL_TIME) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        o.localTime = BCDDateTime.fromBytes(tv.getValue(), (short) 0);
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_SENDER_INFO) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        o.senderInfo = SenderInfo.decode(tv.getTvs());
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_FILE) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        list.add(File.decode(tv.getTvs()));
                        return false;
                    }
                });
            }
        }
        o.setFiles(list.toArray(new File[0]));
        return o;
    }

    /**
     * @return TerminalID
     */
    public String getTerminalID() {
        return terminalID;
    }

    /**
     * @param terminalID TerminalID
     */
    public void setTerminalID(String terminalID) {
        this.terminalID = terminalID;
    }

    /**
     * @return дата-время устройства не момент отправки
     */
    public Date getLocalTime() {
        return localTime;
    }

    /**
     * @param localTime дата-время устройства не момент отправки
     */
    public void setLocalTime(Date localTime) {
        this.localTime = localTime;
    }

    /**
     * @return информация об отправителе
     */
    public SenderInfo getSenderInfo() {
        return senderInfo;
    }

    /**
     * @param senderInfo информация об отправителе
     */
    public void setSenderInfo(SenderInfo senderInfo) {
        this.senderInfo = senderInfo;
    }

    /**
     * @return файлы для отправки
     */
    public File[] getFiles() {
        return files;
    }

    /**
     * @param files файлы для отправки
     */
    public void setFiles(File[] files) {
        this.files = files;
    }

    /**
     * Закодировать в TLV-структуру
     *
     * @param w поток записи
     * @throws Exception ошибка при кодировании
     */
    @Override
    public void write(OutputStream w) throws IOException {
        if (terminalID != null && !terminalID.isEmpty()) {
            writeBytes(TAG_TERMINAL_ID, TerminalID.encode(terminalID), 8, w);
        }
        writeDate(TAG_LOCAL_TIME, localTime, w);
        writeTlvEncodable(TAG_SENDER_INFO, senderInfo, w);
        for (File f : files) {
            writeTlvEncodable(TAG_FILE, f, w);
        }
    }

}

package uz.yt.ofd.acrsim.sender.dto;

import uz.yt.ofd.android.lib.codec.message6.AckFile;
import uz.yt.ofd.android.lib.codec.message6.File;

public abstract class SyncItem {

    private AckFile ackFile;

    public abstract Short getIndex();

    public abstract File getFile();

    public void setAckFile(AckFile ackFile) {
        this.ackFile = ackFile;
    }

    public AckFile getAckFile() {
        return ackFile;
    }
}

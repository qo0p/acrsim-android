package uz.yt.ofd.acrsim.sender.dto;

import uz.yt.ofd.android.lib.applet.dto.ZReportFile;
import uz.yt.ofd.android.lib.codec.message6.File;
import uz.yt.ofd.android.lib.codec.message6.FileType;

public class ZReportSyncItem extends SyncItem {

    final ZReportFile file;

    public ZReportSyncItem(ZReportFile file) {
        this.file = file;
    }

    @Override
    public Short getIndex() {
        return file.getIndex();
    }

    @Override
    public File getFile() {
        return new File(FileType.ZReport.value, ZReportFile.VERSION,  file.getFile(),null);
    }
}

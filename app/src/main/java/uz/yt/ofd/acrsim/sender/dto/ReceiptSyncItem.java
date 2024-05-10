package uz.yt.ofd.acrsim.sender.dto;

import uz.yt.ofd.acrsim.db.dto.EncryptedFullReceiptFile;
import uz.yt.ofd.android.lib.applet.dto.ReceiptFile;
import uz.yt.ofd.android.lib.codec.message6.File;
import uz.yt.ofd.android.lib.codec.message6.FileType;

public class ReceiptSyncItem extends SyncItem {

    final ReceiptFile header;
    final EncryptedFullReceiptFile body;

    public ReceiptSyncItem(ReceiptFile header, EncryptedFullReceiptFile body) {
        this.header = header;
        this.body = body;
    }

    public File getFile() {
        FileType t = FileType.ShortReceipt;
        byte v = ReceiptFile.VERSION;
        byte[] h = header.getFile();
        byte[] b = null;
        if(body != null){
            switch (header.getType()){
                case Purchase:
                    t = FileType.PurchaseReceipt;
                    break;
                case Advance:
                    t = FileType.AdvanceReceipt;
                    break;
                case Credit:
                    t = FileType.CreditReceipt;
                    break;
            }
            v = (byte) body.getVersion();
            b = body.getFile();
        }
        return new File(t.value, v, h, b);
    }
}

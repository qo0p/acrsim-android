package uz.yt.ofd.acrsim.sender;

import java.util.List;
import java.util.Map;

import uz.yt.ofd.acrsim.sender.dto.SyncItem;
import uz.yt.ofd.android.lib.codec.message6.StatusInfo;
import uz.yt.ofd.android.lib.validator.FiscalSignValidator;

public interface Sender extends FiscalSignValidator {

    StatusInfo SyncItems(String teminalID, List<SyncItem> syncItems) throws Exception;
    byte[] SyncState(String teminalID, String challenge) throws Exception;
}

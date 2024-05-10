package uz.yt.ofd.acrsim.db;

import java.util.Date;

import uz.yt.ofd.acrsim.db.dto.EncryptedFullReceiptFile;
import uz.yt.ofd.acrsim.db.dto.RegisterReceiptLog;

public interface Storage {

    Long newReceiptRegisterLog(String factoryID, byte receiptVersion, byte receiptType, byte operation, byte[] tlvEncodedReceiptRaw, byte[] totalBlockRaw) throws Exception;

    RegisterReceiptLog getReceiptRegisterLog(String factoryID, Long id) throws Exception;

    void updateReceiptRegisterLog(String factoryID, Long id, byte[] fiscalSignRaw, String terminalID, Long receiptSeq) throws Exception;

    void updateReceiptRegisterLog(String factoryID, Long id, String errorMessage) throws Exception;

    void newFullReceipt(String factoryID, String terminalID, Long receiptSeq, Date time, byte receiptVersion, byte receiptType, byte operation, byte[] file, Long id) throws Exception;

    EncryptedFullReceiptFile getFullReceipt(String factoryID, String terminalID, Long receiptSeq) throws Exception;
}

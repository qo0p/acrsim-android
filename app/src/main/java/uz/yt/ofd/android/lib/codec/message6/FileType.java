/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.codec.message6;

/**
 * Тип файла
 *
 * @author administrator
 */
public enum FileType {
    PurchaseReceipt((byte) 1),
    ZReport((byte) 2),
    ShortReceipt((byte) 3),
    AdvanceReceipt((byte) 4),
    CreditReceipt((byte) 5),

    PurchaseReceiptAck((byte) 21),
    ZReportAck((byte) 22),
    ShortReceiptAck((byte) 23),
    AdvanceReceiptAck((byte) 24),
    CreditReceiptAck((byte) 25),

    VerifyFiscalSignQuery((byte) 200),
    SyncStateQuery((byte) 201);

    public final byte value;

    FileType(byte value) {
        this.value = value;
    }

    public static FileType find(byte value) {
        for (FileType t : values()) {
            if (t.value == value) {
                return t;
            }
        }
        return null;
    }
}

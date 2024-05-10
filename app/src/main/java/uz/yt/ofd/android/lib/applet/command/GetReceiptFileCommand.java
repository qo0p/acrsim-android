/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.applet.command;

import uz.yt.ofd.android.lib.apduio.APDUCommand;
import uz.yt.ofd.android.lib.applet.Instruction;
import uz.yt.ofd.android.lib.applet.decoder.ReceiptFileDecoder;
import uz.yt.ofd.android.lib.codec.BCD8;

public class GetReceiptFileCommand extends AbstractCommand<ReceiptFileDecoder> {

    private final boolean byIndex;
    private final short index;
    private final Long receiptSeq;

    public GetReceiptFileCommand(boolean byIndex, short index, Long receiptSeq) {
        this.byIndex = byIndex;
        this.index = index;
        this.receiptSeq = receiptSeq;
    }

    @Override
    public APDUCommand makeCommand() {
        return new APDUCommand("---get receipt file---", (byte) 0x00, Instruction.INS_GET_RECEIPT_FILE, (byte) (index >> 8), (byte) (index & 0xff), byIndex ? new byte[0] : BCD8.fromLong(receiptSeq).getBytes());
    }

}

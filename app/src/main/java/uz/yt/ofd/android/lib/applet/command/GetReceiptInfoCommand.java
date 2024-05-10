/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.applet.command;

import uz.yt.ofd.android.lib.apduio.APDUCommand;
import uz.yt.ofd.android.lib.applet.Instruction;
import uz.yt.ofd.android.lib.applet.decoder.ReceiptInfoDecoder;


public class GetReceiptInfoCommand extends AbstractCommand<ReceiptInfoDecoder> {

    private final short index;
    private final byte[] tags;

    public GetReceiptInfoCommand(short index, byte[] tags) {
        this.index = index;
        this.tags = tags;
    }

    @Override
    public APDUCommand makeCommand() {
        return new APDUCommand("---get receipt info---", (byte) 0x00, Instruction.INS_GET_RECEIPT_INFO, (byte) (index >> 8), (byte) (index & 0xff), this.tags == null ? new byte[0] : this.tags);
    }

}

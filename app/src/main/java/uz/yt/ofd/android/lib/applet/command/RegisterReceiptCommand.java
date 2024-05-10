package uz.yt.ofd.android.lib.applet.command;

import uz.yt.ofd.android.lib.apduio.APDUCommand;
import uz.yt.ofd.android.lib.applet.Instruction;
import uz.yt.ofd.android.lib.applet.decoder.FiscalSignInfoDecoder;

public class RegisterReceiptCommand extends AbstractCommand<FiscalSignInfoDecoder> {

    byte[] totalBlock;

    public RegisterReceiptCommand(byte[] totalBlock) {
        this.totalBlock = totalBlock;
    }

    @Override
    public APDUCommand makeCommand() {
        return new APDUCommand("---register receipt---", (byte) 0x00, Instruction.GET_REGISTER_RECEIPT[0], Instruction.GET_REGISTER_RECEIPT[1], Instruction.GET_REGISTER_RECEIPT[2], totalBlock);
    }
}

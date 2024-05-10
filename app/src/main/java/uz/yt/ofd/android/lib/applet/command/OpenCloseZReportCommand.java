/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.applet.command;

import java.util.Date;
import uz.yt.ofd.android.lib.apduio.APDUCommand;
import uz.yt.ofd.android.lib.applet.Instruction;
import uz.yt.ofd.android.lib.applet.decoder.VoidDecoder;
import uz.yt.ofd.android.lib.codec.BCDDateTime;

public class OpenCloseZReportCommand extends AbstractCommand<VoidDecoder> {

    private final boolean open;
    private final Date time;

    public OpenCloseZReportCommand(boolean open, Date time) {
        this.open = open;
        this.time = time;
    }

    @Override
    public APDUCommand makeCommand() {
        if (open) {
            return new APDUCommand("---open zreport---", (byte) 0x00, Instruction.OPEN_ZREPORT[0], Instruction.OPEN_ZREPORT[1], Instruction.OPEN_ZREPORT[2], BCDDateTime.toBytes(time));
        } else {
            return new APDUCommand("---close zreport---", (byte) 0x00, Instruction.CLOSE_ZREPORT[0], Instruction.CLOSE_ZREPORT[1], Instruction.CLOSE_ZREPORT[2], BCDDateTime.toBytes(time));
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.applet.command;

import uz.yt.ofd.android.lib.apduio.APDUCommand;
import uz.yt.ofd.android.lib.applet.Instruction;
import uz.yt.ofd.android.lib.applet.decoder.ZReportFileDecoder;

public class GetZReportFileCommand extends AbstractCommand<ZReportFileDecoder> {

    private final short index;

    public GetZReportFileCommand(short index) {
        this.index = index;
    }

    @Override
    public APDUCommand makeCommand() {
        return new APDUCommand("---get zreport file---", (byte) 0x00, Instruction.INS_GET_ZREPORT_FILE, (byte) (index >> 8), (byte) (index & 0xff));
    }
}

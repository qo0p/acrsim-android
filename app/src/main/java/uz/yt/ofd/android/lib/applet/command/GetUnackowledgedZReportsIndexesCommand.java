/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.applet.command;

import uz.yt.ofd.android.lib.apduio.APDUCommand;
import uz.yt.ofd.android.lib.applet.Instruction;
import uz.yt.ofd.android.lib.applet.decoder.UnackowledgedZReportsIndexesDecoder;

public class GetUnackowledgedZReportsIndexesCommand extends AbstractCommand<UnackowledgedZReportsIndexesDecoder> {

    @Override
    public APDUCommand makeCommand() {
        return new APDUCommand("---get unacknowledged zreport indexes---", (byte) 0x00, Instruction.GET_UNACKNOWLEDGED_ZREPORTS_INDEXES[0], Instruction.GET_UNACKNOWLEDGED_ZREPORTS_INDEXES[1], Instruction.GET_UNACKNOWLEDGED_ZREPORTS_INDEXES[2]);
    }
}

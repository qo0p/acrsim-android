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

public class POSLockCommand extends AbstractCommand<VoidDecoder> {

    private final byte[] secret;

    public POSLockCommand(byte[] secret) {
        this.secret = secret;
    }

    @Override
    public APDUCommand makeCommand() {
        return new APDUCommand("---pos lock---", (byte) 0x00, Instruction.POS_LOCK[0], Instruction.POS_LOCK[1], Instruction.POS_LOCK[2], secret);
    }
}

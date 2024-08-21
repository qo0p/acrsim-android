/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.applet.command;

import uz.yt.ofd.android.lib.apduio.APDUCommand;
import uz.yt.ofd.android.lib.applet.Instruction;
import uz.yt.ofd.android.lib.applet.decoder.VoidDecoder;

public class POSAuthCommand extends AbstractCommand<VoidDecoder> {

    private final byte[] auth;

    public POSAuthCommand(byte[] auth) {
        this.auth = auth;
    }

    @Override
    public APDUCommand makeCommand() {
        return new APDUCommand("---pos auth---", (byte) 0x00, Instruction.POS_AUTH[0], Instruction.POS_AUTH[1], Instruction.POS_AUTH[2], auth);
    }
}

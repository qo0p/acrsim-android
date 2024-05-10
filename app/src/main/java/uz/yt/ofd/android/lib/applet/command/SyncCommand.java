/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.applet.command;

import uz.yt.ofd.android.lib.apduio.APDUCommand;
import uz.yt.ofd.android.lib.applet.Instruction;
import uz.yt.ofd.android.lib.applet.decoder.VoidDecoder;

public class SyncCommand extends AbstractCommand<VoidDecoder> {

    private final byte[] sync;

    public SyncCommand(byte[] sync) {
        this.sync = sync;
    }

    @Override
    public APDUCommand makeCommand() {
        return new APDUCommand("---sync---", (byte) 0x00, Instruction.INS_SYNC, (byte) 0x00, (byte) 0x00, sync);
    }
}

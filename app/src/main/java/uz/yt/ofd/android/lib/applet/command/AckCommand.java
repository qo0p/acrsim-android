/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.applet.command;

import uz.yt.ofd.android.lib.apduio.APDUCommand;
import uz.yt.ofd.android.lib.applet.Instruction;
import uz.yt.ofd.android.lib.applet.decoder.VoidDecoder;

public class AckCommand extends AbstractCommand<VoidDecoder> {

    private final byte[] ackFile;
    private Short index;

    public AckCommand(byte[] ackFile, Short index) {
        this.ackFile = ackFile;
        this.index = index;
    }

    @Override
    public APDUCommand makeCommand() {
        if (index == null) {
            index = (short) -1;
        }
        byte p1 = (byte) (index >> 8);
        byte p2 = (byte) (index & 0xff);
        return new APDUCommand("---ack---", (byte) 0x00, Instruction.INS_ACK, p1, p2, ackFile);
    }
}

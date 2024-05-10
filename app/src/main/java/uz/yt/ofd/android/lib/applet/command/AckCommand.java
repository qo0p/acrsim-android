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

    public AckCommand(byte[] ackFile) {
        this.ackFile = ackFile;
    }

    @Override
    public APDUCommand makeCommand() {
        return new APDUCommand("---ack---", (byte) 0x00, Instruction.ACK[0], Instruction.ACK[1], Instruction.ACK[2], ackFile);
    }
}

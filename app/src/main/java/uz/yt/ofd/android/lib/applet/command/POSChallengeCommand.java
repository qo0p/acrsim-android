/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.applet.command;


import uz.yt.ofd.android.lib.apduio.APDUCommand;
import uz.yt.ofd.android.lib.applet.Instruction;
import uz.yt.ofd.android.lib.applet.decoder.ByteArrayDecoder;

public class POSChallengeCommand extends AbstractCommand<ByteArrayDecoder> {

    public POSChallengeCommand() {

    }

    @Override
    public APDUCommand makeCommand() {
        return new APDUCommand("---pos challenge---", (byte) 0x00, Instruction.POS_CHALLENGE[0], Instruction.POS_CHALLENGE[1], Instruction.POS_CHALLENGE[2]);
    }
}

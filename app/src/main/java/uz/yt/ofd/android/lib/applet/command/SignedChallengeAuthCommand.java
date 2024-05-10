/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.applet.command;


import uz.yt.ofd.android.lib.apduio.APDUCommand;
import uz.yt.ofd.android.lib.applet.Instruction;
import uz.yt.ofd.android.lib.applet.decoder.ByteArrayDecoder;
import uz.yt.ofd.android.lib.applet.decoder.ZReportInfoDecoder;

public class SignedChallengeAuthCommand extends AbstractCommand<ByteArrayDecoder> {

    private final byte[] challenge;

    public SignedChallengeAuthCommand(byte[] challenge) {
        this.challenge = challenge;
    }

    @Override
    public APDUCommand makeCommand() {
        return new APDUCommand("---signed challenge auth---", (byte) 0x00, Instruction.INS_SIGNED_CHALLENGE_AUTH, (byte) 0x00, (byte) 0x00, this.challenge);
    }
}

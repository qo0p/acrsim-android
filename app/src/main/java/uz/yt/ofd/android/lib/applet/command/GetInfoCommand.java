/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.applet.command;

import uz.yt.ofd.android.lib.apduio.APDUCommand;
import uz.yt.ofd.android.lib.applet.Instruction;
import uz.yt.ofd.android.lib.applet.decoder.InfoDecoder;


public class GetInfoCommand extends AbstractCommand<InfoDecoder> {

    final byte[] tags;

    public GetInfoCommand(byte[] tags) {
        this.tags = tags;
    }

    @Override
    public APDUCommand makeCommand() {
        return new APDUCommand("---get info---", (byte) 0x00, Instruction.GET_INFO[0], Instruction.GET_INFO[1], Instruction.GET_INFO[2], this.tags == null ? new byte[0] : this.tags);
    }
}

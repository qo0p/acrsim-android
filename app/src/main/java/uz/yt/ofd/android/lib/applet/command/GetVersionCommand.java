/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.applet.command;

import uz.yt.ofd.android.lib.apduio.APDUCommand;
import uz.yt.ofd.android.lib.applet.Instruction;
import uz.yt.ofd.android.lib.applet.decoder.VersionDecoder;

public class GetVersionCommand extends AbstractCommand<VersionDecoder> {

    @Override
    public APDUCommand makeCommand() {
        return new APDUCommand("---get version---", (byte) 0x00, Instruction.GET_VERSION[0], Instruction.GET_VERSION[1], Instruction.GET_VERSION[2]);
    }
}

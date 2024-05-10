/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.applet.command;

import uz.yt.ofd.android.lib.apduio.APDUCommand;
import uz.yt.ofd.android.lib.applet.Instruction;
import uz.yt.ofd.android.lib.applet.decoder.FiscalMemoryInfoDecoder;

public class GetFiscalMemoryInfoCommand extends AbstractCommand<FiscalMemoryInfoDecoder> {

    final byte[] tags;

    public GetFiscalMemoryInfoCommand(byte[] tags) {
        this.tags = tags;
    }

    @Override
    public APDUCommand makeCommand() {
        return new APDUCommand("---get fiscal memory info---", (byte) 0x00, Instruction.GET_FISCAL_MEMORY_INFO[0], Instruction.GET_FISCAL_MEMORY_INFO[1], Instruction.GET_FISCAL_MEMORY_INFO[2], this.tags == null ? new byte[0] : this.tags);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.applet.command;

import uz.yt.ofd.android.lib.apduio.APDUCommand;

public class Applet {

    static final byte[] AID = new byte[]{(byte) 0x66, (byte) 0x69, (byte) 0x73, (byte) 0x63, (byte) 0x61, (byte) 0x6C, (byte) 0x64, (byte) 0x72, (byte) 0x69, (byte) 0x76, (byte) 0x65, (byte) 0x53, (byte) 0x30, (byte) 0x31};

    public static APDUCommand selectCommand() {
        return new APDUCommand("---select applet---", (byte) 0x00, (byte) 0xa4, (byte) 0x04, (byte) 0x00, AID);
    }

    public static APDUCommand deselectCommand() {
        return new APDUCommand("---deselect applet---", (byte) 0x00, (byte) 0xa4, (byte) 0x04, (byte) 0x00);
    }
}

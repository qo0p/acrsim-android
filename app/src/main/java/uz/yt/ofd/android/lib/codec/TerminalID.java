/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.codec;


public class TerminalID {

    public static String decode(byte[] raw) throws IllegalArgumentException {
        return decode(raw, 0, raw.length);
    }

    public static String decode(byte[] raw, int ofs, int len) throws IllegalArgumentException {
        if (len != 8) {
            throw new IllegalArgumentException("bad TerminalID value size " + len + ", 8 bytes expected");
        }
        if (raw[ofs + 0] < 'A' || raw[ofs + 0] > 'Z' || raw[ofs + 1] < 'A' || raw[ofs + 1] > 'Z') {
            throw new IllegalArgumentException("bad TerminalID value " + HexBin.encode(raw, ofs, len));
        }
        if (!BCD8.isBCD(raw, (short) (ofs + 2), (short) (len - 2))) {
            throw new IllegalArgumentException("bad TerminalID value " + HexBin.encode(raw, ofs, len));
        }
        return new String(raw, ofs + 0, 2).concat(HexBin.encode(raw, ofs + 2, len - 2));
    }

    public static byte[] encode(String terminalID) throws IllegalArgumentException {
        if (terminalID.length() != 14) {
            throw new IllegalArgumentException("bad TerminalID value size " + terminalID.length() + ", 14 chars expected");
        }
        if (terminalID.charAt(0) < 'A' || terminalID.charAt(0) > 'Z' || terminalID.charAt(1) < 'A' || terminalID.charAt(1) > 'Z') {
            throw new IllegalArgumentException("bad TerminalID value " + terminalID);
        }
        if (!terminalID.substring(2).matches("[0-9]{12}")) {
            throw new IllegalArgumentException("bad TerminalID value " + terminalID);
        }
        byte[] tmp = terminalID.getBytes();
        byte[] nums = HexBin.decode(terminalID.substring(2));
        byte[] raw = new byte[8];
        raw[0] = tmp[0];
        raw[1] = tmp[1];
        for (int i = 2; i < 8; i++) {
            raw[i] = nums[i - 2];
        }
        return raw;
    }
}

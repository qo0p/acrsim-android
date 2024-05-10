/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.exception;


import uz.yt.ofd.android.lib.applet.SW;

public class SWException extends Exception {

    private final SW sw;
    private final short code;

    public SWException(short code) {
        super(String.format("ISO7816 SW: %04x%s", code, SW.find(code) != null ? " - " + SW.find(code).name() : ""));
        this.code = code;
        this.sw = SW.find(code);
    }

    public short getCode() {
        return code;
    }

    public SW getSw() {
        return sw;
    }

}

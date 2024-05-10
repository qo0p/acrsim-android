/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.codec.tlv.exception;

public class TLVParseException extends Exception {

    public TLVParseException(String message) {
        super(message);
    }

    public TLVParseException(String message, Throwable cause) {
        super(message, cause);
    }

}

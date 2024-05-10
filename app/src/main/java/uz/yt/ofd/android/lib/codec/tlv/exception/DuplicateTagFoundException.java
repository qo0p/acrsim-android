/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.codec.tlv.exception;

public class DuplicateTagFoundException extends Exception {

    public DuplicateTagFoundException(byte tag) {
        super(String.format("tag %02x found more than 1 time", tag));
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.codec.text;

public interface TextCodec {
    
    public byte[] encode(String ins);

    public String decode(byte[] inb);
}

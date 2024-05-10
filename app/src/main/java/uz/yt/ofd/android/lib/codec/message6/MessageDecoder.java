/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.codec.message6;

import java.io.ByteArrayInputStream;

import uz.yt.ofd.android.lib.codec.tlv.TLV;
import uz.yt.ofd.android.lib.decoder.AbstractDecoder;

/**
 *
 * @author administrator
 */
public class MessageDecoder extends AbstractDecoder<Message> {

    public MessageDecoder(byte[] data) {
        super("MESSAGE", data);
    }

    @Override
    public Message decode() throws IllegalArgumentException, Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        byte version = (byte) bais.read();
        byte tag = (byte) bais.read();
        byte[] sizeBytes = TLV.readSizeBytes(bais);
        int size = TLV.bytesSize(sizeBytes);
        dumpDescriptor.readByte("Version", 0);
        dumpDescriptor.readHex("TLVTag", 1, 1);
        dumpDescriptor.readBytes("TLVSizeBytes", 2, sizeBytes.length, size + " bytes");
        byte[] data = dumpDescriptor.readBytes("TLVData", 2 + sizeBytes.length, size);
        dumpDescriptor.readBytes("CRC32", 2 + sizeBytes.length + size, 4);
        return new Message(version, tag, data);
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.applet.dto;

import java.io.IOException;
import java.io.OutputStream;

import uz.yt.ofd.android.lib.codec.TlvTagDescriptions;
import uz.yt.ofd.android.lib.codec.Utils;
import uz.yt.ofd.android.lib.codec.tlv.SingleTagReader;
import uz.yt.ofd.android.lib.codec.tlv.TLV;
import uz.yt.ofd.android.lib.codec.tlv.TLVEncodable;
import uz.yt.ofd.android.lib.codec.tlv.TV;
import uz.yt.ofd.android.lib.codec.tlv.TVS;

public class MemoryInfo extends TLVEncodable {

    public static final byte TAG_AVAIL_PERSIST = (byte) 0x01;
    public static final byte TAG_AVAIL_RESET = (byte) 0x02;
    public static final byte TAG_AVAIL_DESELECT = (byte) 0x03;

    public static void buildTlvTagDescriptions(TlvTagDescriptions parentTlvTagDescriptions, TlvTagDescriptions.OID oid) {
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_AVAIL_PERSIST, "AvailablePersistentMemory"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_AVAIL_RESET, "AvailableResetMemory"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_AVAIL_DESELECT, "AvailableDeselectMemory"));
    }

    Short availablePersistentMemory;
    Short availableResetMemory;
    Short availableDeselectMemory;

    @Override
    public void write(OutputStream w) throws IOException {
        if (availablePersistentMemory != null) {
            w.write(TLV.encode(TAG_AVAIL_PERSIST, Utils.short2bytes(availablePersistentMemory)));
        }
        if (availableResetMemory != null) {
            w.write(TLV.encode(TAG_AVAIL_RESET, Utils.short2bytes(availableResetMemory)));
        }
        if (availableDeselectMemory != null) {
            w.write(TLV.encode(TAG_AVAIL_DESELECT, Utils.short2bytes(availableDeselectMemory)));
        }
    }

    public static MemoryInfo decode(TVS tvs) throws Exception {
        MemoryInfo o = new MemoryInfo();
        SingleTagReader str = new SingleTagReader();
        for (TV tv : tvs) {
            if (tv.getTag() == TAG_AVAIL_PERSIST) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == 2) {
                            o.availablePersistentMemory = Utils.readShort(tv.getValue(), 0);
                        } else {
                            throw new IllegalArgumentException(String.format("availablePersistentMemory must be 2 bytes long"));
                        }
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_AVAIL_RESET) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == 2) {
                            o.availableResetMemory = Utils.readShort(tv.getValue(), 0);
                        } else {
                            throw new IllegalArgumentException(String.format("availableResetMemory must be 2 bytes long"));
                        }
                        return true;
                    }

                });
            }
            if (tv.getTag() == TAG_AVAIL_DESELECT) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == 2) {
                            o.availableDeselectMemory = Utils.readShort(tv.getValue(), 0);
                        } else {
                            throw new IllegalArgumentException(String.format("availableDeselectMemory must be 2 bytes long"));
                        }
                        return true;
                    }

                });
            }
        }
        return o;
    }

    public Short getAvailablePersistentMemory() {
        return availablePersistentMemory;
    }

    public void setAvailablePersistentMemory(Short availablePersistentMemory) {
        this.availablePersistentMemory = availablePersistentMemory;
    }

    public Short getAvailableResetMemory() {
        return availableResetMemory;
    }

    public void setAvailableResetMemory(Short availableResetMemory) {
        this.availableResetMemory = availableResetMemory;
    }

    public Short getAvailableDeselectMemory() {
        return availableDeselectMemory;
    }

    public void setAvailableDeselectMemory(Short availableDeselectMemory) {
        this.availableDeselectMemory = availableDeselectMemory;
    }
}

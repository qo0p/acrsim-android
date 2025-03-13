/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.applet.dto;

import java.io.IOException;
import java.io.OutputStream;

import uz.yt.ofd.android.lib.codec.HexBin;
import uz.yt.ofd.android.lib.codec.TerminalID;
import uz.yt.ofd.android.lib.codec.TlvTagDescriptions;
import uz.yt.ofd.android.lib.codec.Utils;
import uz.yt.ofd.android.lib.codec.tlv.SingleTagReader;
import uz.yt.ofd.android.lib.codec.tlv.TLV;
import uz.yt.ofd.android.lib.codec.tlv.TLVEncodable;
import uz.yt.ofd.android.lib.codec.tlv.TV;
import uz.yt.ofd.android.lib.codec.tlv.TVS;

public class Info extends TLVEncodable {

    public static final byte TAG_VERSION = (byte) 0x01;
    public static final byte TAG_CPLC = (byte) 0x02;
    public static final byte TAG_TERMINAL_ID = (byte) 0x03;
    public static final byte TAG_SYNC_CHALLENGE = (byte) 0x04;
    public static final byte TAG_LOCKED = (byte) 0x05;
    public static final byte TAG_JCRE_VERSION = (byte) 0x06;
    public static final byte TAG_MODE = (byte) 0x07;
    public static final byte TAG_POS_LOCKED = (byte) 0x08;
    public static final byte TAG_POS_AUTH = (byte) 0x09;
    public static final byte TAG_PATCH = (byte) 0x0a;
    public static final byte TAG_MEMORY = (byte) 0x80;

    public static void buildTlvTagDescriptions(TlvTagDescriptions parentTlvTagDescriptions, TlvTagDescriptions.OID oid) {
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_VERSION, "AppletVersion"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_PATCH, "Patch"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_CPLC, "CPLC"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_TERMINAL_ID, "TerminalID"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_SYNC_CHALLENGE, "SyncChallenge"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_LOCKED, "Locked"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_JCRE_VERSION, "JCREVersion"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_MODE, "Mode"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_POS_LOCKED, "POSLocked"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_POS_AUTH, "POSAuth"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_MEMORY, "MemoryInfo"));
        MemoryInfo.buildTlvTagDescriptions(parentTlvTagDescriptions, oid.append(TAG_MEMORY, "MemoryInfo"));
    }

    private String appletVersion;
    private String patch;
    private byte[] cplc;
    private String terminalID;
    private String syncChallenge;
    private Boolean locked;
    private String jcreVersion;
    private Byte mode;
    private Boolean posLocked;
    private Boolean posAuth;
    private MemoryInfo memoryInfo;


    @Override
    public void write(OutputStream w) throws IOException {
        if (appletVersion != null && !appletVersion.isEmpty()) {
            w.write(TLV.encode(TAG_VERSION, Utils.short2bytes(Short.parseShort(appletVersion, 16))));
        }
        if (cplc != null) {
            w.write(TLV.encode(TAG_CPLC, cplc));
        }
        if (patch != null) {
            w.write(TLV.encode(TAG_PATCH, HexBin.decode(patch)));
        }
        if (terminalID != null && !terminalID.isEmpty()) {
            w.write(TLV.encode(TAG_TERMINAL_ID, TerminalID.encode(terminalID)));
        }
        if (syncChallenge != null) {
            w.write(TLV.encode(TAG_SYNC_CHALLENGE, HexBin.decode(syncChallenge)));
        }
        if (locked != null) {
            w.write(TLV.encode(TAG_LOCKED, new byte[]{locked ? (byte) 0x00 : (byte) 0xff}));
        }
        if (jcreVersion != null && !jcreVersion.isEmpty()) {
            w.write(TLV.encode(TAG_JCRE_VERSION, Utils.short2bytes(Short.parseShort(jcreVersion, 16))));
        }
        if (mode != null) {
            w.write(TLV.encode(TAG_MODE, new byte[]{mode}));
        }
        if (posLocked != null) {
            w.write(TLV.encode(TAG_POS_LOCKED, new byte[]{posLocked ? (byte) 0x00 : (byte) 0xff}));
        }
        if (posAuth != null) {
            w.write(TLV.encode(TAG_POS_AUTH, new byte[]{posAuth ? (byte) 0x00 : (byte) 0xff}));
        }
        if (memoryInfo != null) {
            w.write(TLV.encode(TAG_MEMORY, memoryInfo.encode()));
        }
    }

    public static Info decode(TVS tvs) throws Exception {
        Info o = new Info();
        SingleTagReader str = new SingleTagReader();
        for (TV tv : tvs) {
            if (tv.getTag() == TAG_VERSION) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == 2) {
                            o.appletVersion = String.format("%04x", Utils.readShort(tv.getValue(), 0));
                        } else {
                            throw new IllegalArgumentException(String.format("version must be 2 bytes long"));
                        }
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_CPLC) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == Size.CPLC_LEN) {
                            o.cplc = tv.getValue();
                        } else {
                            throw new IllegalArgumentException(String.format("cplc must be %d bytes long", Size.CPLC_LEN));
                        }
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_TERMINAL_ID) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        o.terminalID = TerminalID.decode(tv.getValue());
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_SYNC_CHALLENGE) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        o.syncChallenge = HexBin.encode(tv.getValue());
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_LOCKED) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == 1) {
                            o.locked = tv.getValue()[0] == 0x00;
                        } else {
                            throw new IllegalArgumentException(String.format("locked must be %d bytes long", 1));
                        }
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_JCRE_VERSION) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == 2) {
                            o.jcreVersion = String.format("%04x", Utils.readShort(tv.getValue(), 0));
                        } else {
                            throw new IllegalArgumentException(String.format("jcreVersion must be 2 bytes long"));
                        }
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_MODE) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == 1) {
                            o.mode = tv.getValue()[0];
                        } else {
                            throw new IllegalArgumentException(String.format("mode must be %d bytes long", 1));
                        }
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_POS_LOCKED) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == 1) {
                            o.posLocked = tv.getValue()[0] == 0x00;
                        } else {
                            throw new IllegalArgumentException(String.format("posLocked must be %d bytes long", 1));
                        }
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_POS_AUTH) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null && tv.getValue().length == 1) {
                            o.posAuth = tv.getValue()[0] == 0x00;
                        } else {
                            throw new IllegalArgumentException(String.format("posAuth must be %d bytes long", 1));
                        }
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_PATCH) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        if (tv.getValue() != null) {
                            o.patch = HexBin.encode(tv.getValue());
                        }
                        return true;
                    }
                });
            }
            if (tv.getTag() == TAG_MEMORY) {
                str.read(tv, new SingleTagReader.Callback() {
                    @Override
                    public boolean assign(TV tv) throws Exception {
                        o.memoryInfo = MemoryInfo.decode(tv.getTvs());
                        return true;
                    }
                });
            }
        }
        return o;
    }

    public String getAppletVersion() {
        return appletVersion;
    }

    public String getPatch() {
        return patch;
    }

    public void setAppletVersion(String appletVersion) {
        this.appletVersion = appletVersion;
    }

    public byte[] getCplc() {
        return cplc;
    }

    public void setCplc(byte[] cplc) {
        this.cplc = cplc;
    }

    public String getTerminalID() {
        return terminalID;
    }

    public void setTerminalID(String terminalID) {
        this.terminalID = terminalID;
    }

    public String getSyncChallenge() {
        return syncChallenge;
    }

    public void setPatch(String patch) {
        this.patch = patch;
    }

    public String getJcreVersion() {
        return jcreVersion;
    }

    public void setJcreVersion(String jcreVersion) {
        this.jcreVersion = jcreVersion;
    }

    public Byte getMode() {
        return mode;
    }

    public void setMode(Byte mode) {
        this.mode = mode;
    }

    public void setSyncChallenge(String syncChallenge) {
        this.syncChallenge = syncChallenge;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Boolean getPosAuth() {
        return posAuth;
    }

    public void setPosAuth(Boolean posAuth) {
        this.posAuth = posAuth;
    }

    public Boolean getPosLocked() {
        return posLocked;
    }

    public void setPosLocked(Boolean posLocked) {
        this.posLocked = posLocked;
    }

    public MemoryInfo getMemoryInfo() {
        return memoryInfo;
    }

    public void setMemoryInfo(MemoryInfo memoryInfo) {
        this.memoryInfo = memoryInfo;
    }

}

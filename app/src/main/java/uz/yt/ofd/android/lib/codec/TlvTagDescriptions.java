/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.codec;

import java.util.HashMap;

public class TlvTagDescriptions extends HashMap<String, String> {

    public static class OID {

        private byte[] id;
        private String[] name;

        public OID() {
            this.id = new byte[0];
            this.name = new String[0];
        }

        public OID(byte tag, String name) {
            this.id = new byte[]{tag};
            this.name = new String[]{name};
        }

        public int length() {
            return id.length;
        }

        public OID append(byte tag, String desc) {
            OID r = new OID();
            r.id = Utils.append(this.id, tag);
            r.name = Utils.append(this.name, desc);
            return r;
        }

        public String id() {
            String[] sid = new String[id.length];
            for (int i = 0; i < id.length; i++) {
                sid[i] = String.format("%02x", id[i]);
            }
            return String.join(".", sid);
        }

        public String name() {
            return String.join(".", name);
        }

    }

    public final void addTagDesciption(OID oid) {
        put(oid.id(), oid.name());
    }
}

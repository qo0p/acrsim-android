/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.codec.tlv;


import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import uz.yt.ofd.android.lib.codec.Utils;

public class TVS extends LinkedList<TV> {

    private String string(TV tv, int level) {
        StringBuilder sb = new StringBuilder();
        String padd = new String(new char[level]).replace("\0", "  ");
        sb.append(padd);
        sb.append(tv.toString()).append("\n");
        for (TV tvi : tv.getTvs()) {
            sb.append(string(tvi, level + 1));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int level = 0;
        for (TV tv : this) {
            sb.append(string(tv, level));
        }
        return sb.toString();
    }

    private TV find(TVS subTVS, byte[] subTags, int pos) {
        if (subTags == null || subTags.length == pos) {
            return null;
        }
        byte tag = subTags[pos];
        for (TV tvi : subTVS) {
            if (tvi.getTag() == tag) {
                if (subTags.length - 1 == pos) {
                    return tvi;
                }
                return this.find(tvi.getTvs(), subTags, pos + 1);
            }
        }
        return null;
    }

    public TV find(byte tag, byte... subTags) {
        for (TV tvi : this) {
            if (tvi.getTag() == tag) {
                if (subTags.length == 0) {
                    return tvi;
                }
                return this.find(tvi.getTvs(), subTags, 1);
            }
        }
        return null;
    }

    public TV findByOID(OID oid) {
        if (oid.isEmpty()) {
            return null;
        }
        byte tag = oid.getValue()[0];
        byte[] subTags = oid.getValue();
        for (TV tvi : this) {
            if (tvi.getTag() == tag) {
                if (subTags.length == 1) {
                    return tvi;
                }
                return this.find(tvi.getTvs(), subTags, 1);
            }
        }
        return null;
    }

    private void tagList(TVS subTVS, byte[] parentTags, Map<String, OID> tm) {
        if (subTVS == null || subTVS.isEmpty()) {
            OID oid = new OID(parentTags);
            tm.put(oid.toString(), oid);
            return;
        }
        for (TV tvi : subTVS) {
            byte[] tags = Utils.append(parentTags, tvi.getTag());
            tagList(tvi.getTvs(), tags, tm);
        }
    }

    public Collection<OID> tagList() {
        Map<String, OID> tm = new HashMap();
        for (TV tvi : this) {
            tagList(tvi.getTvs(), new byte[]{tvi.getTag()}, tm);
        }
        return tm.values();
    }

    private void values(TVS subTVS, byte[] parentTags, byte[] value, List<OIDValue> ov, Map<String, Integer> arrayOIDs) {
        if (subTVS == null || subTVS.isEmpty()) {
            OID oid = new OID(Utils.copy(parentTags));
            ov.add(new OIDValue(oid, value));
            return;
        }
        Set<String> clearList = new TreeSet<>();
        for (TV tv : subTVS) {
            byte[] tag = Utils.append(parentTags, tv.getTag());
            OID oid = new OID(tag);
            if (arrayOIDs.containsKey(oid.toString())) {
                int index = arrayOIDs.get(oid.toString());
                clearList.add(oid.toString());

                List<OIDValue> aov = new LinkedList();
                values(tv.getTvs(), tag, tv.getValue(), aov, arrayOIDs);
                OID poid = oid.copy();
                for (int i = 0; i < aov.size(); i++) {
                    aov.get(i).addParent(poid, index);
                }
                index++;
                arrayOIDs.put(oid.toString(), index);
                ov.addAll(aov);

            } else {
                values(tv.getTvs(), tag, tv.getValue(), ov, arrayOIDs);
            }
        }
        for (String cl : clearList) {
            arrayOIDs.put(cl, 0);
        }
        return;
    }

    public List<OIDValue> values(String[] arrayOIDs) {
        Map<String, Integer> aoids = new HashMap();
        for(String aoid: arrayOIDs){
            aoids.put(aoid, 0);
        }
        List<OIDValue> ov = new LinkedList();
        values(this, new byte[0], new byte[0], ov, aoids);
        return ov;
    }

}

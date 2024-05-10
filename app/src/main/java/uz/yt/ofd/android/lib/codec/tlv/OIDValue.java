/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.codec.tlv;

import java.util.Formattable;
import java.util.FormattableFlags;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.List;
import uz.yt.ofd.android.lib.codec.HexBin;

public class OIDValue implements Formattable {

    private List<ParentOID> parents;
    private OID oid;
    private byte[] value;

    public OIDValue(OID oid, byte[] value) {
        this.oid = oid;
        this.value = value;
    }

    public OID getOid() {
        return oid;
    }

    public byte[] getValue() {
        return value;
    }

    @Override
    public void formatTo(Formatter formatter, int flags, int width, int precision) {
        boolean alternateFlag = (FormattableFlags.ALTERNATE & flags) == FormattableFlags.ALTERNATE;
//        boolean upperFlag = (FormattableFlags.UPPERCASE & flags) == FormattableFlags.UPPERCASE;
        boolean leftJustifiedFlag = (FormattableFlags.LEFT_JUSTIFY & flags) == FormattableFlags.LEFT_JUSTIFY;
        if (parents != null && parents.size() > 0) {
            if (alternateFlag) {
                String coids = oid.toString();
                for (ParentOID poid : parents) {
                    String poids = poid.oid.toString();
                    coids = coids.replace(poids, String.format("%s[%d]", poids, poid.index));
                }
                formatter.format("%s = %s", coids, HexBin.encode(value));
            } else if (leftJustifiedFlag) {
                String coids = oid.toString();
                for (ParentOID poid : parents) {
                    String poids = poid.oid.toString();
                    coids = coids.replace(poids, String.format("%s[%d]", poids, poid.index));
                }
                formatter.format("%s", coids);
            }
            return;
        }
        if (leftJustifiedFlag) {
            formatter.format("%s", oid);
            return;
        }
        formatter.format("%s = %s", oid, HexBin.encode(value));
    }

    public void addParent(OID poid, int index) {
        if (parents == null) {
            parents = new LinkedList();
        }
        parents.add(new ParentOID(poid, index));
    }

}

class ParentOID {

    OID oid;
    int index;

    public ParentOID(OID oid, int index) {
        this.oid = oid;
        this.index = index;
    }

}

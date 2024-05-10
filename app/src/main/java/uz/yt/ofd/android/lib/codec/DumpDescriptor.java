package uz.yt.ofd.android.lib.codec;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formattable;
import java.util.FormattableFlags;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.List;

import uz.yt.ofd.android.lib.codec.tlv.TLV;
import uz.yt.ofd.android.lib.codec.tlv.TV;
import uz.yt.ofd.android.lib.codec.tlv.TVS;


public class DumpDescriptor implements Formattable {

    protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    final static int NAME_MAX_LEN = 32;

    private final String name;
    private final byte[] dump;

    private DumpDescriptor header;

    class Block {

        int begin;
        int end;
        String key;
        String value;

        public Block(int begin, int end, String key, String value) {
            this.begin = begin;
            this.end = end;
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return String.format("%04x.%04x - %s: %s", begin, end, key, value);
        }

    }

    private final List<Block> blocks;

    String offssize = "offs.size";
    int offssizepad;

    public static interface Byte2BooleanTranslator {

        public boolean translateValue(byte b);
    }

    public DumpDescriptor(String name, byte[] dump) {
        String trimmedName = name;
        if (trimmedName.length() < NAME_MAX_LEN) {
            trimmedName = new String(new char[NAME_MAX_LEN - trimmedName.length()]).replaceAll("\0", " ") + trimmedName;
        }
        if (trimmedName.length() < offssize.length()) {
            trimmedName = String.format("%-" + (offssize.length() - trimmedName.length()) + "s", trimmedName);
        }
        if (trimmedName.length() > offssize.length()) {
            offssizepad = trimmedName.length() - offssize.length();
            offssize = new String(new char[offssizepad]).replaceAll("\0", " ") + offssize;
        }
        this.name = trimmedName;
        this.dump = dump;
        this.blocks = new LinkedList();
    }

    public long readBCD(String key, int ofs, int len) {
        long val = new BCD8(dump, (short) ofs, (short) len).toLong();
        blocks.add(new Block(ofs, ofs + len, key, String.valueOf(val)));
        return val;
    }

    public Date readDate(String key, int ofs) {
        Date val = BCDDateTime.fromBytes(dump, (short) ofs);
        blocks.add(new Block(ofs, ofs + 8, key, val == null ? "" : dateFormat.format(val)));
        return val;
    }

    public String readTerminalID(String key, int ofs) throws IllegalArgumentException {
        String val = TerminalID.decode(dump, (short) ofs, (short) 8);
        blocks.add(new Block(ofs, ofs + 8, key, val));
        return val;
    }

    public String readHex(String key, int ofs, int len) {
        String val = HexBin.encode(dump, ofs, len);
        blocks.add(new Block(ofs, ofs + len, key, Utils.trim(val, 8)));
        return val;
    }

    public byte[] readBytes(String key, int ofs, int len) {
        String val = HexBin.encode(dump, ofs, len);
        blocks.add(new Block(ofs, ofs + len, key, Utils.trim(val, 8)));
        return Utils.slice(dump, ofs, ofs + len);
    }

    public byte[] readBytes(String key, int ofs, int len, String predefinedValue) {
        blocks.add(new Block(ofs, ofs + len, key, predefinedValue));
        return Utils.slice(dump, ofs, ofs + len);
    }

    public byte readByte(String key, int ofs) {
        byte val = dump[ofs];
        blocks.add(new Block(ofs, ofs + 1, key, String.valueOf(val)));
        return val;
    }

    public short readShort(String key, int ofs) {
        short val = Utils.readShort(dump, ofs);
        blocks.add(new Block(ofs, ofs + 2, key, String.valueOf(val)));
        return val;
    }

    public boolean readBoolean(String key, int ofs, Byte2BooleanTranslator translator) {
        boolean val = translator.translateValue(dump[ofs]);
        blocks.add(new Block(ofs, ofs + 1, key, String.valueOf(val)));
        return val;
    }

    public TVS readTLV(String key, TlvTagDescriptions tlvTagDescriptions) throws Exception {
        String val = HexBin.encode(dump);
        blocks.add(new Block(0, dump.length, key, Utils.trim(val, 8)));
        TVS tvs = TLV.decode(dump);

//        System.out.println(tvs.toString());
//        System.out.println("TAG LIST");
//        Collection<OID> tl = tvs.tagList();
//        Iterator<OID> it = tl.iterator();
//        while (it.hasNext()) {
//            OID oid = it.next();
//            System.out.println(oid);
//        }
//
//        System.out.println();
//        System.out.println("VALUES");
//        List<OIDValue> tvv = tvs.values(new String[0]);
//        for (OIDValue ov : tvv) {
//            System.out.printf("%s\n", ov);
//        }

        for (TV tv : tvs) {
            val = HexBin.encode(tv.getValue());
            String tag = String.format("%02x", tv.getTag());
            String desc = tlvTagDescriptions.getOrDefault(tag, "");
            blocks.add(new Block(tv.getFrom(), tv.getTo(), String.format("Tag %s, Name: %s", tag, desc), Utils.trim(val, 8)));
            for (TV tv0 : tv.getTvs()) {
                val = HexBin.encode(tv0.getValue());
                String tag0 = String.format("%02x", tv0.getTag());
                desc = tlvTagDescriptions.getOrDefault(tag + "." + tag0, "");
                blocks.add(new Block(tv0.getFrom(), tv0.getTo(), String.format("Tag %s, Name: %s", tag + "." + tag0, desc), Utils.trim(val, 8)));
                for (TV tv1 : tv0.getTvs()) {
                    val = HexBin.encode(tv1.getValue());
                    String tag1 = String.format("%02x", tv1.getTag());
                    desc = tlvTagDescriptions.getOrDefault(tag + "." + tag0 + "." + tag1, "");
                    blocks.add(new Block(tv1.getFrom(), tv1.getTo(), String.format("Tag %s, Name: %s", tag + "." + tag0 + "." + tag1, desc), Utils.trim(val, 8)));
                    for (TV tv2 : tv1.getTvs()) {
                        val = HexBin.encode(tv2.getValue());
                        String tag2 = String.format("%02x", tv2.getTag());
                        desc = tlvTagDescriptions.getOrDefault(tag + "." + tag0 + "." + tag1 + "." + tag2, "");
                        blocks.add(new Block(tv2.getFrom(), tv2.getTo(), String.format("Tag %s, Name: %s", tag + "." + tag0 + "." + tag1 + "." + tag2, desc), Utils.trim(val, 8)));

                    }
                }
            }
        }
        return tvs;

    }

    public void setHeader(DumpDescriptor header) {
        this.header = header;
    }

    @Override
    public String toString() {
        return String.format("%#s", this);
    }

    @Override
    public void formatTo(Formatter formatter, int flags, int width, int precision) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        boolean alternateFlag = (FormattableFlags.ALTERNATE & flags) == FormattableFlags.ALTERNATE;
        boolean upperCaseFlag = (FormattableFlags.UPPERCASE & flags) == FormattableFlags.UPPERCASE;
        if (alternateFlag) {
            if (!upperCaseFlag) {
                if (header != null) {
                    sb.append(String.format("%#" + (upperCaseFlag ? "S" : "s"), header));
                }
                sb.append(String.format("%s\n size: %d\n dump: %s\n", name.trim(), dump.length, HexBin.encode(dump)));
                int keyMaxSize = 0;
                int dumpMaxSize = 0;
                int valMaxSize = 0;
                for (Block db : blocks) {
                    if (db.key.length() > keyMaxSize) {
                        keyMaxSize = db.key.length();
                    }
                    int dumpLength = db.end - db.begin;
                    if (dumpLength > dumpMaxSize) {
                        dumpMaxSize = dumpLength;
                    }
                    if (db.value != null && (db.value.length() > valMaxSize)) {
                        valMaxSize = db.value.length();
                    }
                }
                if (keyMaxSize < 4) {
                    keyMaxSize = 4;
                }
                if (dumpMaxSize < 2) {
                    dumpMaxSize = 2;
                }
                if (dumpMaxSize > 16) {
                    dumpMaxSize = 16;
                }
                if (valMaxSize < 5) {
                    valMaxSize = 5;
                }

                String keyPadd = new String(new char[keyMaxSize]).replaceAll("\0", " ");
                String dumpPadd = new String(new char[(dumpMaxSize * 2)]).replaceAll("\0", " ");
                String valPadd = new String(new char[valMaxSize]).replaceAll("\0", " ");

                sb.append(String.format("+--------+--------+-----%s-+-----%s-+------%s-+\n", keyPadd.substring(4).replace(" ", "-"), dumpPadd.substring(4).replace(" ", "-"), valPadd.substring(5).replace(" ", "-")));
                sb.append(String.format("| offset |  size  | name%s | dump%s | value%s |\n", keyPadd.substring(4), dumpPadd.substring(4), valPadd.substring(5)));
                sb.append(String.format("+--------+--------+-----%s-+-----%s-+------%s-+\n", keyPadd.substring(4).replace(" ", "-"), dumpPadd.substring(4).replace(" ", "-"), valPadd.substring(5).replace(" ", "-")));
                for (Block db : blocks) {
                    int dumpLength = db.end - db.begin;
                    byte[] block = Utils.slice(dump, db.begin, db.end);
                    String blockHex = HexBin.encode(block);
                    if (dumpLength > 16) {
                        dumpLength = 16;
                        blockHex = blockHex.substring(0, 8 * 2 - 2) + "...." + blockHex.substring(blockHex.length() - 8 * 2 + 2);
                    }
                    sb.append(String.format("| 0x%04x | 0x%04x | %s%s | %s%s | %s%s |\n", db.begin, db.end - db.begin, db.key, keyPadd.substring(db.key.length()), blockHex, dumpPadd.substring(dumpLength * 2), db.value, valPadd.substring(db.value.length())));
                }
                sb.append(String.format("+--------+--------+-----%s-+-----%s-+------%s-+\n", keyPadd.substring(4).replace(" ", "-"), dumpPadd.substring(4).replace(" ", "-"), valPadd.substring(5).replace(" ", "-")));
            } else {
                if (header != null) {
                    String headerContent = String.format("%#" + (upperCaseFlag ? "S" : "s"), header);
                    headerContent = headerContent.replaceFirst("</table>", "");
                    sb.append(headerContent);
                    sb.append("<tr><th colspan='5'>").append(name.trim()).append(" (").append(dump.length).append(" bytes)").append("</th></tr>");
                } else {
                    sb.append("<style type='text/css'> table, th, td { border: 1px solid black; border-collapse: collapse; }</style>");
                    sb.append("<table>");
                    sb.append("<tr><th colspan='5'>").append(name.trim()).append(" (").append(dump.length).append(" bytes)").append("</th></tr>");
                    sb.append("<tr><td colspan='5'>").append(HexBin.encode(dump)).append("</td></tr>");
                    sb.append("<tr><th>offset</th><th>size</th><th>name</th><th>dump</th><th>value</th></tr>");
                }
                for (Block db : blocks) {
                    int dumpLength = db.end - db.begin;
                    byte[] block = Utils.slice(dump, db.begin, db.end);
                    String blockHex = HexBin.encode(block);
                    if (dumpLength > 16) {
                        dumpLength = 16;
                        blockHex = blockHex.substring(0, 8 * 2 - 2) + "...." + blockHex.substring(blockHex.length() - 8 * 2 + 2);
                    }
                    sb.append(String.format("<tr><td>0x%04x</td><td>0x%04x</td><td>%s</td><td>%s</td><td>%s</td></tr>", db.begin, db.end - db.begin, db.key, blockHex, db.value));
                }
                sb.append("</table>");
            }
        } else {
            if (header != null) {
                sb.append(String.format("%s", header));
            }
            int dls = (2 * dump.length - 4);
            sb.append(String.format("%s: %s - size %d\n%s: %s%s - %s\n", name, HexBin.encode(dump), dump.length, offssize, "dump", new String(new char[dls]).replaceAll("\0", " "), "description"));
            for (Block db : blocks) {
                int length = db.end - db.begin;
                byte[] block = Utils.slice(dump, db.begin, db.end);
                int postDots = dump.length - (db.begin + length);
                if (postDots < 0) {
                    postDots = 0;
                }
                sb.append(String.format("%s%04x.%04x: %s%s%s - %s: %s\n", new String(new char[offssizepad]).replaceAll("\0", " "), db.begin, length, new String(new char[db.begin]).replaceAll("\0", ".."), HexBin.encode(block), new String(new char[postDots]).replaceAll("\0", ".."), db.key, db.value));
            }
        }
        formatter.format("%s", sb.toString());
        return;
    }

}

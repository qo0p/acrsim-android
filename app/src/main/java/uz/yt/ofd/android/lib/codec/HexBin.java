package uz.yt.ofd.android.lib.codec;

public class HexBin {

    static private final int BASELENGTH = 128;
    static private final int LOOKUPLENGTH = 16;
    static final private byte[] hexNumberTable = new byte[BASELENGTH];
    static final private char[] lookUpHexAlphabet = new char[LOOKUPLENGTH];

    static {
        for (int i = 0; i < BASELENGTH; i++) {
            hexNumberTable[i] = -1;
        }
        for (int i = '9'; i >= '0'; i--) {
            hexNumberTable[i] = (byte) (i - '0');
        }
        for (int i = 'F'; i >= 'A'; i--) {
            hexNumberTable[i] = (byte) (i - 'A' + 10);
        }
        for (int i = 'f'; i >= 'a'; i--) {
            hexNumberTable[i] = (byte) (i - 'a' + 10);
        }

        for (int i = 0; i < 10; i++) {
            lookUpHexAlphabet[i] = (char) ('0' + i);
        }
        for (int i = 10; i <= 15; i++) {
            lookUpHexAlphabet[i] = (char) ('a' + i - 10);
        }
    }

    public static String encode(byte[] binaryData, int ofs, int len) {
        if (binaryData == null) {
            return null;
        }
        int lengthData = Math.min(binaryData.length - ofs, len);
        if (lengthData <= 0) {
            return "";
        }
        int lengthEncode = lengthData * 2;
        char[] encodedData = new char[lengthEncode];
        int temp;
        for (int i = 0; i < lengthData; i++) {
            temp = binaryData[i + ofs];
            if (temp < 0) {
                temp += 256;
            }
            encodedData[i * 2] = lookUpHexAlphabet[temp >> 4];
            encodedData[i * 2 + 1] = lookUpHexAlphabet[temp & 0xf];
        }
        return new String(encodedData);
    }

    public static String encode(byte[] binaryData) {
        if (binaryData == null) {
            return null;
        }
        return encode(binaryData, 0, binaryData.length);
    }

    static public byte[] decode(String encoded) throws IllegalArgumentException {
        if (encoded == null) {
            throw new IllegalArgumentException("null passed");
        }
        int lengthData = encoded.length();
        if (lengthData % 2 != 0) {
            throw new IllegalArgumentException("bad hex string passed");
        }

        char[] binaryData = encoded.toCharArray();
        int lengthDecode = lengthData / 2;
        byte[] decodedData = new byte[lengthDecode];
        byte temp1, temp2;
        char tempChar;
        for (int i = 0; i < lengthDecode; i++) {
            tempChar = binaryData[i * 2];
            temp1 = (tempChar < BASELENGTH) ? hexNumberTable[tempChar] : -1;
            if (temp1 == -1) {
                throw new IllegalArgumentException("bad hex string passed");
            }
            tempChar = binaryData[i * 2 + 1];
            temp2 = (tempChar < BASELENGTH) ? hexNumberTable[tempChar] : -1;
            if (temp2 == -1) {
                throw new IllegalArgumentException("bad hex string passed");
            }
            decodedData[i] = (byte) ((temp1 << 4) | temp2);
        }
        return decodedData;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.codec.text;

import java.io.ByteArrayOutputStream;

public class TextCodecV2 implements TextCodec {

    public static final char[] CHARMAP = new char[]{
            'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю',
            'Я', 'Ў', 'Қ', 'Ғ', 'Ҳ', '㎪', '㎏', '≤', '≠', '√', 'Ⅰ', 'Ⅱ', 'Ⅲ', 'Ⅳ', 'Ⅴ', 'Ⅵ', 'Ⅶ', 'Ⅷ', 'Ⅸ', 'Ⅹ', 'Ⅺ', 'Ⅻ', '[', '+', '-', '=', '<', '>', '_', '\'', '~', ']',
            'а', 'б', 'в', 'г', 'д', 'е', 'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю',
            'я', 'ў', 'қ', 'ғ', 'ҳ', '㎞', '㎡', '≥', '?', '∞', 'ⅰ', 'ⅱ', 'ⅲ', 'ⅳ', 'ⅴ', 'ⅵ', 'ⅶ', 'ⅷ', 'ⅸ', 'ⅹ', 'ⅺ', 'ⅻ', '}', '"', '№', ';', ',', ':', '/', '\\', '|', '{',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'Ö', 'Ç', 'Ğ', 'Ş', 'Ü', 'Ŋ',
            'Ӑ', 'Ѐ', 'Ӗ', 'Ṓ', '㎇', '㎾', '⌛', '⊕', '≡', '∫', '∆', 'ℼ', '™', '℃', '฿', '§', '½', '÷', '·', '±', '®', '«', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'ö', 'ç', 'ğ', 'ş', 'ü', 'ŋ',
            'ӑ', 'ѐ', 'ӗ', 'ṓ', '㎖', '㎥', ' ', '⊗', '≢', '≈', '∅', '⅀', '℠', '℉', '€', '¤', '¾', '¼', '×', '¶', '©', '»', ')', '!', '@', '#', '$', '%', '^', '&', '*', '('};

    public byte[] encode(String ins) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (char c : ins.toCharArray()) {
            for (int code = 0; code < CHARMAP.length; code++) {
                if (c == CHARMAP[code]) {
                    baos.write((byte) code);
                    break;
                }
            }
        }
        return baos.toByteArray();
    }

    public String decode(byte[] inb) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < inb.length; i++) {
            sb.append(CHARMAP[(int) (inb[i] & 0xff)]);
        }
        return sb.toString();
    }

}

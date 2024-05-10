/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.codec.message6;

/**
 * Состояние принятия файла
 *
 * @author administrator
 */
public enum AckFileStatus {
    /**
     * Принят
     */
    Acknowledge((byte) 0),
    /**
     * Отвергнут (причину уточните у оператора ОФД)
     */
    Reject((byte) 1),
    /**
     * Файл не принят, возмодно файл содержит ошибки кодирования/шифрования или
     * прочие ошибки
     */
    Error((byte) 2),
    /**
     * Передан недействительный тип файла
     */
    UnrecognizedType((byte) 3);

    public final byte value;

    AckFileStatus(byte value) {
        this.value = value;
    }

    public static AckFileStatus find(byte value) {
        for (AckFileStatus t : values()) {
            if (t.value == value) {
                return t;
            }
        }
        return null;
    }
}

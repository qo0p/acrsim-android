/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.codec.message6;

/**
 * Код статуса приемя файлов
 *
 * @author administrator
 */
public enum StatusCode {
    /**
     * Успешно
     */
    OK((byte) 0),
    /**
     * Успешно, имеется сообщение от сервера
     */
    OKNotice((byte) 1),
    /**
     * Повторить попытку отправки позьже
     */
    RetrySend((byte) 2),
    /**
     * ФМ не активен, прием файлов пока не возможен
     */
    NotActive((byte) 10),
    /**
     * ФМ (его TerminalID) не найден в БД сервера ОФД
     */
    NotFound((byte) 11),
    /**
     * Запрос содержит ошибки
     */
    BadMessageSyntax((byte) 100),
    /**
     * Струтура запроса содержит ошибки
     */
    BadMessageStruct((byte) 101),
    /**
     * Очень большое сообщение, уменьшите кол-во файлов для отправки и повторите
     * заного
     */
    TooBigMessage((byte) 102),
    /**
     * Сообщение имеет неправильный CRC32
     */
    BadMessageCRC32((byte) 103),
    /**
     * Слишкоа много файлов для отправки, уменьшите кол-во файлов для отправки и
     * повторите заного
     */
    TooManyFiles((byte) 104);

    public final byte value;

    StatusCode(byte value) {
        this.value = value;
    }

    public static StatusCode find(byte value) {
        for (StatusCode t : values()) {
            if (t.value == value) {
                return t;
            }
        }
        return null;
    }
}

# ИНСТРУКЦИЯ РАЗРАБОТЧИКА КОНТРОЛЬНО-КАССОВОЙ МАШИНЫ НА БАЗЕ ОС ANDROID ДЛЯ ИНТЕГРАЦИИ ФИСКАЛЬНОГО МОДУЛЯ ВЕРСИИ 0400

## ТЕРМИНЫ И ОПРЕДЕЛЕНИЯ

* __ОС__ - Операционная система
* __ККМ__ - Котрольно-кассовая машина.
* __ISO7816__ - Стандарт относится к смарт-картам (в первую очередь контактным). Описывает форму карты, контактов, их расположение и назначение; протоколы обмена и некоторые аспекты работы с данными. 
* __ФМ__ - Фискальный модуль, смарт-карта или USB-токен с ОС JavaCard и установленным апплетов выполняющего функцию фискального модуля.
* __ФП__ - Фискальный признак чека.
* __TLV__ - «Tag-Length-Value» широко распространённый метод записи коротких данных в компьютерных файлах и телекоммуникационных протоколах. 
* __ZReport__ - Запись в ФМ хранит дату-время открытия и закрытия дня и накопленные суммы продаж, возвратов и НДС.
* __Receipt__ - Запись в ФМ хранит дату-время, тип, сумму чека до тех пор пока не произойдет синхронизация с сервером.
* __AckFile__ - Файл получаемый от сервера для передачи в ФМ для подтверждения доставки ZReport и Receipt.  
* __SyncFile__ - Файл получаемый от сервера для передачи в ФМ для синхронизации времени, состояния ФМ с сервером.  
* __SyncChallenge__ - Применяется при синхронизации времени, состояния ФМ с сервером.



## ВВЕДЕНИЕ

В инструкции приведены описания и принцып работы ФМ версии 0400, примерный проект android-приложения и описания методов для взаимодействия с ФМ

## ФМ 0400

Отличия ФМ 0400 от прежних версий:

* __Защита от случайной установки будущей даты-врмени__, еслм передаваемое дата-время больще на 2 дня чем дата-время последней операции в ФМ. то ФМ возвратом кода ошибки потребует синхронизацию с сервером для установки серверного даты-время в ФМ.
* __Нет ограничения на кол-во записей ZReport__, Максимальное кол-во зависит от доступной физ.памяти ФМ.
* __Кол-во операции продажа и возврат в одном ZReport до 29999__, в прежних версиях ФМ было до 9999.
* __Нет ограничения на кол-во записей Receipt__, Максимальное кол-во зависит от доступной физ.памяти ФМ.
* __Добавлены типы чеков Аванс и Кредит__, Для этих типов чеков ФМ также выдает порядковый номер чека, но не выдает ФП.
* __Идемпотентность операций ФМ__, При регистрации чека передача одной и той же команды (много раз) в ФМ будет выполненя одна операция и выдача одного и того же результата (много раз).
* __Выполнена оптимизация работы ФМ__, Сокращено кол-во инструкций и возвращаемых кодов ошибок в ФМ. Данные из ФМ можно запрашивать частично передавая TLV-теги запрашиваемых полей.
* __Разработан эмулятор ФМ__, Для разработки и тестирования ПО для ККМ больше не требуется физ.ФМ, можно запустить программный эмулятор ФМ 0400.

## Коды возврата

ФМ всегда (по ISO7816) возвращает 2х байтовый код после выполнения инструкции. Если ответ от ФМ не вернулся,то скорее всего проблема в драйвере SAM-считывателя.

| Values   | Names                                  | Description |
|----------|----------------------------------------|-------------|
| `0x9000` | `NO_ERROR`                             | Успешно     |
| `0x9010` | `INVALID_DATETIME`                     | Передано неправильное значение даты-времени (должно быть в формате BCDDateTime)          |
| `0x9011` | `INVALID_INDEX`                        | Передано неправильное значение индекса (должно быть от 0 до 32767)            |
| `0x9012` | `INVALID_BCD`                          | Передано неправильное значение числа (должно быть в формате BCD8)            |
| `0x9013` | `INVALID_TYPE`                         | Передано неправильное значение типа чека            |
| `0x9014` | `INVALID_OPERATION`                    | Передано неправильное значение типа операции            |
| `0x9015` | `INVALID_ACK_SIGNATURE`                | Передано неправильное значение AckFile             |
| `0x9016` | `WRONG_TERMINAL_ID`                    | Передано неправильное значение AckFile (возможно AckFile не для этого ФМ)            |
| `0x9017` | `INVALID_SYNC_SIGNATURE`               | Передано неправильное значение SyncFile            |
| `0x9018` | `WRONG_SYNC_CHALLENGE`                 | Передано неправильное значение SyncChallenge (возможно был повторно передан прежний SyncFile)           |
| `0x9020` | `NOT_FOUND`                            | Запрашиваемая запись ненайдена. Может быть при повторной передачи AckFile в ФМ или при передачи индекса для получения записи которого нет в ФМ            |
| `0x9021` | `ZREPORT_IS_NOT_OPENED`                |             |
| `0x9022` | `ZREPORT_IS_NOT_CLOSED`                |             |
| `0x9023` | `ZREPORT_IS_ALREADY_CLOSED`            |             |
| `0x9030` | `DATETIME_IS_IN_THE_PAST`              |             |
| `0x9031` | `SEND_ALL_RECEIPTS_FIRST`              |             |
| `0x9032` | `CANNOT_CLOSE_EMPTY_ZREPORT`           |             |
| `0x9033` | `RECIPT_SEQ_MAX_VALUE_REACHED`         |             |
| `0x9034` | `CASH_CARD_ACCUMULATOR_OVERFLOW`       |             |
| `0x9035` | `NOT_ENOUGH_SUM_FOR_REFUND`            |             |
| `0x9036` | `VAT_ACCUMULATOR_OVERFLOW`             |             |
| `0x9037` | `NOT_ENOUGH_VAT_FOR_REFUND`            |             |
| `0x9040` | `TOTAL_COUNT_OVERFLOW_OPEN_NEW_ZREPORT`|             |
| `0x9041` | `TOTAL_CASH_OVERFLOW_OPEN_NEW_ZREPORT` |             |
| `0x9042` | `TOTAL_CARD_OVERFLOW_OPEN_NEW_ZREPORT` |             |
| `0x9043` | `TOTAL_VAT_OVERFLOW_OPEN_NEW_ZREPORT`  |             |
| `0x9044` | `CASH_ACCUMULATOR_OVERFLOW`            |             |
| `0x9045` | `CARD_ACCUMULATOR_OVERFLOW`            |             |
| `0x9090` | `LOCKED_SYNC_WITH_SERVER`              |             |
| `0x9091` | `DATETIME_SYNC_WITH_SERVER`            |             |

# ИНСТРУКЦИЯ РАЗРАБОТЧИКА КОНТРОЛЬНО-КАССОВОЙ МАШИНЫ НА БАЗЕ ОС ANDROID ДЛЯ ИНТЕГРАЦИИ ФИСКАЛЬНОГО МОДУЛЯ ВЕРСИИ 0400

## ТЕРМИНЫ И ОПРЕДЕЛЕНИЯ

* __ОС__ - Операционная система
* __ККМ__ - Котрольно-кассовая машина.
* __ISO7816__ - Стандарт относится к смарт-картам (в первую очередь контактным). Описывает форму карты, контактов, их расположение и назначение; протоколы обмена и некоторые аспекты работы с данными. 
* __ФМ__ - Фискальный модуль, смарт-карта или USB-токен с ОС JavaCard и установленным апплетов выполняющего функцию фискального модуля.
* __ФП__ - Фискальный признак чека.
* __TLV__ - «Tag-Length-Value» широко распространённый метод записи коротких данных в компьютерных файлах и телекоммуникационных протоколах. 
* __ZReport__ - Запись в ФМ хранит дату-время открытия и закрытия дня и накопленные суммы продаж, возвратов и НДС.
* __ZReportFile__ - Зашифрованный и подписанны ЭЦП ZReport-файл, предназначен для отправки на сервер.
* __Receipt__ - Запись в ФМ хранит дату-время, тип, сумму чека до тех пор пока не произойдет синхронизация с сервером.
* __ReceiptFile__ - Зашифрованный и подписанны ЭЦП Receipt-файл, предназначен для отправки на сервер.
* __AckFile__ - Файл получаемый от сервера для передачи в ФМ для подтверждения доставки ZReport и Receipt.  
* __SyncFile__ - Файл получаемый от сервера для передачи в ФМ для синхронизации времени, состояния ФМ с сервером.  
* __SyncChallenge__ - Применяется при синхронизации времени, состояния ФМ с сервером.
* __reverse-индекс__ - Индекс где 0 - текущий элемент, 1 - предыдущий и т.д.
* __absolute-индекс__ - Абсолютный индекс в памяти ФМ где хранится Receipt или ZReport.



## ВВЕДЕНИЕ

В инструкции приведены описания и принцып работы ФМ версии 0400, примерный проект android-приложения и описания методов для взаимодействия с ФМ

## ФМ версия 0400

Отличия ФМ версии 0400 от прежних версий:

* __Защита от случайной установки будущей даты-врмени__, еслм передаваемое дата-время больше на 2 дня чем дата-время последней операции в ФМ, то ФМ возвратом кода ошибки потребует синхронизацию с сервером для установки серверного даты-время в ФМ.
* __Нет ограничения на кол-во записей ZReport__, Максимальное кол-во зависит от доступной физ.памяти ФМ.
* __Кол-во операции продажа и возврат в одном ZReport до 29999__, в прежних версиях ФМ было до 9999.
* __Нет ограничения на кол-во записей Receipt__, Максимальное кол-во зависит от доступной физ.памяти ФМ.
* __Добавлены типы чеков Аванс и Кредит__, Для этих типов чеков ФМ также выдает порядковый номер чека, но не выдает ФП.
* __Добавлена возможность аутентификации по ФМ__ в IT-системах.
* __Добавлена опция привязки ФМ к POS-системе или ККМ__, при открытии/закрытии ZReport или регистрации чека ФМ потребует секретный ключ.
* __Идемпотентность операций ФМ__, При регистрации чека передача одной и той же команды (много раз) в ФМ будет выполненя одна операция и выдача одного и того же результата (много раз).
* __Выполнена оптимизация работы ФМ__, Сокращено кол-во инструкций и возвращаемых кодов ошибок в ФМ. Данные из ФМ можно запрашивать частично передавая TLV-теги запрашиваемых полей.
* __Разработан эмулятор ФМ__, Для разработки и тестирования ПО для ККМ больше не требуется физ.ФМ, можно запустить программный эмулятор ФМ версии 0400 (входит в состав ПО FiscalDriveService v10).
* __Нет обратной совместимости__ по кодам инструкций, кодам возврата и структурой данных с ФМ прежних версий.

Организация памяти

Разные модели смарт-карт имеют разный объем доступной памяти. Апплет ФМ при открытии ZReport пытается зарезервировать блок памяти для хранения ZReport из доступной памяти. Также и при регистрации нового Receipt. При этом блоки памяти для Receipt которые уже отправлены на сервер и для них был получен AckFile будут переиспользованы для хранения нового Receipt. Следует вовремя отправлять Receipt на сервер, иначе вся доступная память будет заполнена блоками Receipt и при попытке открыть новый ZReport может нехватить блок памяти для нового ZReport.

Пример:

Память нового ФМ

`ZREPORTS_ALLOCATED` = 0
`RECEIPTS_ALLOCATED` = 0
`ZREPORTS_COUNT` = 0
`RECEIPTS_COUNT` = 0

|`   `|`   `|`   `|`   `|`   `|`   `|`   `|`   `|`   `|`   `|`   `|`   `|`   `|`   `|
|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|

После открытия ZReport

`ZREPORTS_ALLOCATED` = 1
`RECEIPTS_ALLOCATED` = 0
`ZREPORTS_COUNT` = 1
`RECEIPTS_COUNT` = 0

|`Z1 `|`   `|`   `|`   `|`   `|`   `|`   `|`   `|`   `|`   `|`   `|`   `|`   `|`   `|
|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|

После регистрации Receipt

`ZREPORTS_ALLOCATED` = 1
`RECEIPTS_ALLOCATED` = 1
`ZREPORTS_COUNT` = 1
`RECEIPTS_COUNT` = 1

|`Z1 `|`   `|`   `|`   `|`   `|`   `|`   `|`   `|`   `|`   `|`   `|`   `|`   `|`R1 `|
|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|

После регистрации 7 Receipt

`ZREPORTS_ALLOCATED` = 1
`RECEIPTS_ALLOCATED` = 8
`ZREPORTS_COUNT` = 1
`RECEIPTS_COUNT` = 8

|`Z1 `|`   `|`   `|`   `|`   `|`   `|`R8 `|`R7 `|`R6 `|`R5 `|`R4 `|`R3 `|`R2 `|`R1 `|
|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|

После закрытия, отправки и открытия ZReport

`ZREPORTS_ALLOCATED` = 2
`RECEIPTS_ALLOCATED` = 8
`ZREPORTS_COUNT` = 2
`RECEIPTS_COUNT` = 8

|`Z1 `|`Z2 `|`   `|`   `|`   `|`   `|`R8 `|`R7 `|`R6 `|`R5 `|`R4 `|`R3 `|`R2 `|`R1 `|
|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|

После отправки Receipt R8,R7,R6 и получения AckFile

`ZREPORTS_ALLOCATED` = 2
`RECEIPTS_ALLOCATED` = 8
`ZREPORTS_COUNT` = 2
`RECEIPTS_COUNT` = 5

|`Z1 `|`Z2 `|`   `|`   `|`   `|`   `|`R. `|`R. `|`R. `|`R5 `|`R4 `|`R3 `|`R2 `|`R1 `|
|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|

После регистрации 2 Receipt

`ZREPORTS_ALLOCATED` = 2
`RECEIPTS_ALLOCATED` = 8
`ZREPORTS_COUNT` = 2
`RECEIPTS_COUNT` = 7

|`Z1 `|`Z2 `|`   `|`   `|`   `|`   `|`R. `|`R10`|`R9 `|`R5 `|`R4 `|`R3 `|`R2 `|`R1 `|
|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|

После отправки всех Receipt и получения AckFile

`ZREPORTS_ALLOCATED` = 2
`RECEIPTS_ALLOCATED` = 8
`ZREPORTS_COUNT` = 2
`RECEIPTS_COUNT` = 0

|`Z1 `|`Z2 `|`   `|`   `|`   `|`   `|`R. `|`R. `|`R. `|`R. `|`R. `|`R. `|`R. `|`R. `|
|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|

После закрытия, отправки и открытия ZReport

`ZREPORTS_ALLOCATED` = 3
`RECEIPTS_ALLOCATED` = 8
`ZREPORTS_COUNT` = 3
`RECEIPTS_COUNT` = 0

|`Z1 `|`Z2 `|`Z3 `|`   `|`   `|`   `|`R. `|`R. `|`R. `|`R. `|`R. `|`R. `|`R. `|`R. `|
|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|

Случай когда нет возможности открытия ZReport

`ZREPORTS_ALLOCATED` = 4
`RECEIPTS_ALLOCATED` = 10
`ZREPORTS_COUNT` = 4
`RECEIPTS_COUNT` = 5

|`Z1 `|`Z2 `|`Z3 `|`Z4 `|`R. `|`R. `|`R42`|`R41`|`R40`|`R. `|`R. `|`R27`|`R11`|`R. `|
|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|

## Коды возврата SW

ФМ всегда (по ISO7816) возвращает 2х байтовый SW-код после выполнения инструкции. Если ответ от ФМ не вернулся,то скорее всего проблема в драйвере SAM-считывателя.

| Код      | Название                                | Описание                                                                                                                                                                                                    |
|----------|-----------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `0x9000` | `NO_ERROR`                              | Успешно                                                                                                                                                                                                     |
| `0x9010` | `INVALID_DATETIME`                      | Передано неправильное значение даты-времени (должно быть в формате BCDDateTime)                                                                                                                             |
| `0x9011` | `INVALID_INDEX`                         | Передано неправильное значение индекса (должно быть от 0 до 32767)                                                                                                                                          |
| `0x9012` | `INVALID_BCD`                           | Передано неправильное значение числа (должно быть в формате BCD8)                                                                                                                                           |
| `0x9013` | `INVALID_TYPE`                          | Передано неправильное значение типа чека                                                                                                                                                                    |
| `0x9014` | `INVALID_OPERATION`                     | Передано неправильное значение типа операции                                                                                                                                                                |
| `0x9015` | `INVALID_ACK_SIGNATURE`                 | Передано неправильное значение AckFile                                                                                                                                                                      |
| `0x9016` | `WRONG_TERMINAL_ID`                     | Передано неправильное значение AckFile (возможно AckFile не для этого ФМ)                                                                                                                                   |
| `0x9017` | `INVALID_SYNC_SIGNATURE`                | Передано неправильное значение SyncFile                                                                                                                                                                     |
| `0x9018` | `WRONG_SYNC_CHALLENGE`                  | Передано неправильное значение SyncChallenge (возможно был повторно передан прежний SyncFile)                                                                                                               |
| `0x9020` | `NOT_FOUND`                             | Запрашиваемая запись ненайдена. Может быть при повторной передачи AckFile в ФМ или при передачи индекса для получения записи которого нет в ФМ                                                              |
| `0x9021` | `ZREPORT_IS_NOT_OPENED`                 | Не выполнена операция открытия ZReport                                                                                                                                                                      |
| `0x9022` | `ZREPORT_IS_NOT_CLOSED`                 | Не выполнена операция заккрытия ZReport                                                                                                                                                                     |
| `0x9023` | `ZREPORT_IS_ALREADY_CLOSED`             | ZReport уже был закрыт, выполните операцию открытия ZReport                                                                                                                                                 |
| `0x9030` | `DATETIME_IS_IN_THE_PAST`               | Передаваемое дата-время ранее (прошлое) или равно дате-время последней операции в ФМ (Передаваемое дата-время должно быть как минимум на 1 сек. позьже чем дата-время последней операции в ФМ)              |
| `0x9031` | `SEND_ALL_RECEIPTS_FIRST`               | В ФМ хранятся чеки более чем 2 дня, выполните синхронизацию файлов с сервером                                                                                                                               |
| `0x9032` | `CANNOT_CLOSE_EMPTY_ZREPORT`            | Нельзя выполнить операцию заккрытия ZReport если в нем не заригистрирована минимум 1 операция продажи или возврата                                                                                          |
| `0x9033` | `RECIPT_SEQ_MAX_VALUE_REACHED`          | Достигнут максимальный номмер чека в ФМ. Следует заменить ФМ на новый.                                                                                                                                      |
| `0x9034` | `CASH_CARD_ACCUMULATOR_OVERFLOW`        | Достигнута максимальная накопленная сумма в фискальной памяти ФМ при операции возврат. Следует заменить ФМ на новый.                                                                                        |
| `0x9035` | `NOT_ENOUGH_SUM_FOR_REFUND`             | В фискальной памяти ФМ надостаточно суммы от продаж для выполнения операции возврат                                                                                                                         |
| `0x9036` | `VAT_ACCUMULATOR_OVERFLOW`              | Достигнута максимальная накопленная сумма НДС в фискальной памяти ФМ. Следует заменить ФМ на новый.                                                                                                         |
| `0x9037` | `NOT_ENOUGH_VAT_FOR_REFUND`             | В фискальной памяти ФМ надостаточно суммы НДС от продаж для выполнения операции возврат                                                                                                                     |
| `0x9040` | `TOTAL_COUNT_OVERFLOW_OPEN_NEW_ZREPORT` | Достигнуто максимальное кол-во операций (29999) в текущем ZReport. Закройта текущий ZReport и откройте новый.                                                                                               |
| `0x9041` | `TOTAL_CASH_OVERFLOW_OPEN_NEW_ZREPORT`  | Достигнута максимальная накопленная сумма в текущем ZReport. Закройта текущий ZReport и откройте новый.                                                                                                     |
| `0x9042` | `TOTAL_CARD_OVERFLOW_OPEN_NEW_ZREPORT`  | Достигнута максимальная накопленная сумма в текущем ZReport. Закройта текущий ZReport и откройте новый.                                                                                                     |
| `0x9043` | `TOTAL_VAT_OVERFLOW_OPEN_NEW_ZREPORT`   | Достигнута максимальная накопленная сумма НДС в текущем ZReport. Закройта текущий ZReport и откройте новый.                                                                                                 |
| `0x9044` | `CASH_ACCUMULATOR_OVERFLOW`             | Достигнута максимальная накопленная сумма в фискальной памяти ФМ при операции продажа. Следует заменить ФМ на новый.                                                                                        |
| `0x9045` | `CARD_ACCUMULATOR_OVERFLOW`             | Достигнута максимальная накопленная сумма в фискальной памяти ФМ при операции продажа. Следует заменить ФМ на новый.                                                                                        |
| `0x9090` | `LOCKED_SYNC_WITH_SERVER`               | ФМ заблокирован. Требуется синхронизация состояния ФМ с сервером.                                                                                                                                           |
| `0x9091` | `DATETIME_SYNC_WITH_SERVER`             | Передаваемое дата-время больше на 2 дня чем дата-время последней операции в ФМ. Настройте реальное время в ККМ и повторите попытку или выполните синхронизацию состояния ФМ с сервером и повторите попытку. |
| `0x9092` | `ALREADY_POS_LOCKED`                    | ФМ уже привязан к POS-системе или к ККМ секретным ключем.                                                                                                                                                   |
| `0x9093` | `POS_AUTH_FAIL`                         | ФМ тебует передачи правильного секретного ключа для идентификации POS-системы или ККМ.                                                                                                                      |
| `0x90F0` | `ZREPORTS_MEMORY_FULL`                  | Память ФМ для хранения ZReport заполнена. Следует заменить ФМ на новый.                                                                                                                                     | 
| `0x90F1` | `RECEIPTS_MEMORY_FULL`                  | Память ФМ для хранения Receipt заполнена, выполните синхронизацию файлов с сервером для освобождения памяти.                                                                                                | 
| `0x90FF` | `NOT_ENOUGH_MEMORY`                     | Память ФМ заполнена, выполните синхронизацию файлов (и синхронизация состояния ФМ) с сервером для освобождения памяти.                                                                                      |
| `0x6A86` | `INCORRECT_P1P2`                        | Передано неправильное значение параметров P1,P2                                                                                                                                                             |
| `0x6D00` | `INS_NOT_SUPPORTED`                     | Передано неправильное значение параметра INS                                                                                                                                                                |
| `0x6700` | `WRONG_LENGTH`                          | Передано неправильный размер DATA                                                                                                                                                                           |
| `0x6A80` | `WRONG_DATA`                            | Передано неправильное значение DATA                                                                                                                                                                         |
| `0x6F00` | `UNKNOWN`                               | Обычно данная ошибка возникает при недостатке памяти или ее повреждения. Проверьте кол-во свободной памяти ФМ. Попробуйте выполнить синхронизацию состояния ФМ с сервером и повторите попытку.              |

_Другие коды возвращаются OC смарт-карты. См._  [Complete list of APDU responses](https://www.eftlab.com/knowledge-base/complete-list-of-apdu-responses)

## Инструкции

| Название                              | CLA     | INS    | P1     | P2     | DATA             | Возврат                    | Описание                                                                                                                                                                                                                                                       |
|---------------------------------------|---------|--------|--------|--------|------------------|----------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `GET_VERSION`                         | `0x00`  | `0x00` | `0x00` | `0x00` |                  | `short` + `SW`             | Получить версию апплета <br /><sub>(см. класс _uz.yt.ofd.android.lib.applet.command.GetVersionCommand_)</sub>                                                                                                                                                  |
| `GET_INFO`                            | `0x00`  | `0x00` | `0x01` | `0x00` | `список тегов`   | `Info` + `SW`              | Получить информацию об ФМ, в DATA можно передавать список тегов полей (`[]byte`) (см. класс _uz.yt.ofd.android.lib.applet.command.GetInfoCommand_)                                                                                                             |
| `GET_FISCAL_MEMORY_INFO`              | `0x00`  | `0x00` | `0x02` | `0x00` | `список тегов`   | `FiscalMemoryInfo` + `SW`  | Получить информацию о фискальной памяти ФМ, в DATA можно передавать список тегов полей (`[]byte`) (см. класс _uz.yt.ofd.android.lib.applet.command.GetFiscalMemoryInfoCommand_)                                                                                |
| `GET_UNACKNOWLEDGED_ZREPORTS_INDEXES` | `0x00`  | `0x00` | `0x03` | `0x00` |                  | `short` + `[]short` + `SW` | Получить reverse-индексы неотправленных на сервер ZReport (см. класс _uz.yt.ofd.android.lib.applet.command.GetUnackowledgedZReportsIndexesCommand_)                                                                                                            |
| `GET_ZREPORT_INFO`                    | `0x00`  | `0x01` | `0xXX` | `0xYY` | `список тегов`   | `ZReportInfo` + `SW`       | Получить информацию о ZReport, в P1,P2 передать reverse-индекс а в DATA можно передавать список тегов полей (`[]byte`) (см. класс _uz.yt.ofd.android.lib.applet.command.GetZReportInfoCommand_)                                                                |
| `GET_ZREPORT_FILE`                    | `0x00`  | `0x02` | `0xXX` | `0xYY` |                  | `ZReportFile` + `SW`       | Получить файл ZReport для отправки на сервер (в P1,P2 передать reverse-индекс) (см. класс _uz.yt.ofd.android.lib.applet.command.GetZReportFileCommand_)                                                                                                        |
| `ZREPORT_OPEN`                        | `0x00`  | `0x03` | `0x00` | `0x00` | `дата-время`     | `SW`                       | Открыть ZReport передав дату-время в формате `BCDDateTime` (см. класс _uz.yt.ofd.android.lib.applet.command.OpenCloseZReportCommand_)                                                                                                                          |
| `ZREPORT_CLOSE`                       | `0x00`  | `0x03` | `0x01` | `0x00` | `дата-время`     | `SW`                       | Закрыть ZReport передав дату-время в формате `BCDDateTime` (см. класс _uz.yt.ofd.android.lib.applet.command.OpenCloseZReportCommand_)                                                                                                                          |
| `GET_RECEIPT_INFO`                    | `0x00`  | `0x05` | `0xXX` | `0xYY` | `список тегов`   | `ReceiptInfo` + `SW`       | Получить информацию о Receipt, в P1,P2 передать reverse-индекс а в DATA можно передавать список тегов полей (`[]byte`) (см. класс _uz.yt.ofd.android.lib.applet.command.GetReceiptInfoCommand_)                                                                |
| `GET_RECEIPT_FILE`                    | `0x00`  | `0x06` | `0xXX` | `0xYY` |                  | `ReceiptFile` + `SW`       | Получить файл Receipt для отправки на сервер (в P1,P2 передать reverse-индекс) (см. класс _uz.yt.ofd.android.lib.applet.command.GetReceiptFileCommand_)                                                                                                        |
| `ACK`                                 | `0x00`  | `0x09` | `0xXX` | `0xYY` | `AckFile`        | `SW`                       | Передать в ФМ `AckFile` (для быстрого выполнения нужно в P1,P2 передать absolute-индекс ZReport/Receipt в памяти ФМ) от сервера полученный в ответ на передачу `ZReportFile` или `ReceiptFile` (см. класс _uz.yt.ofd.android.lib.applet.command.AckCommand_)   |
| `SYNC`                                | `0x00`  | `0x11` | `0x00` | `0x00` | `SyncFile`       | `SW`                       | Синхронизации времени, состояния ФМ с сервером (см. класс _uz.yt.ofd.android.lib.applet.command.SyncCommand_)                                                                                                                                                  |
| `RECEIPT_REGISTER`                    | `0x00`  | `0x17` | `0x00` | `0x00` | `TotalBlock`     | `FiscalSignInfo` + `SW`    | Зарегистрировать чек передав `TotalBlock` и получить в ответ ФП (см. класс _uz.yt.ofd.android.lib.applet.command.RegisterReceiptCommand_)                                                                                                                      |
| `SIGNED_CHALLENGE_AUTH`               | `0x00`  | `0x0b` | `0x00` | `0x00` | `Challenge`      | `SignedChallenge` + `SW`   | Аутентификации по ФМ передав `Challenge` и получить в ответ подписанный `Challenge` (см. класс _uz.yt.ofd.android.lib.applet.command.SignedChallengeAuthCommand_)                                                                                              |
| `POS_LOCK`                            | `0x00`  | `0x0c` | `0x00` | `0x00` | `секретный ключ` | `SW`                       | Привязка ФМ к ККМ (см. класс _uz.yt.ofd.android.lib.applet.command.POSLockCommand_)                                                                                                                                                                            |
| `POS_CHALLENGE`                       | `0x00`  | `0x0c` | `0x01` | `0x00` |                  | `POSChallenge` + `SW`      | Запрос `POSChallenge` из ФМ (см. класс _uz.yt.ofd.android.lib.applet.command.POSChallengeCommand_)                                                                                                                                                             |
| `POS_AUTH`                            | `0x00`  | `0x0c` | `0x02` | `0x00` | `POSAuth`        | `SW`                       | Передать `POSAuth = SHA-256(секретный ключ + POSChallenge)` в ФМ после которого можно выполнить операцию открытии/закрытии ZReport или регистрации чека ФМ (см. класс _uz.yt.ofd.android.lib.applet.command.POSAuthCommand_)                                   |

## Описание ACR-SIM

### Подключение к SAM-слоту и серверу ОФД

![](img/0.png)

Для работы с ФМ подключенному к SAM-слоту ККМ выберите опцию `SAM Slot`, но перед этим вам нужно реализовать метод `getSamSlot` класса `uz.yt.ofd.acrsim.driver.SAMSlotProvider`, реализовать интерфейс `uz.yt.ofd.acrsim.SAMSlot` применяя библиотеки вашего ККМ для работы с SAM-слотом.

```java
    public static SAMSlot getSamSlot(int slotNumber) {
        // TODO: IMPLEMENT CLASS uz.yt.ofd.acrsim.driver.SAMSlotProvider ACCORDING TO YOUR DEVICE'S SPECIFICATION
        throw new UnsupportedOperationException("IMPLEMENT CLASS uz.yt.ofd.acrsim.driver.SAMSlotProvider ACCORDING TO YOUR DEVICE'S SPECIFICATION");
    }
```

Для разработки и тестирования можно использовать эмулятор ФМ, для этого выберите опцию `FD Emulator`. Введте в поле `Emulator TCP-address` IP-адрес и TCP-порт эмулятора и нажмите кнопку `SET`. При этом ККМ и компьютер (разработчика) где запущен эмулятор должны быть подключены к одной сети (Wi-Fi).

Для запуска эмулятора и тестового сервера ОФД нужно ПО FiscalDriveService установленное на компьютере разработчика.

Для запуска эмулятор ФМ введите команду:

```
fiscal-drive-service devtool fiscal-drive-emulator
```

> см. https://github.com/qo0p/fiscal-drive-service

В поле `OFD Server TCP-Addresses` введите IP-адрес и TCP-порт тестового сервера ОФД и нажмите кнопку `SET`, если адресов несколько, разделите их запятой.

Для запуска тестового сервера ОФД введите команду:

```
fiscal-drive-service devtool test-server
```

> см. https://github.com/qo0p/fiscal-drive-service

### Получение информации об ФМ

![](img/1.png)

- `GET VERSION` - Получить номер версии ФМ
- `GET INFO` - Получить информацию об ФМ. В поле `Tags` можно указать TLV-теги нужных вам полей:
    - `TAG_VERSION` = 0x01 - Версия ФМ
    - `TAG_CPLC` = 0x02 - Заводской номер ФМ
    - `TAG_TERMINAL_ID` = 0x03 - Серийный номер ФМ
    - `TAG_SYNC_CHALLENGE` = 0x04 - Challenge для синхронизации с сервером ОФД
    - `TAG_LOCKED` = 0x05 - ФМ блокирован или нет
    - `TAG_JCRE_VERSION` = 0x06 - Версия JCRE
    - `TAG_MODE` = 0x07 - Режим работы, тест или продакшн
    - `TAG_POS_LOCKED` = 0x08 - ФМ привязан к ККМ или нет
    - `TAG_POS_AUTH` = 0x09 - ФМ аутентифицировал ККМ
    - `TAG_PATCH` = 0x0a - Версия патча ФМ
    - `TAG_MEMORY` = 0x80 - Информация о доступной памяти ФМ
- `SYNC STATE WITH SERVER` - Синхронизировать состояния ФМ с сервером ОФД. Для установки серверного времени, разблокировки и др.
- `GET FISCAL MEMORY INFO` - Получить информацию о фискальной памяти. В поле `Tags` можно указать TLV-теги нужных вам полей:
    - `TAG_TERMINAL_ID` = 0x01 - Серийный номер ФМ
    - `TAG_RECEIPT_SEQ` = 0x02 - Текущий номер чека
    - `TAG_LAST_OPERATION_TIME` = 0x03 - Дата-время последней операции
    - `TAG_FIRST_UNACKNOWLEDGED_RECEIPT_TIME` = 0x04 - Дата-время первого неотправленного на сервер чека
    - `TAG_ZREPORTS_COUNT` = 0x05 - Кол-во ZReport
    - `TAG_RECEIPTS_COUNT` = 0x06 - Кол-во неотправленных Receipt
    - `TAG_ZREPORTS_CAPACITY` = 0x07 - Макс. кол-во ZReport которое можно открыть при условии что памяти ФМ достаточно
    - `TAG_RECEIPTS_CAPACITY` = 0x08 - Макс. кол-во Receipt которое можно зарегистрировать (не отправляя на сервер) при условии что памяти ФМ достаточно
    - `TAG_FREPORT_CURRENT_INDEX` = 0x09 - Текущий absolute-индекс записи который хранит информацию о фискальной памяти
    - `TAG_ZREPORT_CURRENT_INDEX` = 0x0a - Текущий absolute-индекс записи который хранит информацию о ZReport
    - `TAG_RECEIPT_CURRENT_INDEX` = 0x0b - Текущий absolute-индекс записи который хранит информацию о последнем зарегистрированном Receipt
    - `TAG_ZREPORTS_ALLOCATED` = 0x0c - Кол-во занятых блоков памяти для хранения ZReport
    - `TAG_RECEIPTS_ALLOCATED` = 0x0d - Кол-во занятых блоков памяти для хранения Receipt
    - `TAG_CASH_ACCUMULATOR` = 0x80 - Общая сумма наличности продажа/возврат
    - `TAG_CARD_ACCUMULATOR` = 0x81 - Общая сумма безналичности продажа/возврат
    - `TAG_VAT_ACCUMULATOR` = 0x82 - Общая сумма НДС продажа/возврат
- `GET UNACKNOWLEDGED ZREPORTS INDEXES` - Reverce-индексы неотправленных на сервер `ZReport`


### Работа с ZReport

![](img/2.png)

- `SET CURRENT TIME` - Записать в поле ввода текущюю дату-время
- `OPEN ZREPORT` - Открыть ZReport с датой-временем из поля ввода
- `CLOSE ZREPORT` - Закрыть ZReport с датой-временем из поля ввода
- `GET ZREPORT INFO` - Получить информацию о ZReport по reverse-индексу в поле `Index` (_0_ - текущий, _1_ - предыдущий, ...). В поле `Tags` можно указать TLV-теги нужных вам полей:
    - `TAG_TERMINAL_ID` = 0x01 - Серийный номер ФМ
    - `TAG_OPEN_TIME` = 0x02 - Дата-время открытия ZReport
    - `TAG_CLOSE_TIME` = 0x03 - Дата-время закрытия ZReport
    - `TAG_TOTAL_SALE_COUNT` = 0x04 - Кол-во операций продажа
    - `TAG_TOTAL_REFUND_COUNT` = 0x05 - Кол-во операций возврат
    - `TAG_LAST_RECEIPT_SEQ` = 0x06 - Последний номер чека
    - `TAG_ACKNOWLEDGED_TIME` = 0x07 - Дата-время отправки ZReport на сервер ОФД
    - `TAG_FIRST_RECEIPT_SEQ` = 0x08 - Первый номер чека
    - `TAG_TOTAL_CASH` = 0x80 - Общая сумма наличности продажа/возврат
    - `TAG_TOTAL_CARD` = 0x81 - Общая сумма безналичности продажа/возврат
    - `TAG_TOTAL_VAT` = 0x82 - Общая сумма НДС продажа/возврат
- `GET ZREPORT FILE` - Получить информацию о ZReportFile по reverse-индексу в поле `Index` (_0_ - текущий, _1_ - предыдущий, ...)
- `SYNC ZREPORT FILE WITH SERVER` - Отправить ZReportFile (по reverse-индексу в поле `Index`) на сервер ОФД.


### Работа с Receipt

![](img/3.png)

Выберите тип чека:
- `Purchase` - Чек покупки
- `Advance` - Авансовый чек
- `Credit` - Кредитный чек

Выберите операцию:
- `Sale` - Продажа
- `Refund` - Возврат

`Target sum` - целевая сумма тестового чека

- `SET CURRENT TIME` - Записать в поле ввода текущюю дату-время
- `GENERATE TEST RECEIPT` - Сгенерировать тестовый JSON-чек с целевой суммой
- `GET REGISTERED RECEIPT TX ID` - Записать тестовый JSON-чек в БД и получить идентифиактор чека
- `REGISTER RECEIPT WITH TX ID` - Зарегистрировать сумму тестового чека из БД (по идентифиактор чека в поле `TX ID`) в ФМ и получить ФП. Эту операцию можно выполнять повторно если по техническим причинам произойдет обрыв связи с ФМ, будет возвращен один и тот же ФП для данного чека.
- `GET RECEIPT INFO` - Получить информацию о Receipt по reverse-индексу в поле `Index` (_0_ - последний, _1_ - раний, ...). В поле `Tags` можно указать TLV-теги нужных вам полей:
    - `TAG_TERMINAL_ID` = 0x01 - Серийный номер ФМ
    - `TAG_RECEIPT_SEQ` = 0x02 - Номер чека
    - `TAG_TIME` = 0x03 - Дата-время чека
    - `TAG_FISCAL_SIGN` = 0x04 - ФП чека
    - `TAG_TYPE` = 0x05 - Тип чека
    - `TAG_OPERATION` = 0x06 - Операция
    - `TAG_RECEIVED_CASH` = 0x07 - Сумма наличности
    - `TAG_RECEIVED_CARD` = 0x08 - Сумма безналичности
    - `TAG_TOTAL_VAT` = 0x09 - Сумма НДС
    - `TAG_ITEMS_COUNT` = 0x0a - Кол-во товаров услуг в теле чека
    - `TAG_EXTRA` = 0x0e - Доп. поля
    - `TAG_ITEMS_HASH` = 0x0f - Хеш-значение тела чека
- `GET RECEIPT FILE` - Получить информацию о ReceiptFile по reverse-индексу в поле `Index` (_0_ - последний, _1_ - раний, ...)


### Отправка файлов на сервер ОФД

![](img/4.png)

Выберите тип отправляемых файлов:
- `All Items` - ReceiptFile и тело чека, ZReportFile
- `Only ZReports` - только ZReportFile
- `Only Short Receipts` - только ReceiptFile, **только в случае если тело чека было утеряно, стерта БД !**

- `SYNC FILES WITH SERVER` - Отправить ReceiptFile и тело чека, ZReportFile на сервер ОФД в количестве не более `Sync Items count` за одну операцию, получить в ответ AckFile и передать в ФМ.

### Аутентификации по ФМ

![](img/5.png)

Для аутентификации по ФМ на сайте (или API) который поддерживает данную функцию, API сайта возвращает Challenge который передается в ФМ, ФМ возвращает подписанный ответ. Подписанный ответ следует отправить на API сайта для верификации и получения Access-Token. Далее по Access-Token вызывает закрытые методы API сайта.

### Привязка ФМ к ККМ

![](img/6.png)

Привязка ФМ к ККМ нужна для защиты от постороннего использования ФМ другими ККМ, например ФМ могут вынуть из ККМ одного ЦТО и установить в ККМ другого ЦТО (без переоформления и уведомления) и использовать.

Для привязки, каждое ЦТО генерирует свой секретный ключ 32-байт и устанавливает этот ключ в ФМ а также с свое ПО в защищенную память ККМ.
После привязки перед тем как ПО ККМ выполнить операцию открытии/закрытии ZReport или регистрации чека ФМ, ПО ККМ запрашивает POSChallenge из ФМ, выполняет операцию `POSAuth = SHA-256(секретный ключ + POSChallenge)` отпарвляет POSAuth в ФМ и только потом следом выполняет операцию открытии/закрытии ZReport или регистрации чека ФМ. Если секретный ключ не совпадет то ФМ не даст выполнить операцию.


- `LOCK WITH POS SECRET` - Устанавливает секретный ключ в ФМ
- `AUTH WITH POS SECRET` - Аутентификация по секретному ключу в ФМ для тестирования
- `Use POS Lock` - Если включено то выполняет аутентификацию перед операцией открытии/закрытии ZReport или регистрации чека ФМ. См. код проекта.

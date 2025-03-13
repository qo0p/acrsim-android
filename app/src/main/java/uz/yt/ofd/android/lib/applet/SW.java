package uz.yt.ofd.android.lib.applet;

public enum SW {
    NO_ERROR((short) 0x9000),

    INVALID_DATETIME((short) 0x9010),
    INVALID_INDEX((short) 0x9011),
    INVALID_BCD((short) 0x9012),
    INVALID_TYPE((short) 0x9013),
    INVALID_OPERATION((short) 0x9014),
    INVALID_ACK_SIGNATURE((short) 0x9015),
    WRONG_TERMINAL_ID((short) 0x9016),
    INVALID_SYNC_SIGNATURE((short) 0x9017),
    WRONG_SYNC_CHALLENGE((short) 0x9018),

    NOT_FOUND((short) 0x9020),
    ZREPORT_IS_NOT_OPENED((short) 0x9021),
    ZREPORT_IS_NOT_CLOSED((short) 0x9022),
    ZREPORT_IS_ALREADY_CLOSED((short) 0x9023),

    DATETIME_IS_IN_THE_PAST((short) 0x9030),
    SEND_ALL_RECEIPTS_FIRST((short) 0x9031),
    CANNOT_CLOSE_EMPTY_ZREPORT((short) 0x9032),
    RECIPT_SEQ_MAX_VALUE_REACHED((short) 0x9033),
    CASH_CARD_ACCUMULATOR_OVERFLOW((short) 0x9034),
    NOT_ENOUGH_SUM_FOR_REFUND((short) 0x9035),
    VAT_ACCUMULATOR_OVERFLOW((short) 0x9036),
    NOT_ENOUGH_VAT_FOR_REFUND((short) 0x9037),

    TOTAL_COUNT_OVERFLOW_OPEN_NEW_ZREPORT((short) 0x9040),
    TOTAL_CASH_OVERFLOW_OPEN_NEW_ZREPORT((short) 0x9041),
    TOTAL_CARD_OVERFLOW_OPEN_NEW_ZREPORT((short) 0x9042),
    TOTAL_VAT_OVERFLOW_OPEN_NEW_ZREPORT((short) 0x9043),
    CASH_ACCUMULATOR_OVERFLOW((short) 0x9044),
    CARD_ACCUMULATOR_OVERFLOW((short) 0x9045),

    LOCKED_SYNC_WITH_SERVER((short) 0x9090),
    DATETIME_SYNC_WITH_SERVER((short) 0x9091),
    ALREADY_POS_LOCKED((short) 0x9092),
    POS_AUTH_FAIL((short) 0x9093),

    ZREPORTS_MEMORY_FULL((short) 0x90F0),
    RECEIPTS_MEMORY_FULL((short) 0x90F1),
    NOT_ENOUGH_MEMORY((short) 0x90FF),

    BYTES_REMAINING_00((short) 24832),
    WRONG_LENGTH((short) 26368),
    SECURITY_STATUS_NOT_SATISFIED((short) 27010),
    FILE_INVALID((short) 27011),
    DATA_INVALID((short) 27012),
    CONDITIONS_NOT_SATISFIED((short) 27013),
    COMMAND_NOT_ALLOWED((short) 27014),
    APPLET_SELECT_FAILED((short) 27033),
    WRONG_DATA((short) 27264),
    FUNC_NOT_SUPPORTED((short) 27265),
    FILE_NOT_FOUND((short) 27266),
    RECORD_NOT_FOUND((short) 27267),
    INCORRECT_P1P2((short) 27270),
    WRONG_P1P2((short) 27392),
    CORRECT_LENGTH_00((short) 27648),
    INS_NOT_SUPPORTED((short) 27904),
    CLA_NOT_SUPPORTED((short) 28160),
    UNKNOWN((short) 28416),
    FILE_FULL((short) 27268),
    LOGICAL_CHANNEL_NOT_SUPPORTED((short) 26753),
    SECURE_MESSAGING_NOT_SUPPORTED((short) 26754),
    WARNING_STATE_UNCHANGED((short) 25088),
    LAST_COMMAND_EXPECTED((short) 26755),
    COMMAND_CHAINING_NOT_SUPPORTED((short) 26756);

    public final short code;

    SW(short code) {
        this.code = code;
    }

    public static SW find(short code) {
        for (SW t : values()) {
            if (t.code == code) {
                return t;
            }
        }
        return null;
    }
}

package uz.yt.ofd.android.lib.applet;

public interface Instruction {

    byte INS_GET_INFO = (byte) 0x00;
    byte INS_GET_INFO_P1_GET_VERSION = (byte) 0x00;
    byte INS_GET_INFO_P1_GET_INFO = (byte) 0x01;
    byte INS_GET_INFO_P1_GET_FISCAL_MEMORY_INFO = (byte) 0x02;
    byte INS_GET_INFO_P1_GET_UNACKNOWLEDGED_ZREPORTS_INDEXES = (byte) 0x03;

    byte INS_GET_ZREPORT_INFO = (byte) 0x01;
    byte INS_GET_ZREPORT_FILE = (byte) 0x02;


    byte INS_ZREPORT = (byte) 0x03;
    byte INS_ZREPORT_P1_ZREPORT_OPEN = (byte) 0x00;
    byte INS_ZREPORT_P1_ZREPORT_CLOSE = (byte) 0x01;

    byte INS_RECEIPT = (byte) 0x17;
    byte P1_RECEIPT_REGISTER = (byte) 0x00;
    byte P1_RECEIPT_RESCAN = (byte) 0x01;

    byte INS_GET_RECEIPT_INFO = (byte) 0x05;
    byte INS_GET_RECEIPT_FILE = (byte) 0x06;

    byte INS_ACK = (byte) 0x09;


    byte INS_SIGNED_CHALLENGE_AUTH = (byte) 0x0b;

    byte INS_POS = (byte) 0x0c;
    byte P1_POS_LOCK = (byte) 0x00;
    byte P1_POS_CHALLENGE = (byte) 0x01;
    byte P1_POS_AUTH = (byte) 0x02;

    byte INS_SYNC = (byte) 0x11;

    byte[] GET_VERSION = {INS_GET_INFO, INS_GET_INFO_P1_GET_VERSION, 0x00};
    byte[] GET_INFO = {INS_GET_INFO, INS_GET_INFO_P1_GET_INFO, 0x00};
    byte[] GET_FISCAL_MEMORY_INFO = {INS_GET_INFO, INS_GET_INFO_P1_GET_FISCAL_MEMORY_INFO, 0x00};
    byte[] GET_UNACKNOWLEDGED_ZREPORTS_INDEXES = {INS_GET_INFO, INS_GET_INFO_P1_GET_UNACKNOWLEDGED_ZREPORTS_INDEXES, 0x00};

    byte[] OPEN_ZREPORT = {INS_ZREPORT, INS_ZREPORT_P1_ZREPORT_OPEN, 0x00};
    byte[] CLOSE_ZREPORT = {INS_ZREPORT, INS_ZREPORT_P1_ZREPORT_CLOSE, 0x00};

    byte[] GET_REGISTER_RECEIPT = {INS_RECEIPT, P1_RECEIPT_REGISTER, 0x00};
    byte[] GET_RESCAN_RECEIPT = {INS_RECEIPT, P1_RECEIPT_RESCAN, 0x00};

    byte[] ACK = {INS_ACK, 0x00, 0x00};


    byte[] POS_LOCK = {INS_POS, P1_POS_LOCK, 0x00};
    byte[] POS_CHALLENGE = {INS_POS, P1_POS_CHALLENGE, 0x00};
    byte[] POS_AUTH = {INS_POS, P1_POS_AUTH, 0x00};

}


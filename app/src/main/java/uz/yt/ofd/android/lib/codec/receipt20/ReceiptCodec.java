package uz.yt.ofd.android.lib.codec.receipt20;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import uz.yt.ofd.android.lib.codec.BCD8;
import uz.yt.ofd.android.lib.codec.BCDDateTime;
import uz.yt.ofd.android.lib.codec.HexBin;
import uz.yt.ofd.android.lib.codec.TerminalID;
import uz.yt.ofd.android.lib.codec.Utils;
import uz.yt.ofd.android.lib.codec.tlv.TLV;
import uz.yt.ofd.android.lib.codec.tlv.TLVEncodable;
import uz.yt.ofd.android.lib.crypto.OzDSt1106Digest;
import uz.yt.ofd.android.lib.validator.FiscalSignValidator;


public class ReceiptCodec {

    public static final byte VERSION = 20;

    private static byte TagReceipt = (byte) 0x8d;


    public static int STRING_MAX_SIZE = 63;
    public static int SPIC_MAX_SIZE = 18;
    public static int TIN_MAX_SIZE = 9;
    public static int PINFL_MAX_SIZE = 14;
    public static int PACKAGE_CODE_MAX_SIZE = 20;

    public static int PHONE_NUMBER_MAX_SIZE = 13;
    public static int CAR_NUMBER_MAX_SIZE = 8;
    public static int QR_PAYMENT_ID_MAX_SIZE = 36;
    public static int PPTID_MAX_SIZE = 16;
    public static int CARD_NUMBER_MAX_SIZE = 16;
    public static int OTHER_MAX_SIZE = 32;

    public static long BCD8_MAX_LIMIT = 9999999999999999l;
    public static long MAX_ALLOWED_DIFF = 10000l;

    public static int HASH_SIZE = 32;
    public static int RECEIPT_TYPE_SIZE = 1;
    public static int OPERATION_TYPE_SIZE = 1;
    public static int ITEMS_COUNT_SIZE = 2;
    public static int TOTAL_BLOCK_SIZE = HASH_SIZE + BCD8.SIZE + BCD8.SIZE + BCD8.SIZE + BCDDateTime.SIZE + RECEIPT_TYPE_SIZE + OPERATION_TYPE_SIZE + ITEMS_COUNT_SIZE;
    public static int EXTRA_DATA_MAX_SIZE = 32;

    public static int CRYPTO_BLOCK_SIZE = 8;


    private static void writeTotalBlock(byte[] tlvReceiptHash, long receivedCash, long receivedCard, long totalVAT, Date time, ReceiptType type, OperationType operation, short itemsCount, byte[] extra, OutputStream os) throws IOException {
        os.write(tlvReceiptHash);
        os.write(BCD8.fromLong(receivedCash).getBytes());
        os.write(BCD8.fromLong(receivedCard).getBytes());
        os.write(BCD8.fromLong(totalVAT).getBytes());
        os.write(BCDDateTime.toBytes(time));
        os.write(type.getValue());
        os.write(operation.getValue());
        os.write(Utils.short2bytes(itemsCount));
        if (extra != null) {
            if (extra.length > EXTRA_DATA_MAX_SIZE) {
                extra = Utils.slice(extra, 0, 32);
            }
            os.write(extra);
        }
    }


    public static void encode(Receipt receipt, OutputStream encodedReceipt, OutputStream totalBlock, FiscalSignValidator fiscalSignValidator) throws IOException, IllegalArgumentException {
        if (receipt.getOperation().equals(OperationType.Refund)) {
            if (receipt.getRefundInfo() == null) {
                throw new IllegalArgumentException("RefundInfo is undefined");
            }
            try {
                TerminalID.encode(receipt.getRefundInfo().getTerminalID());
            } catch (Throwable ex) {
                throw new IllegalArgumentException("Bad RefundInfo TerminalID value", ex);
            }
            long receiptSeq;
            byte[] receiptSeqRaw;
            try {
                receiptSeq = Long.parseUnsignedLong(receipt.getRefundInfo().getReceiptSeq());
                receiptSeqRaw = BCD8.fromLong(receiptSeq).getBytes();
            } catch (Throwable ex) {
                throw new IllegalArgumentException("Bad RefundInfo ReceiptSeq value", ex);
            }
            Date dateTime;
            byte[] dateTimeRaw;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                dateTime = sdf.parse(receipt.getRefundInfo().getDateTime());
                dateTimeRaw = BCDDateTime.toBytes(dateTime);
            } catch (Throwable ex) {
                throw new IllegalArgumentException("Bad RefundInfo DateTime value", ex);
            }
            byte[] fiscalSignRaw = HexBin.decode(receipt.getRefundInfo().getFiscalSign());
            if (fiscalSignRaw == null || fiscalSignRaw.length != 6) {
                throw new IllegalArgumentException("Bad RefundInfo FiscalSign value");
            }
            boolean valid = fiscalSignValidator.check(receipt.getRefundInfo().getTerminalID(), receiptSeqRaw, dateTimeRaw, fiscalSignRaw);
            if (!valid) {
                throw new IllegalArgumentException("RefundInfo is not valid");
            }
        }
        byte[] encodedReceiptBytes = receipt.encode();

        byte[] tlvReceipt = TLV.encode(TagReceipt, encodedReceiptBytes);
        int paddSize = CRYPTO_BLOCK_SIZE - (tlvReceipt.length % CRYPTO_BLOCK_SIZE);
        byte[] padd = new byte[paddSize];

        OzDSt1106Digest digest = new OzDSt1106Digest();
        digest.update(tlvReceipt, 0, tlvReceipt.length);
        digest.update(padd, 0, padd.length);
        byte[] tlvReceiptHash = new byte[digest.getDigestSize()];
        digest.doFinal(tlvReceiptHash, 0);

        byte[] extra = null;
        if (receipt.getItems().size() == 1 && !TLVEncodable.isEmpty(receipt.getItems().get(0).getSpic())) {
            ByteArrayOutputStream extraData = new ByteArrayOutputStream();
            TLVEncodable.writeString(ReceiptItem.TAG_SPIC, receipt.getItems().get(0).getSpic(), ReceiptCodec.SPIC_MAX_SIZE, extraData);
            extra = extraData.toByteArray();
        }

        writeTotalBlock(tlvReceiptHash, receipt.getReceivedCash(), receipt.getReceivedCard(), receipt.calcTotalVAT(), receipt.getTime(), receipt.getType(), receipt.getOperation(), (short) receipt.getItems().size(), extra, totalBlock);
        totalBlock.flush();

        encodedReceipt.write(tlvReceipt);
        encodedReceipt.write(padd);
        encodedReceipt.flush();
    }

}

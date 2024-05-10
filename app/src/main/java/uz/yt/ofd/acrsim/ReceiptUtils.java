package uz.yt.ofd.acrsim;

import java.security.SecureRandom;
import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;

import uz.yt.ofd.android.lib.codec.receipt20.CommissionInfo;
import uz.yt.ofd.android.lib.codec.receipt20.ExtraInfo;
import uz.yt.ofd.android.lib.codec.receipt20.Location;
import uz.yt.ofd.android.lib.codec.receipt20.OperationType;
import uz.yt.ofd.android.lib.codec.receipt20.Receipt;
import uz.yt.ofd.android.lib.codec.receipt20.ReceiptItem;
import uz.yt.ofd.android.lib.codec.receipt20.ReceiptType;
import uz.yt.ofd.android.lib.codec.receipt20.RefundInfo;

public class ReceiptUtils {

    protected static SecureRandom random = new SecureRandom();

    private static String generateFakeTIN() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            res.append(random.nextInt(10));
        }
        return res.toString();
    }

    private static String generateFakePINFL() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < 14; i++) {
            res.append(random.nextInt(10));
        }
        return res.toString();
    }

    private static String generateFakePhoneNumber() {
        StringBuilder res = new StringBuilder();
        res.append("998");
        for (int i = 0; i < 9; i++) {
            res.append(random.nextInt(10));
        }
        return res.toString();
    }

    private static String generateFakeCarNumber() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < 2; i++) {
            res.append(random.nextInt(10));
        }
        res.append((char) (Character.valueOf('A') + (random.nextInt(26))));
        for (int i = 0; i < 3; i++) {
            res.append(random.nextInt(10));
        }
        res.append((char) (Character.valueOf('A') + (random.nextInt(26))));
        res.append((char) (Character.valueOf('A') + (random.nextInt(26))));
        return res.toString();
    }

    public static ReceiptItem generateItem(char nameChar, long target, int vatPersent) {
        int nameLen = (Math.abs(random.nextInt()) % 80) + 4;
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < nameLen; i++) {
            name.append(nameChar);
        }
        StringBuilder barcode = new StringBuilder();
        StringBuilder label = new StringBuilder();
        for (int i = 0; i < 13; i++) {
            barcode.append((Math.abs(random.nextLong()) % 10));
            label.append((Math.abs(random.nextLong()) % 10));
        }
        StringBuilder pacode = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            pacode.append((Math.abs(random.nextLong()) % 10));
        }

        String spic = String.valueOf(Math.abs(random.nextLong()) % 100000000000000000l);
        long units = (long) ((Math.abs(random.nextLong()) % 1000000000) + 1000);
        long price = target;
        long vat = (long) ((price * vatPersent) / (100 + vatPersent));
        long amount = (Math.abs(random.nextLong()) % 4) * 1000 + 1000;
        long discount = 0;
        long other = 0;
        switch (Math.abs(random.nextInt()) % 4) {
            case 0:
                discount = 0;
                other = 0;
                break;
            case 1:
                discount = price;
                other = 0;
                break;
            case 2:
                discount = 0;
                other = price;
                break;
            case 3:
                discount = price / 3;
                other = price / 3;
                break;
        }
        price += discount + other;
        String comtin = null;
        String compinfl = null;
        if ((Math.abs(random.nextInt()) % 4) == 0) {
            comtin = generateFakeTIN();
            compinfl = generateFakePINFL();
            switch (Math.abs(random.nextInt()) % 3) {
                case 0:
                    compinfl = null;
                    break;
                case 1:
                    comtin = null;
                    break;
            }
        }
        byte ownerType = (byte) (Math.abs(random.nextLong()) % 3);
        return new ReceiptItem(name.toString(), barcode.toString(), label.toString(), spic, units, pacode.toString(), ownerType, price, (short) vatPersent, vat, amount, discount, other, (comtin == null && compinfl == null) ? null : new CommissionInfo(comtin, compinfl));
    }

    public static Receipt generateReceipt(long target, Date time, ReceiptType type, OperationType operation) {
        Integer count = (Math.abs(random.nextInt()) % 3) + 1;
        long[] prices = new long[count];
        long minPrice = target / count;
        long sum = minPrice * count;
        for (int i = 0; i < count; i++) {
            prices[i] = minPrice;
        }
        prices[count - 1] += target - sum;
        Integer vatPersent = 12;
        long totalCash = 0;
        long totalCard = 0;
        LinkedList<ReceiptItem> items = new LinkedList();

        for (int i = 0; i < count; i++) {
            ReceiptItem it = generateItem((char) (Character.valueOf('A') + (i % 26)), prices[i], vatPersent);
            if ((Math.abs(random.nextInt()) % 10) > 8) {
                totalCash += it.getPrice() - it.getDiscount() - it.getOther();
            } else {
                totalCard += it.getPrice() - it.getDiscount() - it.getOther();
            }
            items.add(it);
        }
//        int MAX_ALLOWED_DIFF = 10000;
//        long diff = (long) (Math.abs(random.nextInt()) % (MAX_ALLOWED_DIFF + 1));
//        int op = Math.abs(random.nextInt()) % 3;
//        if (op == 1) {
//            if (totalCard >= diff) {
//                totalCard -= diff;
//            } else if (totalCash >= diff) {
//                totalCash -= diff;
//            }
//        }
//        if (op == 2) {
//            if (totalCard != 0) {
//                totalCard += diff;
//            } else if (totalCash != 0) {
//                totalCash += diff;
//            }
//        }
        double lon = 69.218415f;
        double lat = 41.295800f;
        double dlat = (2 * random.nextDouble() - 1) / 1e3;
        double dlon = (2 * random.nextDouble() - 1) / 1e3;
        Location location = new Location(lat + dlat, lon + dlon);

        ExtraInfo extraInfo = new ExtraInfo();
        String comtin = null;
        String compinfl = null;
        if ((Math.abs(random.nextInt()) % 4) == 0) {
            comtin = generateFakeTIN();
            compinfl = generateFakePINFL();
            switch (Math.abs(random.nextInt()) % 3) {
                case 0:
                    compinfl = null;
                    break;
                case 1:
                    comtin = null;
                    break;
            }
        }
        extraInfo.setTin(comtin);
        extraInfo.setPinfl(compinfl);
        extraInfo.setCarNumber(generateFakeCarNumber());
        extraInfo.setPhoneNumber(generateFakePhoneNumber());
        extraInfo.setCardType((byte) (Math.abs(random.nextInt()) % 3));
        extraInfo.setCashedOutFromCard((Math.abs(random.nextLong()) % 10000) + 10000);
        extraInfo.setQrPaymentId(UUID.randomUUID().toString());
        extraInfo.setQrPaymentProvider((short) (Math.abs(random.nextInt()) % 100));
        extraInfo.setPptid(String.valueOf((short) (Math.abs(random.nextInt()) % 100)));

        RefundInfo refundInfo = null;
        if (type == ReceiptType.Purchase && operation == OperationType.Refund) {
            String terminalID = "ZZ000000000000";
            String receiptSeq = "1";
            String dateTime = "20240101090405";
            String fiscalSign = "000000000000";
            refundInfo = new RefundInfo(terminalID, receiptSeq, dateTime, fiscalSign);
        }
        Receipt receipt = new Receipt(items, totalCash, totalCard, time, type, operation, refundInfo, location, extraInfo);
        return receipt;
    }
}

package uz.yt.ofd.acrsim.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

import uz.yt.ofd.acrsim.db.dto.EncryptedFullReceiptFile;
import uz.yt.ofd.acrsim.db.dto.RegisterReceiptLog;

public class SQLiteStorage extends SQLiteOpenHelper implements Storage {

    final static String DB_NAME = "storage.db";
    final static int DB_VERSION = 5;


    // Creating table query
    private static final String CREATE_TABLE_1 = "CREATE TABLE \"receipt_register_log\" (\n" +
            "\t\"id\"\tINTEGER NOT NULL,\n" +
            "\t\"factory_id\"\tTEXT NOT NULL,\n" +
            "\t\"ts\"\tTEXT NOT NULL DEFAULT current_timestamp,\n" +
            "\t\"receipt_version\"\tINTEGER NOT NULL,\n" +
            "\t\"receipt_type\"\tINTEGER NOT NULL,\n" +
            "\t\"operation\"\tINTEGER NOT NULL,\n" +
            "\t\"tlv_receipt_body\"\tBLOB NOT NULL,\n" +
            "\t\"total_block\"\tBLOB NOT NULL,\n" +
            "\t\"error\"\tTEXT,\n" +
            "\t\"fiscal_sign_info\"\tBLOB,\n" +
            "\t\"terminal_id\"\tTEXT,\n" +
            "\t\"receipt_seq\"\tINTEGER,\n" +
            "\tPRIMARY KEY(\"id\" AUTOINCREMENT)\n" +
            ")";

    private static final String CREATE_TABLE_2 = "CREATE TABLE \"full_receipt\" (\n" +
            "\t\"factory_id\"\tTEXT NOT NULL,\n" +
            "\t\"terminal_id\"\tTEXT NOT NULL,\n" +
            "\t\"receipt_seq\"\tINTEGER NOT NULL,\n" +
            "\t\"time\"\tTEXT NOT NULL,\n" +
            "\t\"receipt_version\"\tINTEGER NOT NULL,\n" +
            "\t\"receipt_type\"\tINTEGER NOT NULL,\n" +
            "\t\"operation\"\tINTEGER NOT NULL,\n" +
            "\t\"file\"\tBLOB NOT NULL,\n" +
            "\t\"id\"\tINTEGER,\n" +
            "\t\"status\"\tINTEGER NOT NULL DEFAULT 0,\n" +
            "\tPRIMARY KEY(\"factory_id\",\"terminal_id\",\"receipt_seq\")\n" +
            ")";

    public SQLiteStorage(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_1);
        db.execSQL(CREATE_TABLE_2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
            // Код для работы с базой данных с версией 1
        }
        if (oldVersion < 3) {
            // Код для работы с базой данных с версией 1 или 2
        }
        db.execSQL("DROP TABLE IF EXISTS receipt_register_log");
        db.execSQL("DROP TABLE IF EXISTS full_receipt");
        onCreate(db);
    }

    @Override
    public Long newReceiptRegisterLog(String factoryID, byte receiptVersion, byte receiptType, byte operation, byte[] tlvEncodedReceiptRaw, byte[] totalBlockRaw) throws Exception {
        Long id = null;
        try {
            {
                SQLiteDatabase wdb = getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("factory_id", factoryID);
                values.put("receipt_version", receiptVersion);
                values.put("receipt_type", receiptType);
                values.put("operation", operation);
                values.put("tlv_receipt_body", tlvEncodedReceiptRaw);
                values.put("total_block", totalBlockRaw);

                wdb.insert("receipt_register_log", null, values);
            }
            {
                SQLiteDatabase rdb = getReadableDatabase();
                Cursor cursor = rdb.query("sqlite_sequence", new String[]{"seq"}, "name = ?", new String[]{"receipt_register_log"}, null, null, null);
                try {
                    if (!cursor.moveToNext()) {
                        throw new IllegalStateException("no record found in sqlite_sequence for table receipt_register_log");
                    }
                    id = cursor.getLong(cursor.getColumnIndexOrThrow("seq"));
                } finally {
                    cursor.close();
                }
            }
        } catch (Throwable t) {
            throw t;
        }
        return id;
    }

    @Override
    public RegisterReceiptLog getReceiptRegisterLog(String factoryID, Long id) throws Exception {
        try {
            SQLiteDatabase rdb = getReadableDatabase();
            Cursor cursor = rdb.query("receipt_register_log", new String[]{"receipt_version", "receipt_type", "operation", "tlv_receipt_body", "total_block"}, "id = ? and factory_id = ?", new String[]{String.valueOf(id), factoryID}, null, null, null);
            try {
                if (!cursor.moveToNext()) {
                    throw new IllegalStateException("no record found in table receipt_register_log with id = " + id);
                }
                return new RegisterReceiptLog(
                        id,
                        (byte) cursor.getInt(cursor.getColumnIndexOrThrow("receipt_version")),
                        (byte) cursor.getInt(cursor.getColumnIndexOrThrow("receipt_type")),
                        (byte) cursor.getInt(cursor.getColumnIndexOrThrow("operation")),
                        cursor.getBlob(cursor.getColumnIndexOrThrow("tlv_receipt_body")),
                        cursor.getBlob(cursor.getColumnIndexOrThrow("total_block")));
            } finally {
                cursor.close();
            }
        } catch (Throwable t) {
            throw t;
        }
    }

    @Override
    public void updateReceiptRegisterLog(String factoryID, Long id, byte[] fiscalSignRaw, String terminalID, Long receiptSeq) throws Exception {
        try {
            SQLiteDatabase wdb = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("factory_id", factoryID);
            values.put("fiscal_sign_info", fiscalSignRaw);
            values.put("terminal_id", terminalID);
            values.put("receipt_seq", receiptSeq);

            wdb.update("receipt_register_log", values, "id = ?", new String[]{String.valueOf(id)});
        } catch (Throwable t) {
            throw t;
        }
    }

    @Override
    public void updateReceiptRegisterLog(String factoryID, Long id, String errorMessage) throws Exception {
        try {
            SQLiteDatabase wdb = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("error", errorMessage);

            wdb.update("receipt_register_log", values, "id = ? and factory_id = ?", new String[]{String.valueOf(id), factoryID});
        } catch (Throwable t) {
            throw t;
        }
    }

    @Override
    public void newFullReceipt(String factoryID, String terminalID, Long receiptSeq, Date time, byte receiptVersion, byte receiptType, byte operation, byte[] file, Long id) throws Exception {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SQLiteDatabase wdb = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("factory_id", factoryID);
            values.put("terminal_id", terminalID);
            values.put("receipt_seq", receiptSeq);
            values.put("time", sdf.format(time));
            values.put("receipt_version", receiptVersion);
            values.put("receipt_type", receiptType);
            values.put("operation", operation);
            values.put("file", file);
            values.put("id", id);
            values.put("status", 0);

            wdb.insert("full_receipt", null, values);
        } catch (Throwable t) {
            throw t;
        }
    }

    @Override
    public EncryptedFullReceiptFile getFullReceipt(String factoryID, String terminalID, Long receiptSeq) throws Exception {
        try {
            SQLiteDatabase rdb = getReadableDatabase();
            Cursor cursor = rdb.query("full_receipt", new String[]{"file", "receipt_version", "receipt_type"}, "factory_id = ? and terminal_id = ? and receipt_seq = ? and status = ?", new String[]{factoryID, terminalID, String.valueOf(receiptSeq), "0"}, null, null, null);
            try {
                if (!cursor.moveToNext()) {
                    return null;
                }
                return new EncryptedFullReceiptFile(
                        cursor.getBlob(cursor.getColumnIndexOrThrow("file")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("receipt_version")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("receipt_type")));
            } finally {
                cursor.close();
            }
        } catch (Throwable t) {
            throw t;
        }
    }
}

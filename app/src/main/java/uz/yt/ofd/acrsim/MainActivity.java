package uz.yt.ofd.acrsim;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import uz.yt.ofd.acrsim.db.Storage;
import uz.yt.ofd.acrsim.db.dto.EncryptedFullReceiptFile;
import uz.yt.ofd.acrsim.db.dto.RegisterReceiptLog;
import uz.yt.ofd.acrsim.dev.DumpXReportCommand;
import uz.yt.ofd.acrsim.develop.FiscalDriveEmulator;
import uz.yt.ofd.acrsim.sender.dto.ReceiptSyncItem;
import uz.yt.ofd.acrsim.sender.dto.SyncItem;
import uz.yt.ofd.acrsim.sender.dto.ZReportSyncItem;
import uz.yt.ofd.android.lib.apduio.APDUCommand;
import uz.yt.ofd.android.lib.apduio.APDUIO;
import uz.yt.ofd.android.lib.apduio.APDUResponse;
import uz.yt.ofd.android.lib.applet.SW;
import uz.yt.ofd.android.lib.applet.command.AckCommand;
import uz.yt.ofd.android.lib.applet.command.Applet;
import uz.yt.ofd.android.lib.applet.command.GetFiscalMemoryInfoCommand;
import uz.yt.ofd.android.lib.applet.command.GetInfoCommand;
import uz.yt.ofd.android.lib.applet.command.GetReceiptFileCommand;
import uz.yt.ofd.android.lib.applet.command.GetReceiptInfoCommand;
import uz.yt.ofd.android.lib.applet.command.GetUnackowledgedZReportsIndexesCommand;
import uz.yt.ofd.android.lib.applet.command.GetVersionCommand;
import uz.yt.ofd.android.lib.applet.command.GetZReportFileCommand;
import uz.yt.ofd.android.lib.applet.command.GetZReportInfoCommand;
import uz.yt.ofd.android.lib.applet.command.OpenCloseZReportCommand;
import uz.yt.ofd.android.lib.applet.command.POSAuthCommand;
import uz.yt.ofd.android.lib.applet.command.POSChallengeCommand;
import uz.yt.ofd.android.lib.applet.command.POSLockCommand;
import uz.yt.ofd.android.lib.applet.command.RegisterReceiptCommand;
import uz.yt.ofd.android.lib.applet.command.SignedChallengeAuthCommand;
import uz.yt.ofd.android.lib.applet.command.SyncCommand;
import uz.yt.ofd.android.lib.applet.decoder.ByteArrayDecoder;
import uz.yt.ofd.android.lib.applet.decoder.FiscalMemoryInfoDecoder;
import uz.yt.ofd.android.lib.applet.decoder.FiscalSignInfoDecoder;
import uz.yt.ofd.android.lib.applet.decoder.InfoDecoder;
import uz.yt.ofd.android.lib.applet.decoder.ReceiptFileDecoder;
import uz.yt.ofd.android.lib.applet.decoder.ReceiptInfoDecoder;
import uz.yt.ofd.android.lib.applet.decoder.UnackowledgedZReportsIndexesDecoder;
import uz.yt.ofd.android.lib.applet.decoder.VersionDecoder;
import uz.yt.ofd.android.lib.applet.decoder.VoidDecoder;
import uz.yt.ofd.android.lib.applet.decoder.ZReportFileDecoder;
import uz.yt.ofd.android.lib.applet.decoder.ZReportInfoDecoder;
import uz.yt.ofd.android.lib.applet.dto.FiscalMemoryInfo;
import uz.yt.ofd.android.lib.applet.dto.FiscalSignInfo;
import uz.yt.ofd.android.lib.applet.dto.Info;
import uz.yt.ofd.android.lib.applet.dto.ReceiptFile;
import uz.yt.ofd.android.lib.applet.dto.ReceiptInfo;
import uz.yt.ofd.android.lib.applet.dto.UnackowledgedZReportsIndexes;
import uz.yt.ofd.android.lib.applet.dto.ZReportFile;
import uz.yt.ofd.android.lib.applet.dto.ZReportInfo;
import uz.yt.ofd.android.lib.codec.BCD8;
import uz.yt.ofd.android.lib.codec.HexBin;
import uz.yt.ofd.android.lib.codec.TerminalID;
import uz.yt.ofd.android.lib.codec.Utils;
import uz.yt.ofd.android.lib.codec.message6.AckFile;
import uz.yt.ofd.android.lib.codec.message6.StatusInfo;
import uz.yt.ofd.android.lib.codec.receipt20.OperationType;
import uz.yt.ofd.android.lib.codec.receipt20.Receipt;
import uz.yt.ofd.android.lib.codec.receipt20.ReceiptCodec;
import uz.yt.ofd.android.lib.codec.receipt20.ReceiptType;
import uz.yt.ofd.android.lib.crypto.GOST28147Engine;
import uz.yt.ofd.android.lib.exception.SWException;
import uz.yt.ofd.android.lib.validator.FiscalSignValidator;

public class MainActivity extends AppCompatActivity {


    int slotNumber = 0;
    SAMSlot samSlot;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    final AtomicBoolean busy = new AtomicBoolean(false);

    private byte[] getCPLC(APDUIO apduio) throws Exception {
        byte[] CPLC = apduio.transmit(new APDUCommand("get factory id", (byte) 0x00, (byte) 0xCA, (byte) 0x9F, (byte) 0x7F)).getData();
        if (CPLC[0] == (byte) 0x9F && CPLC[1] == (byte) 0x7F) {
            return Utils.slice(CPLC, 3, CPLC.length);
        }
        return CPLC;
    }

    private void runCardCommand(Callback callback) {
        if (!busy.getAndSet(true)) {
            try {

                byte[] atr = samSlot.connect();
                Log.d("sam slot", "Card ATR: " + HexBin.encode(atr));
                try {
                    APDUIO apduio = new LoggingAPDUIO(samSlot);
                    // GET FACTORY_ID
                    byte[] CPLC = getCPLC(apduio);


                    APDUResponse response = apduio.transmit(Applet.selectCommand());
                    if (response.getSw() != SW.NO_ERROR.code) {
                        throw new SWException(response.getSw());
                    }
                    try {
                        VersionDecoder decoder = new GetVersionCommand().run(apduio, VersionDecoder.class);
                        String appletVersion = decoder.decode();
                        if (Integer.parseInt(appletVersion) < 400 || Integer.parseInt(appletVersion) > 499) {
                            throw new IllegalStateException("Unsupported version");
                        }
                        callback.run(apduio, CPLC);
                    } finally {
                        apduio.transmit(Applet.deselectCommand());
                    }
                } finally {
                    samSlot.close();
                }
            } catch (Throwable t) {
                Log.e("sam slot", "execute", t);
                error(t);
            } finally {
                busy.set(false);
            }
        } else {
            alert("Warning", "SAM card is busy");
        }
    }

    private void error(Throwable t) {
        alert("Error", t.getMessage() == null ? t.getClass().getSimpleName().replace("Exception", "") : t.getMessage());
    }

    private void alert(String title, String message) {
        Log.d(title, message);
        Toast.makeText(MainActivity.this, title + "\n" + message, Toast.LENGTH_SHORT).show();
    }

    private void setText(TextView tv, String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText(text);
            }
        });
    }

    private FiscalDriveEmulator create(String address) {
        String[] a = address.split(":");
        String host = a[0];
        int port = Integer.parseInt(a[1]);
        return new FiscalDriveEmulator(new InetSocketAddress(host, port));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        RadioGroup radioGroupSAMType = findViewById(R.id.radioGroupSAMType);
        RadioButton radioButtonSAMSlot = findViewById(R.id.radioButtonSAMSlot);
        RadioButton radioButtonFDEmulator = findViewById(R.id.radioButtonFDEmulator);
        Button buttonSetEmulatorAddresses = findViewById(R.id.buttonSetEmulatorAddresses);
        final EditText editTextEmulatorTCPAddress = findViewById(R.id.editTextEmulatorTCPAddress);

        editTextEmulatorTCPAddress.setText(App.config.getProperty("fiscal.drive.emulator.address"));

        final Switch switchUsePOSLock = findViewById(R.id.switchUsePOSLock);
        final EditText editTextPOSSecretHex = findViewById(R.id.editTextPOSSecretHex);

        buttonSetEmulatorAddresses.setOnClickListener(view -> {
            try {
                samSlot = create(editTextEmulatorTCPAddress.getText().toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        radioButtonFDEmulator.setChecked(true);
                    }
                });
            } catch (Throwable t) {
                Log.e("sam slot", "execute", t);
                error(t);
            }
        });

        radioGroupSAMType.setOnCheckedChangeListener((radioGroup, i) -> {
            runOnUiThread(new Runnable() {
                public void run() {
                    editTextEmulatorTCPAddress.setEnabled(radioButtonFDEmulator.isChecked());
                    buttonSetEmulatorAddresses.setEnabled(radioButtonFDEmulator.isChecked());
                }
            });
            samSlot = null;
            try {
                if (radioButtonSAMSlot.isChecked()) {
                    // TODO: IMPLEMENT CLASS uz.yt.ofd.acrsim.driver.SAMSlotProvider ACCORDING TO YOUR DEVICE'S SPECIFICATION
                    samSlot = uz.yt.ofd.acrsim.driver.SAMSlotProvider.getSamSlot(slotNumber);
                }
                if (radioButtonFDEmulator.isChecked()) {
                    samSlot = create(editTextEmulatorTCPAddress.getText().toString());
                }
            } catch (Throwable t) {
                Log.e("sam slot", "execute", t);
                error(t);
            }
        });

        radioButtonFDEmulator.setChecked(true);

        EditText editTextServerAddresses = findViewById(R.id.editTextServerAddresses);
        setText(editTextServerAddresses, String.join(",", App.getServerAddresses()));
        Button buttonSetServerAddresses = findViewById(R.id.buttonSetServerAddresses);
        buttonSetServerAddresses.setOnClickListener(view -> {
            App.setServerAddresses(Arrays.asList(editTextServerAddresses.getText().toString().split(",")));
        });

        Button buttonGetVersion = findViewById(R.id.buttonGetVersion);
        TextView textViewGetVersion = findViewById(R.id.textViewGetVersion);

        buttonGetVersion.setOnClickListener(view -> {
            setText(textViewGetVersion, "?");
            runCardCommand(new Callback() {
                @Override
                public void run(APDUIO apduio, byte[] CPLC) throws Exception {
                    VersionDecoder decoder = new GetVersionCommand().run(apduio, VersionDecoder.class);
                    String appletVersion = decoder.decode();
                    setText(textViewGetVersion, appletVersion);

                }
            });
        });

        TextView textViewGetInfo = findViewById(R.id.textViewGetInfo);


        EditText editTextInfoTags = findViewById(R.id.editTextInfoTags);
        Button buttonGetInfo = findViewById(R.id.buttonGetInfo);

        buttonGetInfo.setOnClickListener(view -> {
            setText(textViewGetInfo, "?");
            runCardCommand(new Callback() {
                @Override
                public void run(APDUIO apduio, byte[] CPLC) throws Exception {
                    byte[] tags = HexBin.decode(editTextInfoTags.getText().toString());
                    InfoDecoder decoder = new GetInfoCommand(tags).run(apduio, InfoDecoder.class);
                    Info info = decoder.decode();
                    setText(textViewGetInfo, App.getGson().toJson(info));

                }
            });
        });


        Button buttonStateSyncWithServer = findViewById(R.id.buttonStateSyncWithServer);
        buttonStateSyncWithServer.setOnClickListener(view -> {
            setText(textViewGetInfo, "?");
            runCardCommand(new Callback() {
                @Override
                public void run(APDUIO apduio, byte[] CPLC) throws Exception {
                    byte[] tags = new byte[]{Info.TAG_TERMINAL_ID, Info.TAG_SYNC_CHALLENGE};
                    InfoDecoder decoder = new GetInfoCommand(tags).run(apduio, InfoDecoder.class);
                    Info info = decoder.decode();
                    setText(textViewGetInfo, App.getGson().toJson(info));
                    byte[] sync = null;
                    boolean syncStateOnline = true;


                    if (syncStateOnline) {
                        sync = App.getSender().SyncState(info.getTerminalID(), info.getSyncChallenge());
                    }
                    new SyncCommand(sync).run(apduio, VoidDecoder.class);
                }
            });
        });


        EditText editTextFiscalMemoryInfoTags = findViewById(R.id.editTextFiscalMemoryInfoTags);
        Button buttonGetFiscalMemoryInfo = findViewById(R.id.buttonGetFiscalMemoryInfo);
        TextView textViewGetFiscalMemoryInfo = findViewById(R.id.textViewGetFiscalMemoryInfo);

        buttonGetFiscalMemoryInfo.setOnClickListener(view -> {
            setText(textViewGetFiscalMemoryInfo, "?");
            runCardCommand(new Callback() {
                @Override
                public void run(APDUIO apduio, byte[] CPLC) throws Exception {
                    byte[] tags = HexBin.decode(editTextFiscalMemoryInfoTags.getText().toString());
                    FiscalMemoryInfoDecoder decoder = new GetFiscalMemoryInfoCommand(tags).run(apduio, FiscalMemoryInfoDecoder.class);
                    FiscalMemoryInfo info = decoder.decode();
                    setText(textViewGetFiscalMemoryInfo, App.getGson().toJson(info));

                }
            });
        });

        Button buttonGetUnackowledgedZReportsIndexes = findViewById(R.id.buttonGetUnackowledgedZReportsIndexes);
        TextView textViewGetUnackowledgedZReportsIndexes = findViewById(R.id.textViewGetUnackowledgedZReportsIndexes);

        buttonGetUnackowledgedZReportsIndexes.setOnClickListener(view -> {
            setText(textViewGetUnackowledgedZReportsIndexes, "?");
            runCardCommand(new Callback() {
                @Override
                public void run(APDUIO apduio, byte[] CPLC) throws Exception {
                    UnackowledgedZReportsIndexesDecoder decoder = new GetUnackowledgedZReportsIndexesCommand().run(apduio, UnackowledgedZReportsIndexesDecoder.class);
                    UnackowledgedZReportsIndexes info = decoder.decode();
                    setText(textViewGetUnackowledgedZReportsIndexes, App.getGson().toJson(info));

                }
            });
        });

        Button buttonSetCurrentTime = findViewById(R.id.buttonSetCurrentTime);
        Button buttonSetCurrentTime2 = findViewById(R.id.buttonSetCurrentTime2);
        EditText editTextDateCurrentTime = findViewById(R.id.editTextDateCurrentTime);
        EditText editTextDateCurrentTime2 = findViewById(R.id.editTextDateCurrentTime2);

        buttonSetCurrentTime.setOnClickListener(view -> {
            setText(editTextDateCurrentTime, dateFormat.format(new Date()));
            setText(editTextDateCurrentTime2, editTextDateCurrentTime.getText().toString());
        });

        buttonSetCurrentTime2.setOnClickListener(view -> {
            setText(editTextDateCurrentTime, dateFormat.format(new Date()));
            setText(editTextDateCurrentTime2, editTextDateCurrentTime.getText().toString());
        });

        setText(editTextDateCurrentTime, dateFormat.format(new Date()));
        setText(editTextDateCurrentTime2, editTextDateCurrentTime.getText().toString());

        TextView textViewOpenCloseZReport = findViewById(R.id.textViewOpenCloseZReport);

        Button buttonOpenZReport = findViewById(R.id.buttonOpenZReport);

        buttonOpenZReport.setOnClickListener(view -> {
            setText(textViewOpenCloseZReport, "?");
            runCardCommand(new Callback() {
                @Override
                public void run(APDUIO apduio, byte[] CPLC) throws Exception {
                    if (switchUsePOSLock.isChecked()) {
                        posAuth(apduio, editTextPOSSecretHex.getText().toString());
                    }
                    Date time = dateFormat.parse(editTextDateCurrentTime.getText().toString());
                    new OpenCloseZReportCommand(true, time).run(apduio, VoidDecoder.class);
                    setText(textViewOpenCloseZReport, "OK");
                }
            });
        });

        Button buttonCloseZReport = findViewById(R.id.buttonCloseZReport);

        buttonCloseZReport.setOnClickListener(view -> {
            setText(textViewOpenCloseZReport, "?");
            runCardCommand(new Callback() {
                @Override
                public void run(APDUIO apduio, byte[] CPLC) throws Exception {
                    if (switchUsePOSLock.isChecked()) {
                        posAuth(apduio, editTextPOSSecretHex.getText().toString());
                    }
                    Date time = dateFormat.parse(editTextDateCurrentTime.getText().toString());
                    new OpenCloseZReportCommand(false, time).run(apduio, VoidDecoder.class);
                    setText(textViewOpenCloseZReport, "OK");
                }
            });
        });


        EditText editTextNumberSignedZReportIndex = findViewById(R.id.editTextNumberSignedZReportIndex);
        EditText editTextZReportInfoTags = findViewById(R.id.editTextZReportInfoTags);
        Button buttonGetZReportInfo = findViewById(R.id.buttonGetZReportInfo);
        TextView textViewGetZReportInfo = findViewById(R.id.textViewGetZReportInfo);

        buttonGetZReportInfo.setOnClickListener(view -> {
            setText(textViewGetZReportInfo, "?");
            runCardCommand(new Callback() {
                @Override
                public void run(APDUIO apduio, byte[] CPLC) throws Exception {
                    short index = Short.parseShort(editTextNumberSignedZReportIndex.getText().toString());
                    byte[] tags = HexBin.decode(editTextZReportInfoTags.getText().toString());
                    ZReportInfoDecoder decoder = new GetZReportInfoCommand(index, tags).run(apduio, ZReportInfoDecoder.class);
                    ZReportInfo info = decoder.decode();
                    setText(textViewGetZReportInfo, App.getGson().toJson(info));

                }
            });
        });

        Button buttonGetZReportFile = findViewById(R.id.buttonGetZReportFile);

        buttonGetZReportFile.setOnClickListener(view -> {
            setText(textViewGetZReportInfo, "?");
            runCardCommand(new Callback() {
                @Override
                public void run(APDUIO apduio, byte[] CPLC) throws Exception {
                    short index = Short.parseShort(editTextNumberSignedZReportIndex.getText().toString());
                    ZReportFileDecoder decoder = new GetZReportFileCommand(index).run(apduio, ZReportFileDecoder.class);
                    ZReportFile info = decoder.decode();
                    setText(textViewGetZReportInfo, App.getGson().toJson(info));

                }
            });
        });

        Button buttonSyncZReportFile = findViewById(R.id.buttonSyncZReportFile);

        buttonSyncZReportFile.setOnClickListener(view -> {
            runCardCommand(new Callback() {
                @Override
                public void run(APDUIO apduio, byte[] CPLC) throws Exception {
                    short index = Short.parseShort(editTextNumberSignedZReportIndex.getText().toString());
                    ZReportFileDecoder decoder = new GetZReportFileCommand(index).run(apduio, ZReportFileDecoder.class);
                    ZReportFile info = decoder.decode();


                    List<SyncItem> syncItems = new LinkedList();
                    syncItems.add(new ZReportSyncItem(info));
                    doSync(apduio, info.getTerminalID(), syncItems);
                }
            });
        });


        Button buttonGenerateTestReceipt = findViewById(R.id.buttonGenerateTestReceipt);
        RadioButton radioButtonPurchase = findViewById(R.id.radioButtonPurchase);
        RadioButton radioButtonAdvance = findViewById(R.id.radioButtonAdvance);
        RadioButton radioButtonCredit = findViewById(R.id.radioButtonCredit);
        RadioButton radioButtonSale = findViewById(R.id.radioButtonSale);
        RadioButton radioButtonRefund = findViewById(R.id.radioButtonRefund);
        EditText editTextNumberTargetSum = findViewById(R.id.editTextNumberTargetSum);
        TextView textViewGenerateTestReceipt = findViewById(R.id.textViewGenerateTestReceipt);

        buttonGenerateTestReceipt.setOnClickListener(view -> {
            Date time;
            try {
                ReceiptType type;
                if (radioButtonPurchase.isChecked()) {
                    type = ReceiptType.Purchase;
                } else if (radioButtonAdvance.isChecked()) {
                    type = ReceiptType.Advance;
                } else if (radioButtonCredit.isChecked()) {
                    type = ReceiptType.Credit;
                } else {
                    throw new IllegalArgumentException("bad receipt type chosen");
                }
                OperationType operation;
                if (radioButtonSale.isChecked()) {
                    operation = OperationType.Sale;
                } else if (radioButtonRefund.isChecked()) {
                    operation = OperationType.Refund;
                } else {
                    throw new IllegalArgumentException("bad operation type chosen");
                }
                time = dateFormat.parse(editTextDateCurrentTime.getText().toString());
                long targetSum = Long.parseLong(editTextNumberTargetSum.getText().toString());
                Receipt receipt = ReceiptUtils.generateReceipt(targetSum, time, type, operation);
                setText(textViewGenerateTestReceipt, App.getGson().toJson(receipt));
            } catch (Throwable t) {
                error(t);
            }
        });

        Button buttonGetRegisterTXID = findViewById(R.id.buttonGetRegisterTXID);
        EditText editTextNumberTXID = findViewById(R.id.editTextNumberTXID);

        buttonGetRegisterTXID.setOnClickListener(view -> {
            setText(editTextNumberTXID, "?");
            try {
                String receiptJson = textViewGenerateTestReceipt.getText().toString();
                final Receipt receipt = App.getGson().fromJson(receiptJson, Receipt.class);

                runCardCommand(new Callback() {
                    @Override
                    public void run(APDUIO apduio, byte[] CPLC) throws Exception {
                        String factoryID = HexBin.encode(CPLC);

                        FiscalMemoryInfoDecoder decoder = new GetFiscalMemoryInfoCommand(new byte[]{FiscalMemoryInfo.TAG_LAST_OPERATION_TIME}).run(apduio, FiscalMemoryInfoDecoder.class);
                        FiscalMemoryInfo info = decoder.decode();

                        if (info.getLastOperationTime().after(receipt.getTime()) || info.getLastOperationTime().equals(receipt.getTime())) {
                            throw new IllegalArgumentException("receipt time is in the past");
                        }

                        Storage storage = App.getStorage();

                        ByteArrayOutputStream tlvEncodedReceipt = new ByteArrayOutputStream();
                        ByteArrayOutputStream totalBlock = new ByteArrayOutputStream();
                        ReceiptCodec.encode(receipt, tlvEncodedReceipt, totalBlock, new FiscalSignValidator() {
                            @Override
                            public boolean check(String terminalID, byte[] receiptSeqRaw, byte[] dateTimeRaw, byte[] fiscalSignRaw) throws IOException {
                                if (receipt.getType().equals(ReceiptType.Purchase)) {
                                    // TODO: check online before refund
                                    // return App.getSender().check(terminalID, receiptSeqRaw, dateTimeRaw, fiscalSignRaw);
                                }
                                return true;
                            }
                        });
                        GOST28147Engine cipher = new GOST28147Engine();
                        byte[] tlvEncodedReceiptRaw = tlvEncodedReceipt.toByteArray();
                        if ((tlvEncodedReceiptRaw.length % cipher.getBlockSize()) != 0) {
                            throw new IllegalArgumentException(String.format("Bad encoded receipt data size %d, it should be divisible by %d", tlvEncodedReceiptRaw.length, cipher.getBlockSize()));
                        }

                        Long id = storage.newReceiptRegisterLog(factoryID, ReceiptCodec.VERSION, receipt.getType().getValue(), receipt.getOperation().getValue(), tlvEncodedReceiptRaw, totalBlock.toByteArray());

                        setText(editTextNumberTXID, String.valueOf(id));
                    }
                });
            } catch (Throwable t) {
                error(t);
            }
        });

        Button buttonRegisterReceipt = findViewById(R.id.buttonRegisterReceipt);
        TextView textViewRegisterReceipt = findViewById(R.id.textViewRegisterReceipt);


        buttonRegisterReceipt.setOnClickListener(view -> {
            setText(textViewRegisterReceipt, "?");
            runCardCommand(new Callback() {
                @Override
                public void run(APDUIO apduio, byte[] CPLC) throws Exception {

                    String factoryID = HexBin.encode(CPLC);

                    Storage storage = App.getStorage();

                    Long id = Long.parseLong(editTextNumberTXID.getText().toString());

                    RegisterReceiptLog rrl = storage.getReceiptRegisterLog(factoryID, id);

                    GOST28147Engine cipher = new GOST28147Engine();
                    byte[] tlvEncodedReceiptRaw = rrl.getTlvEncodedReceiptRaw();
                    if ((tlvEncodedReceiptRaw.length % cipher.getBlockSize()) != 0) {
                        throw new IllegalArgumentException(String.format("Bad encoded receipt data size %d, it should be divisible by %d", tlvEncodedReceiptRaw.length, cipher.getBlockSize()));
                    }

                    byte[] totoalBlock = rrl.getTotalBlockRaw();


                    if (switchUsePOSLock.isChecked()) {
                        posAuth(apduio, editTextPOSSecretHex.getText().toString());
                    }

                    FiscalSignInfoDecoder decoder;
                    FiscalSignInfo info;
                    try {
                        decoder = new RegisterReceiptCommand(totoalBlock).run(apduio, FiscalSignInfoDecoder.class);
                        info = decoder.decode();
                    } catch (Throwable t) {
                        storage.updateReceiptRegisterLog(factoryID, id, t.getMessage());
                        throw t;
                    }

                    storage.updateReceiptRegisterLog(factoryID, id, decoder.getData(), info.getTerminalID(), info.getReceiptSeq());


                    cipher.init(true, cipher.getSBox("D-A"), info.getCipherKey());
                    int blocks = tlvEncodedReceiptRaw.length / cipher.getBlockSize();
                    byte[] file = new byte[tlvEncodedReceiptRaw.length];
                    for (int bn = 0; bn < blocks; bn++) {
                        cipher.processBlock(tlvEncodedReceiptRaw, bn * cipher.getBlockSize(), file, bn * cipher.getBlockSize());
                    }


                    storage.newFullReceipt(factoryID, info.getTerminalID(), info.getReceiptSeq(), info.getTime(), rrl.getReceiptVersion(), rrl.getReceiptType(), rrl.getOperation(), file, id);

                    setText(textViewRegisterReceipt, App.getGson().toJson(info));

                }
            });
        });

        EditText editTextNumberSignedReceiptIndex = findViewById(R.id.editTextNumberSignedReceiptIndex);
        EditText editTextReceiptInfoTags = findViewById(R.id.editTextReceiptInfoTags);
        Button buttonGetReceiptInfo = findViewById(R.id.buttonGetReceiptInfo);
        TextView textViewGetReceiptInfo = findViewById(R.id.textViewGetReceiptInfo);

        buttonGetReceiptInfo.setOnClickListener(view -> {
            setText(textViewGetReceiptInfo, "?");
            runCardCommand(new Callback() {
                @Override
                public void run(APDUIO apduio, byte[] CPLC) throws Exception {
                    short index = Short.parseShort(editTextNumberSignedReceiptIndex.getText().toString());
                    byte[] tags = HexBin.decode(editTextReceiptInfoTags.getText().toString());
                    ReceiptInfoDecoder decoder = new GetReceiptInfoCommand(index, tags).run(apduio, ReceiptInfoDecoder.class);
                    ReceiptInfo info = decoder.decode();
                    setText(textViewGetReceiptInfo, App.getGson().toJson(info));

                }
            });
        });

        Button buttonGetReceiptFile = findViewById(R.id.buttonGetReceiptFile);

        buttonGetReceiptFile.setOnClickListener(view -> {
            setText(textViewGetReceiptInfo, "?");
            runCardCommand(new Callback() {
                @Override
                public void run(APDUIO apduio, byte[] CPLC) throws Exception {
                    short index = Short.parseShort(editTextNumberSignedReceiptIndex.getText().toString());
                    ReceiptFileDecoder decoder = new GetReceiptFileCommand(true, index, null).run(apduio, ReceiptFileDecoder.class);
                    ReceiptFile info = decoder.decode();
                    setText(textViewGetReceiptInfo, App.getGson().toJson(info));

                }
            });
        });



        EditText editTextNumberSyncItemsCount = findViewById(R.id.editTextNumberSyncItemsCount);
        Button buttonSyncFiles = findViewById(R.id.buttonSyncFiles);
        RadioButton radioButtonSyncAllItems = findViewById(R.id.radioButtonSyncAllItems);
        RadioButton radioButtonSyncOnlyZReports = findViewById(R.id.radioButtonSyncOnlyZReports);
        RadioButton radioButtonSyncOnlyShortReceipts = findViewById(R.id.radioButtonSyncOnlyShortReceipts);
        TextView textViewSyncFilesReport = findViewById(R.id.textViewSyncFilesReport);

        buttonSyncFiles.setOnClickListener(view -> {

            setText(textViewSyncFilesReport, "?");
            runCardCommand(new Callback() {
                @Override
                public void run(APDUIO apduio, byte[] CPLC) throws Exception {

                    short syncItemsCount = Short.parseShort(editTextNumberSyncItemsCount.getText().toString());

                    String terminalId;
                    int unsentReceiptsCount = 0;
                    short unsentZreportsCount = 0;
                    short[] unsentZreportIndexes = null;


                    String factoryID = HexBin.encode(CPLC);

                    Storage storage = App.getStorage();

                    List<SyncItem> syncItems = new LinkedList();

                    {
                        byte[] tags = new byte[]{FiscalMemoryInfo.TAG_TERMINAL_ID, FiscalMemoryInfo.TAG_RECEIPTS_COUNT};
                        FiscalMemoryInfoDecoder decoder = new GetFiscalMemoryInfoCommand(tags).run(apduio, FiscalMemoryInfoDecoder.class);
                        FiscalMemoryInfo info = decoder.decode();


                        terminalId = info.getTerminalID();

                        if (radioButtonSyncAllItems.isChecked() || radioButtonSyncOnlyShortReceipts.isChecked()) {
                            unsentReceiptsCount = info.getReceiptsCount();
                        }
                    }
                    if (radioButtonSyncAllItems.isChecked() || radioButtonSyncOnlyZReports.isChecked()) {

                        UnackowledgedZReportsIndexesDecoder decoder = new GetUnackowledgedZReportsIndexesCommand().run(apduio, UnackowledgedZReportsIndexesDecoder.class);
                        UnackowledgedZReportsIndexes info = decoder.decode();


                        unsentZreportsCount = info.getCount();
                        unsentZreportIndexes = info.getIndexes();
                    }


                    if (unsentReceiptsCount > 0) {
                        short index = 0;
                        while (syncItemsCount > 0) {
                            try {
                                // fetch short-receipts from sam-card
                                ReceiptFileDecoder decoder = new GetReceiptFileCommand(true, index, null).run(apduio, ReceiptFileDecoder.class);
                                ReceiptFile info = decoder.decode();


                                EncryptedFullReceiptFile file = null;
                                if (!radioButtonSyncOnlyShortReceipts.isChecked()) {
                                    file = storage.getFullReceipt(factoryID, info.getTerminalID(), info.getReceiptSeq());
                                }
                                syncItems.add(new ReceiptSyncItem(info, file));

                            } catch (SWException swe) {
                                if (swe.getSw().equals(SW.NOT_FOUND)) {
                                    break;
                                } else {
                                    Log.e("sam slot", "execute", swe);
                                    error(swe);
                                    break;
                                }
                            } catch (Throwable t) {
                                Log.e("sam slot", "execute", t);
                                error(t);
                                break;
                            } finally {
                                index++;
                                syncItemsCount--;
                            }
                        }
                    }

                    if (unsentZreportsCount > 0) {
                        short i = 0;
                        while (syncItemsCount > 0 && unsentZreportsCount > 0) {
                            short index = unsentZreportIndexes[i];
                            try {
                                // fetch zreports from sam-card
                                ZReportFileDecoder decoder = new GetZReportFileCommand(index).run(apduio, ZReportFileDecoder.class);
                                ZReportFile info = decoder.decode();


                                syncItems.add(new ZReportSyncItem(info));
                            } catch (SWException swe) {
                                if (swe.getSw().equals(SW.ZREPORT_IS_NOT_CLOSED) || swe.getSw().equals(SW.ZREPORT_IS_NOT_OPENED)) {
                                    // pass
                                } else if (swe.getSw().equals(SW.NOT_FOUND)) {
                                    break;
                                } else {
                                    Log.e("sam slot", "execute", swe);
                                    error(swe);
                                    break;
                                }
                            } catch (Throwable t) {
                                Log.e("sam slot", "execute", t);
                                error(t);
                                break;
                            } finally {
                                i++;
                                unsentZreportsCount--;
                                syncItemsCount--;

                            }
                        }
                    }

                    doSync(apduio, terminalId, syncItems);
                }
            });
        });


        EditText editTextAuthChallengeHex = findViewById(R.id.editTextAuthChallengeHex);
        Button buttonSignedChallengeAuth = findViewById(R.id.buttonSignedChallengeAuth);
        TextView textViewSignedChallengeAuth = findViewById(R.id.textViewSignedChallengeAuth);

        buttonSignedChallengeAuth.setOnClickListener(view -> {
            setText(textViewSignedChallengeAuth, "?");
            runCardCommand(new Callback() {
                @Override
                public void run(APDUIO apduio, byte[] CPLC) throws Exception {
                    byte[] challenge = HexBin.decode(editTextAuthChallengeHex.getText().toString());
                    if (challenge.length != 16) {
                        throw new IllegalArgumentException("AuthChallengeHex length is not 16 bytes");
                    }
                    ByteArrayDecoder decoder = new SignedChallengeAuthCommand(challenge).run(apduio, ByteArrayDecoder.class);
                    byte[] singedChallenge = decoder.getData();
                    setText(textViewSignedChallengeAuth, HexBin.encode(singedChallenge));

                }
            });
        });

        Button buttonPOSLock = findViewById(R.id.buttonPOSLock);
        TextView textViewPOSLock = findViewById(R.id.textViewPOSLock);

        buttonPOSLock.setOnClickListener(view -> {
            setText(textViewPOSLock, "?");
            runCardCommand(new Callback() {
                @Override
                public void run(APDUIO apduio, byte[] CPLC) throws Exception {
                    byte[] secret = HexBin.decode(editTextPOSSecretHex.getText().toString());
                    if (secret.length != 32) {
                        throw new IllegalArgumentException("POS secret length is not 32 bytes");
                    }
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    byte[] secretHash = digest.digest(secret);
                    new POSLockCommand(Utils.append(secret, secretHash)).run(apduio, VoidDecoder.class);

                    setText(textViewPOSLock, "OK");

                }
            });
        });

        Button buttonPOSAuth = findViewById(R.id.buttonPOSAuth);

        buttonPOSAuth.setOnClickListener(view -> {
            setText(textViewPOSLock, "?");
            runCardCommand(new Callback() {
                @Override
                public void run(APDUIO apduio, byte[] CPLC) throws Exception {

                    posAuth(apduio, editTextPOSSecretHex.getText().toString());

                    byte[] tags = new byte[]{Info.TAG_POS_LOCKED, Info.TAG_POS_AUTH};
                    InfoDecoder decoder = new GetInfoCommand(tags).run(apduio, InfoDecoder.class);
                    Info info = decoder.decode();
                    setText(textViewPOSLock, App.getGson().toJson(info));
                }
            });
        });

    }

    void doSync(APDUIO apduio, String terminalId, List<SyncItem> syncItems) throws Exception {
        StatusInfo statusInfo = App.getSender().SyncItems(terminalId, syncItems);
        if (statusInfo == null) {
            alert("Sync", "No Sync items to send");
            return;
        }
        switch (statusInfo.getStatusCode()) {
            case OK:
                break;
            case OKNotice:
                alert("Sync", statusInfo.getNotice());
                break;
            case RetrySend:
                alert("Sync", "Retry sync after some time");
                return;
            case NotActive:
                alert("Sync", "Fiscal drive " + terminalId + " is not active, notice: " + statusInfo.getNotice() + ", reasonCode:" + statusInfo.getReasonCode());
                return;
            case NotFound:
                alert("Sync", "Fiscal drive " + terminalId + " is not found");
                return;
            case BadMessageSyntax:
                alert("Sync", "Bad message syntax");
                return;
            case BadMessageStruct:
                alert("Sync", "Bad message struct");
                return;
            case TooManyFiles:
                alert("Sync", "Too many files to sync, decrease sync items count");
                return;
            case TooBigMessage:
                alert("Sync", "Too big message to sync, decrease sync items count");
                return;
            case BadMessageCRC32:
                alert("Sync", "Bad message crc32");
                return;
            default:
                alert("Sync", "Unknown status code");
                return;
        }
        int successfulAckCount = 0;
        int unsuccessfulAckCount = 0;
        for (SyncItem syncItem : syncItems) {
            AckFile af = syncItem.getAckFile();
            switch (af.getStatus()) {
                case Acknowledge:
                    byte[] ack = af.getBody();
                    try {
                        new AckCommand(ack).run(apduio, VoidDecoder.class);
                        successfulAckCount++;
                    } catch (SWException swe) {
                        if (swe.getSw().equals(SW.NOT_FOUND)) {
                            // Already had been acknowledged or not found
                        } else {
                            throw swe;
                        }
                    }
                    break;
                case Reject:
                    alert("Sync", "Sync item rejected, ask technical support");
                    unsuccessfulAckCount++;
                    break;
                case Error:
                    alert("Sync", "Sync item error, ask technical support, error reason is in file: " + new String(af.getHeader()));
                    unsuccessfulAckCount++;
                    break;
                case UnrecognizedType:
                    alert("Sync", "Sync item type is not supported, see docs");
                    unsuccessfulAckCount++;
                    break;
                default:
                    alert("Sync", "Uncknown error");
                    unsuccessfulAckCount++;
                    break;
            }
        }
        alert("Sync", "Acknowledged Items Total: " + syncItems.size() + " Successful: " + successfulAckCount + " Failed: " + unsuccessfulAckCount);
    }

    void posAuth(APDUIO apduio, String posSecretHex) throws Exception {
        byte[] secret = HexBin.decode(posSecretHex);
        ByteArrayDecoder decoder = new POSChallengeCommand().run(apduio, ByteArrayDecoder.class);
        byte[] posChallenge = decoder.getData();
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(secret);
        byte[] auth = digest.digest(posChallenge);
        new POSAuthCommand(auth).run(apduio, VoidDecoder.class);
    }

}

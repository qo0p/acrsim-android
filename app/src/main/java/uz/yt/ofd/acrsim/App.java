package uz.yt.ofd.acrsim;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import uz.yt.ofd.acrsim.db.SQLiteStorage;
import uz.yt.ofd.acrsim.db.Storage;
import uz.yt.ofd.acrsim.sender.Sender;
import uz.yt.ofd.acrsim.sender.TCPSender;
import uz.yt.ofd.android.lib.codec.HexBin;
import uz.yt.ofd.android.lib.codec.message6.SenderInfo;
import uz.yt.ofd.android.lib.codec.receipt20.OperationType;
import uz.yt.ofd.android.lib.codec.receipt20.ReceiptType;

public class App extends Application {

    private static Context appContext;
    private static SQLiteStorage storage;
    private static List<String> serverAddresses;
    private static Sender sender;
    public static Properties config;
    private static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .registerTypeAdapter(byte[].class, new JsonSerializer<byte[]>() {
                @Override
                public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
                    return new JsonPrimitive(HexBin.encode(src));
                }
            })
            .registerTypeAdapter(ReceiptType.class, new JsonSerializer<ReceiptType>() {
                @Override
                public JsonElement serialize(ReceiptType src, Type typeOfSrc, JsonSerializationContext context) {
                    return src != null ? new JsonPrimitive(src.getValue()) : null;
                }
            })
            .registerTypeAdapter(ReceiptType.class, new JsonDeserializer<ReceiptType>() {
                @Override
                public ReceiptType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    for (ReceiptType rt : ReceiptType.values()) {
                        if (rt.getValue() == json.getAsByte()) {
                            return rt;
                        }
                    }
                    return null;
                }
            })
            .registerTypeAdapter(OperationType.class, new JsonSerializer<OperationType>() {
                @Override
                public JsonElement serialize(OperationType src, Type typeOfSrc, JsonSerializationContext context) {
                    return src != null ? new JsonPrimitive(src.getValue()) : null;
                }
            })
            .registerTypeAdapter(OperationType.class, new JsonDeserializer<OperationType>() {
                @Override
                public OperationType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    for (OperationType rt : OperationType.values()) {
                        if (rt.getValue() == json.getAsByte()) {
                            return rt;
                        }
                    }
                    return null;
                }
            })
            .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();

    public static List<String> getServerAddresses() {
        return serverAddresses;
    }

    public static void setServerAddresses(List<String> list) {
        ((TCPSender) sender).setServerAddresses(list);
    }

    public static Gson getGson() {
        return gson;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        storage = new SQLiteStorage(appContext);

        config = new Properties();
        try {
            config.load(getResources().openRawResource(R.raw.config));
            int connectionTimeout = 5000;
            serverAddresses = Arrays.asList(config.getProperty("server.addresses", "").split(","));
            sender = new TCPSender(serverAddresses, connectionTimeout, new SenderInfo("ACR-SIM-Android", "", "v1.0"));
        } catch (Throwable e) {
            Log.e("config", e.getMessage(), e);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        storage.close();
    }

    public static Storage getStorage() {
        return storage;
    }

    public static Sender getSender() {
        return sender;
    }
}

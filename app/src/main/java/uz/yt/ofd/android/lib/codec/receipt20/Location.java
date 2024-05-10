/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uz.yt.ofd.android.lib.codec.receipt20;

import java.io.IOException;
import java.io.OutputStream;

import uz.yt.ofd.android.lib.codec.TlvTagDescriptions;
import uz.yt.ofd.android.lib.codec.tlv.TLVEncodable;

public class Location extends TLVEncodable {

    public static final byte TAG_LONGITUDE = (byte) 0x01;
    public static final byte TAG_LATITUDE = (byte) 0x02;

    public static void buildTlvTagDescriptions(TlvTagDescriptions parentTlvTagDescriptions, TlvTagDescriptions.OID oid) {
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_LONGITUDE, "Longitude"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_LATITUDE, "Latitude"));
    }

    private double latitude;

    private double longitude;

    @Override
    public void write(OutputStream w) throws IOException {
        longitude = longitude % 360d;
        writeString(TAG_LONGITUDE, String.format("%f", longitude), 3 + 1 + 10, w);

        latitude = latitude % 360d;
        writeString(TAG_LATITUDE, String.format("%f", latitude), 3 + 1 + 10, w);
    }

    public Location() {
    }

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

}

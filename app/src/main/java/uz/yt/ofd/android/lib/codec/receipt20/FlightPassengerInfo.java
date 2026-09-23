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

public class FlightPassengerInfo extends TLVEncodable {

    public static final byte TAG_PASSPORT_NUMBER = (byte) 0x01;
    public static final byte TAG_PINFL = (byte) 0x02;
    public static final byte TAG_FLIGHT_NUMBER = (byte) 0x03;
    public static final byte TAG_SEAT_NUMBER = (byte) 0x04;

    public static void buildTlvTagDescriptions(TlvTagDescriptions parentTlvTagDescriptions, TlvTagDescriptions.OID oid) {
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_PASSPORT_NUMBER, "PassportNumber"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_PINFL, "PINFL"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_FLIGHT_NUMBER, "FlightNumber"));
        parentTlvTagDescriptions.addTagDesciption(oid.append(TAG_SEAT_NUMBER, "SeatNumber"));
    }

    private String passportNumber;
    private String pinfl;
    private String flightNumber;
    private String seatNumber;

    public FlightPassengerInfo() {
    }

    public FlightPassengerInfo(String passportNumber, String pinfl, String flightNumber, String seatNumber) {
        this.passportNumber = passportNumber;
        this.pinfl = pinfl;
        this.flightNumber = flightNumber;
        this.seatNumber = seatNumber;
    }

    @Override
    public void write(OutputStream w) throws IOException {
        writeString(TAG_PASSPORT_NUMBER, passportNumber, ReceiptCodec.PASSPORT_NUMBER_MAX_SIZE, w);
        writeString(TAG_PINFL, pinfl, ReceiptCodec.PINFL_MAX_SIZE, w);
        writeString(TAG_FLIGHT_NUMBER, flightNumber, ReceiptCodec.FLIGHT_NUMBER_MAX_SIZE, w);
        writeString(TAG_SEAT_NUMBER, seatNumber, ReceiptCodec.SEAT_NUMBER_MAX_SIZE, w);
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getPinfl() {
        return pinfl;
    }

    public void setPinfl(String pinfl) {
        this.pinfl = pinfl;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }
}

package com.jwoglom.pumpx2.pump.messages.request.control;

import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.JoinFSL3Response;

import org.apache.commons.lang3.Validate;

import java.util.Arrays;

/**
 * Instructs the pump to join (pair with) a FreeStyle Libre 3 CGM sensor.
 *
 * The phone performs the NFC activation of the sensor and then hands the pump the
 * information it needs to establish its own BLE session with the sensor.
 *
 * Cargo layout (36 bytes):
 *   offset 0  (6 bytes): bleAddress             - sensor BLE MAC address
 *   offset 6  (4 bytes): sensorCode
 *   offset 10 (4 bytes): activationTime
 *   offset 14 (4 bytes): firmwareVersion
 *   offset 18 (1 byte):  warmupTime
 *   offset 19 (9 bytes): compressedSerialNumber
 *   offset 28 (8 bytes): nfcUid                 - sensor NFC UID
 *
 * Derived from the decompiled t:connect Android app (JoinFSL3Request, opcode 120).
 */
@MessageProps(
    opCode=120,
    size=36,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    response=JoinFSL3Response.class,
    signed=true
)
public class JoinFSL3Request extends Message {
    private byte[] bleAddress;
    private byte[] sensorCode;
    private byte[] activationTime;
    private byte[] firmwareVersion;
    private int warmupTime;
    private byte[] compressedSerialNumber;
    private byte[] nfcUid;

    public JoinFSL3Request() {}

    public JoinFSL3Request(
            byte[] bleAddress,
            byte[] sensorCode,
            byte[] activationTime,
            byte[] firmwareVersion,
            int warmupTime,
            byte[] compressedSerialNumber,
            byte[] nfcUid) {
        this.cargo = buildCargo(
                bleAddress,
                sensorCode,
                activationTime,
                firmwareVersion,
                warmupTime,
                compressedSerialNumber,
                nfcUid);
        parse(this.cargo);
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.bleAddress = Arrays.copyOfRange(raw, 0, 6);
        this.sensorCode = Arrays.copyOfRange(raw, 6, 10);
        this.activationTime = Arrays.copyOfRange(raw, 10, 14);
        this.firmwareVersion = Arrays.copyOfRange(raw, 14, 18);
        this.warmupTime = raw[18] & 0xFF;
        this.compressedSerialNumber = Arrays.copyOfRange(raw, 19, 28);
        this.nfcUid = Arrays.copyOfRange(raw, 28, 36);
    }

    public static byte[] buildCargo(
            byte[] bleAddress,
            byte[] sensorCode,
            byte[] activationTime,
            byte[] firmwareVersion,
            int warmupTime,
            byte[] compressedSerialNumber,
            byte[] nfcUid) {
        Validate.isTrue(bleAddress.length == 6);
        Validate.isTrue(sensorCode.length == 4);
        Validate.isTrue(activationTime.length == 4);
        Validate.isTrue(firmwareVersion.length == 4);
        Validate.isTrue(compressedSerialNumber.length == 9);
        Validate.isTrue(nfcUid.length == 8);
        return Bytes.combine(
                bleAddress,
                sensorCode,
                activationTime,
                firmwareVersion,
                new byte[]{ (byte) warmupTime },
                compressedSerialNumber,
                nfcUid);
    }

    public byte[] getBleAddress() {
        return bleAddress;
    }

    public byte[] getSensorCode() {
        return sensorCode;
    }

    public byte[] getActivationTime() {
        return activationTime;
    }

    public byte[] getFirmwareVersion() {
        return firmwareVersion;
    }

    public int getWarmupTime() {
        return warmupTime;
    }

    public byte[] getCompressedSerialNumber() {
        return compressedSerialNumber;
    }

    public byte[] getNfcUid() {
        return nfcUid;
    }
}

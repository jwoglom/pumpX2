package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.BleSoftwareInfoRequest;

/**
 * Returns information about the BLE soft device firmware.
 *
 * Binary layout (14 bytes total, all little-endian):
 *   offset 0,  2 bytes: softDeviceId          (uint16)
 *   offset 2,  2 bytes: softDeviceMajorVersion (uint16)
 *   offset 4,  2 bytes: softDeviceMinorVersion (uint16)
 *   offset 6,  2 bytes: softDeviceBugfixVersion(uint16)
 *   offset 8,  4 bytes: softDeviceVersion      (uint32)
 *   offset 12, 2 bytes: softDeviceSubVersion   (uint16)
 */
@MessageProps(
    opCode=-119,
    size=14,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CURRENT_STATUS,
    request=BleSoftwareInfoRequest.class
)
public class BleSoftwareInfoResponse extends Message {

    private int softDeviceId;
    private int softDeviceMajorVersion;
    private int softDeviceMinorVersion;
    private int softDeviceBugfixVersion;
    private long softDeviceVersion;
    private int softDeviceSubVersion;

    public BleSoftwareInfoResponse() {}

    public BleSoftwareInfoResponse(byte[] raw) {
        this.cargo = raw;
        parse(raw);
    }

    public BleSoftwareInfoResponse(int softDeviceId, int softDeviceMajorVersion, int softDeviceMinorVersion, int softDeviceBugfixVersion, long softDeviceVersion, int softDeviceSubVersion) {
        this.cargo = buildCargo(softDeviceId, softDeviceMajorVersion, softDeviceMinorVersion, softDeviceBugfixVersion, softDeviceVersion, softDeviceSubVersion);
        this.softDeviceId = softDeviceId;
        this.softDeviceMajorVersion = softDeviceMajorVersion;
        this.softDeviceMinorVersion = softDeviceMinorVersion;
        this.softDeviceBugfixVersion = softDeviceBugfixVersion;
        this.softDeviceVersion = softDeviceVersion;
        this.softDeviceSubVersion = softDeviceSubVersion;
    }

    public void parse(byte[] raw) {
        this.cargo = raw;
        this.softDeviceId = Bytes.readShort(raw, 0);
        this.softDeviceMajorVersion = Bytes.readShort(raw, 2);
        this.softDeviceMinorVersion = Bytes.readShort(raw, 4);
        this.softDeviceBugfixVersion = Bytes.readShort(raw, 6);
        this.softDeviceVersion = Bytes.readUint32(raw, 8);
        this.softDeviceSubVersion = Bytes.readShort(raw, 12);
    }

    public static byte[] buildCargo(int softDeviceId, int softDeviceMajorVersion, int softDeviceMinorVersion, int softDeviceBugfixVersion, long softDeviceVersion, int softDeviceSubVersion) {
        return Bytes.combine(
            Bytes.firstTwoBytesLittleEndian(softDeviceId),
            Bytes.firstTwoBytesLittleEndian(softDeviceMajorVersion),
            Bytes.firstTwoBytesLittleEndian(softDeviceMinorVersion),
            Bytes.firstTwoBytesLittleEndian(softDeviceBugfixVersion),
            Bytes.toUint32(softDeviceVersion),
            Bytes.firstTwoBytesLittleEndian(softDeviceSubVersion));
    }

    public int getSoftDeviceId() {
        return softDeviceId;
    }

    public int getSoftDeviceMajorVersion() {
        return softDeviceMajorVersion;
    }

    public int getSoftDeviceMinorVersion() {
        return softDeviceMinorVersion;
    }

    public int getSoftDeviceBugfixVersion() {
        return softDeviceBugfixVersion;
    }

    public long getSoftDeviceVersion() {
        return softDeviceVersion;
    }

    public int getSoftDeviceSubVersion() {
        return softDeviceSubVersion;
    }
}

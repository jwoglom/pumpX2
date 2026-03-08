package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ExtendedBolusStatusV2Request;

/**
 * Returns information on the current extended bolus (B variant).
 *
 * Binary layout (22 bytes):
 *   Byte   0:     bolusStatus (uint8)
 *   Bytes  1-2:   bolusId (uint16 little-endian)
 *   Bytes  3-4:   reserved (skipped)
 *   Bytes  5-8:   timestamp (uint32 little-endian)
 *   Bytes  9-12:  requestedVolume (uint32 little-endian)
 *   Bytes 13-16:  duration (uint32 little-endian, seconds)
 *   Byte  17:     bolusSource (uint8)
 *   Bytes 18-21:  secondsSincePumpReset (uint32 little-endian)
 *
 * This is equivalent to ExtendedBolusStatusResponse but adds secondsSincePumpReset
 * at the end and drops the "reserve" field from bytes 3-4 from the domain model.
 */
@MessageProps(
    opCode=-73,
    size=22,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CURRENT_STATUS,
    request=ExtendedBolusStatusV2Request.class
)
public class ExtendedBolusStatusV2Response extends Message {

    private int bolusStatus;
    private int bolusId;
    private long timestamp;
    private long requestedVolume;
    private long duration;
    private int bolusSource;
    private long secondsSincePumpReset;

    public ExtendedBolusStatusV2Response() {}

    public ExtendedBolusStatusV2Response(int bolusStatus, int bolusId, long timestamp, long requestedVolume, long duration, int bolusSource, long secondsSincePumpReset) {
        this.cargo = buildCargo(bolusStatus, bolusId, timestamp, requestedVolume, duration, bolusSource, secondsSincePumpReset);
        this.bolusStatus = bolusStatus;
        this.bolusId = bolusId;
        this.timestamp = timestamp;
        this.requestedVolume = requestedVolume;
        this.duration = duration;
        this.bolusSource = bolusSource;
        this.secondsSincePumpReset = secondsSincePumpReset;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.bolusStatus = raw[0];
        this.bolusId = Bytes.readShort(raw, 1);
        // bytes 3-4 are reserved/skipped
        this.timestamp = Bytes.readUint32(raw, 5);
        this.requestedVolume = Bytes.readUint32(raw, 9);
        this.duration = Bytes.readUint32(raw, 13);
        this.bolusSource = raw[17];
        this.secondsSincePumpReset = Bytes.readUint32(raw, 18);
    }

    public static byte[] buildCargo(int bolusStatus, int bolusId, long timestamp, long requestedVolume, long duration, int bolusSource, long secondsSincePumpReset) {
        return Bytes.combine(
            new byte[]{ (byte) bolusStatus },
            Bytes.firstTwoBytesLittleEndian(bolusId),
            new byte[]{ 0, 0 }, // reserved bytes 3-4
            Bytes.toUint32(timestamp),
            Bytes.toUint32(requestedVolume),
            Bytes.toUint32(duration),
            new byte[]{ (byte) bolusSource },
            Bytes.toUint32(secondsSincePumpReset)
        );
    }

    public int getBolusStatus() {
        return bolusStatus;
    }
    public int getBolusId() {
        return bolusId;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public long getRequestedVolume() {
        return requestedVolume;
    }
    public long getDuration() {
        return duration;
    }
    public int getBolusSource() {
        return bolusSource;
    }
    public long getSecondsSincePumpReset() {
        return secondsSincePumpReset;
    }
}

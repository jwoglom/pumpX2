package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.ApiVersionDependent;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.helpers.Dates;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.LastBolusStatusV3Request;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.BolusDeliveryHistoryLog;

import java.time.Instant;
import java.util.Set;

/**
 * Represents the last bolus status, version C. Contains both standard and extended bolus info.
 *
 * Binary layout (53 bytes total):
 *   byte  0      : lastBolusTypeBitmask (uint8, bit0=standard present, bit1=extended present)
 *   byte  1      : standardBolusStatus  (uint8, BolusStatusC id: 0=INVALID, 3=STOPPING, 4=FINISHED)
 *   bytes 2-3    : standardBolusId      (uint16 LE)
 *   bytes 4-5    : unknown/padding      (2 bytes)
 *   bytes 6-9    : standardBolusTimestamp (uint32 LE, seconds since pump epoch)
 *   bytes 10-13  : standardBolusDeliveredVolume (uint32 LE)
 *   byte  14     : standardBolusEndReason (uint8, BolusEndReason id)
 *   byte  15     : standardBolusSource    (uint8, BolusSource id)
 *   byte  16     : standardBolusTypeBitmask (uint8)
 *   bytes 17-20  : standardBolusRequestedVolume (uint32 LE)
 *   bytes 21-24  : standardBolusSecondsSincePumpReset (uint32 LE)
 *   byte  25     : extendedBolusStatus  (uint8, BolusStatusC id)
 *   bytes 26-27  : extendedBolusId      (uint16 LE)
 *   bytes 28-29  : unknown/padding      (2 bytes)
 *   bytes 30-33  : extendedBolusTimestamp (uint32 LE, seconds since pump epoch)
 *   bytes 34-37  : extendedBolusDeliveredVolume (uint32 LE)
 *   byte  38     : extendedBolusEndReason (uint8, BolusEndReason id)
 *   byte  39     : extendedBolusSource    (uint8, BolusSource id)
 *   byte  40     : extendedBolusTypeBitmask (uint8)
 *   bytes 41-44  : extendedBolusRequestedVolume (uint32 LE)
 *   bytes 45-48  : extendedBolusSecondsSincePumpReset (uint32 LE)
 *   bytes 49-52  : extendedBolusDuration (uint32 LE, seconds)
 *
 * Derived from decompiled Tandem Mobi Android app:
 *   LastBolusStatusV3Response$LastBolusStatusCCargo.java (using C8082b/C8083c dispatch tables)
 *
 * Compared to {@link LastBolusStatusV2Response}, this response contains info for both
 * a standard bolus and an extended bolus, plus secondsSincePumpReset fields.
 */
@MessageProps(
    opCode=-69,
    size=53,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CURRENT_STATUS,
    request=LastBolusStatusV3Request.class,
    minApi=KnownApiVersion.MOBI_API_V3_5
)
@ApiVersionDependent
public class LastBolusStatusV3Response extends Message {

    // lastBolusTypeBitmask: bit 0 = standard bolus present, bit 1 = extended bolus present
    private int lastBolusTypeBitmask;

    // Standard bolus fields
    private int standardBolusStatusId;
    private int standardBolusId;
    private byte[] standardUnknown;
    private long standardBolusTimestamp;
    private long standardBolusDeliveredVolume;
    private int standardBolusEndReasonId;
    private int standardBolusSourceId;
    private int standardBolusTypeBitmask;
    private long standardBolusRequestedVolume;
    private long standardBolusSecondsSincePumpReset;

    // Extended bolus fields
    private int extendedBolusStatusId;
    private int extendedBolusId;
    private byte[] extendedUnknown;
    private long extendedBolusTimestamp;
    private long extendedBolusDeliveredVolume;
    private int extendedBolusEndReasonId;
    private int extendedBolusSourceId;
    private int extendedBolusTypeBitmask;
    private long extendedBolusRequestedVolume;
    private long extendedBolusSecondsSincePumpReset;
    private long extendedBolusDuration;

    public LastBolusStatusV3Response() {}

    public LastBolusStatusV3Response(
            int lastBolusTypeBitmask,
            int standardBolusStatusId,
            int standardBolusId,
            byte[] standardUnknown,
            long standardBolusTimestamp,
            long standardBolusDeliveredVolume,
            int standardBolusEndReasonId,
            int standardBolusSourceId,
            int standardBolusTypeBitmask,
            long standardBolusRequestedVolume,
            long standardBolusSecondsSincePumpReset,
            int extendedBolusStatusId,
            int extendedBolusId,
            byte[] extendedUnknown,
            long extendedBolusTimestamp,
            long extendedBolusDeliveredVolume,
            int extendedBolusEndReasonId,
            int extendedBolusSourceId,
            int extendedBolusTypeBitmask,
            long extendedBolusRequestedVolume,
            long extendedBolusSecondsSincePumpReset,
            long extendedBolusDuration) {
        this.cargo = buildCargo(
                lastBolusTypeBitmask,
                standardBolusStatusId,
                standardBolusId,
                standardUnknown,
                standardBolusTimestamp,
                standardBolusDeliveredVolume,
                standardBolusEndReasonId,
                standardBolusSourceId,
                standardBolusTypeBitmask,
                standardBolusRequestedVolume,
                standardBolusSecondsSincePumpReset,
                extendedBolusStatusId,
                extendedBolusId,
                extendedUnknown,
                extendedBolusTimestamp,
                extendedBolusDeliveredVolume,
                extendedBolusEndReasonId,
                extendedBolusSourceId,
                extendedBolusTypeBitmask,
                extendedBolusRequestedVolume,
                extendedBolusSecondsSincePumpReset,
                extendedBolusDuration);
        this.lastBolusTypeBitmask = lastBolusTypeBitmask;
        this.standardBolusStatusId = standardBolusStatusId;
        this.standardBolusId = standardBolusId;
        this.standardUnknown = standardUnknown;
        this.standardBolusTimestamp = standardBolusTimestamp;
        this.standardBolusDeliveredVolume = standardBolusDeliveredVolume;
        this.standardBolusEndReasonId = standardBolusEndReasonId;
        this.standardBolusSourceId = standardBolusSourceId;
        this.standardBolusTypeBitmask = standardBolusTypeBitmask;
        this.standardBolusRequestedVolume = standardBolusRequestedVolume;
        this.standardBolusSecondsSincePumpReset = standardBolusSecondsSincePumpReset;
        this.extendedBolusStatusId = extendedBolusStatusId;
        this.extendedBolusId = extendedBolusId;
        this.extendedUnknown = extendedUnknown;
        this.extendedBolusTimestamp = extendedBolusTimestamp;
        this.extendedBolusDeliveredVolume = extendedBolusDeliveredVolume;
        this.extendedBolusEndReasonId = extendedBolusEndReasonId;
        this.extendedBolusSourceId = extendedBolusSourceId;
        this.extendedBolusTypeBitmask = extendedBolusTypeBitmask;
        this.extendedBolusRequestedVolume = extendedBolusRequestedVolume;
        this.extendedBolusSecondsSincePumpReset = extendedBolusSecondsSincePumpReset;
        this.extendedBolusDuration = extendedBolusDuration;
    }

    @Override
    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        // byte 0: lastBolusTypeBitmask (uint8)
        this.lastBolusTypeBitmask = raw[0] & 0xFF;
        // byte 1: standardBolusStatus (uint8)
        this.standardBolusStatusId = raw[1] & 0xFF;
        // bytes 2-3: standardBolusId (uint16 LE)
        this.standardBolusId = Bytes.readShort(raw, 2);
        // bytes 4-5: unknown/padding
        this.standardUnknown = new byte[]{raw[4], raw[5]};
        // bytes 6-9: standardBolusTimestamp (uint32 LE)
        this.standardBolusTimestamp = Bytes.readUint32(raw, 6);
        // bytes 10-13: standardBolusDeliveredVolume (uint32 LE)
        this.standardBolusDeliveredVolume = Bytes.readUint32(raw, 10);
        // byte 14: standardBolusEndReason (uint8)
        this.standardBolusEndReasonId = raw[14] & 0xFF;
        // byte 15: standardBolusSource (uint8)
        this.standardBolusSourceId = raw[15] & 0xFF;
        // byte 16: standardBolusTypeBitmask (uint8)
        this.standardBolusTypeBitmask = raw[16] & 0xFF;
        // bytes 17-20: standardBolusRequestedVolume (uint32 LE)
        this.standardBolusRequestedVolume = Bytes.readUint32(raw, 17);
        // bytes 21-24: standardBolusSecondsSincePumpReset (uint32 LE)
        this.standardBolusSecondsSincePumpReset = Bytes.readUint32(raw, 21);
        // byte 25: extendedBolusStatus (uint8)
        this.extendedBolusStatusId = raw[25] & 0xFF;
        // bytes 26-27: extendedBolusId (uint16 LE)
        this.extendedBolusId = Bytes.readShort(raw, 26);
        // bytes 28-29: unknown/padding
        this.extendedUnknown = new byte[]{raw[28], raw[29]};
        // bytes 30-33: extendedBolusTimestamp (uint32 LE)
        this.extendedBolusTimestamp = Bytes.readUint32(raw, 30);
        // bytes 34-37: extendedBolusDeliveredVolume (uint32 LE)
        this.extendedBolusDeliveredVolume = Bytes.readUint32(raw, 34);
        // byte 38: extendedBolusEndReason (uint8)
        this.extendedBolusEndReasonId = raw[38] & 0xFF;
        // byte 39: extendedBolusSource (uint8)
        this.extendedBolusSourceId = raw[39] & 0xFF;
        // byte 40: extendedBolusTypeBitmask (uint8)
        this.extendedBolusTypeBitmask = raw[40] & 0xFF;
        // bytes 41-44: extendedBolusRequestedVolume (uint32 LE)
        this.extendedBolusRequestedVolume = Bytes.readUint32(raw, 41);
        // bytes 45-48: extendedBolusSecondsSincePumpReset (uint32 LE)
        this.extendedBolusSecondsSincePumpReset = Bytes.readUint32(raw, 45);
        // bytes 49-52: extendedBolusDuration in seconds (uint32 LE)
        this.extendedBolusDuration = Bytes.readUint32(raw, 49);
    }

    public static byte[] buildCargo(
            int lastBolusTypeBitmask,
            int standardBolusStatusId,
            int standardBolusId,
            byte[] standardUnknown,
            long standardBolusTimestamp,
            long standardBolusDeliveredVolume,
            int standardBolusEndReasonId,
            int standardBolusSourceId,
            int standardBolusTypeBitmask,
            long standardBolusRequestedVolume,
            long standardBolusSecondsSincePumpReset,
            int extendedBolusStatusId,
            int extendedBolusId,
            byte[] extendedUnknown,
            long extendedBolusTimestamp,
            long extendedBolusDeliveredVolume,
            int extendedBolusEndReasonId,
            int extendedBolusSourceId,
            int extendedBolusTypeBitmask,
            long extendedBolusRequestedVolume,
            long extendedBolusSecondsSincePumpReset,
            long extendedBolusDuration) {
        return Bytes.combine(
            new byte[]{ (byte) lastBolusTypeBitmask },           // byte 0
            new byte[]{ (byte) standardBolusStatusId },          // byte 1
            Bytes.firstTwoBytesLittleEndian(standardBolusId),    // bytes 2-3
            standardUnknown,                                      // bytes 4-5
            Bytes.toUint32(standardBolusTimestamp),               // bytes 6-9
            Bytes.toUint32(standardBolusDeliveredVolume),         // bytes 10-13
            new byte[]{ (byte) standardBolusEndReasonId },        // byte 14
            new byte[]{ (byte) standardBolusSourceId },           // byte 15
            new byte[]{ (byte) standardBolusTypeBitmask },        // byte 16
            Bytes.toUint32(standardBolusRequestedVolume),         // bytes 17-20
            Bytes.toUint32(standardBolusSecondsSincePumpReset),   // bytes 21-24
            new byte[]{ (byte) extendedBolusStatusId },           // byte 25
            Bytes.firstTwoBytesLittleEndian(extendedBolusId),    // bytes 26-27
            extendedUnknown,                                      // bytes 28-29
            Bytes.toUint32(extendedBolusTimestamp),               // bytes 30-33
            Bytes.toUint32(extendedBolusDeliveredVolume),         // bytes 34-37
            new byte[]{ (byte) extendedBolusEndReasonId },        // byte 38
            new byte[]{ (byte) extendedBolusSourceId },           // byte 39
            new byte[]{ (byte) extendedBolusTypeBitmask },        // byte 40
            Bytes.toUint32(extendedBolusRequestedVolume),         // bytes 41-44
            Bytes.toUint32(extendedBolusSecondsSincePumpReset),   // bytes 45-48
            Bytes.toUint32(extendedBolusDuration)                 // bytes 49-52
        );
    }

    /**
     * Bitmask indicating which bolus types are present.
     * Bit 0 = standard bolus present, bit 1 = extended bolus present.
     */
    public int getLastBolusTypeBitmask() {
        return lastBolusTypeBitmask;
    }

    /** @return true if a standard bolus record is present */
    public boolean isStandardBolusPresent() {
        return (lastBolusTypeBitmask & 0x01) != 0;
    }

    /** @return true if an extended bolus record is present */
    public boolean isExtendedBolusPresent() {
        return (lastBolusTypeBitmask & 0x02) != 0;
    }

    // ---- Standard bolus getters ----

    public int getStandardBolusStatusId() {
        return standardBolusStatusId;
    }

    public int getStandardBolusId() {
        return standardBolusId;
    }

    public long getStandardBolusTimestamp() {
        return standardBolusTimestamp;
    }

    public Instant getStandardBolusTimestampInstant() {
        return Dates.fromJan12008EpochSecondsToDate(standardBolusTimestamp);
    }

    public long getStandardBolusDeliveredVolume() {
        return standardBolusDeliveredVolume;
    }

    public int getStandardBolusEndReasonId() {
        return standardBolusEndReasonId;
    }

    public int getStandardBolusSourceId() {
        return standardBolusSourceId;
    }

    public BolusDeliveryHistoryLog.BolusSource getStandardBolusSource() {
        return BolusDeliveryHistoryLog.BolusSource.fromId(standardBolusSourceId);
    }

    public int getStandardBolusTypeBitmask() {
        return standardBolusTypeBitmask;
    }

    public Set<BolusDeliveryHistoryLog.BolusType> getStandardBolusTypes() {
        return BolusDeliveryHistoryLog.BolusType.fromBitmask(standardBolusTypeBitmask);
    }

    public long getStandardBolusRequestedVolume() {
        return standardBolusRequestedVolume;
    }

    public long getStandardBolusSecondsSincePumpReset() {
        return standardBolusSecondsSincePumpReset;
    }

    // ---- Extended bolus getters ----

    public int getExtendedBolusStatusId() {
        return extendedBolusStatusId;
    }

    public int getExtendedBolusId() {
        return extendedBolusId;
    }

    public long getExtendedBolusTimestamp() {
        return extendedBolusTimestamp;
    }

    public Instant getExtendedBolusTimestampInstant() {
        return Dates.fromJan12008EpochSecondsToDate(extendedBolusTimestamp);
    }

    public long getExtendedBolusDeliveredVolume() {
        return extendedBolusDeliveredVolume;
    }

    public int getExtendedBolusEndReasonId() {
        return extendedBolusEndReasonId;
    }

    public int getExtendedBolusSourceId() {
        return extendedBolusSourceId;
    }

    public BolusDeliveryHistoryLog.BolusSource getExtendedBolusSource() {
        return BolusDeliveryHistoryLog.BolusSource.fromId(extendedBolusSourceId);
    }

    public int getExtendedBolusTypeBitmask() {
        return extendedBolusTypeBitmask;
    }

    public Set<BolusDeliveryHistoryLog.BolusType> getExtendedBolusTypes() {
        return BolusDeliveryHistoryLog.BolusType.fromBitmask(extendedBolusTypeBitmask);
    }

    public long getExtendedBolusRequestedVolume() {
        return extendedBolusRequestedVolume;
    }

    public long getExtendedBolusSecondsSincePumpReset() {
        return extendedBolusSecondsSincePumpReset;
    }

    /**
     * Duration of the extended bolus in seconds.
     */
    public long getExtendedBolusDuration() {
        return extendedBolusDuration;
    }
}

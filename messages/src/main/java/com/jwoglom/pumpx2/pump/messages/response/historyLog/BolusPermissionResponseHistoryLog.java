package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * Written with the pump's answer to each BolusPermissionRequest from a BLE app, right after the
 * matching {@link BolusPermissionRequestHistoryLog}. Tandem publishes no name for opcode 297, so
 * this class name is provisional. Bytes 12-13 and 16-25 were zero in every record observed.
 * See https://github.com/jwoglom/pumpX2/issues/142.
 */
@HistoryLogProps(
    opCode = 297,
    displayName = "Bolus Permission Response"
)
public class BolusPermissionResponseHistoryLog extends HistoryLog {

    private int bolusId;
    private int unknownU8At14;
    private int unknownU8At15;

    public BolusPermissionResponseHistoryLog() {}

    public BolusPermissionResponseHistoryLog(long pumpTimeSec, long sequenceNum, int bolusId, int unknownU8At14, int unknownU8At15) {
        this(pumpTimeSec, sequenceNum, bolusId, unknownU8At14, unknownU8At15, 0);
    }

    public BolusPermissionResponseHistoryLog(long pumpTimeSec, long sequenceNum, int bolusId, int unknownU8At14, int unknownU8At15, int headerHighNibble) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, bolusId, unknownU8At14, unknownU8At15, headerHighNibble);
        this.bolusId = bolusId;
        this.unknownU8At14 = unknownU8At14;
        this.unknownU8At15 = unknownU8At15;
    }

    public int typeId() {
        return 297;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.bolusId = Bytes.readShort(raw, 10);
        this.unknownU8At14 = raw[14] & 0xFF;
        this.unknownU8At15 = raw[15] & 0xFF;
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int bolusId, int unknownU8At14, int unknownU8At15, int headerHighNibble) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(297, headerHighNibble),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(bolusId),
            new byte[]{ 0, 0, (byte) unknownU8At14, (byte) unknownU8At15 }));
    }

    /**
     * @return the bolus id the pump granted, which is also the id in
     * BolusPermissionResponse; 0 when the request was refused
     */
    public int getBolusId() {
        return bolusId;
    }

    /**
     * @return byte 14. 0 when permission was granted. In every record for a refused request it was
     * 1, and so was byte 15, matching a BolusPermissionResponse of status 1 with nackReason 1
     * (INVALID_PUMPING_STATE); which of the two bytes is the status and which the nack reason is
     * not separable from those records.
     */
    public int getUnknownU8At14() {
        return unknownU8At14;
    }

    /**
     * @return byte 15. See {@link #getUnknownU8At14()}.
     */
    public int getUnknownU8At15() {
        return unknownU8At15;
    }
}

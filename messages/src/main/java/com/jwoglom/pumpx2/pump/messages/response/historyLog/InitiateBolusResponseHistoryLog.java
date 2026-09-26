package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * Written with the pump's answer to a BLE InitiateBolusRequest, after
 * {@link InitiateBolusRequestMsg3HistoryLog}. Tandem publishes no name for opcode 298, so this class
 * name is provisional. Bytes 12-13 and 16-25 were zero in every record observed.
 * See https://github.com/jwoglom/pumpX2/issues/142.
 */
@HistoryLogProps(
    opCode = 298,
    displayName = "Initiate Bolus Response"
)
public class InitiateBolusResponseHistoryLog extends HistoryLog {

    private int bolusId;
    private int unknownU8At14;
    private int unknownU8At15;

    public InitiateBolusResponseHistoryLog() {}

    public InitiateBolusResponseHistoryLog(long pumpTimeSec, long sequenceNum, int bolusId, int unknownU8At14, int unknownU8At15) {
        this(pumpTimeSec, sequenceNum, bolusId, unknownU8At14, unknownU8At15, 0);
    }

    public InitiateBolusResponseHistoryLog(long pumpTimeSec, long sequenceNum, int bolusId, int unknownU8At14, int unknownU8At15, int headerHighNibble) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, bolusId, unknownU8At14, unknownU8At15, headerHighNibble);
        this.bolusId = bolusId;
        this.unknownU8At14 = unknownU8At14;
        this.unknownU8At15 = unknownU8At15;
    }

    public int typeId() {
        return 298;
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
            HistoryLog.typeIdBytes(298, headerHighNibble),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(bolusId),
            new byte[]{ 0, 0, (byte) unknownU8At14, (byte) unknownU8At15 }));
    }

    public int getBolusId() {
        return bolusId;
    }

    /**
     * @return byte 14. 0 for every accepted bolus. The one rejected request observed wrote 2 here
     * and 1 in byte 15, alongside an InitiateBolusResponse of status 1 and statusTypeId 2
     * (REVOKED_PRIORITY); one record is not enough to confirm the mapping.
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

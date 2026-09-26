package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * First of three records (292, 293, 343) in which the pump copies the fields of an
 * {@link com.jwoglom.pumpx2.pump.messages.request.control.InitiateBolusRequest} received over BLE,
 * written right after BolusRequestedMsg3. Pump-UI and Control-IQ boluses do not write them. Every
 * field matched the request as sent. Tandem publishes no name for opcode 292, so this class name is
 * provisional. See https://github.com/jwoglom/pumpX2/issues/142.
 */
@HistoryLogProps(
    opCode = 292,
    displayName = "Initiate Bolus Request 1/3"
)
public class InitiateBolusRequestMsg1HistoryLog extends HistoryLog {

    private long requestTimestamp;
    private int transactionId;
    private int bolusTypeBitmask;
    private int bolusId;
    private long totalVolume;
    private long foodVolume;

    public InitiateBolusRequestMsg1HistoryLog() {}

    public InitiateBolusRequestMsg1HistoryLog(long pumpTimeSec, long sequenceNum, long requestTimestamp, int transactionId, int bolusTypeBitmask, int bolusId, long totalVolume, long foodVolume) {
        this(pumpTimeSec, sequenceNum, requestTimestamp, transactionId, bolusTypeBitmask, bolusId, totalVolume, foodVolume, 0);
    }

    public InitiateBolusRequestMsg1HistoryLog(long pumpTimeSec, long sequenceNum, long requestTimestamp, int transactionId, int bolusTypeBitmask, int bolusId, long totalVolume, long foodVolume, int headerHighNibble) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, requestTimestamp, transactionId, bolusTypeBitmask, bolusId, totalVolume, foodVolume, headerHighNibble);
        this.requestTimestamp = requestTimestamp;
        this.transactionId = transactionId;
        this.bolusTypeBitmask = bolusTypeBitmask;
        this.bolusId = bolusId;
        this.totalVolume = totalVolume;
        this.foodVolume = foodVolume;
    }

    public int typeId() {
        return 292;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.requestTimestamp = Bytes.readUint32(raw, 10);
        this.transactionId = raw[14] & 0xFF;
        this.bolusTypeBitmask = raw[15] & 0xFF;
        this.bolusId = Bytes.readShort(raw, 16);
        this.totalVolume = Bytes.readUint32(raw, 18);
        this.foodVolume = Bytes.readUint32(raw, 22);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long requestTimestamp, int transactionId, int bolusTypeBitmask, int bolusId, long totalVolume, long foodVolume, int headerHighNibble) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(292, headerHighNibble),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(requestTimestamp),
            new byte[]{ (byte) transactionId, (byte) bolusTypeBitmask },
            Bytes.firstTwoBytesLittleEndian(bolusId),
            Bytes.toUint32(totalVolume),
            Bytes.toUint32(foodVolume)));
    }

    /**
     * @return the time value from the signed InitiateBolusRequest's authentication trailer, as the
     * app sent it. See {@link BolusPermissionRequestHistoryLog#getRequestTimestamp()}.
     */
    public long getRequestTimestamp() {
        return requestTimestamp;
    }

    /**
     * @return the BLE transaction id the InitiateBolusRequest was sent with (0-255). Also written at
     * byte 10 of {@link InitiateBolusRequestMsg2HistoryLog} and {@link InitiateBolusRequestMsg3HistoryLog}.
     */
    public int getTransactionId() {
        return transactionId;
    }

    /**
     * @return InitiateBolusRequest.bolusTypeBitmask as sent
     */
    public int getBolusTypeBitmask() {
        return bolusTypeBitmask;
    }

    public int getBolusId() {
        return bolusId;
    }

    /**
     * @return InitiateBolusRequest.totalVolume as sent, in milliunits
     */
    public long getTotalVolume() {
        return totalVolume;
    }

    /**
     * @return InitiateBolusRequest.foodVolume as sent, in milliunits
     */
    public long getFoodVolume() {
        return foodVolume;
    }
}

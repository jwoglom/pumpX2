package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * Third of three records copying a BLE InitiateBolusRequest; see
 * {@link InitiateBolusRequestMsg1HistoryLog}. Tandem publishes no name for opcode 343, so this class
 * name is provisional. Byte 11 was zero in every record observed.
 * See https://github.com/jwoglom/pumpX2/issues/142.
 */
@HistoryLogProps(
    opCode = 343,
    displayName = "Initiate Bolus Request 3/3"
)
public class InitiateBolusRequestMsg3HistoryLog extends HistoryLog {

    private int transactionId;
    private int bolusId;
    private long bolusIOB;
    private long extendedSeconds;
    private long extended3;

    public InitiateBolusRequestMsg3HistoryLog() {}

    public InitiateBolusRequestMsg3HistoryLog(long pumpTimeSec, long sequenceNum, int transactionId, int bolusId, long bolusIOB, long extendedSeconds, long extended3) {
        this(pumpTimeSec, sequenceNum, transactionId, bolusId, bolusIOB, extendedSeconds, extended3, 0);
    }

    public InitiateBolusRequestMsg3HistoryLog(long pumpTimeSec, long sequenceNum, int transactionId, int bolusId, long bolusIOB, long extendedSeconds, long extended3, int headerHighNibble) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, transactionId, bolusId, bolusIOB, extendedSeconds, extended3, headerHighNibble);
        this.transactionId = transactionId;
        this.bolusId = bolusId;
        this.bolusIOB = bolusIOB;
        this.extendedSeconds = extendedSeconds;
        this.extended3 = extended3;
    }

    public int typeId() {
        return 343;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.transactionId = raw[10] & 0xFF;
        this.bolusId = Bytes.readShort(raw, 12);
        this.bolusIOB = Bytes.readUint32(raw, 14);
        this.extendedSeconds = Bytes.readUint32(raw, 18);
        this.extended3 = Bytes.readUint32(raw, 22);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int transactionId, int bolusId, long bolusIOB, long extendedSeconds, long extended3, int headerHighNibble) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(343, headerHighNibble),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{ (byte) transactionId, 0 },
            Bytes.firstTwoBytesLittleEndian(bolusId),
            Bytes.toUint32(bolusIOB),
            Bytes.toUint32(extendedSeconds),
            Bytes.toUint32(extended3)));
    }

    /**
     * @return the BLE transaction id of the InitiateBolusRequest; see
     * {@link InitiateBolusRequestMsg1HistoryLog#getTransactionId()}
     */
    public int getTransactionId() {
        return transactionId;
    }

    public int getBolusId() {
        return bolusId;
    }

    /**
     * @return InitiateBolusRequest.bolusIOB as sent by the app, in milliunits. This is the app's
     * figure, not the pump's IOB.
     */
    public long getBolusIOB() {
        return bolusIOB;
    }

    /**
     * @return InitiateBolusRequest.extendedSeconds as sent
     */
    public long getExtendedSeconds() {
        return extendedSeconds;
    }

    /**
     * @return InitiateBolusRequest.extended3 as sent; 0 in every record observed
     */
    public long getExtended3() {
        return extended3;
    }
}

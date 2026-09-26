package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * Second of three records copying a BLE InitiateBolusRequest; see
 * {@link InitiateBolusRequestMsg1HistoryLog}. Tandem publishes no name for opcode 293, so this class
 * name is provisional. Byte 11 was zero in every record observed.
 * See https://github.com/jwoglom/pumpX2/issues/142.
 */
@HistoryLogProps(
    opCode = 293,
    displayName = "Initiate Bolus Request 2/3"
)
public class InitiateBolusRequestMsg2HistoryLog extends HistoryLog {

    private int transactionId;
    private int bolusId;
    private long correctionVolume;
    private long extendedVolume;
    private int bolusCarbs;
    private int bolusBG;

    public InitiateBolusRequestMsg2HistoryLog() {}

    public InitiateBolusRequestMsg2HistoryLog(long pumpTimeSec, long sequenceNum, int transactionId, int bolusId, long correctionVolume, long extendedVolume, int bolusCarbs, int bolusBG) {
        this(pumpTimeSec, sequenceNum, transactionId, bolusId, correctionVolume, extendedVolume, bolusCarbs, bolusBG, 0);
    }

    public InitiateBolusRequestMsg2HistoryLog(long pumpTimeSec, long sequenceNum, int transactionId, int bolusId, long correctionVolume, long extendedVolume, int bolusCarbs, int bolusBG, int headerHighNibble) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, transactionId, bolusId, correctionVolume, extendedVolume, bolusCarbs, bolusBG, headerHighNibble);
        this.transactionId = transactionId;
        this.bolusId = bolusId;
        this.correctionVolume = correctionVolume;
        this.extendedVolume = extendedVolume;
        this.bolusCarbs = bolusCarbs;
        this.bolusBG = bolusBG;
    }

    public int typeId() {
        return 293;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.transactionId = raw[10] & 0xFF;
        this.bolusId = Bytes.readShort(raw, 12);
        this.correctionVolume = Bytes.readUint32(raw, 14);
        this.extendedVolume = Bytes.readUint32(raw, 18);
        this.bolusCarbs = Bytes.readShort(raw, 22);
        this.bolusBG = Bytes.readShort(raw, 24);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int transactionId, int bolusId, long correctionVolume, long extendedVolume, int bolusCarbs, int bolusBG, int headerHighNibble) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(293, headerHighNibble),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{ (byte) transactionId, 0 },
            Bytes.firstTwoBytesLittleEndian(bolusId),
            Bytes.toUint32(correctionVolume),
            Bytes.toUint32(extendedVolume),
            Bytes.firstTwoBytesLittleEndian(bolusCarbs),
            Bytes.firstTwoBytesLittleEndian(bolusBG)));
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
     * @return InitiateBolusRequest.correctionVolume as sent, in milliunits
     */
    public long getCorrectionVolume() {
        return correctionVolume;
    }

    /**
     * @return InitiateBolusRequest.extendedVolume as sent, in milliunits
     */
    public long getExtendedVolume() {
        return extendedVolume;
    }

    /**
     * @return InitiateBolusRequest.bolusCarbs as sent, in grams
     */
    public int getBolusCarbs() {
        return bolusCarbs;
    }

    /**
     * @return InitiateBolusRequest.bolusBG as sent, in mg/dL
     */
    public int getBolusBG() {
        return bolusBG;
    }
}

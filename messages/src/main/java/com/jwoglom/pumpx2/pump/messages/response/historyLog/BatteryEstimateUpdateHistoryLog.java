package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CurrentBatteryV2Response;

/**
 * One step of a multi-record battery procedure, written on both the Mobi and the t:slim X2 at no
 * fixed time of day. A sequence starts with {@link #getUnknown10()} = 1 and ends with 0, about 30
 * seconds per step. Some sequences end after the first step; others run three 1, 2, 3 cycles
 * first. In the 3 sequences with live battery traffic where the value changed, the final record's
 * {@link #getCurrentBatteryAbc()} equalled {@link #getUnknown13()} of the earlier records, and the
 * next {@link CurrentBatteryV2Response} reported that new value as currentBatteryAbc. So this looks
 * like a re-estimate of the battery's state of charge; that reading is an inference.
 *
 * The class name is provisional: opcode 330 is not in Tandem's cloud event schema or the Mobi
 * app's history log list. Fields not named here have not been identified and are exposed raw.
 */
@HistoryLogProps(
    opCode = 330,
    displayName = "Battery Estimate Update"
)
public class BatteryEstimateUpdateHistoryLog extends HistoryLog {

    private int unknown10;
    private int unknown11;
    private int currentBatteryAbc;
    private int unknown13;
    private int unknown14;
    private int lipoMv;
    private int currentBatteryIbc;
    private long elapsedMs;

    public BatteryEstimateUpdateHistoryLog() {}
    public BatteryEstimateUpdateHistoryLog(long pumpTimeSec, long sequenceNum, int unknown10, int unknown11, int currentBatteryAbc, int unknown13, int unknown14, int lipoMv, int currentBatteryIbc, long elapsedMs) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, unknown10, unknown11, currentBatteryAbc, unknown13, unknown14, lipoMv, currentBatteryIbc, elapsedMs);
        this.unknown10 = unknown10;
        this.unknown11 = unknown11;
        this.currentBatteryAbc = currentBatteryAbc;
        this.unknown13 = unknown13;
        this.unknown14 = unknown14;
        this.lipoMv = lipoMv;
        this.currentBatteryIbc = currentBatteryIbc;
        this.elapsedMs = elapsedMs;
    }

    public int typeId() {
        return 330;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.unknown10 = raw[10] & 0xFF;
        this.unknown11 = raw[11] & 0xFF;
        this.currentBatteryAbc = raw[12] & 0xFF;
        this.unknown13 = raw[13] & 0xFF;
        this.unknown14 = (short) Bytes.readShort(raw, 14);
        this.lipoMv = Bytes.readShort(raw, 16);
        this.currentBatteryIbc = raw[18] & 0xFF;
        this.elapsedMs = Bytes.readUint32(raw, 22);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int unknown10, int unknown11, int currentBatteryAbc, int unknown13, int unknown14, int lipoMv, int currentBatteryIbc, long elapsedMs) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(330, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstByteLittleEndian(unknown10),
            Bytes.firstByteLittleEndian(unknown11),
            Bytes.firstByteLittleEndian(currentBatteryAbc),
            Bytes.firstByteLittleEndian(unknown13),
            Bytes.firstTwoBytesLittleEndian(unknown14),
            Bytes.firstTwoBytesLittleEndian(lipoMv),
            Bytes.firstByteLittleEndian(currentBatteryIbc),
            new byte[3],
            Bytes.toUint32(elapsedMs)));
    }

    /**
     * Step within the sequence: 1, 2, 3 (occasionally 4) while it runs, 0 in its final record.
     *
     * @return raw uint8 at offset 10
     */
    public int getUnknown10() {
        return unknown10;
    }

    /**
     * 1 only in records where {@link #getUnknown10()} is 3, else 0.
     *
     * @return raw uint8 at offset 11
     */
    public int getUnknown11() {
        return unknown11;
    }

    /**
     * Equal to {@link CurrentBatteryV2Response#getCurrentBatteryAbc()} before the sequence; in the
     * final record, the value the pump reports afterwards.
     *
     * @return battery percent, 0-100
     */
    public int getCurrentBatteryAbc() {
        return currentBatteryAbc;
    }

    /**
     * Often above 100 in the first record of a sequence (always 136 on the Mobi); in later
     * records, the value {@link #getCurrentBatteryAbc()} takes in the final record.
     *
     * @return raw uint8 at offset 13
     */
    public int getUnknown13() {
        return unknown13;
    }

    /**
     * Signed; -38 to 213 observed.
     *
     * @return raw int16 at offset 14
     */
    public int getUnknown14() {
        return unknown14;
    }

    /**
     * Close to {@link CurrentBatteryV2Response#getUnknown3()} read around the same time.
     *
     * @return battery voltage in millivolts
     */
    public int getLipoMv() {
        return lipoMv;
    }

    /**
     * @return {@link CurrentBatteryV2Response#getCurrentBatteryIbc()} before the sequence
     */
    public int getCurrentBatteryIbc() {
        return currentBatteryIbc;
    }

    /**
     * Milliseconds since the first record of the sequence (exactly 30000, 60000, 62000, ... on the
     * t:slim X2, about 30300 per 30-second step on the Mobi); 0 in the final record.
     *
     * @return elapsed time in milliseconds
     */
    public long getElapsedMs() {
        return elapsedMs;
    }
}

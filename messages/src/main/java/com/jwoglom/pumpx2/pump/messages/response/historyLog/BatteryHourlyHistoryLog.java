package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CurrentBatteryV2Response;

/**
 * Battery snapshot the Tandem Mobi writes once an hour, at a fixed minute that drifts slowly and
 * differs between pumps (it is not aligned to the clock hour). Not seen on the t:slim X2.
 *
 * The class name is provisional: opcode 271 is not in Tandem's cloud event schema or the Mobi
 * app's history log list. Fields not named here have not been identified and are exposed raw.
 *
 * @see CurrentBatteryV2Response
 */
@HistoryLogProps(
    opCode = 271,
    displayName = "Battery Hourly"
)
public class BatteryHourlyHistoryLog extends HistoryLog {

    private int currentBatteryIbc;
    private int unknown12;
    private int lipoMv;
    private int unknown16;
    private int unknown18;
    private int unknown20;
    private int unknown22;
    private int unknown24;

    public BatteryHourlyHistoryLog() {}
    public BatteryHourlyHistoryLog(long pumpTimeSec, long sequenceNum, int currentBatteryIbc, int unknown12, int lipoMv, int unknown16, int unknown18, int unknown20, int unknown22, int unknown24) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, currentBatteryIbc, unknown12, lipoMv, unknown16, unknown18, unknown20, unknown22, unknown24);
        this.currentBatteryIbc = currentBatteryIbc;
        this.unknown12 = unknown12;
        this.lipoMv = lipoMv;
        this.unknown16 = unknown16;
        this.unknown18 = unknown18;
        this.unknown20 = unknown20;
        this.unknown22 = unknown22;
        this.unknown24 = unknown24;
    }

    public int typeId() {
        return 271;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.currentBatteryIbc = Bytes.readShort(raw, 10);
        this.unknown12 = Bytes.readShort(raw, 12);
        this.lipoMv = Bytes.readShort(raw, 14);
        this.unknown16 = (short) Bytes.readShort(raw, 16);
        this.unknown18 = Bytes.readShort(raw, 18);
        this.unknown20 = Bytes.readShort(raw, 20);
        this.unknown22 = Bytes.readShort(raw, 22);
        this.unknown24 = Bytes.readShort(raw, 24);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int currentBatteryIbc, int unknown12, int lipoMv, int unknown16, int unknown18, int unknown20, int unknown22, int unknown24) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(271, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(currentBatteryIbc),
            Bytes.firstTwoBytesLittleEndian(unknown12),
            Bytes.firstTwoBytesLittleEndian(lipoMv),
            Bytes.firstTwoBytesLittleEndian(unknown16),
            Bytes.firstTwoBytesLittleEndian(unknown18),
            Bytes.firstTwoBytesLittleEndian(unknown20),
            Bytes.firstTwoBytesLittleEndian(unknown22),
            Bytes.firstTwoBytesLittleEndian(unknown24)));
    }

    /**
     * Equal to {@link CurrentBatteryV2Response#getCurrentBatteryIbc()} read within 15 minutes in
     * 319 of 320 checked records (two pumps): the pump-displayed battery percentage.
     *
     * @return battery percent, 0-100
     */
    public int getCurrentBatteryIbc() {
        return currentBatteryIbc;
    }

    /**
     * Not a constant. Usually 0x0128. Bit 0x0004 was set in 76 of 77 records at 30% or lower and
     * in 1 record above that; the high byte was 0 or 2 only at 100%.
     *
     * @return raw uint16 at offset 12
     */
    public int getUnknown12() {
        return unknown12;
    }

    /**
     * Within 10 mV of {@link CurrentBatteryV2Response#getUnknown3()} read in the same minutes in
     * 289 of 320 checked records, and close to {@link DailyBasalHistoryLog#getLipoMv()}.
     *
     * @return battery voltage in millivolts
     */
    public int getLipoMv() {
        return lipoMv;
    }

    /**
     * Signed. 0 in most records and -5 to -10 in some. Positive in 4 of 750 records; the one of
     * those with live battery traffic was written while charging (+209, when
     * {@link CurrentBatteryV2Response#getUnknown4()} read 207).
     *
     * @return raw int16 at offset 16
     */
    public int getUnknown16() {
        return unknown16;
    }

    /**
     * Tracks {@link CurrentBatteryV2Response#getUnknown2()}; 100 * unknown18 / unknown20 is within
     * 1 of {@link CurrentBatteryV2Response#getCurrentBatteryAbc()} in 319 of 320 checked records
     * (2 in the other).
     *
     * @return raw uint16 at offset 18
     */
    public int getUnknown18() {
        return unknown18;
    }

    /**
     * Tracks {@link CurrentBatteryV2Response#getUnknown1()} (about 143-159).
     *
     * @return raw uint16 at offset 20
     */
    public int getUnknown20() {
        return unknown20;
    }

    /**
     * Same kind of value as {@link #getUnknown18()}, within 3 of it.
     *
     * @return raw uint16 at offset 22
     */
    public int getUnknown22() {
        return unknown22;
    }

    /**
     * Same kind of value as {@link #getUnknown20()}.
     *
     * @return raw uint16 at offset 24
     */
    public int getUnknown24() {
        return unknown24;
    }
}

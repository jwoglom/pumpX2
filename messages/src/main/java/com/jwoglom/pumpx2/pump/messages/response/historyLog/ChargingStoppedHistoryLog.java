package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CurrentBatteryV2Response;

/**
 * Battery snapshot written when charging stops, on both the Mobi and the t:slim X2, whether the
 * pump was taken off the charger or charging finished at 100%. Every
 * {@link CurrentBatteryV2Response} chargingStatus 1 to 0 transition seen in the maintainer's and a
 * tester's Mobi captures (9 of 9) has one within seconds, and every record matches such a
 * transition. On the t:slim X2 it usually follows a {@link UsbDisconnectedHistoryLog}.
 *
 * The class name is provisional: opcode 35 is not in Tandem's cloud event schema or the Mobi
 * app's history log list. {@link ChargingStartedHistoryLog} has the same layout. Fields not named
 * here have not been identified and are exposed raw.
 *
 * @see CurrentBatteryV2Response
 */
@HistoryLogProps(
    opCode = 35,
    displayName = "Charging Stopped"
)
public class ChargingStoppedHistoryLog extends HistoryLog {

    private int currentBatteryAbc;
    private int unknown12;
    private int unknown14;
    private int currentBatteryIbc;
    private int unknown18;
    private int lipoMv;
    private int unknown22;
    private int unknown24;

    public ChargingStoppedHistoryLog() {}
    public ChargingStoppedHistoryLog(long pumpTimeSec, long sequenceNum, int currentBatteryAbc, int unknown12, int unknown14, int currentBatteryIbc, int unknown18, int lipoMv, int unknown22, int unknown24) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, currentBatteryAbc, unknown12, unknown14, currentBatteryIbc, unknown18, lipoMv, unknown22, unknown24);
        this.currentBatteryAbc = currentBatteryAbc;
        this.unknown12 = unknown12;
        this.unknown14 = unknown14;
        this.currentBatteryIbc = currentBatteryIbc;
        this.unknown18 = unknown18;
        this.lipoMv = lipoMv;
        this.unknown22 = unknown22;
        this.unknown24 = unknown24;
    }

    public int typeId() {
        return 35;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.currentBatteryAbc = Bytes.readShort(raw, 10);
        this.unknown12 = Bytes.readShort(raw, 12);
        this.unknown14 = Bytes.readShort(raw, 14);
        this.currentBatteryIbc = Bytes.readShort(raw, 16);
        this.unknown18 = Bytes.readShort(raw, 18);
        this.lipoMv = Bytes.readShort(raw, 20);
        this.unknown22 = Bytes.readShort(raw, 22);
        this.unknown24 = Bytes.readShort(raw, 24);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int currentBatteryAbc, int unknown12, int unknown14, int currentBatteryIbc, int unknown18, int lipoMv, int unknown22, int unknown24) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(35, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(currentBatteryAbc),
            Bytes.firstTwoBytesLittleEndian(unknown12),
            Bytes.firstTwoBytesLittleEndian(unknown14),
            Bytes.firstTwoBytesLittleEndian(currentBatteryIbc),
            Bytes.firstTwoBytesLittleEndian(unknown18),
            Bytes.firstTwoBytesLittleEndian(lipoMv),
            Bytes.firstTwoBytesLittleEndian(unknown22),
            Bytes.firstTwoBytesLittleEndian(unknown24)));
    }

    /**
     * @return {@link CurrentBatteryV2Response#getCurrentBatteryAbc()} at the time of the record
     */
    public int getCurrentBatteryAbc() {
        return currentBatteryAbc;
    }

    /**
     * Equal to {@link CurrentBatteryV2Response#getUnknown1()} at the time of the record;
     * 100 * unknown18 / unknown12 is within 1 of {@link #getCurrentBatteryAbc()} in 246 of 249
     * records.
     *
     * @return raw uint16 at offset 12
     */
    public int getUnknown12() {
        return unknown12;
    }

    /**
     * unknown14 / 10 - 273.15 is within 2.5 of {@link #getUnknown24()} in 197 of 202 Mobi
     * records, so this is probably a temperature in 0.1 K (unconfirmed). Also present on the
     * t:slim X2 (2947-3083 there).
     *
     * @return raw uint16 at offset 14
     */
    public int getUnknown14() {
        return unknown14;
    }

    /**
     * @return {@link CurrentBatteryV2Response#getCurrentBatteryIbc()} at the time of the record
     */
    public int getCurrentBatteryIbc() {
        return currentBatteryIbc;
    }

    /**
     * Equal to {@link CurrentBatteryV2Response#getUnknown2()} at the time of the record.
     *
     * @return raw uint16 at offset 18
     */
    public int getUnknown18() {
        return unknown18;
    }

    /**
     * Equal (within 2 mV) to {@link CurrentBatteryV2Response#getUnknown3()} at the time of the
     * record.
     *
     * @return battery voltage in millivolts
     */
    public int getLipoMv() {
        return lipoMv;
    }

    /**
     * 0 on the t:slim X2 and on Mobi firmware 7.9.0.2. On an older-firmware Mobi it was 0, 1 or 2.
     *
     * @return raw uint16 at offset 22
     */
    public int getUnknown22() {
        return unknown22;
    }

    /**
     * 0 on the t:slim X2. On the Mobi it stays within a few units of
     * {@link #getUnknown14()} / 10 - 273.15, which suggests a second temperature reading in
     * degrees C (unconfirmed).
     *
     * @return raw uint16 at offset 24
     */
    public int getUnknown24() {
        return unknown24;
    }
}

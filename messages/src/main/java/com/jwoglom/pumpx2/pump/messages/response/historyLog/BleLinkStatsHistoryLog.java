package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * Hourly Tandem Mobi record of six signed-byte statistics, probably of the BLE link's RSSI in dBm
 * (values -95 to -18; not cross-checked against phone-side RSSI). In all 376 records from five
 * pumps, {@link #getMinimum()} is the lowest and {@link #getMaximum()} the highest of the six,
 * bytes 13-15 are non-decreasing multiples of 5 between them, and byte 12 lies between bytes 13
 * and 15. Written at a fixed minute that differs between pumps.
 *
 * The class name is provisional: opcode 473 is not in Tandem's cloud event schema or the Mobi
 * app's history log list. Fields not named here have not been identified and are exposed raw.
 */
@HistoryLogProps(
    opCode = 473,
    displayName = "BLE Link Stats"
)
public class BleLinkStatsHistoryLog extends HistoryLog {

    private int minimum;
    private int maximum;
    private int unknown12;
    private int unknown13;
    private int unknown14;
    private int unknown15;
    private int unknown16;
    private int unknown18;
    private int intervalSeconds;

    public BleLinkStatsHistoryLog() {}
    public BleLinkStatsHistoryLog(long pumpTimeSec, long sequenceNum, int minimum, int maximum, int unknown12, int unknown13, int unknown14, int unknown15, int unknown16, int unknown18, int intervalSeconds) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, minimum, maximum, unknown12, unknown13, unknown14, unknown15, unknown16, unknown18, intervalSeconds);
        this.minimum = minimum;
        this.maximum = maximum;
        this.unknown12 = unknown12;
        this.unknown13 = unknown13;
        this.unknown14 = unknown14;
        this.unknown15 = unknown15;
        this.unknown16 = unknown16;
        this.unknown18 = unknown18;
        this.intervalSeconds = intervalSeconds;
    }

    public int typeId() {
        return 473;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.minimum = raw[10];
        this.maximum = raw[11];
        this.unknown12 = raw[12];
        this.unknown13 = raw[13];
        this.unknown14 = raw[14];
        this.unknown15 = raw[15];
        this.unknown16 = Bytes.readShort(raw, 16);
        this.unknown18 = Bytes.readShort(raw, 18);
        this.intervalSeconds = Bytes.readShort(raw, 20);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int minimum, int maximum, int unknown12, int unknown13, int unknown14, int unknown15, int unknown16, int unknown18, int intervalSeconds) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(473, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstByteLittleEndian(minimum),
            Bytes.firstByteLittleEndian(maximum),
            Bytes.firstByteLittleEndian(unknown12),
            Bytes.firstByteLittleEndian(unknown13),
            Bytes.firstByteLittleEndian(unknown14),
            Bytes.firstByteLittleEndian(unknown15),
            Bytes.firstTwoBytesLittleEndian(unknown16),
            Bytes.firstTwoBytesLittleEndian(unknown18),
            Bytes.firstTwoBytesLittleEndian(intervalSeconds)));
    }

    /**
     * @return the lowest of the six signed statistics
     */
    public int getMinimum() {
        return minimum;
    }

    /**
     * @return the highest of the six signed statistics
     */
    public int getMaximum() {
        return maximum;
    }

    /**
     * Signed. Between {@link #getUnknown13()} and {@link #getUnknown15()}, and not quantized, so
     * possibly a mean.
     *
     * @return raw int8 at offset 12
     */
    public int getUnknown12() {
        return unknown12;
    }

    /**
     * Signed multiple of 5, &lt;= {@link #getUnknown14()}; possibly a percentile.
     *
     * @return raw int8 at offset 13
     */
    public int getUnknown13() {
        return unknown13;
    }

    /**
     * Signed multiple of 5, between {@link #getUnknown13()} and {@link #getUnknown15()}.
     *
     * @return raw int8 at offset 14
     */
    public int getUnknown14() {
        return unknown14;
    }

    /**
     * Signed multiple of 5, &gt;= {@link #getUnknown14()}.
     *
     * @return raw int8 at offset 15
     */
    public int getUnknown15() {
        return unknown15;
    }

    /**
     * 15-181 observed. Not the number of phone connections in the hour, and not correlated with the
     * phone's message traffic.
     *
     * @return raw uint16 at offset 16
     */
    public int getUnknown16() {
        return unknown16;
    }

    /**
     * 311-5408 observed. Not the phone's connected seconds in the hour.
     *
     * @return raw uint16 at offset 18
     */
    public int getUnknown18() {
        return unknown18;
    }

    /**
     * 3599-3601 observed; equal to the pump-time gap to the previous record in 334 of 363
     * hourly pairs and 1 second off in the other 29.
     *
     * @return the length of the period the record covers, in seconds
     */
    public int getIntervalSeconds() {
        return intervalSeconds;
    }
}

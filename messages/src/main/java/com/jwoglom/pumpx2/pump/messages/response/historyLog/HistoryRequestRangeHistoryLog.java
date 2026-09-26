package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * Part of the burst of records 488-497 the Tandem Mobi writes every two hours. Each covers the two
 * hours since the previous burst; the counts below were compared with the requests TandemKit
 * logged in 161 such windows on two pumps (the maintainer's and a tester's).
 *
 * The class name is provisional: opcode 491 is not in Tandem's cloud event schema or the Mobi
 * app's history log list. Fields not named here have not been identified and are exposed raw.
 */
@HistoryLogProps(
    opCode = 491,
    displayName = "History Request Range"
)
public class HistoryRequestRangeHistoryLog extends HistoryLog {

    private long unknown10;
    private long minStartLog;
    private long maxStartLog;
    private int unknown22;
    private int unknown24;

    public HistoryRequestRangeHistoryLog() {}
    public HistoryRequestRangeHistoryLog(long pumpTimeSec, long sequenceNum, long unknown10, long minStartLog, long maxStartLog, int unknown22, int unknown24) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, unknown10, minStartLog, maxStartLog, unknown22, unknown24);
        this.unknown10 = unknown10;
        this.minStartLog = minStartLog;
        this.maxStartLog = maxStartLog;
        this.unknown22 = unknown22;
        this.unknown24 = unknown24;
    }

    public int typeId() {
        return 491;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.unknown10 = Bytes.readUint32(raw, 10);
        this.minStartLog = Bytes.readUint32(raw, 14);
        this.maxStartLog = Bytes.readUint32(raw, 18);
        this.unknown22 = Bytes.readShort(raw, 22);
        this.unknown24 = Bytes.readShort(raw, 24);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long unknown10, long minStartLog, long maxStartLog, int unknown22, int unknown24) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(491, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(unknown10),
            Bytes.toUint32(minStartLog),
            Bytes.toUint32(maxStartLog),
            Bytes.firstTwoBytesLittleEndian(unknown22),
            Bytes.firstTwoBytesLittleEndian(unknown24)));
    }

    /**
     * Not the number of records the phone received: a median of 2.6 times that count
     * (0.97 to 431 times) in 158 windows. It correlates with the amount of history streamed.
     *
     * @return raw uint32 at offset 10
     */
    public long getUnknown10() {
        return unknown10;
    }

    /**
     * Equal to the lowest HistoryLogRequest startLog in the window in 161 of 161 windows.
     *
     * @return lowest requested startLog
     */
    public long getMinStartLog() {
        return minStartLog;
    }

    /**
     * Equal to the highest HistoryLogRequest startLog in the window in 161 of 161 windows.
     *
     * @return highest requested startLog
     */
    public long getMaxStartLog() {
        return maxStartLog;
    }

    /**
     * 0 in every record.
     *
     * @return raw uint16 at offset 22
     */
    public int getUnknown22() {
        return unknown22;
    }

    /**
     * Usually 1 to 4 below the number of HistoryLogRequests in the window (141 of 161
     * windows), equal in 1, and up to 25 below in windows with large backfills.
     *
     * @return raw uint16 at offset 24
     */
    public int getUnknown24() {
        return unknown24;
    }
}

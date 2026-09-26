package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * Part of the burst of records 488-497 the Tandem Mobi writes every two hours. Each covers the two
 * hours since the previous burst; the counts below were compared with the requests TandemKit
 * logged in 161 such windows on two pumps (the maintainer's and a tester's).
 *
 * The class name is provisional: opcode 493 is not in Tandem's cloud event schema or the Mobi
 * app's history log list. Fields not named here have not been identified and are exposed raw.
 */
@HistoryLogProps(
    opCode = 493,
    displayName = "Control Stream Counters"
)
public class ControlStreamCountersHistoryLog extends HistoryLog {

    private long controlStreamMessageCount;
    private long unknown14;
    private long unknown18;
    private long unknown22;

    public ControlStreamCountersHistoryLog() {}
    public ControlStreamCountersHistoryLog(long pumpTimeSec, long sequenceNum, long controlStreamMessageCount, long unknown14, long unknown18, long unknown22) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, controlStreamMessageCount, unknown14, unknown18, unknown22);
        this.controlStreamMessageCount = controlStreamMessageCount;
        this.unknown14 = unknown14;
        this.unknown18 = unknown18;
        this.unknown22 = unknown22;
    }

    public int typeId() {
        return 493;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.controlStreamMessageCount = Bytes.readUint32(raw, 10);
        this.unknown14 = Bytes.readUint32(raw, 14);
        this.unknown18 = Bytes.readUint32(raw, 18);
        this.unknown22 = Bytes.readUint32(raw, 22);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long controlStreamMessageCount, long unknown14, long unknown18, long unknown22) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(493, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(controlStreamMessageCount),
            Bytes.toUint32(unknown14),
            Bytes.toUint32(unknown18),
            Bytes.toUint32(unknown22)));
    }

    /**
     * Equal to the number of CONTROL_STREAM notifications (fill tubing, fill cannula, cartridge
     * and exit-fill states) the phone received in the window in 161 of 161 windows; nonzero in
     * 9 of them, all with cartridge or tubing fills.
     *
     * @return CONTROL_STREAM notifications sent in the window
     */
    public long getControlStreamMessageCount() {
        return controlStreamMessageCount;
    }

    /**
     * 0 in every record.
     *
     * @return raw uint32 at offset 14
     */
    public long getUnknown14() {
        return unknown14;
    }

    /**
     * Equal to {@link #getControlStreamMessageCount()} in every record.
     *
     * @return raw uint32 at offset 18
     */
    public long getUnknown18() {
        return unknown18;
    }

    /**
     * 0 in every record.
     *
     * @return raw uint32 at offset 22
     */
    public long getUnknown22() {
        return unknown22;
    }
}

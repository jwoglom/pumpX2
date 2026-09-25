package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * Part of the burst of records 488-497 the Tandem Mobi writes every two hours. Each covers the two
 * hours since the previous burst; the counts below were compared with the requests TandemKit
 * logged in 161 such windows on two pumps (the maintainer's and a tester's).
 *
 * The class name is provisional: opcode 492 is not in Tandem's cloud event schema or the Mobi
 * app's history log list. Fields not named here have not been identified and are exposed raw.
 */
@HistoryLogProps(
    opCode = 492,
    displayName = "Control Request Counters"
)
public class ControlRequestCountersHistoryLog extends HistoryLog {

    private long controlRequestCount;
    private long unknown14;
    private long unknown18;
    private long unknown22;

    public ControlRequestCountersHistoryLog() {}
    public ControlRequestCountersHistoryLog(long pumpTimeSec, long sequenceNum, long controlRequestCount, long unknown14, long unknown18, long unknown22) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, controlRequestCount, unknown14, unknown18, unknown22);
        this.controlRequestCount = controlRequestCount;
        this.unknown14 = unknown14;
        this.unknown18 = unknown18;
        this.unknown22 = unknown22;
    }

    public int typeId() {
        return 492;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.controlRequestCount = Bytes.readUint32(raw, 10);
        this.unknown14 = Bytes.readUint32(raw, 14);
        this.unknown18 = Bytes.readUint32(raw, 18);
        this.unknown22 = Bytes.readUint32(raw, 22);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long controlRequestCount, long unknown14, long unknown18, long unknown22) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(492, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(controlRequestCount),
            Bytes.toUint32(unknown14),
            Bytes.toUint32(unknown18),
            Bytes.toUint32(unknown22)));
    }

    /**
     * Equal to the number of CONTROL-characteristic requests the phone sent in the window in
     * 161 of 161 windows, and to unknown18 + unknown22 in every record.
     *
     * @return CONTROL requests received in the window
     */
    public long getControlRequestCount() {
        return controlRequestCount;
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
     * The CONTROL requests not counted in {@link #getUnknown22()} (temp rate, bolus, suspend,
     * resume and fill requests in the checked windows).
     *
     * @return raw uint32 at offset 18
     */
    public long getUnknown18() {
        return unknown18;
    }

    /**
     * Equal to the number of DismissNotification, SetMaxBasalLimit and SetMaxBolusLimit
     * requests in the window in 161 of 161 windows. What sets these apart is unknown.
     *
     * @return raw uint32 at offset 22
     */
    public long getUnknown22() {
        return unknown22;
    }
}

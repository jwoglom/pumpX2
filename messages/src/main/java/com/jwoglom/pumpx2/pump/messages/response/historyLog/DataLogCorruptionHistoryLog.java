package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 60,
    displayName = "Data Log Corruption",
    internalName = "LID_DATA_LOG_CORRUPTION",
    usedByTidepool = true
)
public class DataLogCorruptionHistoryLog extends HistoryLog {

    private long block;
    private int reason;

    public DataLogCorruptionHistoryLog() {}
    public DataLogCorruptionHistoryLog(long pumpTimeSec, long sequenceNum, long block, int reason) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, block, reason);
        this.block = block;
        this.reason = reason;

    }

    public DataLogCorruptionHistoryLog(long block, int reason) {
        this(0, 0, block, reason);
    }

    public int typeId() {
        return 60;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.block = Bytes.readUint32(raw, 10);
        this.reason = raw[17] & 0xFF;

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long block, int reason) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(60, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(block),
            new byte[]{0,0,0},
            new byte[]{(byte) reason}));
    }

    public long getBlock() {
        return block;
    }
    public int getReason() {
        return reason;
    }

}
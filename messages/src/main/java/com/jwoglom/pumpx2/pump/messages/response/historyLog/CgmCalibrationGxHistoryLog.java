package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 210,
    displayName = "CGM Calibration GX",
    internalName = "LID_CGM_CAL_GX",
    usedByTidepool = true
)
public class CgmCalibrationGxHistoryLog extends HistoryLog {
    
    private long value;

    public CgmCalibrationGxHistoryLog() {}
    public CgmCalibrationGxHistoryLog(long pumpTimeSec, long sequenceNum, long value) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, value);
        this.value = value;

    }

    public CgmCalibrationGxHistoryLog(long value) {
        this(0, 0, value);
    }

    /** @deprecated value is a uint32; use the long overload. Kept for binary compatibility. */
    @Deprecated
    public CgmCalibrationGxHistoryLog(long pumpTimeSec, long sequenceNum, int value) {
        this(pumpTimeSec, sequenceNum, value & 0xFFFFFFFFL);
    }

    /** @deprecated value is a uint32; use the long overload. Kept for binary compatibility. */
    @Deprecated
    public CgmCalibrationGxHistoryLog(int value) {
        this(0, 0, value & 0xFFFFFFFFL);
    }

    public int typeId() {
        return 210;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.value = Bytes.readUint32(raw, 10);

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long value) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(210, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(value)));
    }
    public long getValue() {
        return value;
    }
    
}
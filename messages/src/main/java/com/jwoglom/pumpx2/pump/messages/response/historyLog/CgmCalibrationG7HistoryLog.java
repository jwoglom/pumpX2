package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 438,
    displayName = "CGM Calibration (G7)",
    internalName = "LID_CGM_CAL_G7"
)
public class CgmCalibrationG7HistoryLog extends HistoryLog {

    private long bg;

    public CgmCalibrationG7HistoryLog() {}
    public CgmCalibrationG7HistoryLog(long pumpTimeSec, long sequenceNum, long bg) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, bg);
        this.bg = bg;
    }

    public CgmCalibrationG7HistoryLog(long bg) {
        this(0, 0, bg);
    }

    public int typeId() {
        return 438;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.bg = Bytes.readUint32(raw, 10);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long bg) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{(byte)(438 & 0xFF), (byte)(438 >> 8)}, // 438 = 0x01B6
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(bg)));
    }

    public long getBg() {
        return bg;
    }
}

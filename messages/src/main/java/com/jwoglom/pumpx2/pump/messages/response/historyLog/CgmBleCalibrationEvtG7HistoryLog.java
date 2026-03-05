package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 439,
    displayName = "CGM BLE Calibration Event (G7)",
    internalName = "LID_CGM_BLE_CALIBRATION_EVT_G7"
)
public class CgmBleCalibrationEvtG7HistoryLog extends HistoryLog {

    private boolean isCalibrationAccepted;

    public CgmBleCalibrationEvtG7HistoryLog() {}
    public CgmBleCalibrationEvtG7HistoryLog(long pumpTimeSec, long sequenceNum, boolean isCalibrationAccepted) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, isCalibrationAccepted);
        this.isCalibrationAccepted = isCalibrationAccepted;
    }

    public CgmBleCalibrationEvtG7HistoryLog(boolean isCalibrationAccepted) {
        this(0, 0, isCalibrationAccepted);
    }

    public int typeId() {
        return 439;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.isCalibrationAccepted = raw[10] != 0;
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, boolean isCalibrationAccepted) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{(byte)(439 & 0xFF), (byte)(439 >> 8)}, // 439 = 0x01B7
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{(byte)(isCalibrationAccepted ? 1 : 0)}));
    }

    public boolean isCalibrationAccepted() {
        return isCalibrationAccepted;
    }
}

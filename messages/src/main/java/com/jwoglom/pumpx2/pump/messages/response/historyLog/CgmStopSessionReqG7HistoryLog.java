package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 443,
    displayName = "CGM Stop Session Request (G7)",
    internalName = "LID_CGM_STOP_SESSION_REQ_G7"
)
public class CgmStopSessionReqG7HistoryLog extends HistoryLog {

    public CgmStopSessionReqG7HistoryLog() {}
    public CgmStopSessionReqG7HistoryLog(long pumpTimeSec, long sequenceNum) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum);
    }

    public int typeId() {
        return 443;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{(byte)(443 & 0xFF), (byte)(443 >> 8)}, // 443 = 0x01BB
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum)));
    }
}

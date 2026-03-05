package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 419,
    displayName = "TIPS Error",
    internalName = "LID_TIPS_ERROR"
)
public class TipsErrorHistoryLog extends HistoryLog {

    private int messageType;
    private int requestCode;
    private int errorCode;
    private boolean isDataMasked;

    public TipsErrorHistoryLog() {}
    public TipsErrorHistoryLog(long pumpTimeSec, long sequenceNum, int messageType, int requestCode, int errorCode, boolean isDataMasked) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, messageType, requestCode, errorCode, isDataMasked);
        this.messageType = messageType;
        this.requestCode = requestCode;
        this.errorCode = errorCode;
        this.isDataMasked = isDataMasked;
    }

    public TipsErrorHistoryLog(int messageType, int requestCode, int errorCode, boolean isDataMasked) {
        this(0, 0, messageType, requestCode, errorCode, isDataMasked);
    }

    public int typeId() {
        return 419;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.messageType = raw[10] & 0xFF;
        this.requestCode = raw[11] & 0xFF;
        this.errorCode = raw[12] & 0xFF;
        this.isDataMasked = raw[13] != 0;
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int messageType, int requestCode, int errorCode, boolean isDataMasked) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{(byte)(419 & 0xFF), (byte)(419 >> 8)}, // 419 = 0x01A3
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{(byte) messageType},
            new byte[]{(byte) requestCode},
            new byte[]{(byte) errorCode},
            new byte[]{(byte)(isDataMasked ? 1 : 0)}));
    }

    public int getMessageType() {
        return messageType;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public boolean isDataMasked() {
        return isDataMasked;
    }
}

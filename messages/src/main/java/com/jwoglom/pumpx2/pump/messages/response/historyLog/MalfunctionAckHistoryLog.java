package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 7,
    displayName = "Malfunction Ack",
    internalName = "LID_MALFUNCTION_ACK"
)
public class MalfunctionAckHistoryLog extends HistoryLog {

    private long malfId;

    public MalfunctionAckHistoryLog() {}
    public MalfunctionAckHistoryLog(long pumpTimeSec, long sequenceNum, long malfId) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, malfId);
        this.malfId = malfId;
    }

    public MalfunctionAckHistoryLog(long malfId) {
        this(0, 0, malfId);
    }

    public int typeId() {
        return 7;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.malfId = Bytes.readUint32(raw, 10);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long malfId) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{7, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(malfId)));
    }

    public long getMalfId() {
        return malfId;
    }

}

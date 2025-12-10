package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 6,
    displayName = "Malfunction Activated",
    internalName = "LID_MALFUNCTION_ACTIVATED"
)
public class MalfunctionHistoryLog extends HistoryLog {

    private long malfId;
    private long faultLocatorData;
    private long param1;
    private float param2;

    public MalfunctionHistoryLog() {}
    public MalfunctionHistoryLog(long pumpTimeSec, long sequenceNum, long malfId, long faultLocatorData, long param1, float param2) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, malfId, faultLocatorData, param1, param2);
        this.malfId = malfId;
        this.faultLocatorData = faultLocatorData;
        this.param1 = param1;
        this.param2 = param2;

    }

    public MalfunctionHistoryLog(long malfId, long faultLocatorData, long param1, float param2) {
        this(0, 0, malfId, faultLocatorData, param1, param2);
    }

    public int typeId() {
        return 6;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.malfId = Bytes.readUint32(raw, 10);
        this.faultLocatorData = Bytes.readUint32(raw, 14);
        this.param1 = Bytes.readUint32(raw, 18);
        this.param2 = Bytes.readFloat(raw, 22);

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long malfId, long faultLocatorData, long param1, float param2) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{6, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(malfId),
            Bytes.toUint32(faultLocatorData),
            Bytes.toUint32(param1),
            Bytes.toFloat(param2)));
    }

    public long getMalfId() {
        return malfId;
    }

    public long getFaultLocatorData() {
        return faultLocatorData;
    }

    public long getParam1() {
        return param1;
    }

    public float getParam2() {
        return param2;
    }

}

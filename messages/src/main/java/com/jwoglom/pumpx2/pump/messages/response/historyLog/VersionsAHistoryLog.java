package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 307,
    displayName = "Versions A",
    internalName = "LID_VERSIONS_A"
)
public class VersionsAHistoryLog extends HistoryLog {

    private long armPartNumber;
    private long armSwVersion;
    private long blePartNumber;
    private long bleSwVersion;

    public VersionsAHistoryLog() {}
    public VersionsAHistoryLog(long pumpTimeSec, long sequenceNum, long armPartNumber, long armSwVersion, long blePartNumber, long bleSwVersion) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, armPartNumber, armSwVersion, blePartNumber, bleSwVersion);
        this.armPartNumber = armPartNumber;
        this.armSwVersion = armSwVersion;
        this.blePartNumber = blePartNumber;
        this.bleSwVersion = bleSwVersion;

    }

    public VersionsAHistoryLog(long armPartNumber, long armSwVersion, long blePartNumber, long bleSwVersion) {
        this(0, 0, armPartNumber, armSwVersion, blePartNumber, bleSwVersion);
    }

    public int typeId() {
        return 307;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.armPartNumber = Bytes.readUint32(raw, 10);
        this.armSwVersion = Bytes.readUint32(raw, 14);
        this.blePartNumber = Bytes.readUint32(raw, 18);
        this.bleSwVersion = Bytes.readUint32(raw, 22);

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long armPartNumber, long armSwVersion, long blePartNumber, long bleSwVersion) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(307, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(armPartNumber),
            Bytes.toUint32(armSwVersion),
            Bytes.toUint32(blePartNumber),
            Bytes.toUint32(bleSwVersion)));
    }

    public long getArmPartNumber() {
        return armPartNumber;
    }

    public long getArmSwVersion() {
        return armSwVersion;
    }

    public long getBlePartNumber() {
        return blePartNumber;
    }

    public long getBleSwVersion() {
        return bleSwVersion;
    }
}

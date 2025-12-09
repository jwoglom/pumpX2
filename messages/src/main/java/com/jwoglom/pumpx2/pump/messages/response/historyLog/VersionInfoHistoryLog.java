package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 191,
    displayName = "Version Info",
    internalName = "LID_VERSION_INFO"
)
public class VersionInfoHistoryLog extends HistoryLog {

    private long version;
    private long configABits;
    private long configBBits;
    private int armCrc;

    public VersionInfoHistoryLog() {}
    public VersionInfoHistoryLog(long pumpTimeSec, long sequenceNum, long version, long configABits, long configBBits, int armCrc) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, version, configABits, configBBits, armCrc);
        this.version = version;
        this.configABits = configABits;
        this.configBBits = configBBits;
        this.armCrc = armCrc;

    }

    public VersionInfoHistoryLog(long version, long configABits, long configBBits, int armCrc) {
        this(0, 0, version, configABits, configBBits, armCrc);
    }

    public int typeId() {
        return 191;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.version = Bytes.readUint32(raw, 10);
        this.configABits = Bytes.readUint32(raw, 14);
        this.configBBits = Bytes.readUint32(raw, 18);
        this.armCrc = Bytes.readShort(raw, 24);

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long version, long configABits, long configBBits, int armCrc) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{(byte) 191, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(version),
            Bytes.toUint32(configABits),
            Bytes.toUint32(configBBits),
            new byte[]{0, 0},
            Bytes.firstTwoBytesLittleEndian(armCrc)));
    }

    public long getVersion() {
        return version;
    }

    public long getConfigABits() {
        return configABits;
    }

    public long getConfigBBits() {
        return configBBits;
    }

    public int getArmCrc() {
        return armCrc;
    }
}

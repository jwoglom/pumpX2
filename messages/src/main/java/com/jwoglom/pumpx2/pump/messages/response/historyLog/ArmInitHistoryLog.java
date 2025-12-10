package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 99,
    displayName = "Arm Init",
    internalName = "LID_ARM_INIT"
)
public class ArmInitHistoryLog extends HistoryLog {

    private long version;
    private long configABits;
    private long configBBits;
    private long numLogEntries;

    public ArmInitHistoryLog() {}
    public ArmInitHistoryLog(long pumpTimeSec, long sequenceNum, long version, long configABits, long configBBits, long numLogEntries) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, version, configABits, configBBits, numLogEntries);
        this.version = version;
        this.configABits = configABits;
        this.configBBits = configBBits;
        this.numLogEntries = numLogEntries;

    }

    public ArmInitHistoryLog(long version, long configABits, long configBBits, long numLogEntries) {
        this(0, 0, version, configABits, configBBits, numLogEntries);
    }

    public int typeId() {
        return 99;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.version = Bytes.readUint32(raw, 10);
        this.configABits = Bytes.readUint32(raw, 14);
        this.configBBits = Bytes.readUint32(raw, 18);
        this.numLogEntries = Bytes.readUint32(raw, 22);

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long version, long configABits, long configBBits, long numLogEntries) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{99, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(version),
            Bytes.toUint32(configABits),
            Bytes.toUint32(configBBits),
            Bytes.toUint32(numLogEntries)));
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

    public long getNumLogEntries() {
        return numLogEntries;
    }
}

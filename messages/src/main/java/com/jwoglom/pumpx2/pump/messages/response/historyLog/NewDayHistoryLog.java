package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 90,
    displayName = "New Day",
    internalName = "LID_NEW_DAY",
    usedByTidepool = true
)
public class NewDayHistoryLog extends HistoryLog {
    
    private float commandedBasalRate;
    private long featuresBitmask;
    private long featureBitmaskIndex;

    public NewDayHistoryLog() {}
    public NewDayHistoryLog(long pumpTimeSec, long sequenceNum, float commandedBasalRate, long featuresBitmask, long featureBitmaskIndex) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, commandedBasalRate, featuresBitmask, featureBitmaskIndex);
        this.commandedBasalRate = commandedBasalRate;
        this.featuresBitmask = featuresBitmask;
        this.featureBitmaskIndex = featureBitmaskIndex;

    }

    public NewDayHistoryLog(float commandedBasalRate, long featuresBitmask, long featureBitmaskIndex) {
        this(0, 0, commandedBasalRate, featuresBitmask, featureBitmaskIndex);
    }

    public int typeId() {
        return 90;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.commandedBasalRate = Bytes.readFloat(raw, 10);
        this.featuresBitmask = Bytes.readUint32(raw, 14);
        this.featureBitmaskIndex = Bytes.readUint32(raw, 18);

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, float commandedBasalRate, long featuresBitmask, long featureBitmaskIndex) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{90, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toFloat(commandedBasalRate),
            Bytes.toUint32(featuresBitmask),
            Bytes.toUint32(featureBitmaskIndex)));
    }
    public float getCommandedBasalRate() {
        return commandedBasalRate;
    }

    public long getFeaturesBitmask() {
        return featuresBitmask;
    }

    public long getFeatureBitmaskIndex() {
        return featureBitmaskIndex;
    }

}
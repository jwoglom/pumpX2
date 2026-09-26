package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.PumpFeaturesV1Response;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.PumpFeaturesV2Response;

import java.math.BigInteger;
import java.util.Set;
import java.util.TreeSet;

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
            HistoryLog.typeIdBytes(90, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toFloat(commandedBasalRate),
            Bytes.toUint32(featuresBitmask),
            Bytes.toUint32(featureBitmaskIndex)));
    }
    public float getCommandedBasalRate() {
        return commandedBasalRate;
    }

    /**
     * The pump's main feature bitmask: the {@code pumpFeaturesBitmask} that
     * {@link PumpFeaturesV2Response} returns for {@link PumpFeaturesV2Response.SupportedFeatureIndex#MAIN_FEATURES}
     * (index 0), which is also the {@link PumpFeaturesV1Response} bitmask. Decoded by
     * {@link #getPrimaryFeatures()}. Matched the live response on four of the maintainer's pumps
     * (t:slim X2 and Mobi).
     */
    public long getFeaturesBitmask() {
        return featuresBitmask;
    }

    public Set<PumpFeaturesV1Response.PumpFeatureType> getPrimaryFeatures() {
        return PumpFeaturesV1Response.PumpFeatureType.fromBitmask(BigInteger.valueOf(featuresBitmask));
    }

    /**
     * Despite the name, a bitmask of the {@link PumpFeaturesV2Response.SupportedFeatureIndex} values
     * the pump supports: bit {@code n} is set when {@code PumpFeaturesV2Request} for index {@code n}
     * gets a status-0 reply. Observed: 21 (indices 0, 2, 4) on older t:slim X2 firmware, 125 (0, 2-6)
     * on newer X2 firmware, 93 (0, 2, 3, 4, 6) on the Mobi. Decoded by
     * {@link #getSupportedFeatureIndices()}.
     */
    public long getFeatureBitmaskIndex() {
        return featureBitmaskIndex;
    }

    public Set<PumpFeaturesV2Response.SupportedFeatureIndex> getSupportedFeatureIndices() {
        Set<PumpFeaturesV2Response.SupportedFeatureIndex> indices = new TreeSet<>();
        for (int i = 0; i < 32; i++) {
            if (((featureBitmaskIndex >> i) & 1) == 1) {
                indices.add(PumpFeaturesV2Response.SupportedFeatureIndex.fromId(i));
            }
        }
        return indices;
    }

}
package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.PumpFeaturesV2Response;

import java.util.Arrays;
import java.util.Set;

/**
 * The pump's Control-IQ feature bitmask: the value {@link PumpFeaturesV2Response} returns for
 * {@link PumpFeaturesV2Response.SupportedFeatureIndex#CONTROL_IQ_FEATURES}, or 0 on firmware that
 * does not support that index. It is fixed by the firmware, not by settings: 0 on t:slim X2 API 2.5
 * and Mobi 7.6.0.3, 49 on t:slim X2 API 3.2/3.4, 3072 on Mobi 7.7.0.1 and 896 on Mobi 7.9.0.1 and
 * 7.9.0.2. Across 81 records from 8 pumps it matched the live response in every session that
 * captured both, and it changed only at the first boot after a firmware update.
 *
 * <p>Written at every boot, right after {@code ArmInit} and {@code VersionInfo} (on the X2, after
 * opcode 312 as well), and in the block the pump writes after every {@link NewDayHistoryLog}
 * ({@link VersionsAHistoryLog}, opcode 449, opcode 450, this record, {@link DailyStatusHistoryLog}):
 * at midnight, after a boot on a new calendar day, after a log erase and after a date change.
 * Older t:slim X2 firmware without PumpFeaturesV2 does not write it.
 *
 * <p>The class name is provisional: opcode 328 is not in Tandem's cloud event schema or the Mobi
 * app's history log list. Bytes 14-25 have been zero in every record and are exposed raw.
 * See https://github.com/jwoglom/pumpx2/issues/152.
 */
@HistoryLogProps(
    opCode = 328,
    displayName = "Control-IQ Features"
)
public class ControlIQFeaturesHistoryLog extends HistoryLog {

    private long controlIqFeaturesBitmask;
    private byte[] unknownTail;

    public ControlIQFeaturesHistoryLog() {}
    public ControlIQFeaturesHistoryLog(long pumpTimeSec, long sequenceNum, long controlIqFeaturesBitmask, byte[] unknownTail) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, controlIqFeaturesBitmask, unknownTail);
        this.controlIqFeaturesBitmask = controlIqFeaturesBitmask;
        this.unknownTail = Arrays.copyOf(unknownTail, 12);
    }

    public ControlIQFeaturesHistoryLog(long pumpTimeSec, long sequenceNum, long controlIqFeaturesBitmask) {
        this(pumpTimeSec, sequenceNum, controlIqFeaturesBitmask, new byte[12]);
    }

    public int typeId() {
        return 328;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.controlIqFeaturesBitmask = Bytes.readUint32(raw, 10);
        this.unknownTail = Arrays.copyOfRange(raw, 14, 26);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long controlIqFeaturesBitmask, byte[] unknownTail) {
        Validate.isTrue(unknownTail.length == 12, "unknownTail must be 12 bytes");
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(328, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(controlIqFeaturesBitmask),
            unknownTail));
    }

    /**
     * Only the low 16 bits have ever been set: 0, 49 (bits 0, 4, 5), 3072 (bits 10, 11) or 896
     * (bits 7, 8, 9) observed.
     *
     * @return the uint32 at offset 10, the PumpFeaturesV2 CONTROL_IQ_FEATURES bitmask
     */
    public long getControlIqFeaturesBitmask() {
        return controlIqFeaturesBitmask;
    }

    /**
     * The bits of {@link #getControlIqFeaturesBitmask()} that pumpX2 has names for. The names come
     * from {@link PumpFeaturesV2Response.ControlIqFeatureType} and are not confirmed by this record;
     * bits 4, 5, 7 and 10, which are set on some firmware, have no name and appear only in the raw
     * bitmask.
     *
     * @return the named Control-IQ features in the bitmask
     */
    public Set<PumpFeaturesV2Response.ControlIqFeatureType> getControlIqFeatures() {
        return PumpFeaturesV2Response.ControlIqFeatureType.fromBitmask(controlIqFeaturesBitmask);
    }

    /**
     * Bytes 14-25, all zero in every observed record (81/81).
     */
    public byte[] getUnknownTail() {
        return unknownTail;
    }
}

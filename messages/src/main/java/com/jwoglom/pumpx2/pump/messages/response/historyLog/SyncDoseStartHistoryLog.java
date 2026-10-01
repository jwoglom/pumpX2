package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * Synchronous dose start (AIQ / AIDANET algorithm).
 */
@HistoryLogProps(
    opCode = 558,
    displayName = "Sync Dose Start",
    internalName = "LID_SYNC_DOSE_START"
)
public class SyncDoseStartHistoryLog extends HistoryLog {

    private int doseId;
    private int doseSource;
    private int tempAdjustmentPercent;
    private float syncDoseSizeRequested;
    private int currentBasalRate;
    private int profileBasalRate;
    private float activeInsulin;

    public SyncDoseStartHistoryLog() {}
    public SyncDoseStartHistoryLog(long pumpTimeSec, long sequenceNum) {
        this(pumpTimeSec, sequenceNum, 0, 0, 0, 0, 0, 0, 0);
    }

    public SyncDoseStartHistoryLog(long pumpTimeSec, long sequenceNum, int doseId, int doseSource, int tempAdjustmentPercent, float syncDoseSizeRequested, int currentBasalRate, int profileBasalRate, float activeInsulin) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, doseId, doseSource, tempAdjustmentPercent, syncDoseSizeRequested, currentBasalRate, profileBasalRate, activeInsulin);
        parse(cargo);
    }

    public SyncDoseStartHistoryLog(int doseId, int doseSource, int tempAdjustmentPercent, float syncDoseSizeRequested, int currentBasalRate, int profileBasalRate, float activeInsulin) {
        this(0, 0, doseId, doseSource, tempAdjustmentPercent, syncDoseSizeRequested, currentBasalRate, profileBasalRate, activeInsulin);
    }

    public int typeId() {
        return 558;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.doseId = Bytes.readShort(raw, 10);
        this.doseSource = raw[12] & 0xFF;
        this.tempAdjustmentPercent = raw[13] & 0xFF;
        this.syncDoseSizeRequested = Bytes.readFloat(raw, 14);
        this.currentBasalRate = Bytes.readShort(raw, 18);
        this.profileBasalRate = Bytes.readShort(raw, 20);
        this.activeInsulin = Bytes.readFloat(raw, 22);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum) {
        return buildCargo(pumpTimeSec, sequenceNum, 0, 0, 0, 0, 0, 0, 0);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int doseId, int doseSource, int tempAdjustmentPercent, float syncDoseSizeRequested, int currentBasalRate, int profileBasalRate, float activeInsulin) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(558, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(doseId),
            new byte[]{(byte) doseSource},
            new byte[]{(byte) tempAdjustmentPercent},
            Bytes.toFloat(syncDoseSizeRequested),
            Bytes.firstTwoBytesLittleEndian(currentBasalRate),
            Bytes.firstTwoBytesLittleEndian(profileBasalRate),
            Bytes.toFloat(activeInsulin)));
    }

    /**
     * @return the dose ID
     */
    public int getDoseId() {
        return doseId;
    }
    /**
     * @return raw dose source, see {@link DoseSource}
     */
    public int getDoseSource() {
        return doseSource;
    }
    public DoseSource getDoseSourceEnum() {
        return DoseSource.fromId(doseSource);
    }
    /**
     * @return temporary adjustment percent (100 = 100%)
     */
    public int getTempAdjustmentPercent() {
        return tempAdjustmentPercent;
    }
    /**
     * @return requested dose size, in units
     */
    public float getSyncDoseSizeRequested() {
        return syncDoseSizeRequested;
    }
    /**
     * @return current basal rate, in milliunits/hr
     */
    public int getCurrentBasalRate() {
        return currentBasalRate;
    }
    /**
     * @return profile basal rate, in milliunits/hr
     */
    public int getProfileBasalRate() {
        return profileBasalRate;
    }
    /**
     * @return active insulin (IOB), in units
     */
    public float getActiveInsulin() {
        return activeInsulin;
    }

    public enum DoseSource {
        SUSPENDED(0),
        PROFILE(1),
        PROFILE_AND_TEMP_RATE(2),
        ALGORITHM(3),
        ALGORITHM_AND_TEMP_CONTROL_RATE(4)

        ;
        private final int id;
        DoseSource(int id) {
            this.id = id;
        }

        public static DoseSource fromId(int id) {
            for (DoseSource r : values()) {
                if (r.id == id) {
                    return r;
                }
            }
            return null;
        }

        public int getId() {
            return id;
        }
    }
}

package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * Synchronous dose complete (AIQ / AIDANET algorithm).
 */
@HistoryLogProps(
    opCode = 559,
    displayName = "Sync Dose Complete",
    internalName = "LID_SYNC_DOSE_COMPLETE"
)
public class SyncDoseCompleteHistoryLog extends HistoryLog {

    private int doseId;
    private int doseSource;
    private int tempAdjustmentPercent;
    private float syncDoseSizeDelivered;
    private int currentBasalRate;
    private float activeInsulin;

    public SyncDoseCompleteHistoryLog() {}
    public SyncDoseCompleteHistoryLog(long pumpTimeSec, long sequenceNum) {
        this(pumpTimeSec, sequenceNum, 0, 0, 0, 0, 0, 0);
    }

    public SyncDoseCompleteHistoryLog(long pumpTimeSec, long sequenceNum, int doseId, int doseSource, int tempAdjustmentPercent, float syncDoseSizeDelivered, int currentBasalRate, float activeInsulin) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, doseId, doseSource, tempAdjustmentPercent, syncDoseSizeDelivered, currentBasalRate, activeInsulin);
        parse(cargo);
    }

    public SyncDoseCompleteHistoryLog(int doseId, int doseSource, int tempAdjustmentPercent, float syncDoseSizeDelivered, int currentBasalRate, float activeInsulin) {
        this(0, 0, doseId, doseSource, tempAdjustmentPercent, syncDoseSizeDelivered, currentBasalRate, activeInsulin);
    }

    public int typeId() {
        return 559;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.doseId = Bytes.readShort(raw, 10);
        this.doseSource = raw[12] & 0xFF;
        this.tempAdjustmentPercent = raw[13] & 0xFF;
        this.syncDoseSizeDelivered = Bytes.readFloat(raw, 14);
        this.currentBasalRate = Bytes.readShort(raw, 18);
        this.activeInsulin = Bytes.readFloat(raw, 22);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum) {
        return buildCargo(pumpTimeSec, sequenceNum, 0, 0, 0, 0, 0, 0);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int doseId, int doseSource, int tempAdjustmentPercent, float syncDoseSizeDelivered, int currentBasalRate, float activeInsulin) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(559, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(doseId),
            new byte[]{(byte) doseSource},
            new byte[]{(byte) tempAdjustmentPercent},
            Bytes.toFloat(syncDoseSizeDelivered),
            Bytes.firstTwoBytesLittleEndian(currentBasalRate),
            new byte[]{0, 0},
            Bytes.toFloat(activeInsulin)));
    }

    /**
     * @return the dose ID
     */
    public int getDoseId() {
        return doseId;
    }
    /**
     * @return raw dose source, see {@link SyncDoseStartHistoryLog.DoseSource}
     */
    public int getDoseSource() {
        return doseSource;
    }
    /**
     * @return temporary adjustment percent (100 = 100%)
     */
    public int getTempAdjustmentPercent() {
        return tempAdjustmentPercent;
    }
    /**
     * @return delivered dose size, in units
     */
    public float getSyncDoseSizeDelivered() {
        return syncDoseSizeDelivered;
    }
    /**
     * @return current basal rate, in milliunits/hr
     */
    public int getCurrentBasalRate() {
        return currentBasalRate;
    }
    /**
     * @return active insulin (IOB), in units
     */
    public float getActiveInsulin() {
        return activeInsulin;
    }
}

package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * CGM Rise Rate Alert (RRA) settings change history log entry.
 *
 * Fields parsed from the decompiled CgmGlucoseAlertSettingsModel:
 *   alertLevel   (raw[10-11]): uint16 - the rise rate threshold value (mg/dL/min)
 *   repeatDuration (raw[12-13]): uint16 - repeat alert duration in minutes
 *   isEnabled    (raw[14]):    boolean - whether the alert is enabled
 *   modifiedField (raw[16-17]): uint16 - which field was changed:
 *                               0=ENABLE, 1=THRESHOLD, 2=DURATION, 3=INVALID
 */
@HistoryLogProps(
    opCode = 167,
    displayName = "CGM RRA Settings",
    internalName = "LID_CGM_RRA_SETTINGS"
)
public class CgmRraSettingsHistoryLog extends HistoryLog {

    private int alertLevel;
    private int repeatDuration;
    private boolean isEnabled;
    private int modifiedField;

    public CgmRraSettingsHistoryLog() {}
    public CgmRraSettingsHistoryLog(long pumpTimeSec, long sequenceNum, int alertLevel, int repeatDuration, boolean isEnabled, int modifiedField) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, alertLevel, repeatDuration, isEnabled, modifiedField);
        this.alertLevel = alertLevel;
        this.repeatDuration = repeatDuration;
        this.isEnabled = isEnabled;
        this.modifiedField = modifiedField;
    }

    public CgmRraSettingsHistoryLog(int alertLevel, int repeatDuration, boolean isEnabled, int modifiedField) {
        this(0, 0, alertLevel, repeatDuration, isEnabled, modifiedField);
    }

    public int typeId() {
        return 167;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.alertLevel = Bytes.readShort(raw, 10);
        this.repeatDuration = Bytes.readShort(raw, 12);
        this.isEnabled = raw[14] != 0;
        this.modifiedField = Bytes.readShort(raw, 16);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int alertLevel, int repeatDuration, boolean isEnabled, int modifiedField) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{(byte) 167, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(alertLevel),
            Bytes.firstTwoBytesLittleEndian(repeatDuration),
            new byte[]{(byte) (isEnabled ? 1 : 0), 0},
            Bytes.firstTwoBytesLittleEndian(modifiedField)));
    }

    /**
     * @return the rise rate alert threshold value (mg/dL/min)
     */
    public int getAlertLevel() {
        return alertLevel;
    }

    /**
     * @return the repeat alert duration in minutes
     */
    public int getRepeatDuration() {
        return repeatDuration;
    }

    /**
     * @return whether the rise rate alert is enabled
     */
    public boolean getIsEnabled() {
        return isEnabled;
    }

    /**
     * @return which field was modified: 0=ENABLE, 1=THRESHOLD, 2=DURATION, 3=INVALID
     */
    public int getModifiedField() {
        return modifiedField;
    }

    public enum ModifiedField {
        ENABLE(0),
        THRESHOLD(1),
        DURATION(2),
        INVALID(3),
        ;

        final int id;
        ModifiedField(int id) {
            this.id = id;
        }

        public static ModifiedField fromId(int id) {
            for (ModifiedField f : values()) {
                if (f.id == id) {
                    return f;
                }
            }
            return INVALID;
        }
    }

    public ModifiedField getModifiedFieldEnum() {
        return ModifiedField.fromId(modifiedField);
    }
}

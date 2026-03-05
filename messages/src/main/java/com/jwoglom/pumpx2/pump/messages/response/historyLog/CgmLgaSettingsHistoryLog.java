package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * CGM Low Glucose Alert (LGA) settings change history log entry.
 *
 * Fields parsed from the decompiled CgmGlucoseAlertSettingsModel:
 *   alertLevel   (raw[10-11]): uint16 - the alert threshold value (mg/dL)
 *   repeatDuration (raw[12-13]): uint16 - repeat alert duration in minutes
 *   isEnabled    (raw[14]):    boolean - whether the alert is enabled
 *   modifiedField (raw[16-17]): uint16 - which field was changed:
 *                               0=ENABLE, 1=THRESHOLD, 2=DURATION, 3=INVALID
 */
@HistoryLogProps(
    opCode = 166,
    displayName = "CGM LGA Settings",
    internalName = "LID_CGM_LGA_SETTINGS"
)
public class CgmLgaSettingsHistoryLog extends HistoryLog {

    private int alertLevel;
    private int repeatDuration;
    private boolean isEnabled;
    private int modifiedField;

    public CgmLgaSettingsHistoryLog() {}
    public CgmLgaSettingsHistoryLog(long pumpTimeSec, long sequenceNum, int alertLevel, int repeatDuration, boolean isEnabled, int modifiedField) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, alertLevel, repeatDuration, isEnabled, modifiedField);
        this.alertLevel = alertLevel;
        this.repeatDuration = repeatDuration;
        this.isEnabled = isEnabled;
        this.modifiedField = modifiedField;
    }

    public CgmLgaSettingsHistoryLog(int alertLevel, int repeatDuration, boolean isEnabled, int modifiedField) {
        this(0, 0, alertLevel, repeatDuration, isEnabled, modifiedField);
    }

    public int typeId() {
        return 166;
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
            new byte[]{(byte) 166, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(alertLevel),
            Bytes.firstTwoBytesLittleEndian(repeatDuration),
            new byte[]{(byte) (isEnabled ? 1 : 0), 0},
            Bytes.firstTwoBytesLittleEndian(modifiedField)));
    }

    /**
     * @return the alert threshold glucose level in mg/dL
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
     * @return whether the low glucose alert is enabled
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

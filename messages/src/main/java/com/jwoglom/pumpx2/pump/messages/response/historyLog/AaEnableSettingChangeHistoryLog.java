package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * Parsed from the Android app via ControlIqSettingChangeModel:
 * the payload contains a single byte at offset 0 (raw[10]) indicating
 * whether the AA (Control-IQ) feature is enabled (1) or disabled (0).
 */
@HistoryLogProps(
    opCode = 244,
    displayName = "AA Enable Setting Change",
    internalName = "LID_AA_ENABLE_SETTING_CHANGE",
    usedByAndroid = true
)
public class AaEnableSettingChangeHistoryLog extends HistoryLog {

    private int enabled;

    public AaEnableSettingChangeHistoryLog() {}
    public AaEnableSettingChangeHistoryLog(long pumpTimeSec, long sequenceNum, int enabled) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, enabled);
        this.enabled = enabled;

    }

    public AaEnableSettingChangeHistoryLog(int enabled) {
        this(0, 0, enabled);
    }

    public int typeId() {
        return 244;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.enabled = raw[10];

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int enabled) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{(byte)(244 & 0xFF), (byte)(244 >> 8)}, // 244
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{ (byte) enabled }));
    }

    /**
     * Returns 1 if AA (Control-IQ) is enabled, 0 if disabled.
     */
    public int getEnabled() {
        return enabled;
    }

    public boolean isEnabled() {
        return enabled == 1;
    }

}

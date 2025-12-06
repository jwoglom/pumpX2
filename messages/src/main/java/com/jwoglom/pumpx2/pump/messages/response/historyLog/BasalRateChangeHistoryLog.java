package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 3,
    displayName = "Basal Rate Change",
    internalName = "LID_BASAL_RATE_CHANGE",
    usedByTidepool = true
)
public class BasalRateChangeHistoryLog extends HistoryLog {
    
    private float commandBasalRate;
    private float baseBasalRate;
    private float maxBasalRate;
    private int insulinDeliveryProfile;
    private int changeTypeId;
    
    public BasalRateChangeHistoryLog() {}
    public BasalRateChangeHistoryLog(long pumpTimeSec, long sequenceNum, float commandBasalRate, float baseBasalRate, float maxBasalRate, int insulinDeliveryProfile, int changeTypeId) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, commandBasalRate, baseBasalRate, maxBasalRate, insulinDeliveryProfile, changeTypeId);
        this.commandBasalRate = commandBasalRate;
        this.baseBasalRate = baseBasalRate;
        this.maxBasalRate = maxBasalRate;
        this.insulinDeliveryProfile = insulinDeliveryProfile;
        this.changeTypeId = changeTypeId;
        
    }

    public BasalRateChangeHistoryLog(float commandBasalRate, float baseBasalRate, float maxBasalRate, int insulinDeliveryProfile, int changeTypeId) {
        this(0, 0, commandBasalRate, baseBasalRate, maxBasalRate, insulinDeliveryProfile, changeTypeId);
    }

    public int typeId() {
        return 3;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.commandBasalRate = Bytes.readFloat(raw, 10);
        this.baseBasalRate = Bytes.readFloat(raw, 14);
        this.maxBasalRate = Bytes.readFloat(raw, 18);
        this.insulinDeliveryProfile = Bytes.readShort(raw, 22);
        this.changeTypeId = raw[24];
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, float commandBasalRate, float baseBasalRate, float maxBasalRate, int insulinDeliveryProfile, int changeType) {
        return Bytes.combine(
            new byte[]{3, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toFloat(commandBasalRate), 
            Bytes.toFloat(baseBasalRate), 
            Bytes.toFloat(maxBasalRate), 
            Bytes.firstTwoBytesLittleEndian(insulinDeliveryProfile), 
            new byte[]{ (byte) changeType, 0 });
    }

    /**
     * @return the basal rate which is being changed to
     */
    public float getCommandBasalRate() {
        return commandBasalRate;
    }

    /**
     * @return the basal rate which is default for the current profile segment
     */
    public float getBaseBasalRate() {
        return baseBasalRate;
    }

    /**
     * @return the maximum allowed basal rate in the profile settings
     */
    public float getMaxBasalRate() {
        return maxBasalRate;
    }

    /**
     * @return the ID of the Insulin Delivery Profile (IDP)
     */
    public int getInsulinDeliveryProfile() {
        return insulinDeliveryProfile;
    }

    public int getChangeTypeId() {
        return changeTypeId;
    }

    enum ChangeType {
        TIMED_SEGMENT(1),
        NEW_PROFILE(2),
        TEMP_RATE_START(4),
        TEMP_RATE_END(8),
        PUMP_SUSPENDED(16),
        PUMP_RESUMED(32),
        PUMP_SHUT_DOWN(64),
        ;

        final int id;
        ChangeType(int id) {
            this.id = id;
        }

        public static ChangeType fromId(int id) {
            for (ChangeType c : values()) {
                if (c.id == id) {
                    return c;
                }
            }
            return null;
        }
    }

    /**
     * @return the reason that the basal rate was changed
     */
    public ChangeType getChangeType() {
        return ChangeType.fromId(changeTypeId);
    }
    
}
package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 3,
    displayName = "Basal Rate Change",
    usedByTidepool = true
)
public class BasalRateChangeHistoryLog extends HistoryLog {
    
    private float commandBasalRate;
    private float baseBasalRate;
    private float maxBasalRate;
    private int insulinDeliveryProfile;
    private int changeType;
    
    public BasalRateChangeHistoryLog() {}
    public BasalRateChangeHistoryLog(long pumpTimeSec, long sequenceNum, float commandBasalRate, float baseBasalRate, float maxBasalRate, int insulinDeliveryProfile, int changeType) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, commandBasalRate, baseBasalRate, maxBasalRate, insulinDeliveryProfile, changeType);
        this.commandBasalRate = commandBasalRate;
        this.baseBasalRate = baseBasalRate;
        this.maxBasalRate = maxBasalRate;
        this.insulinDeliveryProfile = insulinDeliveryProfile;
        this.changeType = changeType;
        
    }

    public BasalRateChangeHistoryLog(float commandBasalRate, float baseBasalRate, float maxBasalRate, int insulinDeliveryProfile, int changeType) {
        this(0, 0, commandBasalRate, baseBasalRate, maxBasalRate, insulinDeliveryProfile, changeType);
    }

    public int typeId() {
        return 3;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.commandBasalRate = Bytes.readFloat(raw, 10);
        this.baseBasalRate = Bytes.readFloat(raw, 14);
        this.maxBasalRate = Bytes.readFloat(raw, 18);
        this.insulinDeliveryProfile = Bytes.readShort(raw, 22);
        this.changeType = raw[24];
        
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
            new byte[]{ (byte) changeType });
    }
    public float getCommandBasalRate() {
        return commandBasalRate;
    }
    public float getBaseBasalRate() {
        return baseBasalRate;
    }
    public float getMaxBasalRate() {
        return maxBasalRate;
    }
    public int getInsulinDeliveryProfile() {
        return insulinDeliveryProfile;
    }
    public int getChangeType() {
        return changeType;
    }
    
}
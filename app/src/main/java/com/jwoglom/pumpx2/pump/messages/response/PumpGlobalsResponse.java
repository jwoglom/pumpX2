package com.jwoglom.pumpx2.pump.messages.response;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.PumpGlobalsRequest;

import java.math.BigInteger;

@MessageProps(
    opCode=87,
    size=14,
    type=MessageType.RESPONSE,
    request=PumpGlobalsRequest.class
)
public class PumpGlobalsResponse extends Message {
    
    private int quickBolusEnabled;
    private int quickBolusIncrementUnits;
    private int quickBolusIncrementCarbs;
    private int quickBolusEntryType;
    private int quickBolusStatus;
    private int buttonAnnun;
    private int quickBolusAnnun;
    private int bolusAnnun;
    private int reminderAnnun;
    private int alertAnnun;
    private int alarmAnnun;
    private int fillTubingAnnun;
    
    public PumpGlobalsResponse() {}
    
    public PumpGlobalsResponse(int quickBolusEnabled, int quickBolusIncrementUnits, int quickBolusIncrementCarbs, int quickBolusEntryType, int quickBolusStatus, int buttonAnnun, int quickBolusAnnun, int bolusAnnun, int reminderAnnun, int alertAnnun, int alarmAnnun, int fillTubingAnnun) {
        this.cargo = buildCargo(quickBolusEnabled, quickBolusIncrementUnits, quickBolusIncrementCarbs, quickBolusEntryType, quickBolusStatus, buttonAnnun, quickBolusAnnun, bolusAnnun, reminderAnnun, alertAnnun, alarmAnnun, fillTubingAnnun);
        this.quickBolusEnabled = quickBolusEnabled;
        this.quickBolusIncrementUnits = quickBolusIncrementUnits;
        this.quickBolusIncrementCarbs = quickBolusIncrementCarbs;
        this.quickBolusEntryType = quickBolusEntryType;
        this.quickBolusStatus = quickBolusStatus;
        this.buttonAnnun = buttonAnnun;
        this.quickBolusAnnun = quickBolusAnnun;
        this.bolusAnnun = bolusAnnun;
        this.reminderAnnun = reminderAnnun;
        this.alertAnnun = alertAnnun;
        this.alarmAnnun = alarmAnnun;
        this.fillTubingAnnun = fillTubingAnnun;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.quickBolusEnabled = raw[0];
        this.quickBolusIncrementUnits = Bytes.readShort(raw, 1);
        this.quickBolusIncrementCarbs = Bytes.readShort(raw, 3);
        this.quickBolusEntryType = raw[5];
        this.quickBolusStatus = raw[6];
        this.buttonAnnun = raw[7];
        this.quickBolusAnnun = raw[8];
        this.bolusAnnun = raw[9];
        this.reminderAnnun = raw[10];
        this.alertAnnun = raw[11];
        this.alarmAnnun = raw[12];
        this.fillTubingAnnun = raw[13];
        
    }

    
    public static byte[] buildCargo(int quickBolusEnabled, int quickBolusIncrementUnits, int quickBolusIncrementCarbs, int quickBolusEntryType, int quickBolusStatus, int buttonAnnun, int quickBolusAnnun, int bolusAnnun, int reminderAnnun, int alertAnnun, int alarmAnnun, int fillTubingAnnun) {
        return Bytes.combine(
            new byte[]{ (byte) quickBolusEnabled }, 
            Bytes.firstTwoBytesLittleEndian(quickBolusIncrementUnits), 
            Bytes.firstTwoBytesLittleEndian(quickBolusIncrementCarbs), 
            new byte[]{ (byte) quickBolusEntryType }, 
            new byte[]{ (byte) quickBolusStatus }, 
            new byte[]{ (byte) buttonAnnun }, 
            new byte[]{ (byte) quickBolusAnnun }, 
            new byte[]{ (byte) bolusAnnun }, 
            new byte[]{ (byte) reminderAnnun }, 
            new byte[]{ (byte) alertAnnun }, 
            new byte[]{ (byte) alarmAnnun }, 
            new byte[]{ (byte) fillTubingAnnun });
    }
    
    public int getQuickBolusEnabled() {
        return quickBolusEnabled;
    }
    public int getQuickBolusIncrementUnits() {
        return quickBolusIncrementUnits;
    }
    public int getQuickBolusIncrementCarbs() {
        return quickBolusIncrementCarbs;
    }
    public int getQuickBolusEntryType() {
        return quickBolusEntryType;
    }
    public int getQuickBolusStatus() {
        return quickBolusStatus;
    }
    public AnnunciationEnum getButtonAnnun() {
        return AnnunciationEnum.fromId(buttonAnnun);
    }
    public AnnunciationEnum getQuickBolusAnnun() {
        return AnnunciationEnum.fromId(quickBolusAnnun);
    }
    public AnnunciationEnum getBolusAnnun() {
        return AnnunciationEnum.fromId(bolusAnnun);
    }
    public AnnunciationEnum getReminderAnnun() {
        return AnnunciationEnum.fromId(reminderAnnun);
    }
    public AnnunciationEnum getAlertAnnun() {
        return AnnunciationEnum.fromId(alertAnnun);
    }
    public AnnunciationEnum getAlarmAnnun() {
        return AnnunciationEnum.fromId(alarmAnnun);
    }
    public AnnunciationEnum getFillTubingAnnun() {
        return AnnunciationEnum.fromId(fillTubingAnnun);
    }

    public enum AnnunciationEnum {
        AUDIO_HIGH(0),
        AUDIO_MEDIUM(1),
        AUDIO_LOW(2),
        VIBRATE(3),

        ;

        private final int id;
        AnnunciationEnum(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }

        public static AnnunciationEnum fromId(int id) {
            for (AnnunciationEnum a : AnnunciationEnum.values()) {
                if (a.id() == id) {
                    return a;
                }
            }
            return null;
        }
    }
    
}
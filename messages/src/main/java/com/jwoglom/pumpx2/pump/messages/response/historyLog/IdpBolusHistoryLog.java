package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 70,
    displayName = "Personal Profile (IDP) Bolus Data Change",
    usedByTidepool = true
)
public class IdpBolusHistoryLog extends HistoryLog {
    
    private int idp;
    private int modification;
    private int bolusStatus;
    private int insulinDuration;
    private int maxBolusSize;
    private int bolusEntryType;
    
    public IdpBolusHistoryLog() {}
    public IdpBolusHistoryLog(long pumpTimeSec, long sequenceNum, int idp, int modification, int bolusStatus, int insulinDuration, int maxBolusSize, int bolusEntryType) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, idp, modification, bolusStatus, insulinDuration, maxBolusSize, bolusEntryType);
        this.idp = idp;
        this.modification = modification;
        this.bolusStatus = bolusStatus;
        this.insulinDuration = insulinDuration;
        this.maxBolusSize = maxBolusSize;
        this.bolusEntryType = bolusEntryType;
        
    }

    public IdpBolusHistoryLog(int idp, int modification, int bolusStatus, int insulinDuration, int maxBolusSize, int bolusEntryType) {
        this(0, 0, idp, modification, bolusStatus, insulinDuration, maxBolusSize, bolusEntryType);
    }

    public int typeId() {
        return 70;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.idp = raw[10];
        this.modification = raw[11];
        this.bolusStatus = raw[12];
        this.insulinDuration = Bytes.readShort(raw, 14);
        this.maxBolusSize = Bytes.readShort(raw, 16);
        this.bolusEntryType = raw[18];
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int idp, int modification, int bolusStatus, int insulinDuration, int maxBolusSize, int bolusEntryType) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{70, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{ (byte) idp }, 
            new byte[]{ (byte) modification }, 
            new byte[]{ (byte) bolusStatus }, 
            Bytes.firstTwoBytesLittleEndian(insulinDuration), 
            Bytes.firstTwoBytesLittleEndian(maxBolusSize), 
            new byte[]{ (byte) bolusEntryType }));
    }
    public int getIdp() {
        return idp;
    }
    public int getModification() {
        return modification;
    }
    public int getBolusStatus() {
        return bolusStatus;
    }
    public int getInsulinDuration() {
        return insulinDuration;
    }
    public int getMaxBolusSize() {
        return maxBolusSize;
    }
    public int getBolusEntryType() {
        return bolusEntryType;
    }
    
}
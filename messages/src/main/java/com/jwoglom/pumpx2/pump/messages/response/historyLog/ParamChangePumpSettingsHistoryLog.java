package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 73,
    displayName = "Pump Settings Parameter Change",
    usedByTidepool = true
)
public class ParamChangePumpSettingsHistoryLog extends HistoryLog {
    
    private int modification;
    private int status;
    private int lowInsulinThreshold;
    private int cannulaPrimeSize;
    private int isFeatureLocked;
    private int autoShutdownEnabled;
    private int oledTimeout;
    private int autoShutdownDuration;
    
    public ParamChangePumpSettingsHistoryLog() {}
    public ParamChangePumpSettingsHistoryLog(long pumpTimeSec, long sequenceNum, int modification, int status, int lowInsulinThreshold, int cannulaPrimeSize, int isFeatureLocked, int autoShutdownEnabled, int oledTimeout, int autoShutdownDuration) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, modification, status, lowInsulinThreshold, cannulaPrimeSize, isFeatureLocked, autoShutdownEnabled, oledTimeout, autoShutdownDuration);
        this.modification = modification;
        this.status = status;
        this.lowInsulinThreshold = lowInsulinThreshold;
        this.cannulaPrimeSize = cannulaPrimeSize;
        this.isFeatureLocked = isFeatureLocked;
        this.autoShutdownEnabled = autoShutdownEnabled;
        this.oledTimeout = oledTimeout;
        this.autoShutdownDuration = autoShutdownDuration;
        
    }

    public ParamChangePumpSettingsHistoryLog(int modification, int status, int lowInsulinThreshold, int cannulaPrimeSize, int isFeatureLocked, int autoShutdownEnabled, int oledTimeout, int autoShutdownDuration) {
        this(0, 0, modification, status, lowInsulinThreshold, cannulaPrimeSize, isFeatureLocked, autoShutdownEnabled, oledTimeout, autoShutdownDuration);
    }

    public int typeId() {
        return 73;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.modification = raw[10];
        this.status = Bytes.readShort(raw, 12);
        this.lowInsulinThreshold = raw[14];
        this.cannulaPrimeSize = raw[15];
        this.isFeatureLocked = raw[16];
        this.autoShutdownEnabled = raw[17];
        this.oledTimeout = raw[19];
        this.autoShutdownDuration = Bytes.readShort(raw, 20);
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int modification, int status, int lowInsulinThreshold, int cannulaPrimeSize, int isFeatureLocked, int autoShutdownEnabled, int oledTimeout, int autoShutdownDuration) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{73, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{ (byte) modification }, 
            Bytes.firstTwoBytesLittleEndian(status), 
            new byte[]{ (byte) lowInsulinThreshold }, 
            new byte[]{ (byte) cannulaPrimeSize }, 
            new byte[]{ (byte) isFeatureLocked }, 
            new byte[]{ (byte) autoShutdownEnabled }, 
            new byte[]{ (byte) oledTimeout }, 
            Bytes.firstTwoBytesLittleEndian(autoShutdownDuration)));
    }
    public int getModification() {
        return modification;
    }
    public int getStatus() {
        return status;
    }
    public int getLowInsulinThreshold() {
        return lowInsulinThreshold;
    }
    public int getCannulaPrimeSize() {
        return cannulaPrimeSize;
    }
    public int getIsFeatureLocked() {
        return isFeatureLocked;
    }
    public int getAutoShutdownEnabled() {
        return autoShutdownEnabled;
    }
    public int getOledTimeout() {
        return oledTimeout;
    }
    public int getAutoShutdownDuration() {
        return autoShutdownDuration;
    }
    
}
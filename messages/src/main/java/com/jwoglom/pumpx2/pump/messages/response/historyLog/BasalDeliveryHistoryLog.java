package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 279,
    displayName = "Basal Delivery",
    internalName = "LID_BASAL_DELIVERY",
    usedByTidepool = true
)
public class BasalDeliveryHistoryLog extends HistoryLog {
    
    private int commandedRateSource;
    private int commandedRate;
    private int profileBasalRate;
    private int algorithmRate;
    private int tempRate;
    
    public BasalDeliveryHistoryLog() {}
    public BasalDeliveryHistoryLog(long pumpTimeSec, long sequenceNum, int commandedRateSource, int commandedRate, int profileBasalRate, int algorithmRate, int tempRate) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, commandedRateSource, commandedRate, profileBasalRate, algorithmRate, tempRate);
        this.commandedRateSource = commandedRateSource;
        this.commandedRate = commandedRate;
        this.profileBasalRate = profileBasalRate;
        this.algorithmRate = algorithmRate;
        this.tempRate = tempRate;
        
    }

    public BasalDeliveryHistoryLog(int commandedRateSource, int commandedRate, int profileBasalRate, int algorithmRate, int tempRate) {
        this(0, 0, commandedRateSource, commandedRate, profileBasalRate, algorithmRate, tempRate);
    }

    public int typeId() {
        return 279;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.commandedRateSource = Bytes.readShort(raw, 10);
        this.commandedRate = Bytes.readShort(raw, 14);
        this.profileBasalRate = Bytes.readShort(raw, 16);
        this.algorithmRate = Bytes.readShort(raw, 18);
        this.tempRate = Bytes.readShort(raw, 20);
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int commandedRateSource, int commandedRate, int profileBasalRate, int algorithmRate, int tempRate) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{23, 0}, // (byte) 279
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(commandedRateSource), 
            Bytes.firstTwoBytesLittleEndian(commandedRate), 
            Bytes.firstTwoBytesLittleEndian(profileBasalRate), 
            Bytes.firstTwoBytesLittleEndian(algorithmRate), 
            Bytes.firstTwoBytesLittleEndian(tempRate)));
    }
    public int getCommandedRateSource() {
        return commandedRateSource;
    }
    public int getCommandedRate() {
        return commandedRate;
    }
    public int getProfileBasalRate() {
        return profileBasalRate;
    }
    public int getAlgorithmRate() {
        return algorithmRate;
    }
    public int getTempRate() {
        return tempRate;
    }
    
}
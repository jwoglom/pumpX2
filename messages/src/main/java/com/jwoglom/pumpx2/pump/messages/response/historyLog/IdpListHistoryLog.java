package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 71,
    displayName = "Personal Profile (IDP) List",
    usedByTidepool = true
)
public class IdpListHistoryLog extends HistoryLog {
    
    private int numProfiles;
    private int slot1;
    private int slot2;
    private int slot3;
    private int slot4;
    private int slot5;
    private int slot6;
    
    public IdpListHistoryLog() {}
    public IdpListHistoryLog(long pumpTimeSec, long sequenceNum, int numProfiles, int slot1, int slot2, int slot3, int slot4, int slot5, int slot6) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, numProfiles, slot1, slot2, slot3, slot4, slot5, slot6);
        this.numProfiles = numProfiles;
        this.slot1 = slot1;
        this.slot2 = slot2;
        this.slot3 = slot3;
        this.slot4 = slot4;
        this.slot5 = slot5;
        this.slot6 = slot6;
        
    }

    public IdpListHistoryLog(int numProfiles, int slot1, int slot2, int slot3, int slot4, int slot5, int slot6) {
        this(0, 0, numProfiles, slot1, slot2, slot3, slot4, slot5, slot6);
    }

    public int typeId() {
        return 71;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.numProfiles = raw[10];
        this.slot1 = raw[14];
        this.slot2 = raw[15];
        this.slot3 = raw[16];
        this.slot4 = raw[17];
        this.slot5 = raw[18];
        this.slot6 = raw[19];
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int numProfiles, int slot1, int slot2, int slot3, int slot4, int slot5, int slot6) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{71, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{ (byte) numProfiles }, 
            new byte[]{ (byte) slot1 }, 
            new byte[]{ (byte) slot2 }, 
            new byte[]{ (byte) slot3 }, 
            new byte[]{ (byte) slot4 }, 
            new byte[]{ (byte) slot5 }, 
            new byte[]{ (byte) slot6 }));
    }
    public int getNumProfiles() {
        return numProfiles;
    }
    public int getSlot1() {
        return slot1;
    }
    public int getSlot2() {
        return slot2;
    }
    public int getSlot3() {
        return slot3;
    }
    public int getSlot4() {
        return slot4;
    }
    public int getSlot5() {
        return slot5;
    }
    public int getSlot6() {
        return slot6;
    }
    
}
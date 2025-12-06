package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 230, // -26
    displayName = "ControlIQ Pump Control Mode (PCM) Change",
    usedByTidepool = true // LID_AA_PCM_CHANGE
)
public class ControlIQPcmChangeHistoryLog extends HistoryLog {
    
    private int currentPcmId;
    private int previousPcmId;

    private PCM currentPcm;
    private PCM previousPcm;
    
    public ControlIQPcmChangeHistoryLog() {}
    public ControlIQPcmChangeHistoryLog(long pumpTimeSec, long sequenceNum, int currentPcmId, int previousPcmId) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, currentPcmId, previousPcmId);
        this.currentPcmId = currentPcmId;
        this.previousPcmId = previousPcmId;
        
    }

    public ControlIQPcmChangeHistoryLog(int currentPcmId, int previousPcmId) {
        this(0, 0, currentPcmId, previousPcmId);
    }

    public int typeId() {
        return 230;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.currentPcmId = raw[10];
        this.previousPcmId = raw[11];
        this.currentPcm = PCM.fromId(currentPcmId);
        this.previousPcm = PCM.fromId(previousPcmId);
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int currentPcm, int previousPcm) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{-26, 0}, // (byte) 230
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{ (byte) currentPcm }, 
            new byte[]{ (byte) previousPcm }));
    }
    public int getCurrentPcmId() {
        return currentPcmId;
    }
    public int getPreviousPcmId() {
        return previousPcmId;
    }

    public PCM getCurrentPcm() {
        return PCM.fromId(currentPcmId);
    }

    public PCM getPreviousPcm() {
        return PCM.fromId(previousPcmId);
    }

    public enum PCM {
        NO_CONTROL(0),
        OPEN_LOOP(1),
        CGM_INACTIVE(2),
        CLOSED_LOOP(3),

        ;

        private final int id;
        PCM(int id) {
            this.id = id;
        }

        public static PCM fromId(int id) {
            for (PCM s : values()) {
                if (s.id == id) {
                    return s;
                }
            }
            return null;
        }

        public int getId() {
            return id;
        }
    }
    
}
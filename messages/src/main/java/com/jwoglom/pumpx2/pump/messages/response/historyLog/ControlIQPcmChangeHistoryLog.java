package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 230, // -26
    displayName = "ControlIQ Pump Control Mode (PCM) Change",
    internalName = "LID_AA_PCM_CHANGE",
    usedByTidepool = true // LID_AA_PCM_CHANGE
)
public class ControlIQPcmChangeHistoryLog extends HistoryLog {

    private int currentPcmId;
    private int previousPcmId;

    private PCM currentPcm;
    private PCM previousPcm;

    private int pumpSuspendedRaw;
    private int calculationAvailableRaw;
    private int cgmAvailableRaw;
    private int closedLoopPreferredRaw;
    private int sufficientClosedLoopParamsRaw;

    public ControlIQPcmChangeHistoryLog() {}
    public ControlIQPcmChangeHistoryLog(long pumpTimeSec, long sequenceNum, int currentPcmId, int previousPcmId, int pumpSuspended, int calculationAvailable, int cgmAvailable, int closedLoopPreferred, int sufficientClosedLoopParams) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, currentPcmId, previousPcmId, pumpSuspended, calculationAvailable, cgmAvailable, closedLoopPreferred, sufficientClosedLoopParams);
        this.currentPcmId = currentPcmId;
        this.previousPcmId = previousPcmId;
        this.currentPcm = PCM.fromId(currentPcmId);
        this.previousPcm = PCM.fromId(previousPcmId);
        this.pumpSuspendedRaw = pumpSuspended;
        this.calculationAvailableRaw = calculationAvailable;
        this.cgmAvailableRaw = cgmAvailable;
        this.closedLoopPreferredRaw = closedLoopPreferred;
        this.sufficientClosedLoopParamsRaw = sufficientClosedLoopParams;

    }

    public ControlIQPcmChangeHistoryLog(int currentPcmId, int previousPcmId, int pumpSuspended, int calculationAvailable, int cgmAvailable, int closedLoopPreferred, int sufficientClosedLoopParams) {
        this(0, 0, currentPcmId, previousPcmId, pumpSuspended, calculationAvailable, cgmAvailable, closedLoopPreferred, sufficientClosedLoopParams);
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
        this.pumpSuspendedRaw = raw[12];
        this.calculationAvailableRaw = raw[13];
        this.cgmAvailableRaw = raw[14];
        this.closedLoopPreferredRaw = raw[15];
        this.sufficientClosedLoopParamsRaw = raw[16];

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int currentPcm, int previousPcm, int pumpSuspended, int calculationAvailable, int cgmAvailable, int closedLoopPreferred, int sufficientClosedLoopParams) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(230, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{ (byte) currentPcm },
            new byte[]{ (byte) previousPcm },
            new byte[]{ (byte) pumpSuspended },
            new byte[]{ (byte) calculationAvailable },
            new byte[]{ (byte) cgmAvailable },
            new byte[]{ (byte) closedLoopPreferred },
            new byte[]{ (byte) sufficientClosedLoopParams }));
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

    public int getPumpSuspendedRaw() {
        return pumpSuspendedRaw;
    }

    public boolean isPumpSuspended() {
        return pumpSuspendedRaw != 0;
    }

    public int getCalculationAvailableRaw() {
        return calculationAvailableRaw;
    }

    public boolean isCalculationAvailable() {
        return calculationAvailableRaw != 0;
    }

    public int getCgmAvailableRaw() {
        return cgmAvailableRaw;
    }

    public boolean isCgmAvailable() {
        return cgmAvailableRaw != 0;
    }

    public int getClosedLoopPreferredRaw() {
        return closedLoopPreferredRaw;
    }

    public boolean isClosedLoopPreferred() {
        return closedLoopPreferredRaw != 0;
    }

    public int getSufficientClosedLoopParamsRaw() {
        return sufficientClosedLoopParamsRaw;
    }

    public boolean isSufficientClosedLoopParams() {
        return sufficientClosedLoopParamsRaw != 0;
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

package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 11,
    displayName = "Pumping Suspended",
    usedByTidepool = true
)
public class PumpingSuspendedHistoryLog extends HistoryLog {
    
    private int insulinAmount;
    private int reasonId;
    
    public PumpingSuspendedHistoryLog() {}
    public PumpingSuspendedHistoryLog(long pumpTimeSec, long sequenceNum, int insulinAmount, int reasonId) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, insulinAmount, reasonId);
        this.insulinAmount = insulinAmount;
        this.reasonId = reasonId;
        
    }

    public PumpingSuspendedHistoryLog(int insulinAmount, int reasonId) {
        this(0, 0, insulinAmount, reasonId);
    }

    public int typeId() {
        return 11;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.insulinAmount = Bytes.readShort(raw, 14);
        this.reasonId = raw[16];
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int insulinAmount, int reason) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{11, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(insulinAmount), 
            new byte[]{ (byte) reason }));
    }

    /**
     * @return insulin amount in milliunits at the time pumping was suspended (I think this matches
     * with the IOB, needs confirmation)
     */
    public int getInsulinAmount() {
        return insulinAmount;
    }
    public int getReasonId() {
        return reasonId;
    }
    public SuspendReason getReason() {
        return SuspendReason.fromId(reasonId);
    }

    enum SuspendReason {
        USER_ABORTED(0),
        ALARM(1),
        MALFUNCTION(2),
        AUTO_SUSPEND_PREDICTIVE_LOW_GLUCOSE(6),

        ;
        private final int id;
        SuspendReason(int id) {
            this.id = id;
        }

        static SuspendReason fromId(int id) {
            for (SuspendReason r : values()) {
                if (r.id == id) {
                    return r;
                }
            }
            return null;
        }
    }
    
}
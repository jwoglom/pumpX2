package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 11,
    displayName = "Pumping Suspended",
    internalName = "LID_PUMPING_SUSPENDED",
    usedByTidepool = true
)
public class PumpingSuspendedHistoryLog extends HistoryLog {

    private long preSuspendState;
    private int insulinAmount;
    private int reasonId;
    private int rpaTimeout;

    public PumpingSuspendedHistoryLog() {}
    public PumpingSuspendedHistoryLog(long pumpTimeSec, long sequenceNum, long preSuspendState, int insulinAmount, int reasonId, int rpaTimeout) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, preSuspendState, insulinAmount, reasonId, rpaTimeout);
        this.preSuspendState = preSuspendState;
        this.insulinAmount = insulinAmount;
        this.reasonId = reasonId;
        this.rpaTimeout = rpaTimeout;

    }

    public PumpingSuspendedHistoryLog(long preSuspendState, int insulinAmount, int reasonId, int rpaTimeout) {
        this(0, 0, preSuspendState, insulinAmount, reasonId, rpaTimeout);
    }

    public int typeId() {
        return 11;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.preSuspendState = Bytes.readUint32(raw, 10);
        this.insulinAmount = Bytes.readShort(raw, 14);
        this.reasonId = raw[16];
        this.rpaTimeout = raw[17];

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long preSuspendState, int insulinAmount, int reason, int rpaTimeout) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(11, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(preSuspendState),
            Bytes.firstTwoBytesLittleEndian(insulinAmount),
            new byte[]{ (byte) reason, (byte) rpaTimeout }));
    }

    /**
     * @return the state of the pump immediately before pumping was suspended
     * (Tandem's pump-logs JSON calls this property preSuspendState).
     * 106 in every record observed so far across t:slim X2 and Mobi, for both user-initiated
     * and alarm-triggered suspends; no other value has been seen.
     */
    public long getPreSuspendState() {
        return preSuspendState;
    }

    /**
     * @return the insulin remaining in the reservoir as the pump reports it at the instant
     * pumping was suspended, in whole units. This is the same number
     * {@code InsulinStatusResponse.currentInsulinAmount} returns: on a Mobi capture the two were
     * byte-identical when the status was polled within two seconds of the record, in 4 of 4
     * suspends and 4 of 4 resumes. It is not the cartridge fill level (it dropped 110 -> 90
     * across a tubing fill on the same cartridge) and it is not the IOB (the IOB the pump reports
     * in LID_DAILY_BASAL decays over a suspension while this value does not). The paired
     * suspended and resumed records which bracket a suspension carry the same value when nothing
     * was delivered in between.
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

    /**
     * @return the resume pump alert (RPA) timeout in minutes: how long the pump waits while
     * suspended before alerting that insulin delivery is still stopped. 0 when unset.
     * The value 15 was observed on every suspend in a Mobi capture, and the suspensions that
     * lasted longer than 15 minutes each raised RESUME_PUMP_ALARM (18) and RESUME_PUMP_ALARM2 (23)
     * 15 min 1 s after the suspend, confirming the unit is minutes.
     * (Tandem's pump-logs JSON calls this property rpaTimeout)
     */
    public int getRpaTimeout() {
        return rpaTimeout;
    }

    public enum SuspendReason {
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

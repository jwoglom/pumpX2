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
     * (Tandem's pump-logs JSON calls this property preSuspendState)
     */
    public long getPreSuspendState() {
        return preSuspendState;
    }

    /**
     * @return an insulin reservoir quantity, in whole units, at the time pumping was suspended.
     * This is NOT the IOB: the value is byte-identical in the paired suspended and resumed
     * records which bracket a suspension, while the IOB the pump reports in LID_DAILY_BASAL
     * over the same window decays.
     *
     * TODO: determine whether this is the insulin remaining in the reservoir or the current
     * cartridge fill level. Observed captures support both readings and the question is
     * unresolved: one suspend/resume pair drops 120 -> 105 across a 15 minute suspension
     * (consistent with remaining volume being consumed by a tubing prime), while another
     * capture reads 180 at a suspend roughly ten hours after a 180 unit cartridge fill
     * (which remaining volume should have decremented by then).
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

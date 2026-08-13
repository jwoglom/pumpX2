package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 12,
    displayName = "Pumping Resumed",
    internalName = "LID_PUMPING_RESUMED",
    usedByTidepool = true
)
public class PumpingResumedHistoryLog extends HistoryLog {

    private long preResumeState;
    private int insulinAmount;

    public PumpingResumedHistoryLog() {}
    public PumpingResumedHistoryLog(long pumpTimeSec, long sequenceNum, long preResumeState, int insulinAmount) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, preResumeState, insulinAmount);
        this.preResumeState = preResumeState;
        this.insulinAmount = insulinAmount;

    }

    public PumpingResumedHistoryLog(long preResumeState, int insulinAmount) {
        this(0, 0, preResumeState, insulinAmount);
    }

    public int typeId() {
        return 12;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.preResumeState = Bytes.readUint32(raw, 10);
        this.insulinAmount = Bytes.readShort(raw, 14);

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long preResumeState, int insulinAmount) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{12, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(preResumeState),
            Bytes.firstTwoBytesLittleEndian(insulinAmount)));
    }

    /**
     * @return the state of the pump immediately before pumping was resumed
     * (Tandem's pump-logs JSON calls this property preResumeState)
     */
    public long getPreResumeState() {
        return preResumeState;
    }

    /**
     * @return an insulin reservoir quantity, in whole units, at the time pumping was resumed.
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

}

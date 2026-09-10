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
            HistoryLog.typeIdBytes(12, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(preResumeState),
            Bytes.firstTwoBytesLittleEndian(insulinAmount)));
    }

    /**
     * @return the state of the pump immediately before pumping was resumed
     * (Tandem's pump-logs JSON calls this property preResumeState).
     * Observed as 100 in every record captured so far, across both t:slim X2 and Mobi;
     * no other value has been seen.
     */
    public long getPreResumeState() {
        return preResumeState;
    }

    /**
     * @return the insulin remaining in the reservoir, in whole units, as the pump reports it at
     * the instant pumping was resumed. This is the same figure as
     * {@code InsulinStatusResponse.currentInsulinAmount}: in a Mobi capture the value was
     * byte-identical to that response polled within two seconds in 4 of 4 resumes (and in 4 of 4
     * suspends for the paired {@link PumpingSuspendedHistoryLog}).
     *
     * It is the remaining amount, not the cartridge fill level: after a tubing fill on the same
     * cartridge the resume read 90 where the paired suspend had read 110 (the prime consumed
     * insulin), and after a cartridge change the resume read 185 (equal to
     * {@code CartridgeFilledHistoryLog.insulinDisplay} logged in the same second) where the
     * paired suspend had read 8.
     *
     * It is also NOT the IOB: across a suspension with no fill the value is identical in the
     * paired suspended and resumed records, while the IOB the pump reports in LID_DAILY_BASAL
     * over the same window decays.
     */
    public int getInsulinAmount() {
        return insulinAmount;
    }

}

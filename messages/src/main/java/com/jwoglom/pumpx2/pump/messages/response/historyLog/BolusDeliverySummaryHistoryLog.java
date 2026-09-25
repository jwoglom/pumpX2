package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * Written as each bolus completes, just before {@link BolusIobEntryHistoryLog} (opcode 195), on
 * t:slim X2 and Mobi. Tandem publishes no name for opcode 194, so this class name is provisional,
 * and every payload field is exposed raw: the relations below are exact, but what the pump uses
 * them for is not known. Bytes 12-13 were zero in every record observed.
 * See https://github.com/jwoglom/pumpX2/issues/144.
 */
@HistoryLogProps(
    opCode = 194,
    displayName = "Bolus Delivery Summary"
)
public class BolusDeliverySummaryHistoryLog extends HistoryLog {

    private int unknownU8At10;
    private int unknownU8At11;
    private float unknownFloatAt14;
    private float unknownFloatAt18;
    private float unknownFloatAt22;

    public BolusDeliverySummaryHistoryLog() {}

    public BolusDeliverySummaryHistoryLog(long pumpTimeSec, long sequenceNum, int unknownU8At10, int unknownU8At11, float unknownFloatAt14, float unknownFloatAt18, float unknownFloatAt22) {
        this(pumpTimeSec, sequenceNum, unknownU8At10, unknownU8At11, unknownFloatAt14, unknownFloatAt18, unknownFloatAt22, 0);
    }

    public BolusDeliverySummaryHistoryLog(long pumpTimeSec, long sequenceNum, int unknownU8At10, int unknownU8At11, float unknownFloatAt14, float unknownFloatAt18, float unknownFloatAt22, int headerHighNibble) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, unknownU8At10, unknownU8At11, unknownFloatAt14, unknownFloatAt18, unknownFloatAt22, headerHighNibble);
        this.unknownU8At10 = unknownU8At10;
        this.unknownU8At11 = unknownU8At11;
        this.unknownFloatAt14 = unknownFloatAt14;
        this.unknownFloatAt18 = unknownFloatAt18;
        this.unknownFloatAt22 = unknownFloatAt22;
    }

    public int typeId() {
        return 194;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.unknownU8At10 = raw[10] & 0xFF;
        this.unknownU8At11 = raw[11] & 0xFF;
        this.unknownFloatAt14 = Bytes.readFloat(raw, 14);
        this.unknownFloatAt18 = Bytes.readFloat(raw, 18);
        this.unknownFloatAt22 = Bytes.readFloat(raw, 22);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int unknownU8At10, int unknownU8At11, float unknownFloatAt14, float unknownFloatAt18, float unknownFloatAt22, int headerHighNibble) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(194, headerHighNibble),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{ (byte) unknownU8At10, (byte) unknownU8At11, 0, 0 },
            Bytes.toFloat(unknownFloatAt14),
            Bytes.toFloat(unknownFloatAt18),
            Bytes.toFloat(unknownFloatAt22)));
    }

    /**
     * @return byte 10: the number of boluses, this one included, whose 194 records fall within
     * roughly the last 60 minutes (the records they were summed from for
     * {@link #getUnknownFloatAt22()}). The exact window edge could not be reproduced; records
     * 60-61 minutes apart are sometimes in, sometimes out.
     */
    public int getUnknownU8At10() {
        return unknownU8At10;
    }

    /**
     * @return byte 11: increments by one per bolus and wraps from 29 to 0
     */
    public int getUnknownU8At11() {
        return unknownU8At11;
    }

    /**
     * @return ten times {@link BolusCompletedHistoryLog#getInsulinDelivered()} for this bolus
     * (1.5 for a 0.15 U bolus), including partial and zero deliveries
     */
    public float getUnknownFloatAt14() {
        return unknownFloatAt14;
    }

    /**
     * @return equal to {@link #getUnknownFloatAt14()} in every record observed
     */
    public float getUnknownFloatAt18() {
        return unknownFloatAt18;
    }

    /**
     * @return the sum of {@link #getUnknownFloatAt14()} over the boluses counted in
     * {@link #getUnknownU8At10()}
     */
    public float getUnknownFloatAt22() {
        return unknownFloatAt22;
    }
}

package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.request.control.SetMaxBolusLimitRequest;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.GlobalMaxBolusSettingsResponse;

/**
 * Written when the maximum bolus is set, including when it is set to its current value. Every
 * {@link SetMaxBolusLimitRequest} answered with status 0 while records were being captured (17 of
 * 17, two Mobi pumps) produced one within seconds, carrying the requested value. The previous value
 * equalled the prior record's new value in 17 of 18 consecutive pairs (three Mobi pumps) and the
 * {@link GlobalMaxBolusSettingsResponse#getMaxBolus()} read before the change. Also written on the
 * t:slim X2.
 *
 * The class name is provisional: opcode 326 is not in Tandem's cloud event schema or the Mobi
 * app's history log list.
 *
 * @see SetMaxBolusLimitRequest
 * @see GlobalMaxBolusSettingsResponse
 */
@HistoryLogProps(
    opCode = 326,
    displayName = "Max Bolus Limit Set"
)
public class MaxBolusLimitSetHistoryLog extends HistoryLog {

    private int maxBolusMilliunits;
    private int previousMaxBolusMilliunits;

    public MaxBolusLimitSetHistoryLog() {}
    public MaxBolusLimitSetHistoryLog(long pumpTimeSec, long sequenceNum, int maxBolusMilliunits, int previousMaxBolusMilliunits) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, maxBolusMilliunits, previousMaxBolusMilliunits);
        this.maxBolusMilliunits = maxBolusMilliunits;
        this.previousMaxBolusMilliunits = previousMaxBolusMilliunits;
    }

    public int typeId() {
        return 326;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.maxBolusMilliunits = Bytes.readShort(raw, 10);
        this.previousMaxBolusMilliunits = Bytes.readShort(raw, 12);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int maxBolusMilliunits, int previousMaxBolusMilliunits) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(326, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(maxBolusMilliunits),
            Bytes.firstTwoBytesLittleEndian(previousMaxBolusMilliunits)));
    }

    /**
     * @return the new maximum bolus, in milliunits
     */
    public int getMaxBolusMilliunits() {
        return maxBolusMilliunits;
    }

    /**
     * @return the maximum bolus before this change, in milliunits
     */
    public int getPreviousMaxBolusMilliunits() {
        return previousMaxBolusMilliunits;
    }
}

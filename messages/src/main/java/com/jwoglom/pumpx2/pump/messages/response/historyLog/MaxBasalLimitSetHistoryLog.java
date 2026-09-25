package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.request.control.SetMaxBasalLimitRequest;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.BasalLimitSettingsResponse;

/**
 * Written when the maximum hourly basal limit is set, including when it is set to its current
 * value. Every {@link SetMaxBasalLimitRequest} answered with status 0 while records were being
 * captured (17 of 17, two Mobi pumps) produced one within seconds, carrying the requested value.
 * The previous value equalled the prior record's new value in 19 of 20 consecutive pairs (three
 * Mobi pumps) and the {@link BasalLimitSettingsResponse#getBasalLimit()} read before the change.
 * Also written on the t:slim X2.
 *
 * The class name is provisional: opcode 274 is not in Tandem's cloud event schema or the Mobi
 * app's history log list.
 *
 * @see SetMaxBasalLimitRequest
 * @see BasalLimitSettingsResponse
 */
@HistoryLogProps(
    opCode = 274,
    displayName = "Max Basal Limit Set"
)
public class MaxBasalLimitSetHistoryLog extends HistoryLog {

    private long maxHourlyBasalMilliunits;
    private long previousMaxHourlyBasalMilliunits;

    public MaxBasalLimitSetHistoryLog() {}
    public MaxBasalLimitSetHistoryLog(long pumpTimeSec, long sequenceNum, long maxHourlyBasalMilliunits, long previousMaxHourlyBasalMilliunits) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, maxHourlyBasalMilliunits, previousMaxHourlyBasalMilliunits);
        this.maxHourlyBasalMilliunits = maxHourlyBasalMilliunits;
        this.previousMaxHourlyBasalMilliunits = previousMaxHourlyBasalMilliunits;
    }

    public int typeId() {
        return 274;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.maxHourlyBasalMilliunits = Bytes.readUint32(raw, 10);
        this.previousMaxHourlyBasalMilliunits = Bytes.readUint32(raw, 14);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long maxHourlyBasalMilliunits, long previousMaxHourlyBasalMilliunits) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(274, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(maxHourlyBasalMilliunits),
            Bytes.toUint32(previousMaxHourlyBasalMilliunits)));
    }

    /**
     * @return the new maximum hourly basal, in milliunits per hour
     */
    public long getMaxHourlyBasalMilliunits() {
        return maxHourlyBasalMilliunits;
    }

    /**
     * @return the maximum hourly basal before this change, in milliunits per hour
     */
    public long getPreviousMaxHourlyBasalMilliunits() {
        return previousMaxHourlyBasalMilliunits;
    }
}

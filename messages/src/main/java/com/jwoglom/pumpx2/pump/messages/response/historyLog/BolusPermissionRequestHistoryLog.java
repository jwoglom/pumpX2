package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * Written for each signed BolusPermissionRequest a BLE app sends, immediately followed by a
 * {@link BolusPermissionResponseHistoryLog}. Tandem publishes no name for opcode 338, so this class
 * name is provisional. Bytes 14-25 were zero in every record observed.
 * See https://github.com/jwoglom/pumpX2/issues/142.
 */
@HistoryLogProps(
    opCode = 338,
    displayName = "Bolus Permission Request"
)
public class BolusPermissionRequestHistoryLog extends HistoryLog {

    private long requestTimestamp;

    public BolusPermissionRequestHistoryLog() {}

    public BolusPermissionRequestHistoryLog(long pumpTimeSec, long sequenceNum, long requestTimestamp) {
        this(pumpTimeSec, sequenceNum, requestTimestamp, 0);
    }

    public BolusPermissionRequestHistoryLog(long pumpTimeSec, long sequenceNum, long requestTimestamp, int headerHighNibble) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, requestTimestamp, headerHighNibble);
        this.requestTimestamp = requestTimestamp;
    }

    public int typeId() {
        return 338;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.requestTimestamp = Bytes.readUint32(raw, 10);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long requestTimestamp, int headerHighNibble) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(338, headerHighNibble),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(requestTimestamp)));
    }

    /**
     * @return the 32-bit time value the app placed in the signed request's authentication trailer
     * (the value {@link com.jwoglom.pumpx2.pump.messages.Packetize} takes from
     * {@code PumpStateSupplier.pumpTimeSinceReset}), copied as sent. It is the app's clock, not
     * the pump's: requests from TandemKit carried the app's last-read
     * TimeSinceResetResponse.pumpTimeSinceReset, so the value lags the record time by however long
     * ago that was, while Tandem's app sent a value on the seconds-since-2008 pump-clock scale
     * (equal to the last TimeSinceResetResponse.currentTime, or that plus a whole number of hours).
     */
    public long getRequestTimestamp() {
        return requestTimestamp;
    }
}

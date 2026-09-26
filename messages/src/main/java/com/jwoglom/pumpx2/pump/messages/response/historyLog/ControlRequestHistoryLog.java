package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * Written once for each signed request the pump receives on the CONTROL characteristic.
 *
 * <p>The class name is provisional: Tandem's cloud export and the Mobi app's HistoryLogType enum
 * do not name this opcode.
 *
 * <p>In Trio/TandemKit logs these records match the {@code Writing message} lines one to one in
 * both directions: every record had a request with the same opcode within 30 seconds (3891/3891
 * records from the maintainer's Mobi, 1358/1360 from a tester's Mobi), and every such request
 * written while the pump's history was being fetched produced one. The pump writes the record
 * after the effect of the command, so a stop/set temp rate cycle reads TempRateCompleted, this
 * record (StopTempRate), {@link ControlResponseHistoryLog}, TempRateActivated, this record
 * (SetTempRate), {@link SetTempRateResponseHistoryLog}.
 *
 * <p>Bolus-flow requests (BolusPermission, InitiateBolus, CancelBolus, BolusPermissionRelease,
 * RemoteBgEntry, RemoteCarbEntry) do not produce this record; their trailer time shows up in
 * other, bolus-specific records instead.
 *
 * <p>Bytes 15-25 were zero in all 7447 records examined. See
 * https://github.com/jwoglom/pumpx2/issues/145.
 */
@HistoryLogProps(
    opCode = 290,
    displayName = "Control Request",
    internalName = ""
)
public class ControlRequestHistoryLog extends HistoryLog {

    private long requestTimestamp;
    private int requestOpCode;

    public ControlRequestHistoryLog() {}
    public ControlRequestHistoryLog(long pumpTimeSec, long sequenceNum, long requestTimestamp, int requestOpCode) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, requestTimestamp, requestOpCode);
        this.requestTimestamp = requestTimestamp;
        this.requestOpCode = requestOpCode;
    }

    public ControlRequestHistoryLog(long requestTimestamp, int requestOpCode) {
        this(0, 0, requestTimestamp, requestOpCode);
    }

    public int typeId() {
        return 290;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.requestTimestamp = Bytes.readUint32(raw, 10);
        this.requestOpCode = raw[14] & 0xFF;
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long requestTimestamp, int requestOpCode) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(290, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(requestTimestamp),
            new byte[]{ (byte) requestOpCode }));
    }

    /**
     * The 4-byte time value carried in the signed request's 24-byte trailer (the 4 bytes before
     * the HMAC), copied as the app sent it. This is not a pump clock reading.
     *
     * <p>pumpX2 and TandemKit fill that field with {@code PumpStateSupplier.pumpTimeSinceReset},
     * so with them it equals the most recent {@code TimeSinceResetResponse.pumpTimeSinceReset}
     * the app had received (3889/3891 and 1358/1360 records on two Mobi pumps; the misses fall in
     * gaps of the app log). That is why it can lag the record's own time by minutes. Tandem's
     * official app fills it with the phone's UTC time in seconds since 2008-01-01, so there it
     * differs from the record's pump-local time by the time-zone offset.
     *
     * <p>In the official-app BLE captures the pair (this value, {@link #getRequestOpCode()})
     * equalled the trailer time and opcode of a captured signed CONTROL request for 482 records
     * on six pumps.
     *
     * @return the request's signed trailer time value
     */
    public long getRequestTimestamp() {
        return requestTimestamp;
    }

    /**
     * The request's opcode as an unsigned byte, i.e. the pumpX2 {@code opCode} of the request
     * message ANDed with 0xFF: for example 164 is SetTempRateRequest (opCode -92), 166 is
     * StopTempRateRequest (-90), 184 is DismissNotificationRequest (-72), 148 is
     * EnterFillTubingModeRequest (-108) and 132 is UserInteractionRequest (-124).
     *
     * @return the unsigned opcode of the signed CONTROL request that was received
     */
    public int getRequestOpCode() {
        return requestOpCode;
    }
}

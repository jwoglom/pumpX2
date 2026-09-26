package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.util.Arrays;

/**
 * The response the pump sent to a signed CONTROL request, written immediately after the
 * {@link ControlRequestHistoryLog} for that request (its {@code requestOpCode} is this record's
 * {@code responseOpCode} minus one in 3692/3692 records across both capture sets).
 *
 * <p>The class name is provisional: Tandem's cloud export and the Mobi app's HistoryLogType enum
 * do not name this opcode.
 *
 * <p>Only some commands are answered with this record. Observed response opcodes: 133
 * UserInteraction, 135 SetMaxBolusLimit, 137 SetMaxBasalLimit, 155 ResumePumping, 157
 * SuspendPumping, 167 StopTempRate, 195 CgmHighLowAlert, 197 CgmRiseFallAlert, 199
 * CgmOutOfRangeAlert, 211 SetQuickBolusSettings, 213 SetPumpAlertSnooze, 215 ChangeTimeDate, 217
 * SetBgReminder, 221 SetSiteChangeReminder, 223 SetLowInsulinAlert, 225 SetAutoOffAlert, 229
 * SetPumpSounds, 237 SetActiveIDP and 245 PlaySound. SetTempRate is answered with
 * {@link SetTempRateResponseHistoryLog} instead; the cartridge/fill commands and
 * DismissNotification use other opcodes as well.
 *
 * <p>Byte 11 and bytes 13-25 are zero in every Mobi record examined; they are kept as raw
 * accessors. See https://github.com/jwoglom/pumpx2/issues/145.
 */
@HistoryLogProps(
    opCode = 300,
    displayName = "Control Response",
    internalName = ""
)
public class ControlResponseHistoryLog extends HistoryLog {

    private int responseOpCode;
    private int unknown11;
    private int status;
    private byte[] unknownTail;

    public ControlResponseHistoryLog() {}
    public ControlResponseHistoryLog(long pumpTimeSec, long sequenceNum, int responseOpCode, int unknown11, int status, byte[] unknownTail) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, responseOpCode, unknown11, status, unknownTail);
        this.responseOpCode = responseOpCode;
        this.unknown11 = unknown11;
        this.status = status;
        this.unknownTail = Arrays.copyOfRange(this.cargo, 13, 26);
    }

    public ControlResponseHistoryLog(long pumpTimeSec, long sequenceNum, int responseOpCode, int status) {
        this(pumpTimeSec, sequenceNum, responseOpCode, 0, status, new byte[13]);
    }

    public ControlResponseHistoryLog(int responseOpCode, int status) {
        this(0, 0, responseOpCode, status);
    }

    public int typeId() {
        return 300;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.responseOpCode = raw[10] & 0xFF;
        this.unknown11 = raw[11] & 0xFF;
        this.status = raw[12] & 0xFF;
        this.unknownTail = Arrays.copyOfRange(raw, 13, 26);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int responseOpCode, int unknown11, int status, byte[] unknownTail) {
        Validate.isTrue(unknownTail.length == 13, "unknownTail must be 13 bytes");
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(300, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{ (byte) responseOpCode, (byte) unknown11, (byte) status },
            unknownTail));
    }

    /**
     * The opcode of the response the pump sent, as an unsigned byte (the pumpX2 response
     * {@code opCode} ANDed with 0xFF), which is the request's opcode plus one: for example 167
     * is StopTempRateResponse and 155 is ResumePumpingResponse.
     *
     * @return the unsigned opcode of the response
     */
    public int getResponseOpCode() {
        return responseOpCode;
    }

    /**
     * The status byte of that response: 0 on success. Every nonzero value checked against the
     * app log matched a response that reported a failure (for example a ResumePumping request
     * rejected with status 1 and retried successfully a second later); StopTempRate responses
     * logged as successful all read 0 here (1734/1734).
     *
     * @return the response status, 0 for success
     */
    public int getStatus() {
        return status;
    }

    /**
     * @return byte 11, which was 0 in every record examined
     */
    public int getUnknown11() {
        return unknown11;
    }

    /**
     * Bytes 13-25. They are zero on the Mobi. In 17 t:slim X2 records answering
     * UserInteraction (response opcode 133), bytes 15-17 of the record (index 2-4 here) held the
     * low 24 bits of the record's own pumpTimeSec; their purpose is unknown.
     *
     * @return a copy of bytes 13-25
     */
    public byte[] getUnknownTail() {
        return Arrays.copyOf(unknownTail, unknownTail.length);
    }
}

package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.util.Arrays;

/**
 * Written when the pump answers a remote cartridge-change or fill command. Seen on the Tandem Mobi.
 *
 * <p>Tandem's export and its Mobi app do not name this opcode, so the class name is provisional.
 * Byte 10 is the opcode of the response the pump sent and byte 12 its status. The five values seen
 * are the CONTROL-characteristic responses {@code EnterChangeCartridgeModeResponse} (145),
 * {@code ExitChangeCartridgeModeResponse} (147), {@code EnterFillTubingModeResponse} (149),
 * {@code ExitFillTubingModeResponse} (151) and {@code FillCannulaResponse} (153). They look like
 * wizard step codes only because a site change sends those commands in that order.
 *
 * <p>Each record is written immediately after the history record of the matching request (opcode
 * 290), whose request opcode is this response opcode minus 1: 192/194 records across TandemKit logs
 * and BLE captures, the 2 exceptions sitting at a capture gap. In the maintainer's TandemKit logs
 * the count equals the number of these requests sent (31 = 4 + 4 + 9 + 9 + 5).
 * See https://github.com/jwoglom/pumpx2/issues/148.
 */
@HistoryLogProps(
    opCode = 359,
    displayName = "Cartridge/Fill Response",
    internalName = ""
)
public class CartridgeFillResponseHistoryLog extends HistoryLog {

    private int responseOpCode;
    private int unknown11;
    private int status;
    private byte[] unknownTail;

    public CartridgeFillResponseHistoryLog() {}
    public CartridgeFillResponseHistoryLog(long pumpTimeSec, long sequenceNum, int responseOpCode, int unknown11, int status, byte[] unknownTail) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, responseOpCode, unknown11, status, unknownTail);
        this.responseOpCode = responseOpCode;
        this.unknown11 = unknown11;
        this.status = status;
        this.unknownTail = Arrays.copyOf(unknownTail, 13);
    }

    public CartridgeFillResponseHistoryLog(long pumpTimeSec, long sequenceNum, int responseOpCode, int status) {
        this(pumpTimeSec, sequenceNum, responseOpCode, 0, status, new byte[13]);
    }

    public int typeId() {
        return 359;
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
            HistoryLog.typeIdBytes(359, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{ (byte) responseOpCode, (byte) unknown11, (byte) status },
            unknownTail));
    }

    /**
     * The unsigned opcode of the CONTROL-characteristic response the pump sent (pumpX2's signed
     * {@code opCode} & 0xFF): 145, 147, 149, 151 or 153 in every observed record.
     */
    public int getResponseOpCode() {
        return responseOpCode;
    }

    /**
     * The response's status byte: 0 for success.
     *
     * <p>In one maintainer capture the app sent ExitChangeCartridgeMode twice; the pump answered
     * status 0 then status 1, and this record reads 1 in the same second as the failed response.
     * Every response of these types in the TandemKit logs (34) had status 0, as did the matching
     * records.
     */
    public int getStatus() {
        return status;
    }

    /**
     * Byte 11, zero in every observed record (194/194).
     */
    public int getUnknown11() {
        return unknown11;
    }

    /**
     * Bytes 13-25, all zero in every observed record (194/194).
     */
    public byte[] getUnknownTail() {
        return unknownTail;
    }
}

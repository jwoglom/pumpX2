package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.util.Arrays;

/**
 * The response the pump sent to a DismissNotificationRequest (an alert, alarm or reminder
 * acknowledgement sent over Bluetooth), written immediately after the request's history log
 * (opcode 290, requestOpCode 184) in 591/591 records across the Trio/TandemKit logs and the
 * official-app captures. It has the same layout as the generic control-response record (opcode
 * 300, jwoglom/pumpx2#145); the pump logs DismissNotification answers under their own opcode.
 *
 * <p>In the Trio/TandemKit logs there is exactly one of these per DismissNotificationRequest the
 * app sent (348/348 and 102/102 on two Mobi pumps). {@code responseOpCode} is 185
 * (DismissNotificationResponse) in every record.
 *
 * <p>The class name is provisional: Tandem's cloud export and the Mobi app's HistoryLogType enum
 * do not name this opcode. See https://github.com/jwoglom/pumpx2/issues/147.
 */
@HistoryLogProps(
    opCode = 334,
    displayName = "Dismiss Notification Response",
    internalName = ""
)
public class DismissNotificationResponseHistoryLog extends HistoryLog {

    private int responseOpCode;
    private int unknown11;
    private int status;
    private byte[] unknownTail;

    public DismissNotificationResponseHistoryLog() {}
    public DismissNotificationResponseHistoryLog(long pumpTimeSec, long sequenceNum, int responseOpCode, int unknown11, int status, byte[] unknownTail) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, responseOpCode, unknown11, status, unknownTail);
        this.responseOpCode = responseOpCode;
        this.unknown11 = unknown11;
        this.status = status;
        this.unknownTail = Arrays.copyOfRange(this.cargo, 13, 26);
    }

    public DismissNotificationResponseHistoryLog(long pumpTimeSec, long sequenceNum, int responseOpCode, int status) {
        this(pumpTimeSec, sequenceNum, responseOpCode, 0, status, new byte[13]);
    }

    public DismissNotificationResponseHistoryLog(int responseOpCode, int status) {
        this(0, 0, responseOpCode, status);
    }

    public int typeId() {
        return 334;
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
            HistoryLog.typeIdBytes(334, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{ (byte) responseOpCode, (byte) unknown11, (byte) status },
            unknownTail));
    }

    /**
     * The opcode of the response the pump sent, as an unsigned byte (the pumpX2 response
     * {@code opCode} ANDed with 0xFF). Always 185, DismissNotificationResponse, in the records
     * examined (591 records on four pumps).
     *
     * @return the unsigned opcode of the response
     */
    public int getResponseOpCode() {
        return responseOpCode;
    }

    /**
     * The status byte of the DismissNotificationResponse: 0 on success. Every nonzero value that
     * could be checked against live traffic (four records on three pumps) matched a
     * DismissNotificationResponse reporting status 1, e.g. the second of two alarm dismissals the
     * app sent in the same second.
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
     * @return a copy of bytes 13-25, which were zero in every record examined
     */
    public byte[] getUnknownTail() {
        return Arrays.copyOf(unknownTail, unknownTail.length);
    }
}

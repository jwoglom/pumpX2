package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * The pump's response to a SetTempRateRequest, written immediately after the
 * {@link ControlRequestHistoryLog} for that request (request opcode 164). When the temp rate is
 * accepted, the {@link TempRateActivatedHistoryLog} it started comes two records earlier.
 *
 * <p>The class name is provisional: Tandem's cloud export and the Mobi app's HistoryLogType enum
 * do not name this opcode. Only seen on the Mobi, where the phone can set temp rates.
 *
 * <p>In Trio/TandemKit logs the three fields equalled the status, byte 3 and tempRateId of the
 * logged SetTempRateResponse in every record that had one (1738/1738 and 614/614 on two Mobi
 * pumps). Across all captures, status 0 records carried the tempRateId of the TempRateActivated
 * written two records earlier in 2751/2751 cases; the 7 status 1 records had tempRateId 0, byte
 * 11 set to 1 and no TempRateActivated. Bytes 14-25 were zero in all 2758 records. See
 * https://github.com/jwoglom/pumpx2/issues/145.
 */
@HistoryLogProps(
    opCode = 309,
    displayName = "Set Temp Rate Response",
    internalName = ""
)
public class SetTempRateResponseHistoryLog extends HistoryLog {

    private int status;
    private int unknown11;
    private int tempRateId;

    public SetTempRateResponseHistoryLog() {}
    public SetTempRateResponseHistoryLog(long pumpTimeSec, long sequenceNum, int status, int unknown11, int tempRateId) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, status, unknown11, tempRateId);
        this.status = status;
        this.unknown11 = unknown11;
        this.tempRateId = tempRateId;
    }

    public SetTempRateResponseHistoryLog(int status, int unknown11, int tempRateId) {
        this(0, 0, status, unknown11, tempRateId);
    }

    public int typeId() {
        return 309;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.status = raw[10] & 0xFF;
        this.unknown11 = raw[11] & 0xFF;
        this.tempRateId = Bytes.readShort(raw, 12);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int status, int unknown11, int tempRateId) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(309, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{ (byte) status, (byte) unknown11 },
            Bytes.firstTwoBytesLittleEndian(tempRateId)));
    }

    /**
     * @return the SetTempRateResponse status: 0 when the temp rate was started, 1 when it was
     *         rejected
     */
    public int getStatus() {
        return status;
    }

    /**
     * Byte 3 of the SetTempRateResponse cargo, which pumpX2 does not parse. It was 0 whenever
     * the status was 0 and 1 in all 7 rejected requests observed; its meaning is unknown.
     *
     * @return byte 11
     */
    public int getUnknown11() {
        return unknown11;
    }

    /**
     * @return the id of the temp rate that was started (see
     *         {@link TempRateActivatedHistoryLog#getTempRateId()}), or 0 when the request was
     *         rejected
     */
    public int getTempRateId() {
        return tempRateId;
    }
}

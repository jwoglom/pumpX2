package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * Written as a bolus completes, recording the insulin the pump adds to its IOB for that bolus.
 * Tandem publishes no name for opcode 195, so this class name is provisional. Bytes 12-13 were
 * zero in every record observed. See https://github.com/jwoglom/pumpX2/issues/143.
 */
@HistoryLogProps(
    opCode = 195,
    displayName = "Bolus IOB Entry"
)
public class BolusIobEntryHistoryLog extends HistoryLog {

    private int unknownU8At10;
    private int unknownU8At11;
    private long insulinDurationMillis;
    private float insulinDelivered;
    private float mudaliarIob;

    public BolusIobEntryHistoryLog() {}

    public BolusIobEntryHistoryLog(long pumpTimeSec, long sequenceNum, int unknownU8At10, int unknownU8At11, long insulinDurationMillis, float insulinDelivered, float mudaliarIob) {
        this(pumpTimeSec, sequenceNum, unknownU8At10, unknownU8At11, insulinDurationMillis, insulinDelivered, mudaliarIob, 0);
    }

    public BolusIobEntryHistoryLog(long pumpTimeSec, long sequenceNum, int unknownU8At10, int unknownU8At11, long insulinDurationMillis, float insulinDelivered, float mudaliarIob, int headerHighNibble) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, unknownU8At10, unknownU8At11, insulinDurationMillis, insulinDelivered, mudaliarIob, headerHighNibble);
        this.unknownU8At10 = unknownU8At10;
        this.unknownU8At11 = unknownU8At11;
        this.insulinDurationMillis = insulinDurationMillis;
        this.insulinDelivered = insulinDelivered;
        this.mudaliarIob = mudaliarIob;
    }

    public int typeId() {
        return 195;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.unknownU8At10 = raw[10] & 0xFF;
        this.unknownU8At11 = raw[11] & 0xFF;
        this.insulinDurationMillis = Bytes.readUint32(raw, 14);
        this.insulinDelivered = Bytes.readFloat(raw, 18);
        this.mudaliarIob = Bytes.readFloat(raw, 22);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int unknownU8At10, int unknownU8At11, long insulinDurationMillis, float insulinDelivered, float mudaliarIob, int headerHighNibble) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(195, headerHighNibble),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{ (byte) unknownU8At10, (byte) unknownU8At11, 0, 0 },
            Bytes.toUint32(insulinDurationMillis),
            Bytes.toFloat(insulinDelivered),
            Bytes.toFloat(mudaliarIob)));
    }

    /**
     * @return byte 10. 0 when insulin was delivered; 1 in the records for boluses that delivered
     * nothing, which also carry 0xFF in byte 11, 0xFFFFFFFF as the duration and zero floats.
     */
    public int getUnknownU8At10() {
        return unknownU8At10;
    }

    /**
     * @return byte 11. Unconfirmed, but it behaves like a slot index into the pump's table of
     * boluses still on board: values stay small, and in about 97% of records it is the lowest
     * index not taken by a bolus completed less than {@link #getInsulinDurationMillis()} earlier.
     * 0xFF when nothing was delivered.
     */
    public int getUnknownU8At11() {
        return unknownU8At11;
    }

    /**
     * @return the insulin duration applied to this bolus, in milliseconds. It matched the active
     * profile's IDP insulinDuration setting (4, 5, 6 and 8 hours seen). 0xFFFFFFFF when nothing
     * was delivered.
     */
    public long getInsulinDurationMillis() {
        return insulinDurationMillis;
    }

    /**
     * @return units delivered; equal to {@link BolusCompletedHistoryLog#getInsulinDelivered()} bit
     * for bit
     */
    public float getInsulinDelivered() {
        return insulinDelivered;
    }

    /**
     * @return Mudaliar IOB in units after adding this bolus; equal to
     * {@link IobSnapshotPostBolusHistoryLog#getMudaliarIob()} bit for bit. This is the pump's
     * displayed IOB only when it displays the Mudaliar model (selectedIob 0).
     */
    public float getMudaliarIob() {
        return mudaliarIob;
    }
}

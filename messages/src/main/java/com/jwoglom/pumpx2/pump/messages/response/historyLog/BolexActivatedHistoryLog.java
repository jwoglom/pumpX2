package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 59,
    displayName = "Extended Bolus Activated",
    internalName = "LID_BOLEX_ACTIVATED",
    usedByTidepool = true
)
public class BolexActivatedHistoryLog extends HistoryLog {

    private int bolusId;
    private int selectedIob;
    private float iob;
    private float bolexSize;

    public BolexActivatedHistoryLog() {}

    public BolexActivatedHistoryLog(long pumpTimeSec, long sequenceNum, int bolusId, int selectedIob, float iob, float bolexSize) {
        this(pumpTimeSec, sequenceNum, bolusId, selectedIob, iob, bolexSize, 0);
    }

    public BolexActivatedHistoryLog(long pumpTimeSec, long sequenceNum, int bolusId, int selectedIob, float iob, float bolexSize, int headerHighNibble) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, bolusId, selectedIob, iob, bolexSize, headerHighNibble);
        this.bolusId = bolusId;
        this.selectedIob = selectedIob;
        this.iob = iob;
        this.bolexSize = bolexSize;
    }

    /**
     * @deprecated byte 12 carries selectedIob, as in {@link BolusActivatedHistoryLog}. This
     * constructor assumes a selectedIob of 0.
     */
    @Deprecated
    public BolexActivatedHistoryLog(long pumpTimeSec, long sequenceNum, int bolusId, float iob, float bolexSize) {
        this(pumpTimeSec, sequenceNum, bolusId, 0, iob, bolexSize);
    }

    /**
     * @deprecated see {@link #BolexActivatedHistoryLog(long, long, int, float, float)}
     */
    @Deprecated
    public BolexActivatedHistoryLog(int bolusId, float iob, float bolexSize) {
        this(0, 0, bolusId, 0, iob, bolexSize);
    }

    public int typeId() {
        return 59;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.bolusId = Bytes.readShort(raw, 10);
        this.selectedIob = raw[12];
        this.iob = Bytes.readFloat(raw, 14);
        this.bolexSize = Bytes.readFloat(raw, 18);

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int bolusId, int selectedIob, float iob, float bolexSize) {
        return buildCargo(pumpTimeSec, sequenceNum, bolusId, selectedIob, iob, bolexSize, 0);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int bolusId, int selectedIob, float iob, float bolexSize, int headerHighNibble) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(59, headerHighNibble),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(bolusId),
            // bytes 12-13: selectedIob followed by one unused byte (0 in every known record)
            new byte[]{ (byte) selectedIob, 0 },
            Bytes.toFloat(iob),
            Bytes.toFloat(bolexSize)));
    }

    /**
     * @deprecated use {@link #buildCargo(long, long, int, int, float, float)}; this overload
     * writes a selectedIob of 0.
     */
    @Deprecated
    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int bolusId, float iob, float bolexSize) {
        return buildCargo(pumpTimeSec, sequenceNum, bolusId, 0, iob, bolexSize);
    }

    public int getBolusId() {
        return bolusId;
    }

    /**
     * @return the raw value of byte 12, the IOB algorithm in use; see {@link #getSelectedIobType()}.
     *
     * <p>Tandem's cloud schema gives opcode 59 the same fields as opcode 55
     * ({@link BolusActivatedHistoryLog}), which under the byte-reversed-word cloud mapping puts
     * selectedIob at BLE byte 12. On the wire, byte 12 equalled
     * {@link BolusRequestedMsg2HistoryLog#getSelectedIOB()} of the same bolus in 9/9 opcode 59
     * records from two Mobi pumps (both 0 and 1 seen), and byte 13 was always 0.
     * See https://github.com/jwoglom/pumpx2/issues/46.
     */
    public int getSelectedIob() {
        return selectedIob;
    }

    public BolusRequestedMsg2HistoryLog.SelectedIOBType getSelectedIobType() {
        return BolusRequestedMsg2HistoryLog.SelectedIOBType.fromId(selectedIob);
    }

    public float getIob() {
        return iob;
    }

    public float getBolexSize() {
        return bolexSize;
    }

}

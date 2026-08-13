package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 55,
    displayName = "Bolus Activated",
    internalName = "LID_BOLUS_ACTIVATED",
    usedByTidepool = true
)
public class BolusActivatedHistoryLog extends HistoryLog {

    private int bolusId;
    private int selectedIob;
    private float iob;
    private float bolusSize;

    public BolusActivatedHistoryLog() {}

    public BolusActivatedHistoryLog(long pumpTimeSec, long sequenceNum, int bolusId, int selectedIob, float iob, float bolusSize) {
        this(pumpTimeSec, sequenceNum, bolusId, selectedIob, iob, bolusSize, 0);
    }

    public BolusActivatedHistoryLog(long pumpTimeSec, long sequenceNum, int bolusId, int selectedIob, float iob, float bolusSize, int headerHighNibble) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, bolusId, selectedIob, iob, bolusSize, headerHighNibble);
        this.bolusId = bolusId;
        this.selectedIob = selectedIob;
        this.iob = iob;
        this.bolusSize = bolusSize;

    }

    /**
     * @deprecated bytes 12-13 of this log are not padding: byte 12 carries selectedIob. This
     * constructor assumes a selectedIob of 0, which does not round-trip a record where it is 1.
     */
    @Deprecated
    public BolusActivatedHistoryLog(long pumpTimeSec, long sequenceNum, int bolusId, float iob, float bolusSize) {
        this(pumpTimeSec, sequenceNum, bolusId, 0, iob, bolusSize);
    }

    /**
     * @deprecated see {@link #BolusActivatedHistoryLog(long, long, int, float, float)}
     */
    @Deprecated
    public BolusActivatedHistoryLog(int bolusId, float iob, float bolusSize) {
        this(0, 0, bolusId, 0, iob, bolusSize);
    }

    public int typeId() {
        return 55;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.bolusId = Bytes.readShort(raw, 10);
        this.selectedIob = raw[12];
        this.iob = Bytes.readFloat(raw, 14);
        this.bolusSize = Bytes.readFloat(raw, 18);

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int bolusId, int selectedIob, float iob, float bolusSize) {
        return buildCargo(pumpTimeSec, sequenceNum, bolusId, selectedIob, iob, bolusSize, 0);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int bolusId, int selectedIob, float iob, float bolusSize, int headerHighNibble) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(55, headerHighNibble),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(bolusId),
            // bytes 12-13: selectedIob followed by one unused byte, which is 0 in every known
            // record. These were previously omitted entirely, which shifted iob and bolusSize
            // 2 bytes earlier than parse() reads them.
            new byte[]{ (byte) selectedIob, 0 },
            Bytes.toFloat(iob),
            Bytes.toFloat(bolusSize)));
    }

    /**
     * @return the ID of the bolus operation
     */
    public int getBolusId() {
        return bolusId;
    }

    /**
     * @return the raw value of byte 12, which tracks the IOB algorithm in use.
     *
     * <p>This is an inference drawn from a small number of records, in which byte 12 co-varies
     * exactly with {@link BolusRequestedMsg2HistoryLog#getSelectedIOB()} for the same bolus. It
     * should be confirmed against further captures before being relied upon. The byte offsets
     * either side of it are independently confirmed.
     */
    public int getSelectedIob() {
        return selectedIob;
    }

    public BolusRequestedMsg2HistoryLog.SelectedIOBType getSelectedIobType() {
        return BolusRequestedMsg2HistoryLog.SelectedIOBType.fromId(selectedIob);
    }

    /**
     * @return current insulin on board
     */
    public float getIob() {
        return iob;
    }

    /**
     * @return the size of the bolus which was activated
     */
    public float getBolusSize() {
        return bolusSize;
    }

}

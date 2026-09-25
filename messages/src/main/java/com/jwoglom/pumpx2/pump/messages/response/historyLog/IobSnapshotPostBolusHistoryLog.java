package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * IOB snapshot written as a bolus completes, just before {@link BolusCompletedHistoryLog};
 * {@link IobSnapshotPreBolusHistoryLog} is its counterpart at initiation.
 *
 * <p>Both IOB models the pump tracks are recorded, whichever one it displays. On pumps that
 * answered ControlIQIOBRequest within 90 s of the record, {@link #getMudaliarIob()} equalled
 * mudaliarIOB and {@link #getSwan6hrIob()} equalled swan6hrIOB, to the milliunit. Tandem publishes
 * no name for opcode 207, so this class name is provisional.
 * See https://github.com/jwoglom/pumpX2/issues/143.
 */
@HistoryLogProps(
    opCode = 207,
    displayName = "IOB Snapshot After Bolus"
)
public class IobSnapshotPostBolusHistoryLog extends HistoryLog {

    private int bolusId;
    private int unknownU8At12;
    private int selectedIob;
    private float mudaliarIob;
    private float swan6hrIob;
    private float unknownFloatAt22;

    public IobSnapshotPostBolusHistoryLog() {}

    public IobSnapshotPostBolusHistoryLog(long pumpTimeSec, long sequenceNum, int bolusId, int unknownU8At12, int selectedIob, float mudaliarIob, float swan6hrIob, float unknownFloatAt22) {
        this(pumpTimeSec, sequenceNum, bolusId, unknownU8At12, selectedIob, mudaliarIob, swan6hrIob, unknownFloatAt22, 0);
    }

    public IobSnapshotPostBolusHistoryLog(long pumpTimeSec, long sequenceNum, int bolusId, int unknownU8At12, int selectedIob, float mudaliarIob, float swan6hrIob, float unknownFloatAt22, int headerHighNibble) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, bolusId, unknownU8At12, selectedIob, mudaliarIob, swan6hrIob, unknownFloatAt22, headerHighNibble);
        this.bolusId = bolusId;
        this.unknownU8At12 = unknownU8At12;
        this.selectedIob = selectedIob;
        this.mudaliarIob = mudaliarIob;
        this.swan6hrIob = swan6hrIob;
        this.unknownFloatAt22 = unknownFloatAt22;
    }

    public int typeId() {
        return 207;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.bolusId = Bytes.readShort(raw, 10);
        this.unknownU8At12 = raw[12] & 0xFF;
        this.selectedIob = raw[13] & 0xFF;
        this.mudaliarIob = Bytes.readFloat(raw, 14);
        this.swan6hrIob = Bytes.readFloat(raw, 18);
        this.unknownFloatAt22 = Bytes.readFloat(raw, 22);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int bolusId, int unknownU8At12, int selectedIob, float mudaliarIob, float swan6hrIob, float unknownFloatAt22, int headerHighNibble) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(207, headerHighNibble),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(bolusId),
            new byte[]{ (byte) unknownU8At12, (byte) selectedIob },
            Bytes.toFloat(mudaliarIob),
            Bytes.toFloat(swan6hrIob),
            Bytes.toFloat(unknownFloatAt22)));
    }

    public int getBolusId() {
        return bolusId;
    }

    /**
     * @return byte 12. Meaning unknown: 1 on every Control-IQ-off pump observed, 3 on most
     * records from pumps running Control-IQ, and occasionally 2 or 0.
     */
    public int getUnknownU8At12() {
        return unknownU8At12;
    }

    /**
     * @return the IOB model the pump displays: equal to
     * {@link BolusRequestedMsg2HistoryLog#getSelectedIOB()} and to byte 12 of
     * {@link BolusActivatedHistoryLog} for the same bolus in every record observed
     */
    public int getSelectedIob() {
        return selectedIob;
    }

    public BolusRequestedMsg2HistoryLog.SelectedIOBType getSelectedIobType() {
        return BolusRequestedMsg2HistoryLog.SelectedIOBType.fromId(selectedIob);
    }

    /**
     * @return IOB in units under the pump's Mudaliar model, whose decay follows the profile's
     * insulin duration. When {@link #getSelectedIob()} is 0 it equals
     * {@link BolusCompletedHistoryLog#getIob()} bit for bit.
     */
    public float getMudaliarIob() {
        return mudaliarIob;
    }

    /**
     * @return IOB in units under the pump's Swan 6-hour model (ControlIQIOBResponse.swan6hrIOB).
     * When {@link #getSelectedIob()} is 1 it equals
     * {@link BolusCompletedHistoryLog#getIob()} bit for bit.
     */
    public float getSwan6hrIob() {
        return swan6hrIob;
    }

    /**
     * @return a third IOB-like value in units, of unidentified model. It is usually below
     * {@link #getSwan6hrIob()} and decays faster. Like the Swan value, and unlike the Mudaliar
     * one, it usually rises by exactly the delivered amount between the pre- and post-bolus
     * snapshots of one bolus.
     */
    public float getUnknownFloatAt22() {
        return unknownFloatAt22;
    }
}

package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.BolusPermissionChangeReasonResponse;

/**
 * Written when the pump's bolus permission for a bolus id changes hands. Tandem publishes no name
 * for opcode 295 (it is absent from the Mobi app's HistoryLogType enum and from the Tandem Source
 * schema), so this class name is provisional.
 *
 * <p>Every bolus writes two of these: one when permission is granted (byte 12 nonzero, byte 13 and
 * the change reason 0) and one when it is released or revoked (byte 12 zero, byte 13 equal to the
 * first record's byte 12, the change reason nonzero). This holds for BLE, pump-UI and Control-IQ
 * boluses alike; see https://github.com/jwoglom/pumpX2/issues/142.
 */
@HistoryLogProps(
    opCode = 295,
    displayName = "Bolus Permission Change"
)
public class BolusPermissionChangeHistoryLog extends HistoryLog {

    private int bolusId;
    private int unknownU8At12;
    private int unknownU8At13;
    private int changeReasonId;

    public BolusPermissionChangeHistoryLog() {}

    public BolusPermissionChangeHistoryLog(long pumpTimeSec, long sequenceNum, int bolusId, int unknownU8At12, int unknownU8At13, int changeReasonId) {
        this(pumpTimeSec, sequenceNum, bolusId, unknownU8At12, unknownU8At13, changeReasonId, 0);
    }

    public BolusPermissionChangeHistoryLog(long pumpTimeSec, long sequenceNum, int bolusId, int unknownU8At12, int unknownU8At13, int changeReasonId, int headerHighNibble) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, bolusId, unknownU8At12, unknownU8At13, changeReasonId, headerHighNibble);
        this.bolusId = bolusId;
        this.unknownU8At12 = unknownU8At12;
        this.unknownU8At13 = unknownU8At13;
        this.changeReasonId = changeReasonId;
    }

    public int typeId() {
        return 295;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.bolusId = Bytes.readShort(raw, 10);
        this.unknownU8At12 = raw[12] & 0xFF;
        this.unknownU8At13 = raw[13] & 0xFF;
        this.changeReasonId = raw[14] & 0xFF;
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int bolusId, int unknownU8At12, int unknownU8At13, int changeReasonId, int headerHighNibble) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(295, headerHighNibble),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(bolusId),
            new byte[]{ (byte) unknownU8At12, (byte) unknownU8At13, (byte) changeReasonId }));
    }

    /**
     * @return the bolus id the permission belongs to. It matched the BolusActivated, BolusDelivery
     * and BolusCompleted ids of the same bolus in every record observed.
     */
    public int getBolusId() {
        return bolusId;
    }

    /**
     * @return byte 12. Meaning unconfirmed: it is 1, 2 or 3 on the grant record and 0 on the
     * release record. Across the observed boluses the value tracked who asked for the bolus:
     * 1 for a BLE app (BolusDelivery bolusSource 8), 2 for Control-IQ (bolusSource 7) and 3 for
     * the pump's own UI (bolusSource 1 on t:slim X2, 0 for a Mobi button bolus).
     */
    public int getUnknownU8At12() {
        return unknownU8At12;
    }

    /**
     * @return byte 13. Meaning unconfirmed: 0 on the grant record; on the release record it equals
     * byte 12 of the grant record for the same bolus id.
     */
    public int getUnknownU8At13() {
        return unknownU8At13;
    }

    /**
     * @return byte 14: 0 on the grant record, then the reason the permission ended. It equals
     * {@link BolusPermissionChangeReasonResponse#getLastChangeReasonId()} for the same bolus id
     * wherever both were captured (1 = released, 2 = revoked because the pump UI took priority).
     */
    public int getChangeReasonId() {
        return changeReasonId;
    }

    public BolusPermissionChangeReasonResponse.ChangeReason getChangeReason() {
        return BolusPermissionChangeReasonResponse.ChangeReason.fromId(changeReasonId);
    }
}

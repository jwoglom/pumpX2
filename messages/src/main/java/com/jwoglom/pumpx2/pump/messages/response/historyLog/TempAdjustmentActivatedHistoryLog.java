package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * Temporary adjustment (temp basal rate or temp control rate) activated.
 */
@HistoryLogProps(
    opCode = 565,
    displayName = "Temp Adjustment Activated",
    internalName = "LID_TEMP_ADJUSTMENT_ACTIVATED"
)
public class TempAdjustmentActivatedHistoryLog extends HistoryLog {

    private int requestedPercent;
    private int tempBasalPercent;
    private long duration;
    private int requestedAdjustmentType;
    private int tempRateId;

    public TempAdjustmentActivatedHistoryLog() {}
    public TempAdjustmentActivatedHistoryLog(long pumpTimeSec, long sequenceNum) {
        this(pumpTimeSec, sequenceNum, 0, 0, 0, 0, 0);
    }

    public TempAdjustmentActivatedHistoryLog(long pumpTimeSec, long sequenceNum, int requestedPercent, int tempBasalPercent, long duration, int requestedAdjustmentType, int tempRateId) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, requestedPercent, tempBasalPercent, duration, requestedAdjustmentType, tempRateId);
        parse(cargo);
    }

    public TempAdjustmentActivatedHistoryLog(int requestedPercent, int tempBasalPercent, long duration, int requestedAdjustmentType, int tempRateId) {
        this(0, 0, requestedPercent, tempBasalPercent, duration, requestedAdjustmentType, tempRateId);
    }

    public int typeId() {
        return 565;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.requestedPercent = Bytes.readShort(raw, 10);
        this.tempBasalPercent = Bytes.readShort(raw, 12);
        this.duration = Bytes.readUint32(raw, 18);
        this.requestedAdjustmentType = Bytes.readShort(raw, 22);
        this.tempRateId = Bytes.readShort(raw, 24);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum) {
        return buildCargo(pumpTimeSec, sequenceNum, 0, 0, 0, 0, 0);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int requestedPercent, int tempBasalPercent, long duration, int requestedAdjustmentType, int tempRateId) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(565, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(requestedPercent),
            Bytes.firstTwoBytesLittleEndian(tempBasalPercent),
            new byte[]{0, 0, 0, 0},
            Bytes.toUint32(duration),
            Bytes.firstTwoBytesLittleEndian(requestedAdjustmentType),
            Bytes.firstTwoBytesLittleEndian(tempRateId)));
    }

    /**
     * @return requested percent
     */
    public int getRequestedPercent() {
        return requestedPercent;
    }
    /**
     * @return temp basal percent
     */
    public int getTempBasalPercent() {
        return tempBasalPercent;
    }
    /**
     * @return duration, in milliseconds
     */
    public long getDuration() {
        return duration;
    }
    /**
     * @return raw requested adjustment type, see {@link RequestedAdjustmentType}
     */
    public int getRequestedAdjustmentType() {
        return requestedAdjustmentType;
    }
    public RequestedAdjustmentType getRequestedAdjustmentTypeEnum() {
        return RequestedAdjustmentType.fromId(requestedAdjustmentType);
    }
    /**
     * @return the temp rate ID
     */
    public int getTempRateId() {
        return tempRateId;
    }

    public enum RequestedAdjustmentType {
        TEMP_BASAL_RATE_GUI(0),
        UNUSED_OBE(1),
        TEMP_BASAL_RATE_BLE(2),
        TEMP_CONTROL_RATE_BLE(3)

        ;
        private final int id;
        RequestedAdjustmentType(int id) {
            this.id = id;
        }

        public static RequestedAdjustmentType fromId(int id) {
            for (RequestedAdjustmentType r : values()) {
                if (r.id == id) {
                    return r;
                }
            }
            return null;
        }

        public int getId() {
            return id;
        }
    }
}

package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 313,
    displayName = "Daily Status",
    internalName = "LID_AA_DAILY_STATUS"
)
public class DailyStatusHistoryLog extends HistoryLog {

    private int sensorType;
    private int userMode;
    private int pumpControlState;
    private int weightUnit;
    private int weight;
    private int currentTdiPop;

    public DailyStatusHistoryLog() {}
    public DailyStatusHistoryLog(long pumpTimeSec, long sequenceNum, int sensorType, int userMode, int pumpControlState) {
        this(pumpTimeSec, sequenceNum, sensorType, userMode, pumpControlState, 0, 0, 0);
    }

    public DailyStatusHistoryLog(long pumpTimeSec, long sequenceNum, int sensorType, int userMode, int pumpControlState, int weightUnit, int weight, int currentTdiPop) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, sensorType, userMode, pumpControlState, weightUnit, weight, currentTdiPop);
        parse(cargo);
    }

    public DailyStatusHistoryLog(int sensorType, int userMode, int pumpControlState) {
        this(0, 0, sensorType, userMode, pumpControlState);
    }

    public int typeId() {
        return 313;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        // Layout per the Tandem Source event schema: PumpControlState @10, usermode @11, SensorType @12,
        // WeightUnit @13, Weight u16 @14, currentTDIpop @16.
        this.pumpControlState = raw[10] & 0xFF;
        this.userMode = raw[11] & 0xFF;
        this.sensorType = raw[12] & 0xFF;
        this.weightUnit = raw[13] & 0xFF;
        this.weight = Bytes.readShort(raw, 14);
        this.currentTdiPop = raw[16] & 0xFF;

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int sensorType, int userMode, int pumpControlState) {
        return buildCargo(pumpTimeSec, sequenceNum, sensorType, userMode, pumpControlState, 0, 0, 0);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int sensorType, int userMode, int pumpControlState, int weightUnit, int weight, int currentTdiPop) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(313, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{(byte) pumpControlState, (byte) userMode, (byte) sensorType, (byte) weightUnit},
            Bytes.firstTwoBytesLittleEndian(weight),
            new byte[]{(byte) currentTdiPop}));
    }

    public int getSensorType() {
        return sensorType;
    }

    public int getUserMode() {
        return userMode;
    }

    public int getPumpControlState() {
        return pumpControlState;
    }

    /**
     * @return raw weight unit, see {@link WeightUnit}
     */
    public int getWeightUnit() {
        return weightUnit;
    }

    public WeightUnit getWeightUnitEnum() {
        return WeightUnit.fromId(weightUnit);
    }

    /**
     * @return the configured weight, in the unit given by {@link #getWeightUnit()}
     */
    public int getWeight() {
        return weight;
    }

    /**
     * @return the current total daily insulin (population) estimate, in units
     */
    public int getCurrentTdiPop() {
        return currentTdiPop;
    }

    public SensorType getSensorTypeEnum() {
        return SensorType.fromId(sensorType);
    }

    public UserMode getUserModeEnum() {
        return UserMode.fromId(userMode);
    }

    public PumpControlState getPumpControlStateEnum() {
        return PumpControlState.fromId(pumpControlState);
    }

    public enum SensorType {
        CGM_TYPE_NONE(0),
        CGM_TYPE_DEXCOM_G6(1),
        CGM_TYPE_LIBRE2(2),
        CGM_TYPE_DEXCOM_G7(3),

        ;
        private final int id;
        SensorType(int id) {
            this.id = id;
        }

        static SensorType fromId(int id) {
            for (SensorType t : values()) {
                if (t.id == id) {
                    return t;
                }
            }
            return null;
        }
    }

    public enum UserMode {
        NORMAL(0),
        SLEEPING(1),
        EXERCISING(2),

        ;
        private final int id;
        UserMode(int id) {
            this.id = id;
        }

        static UserMode fromId(int id) {
            for (UserMode m : values()) {
                if (m.id == id) {
                    return m;
                }
            }
            return null;
        }
    }

    public enum WeightUnit {
        NOT_SET(0),
        POUNDS(1),
        KILOGRAMS(2),
        ;

        private final int id;

        WeightUnit(int id) {
            this.id = id;
        }

        static WeightUnit fromId(int id) {
            for (WeightUnit u : values()) {
                if (u.id == id) {
                    return u;
                }
            }
            return null;
        }
    }

    public enum PumpControlState {
        PCM_NO_CONTROL(0),  // No cartridge installed
        PCM_OPEN_LOOP(1),
        PCM_PINING(2),
        PCM_CLOSED_LOOP(3),

        ;
        private final int id;
        PumpControlState(int id) {
            this.id = id;
        }

        static PumpControlState fromId(int id) {
            for (PumpControlState s : values()) {
                if (s.id == id) {
                    return s;
                }
            }
            return null;
        }
    }

}

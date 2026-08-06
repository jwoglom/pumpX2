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

    public DailyStatusHistoryLog() {}
    public DailyStatusHistoryLog(long pumpTimeSec, long sequenceNum, int sensorType, int userMode, int pumpControlState) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, sensorType, userMode, pumpControlState);
        this.sensorType = sensorType;
        this.userMode = userMode;
        this.pumpControlState = pumpControlState;

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
        this.sensorType = raw[11];
        this.userMode = raw[12];
        this.pumpControlState = raw[13];

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int sensorType, int userMode, int pumpControlState) {
        return HistoryLog.fillCargo(Bytes.combine(
            Bytes.firstTwoBytesLittleEndian(313),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{0, (byte) sensorType, (byte) userMode, (byte) pumpControlState}));
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

package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.util.Set;
import java.util.TreeSet;

/**
 * FreeStyle Libre 2 CGM data sample.
 *
 * The layout differs from the Dexcom records ({@link DexcomG6CGMHistoryLog}, {@link DexcomG7CGMHistoryLog}):
 * the glucose value status and CGM data type are single bytes, the rate of change is a signed 16-bit
 * value, and there is no transmitter timestamp. Field layout per the Tandem Source event schema:
 * <pre>
 *  0  uint8   glucoseValueStatus
 *  1  uint8   cgmDataType (bitmask)
 *  2  int16   rate (mg/dL/min)
 *  4  uint8   algorithmState
 *  5  int8    rssi (dBm)
 *  6  uint16  currentGlucoseDisplayValue (mg/dL)
 *  8  uint32  egvTimestamp (seconds)
 * 12  uint16  egvInfoBitmask
 * 14  uint8   interval
 * 15  uint8   reserved
 * </pre>
 */
@HistoryLogProps(
    opCode = 372,
    displayName = "CGM Data (FSL2)",
    internalName = "LID_CGM_DATA_FSL2"
)
public class CgmDataFsl2HistoryLog extends HistoryLog {

    private int glucoseValueStatusRaw;
    private int cgmDataTypeRaw;
    private int rate;
    private int algorithmStateRaw;
    private int rssi;
    private int currentGlucoseDisplayValue;
    private long egvTimestamp;
    private int egvInfoBitmaskRaw;
    private int interval;

    public CgmDataFsl2HistoryLog() {}
    public CgmDataFsl2HistoryLog(long pumpTimeSec, long sequenceNum) {
        this(pumpTimeSec, sequenceNum, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    public CgmDataFsl2HistoryLog(long pumpTimeSec, long sequenceNum, int glucoseValueStatusRaw, int cgmDataTypeRaw, int rate, int algorithmStateRaw, int rssi, int currentGlucoseDisplayValue, long egvTimestamp, int egvInfoBitmaskRaw, int interval) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, glucoseValueStatusRaw, cgmDataTypeRaw, rate, algorithmStateRaw, rssi, currentGlucoseDisplayValue, egvTimestamp, egvInfoBitmaskRaw, interval);
        parse(cargo);
    }

    public CgmDataFsl2HistoryLog(int glucoseValueStatusRaw, int cgmDataTypeRaw, int rate, int algorithmStateRaw, int rssi, int currentGlucoseDisplayValue, long egvTimestamp, int egvInfoBitmaskRaw, int interval) {
        this(0, 0, glucoseValueStatusRaw, cgmDataTypeRaw, rate, algorithmStateRaw, rssi, currentGlucoseDisplayValue, egvTimestamp, egvInfoBitmaskRaw, interval);
    }

    public int typeId() {
        return 372;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.glucoseValueStatusRaw = raw[10] & 0xFF;
        this.cgmDataTypeRaw = raw[11] & 0xFF;
        this.rate = (short) Bytes.readShort(raw, 12);
        this.algorithmStateRaw = raw[14] & 0xFF;
        this.rssi = raw[15];
        this.currentGlucoseDisplayValue = Bytes.readShort(raw, 16);
        this.egvTimestamp = Bytes.readUint32(raw, 18);
        this.egvInfoBitmaskRaw = Bytes.readShort(raw, 22);
        this.interval = raw[24] & 0xFF;
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum) {
        return buildCargo(pumpTimeSec, sequenceNum, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int glucoseValueStatusRaw, int cgmDataTypeRaw, int rate, int algorithmStateRaw, int rssi, int currentGlucoseDisplayValue, long egvTimestamp, int egvInfoBitmaskRaw, int interval) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(372, 0), // 372 = 0x0174
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{(byte) glucoseValueStatusRaw},
            new byte[]{(byte) cgmDataTypeRaw},
            Bytes.firstTwoBytesLittleEndian(rate & 0xFFFF),
            new byte[]{(byte) algorithmStateRaw},
            new byte[]{(byte) rssi},
            Bytes.firstTwoBytesLittleEndian(currentGlucoseDisplayValue),
            Bytes.toUint32(egvTimestamp),
            Bytes.firstTwoBytesLittleEndian(egvInfoBitmaskRaw),
            new byte[]{(byte) interval},
            new byte[]{0}));
    }

    public enum GlucoseValueStatus {
        PRECISE_VALUE(0),
        SPECIAL_HIGH(1),
        SPECIAL_LOW(2)

        ;
        private int id;
        GlucoseValueStatus(int id) {
            this.id = id;
        }

        public static GlucoseValueStatus fromId(int id) {
            for (GlucoseValueStatus r : values()) {
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

    /**
     * Bitmask describing the type of reading. Unlike the Dexcom records, Libre readings
     * can also be one-minute readings (OMR).
     */
    public enum CgmDataType {
        FIVE_MINUTE_READING(1),
        BACKFILL(2),
        NONE(16),
        ONE_MINUTE_READING(32)

        ;
        private int id;
        CgmDataType(int id) {
            this.id = id;
        }

        public static Set<CgmDataType> fromId(int mask) {
            Set<CgmDataType> items = new TreeSet<>();
            for (CgmDataType i : values()) {
                if ((mask & i.getId()) != 0) {
                    items.add(i);
                }
            }
            return items;
        }

        public int getId() {
            return id;
        }
    }

    public enum AlgorithmState {
        WARMUP(2),
        OK(100),
        RF_ERROR(101),
        SENSOR_SIGNAL_LOW(102),
        TEMP_HIGH(103),
        TEMP_LOW(104),
        INVALID_DATA(105),
        OTHER_DQ(106),
        EGV_UNUSABLE(107)

        ;
        private int id;
        AlgorithmState(int id) {
            this.id = id;
        }

        public static AlgorithmState fromId(int id) {
            for (AlgorithmState r : values()) {
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

    /**
     * EGV info bitmask. Bits 11-13 encode the sensor type (2 = Libre 2, 4 = Libre 3), see {@link #getSensorType()}.
     */
    public enum EgvInfo {
        FIVE_MINUTE_READING(1),
        BACKFILL(2),
        NO_EGV(16),
        VALID_TIMESTAMP(32),
        VALID_EGV_RANGE(64),
        VALID_ALG_STATE(128),
        EGV_PROCESSED(256),
        ONE_MINUTE_READING(512)

        ;
        private int id;
        EgvInfo(int id) {
            this.id = id;
        }

        public static Set<EgvInfo> fromId(int mask) {
            Set<EgvInfo> items = new TreeSet<>();
            for (EgvInfo i : values()) {
                if ((mask & i.getId()) != 0) {
                    items.add(i);
                }
            }
            return items;
        }

        public int getId() {
            return id;
        }
    }

    public int getGlucoseValueStatusRaw() {
        return glucoseValueStatusRaw;
    }
    public GlucoseValueStatus getGlucoseValueStatus() {
        return GlucoseValueStatus.fromId(glucoseValueStatusRaw);
    }
    public int getCgmDataTypeRaw() {
        return cgmDataTypeRaw;
    }
    public Set<CgmDataType> getCgmDataType() {
        return CgmDataType.fromId(cgmDataTypeRaw);
    }
    /**
     * Signed rate of change, in mg/dL per minute.
     */
    public int getRate() {
        return rate;
    }
    public int getAlgorithmStateRaw() {
        return algorithmStateRaw;
    }
    public AlgorithmState getAlgorithmState() {
        return AlgorithmState.fromId(algorithmStateRaw);
    }
    /**
     * Signed RSSI, in dBm.
     */
    public int getRssi() {
        return rssi;
    }
    /**
     * @return the glucose value in mg/dL
     */
    public int getCurrentGlucoseDisplayValue() {
        return currentGlucoseDisplayValue;
    }
    public long getEgvTimestamp() {
        return egvTimestamp;
    }
    public int getEgvInfoBitmaskRaw() {
        return egvInfoBitmaskRaw;
    }
    public Set<EgvInfo> getEgvInfo() {
        return EgvInfo.fromId(egvInfoBitmaskRaw);
    }
    /**
     * @return the sensor type encoded in bits 11-13 of the EGV info bitmask (2 = Libre 2, 4 = Libre 3)
     */
    public int getSensorType() {
        return (egvInfoBitmaskRaw >> 11) & 0x7;
    }
    public int getInterval() {
        return interval;
    }

    /** @deprecated use {@link #getGlucoseValueStatusRaw()} */
    @Deprecated
    public int getStatus() {
        return glucoseValueStatusRaw;
    }
    /** @deprecated use {@link #getCgmDataTypeRaw()} */
    @Deprecated
    public int getType() {
        return cgmDataTypeRaw;
    }
    /** @deprecated use {@link #getCurrentGlucoseDisplayValue()} */
    @Deprecated
    public int getValue() {
        return currentGlucoseDisplayValue;
    }
    /** @deprecated use {@link #getEgvTimestamp()} */
    @Deprecated
    public long getTimestamp() {
        return egvTimestamp;
    }
}

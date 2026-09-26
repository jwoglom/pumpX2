package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CgmStatusV2Response;

import java.util.Set;
import java.util.TreeSet;

@HistoryLogProps(
    opCode = 256,
    displayName = "Dexcom G6 CGM Data",
    internalName = "LID_CGM_DATA_GXB",
    usedByAndroid = true,
    usedByTidepool = true, // LID_CGM_DATA_GXB
    usedByTconnectsync = true
)
public class DexcomG6CGMHistoryLog extends HistoryLog {
    
    private int glucoseValueStatusRaw;
    private GlucoseValueStatus glucoseValueStatus;
    private int cgmDataTypeRaw;
    private Set<CgmDataType> cgmDataTypes;
    private int rate;
    private int algorithmState;
    private int rssi;
    private int currentGlucoseDisplayValue;
    private long timeStampSeconds;
    private int egvInfoBitmaskRaw;
    private Set<EgvInfo> egvInfo;
    private int interval;
    private int egvCount;
    
    public DexcomG6CGMHistoryLog() {}
    
    public DexcomG6CGMHistoryLog(long pumpTimeSec, long sequenceNum, int glucoseValueStatusRaw, int cgmDataTypeRaw, int rate, int algorithmState, int rssi, int currentGlucoseDisplayValue, long timeStampSeconds, int egvInfoBitmask, int interval, int egvCount) {
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, glucoseValueStatusRaw, cgmDataTypeRaw, rate, algorithmState, rssi, currentGlucoseDisplayValue, timeStampSeconds, egvInfoBitmask, interval, egvCount);
        parse(cargo);
        
    }

    /**
     * Builds a record with {@code egvCount} = 1, the value a five-minute reading with no missed
     * readings carries.
     */
    public DexcomG6CGMHistoryLog(long pumpTimeSec, long sequenceNum, int glucoseValueStatusRaw, int cgmDataTypeRaw, int rate, int algorithmState, int rssi, int currentGlucoseDisplayValue, long timeStampSeconds, int egvInfoBitmask, int interval) {
        this(pumpTimeSec, sequenceNum, glucoseValueStatusRaw, cgmDataTypeRaw, rate, algorithmState, rssi, currentGlucoseDisplayValue, timeStampSeconds, egvInfoBitmask, interval, 1);
    }

    public int typeId() {
        return 256;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.glucoseValueStatusRaw = Bytes.readShort(raw, 10);
        this.glucoseValueStatus = getGlucoseValueStatus();
        this.cgmDataTypeRaw = raw[12];
        this.cgmDataTypes = getCgmDataTypes();
        this.rate = raw[13];
        this.algorithmState = raw[14];
        this.rssi = raw[15];
        this.currentGlucoseDisplayValue = Bytes.readShort(raw, 16);
        this.timeStampSeconds = Bytes.readUint32(raw, 18);
        this.egvInfoBitmaskRaw = Bytes.readShort(raw, 22);
        this.egvInfo = getEgvInfo();
        this.interval = raw[24];
        this.egvCount = raw[25] & 0xFF;
        
    }

    /**
     * Builds a record with {@code egvCount} = 1, the value a five-minute reading with no missed
     * readings carries.
     */
    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int glucoseValueStatus, int cgmDataType, int rate, int algorithmState, int rssi, int currentGlucoseDisplayValue, long timeStampSeconds, int egvInfoBitmask, int interval) {
        return buildCargo(pumpTimeSec, sequenceNum, glucoseValueStatus, cgmDataType, rate, algorithmState, rssi, currentGlucoseDisplayValue, timeStampSeconds, egvInfoBitmask, interval, 1);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int glucoseValueStatus, int cgmDataType, int rate, int algorithmState, int rssi, int currentGlucoseDisplayValue, long timeStampSeconds, int egvInfoBitmask, int interval, int egvCount) {
        return Bytes.combine(
            HistoryLog.typeIdBytes(256, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(glucoseValueStatus), 
            new byte[]{ (byte) cgmDataType }, 
            new byte[]{ (byte) rate }, 
            new byte[]{ (byte) algorithmState }, 
            new byte[]{ (byte) rssi }, 
            Bytes.firstTwoBytesLittleEndian(currentGlucoseDisplayValue), 
            Bytes.toUint32(timeStampSeconds), 
            Bytes.firstTwoBytesLittleEndian(egvInfoBitmask), 
            new byte[]{ (byte) interval },
            new byte[]{ (byte) egvCount });
    }
    
    public int getGlucoseValueStatusRaw() {
        return glucoseValueStatusRaw;
    }

    public enum GlucoseValueStatus {
        PRECISE_VALUE(0),
        SPECIAL_HIGH(1),
        SPECIAL_LOW(2),
        DO_NOT_SHOW(6)

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

    public GlucoseValueStatus getGlucoseValueStatus() {
        return GlucoseValueStatus.fromId(glucoseValueStatusRaw);
    }

    public int getCgmDataTypeRaw() {
        return cgmDataTypeRaw;
    }

    public enum CgmDataType {
        FIVE_MINUTE_READING(1),
        BACKFILL(2),
        IMMEDIATE_MATCH_VALUE(4),
        CALIBRATION(8),
        NONE(16)

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

    public Set<CgmDataType> getCgmDataTypes() {
        return CgmDataType.fromId(cgmDataTypeRaw);
    }

    /**
     * Signed rate of change, in 0.1 mg/dL per minute.
     */
    public int getRate() {
        return rate;
    }
    public int getAlgorithmState() {
        return algorithmState;
    }
    /**
     * Signed RSSI, in dBm.
     */
    public int getRssi() {
        return rssi;
    }
    public int getCurrentGlucoseDisplayValue() {
        return currentGlucoseDisplayValue;
    }
    public long getTimeStampSeconds() {
        return timeStampSeconds;
    }
    public int getEgvInfoBitmaskRaw() {
        return egvInfoBitmaskRaw;
    }

    public enum EgvInfo {
        FIVE_MINUTE_READING(1),
        BACKFILL(2),
        IMMEDIATE_MATCH_VALUE(4),
        CALIBRATION(8),
        NO_EGV(16),
        VALID_TIMESTAMP(32),
        VALID_EGV_RANGE(64),
        VALID_ALG_STATE(128),
        EGV_PROCESSED(256)

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

    public Set<EgvInfo> getEgvInfo() {
        return EgvInfo.fromId(egvInfoBitmaskRaw);
    }

    /**
     * Bits 11-13 of {@code egvInfoBitmask}: the CGM sensor type. Tandem's FSL3 schema (opcode 480)
     * names these bits "Sensor Type"; in captures they are 1 (G6) in 256 records from 2023 on, 3
     * (G7) in 399 records, and 0 on 2022 firmware.
     */
    public CgmStatusV2Response.CgmSensorType getEgvSensorType() {
        return getEgvSensorType(egvInfoBitmaskRaw);
    }

    static CgmStatusV2Response.CgmSensorType getEgvSensorType(int egvInfoBitmask) {
        return CgmStatusV2Response.CgmSensorType.fromId((egvInfoBitmask >> 11) & 0x7);
    }

    public int getInterval() {
        return interval;
    }

    /**
     * Byte 25, which Tandem's schema leaves unnamed. It is 0 in backfill records. In a five-minute
     * reading it is the number of readings the record covers: 1 normally, or N after N-1 missed
     * readings, whose backfill records the pump logs right after it. The meaning is inferred from
     * captures (see pumpX2#76); newer Mobi firmware has been seen writing 0 here for five-minute
     * readings too.
     */
    public int getEgvCount() {
        return egvCount;
    }
    
}
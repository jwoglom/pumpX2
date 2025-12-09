package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

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
    
    public DexcomG6CGMHistoryLog() {}
    
    public DexcomG6CGMHistoryLog(long pumpTimeSec, long sequenceNum, int glucoseValueStatusRaw, int cgmDataTypeRaw, int rate, int algorithmState, int rssi, int currentGlucoseDisplayValue, long timeStampSeconds, int egvInfoBitmask, int interval) {
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, glucoseValueStatusRaw, cgmDataTypeRaw, rate, algorithmState, rssi, currentGlucoseDisplayValue, timeStampSeconds, egvInfoBitmask, interval);
        parse(cargo);
        
    }

    public int typeId() {
        return 256;
    }

    /**
     * TODO: this needs to be checked against the tconnectsync eventparser code since they seem to differ
     */
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
        
    }

    
    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int glucoseValueStatus, int cgmDataType, int rate, int algorithmState, int rssi, int currentGlucoseDisplayValue, long timeStampSeconds, int egvInfoBitmask, int interval) {
        return Bytes.combine(
            new byte[]{0, 1},
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
            new byte[]{ 1 }); // missing param?
    }
    
    public int getGlucoseValueStatusRaw() {
        return glucoseValueStatusRaw;
    }

    public enum GlucoseValueStatus {
        NORMAL(0),
        HIGH(1),
        LOW(2)

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

    public int getRate() {
        return rate;
    }
    public int getAlgorithmState() {
        return algorithmState;
    }
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
    public int getInterval() {
        return interval;
    }
    
}
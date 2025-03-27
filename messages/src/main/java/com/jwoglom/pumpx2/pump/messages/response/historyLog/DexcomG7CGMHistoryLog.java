package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.util.Set;

@HistoryLogProps(
    opCode = 399,
    displayName = "Dexcom G7 CGM Data",
    usedByTconnectsync = true
)
public class DexcomG7CGMHistoryLog extends HistoryLog {
    
    private int glucoseValueStatusRaw;
    private GlucoseValueStatus glucoseValueStatus;
    private int cgmDataTypeRaw;
    private Set<DexcomG6CGMHistoryLog.CgmDataType> cgmDataType;
    private int rate;
    private int algorithmStateRaw;
    private AlgorithmState algorithmState;
    private int rssi;
    private int currentGlucoseDisplayValue;
    private long egvTimestamp;
    private int egvInfoBitmaskRaw;
    private Set<DexcomG6CGMHistoryLog.EgvInfo> egvInfo;
    private int interval;
    
    public DexcomG7CGMHistoryLog() {}
    public DexcomG7CGMHistoryLog(long pumpTimeSec, long sequenceNum, int glucoseValueStatusRaw, int cgmDataTypeRaw, int rate, int algorithmStateRaw, int rssi, int currentGlucoseDisplayValue, int egvTimestamp, int egvInfoBitmask, int interval) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, glucoseValueStatusRaw, cgmDataTypeRaw, rate, algorithmStateRaw, rssi, currentGlucoseDisplayValue, egvTimestamp, egvInfoBitmask, interval);
        parse(cargo);
        
    }

    public DexcomG7CGMHistoryLog(int glucoseValueStatusRaw, int cgmDataTypeRaw, int rate, int algorithmStateRaw, int rssi, int currentGlucoseDisplayValue, int egvTimestamp, int egvInfoBitmask, int interval) {
        this(0, 0, glucoseValueStatusRaw, cgmDataTypeRaw, rate, algorithmStateRaw, rssi, currentGlucoseDisplayValue, egvTimestamp, egvInfoBitmask, interval);
    }

    public int typeId() {
        return 399;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.glucoseValueStatusRaw = Bytes.readShort(raw, 10);
        this.glucoseValueStatus = getGlucoseValueStatus();
        this.cgmDataTypeRaw = raw[12];
        this.cgmDataType = getCgmDataType();
        this.rate = raw[13];
        this.algorithmStateRaw = raw[14];
        this.algorithmState = getAlgorithmState();
        this.rssi = raw[15];
        this.currentGlucoseDisplayValue = Bytes.readShort(raw, 16);
        this.egvTimestamp = Bytes.readUint32(raw, 18);
        this.egvInfoBitmaskRaw = Bytes.readShort(raw, 22);
        this.egvInfo = getEgvInfo();
        this.interval = raw[24];
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int glucoseValueStatusRaw, int cgmDataTypeRaw, int rate, int algorithmStateRaw, int rssi, int currentGlucoseDisplayValue, int egvTimestamp, int egvInfoBitmask, int interval) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{(byte) 399, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(glucoseValueStatusRaw), 
            new byte[]{ (byte) cgmDataTypeRaw }, 
            new byte[]{ (byte) rate }, 
            new byte[]{ (byte) algorithmStateRaw }, 
            new byte[]{ (byte) rssi }, 
            Bytes.firstTwoBytesLittleEndian(currentGlucoseDisplayValue), 
            Bytes.toUint32(egvTimestamp), 
            Bytes.firstTwoBytesLittleEndian(egvInfoBitmask), 
            new byte[]{ (byte) interval }));
    }
    public int getGlucoseValueStatusRaw() {
        return glucoseValueStatusRaw;
    }
    public int getCgmDataTypeRaw() {
        return cgmDataTypeRaw;
    }
    public Set<DexcomG6CGMHistoryLog.CgmDataType> getCgmDataType() {
        return DexcomG6CGMHistoryLog.CgmDataType.fromId(cgmDataTypeRaw);
    }
    public int getRate() {
        return rate;
    }
    public int getAlgorithmStateRaw() {
        return algorithmStateRaw;
    }


    public enum AlgorithmState {
        WARMUP(2),
        DEFAULT_ELECTRONICS_WAKEUP(30),
        DETECTING_DEPLOYMENT(31),
        REPORTABLE_PERIOD_VALID_EGV(32),
        REPORTABLE_PERIOD_INVALID_EGV(33),
        STOPPED_END_OF_SESSION(34),
        STOPPED_ALG_DETECTED_FAILURE(35),
        STOPPED_MANUAL_STOP(36),
        STOPPED_TX_FAILURE(37),
        STOPPED_SIV_FAILURE(38),
        STOPPED_ENNVIRONMENTAL_CONDITIONS(39)

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

    public AlgorithmState getAlgorithmState() {
        return AlgorithmState.fromId(algorithmStateRaw);
    }


    public enum GlucoseValueStatus {
        NORMAL(0),
        HIGH(1),
        LOW(2),
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

    public int getRssi() {
        return rssi;
    }
    public int getCurrentGlucoseDisplayValue() {
        return currentGlucoseDisplayValue;
    }
    public long getEgvTimestamp() {
        return egvTimestamp;
    }
    public int getEgvInfoBitmaskRaw() {
        return egvInfoBitmaskRaw;
    }
    public Set<DexcomG6CGMHistoryLog.EgvInfo> getEgvInfo() {
        return DexcomG6CGMHistoryLog.EgvInfo.fromId(egvInfoBitmaskRaw);
    }
    public int getInterval() {
        return interval;
    }
    
}
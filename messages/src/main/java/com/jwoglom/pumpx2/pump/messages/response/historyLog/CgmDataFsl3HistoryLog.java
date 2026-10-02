package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.util.Set;

/**
 * FreeStyle Libre 3 CGM data sample. Same layout as {@link CgmDataFsl2HistoryLog}.
 */
@HistoryLogProps(
    opCode = 480,
    displayName = "CGM Data (FSL3)",
    internalName = "LID_CGM_DATA_FSL3"
)
public class CgmDataFsl3HistoryLog extends HistoryLog {

    private int glucoseValueStatusRaw;
    private int cgmDataTypeRaw;
    private int rate;
    private int algorithmStateRaw;
    private int rssi;
    private int currentGlucoseDisplayValue;
    private long egvTimestamp;
    private int egvInfoBitmaskRaw;
    private int interval;

    public CgmDataFsl3HistoryLog() {}
    public CgmDataFsl3HistoryLog(long pumpTimeSec, long sequenceNum) {
        this(pumpTimeSec, sequenceNum, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    public CgmDataFsl3HistoryLog(long pumpTimeSec, long sequenceNum, int glucoseValueStatusRaw, int cgmDataTypeRaw, int rate, int algorithmStateRaw, int rssi, int currentGlucoseDisplayValue, long egvTimestamp, int egvInfoBitmaskRaw, int interval) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, glucoseValueStatusRaw, cgmDataTypeRaw, rate, algorithmStateRaw, rssi, currentGlucoseDisplayValue, egvTimestamp, egvInfoBitmaskRaw, interval);
        parse(cargo);
    }

    public CgmDataFsl3HistoryLog(int glucoseValueStatusRaw, int cgmDataTypeRaw, int rate, int algorithmStateRaw, int rssi, int currentGlucoseDisplayValue, long egvTimestamp, int egvInfoBitmaskRaw, int interval) {
        this(0, 0, glucoseValueStatusRaw, cgmDataTypeRaw, rate, algorithmStateRaw, rssi, currentGlucoseDisplayValue, egvTimestamp, egvInfoBitmaskRaw, interval);
    }

    public int typeId() {
        return 480;
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
            HistoryLog.typeIdBytes(480, 0), // 480 = 0x01E0
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

    public int getGlucoseValueStatusRaw() {
        return glucoseValueStatusRaw;
    }
    public CgmDataFsl2HistoryLog.GlucoseValueStatus getGlucoseValueStatus() {
        return CgmDataFsl2HistoryLog.GlucoseValueStatus.fromId(glucoseValueStatusRaw);
    }
    public int getCgmDataTypeRaw() {
        return cgmDataTypeRaw;
    }
    public Set<CgmDataFsl2HistoryLog.CgmDataType> getCgmDataType() {
        return CgmDataFsl2HistoryLog.CgmDataType.fromId(cgmDataTypeRaw);
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
    public CgmDataFsl2HistoryLog.AlgorithmState getAlgorithmState() {
        return CgmDataFsl2HistoryLog.AlgorithmState.fromId(algorithmStateRaw);
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
    public Set<CgmDataFsl2HistoryLog.EgvInfo> getEgvInfo() {
        return CgmDataFsl2HistoryLog.EgvInfo.fromId(egvInfoBitmaskRaw);
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

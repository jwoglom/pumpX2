package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 372,
    displayName = "CGM Data (FSL2)",
    internalName = "LID_CGM_DATA_FSL2"
)
public class CgmDataFsl2HistoryLog extends HistoryLog {

    private int status;
    private int type;
    private int rate;
    private int rssi;
    private int value;
    private long timestamp;
    private long transmitterTimestamp;

    public CgmDataFsl2HistoryLog() {}
    public CgmDataFsl2HistoryLog(long pumpTimeSec, long sequenceNum) {
        this(pumpTimeSec, sequenceNum, 0, 0, 0, 0, 0, 0, 0);
    }

    public CgmDataFsl2HistoryLog(long pumpTimeSec, long sequenceNum, int status, int type, int rate, int rssi, int value, long timestamp, long transmitterTimestamp) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, status, type, rate, rssi, value, timestamp, transmitterTimestamp);
        this.status = status;
        this.type = type;
        this.rate = rate;
        this.rssi = rssi;
        this.value = value;
        this.timestamp = timestamp;
        this.transmitterTimestamp = transmitterTimestamp;
    }

    public CgmDataFsl2HistoryLog(int status, int type, int rate, int rssi, int value, long timestamp, long transmitterTimestamp) {
        this(0, 0, status, type, rate, rssi, value, timestamp, transmitterTimestamp);
    }

    public int typeId() {
        return 372;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.status = Bytes.readShort(raw, 10);
        this.type = raw[12];
        this.rate = raw[13];
        this.rssi = raw[15];
        this.value = Bytes.readShort(raw, 16);
        this.timestamp = Bytes.readUint32(raw, 18);
        this.transmitterTimestamp = Bytes.readUint32(raw, 22);

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum) {
        return buildCargo(pumpTimeSec, sequenceNum, 0, 0, 0, 0, 0, 0, 0);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int status, int type, int rate, int rssi, int value, long timestamp, long transmitterTimestamp) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{(byte)(372 & 0xFF), (byte)(372 >> 8)}, // 372 = 0x0174
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(status),
            new byte[]{(byte) type},
            new byte[]{(byte) rate},
            new byte[]{0},
            new byte[]{(byte) rssi},
            Bytes.firstTwoBytesLittleEndian(value),
            Bytes.toUint32(timestamp),
            Bytes.toUint32(transmitterTimestamp)));
    }

    public int getStatus() {
        return status;
    }
    public int getType() {
        return type;
    }
    public int getRate() {
        return rate;
    }
    public int getRssi() {
        return rssi;
    }
    /**
     * @return the glucose value in mg/dL
     */
    public int getValue() {
        return value;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public long getTransmitterTimestamp() {
        return transmitterTimestamp;
    }
}

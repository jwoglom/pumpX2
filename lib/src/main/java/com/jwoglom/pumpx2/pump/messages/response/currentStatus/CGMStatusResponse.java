package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CGMStatusRequest;

@MessageProps(
    opCode=81,
    size=10,
    type=MessageType.RESPONSE,
    request=CGMStatusRequest.class
)
public class CGMStatusResponse extends Message {
    
    private int sessionState;
    private long lastCalibrationTimestamp;
    private long sensorStartedTimestamp;
    private int transmitterBatteryStatus;
    
    public CGMStatusResponse() {}
    
    public CGMStatusResponse(int sessionState, long lastCalibrationTimestamp, long sensorStartedTimestamp, int transmitterBatteryStatus) {
        this.cargo = buildCargo(sessionState, lastCalibrationTimestamp, sensorStartedTimestamp, transmitterBatteryStatus);
        this.sessionState = sessionState;
        this.lastCalibrationTimestamp = lastCalibrationTimestamp;
        this.sensorStartedTimestamp = sensorStartedTimestamp;
        this.transmitterBatteryStatus = transmitterBatteryStatus;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.sessionState = raw[0];
        this.lastCalibrationTimestamp = Bytes.readUint32(raw, 1);
        this.sensorStartedTimestamp = Bytes.readUint32(raw, 5);
        this.transmitterBatteryStatus = raw[9];
        
    }

    
    public static byte[] buildCargo(int sessionState, long lastCalibrationTimestamp, long sensorStartedTimestamp, int transmitterBatteryStatus) {
        return Bytes.combine(
            new byte[]{ (byte) sessionState }, 
            Bytes.toUint32(lastCalibrationTimestamp), 
            Bytes.toUint32(sensorStartedTimestamp), 
            new byte[]{ (byte) transmitterBatteryStatus });
    }
    
    public int getSessionState() {
        return sessionState;
    }
    public long getLastCalibrationTimestamp() {
        return lastCalibrationTimestamp;
    }
    public long getSensorStartedTimestamp() {
        return sensorStartedTimestamp;
    }
    public int getTransmitterBatteryStatus() {
        return transmitterBatteryStatus;
    }
    
}
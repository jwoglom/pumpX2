package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.HistoryLogResponse;

@MessageProps(
    opCode=60,
    size=5,
    type=MessageType.REQUEST,
    response=HistoryLogResponse.class
)
public class HistoryLogRequest extends Message { 
    private long startLog;
    private int numberOfLogs;
    
    public HistoryLogRequest() {}

    public HistoryLogRequest(long startLog, int numberOfLogs) {
        this.cargo = buildCargo(startLog, numberOfLogs);
        this.startLog = startLog;
        this.numberOfLogs = numberOfLogs;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.startLog = Bytes.readUint32(raw, 0);
        this.numberOfLogs = raw[4];
        
    }

    
    public static byte[] buildCargo(long startLog, int numberOfLogs) {
        return Bytes.combine(
            Bytes.toUint32(startLog), 
        
            new byte[]{ (byte) numberOfLogs }
        );
    }
    public long getStartLog() {
        return startLog;
    }
    public int getNumberOfLogs() {
        return numberOfLogs;
    }
    
    
}
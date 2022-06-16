package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.UndefinedRequest;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * Multiple HistoryLogSdtreamResponse is received after initiating a HistoryLogRequest.
 */
@MessageProps(
    opCode=-127,
    size=28,
    variableSize=true,
    type=MessageType.RESPONSE,
    request= UndefinedRequest.class
)
public class HistoryLogStreamResponse extends Message {
    
    private int numberOfHistoryLogs;
    private byte[] streamBytes;
    
    public HistoryLogStreamResponse() {}
    
    public HistoryLogStreamResponse(int numberOfHistoryLogs, byte[] streamBytes) {
        this.cargo = buildCargo(numberOfHistoryLogs, streamBytes);
        this.numberOfHistoryLogs = numberOfHistoryLogs;
        this.streamBytes = streamBytes;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.numberOfHistoryLogs = raw[0];
        this.streamBytes = Arrays.copyOfRange(raw, 1, raw.length);
        
    }

    
    public static byte[] buildCargo(int numberOfHistoryLogs, byte[] streamBytes) {
        return Bytes.combine(
            new byte[]{ (byte) numberOfHistoryLogs }, 
            streamBytes
            );
    }
    
    public int getNumberOfHistoryLogs() {
        return numberOfHistoryLogs;
    }
    public byte[] getStreamBytes() {
        return streamBytes;
    }
    
}
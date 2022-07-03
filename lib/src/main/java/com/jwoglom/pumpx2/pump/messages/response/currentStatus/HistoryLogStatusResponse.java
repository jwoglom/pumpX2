package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.HistoryLogStatusRequest;

import java.math.BigInteger;

@MessageProps(
    opCode=59,
    size=12,
    type=MessageType.RESPONSE,
    request=HistoryLogStatusRequest.class
)
public class HistoryLogStatusResponse extends Message {
    
    private long numEntries;
    private long firstSequenceNum;
    private long lastSequenceNum;
    
    public HistoryLogStatusResponse() {}
    
    public HistoryLogStatusResponse(long numEntries, long firstSequenceNum, long lastSequenceNum) {
        this.cargo = buildCargo(numEntries, firstSequenceNum, lastSequenceNum);
        this.numEntries = numEntries;
        this.firstSequenceNum = firstSequenceNum;
        this.lastSequenceNum = lastSequenceNum;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.numEntries = Bytes.readUint32(raw, 0);
        this.firstSequenceNum = Bytes.readUint32(raw, 4);
        this.lastSequenceNum = Bytes.readUint32(raw, 8);
        
    }

    
    public static byte[] buildCargo(long numEntries, long firstSequenceNum, long lastSequenceNum) {
        return Bytes.combine(
            Bytes.toUint32(numEntries), 
            Bytes.toUint32(firstSequenceNum), 
            Bytes.toUint32(lastSequenceNum));
    }
    
    public long getNumEntries() {
        return numEntries;
    }
    public long getFirstSequenceNum() {
        return firstSequenceNum;
    }
    public long getLastSequenceNum() {
        return lastSequenceNum;
    }
    
}
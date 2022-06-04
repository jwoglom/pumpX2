package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.LastBGRequest;

@MessageProps(
    opCode=51,
    size=7,
    type=MessageType.RESPONSE,
    request=LastBGRequest.class
)
public class LastBGResponse extends Message {
    
    private long bgTimestamp;
    private int bgValue;
    private int bgSource;
    
    public LastBGResponse() {}
    
    public LastBGResponse(long bgTimestamp, int bgValue, int bgSource) {
        this.cargo = buildCargo(bgTimestamp, bgValue, bgSource);
        this.bgTimestamp = bgTimestamp;
        this.bgValue = bgValue;
        this.bgSource = bgSource;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.bgTimestamp = Bytes.readUint32(raw, 0);
        this.bgValue = Bytes.readShort(raw, 4);
        this.bgSource = raw[6];
        
    }

    
    public static byte[] buildCargo(long bgTimestamp, int bgValue, int bgSource) {
        return Bytes.combine(
            Bytes.toUint32(bgTimestamp), 
            Bytes.firstTwoBytesLittleEndian(bgValue), 
            new byte[]{ (byte) bgSource });
    }
    
    public long getBgTimestamp() {
        return bgTimestamp;
    }
    public int getBgValue() {
        return bgValue;
    }
    public int getBgSource() {
        return bgSource;
    }
    
}
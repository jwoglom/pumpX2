package com.jwoglom.pumpx2.pump.messages.response;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.CurrentBatteryV1Request;

import java.math.BigInteger;

@MessageProps(
    opCode=53,
    size=2,
    type=MessageType.RESPONSE,
    request=CurrentBatteryV1Request.class
)
public class CurrentBatteryV1Response extends CurrentBatteryAbstractResponse {
    
    private int currentBatteryAbc;
    private int currentBatteryIbc;
    
    public CurrentBatteryV1Response() {}
    
    public CurrentBatteryV1Response(int currentBatteryAbc, int currentBatteryIbc) {
        this.cargo = buildCargo(currentBatteryAbc, currentBatteryIbc);
        this.currentBatteryAbc = currentBatteryAbc;
        this.currentBatteryIbc = currentBatteryIbc;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.currentBatteryAbc = raw[0];
        this.currentBatteryIbc = raw[1];
        
    }

    
    public static byte[] buildCargo(int currentBatteryAbc, int currentBatteryIbc) {
        return Bytes.combine(
            new byte[]{ (byte) currentBatteryAbc }, 
            new byte[]{ (byte) currentBatteryIbc });
    }
    
    public int getCurrentBatteryAbc() {
        return currentBatteryAbc;
    }
    public int getCurrentBatteryIbc() {
        return currentBatteryIbc;
    }
    
}
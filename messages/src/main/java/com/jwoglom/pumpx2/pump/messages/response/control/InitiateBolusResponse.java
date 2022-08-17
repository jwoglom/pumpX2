package com.jwoglom.pumpx2.pump.messages.response.control;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.control.InitiateBolusRequest;

import java.math.BigInteger;

@MessageProps(
    opCode=-97,
    size=30,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    request=InitiateBolusRequest.class,
    signed=true
)
public class InitiateBolusResponse extends Message {
    
    private int status;
    private int bolusId;
    private int statusType;
    
    public InitiateBolusResponse() {}
    
    public InitiateBolusResponse(int status, int bolusId, int statusType) {
        this.cargo = buildCargo(status, bolusId, statusType);
        this.status = status;
        this.bolusId = bolusId;
        this.statusType = statusType;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0];
        this.bolusId = Bytes.readShort(raw, 1);
        this.statusType = raw[5];
        
    }

    
    public static byte[] buildCargo(int status, int bolusId, int statusType) {
        return Bytes.combine(
            new byte[]{ (byte) status }, 
            Bytes.firstTwoBytesLittleEndian(bolusId), 
            new byte[]{ (byte) statusType });
    }
    
    public int getStatus() {
        return status;
    }
    public int getBolusId() {
        return bolusId;
    }
    public int getStatusType() {
        return statusType;
    } // 0 = successBolusIsPending
    
}
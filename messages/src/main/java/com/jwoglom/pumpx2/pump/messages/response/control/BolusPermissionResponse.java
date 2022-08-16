package com.jwoglom.pumpx2.pump.messages.response.control;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.request.control.BolusPermissionRequest;

import java.math.BigInteger;

@MessageProps(
    opCode=-93,
    size=30,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    request=BolusPermissionRequest.class,
    minApi=KnownApiVersion.API_V2_5,
    signed=true
)
public class BolusPermissionResponse extends Message {
    
    private int status;
    private int bolusId;
    private int nackReason;
    
    public BolusPermissionResponse() {}
    
    public BolusPermissionResponse(int status, int bolusId, int nackReason) {
        this.cargo = buildCargo(status, bolusId, nackReason);
        this.status = status;
        this.bolusId = bolusId;
        this.nackReason = nackReason;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0];
        this.bolusId = Bytes.readShort(raw, 1);
        this.nackReason = raw[5];
        
    }

    
    public static byte[] buildCargo(int status, int bolusId, int nackReason) {
        return Bytes.combine(
            new byte[]{ (byte) status }, 
            Bytes.firstTwoBytesLittleEndian(bolusId), 
            new byte[]{ (byte) nackReason },
            new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
    }
    
    public int getStatus() {
        return status;
    }
    public int getBolusId() {
        return bolusId;
    }
    public int getNackReason() {
        return nackReason;
    }
    
}
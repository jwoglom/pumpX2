package com.jwoglom.pumpx2.pump.messages.response.control;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.request.control.CancelBolusRequest;

import java.math.BigInteger;

/**
 * also known as BolusTerminationResponse
 */
@MessageProps(
    opCode=-95,
    size=5,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    request=CancelBolusRequest.class,
    signed=true,
    minApi=KnownApiVersion.API_V2_5
)
public class CancelBolusResponse extends Message {
    
    private int status;
    private int bolusId;
    private int reason;
    
    public CancelBolusResponse() {}
    
    public CancelBolusResponse(int status, int bolusId, int reason) {
        this.cargo = buildCargo(status, bolusId, reason);
        this.status = status;
        this.bolusId = bolusId;
        this.reason = reason;
        
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0];
        this.bolusId = Bytes.readShort(raw, 1);
        this.reason = Bytes.readShort(raw, 3);
        
    }

    
    public static byte[] buildCargo(int status, int bolusId, int reason) {
        return Bytes.combine(
            new byte[]{ (byte) status }, 
            Bytes.firstTwoBytesLittleEndian(bolusId),
            Bytes.firstTwoBytesLittleEndian(reason));
    }
    
    public int getStatus() {
        return status;
    }
    public int getBolusId() {
        return bolusId;
    }

    public int getReason() {
        return reason;
    }
}
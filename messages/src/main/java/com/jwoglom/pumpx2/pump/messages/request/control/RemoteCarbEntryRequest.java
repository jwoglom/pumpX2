package com.jwoglom.pumpx2.pump.messages.request.control;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.response.control.RemoteCarbEntryResponse;

/**
 * untested
 */
@MessageProps(
    opCode=242,
    size=9,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    response=RemoteCarbEntryResponse.class,
    minApi=KnownApiVersion.API_V2_5,
    signed=true
)
public class RemoteCarbEntryRequest extends Message { 
    private int carbs;
    private long pumpTime;
    private int bolusId;
    
    public RemoteCarbEntryRequest() {}

    public RemoteCarbEntryRequest(int carbs, long pumpTime, int bolusId) {
        this.cargo = buildCargo(carbs, pumpTime, bolusId);
        this.carbs = carbs;
        this.pumpTime = pumpTime;
        this.bolusId = bolusId;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.carbs = Bytes.readShort(raw, 0);
        this.pumpTime = Bytes.readUint32(raw, 4);
        this.bolusId = Bytes.readShort(raw, 6);
        
    }

    
    public static byte[] buildCargo(int carbs, long pumpTime, int bolusId) {
        return Bytes.combine(
            Bytes.toUint32(carbs),
            new byte[]{0, 0},
            Bytes.toUint32(pumpTime),
            Bytes.firstTwoBytesLittleEndian(bolusId)
        );
    }
    public int getCarbs() {
        return carbs;
    }
    public long getPumpTime() {
        return pumpTime;
    }
    public int getBolusId() {
        return bolusId;
    }
    
    
}
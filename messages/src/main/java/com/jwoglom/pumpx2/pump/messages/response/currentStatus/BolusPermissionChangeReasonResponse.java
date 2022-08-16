package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.BolusPermissionChangeReasonRequest;

import java.math.BigInteger;

@MessageProps(
    opCode=-87,
    size=5,
    type=MessageType.RESPONSE,
    request=BolusPermissionChangeReasonRequest.class,
    minApi=KnownApiVersion.API_FUTURE
)
public class BolusPermissionChangeReasonResponse extends Message {
    
    private int bolusId;
    private boolean isAcked;
    private int lastChangeReason;
    private boolean currentPermissionHolder;
    
    public BolusPermissionChangeReasonResponse() {}
    
    public BolusPermissionChangeReasonResponse(int bolusId, boolean isAcked, int lastChangeReason, boolean currentPermissionHolder) {
        this.cargo = buildCargo(bolusId, isAcked, lastChangeReason, currentPermissionHolder);
        this.bolusId = bolusId;
        this.isAcked = isAcked;
        this.lastChangeReason = lastChangeReason;
        this.currentPermissionHolder = currentPermissionHolder;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.bolusId = Bytes.readShort(raw, 0);
        this.isAcked = raw[2] != 0;
        this.lastChangeReason = raw[3];
        this.currentPermissionHolder = raw[4] != 0;
        
    }

    
    public static byte[] buildCargo(int bolusId, boolean isAcked, int lastChangeReason, boolean currentPermissionHolder) {
        return Bytes.combine(
            Bytes.firstTwoBytesLittleEndian(bolusId), 
            new byte[]{ (byte) (isAcked ? 1 : 0) }, 
            new byte[]{ (byte) lastChangeReason }, 
            new byte[]{ (byte) (currentPermissionHolder ? 1 : 0) });
    }
    
    public int getBolusId() {
        return bolusId;
    }
    public boolean getIsAcked() {
        return isAcked;
    }
    public int getLastChangeReason() {
        return lastChangeReason;
    }
    public boolean getCurrentPermissionHolder() {
        return currentPermissionHolder;
    }
    
}
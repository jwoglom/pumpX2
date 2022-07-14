package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.IDPSettingsRequest;

import java.math.BigInteger;

@MessageProps(
    opCode=65,
    size=23,
    type=MessageType.RESPONSE,
    request=IDPSettingsRequest.class
)
public class IDPSettingsResponse extends Message {
    
    private int idp;
    private String name;
    private int tDependentNum;
    private int insulinDuration;
    private int maxBolus;
    private boolean carbEntry;
    
    public IDPSettingsResponse() {}
    
    public IDPSettingsResponse(int idp, String name, int tDependentNum, int insulinDuration, int maxBolus, boolean carbEntry) {
        this.cargo = buildCargo(idp, name, tDependentNum, insulinDuration, maxBolus, carbEntry);
        this.idp = idp;
        this.name = name;
        this.tDependentNum = tDependentNum;
        this.insulinDuration = insulinDuration;
        this.maxBolus = maxBolus;
        this.carbEntry = carbEntry;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.idp = raw[0];
        this.name = Bytes.readString(raw, 1, 16);
        this.tDependentNum = raw[17];
        this.insulinDuration = Bytes.readShort(raw, 18);
        this.maxBolus = Bytes.readShort(raw, 20);
        this.carbEntry = raw[22] != 0;
        
    }

    
    public static byte[] buildCargo(int idp, String name, int tDependentNum, int insulinDuration, int maxBolus, boolean carbEntry) {
        return Bytes.combine(
            new byte[]{ (byte) idp }, 
            Bytes.writeString(name, 16),
            new byte[]{ (byte) tDependentNum }, 
            Bytes.firstTwoBytesLittleEndian(insulinDuration), 
            Bytes.firstTwoBytesLittleEndian(maxBolus), 
            new byte[]{ (byte) (carbEntry ? 1 : 0) });
    }
    
    public int getIdp() {
        return idp;
    }
    public String getName() {
        return name;
    }
    public int getTDependentNum() {
        return tDependentNum;
    }
    public int getInsulinDuration() {
        return insulinDuration;
    }
    public int getMaxBolus() {
        return maxBolus;
    }
    public boolean getCarbEntry() {
        return carbEntry;
    }
    
}
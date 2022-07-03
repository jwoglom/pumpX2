package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.IDPSegmentRequest;

import java.math.BigInteger;

/**
 * If the segment index is invalid, returns ErrorResponse with INVALID_PARAMETER
 */
@MessageProps(
    opCode=67,
    size=15,
    type=MessageType.RESPONSE,
    request=IDPSegmentRequest.class
)
public class IDPSegmentResponse extends Message {
    
    private int idpId;
    private int segmentIndex;
    private int profileStartTime;
    private int profileBasalRate;
    private long profileCarbRatio;
    private int profileTargetBG;
    private int profileISF;
    private int status;
    
    public IDPSegmentResponse() {}
    
    public IDPSegmentResponse(int idpId, int segmentIndex, int profileStartTime, int profileBasalRate, long profileCarbRatio, int profileTargetBG, int profileISF, int status) {
        this.cargo = buildCargo(idpId, segmentIndex, profileStartTime, profileBasalRate, profileCarbRatio, profileTargetBG, profileISF, status);
        this.idpId = idpId;
        this.segmentIndex = segmentIndex;
        this.profileStartTime = profileStartTime;
        this.profileBasalRate = profileBasalRate;
        this.profileCarbRatio = profileCarbRatio;
        this.profileTargetBG = profileTargetBG;
        this.profileISF = profileISF;
        this.status = status;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.idpId = raw[0];
        this.segmentIndex = raw[1];
        this.profileStartTime = Bytes.readShort(raw, 2);
        this.profileBasalRate = Bytes.readShort(raw, 4);
        this.profileCarbRatio = Bytes.readUint32(raw, 6);
        this.profileTargetBG = Bytes.readShort(raw, 10);
        this.profileISF = Bytes.readShort(raw, 12);
        this.status = raw[14];
        
    }

    
    public static byte[] buildCargo(int idpId, int segmentIndex, int profileStartTime, int profileBasalRate, long profileCarbRatio, int profileTargetBG, int profileISF, int status) {
        return Bytes.combine(
            new byte[]{ (byte) idpId }, 
            new byte[]{ (byte) segmentIndex }, 
            Bytes.firstTwoBytesLittleEndian(profileStartTime), 
            Bytes.firstTwoBytesLittleEndian(profileBasalRate), 
            Bytes.toUint32(profileCarbRatio), 
            Bytes.firstTwoBytesLittleEndian(profileTargetBG), 
            Bytes.firstTwoBytesLittleEndian(profileISF), 
            new byte[]{ (byte) status });
    }
    
    public int getIdpId() {
        return idpId;
    }
    public int getSegmentIndex() {
        return segmentIndex;
    }
    public int getProfileStartTime() {
        return profileStartTime;
    }
    public int getProfileBasalRate() {
        return profileBasalRate;
    }
    public long getProfileCarbRatio() {
        return profileCarbRatio;
    }
    public int getProfileTargetBG() {
        return profileTargetBG;
    }
    public int getProfileISF() {
        return profileISF;
    }
    public int getStatus() {
        return status;
    }
    
}
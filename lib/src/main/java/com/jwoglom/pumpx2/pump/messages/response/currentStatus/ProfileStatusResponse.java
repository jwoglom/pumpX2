package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ProfileStatusRequest;

import java.math.BigInteger;

/**
 * The returned idp (insulin delivery profile) slots have a value of -1 if the slot is not present.
 * Otherwise, an IDPSettingsRequest with that IDP id will return the details of that slot.
 */
@MessageProps(
    opCode=63,
    size=8,
    type=MessageType.RESPONSE,
    request=ProfileStatusRequest.class
)
public class ProfileStatusResponse extends Message {
    
    private int numberOfProfiles;
    private int idpSlot0Id;
    private int idpSlot1Id;
    private int idpSlot2Id;
    private int idpSlot3Id;
    private int idpSlot4Id;
    private int idpSlot5Id;
    private int activeSegmentIndex;
    
    public ProfileStatusResponse() {}
    
    public ProfileStatusResponse(int numberOfProfiles, int idpSlot0Id, int idpSlot1Id, int idpSlot2Id, int idpSlot3Id, int idpSlot4Id, int idpSlot5Id, int activeSegmentIndex) {
        this.cargo = buildCargo(numberOfProfiles, idpSlot0Id, idpSlot1Id, idpSlot2Id, idpSlot3Id, idpSlot4Id, idpSlot5Id, activeSegmentIndex);
        this.numberOfProfiles = numberOfProfiles;
        this.idpSlot0Id = idpSlot0Id;
        this.idpSlot1Id = idpSlot1Id;
        this.idpSlot2Id = idpSlot2Id;
        this.idpSlot3Id = idpSlot3Id;
        this.idpSlot4Id = idpSlot4Id;
        this.idpSlot5Id = idpSlot5Id;
        this.activeSegmentIndex = activeSegmentIndex;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.numberOfProfiles = raw[0];
        this.idpSlot0Id = raw[1];
        this.idpSlot1Id = raw[2];
        this.idpSlot2Id = raw[3];
        this.idpSlot3Id = raw[4];
        this.idpSlot4Id = raw[5];
        this.idpSlot5Id = raw[6];
        this.activeSegmentIndex = raw[7];
        
    }

    
    public static byte[] buildCargo(int numberOfProfiles, int idpSlot0Id, int idpSlot1Id, int idpSlot2Id, int idpSlot3Id, int idpSlot4Id, int idpSlot5Id, int activeSegmentIndex) {
        return Bytes.combine(
            new byte[]{ (byte) numberOfProfiles }, 
            new byte[]{ (byte) idpSlot0Id }, 
            new byte[]{ (byte) idpSlot1Id }, 
            new byte[]{ (byte) idpSlot2Id }, 
            new byte[]{ (byte) idpSlot3Id }, 
            new byte[]{ (byte) idpSlot4Id }, 
            new byte[]{ (byte) idpSlot5Id }, 
            new byte[]{ (byte) activeSegmentIndex });
    }
    
    public int getNumberOfProfiles() {
        return numberOfProfiles;
    }
    public int getIdpSlot0Id() {
        return idpSlot0Id;
    }
    public int getIdpSlot1Id() {
        return idpSlot1Id;
    }
    public int getIdpSlot2Id() {
        return idpSlot2Id;
    }
    public int getIdpSlot3Id() {
        return idpSlot3Id;
    }
    public int getIdpSlot4Id() {
        return idpSlot4Id;
    }
    public int getIdpSlot5Id() {
        return idpSlot5Id;
    }
    public int getActiveSegmentIndex() {
        return activeSegmentIndex;
    }
    
}
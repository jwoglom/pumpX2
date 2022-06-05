package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.IDPSegmentResponse;

@MessageProps(
    opCode=66,
    size=2,
    type=MessageType.REQUEST,
    response=IDPSegmentResponse.class
)
public class IDPSegmentRequest extends Message { 
    private int idpId;
    private int segmentIndex;
    
    public IDPSegmentRequest() {}

    public IDPSegmentRequest(int idpId, int segmentIndex) {
        this.cargo = buildCargo(idpId, segmentIndex);
        this.idpId = idpId;
        this.segmentIndex = segmentIndex;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.idpId = raw[0];
        this.segmentIndex = raw[1];
        
    }

    
    public static byte[] buildCargo(int idpId, int segmentIndex) {
        return Bytes.combine(
            new byte[]{ (byte) idpId },
            new byte[]{ (byte) segmentIndex }
        );
    }
    public int getIdpId() {
        return idpId;
    }
    public int getSegmentIndex() {
        return segmentIndex;
    }
    
    
}
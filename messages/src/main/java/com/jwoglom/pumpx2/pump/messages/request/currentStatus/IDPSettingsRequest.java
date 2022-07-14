package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.IDPSettingsResponse;

@MessageProps(
    opCode=64,
    size=1,
    type=MessageType.REQUEST,
    response=IDPSettingsResponse.class
)
public class IDPSettingsRequest extends Message { 
    private int idpId;
    
    public IDPSettingsRequest() {}

    public IDPSettingsRequest(int idpId) {
        this.cargo = buildCargo(idpId);
        this.idpId = idpId;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.idpId = raw[0];
        
    }

    
    public static byte[] buildCargo(int idpId) {
        return Bytes.combine(
            Bytes.firstByteLittleEndian(idpId)
        );
    }
    public int getIdpId() {
        return idpId;
    }
    
    
}
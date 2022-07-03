package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.BasalIQSettingsRequest;

import java.math.BigInteger;

@MessageProps(
    opCode=99,
    size=3,
    type=MessageType.RESPONSE,
    request=BasalIQSettingsRequest.class
)
public class BasalIQSettingsResponse extends Message {
    
    private int hypoMinimization;
    private int suspendAlert;
    private int resumeAlert;
    
    public BasalIQSettingsResponse() {}
    
    public BasalIQSettingsResponse(int hypoMinimization, int suspendAlert, int resumeAlert) {
        this.cargo = buildCargo(hypoMinimization, suspendAlert, resumeAlert);
        this.hypoMinimization = hypoMinimization;
        this.suspendAlert = suspendAlert;
        this.resumeAlert = resumeAlert;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.hypoMinimization = raw[0];
        this.suspendAlert = raw[1];
        this.resumeAlert = raw[2];
        
    }

    
    public static byte[] buildCargo(int hypoMinimization, int suspendAlert, int resumeAlert) {
        return Bytes.combine(
            new byte[]{ (byte) hypoMinimization }, 
            new byte[]{ (byte) suspendAlert }, 
            new byte[]{ (byte) resumeAlert });
    }
    
    public int getHypoMinimization() {
        return hypoMinimization;
    }
    public int getSuspendAlert() {
        return suspendAlert;
    }
    public int getResumeAlert() {
        return resumeAlert;
    }
    
}
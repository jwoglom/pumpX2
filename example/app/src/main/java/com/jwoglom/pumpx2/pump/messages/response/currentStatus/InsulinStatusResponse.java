package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.InsulinStatusRequest;

@MessageProps(
    opCode=37,
    size=4,
    type=MessageType.RESPONSE,
    request=InsulinStatusRequest.class
)
public class InsulinStatusResponse extends Message {
    
    private int currentInsulinAmount;
    private int isEstimate;
    private int insulinLowAmount;
    
    public InsulinStatusResponse() {}
    
    public InsulinStatusResponse(int currentInsulinAmount, int isEstimate, int insulinLowAmount) {
        this.cargo = buildCargo(currentInsulinAmount, isEstimate, insulinLowAmount);
        this.currentInsulinAmount = currentInsulinAmount;
        this.isEstimate = isEstimate;
        this.insulinLowAmount = insulinLowAmount;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.currentInsulinAmount = Bytes.readShort(raw, 0);
        this.isEstimate = raw[2];
        this.insulinLowAmount = raw[3];
        
    }

    
    public static byte[] buildCargo(int currentInsulinAmount, int isEstimate, int insulinLowAmount) {
        return Bytes.combine(
            Bytes.firstTwoBytesLittleEndian(currentInsulinAmount), 
            new byte[]{ (byte) isEstimate }, 
            new byte[]{ (byte) insulinLowAmount });
    }
    
    public int getCurrentInsulinAmount() {
        return currentInsulinAmount;
    }
    public int getIsEstimate() {
        return isEstimate;
    }
    public int getInsulinLowAmount() {
        return insulinLowAmount;
    }
    
}
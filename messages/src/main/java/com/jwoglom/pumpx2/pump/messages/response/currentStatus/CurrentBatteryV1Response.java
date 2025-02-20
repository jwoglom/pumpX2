package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.ApiVersionDependent;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.builders.CurrentBatteryRequestBuilder;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CurrentBatteryV1Request;

/**
 * Pump API version dependent request, do NOT invoke directly.
 * For API_V2_1
 *
 * @see CurrentBatteryRequestBuilder
 */
@MessageProps(
    opCode=53,
    size=2,
    type=MessageType.RESPONSE,
    request=CurrentBatteryV1Request.class
)
@ApiVersionDependent
public class CurrentBatteryV1Response extends CurrentBatteryAbstractResponse {
    
    private int currentBatteryAbc;
    private int currentBatteryIbc;
    
    public CurrentBatteryV1Response() {}
    
    public CurrentBatteryV1Response(int currentBatteryAbc, int currentBatteryIbc) {
        this.cargo = buildCargo(currentBatteryAbc, currentBatteryIbc);
        this.currentBatteryAbc = currentBatteryAbc;
        this.currentBatteryIbc = currentBatteryIbc;
        
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.currentBatteryAbc = raw[0];
        this.currentBatteryIbc = raw[1];
        
    }

    
    public static byte[] buildCargo(int currentBatteryAbc, int currentBatteryIbc) {
        return Bytes.combine(
            new byte[]{ (byte) currentBatteryAbc }, 
            new byte[]{ (byte) currentBatteryIbc });
    }
    
    public int getCurrentBatteryAbc() {
        return currentBatteryAbc;
    }
    public int getCurrentBatteryIbc() {
        return currentBatteryIbc;
    }
    
}
package com.jwoglom.pumpx2.pump.messages.request.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.response.control.CancelBolusResponse;

/**
 * also known as BolusTerminationRequest.
 * Can be used on boluses which are initiated from the phone as well as the pump.
 * Works for extended boluses.
 */
@MessageProps(
    opCode=-96,
    size=4, // 28 with 24 byte padding
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    response=CancelBolusResponse.class,
    signed=true,
    minApi=KnownApiVersion.API_V2_5
)
public class CancelBolusRequest extends Message { 
    private int bolusId;
    
    public CancelBolusRequest() {}

    public CancelBolusRequest(int bolusId) {
        this.cargo = buildCargo(bolusId);
        this.bolusId = bolusId;
        
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.bolusId = Bytes.readShort(raw, 0);
        
    }
    
    public static byte[] buildCargo(int bolusId) {
        return Bytes.combine(
            Bytes.firstTwoBytesLittleEndian(bolusId),
            new byte[]{0, 0}
        );
    }

    public int getBolusId() {
        return bolusId;
    }
}
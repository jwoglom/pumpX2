package com.jwoglom.pumpx2.pump.messages.response.control;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.models.SupportedDevices;
import com.jwoglom.pumpx2.pump.messages.request.control.SetTempRateRequest;

import java.math.BigInteger;

@MessageProps(
    opCode=-91,
    size=4,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    signed=true,
    minApi=KnownApiVersion.MOBI_API_V3_5,
    supportedDevices=SupportedDevices.MOBI_ONLY,
    request=SetTempRateRequest.class
)
public class SetTempRateResponse extends Message {

    private int status;
    private int id;


    public SetTempRateResponse() {}
    
    public SetTempRateResponse(byte[] raw) {
        parse(raw);
    }

    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0];
        this.id = raw[1];
        
    }

    
    public static byte[] buildCargo(byte[] raw) {
        return Bytes.combine(
            raw);
    }


    /**
     * @return 0 if successful
     */
    public int getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }
}
package com.jwoglom.pumpx2.pump.messages.response.control;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.models.SupportedDevices;
import com.jwoglom.pumpx2.pump.messages.request.control.StopTempRateRequest;

import java.math.BigInteger;

@MessageProps(
    opCode=-89,
    size=3,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    signed=true,
    minApi=KnownApiVersion.MOBI_API_V3_5,
    supportedDevices=SupportedDevices.MOBI_ONLY,
    request=StopTempRateRequest.class
)
public class StopTempRateResponse extends Message {

    private int bit1;
    private int bit2;
    private int bit3;
    
    public StopTempRateResponse() {
    }

    public StopTempRateResponse(byte[] raw) {
        parse(raw);
    }

    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.bit1 = raw[0];
        this.bit2 = raw[1];
        this.bit3 = raw[2];
        
    }

    public int getBit1() {
        return bit1;
    }

    public int getBit2() {
        return bit2;
    }

    public int getBit3() {
        return bit3;
    }
}
package com.jwoglom.pumpx2.pump.messages.request.control;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.response.control.DisconnectPumpResponse;

/*
 * I don't actually think calling this command is required... since an app can just disconnect
 * from the device over Bluetooth and forget the pairing key, and the user needs to re-trigger
 * a pair in order to connect again anyway (either via menu on tslim X2 or entering "pairing mode"
 * on the Mobi)
 */
@MessageProps(
    opCode=-66,
    size=0,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    signed=true,
    response=DisconnectPumpResponse.class,
    minApi=KnownApiVersion.MOBI_API_V3_5
)
public class DisconnectPumpRequest extends Message { 
    public DisconnectPumpRequest() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        
    }

    
}
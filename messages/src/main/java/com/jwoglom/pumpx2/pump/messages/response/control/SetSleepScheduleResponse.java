package com.jwoglom.pumpx2.pump.messages.response.control;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.models.SupportedDevices;
import com.jwoglom.pumpx2.pump.messages.request.control.SetSleepScheduleRequest;

import java.math.BigInteger;

@MessageProps(
    opCode=-49,
    size=1,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    signed=true,
    minApi=KnownApiVersion.MOBI_API_V3_5,
    supportedDevices=SupportedDevices.MOBI_ONLY,
    request=SetSleepScheduleRequest.class
)
public class SetSleepScheduleResponse extends Message {

    private int status;
    
    public SetSleepScheduleResponse() {}
    
    public SetSleepScheduleResponse(byte[] raw) {
        parse(raw);
    }

    public SetSleepScheduleResponse(int status) {
        parse(buildCargo(status));
    }

    public void parse(byte[] raw) {
        raw = this.removeSignedRequestHmacBytes(raw);
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0];
        
    }

    
    public static byte[] buildCargo(int status) {
        return Bytes.combine(
            new byte[]{ (byte) status }
        );
    }

    /**
     * @return 0 if successful
     */
    public int getStatus() {
        return status;
    }
}
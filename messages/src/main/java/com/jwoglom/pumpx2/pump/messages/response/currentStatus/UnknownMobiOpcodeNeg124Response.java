package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.models.StatusMessage;
import com.jwoglom.pumpx2.pump.messages.models.SupportedDevices;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.UnknownMobiOpcodeNeg124Request;

@MessageProps(
    opCode=-123,
    size=1, // 25 with trailer
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    signed=true,
    minApi=KnownApiVersion.MOBI_API_V3_5,
    supportedDevices=SupportedDevices.MOBI_ONLY,
    request=UnknownMobiOpcodeNeg124Request.class
)
public class UnknownMobiOpcodeNeg124Response extends StatusMessage {

    private int status;
    
    public UnknownMobiOpcodeNeg124Response() {}

    public UnknownMobiOpcodeNeg124Response(int status) {
        this.cargo = buildCargo(status);
    }

    public UnknownMobiOpcodeNeg124Response(byte[] raw) {
        parse(raw);
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0];
        
    }

    
    public static byte[] buildCargo(int status) {
        return new byte[]{(byte) status};
    }


    public int getStatus() {
        return status;
    }
}
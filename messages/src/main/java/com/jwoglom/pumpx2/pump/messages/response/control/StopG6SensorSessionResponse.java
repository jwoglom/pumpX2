package com.jwoglom.pumpx2.pump.messages.response.control;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.control.StopG6SensorSessionRequest;

import java.math.BigInteger;

@MessageProps(
    opCode=-75,
    size=1,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    signed=true,
    request=StopG6SensorSessionRequest.class
)
public class StopG6SensorSessionResponse extends Message {

    private int status;
    
    public StopG6SensorSessionResponse() {
        this.cargo = EMPTY;
    }

    public StopG6SensorSessionResponse(int status) {
        this.cargo = buildCargo(status);
        this.status = status;
    }

    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0];
    }

    public static byte[] buildCargo(int status) {
        return Bytes.combine(
                Bytes.firstByteLittleEndian(status)
        );
    }


    /**
     * @return 0 if successful
     */
    public int getStatus() {
        return status;
    }
}
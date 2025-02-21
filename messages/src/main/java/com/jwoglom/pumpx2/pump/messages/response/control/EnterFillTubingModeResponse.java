package com.jwoglom.pumpx2.pump.messages.response.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.StatusMessage;
import com.jwoglom.pumpx2.pump.messages.request.control.EnterFillTubingModeRequest;

import java.math.BigInteger;

@MessageProps(
    opCode=-107,
    size=1,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    signed=true,
    modifiesInsulinDelivery=true,
    request=EnterFillTubingModeRequest.class
)
public class EnterFillTubingModeResponse extends StatusMessage {

    private int status;
    
    public EnterFillTubingModeResponse() {
        this.cargo = EMPTY;
    }

    public EnterFillTubingModeResponse(int status) {
        this.cargo = buildCargo(status);
        this.status = status;
    }

    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
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
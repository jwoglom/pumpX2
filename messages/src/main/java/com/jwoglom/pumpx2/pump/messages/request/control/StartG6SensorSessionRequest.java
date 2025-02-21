package com.jwoglom.pumpx2.pump.messages.request.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.StartG6SensorSessionResponse;

@MessageProps(
    opCode=-78,
    size=2,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    signed=true,
    response=StartG6SensorSessionResponse.class
)
public class StartG6SensorSessionRequest extends Message {
    public static final int NO_CODE = 0;

    private int sensorCode;
    
    public StartG6SensorSessionRequest() {
        this.cargo = buildCargo(NO_CODE);
        this.sensorCode = NO_CODE;
    }

    public StartG6SensorSessionRequest(int sensorCode) {
        this.cargo = buildCargo(sensorCode);
        this.sensorCode = sensorCode;
    }

    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.sensorCode = Bytes.readShort(raw, 0);
        
    }
    
    public static byte[] buildCargo(int sensorCode) {
        return Bytes.combine(
            Bytes.firstTwoBytesLittleEndian(sensorCode)
        );
    }

    public int getSensorCode() {
        return sensorCode;
    }
    
    
}
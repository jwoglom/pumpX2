package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.NotificationMessage;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.OtherNotification2StatusRequest;

@MessageProps(
    opCode=119,
    size=8,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CURRENT_STATUS,
    request=OtherNotification2StatusRequest.class
)
public class OtherNotification2StatusResponse extends NotificationMessage {
    
    private long codeA;
    private long codeB;
    
    public OtherNotification2StatusResponse() {}
    
    public OtherNotification2StatusResponse(long codeA, long codeB) {
        this.cargo = buildCargo(codeA, codeB);
        parse(cargo);
        
    }

    public void parse(byte[] raw) { 
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.codeA = Bytes.readUint32(raw, 0);
        this.codeB = Bytes.readUint32(raw, 4);
        
    }

    
    public static byte[] buildCargo(long codeA, long codeB) {
        return Bytes.combine(
            Bytes.toUint32(codeA),
            Bytes.toUint32(codeB)
        );
    }

    public long getCodeA() {
        return codeA;
    }

    public long getCodeB() {
        return codeB;
    }

    @Override
    public int size() {
        return (codeA==0 && codeB==0) ? 0 : 1;
    }
}
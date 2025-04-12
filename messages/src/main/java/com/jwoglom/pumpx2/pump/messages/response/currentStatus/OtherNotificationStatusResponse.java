package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.models.NotificationMessage;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.OtherNotificationStatusRequest;

import java.math.BigInteger;

@MessageProps(
    opCode=-109,
    size=17,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CURRENT_STATUS,
    request=OtherNotificationStatusRequest.class,
    minApi=KnownApiVersion.MOBI_API_V3_5
)
public class OtherNotificationStatusResponse extends NotificationMessage {
    
    
    public OtherNotificationStatusResponse() {
        this.cargo = EMPTY;
    }

    public OtherNotificationStatusResponse(byte[] raw) {
        this.cargo = raw;
        parse(raw);
    }

    public void parse(byte[] raw) { 
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        
    }


    @Override
    public int size() {
        return 0; // not sure about this one
    }
}
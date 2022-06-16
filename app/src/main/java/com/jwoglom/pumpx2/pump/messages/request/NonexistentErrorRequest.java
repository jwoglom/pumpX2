package com.jwoglom.pumpx2.pump.messages.request;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.ErrorResponse;

/**
 * This message is used as a paired request message for responses which can occur
 * either in response to any request message (e.g. ErrorResponse).
 */
@MessageProps(
        opCode=-99999999,
        size=0,
        type=MessageType.REQUEST,
        response=ErrorResponse.class
)
public class NonexistentErrorRequest extends Message {

    public NonexistentErrorRequest() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        this.cargo = raw;
    }

}
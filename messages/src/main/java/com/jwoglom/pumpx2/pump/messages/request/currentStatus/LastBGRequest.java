package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.LastBGResponse;

/**
 * Returns the most recent BG entry which was entered in the Bolus Calculator. Does NOT return
 * the most recent CGM reading -- you must browse through the HistoryLog to find that.
 */
@MessageProps(
    opCode=50,
    size=0,
    type=MessageType.REQUEST,
    response=LastBGResponse.class
)
public class LastBGRequest extends Message { 
    public LastBGRequest() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
    }

    
}
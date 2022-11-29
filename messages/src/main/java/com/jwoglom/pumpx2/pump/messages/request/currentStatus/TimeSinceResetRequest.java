package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.TimeSinceResetResponse;

/**
 * Returns information on the pump's current internal time. Different from the human-visible
 * time and date which is stored in the history log.
 *
 * When the Android libary is used, this message is invoked automatically by PumpX2 on connection
 * with the pump so that the state can be tracked globally via PumpState.getPumpTimeSinceReset.
 * Due to this, if called explicitly via the Android library, a callback will not be returned.
 */
@MessageProps(
    opCode=54,
    size=0,
    type=MessageType.REQUEST,
    response=TimeSinceResetResponse.class
)
public class TimeSinceResetRequest extends Message { 
    public TimeSinceResetRequest() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        
    }

    
}
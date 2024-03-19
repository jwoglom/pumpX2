package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ApiVersionResponse;

/**
 * Returns the major and minor API version of the pump.
 *
 * When the Android libary is used, this message is invoked automatically by PumpX2 on connection
 * with the pump so that the state can be tracked globally via PumpState.getPumpAPIVersion.
 * Due to this, if called explicitly via the Android library, a callback will not be returned.
 */
@MessageProps(
    opCode=32,
    size=2, // or 0
    variableSize=true,
    type=MessageType.REQUEST,
    response=ApiVersionResponse.class
)
public class ApiVersionRequest extends Message {
    public ApiVersionRequest() {
        this.cargo = EMPTY;
    }

    public ApiVersionRequest(byte[] cargo) {
        this.cargo = cargo;
    }

    public void parse(byte[] raw) {
        // empty cargo is ok
        if (raw.length == 0) return;

        Preconditions.checkArgument(raw.length == props().size(), "got length "+raw.length);
        this.cargo = Bytes.dropFirstN(raw, 3);
    }
}

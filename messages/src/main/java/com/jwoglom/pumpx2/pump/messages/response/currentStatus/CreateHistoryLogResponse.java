package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CreateHistoryLogRequest;

import org.apache.commons.lang3.Validate;

@MessageProps(
    opCode=127,
    size=1,
    type=MessageType.RESPONSE,
    request=CreateHistoryLogRequest.class
)
public class CreateHistoryLogResponse extends Message {
    private int status;

    public CreateHistoryLogResponse() {}

    public CreateHistoryLogResponse(int status) {
        this.cargo = buildCargo(status);
        this.status = status;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0];
    }

    public static byte[] buildCargo(int status) {
        return new byte[]{ (byte) status };
    }

    /**
     * @return 0 if successful
     */
    public int getStatus() {
        return status;
    }
}

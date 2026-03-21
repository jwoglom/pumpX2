package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CreateHistoryLogResponse;

import org.apache.commons.lang3.Validate;

@MessageProps(
    opCode=126,
    size=4,
    type=MessageType.REQUEST,
    response=CreateHistoryLogResponse.class
)
public class CreateHistoryLogRequest extends Message {
    private long numberOfLogs;

    public CreateHistoryLogRequest() {}

    public CreateHistoryLogRequest(long numberOfLogs) {
        this.cargo = buildCargo(numberOfLogs);
        this.numberOfLogs = numberOfLogs;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.numberOfLogs = Bytes.readUint32(raw, 0);
    }

    public static byte[] buildCargo(long numberOfLogs) {
        return Bytes.toUint32(numberOfLogs);
    }

    public long getNumberOfLogs() {
        return numberOfLogs;
    }
}

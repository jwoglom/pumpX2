package com.jwoglom.pumpx2.pump.messages.request.historyLog;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.HistoryLogStreamResponse;

/**
 * This message is used as a paired request message for HistoryLogStreamResponse,
 * which does not have an originating request.
 */
@MessageProps(
        opCode=0,
        size=0,
        type=MessageType.REQUEST,
        characteristic=Characteristic.HISTORY_LOG,
        response=HistoryLogStreamResponse.class
)
public class NonexistentHistoryLogStreamRequest extends Message {

    public NonexistentHistoryLogStreamRequest() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        this.cargo = raw;
    }

}
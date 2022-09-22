package com.jwoglom.pumpx2.pump.messages.request.controlStream;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.response.controlStream.PumpingStateStreamResponse;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.HistoryLogStreamResponse;

/**
 * This message is used as a paired request message for PumpingStateStreamResponse,
 * which does not have an originating request.
 */
@MessageProps(
        opCode=0, // nonexistent
        size=0,
        type=MessageType.REQUEST,
        characteristic=Characteristic.HISTORY_LOG,
        response=PumpingStateStreamResponse.class
)
public class NonexistentPumpingStateStreamRequest extends Message {

    public NonexistentPumpingStateStreamRequest() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        this.cargo = raw;
    }

}
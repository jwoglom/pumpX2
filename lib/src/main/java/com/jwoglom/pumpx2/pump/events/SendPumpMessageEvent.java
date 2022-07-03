package com.jwoglom.pumpx2.pump.events;

import com.jwoglom.pumpx2.pump.messages.Message;

public class SendPumpMessageEvent {
    private final Message message;
    public SendPumpMessageEvent(Message message) {
        this.message = message;
    }

    public Message message() {
        return message;
    }

    public String toString() {
        return "SendPumpMessageEvent(message=" + message + ")";
    }
}

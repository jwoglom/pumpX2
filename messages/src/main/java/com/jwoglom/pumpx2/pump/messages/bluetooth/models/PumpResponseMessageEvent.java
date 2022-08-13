package com.jwoglom.pumpx2.pump.messages.bluetooth.models;

import com.jwoglom.pumpx2.pump.messages.Message;

import org.apache.commons.codec.binary.Hex;

import java.util.Optional;

public class PumpResponseMessageEvent {
    private final byte[] data;
    private final Optional<Message> message;

    public PumpResponseMessageEvent(byte[] data) {
        this.data = data;
        this.message = Optional.empty();
    }
    public PumpResponseMessageEvent(byte[] data, Message message) {
        this.data = data;
        this.message = Optional.of(message);
    }

    public byte[] data() {
        return data;
    }

    public Optional<Message> message() {
        return message;
    }

    public String toString() {
        return "PumpResponseMessageEvent(data=" + Hex.encodeHexString(data) + ", message=" + message + ")";
    }
}

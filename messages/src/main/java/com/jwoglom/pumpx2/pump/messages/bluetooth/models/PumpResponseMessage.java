package com.jwoglom.pumpx2.pump.messages.bluetooth.models;

import com.jwoglom.pumpx2.pump.messages.Message;

import com.jwoglom.pumpx2.shared.Hex;

import java.util.Optional;

public class PumpResponseMessage {
    private final byte[] data;
    private final Optional<Message> message;

    public PumpResponseMessage(byte[] data) {
        this.data = data;
        this.message = Optional.empty();
    }
    public PumpResponseMessage(byte[] data, Message message) {
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
        return "PumpResponseMessage(data=" + Hex.encodeHexString(data) + ", message=" + message + ")";
    }
}

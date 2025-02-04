package com.jwoglom.pumpx2.pump.messages.bluetooth.models;

import com.jwoglom.pumpx2.pump.messages.Message;

import com.jwoglom.pumpx2.pump.messages.response.qualifyingEvent.QualifyingEvent;
import com.jwoglom.pumpx2.shared.Hex;

import java.util.Optional;
import java.util.Set;

public class PumpResponseMessage {
    private final byte[] data;
    private final Optional<Message> message;
    private final Optional<Set<QualifyingEvent>> qualifyingEvents;

    public PumpResponseMessage(byte[] data) {
        this.data = data;
        this.message = Optional.empty();
        this.qualifyingEvents = Optional.empty();
    }
    public PumpResponseMessage(byte[] data, Message message) {
        this.data = data;
        this.message = Optional.of(message);
        this.qualifyingEvents = Optional.empty();
    }
    public PumpResponseMessage(byte[] data, Set<QualifyingEvent> qualifyingEvents) {
        this.data = data;
        this.message = Optional.empty();
        this.qualifyingEvents = Optional.of(qualifyingEvents);
    }

    public byte[] data() {
        return data;
    }

    public Optional<Message> message() {
        return message;
    }

    public Optional<Set<QualifyingEvent>> qualifyingEvents() {
        return qualifyingEvents;
    }

    public String toString() {
        return "PumpResponseMessage(data=" + Hex.encodeHexString(data) + ", message=" + message + ", event=" + qualifyingEvents + ")";
    }
}

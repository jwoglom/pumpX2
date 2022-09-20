package com.jwoglom.pumpx2.pump.messages;

public enum MessageType {
    REQUEST,
    RESPONSE,

    ;

    public static MessageType fromOpcodeBestEffort(int opcode) {
        if (opcode % 2 == 0) {
            return REQUEST;
        } else {
            return RESPONSE;
        }
    }
}

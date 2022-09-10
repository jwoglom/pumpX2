package com.jwoglom.pumpx2.pump.messages.models;

public class UnexpectedOpCodeException extends IllegalArgumentException {
    public int foundOpcode;
    public UnexpectedOpCodeException(int foundOpcode, int expectedOpCode) {
        super("Unexpected opcode: " + foundOpcode + ", expecting " + expectedOpCode);
        this.foundOpcode = foundOpcode;
    }
}

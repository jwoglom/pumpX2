package com.jwoglom.pumpx2.pump.messages;

import org.apache.commons.lang3.NotImplementedException;

public class UndefinedMessage extends Message {
    public UndefinedMessage() {}

    @Override
    public void parse(byte[] raw) {
        throw new NotImplementedException();
    }
}

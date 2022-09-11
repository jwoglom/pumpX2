package com.jwoglom.pumpx2.pump.messages.response.controlStream;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.Messages;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;

public class ControlStreamMessages {
    public static Message determineRequestMessage(byte[] rawBtValue) throws InstantiationException, IllegalAccessException {
        Preconditions.checkArgument(rawBtValue.length >= 3);
        byte opCode = rawBtValue[2];
        return Messages.fromOpcode(opCode, Characteristic.CONTROL_STREAM).newInstance();
    }
}

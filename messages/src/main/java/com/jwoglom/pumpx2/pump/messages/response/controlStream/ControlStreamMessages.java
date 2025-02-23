package com.jwoglom.pumpx2.pump.messages.response.controlStream;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.Messages;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;

public class ControlStreamMessages {
    public static Message determineRequestMessage(byte[] rawBtValue) throws InstantiationException, IllegalAccessException {
        Validate.isTrue(rawBtValue.length >= 3);
        byte opCode = rawBtValue[2];

        Message responseMessage = Messages.fromOpcode(opCode, Characteristic.CONTROL_STREAM).newInstance();
        return responseMessage.getRequestClass().newInstance();
    }
}

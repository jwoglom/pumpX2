package com.jwoglom.pumpx2.pump.messages.builders;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ControlIQIOBRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.NonControlIQIOBRequest;

public class IOBRequestBuilder {
    public static Message create(boolean controlIQ) {
        if (controlIQ) {
            return new ControlIQIOBRequest();
        } else {
            return new NonControlIQIOBRequest();
        }
    }
}

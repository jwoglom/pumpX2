package com.jwoglom.pumpx2.pump.messages.models;

import com.jwoglom.pumpx2.pump.messages.Message;

public abstract class NotificationMessage extends Message {

    public boolean isEmpty() {
        return size()==0;
    }

    public abstract int size();

}

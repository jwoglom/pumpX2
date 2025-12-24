package com.jwoglom.pumpx2.pump.messages.models;

import com.jwoglom.pumpx2.pump.messages.Message;

import java.util.HashSet;
import java.util.Set;

public abstract class NotificationMessage extends Message {

    public boolean isEmpty() {
        return size()==0;
    }

    public abstract int size();

    /**
     * @return notification IDs which, if present in
     * {@link com.jwoglom.pumpx2.pump.messages.response.currentStatus.MalfunctionStatusResponse}
     * should hide the associated malfunction response
     */
    public Set<Integer> notificationIds() {
        return new HashSet<>();
    }

}

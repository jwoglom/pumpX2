package com.jwoglom.pumpx2.pump.messages.models;

import com.jwoglom.pumpx2.pump.messages.Message;

/**
 * Wrapper around Message which includes a field getStatus()
 * for general status processing across response messages.
 * A status of 0 means success, any other response indicates an error
 * performing the associated command.
 */
public abstract class StatusMessage extends Message {

    /**
     * @return 0 if successful
     */
    public abstract int getStatus();

    /**
     * @return true if successful
     */
    public boolean isStatusOK() {
        return getStatus() == 0;
    }
}

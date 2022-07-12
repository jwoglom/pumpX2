package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.jwoglom.pumpx2.pump.messages.Message;

public abstract class CurrentBatteryAbstractResponse extends Message {
    public abstract int getCurrentBatteryAbc();
    public abstract int getCurrentBatteryIbc();
}

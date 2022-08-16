package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.jwoglom.pumpx2.pump.messages.Message;

/**
 * Abstraction around {@link CurrentBatteryV1Response} {@link CurrentBatteryV2Response}
 *
 * For the "normal", pump displayed battery percent use getBatteryPercent
 *
 * @see com.jwoglom.pumpx2.pump.messages.builders.CurrentBatteryBuilder
 */
public abstract class CurrentBatteryAbstractResponse extends Message {
    public abstract int getCurrentBatteryAbc();
    public abstract int getCurrentBatteryIbc();

    public int getBatteryPercent() {
        return getCurrentBatteryIbc();
    }

}

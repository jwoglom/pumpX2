package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.builders.CurrentBatteryRequestBuilder;

/**
 * Abstraction around {@link CurrentBatteryV1Response} {@link CurrentBatteryV2Response}
 *
 * For the "normal", pump displayed battery percent use getBatteryPercent
 *
 * @see CurrentBatteryRequestBuilder
 */
public abstract class CurrentBatteryAbstractResponse extends Message {
    /**
     * @return a second 0-100 charge figure, not the displayed one; DailyBasalHistoryLog logs this
     * one on Mobi (#149)
     */
    public abstract int getCurrentBatteryAbc();

    /**
     * @return charge remaining, 0-100, as the pump displays it
     */
    public abstract int getCurrentBatteryIbc();

    public int getBatteryPercent() {
        return getCurrentBatteryIbc();
    }

}

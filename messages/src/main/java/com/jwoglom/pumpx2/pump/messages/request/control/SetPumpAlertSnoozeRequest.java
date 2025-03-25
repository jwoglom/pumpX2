package com.jwoglom.pumpx2.pump.messages.request.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.SetPumpAlertSnoozeResponse;


/**
 * Configures whether alert snoozing is enabled on the Mobi (triple-press the button to snooze an alert)
 * and if so for what duration (in minutes).
 */
@MessageProps(
    opCode=-44,
    size=2,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    signed=true,
    response=SetPumpAlertSnoozeResponse.class
)
public class SetPumpAlertSnoozeRequest extends Message {
    private boolean snoozeEnabled;
    private int snoozeDurationMins;
    
    public SetPumpAlertSnoozeRequest() {}

    /**
     * @param snoozeEnabled if snooze is enabled
     * @param snoozeDurationMins if snooze disabled, '0'; otherwise '10' or '20'
     */
    public SetPumpAlertSnoozeRequest(boolean snoozeEnabled, int snoozeDurationMins) {
        this.cargo = buildCargo(snoozeEnabled, snoozeDurationMins);
        this.snoozeEnabled = snoozeEnabled;
        this.snoozeDurationMins = snoozeDurationMins;
        
    }

    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.snoozeEnabled = raw[0] == 1;
        this.snoozeDurationMins = raw[1] % 255;
        
    }

    
    public static byte[] buildCargo(boolean snoozeEnabled, int snoozeDurationMins) {
        return Bytes.combine(
            new byte[]{ (byte) (snoozeEnabled ? 1 : 0) },
            new byte[]{ (byte) snoozeDurationMins }
        );
    }
    public boolean isSnoozeEnabled() {
        return snoozeEnabled;
    }
    public boolean getSnoozeEnabled() {
        return snoozeEnabled;
    }
    public int getSnoozeDurationMins() {
        return snoozeDurationMins;
    }


}
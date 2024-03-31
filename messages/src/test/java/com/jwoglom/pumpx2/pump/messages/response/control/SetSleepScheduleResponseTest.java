package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class SetSleepScheduleResponseTest {
    @Test
    public void testSetSleepScheduleResponse_mobi() throws DecoderException {
        // TimeSinceResetResponse[currentTime=512443266,pumpTimeSinceReset=1905836,cargo={-126,67,-117,30,-84,20,29,0}]
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1905836L);
        
        SetSleepScheduleResponse expected = new SetSleepScheduleResponse(
            new byte[]{0}
        );

        SetSleepScheduleResponse parsedRes = (SetSleepScheduleResponse) MessageTester.test(
                // Untitled_2_Live_-_Humans_iPhone
                "00a2cfa2190099438b1e67bcbb479d9ba61f4ce07e3bfebf2305b87092ed11b4",
                -94,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(0, parsedRes.getStatus());
    }
}
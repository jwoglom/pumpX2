package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class SetMaxBolusLimitResponseTest {
    @Test
    public void testSetDeliveryLimitsResponse() throws DecoderException { 
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);
        
        SetMaxBolusLimitResponse expected = new SetMaxBolusLimitResponse(
            0
        );

        SetMaxBolusLimitResponse parsedRes = (SetMaxBolusLimitResponse) MessageTester.test(
                "0061876119007de14920248f42cba27ec99fd6e97b48b5e464ad29691821c4eb",
                97,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
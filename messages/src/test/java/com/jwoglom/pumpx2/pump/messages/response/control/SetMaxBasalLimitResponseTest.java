package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class SetMaxBasalLimitResponseTest {
    @Test
    public void testSetMaxBasalLimitResponse() throws DecoderException { 
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);
        
        SetMaxBasalLimitResponse expected = new SetMaxBasalLimitResponse(
            0
        );

        SetMaxBasalLimitResponse parsedRes = (SetMaxBasalLimitResponse) MessageTester.test(
                "00758975190029e34920e1e6145d9ec4ced8f764873318b61dac15b7e3fee55e",
                117,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
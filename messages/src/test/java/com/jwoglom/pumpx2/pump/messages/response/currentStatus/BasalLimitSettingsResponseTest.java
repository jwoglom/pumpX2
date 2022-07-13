package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class BasalLimitSettingsResponseTest {
    @Test
    public void testBasalLimitSettingsRequestResponse() throws DecoderException {
        BasalLimitSettingsResponse expected = new BasalLimitSettingsResponse(
            // long basalLimit, long basalLimitDefault
            5000L, 3000L
        );

        BasalLimitSettingsResponse parsedRes = (BasalLimitSettingsResponse) MessageTester.test(
                "00038b030888130000b80b0000a097",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
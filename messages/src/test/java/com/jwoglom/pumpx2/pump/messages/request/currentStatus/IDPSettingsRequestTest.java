package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.IDPSettingsRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class IDPSettingsRequestTest {
    @Test
    public void testIDPSettingsRequest() throws DecoderException {
        IDPSettingsRequest expected = new IDPSettingsRequest(0);

        IDPSettingsRequest parsedReq = (IDPSettingsRequest) MessageTester.test(
                "000440040100ad05",
                4,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}
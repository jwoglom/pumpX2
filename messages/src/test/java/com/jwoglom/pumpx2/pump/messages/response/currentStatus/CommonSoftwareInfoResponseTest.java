package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CommonSoftwareInfoResponseTest {
    @Test
    public void testCommonSoftwareInfoResponse() throws DecoderException { 
        CommonSoftwareInfoResponse expected = new CommonSoftwareInfoResponse(
            // int appSoftwareVersion, long appSoftwarePartNumber, long appSoftwareDashNumber, long appSoftwarePartRevisionNumber, int bootloaderVersion, long bootloaderPartNumber, long bootloaderPartDashNumber, long bootloaderPartRevisionNumber
        );

        CommonSoftwareInfoResponse parsedRes = (CommonSoftwareInfoResponse) MessageTester.test(
                "xxxx",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
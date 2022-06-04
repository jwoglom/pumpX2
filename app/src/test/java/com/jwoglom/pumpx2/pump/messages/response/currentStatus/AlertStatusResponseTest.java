package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.AlertStatusResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import java.math.BigInteger;

public class AlertStatusResponseTest {
    @Test
    public void testAlertStatusEmpty() throws DecoderException {
        // empty cargo
        AlertStatusResponse expected = new AlertStatusResponse(BigInteger.ZERO);

        AlertStatusResponse parsedReq = (AlertStatusResponse) MessageTester.test(
                "000545050800000000000000005bf2",
                5,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        MessageTester.assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

    @Test
    public void testAlertStatusWithDevicePaired() throws DecoderException {
        // empty cargo
        AlertStatusResponse expected = new AlertStatusResponse(
                AlertStatusResponse.AlertResponseType.DEVICE_PAIRED.withBit());

        AlertStatusResponse parsedReq = (AlertStatusResponse) MessageTester.test(
                "00034503080000000000004000288c",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        MessageTester.assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

    @Test
    public void testAlertStatusWithIncompleteCartridgeChange() throws DecoderException {
        // empty cargo
        AlertStatusResponse expected = new AlertStatusResponse(
                AlertStatusResponse.AlertResponseType.INCOMPLETE_CARTRIDGE_CHANGE_ALERT.withBit());

        AlertStatusResponse parsedReq = (AlertStatusResponse) MessageTester.test(
                "00064506080020000000000000622d",
                6,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        MessageTester.assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}

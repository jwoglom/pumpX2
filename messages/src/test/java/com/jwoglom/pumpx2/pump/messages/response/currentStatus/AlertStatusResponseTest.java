package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.response.currentStatus.AlertStatusResponse.AlertResponseType.CGM_GRAPH_REMOVED;
import static org.junit.Assert.assertEquals;

import java.util.Set;
import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

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

    @Test
    public void testAlertStatusWithCgmGraphRemoved() {
        AlertStatusResponse parsed = new AlertStatusResponse(
                new byte[]{0,0,0,2,0,0,0,0}
        );

        assertEquals(Set.of(AlertStatusResponse.AlertResponseType.CGM_GRAPH_REMOVED), parsed.getAlerts());
    }


    @Test
    public void testAlertStatusWithInvalidG7TxId() throws DecoderException {
        AlertStatusResponse expected = new AlertStatusResponse(
                AlertStatusResponse.AlertResponseType.INVALID_TRANSMITTER_ID.withBit());

        AlertStatusResponse parsedReq = (AlertStatusResponse) MessageTester.test(
                "000c450c080000002000000000e1df",
                12,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        MessageTester.assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}

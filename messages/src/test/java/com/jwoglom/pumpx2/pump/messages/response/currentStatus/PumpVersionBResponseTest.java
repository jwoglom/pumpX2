package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class PumpVersionBResponseTest {
    @Test
    public void testPumpVersionBResponse_CapturedMobiPayload() throws DecoderException {
        PumpVersionBResponse expected = new PumpVersionBResponse(
            // String softwareName, long configurationBitsA, long configurationBitsB,
            // long serialNumber, long modelNumber, String pumpRevision,
            // long pcbPartNumberA, long pcbSerialNumberA, String pcbRevisionNumberA
            "Control-IQ+ 7.9.0.2", 0L, 0L, 1485742L, 1004000L, "0", 1011004L, 251300136L, "0"
        );

        PumpVersionBResponse parsedRes = (PumpVersionBResponse) MessageTester.test(
            "000685063c436f6e74726f6c2d49512b20372e392e302e32000000000000000000aeab1600e0510f0030000000000000003c6d0f002889fa0e300000000000000013da",
            6,
            4,
            CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
            expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals("Control-IQ+ 7.9.0.2", parsedRes.getSoftwareName());
        assertEquals(0L, parsedRes.getConfigurationBitsA());
        assertEquals(0L, parsedRes.getConfigurationBitsB());
        assertEquals(1485742L, parsedRes.getSerialNumber());
        assertEquals(1004000L, parsedRes.getModelNumber());
        assertEquals("0", parsedRes.getPumpRevision());
        assertEquals(1011004L, parsedRes.getPcbPartNumberA());
        assertEquals(251300136L, parsedRes.getPcbSerialNumberA());
        assertEquals("0", parsedRes.getPcbRevisionNumberA());
    }
}

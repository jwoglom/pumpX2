package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class GetG6TransmitterHardwareInfoResponseTest {
    @Test
    public void testGetG6TransmitterHardwareInfoResponse_parseCargo() throws DecoderException {
        byte[] raw = Hex.decodeHex("000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000003135373900000000000000000000000000000000000000000000000000000000");

        GetG6TransmitterHardwareInfoResponse parsedRes = new GetG6TransmitterHardwareInfoResponse();
        parsedRes.parse(raw);

        assertHexEquals(raw, parsedRes.getCargo());
        assertEquals("", parsedRes.getTransmitterFirmwareVersion());
        assertEquals("", parsedRes.getTransmitterHardwareRevision());
        assertEquals("", parsedRes.getTransmitterBleHardwareId());
        assertEquals("", parsedRes.getTransmitterSoftwareNumber());
        assertEquals("1579", parsedRes.getTransmitterPairingCode());
        assertEquals("", parsedRes.getTransmitterSerialNumber());
    }
}

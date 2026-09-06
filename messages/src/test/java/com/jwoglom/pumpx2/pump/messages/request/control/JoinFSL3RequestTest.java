package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class JoinFSL3RequestTest {
    @Test
    public void testJoinFSL3Request_buildAndParse() throws DecoderException {
        byte[] bleAddress = Hex.decodeHex("010203040506");
        byte[] sensorCode = Hex.decodeHex("0a0b0c0d");
        byte[] activationTime = Hex.decodeHex("11223344");
        byte[] firmwareVersion = Hex.decodeHex("01000200");
        int warmupTime = 60;
        byte[] compressedSerialNumber = Hex.decodeHex("112233445566778899");
        byte[] nfcUid = Hex.decodeHex("a1a2a3a4a5a6a7a8");

        JoinFSL3Request expected = new JoinFSL3Request(
                bleAddress, sensorCode, activationTime, firmwareVersion,
                warmupTime, compressedSerialNumber, nfcUid);

        JoinFSL3Request parsedReq = new JoinFSL3Request();
        parsedReq.parse(expected.getCargo());

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(36, parsedReq.getCargo().length);

        assertHexEquals(bleAddress, parsedReq.getBleAddress());
        assertHexEquals(sensorCode, parsedReq.getSensorCode());
        assertHexEquals(activationTime, parsedReq.getActivationTime());
        assertHexEquals(firmwareVersion, parsedReq.getFirmwareVersion());
        assertEquals(60, parsedReq.getWarmupTime());
        assertHexEquals(compressedSerialNumber, parsedReq.getCompressedSerialNumber());
        assertHexEquals(nfcUid, parsedReq.getNfcUid());
    }
}

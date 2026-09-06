package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class SetRuntimeConfigRequestTest {
    @Test
    public void testSetRuntimeConfigRequest_default() throws DecoderException {
        SetRuntimeConfigRequest expected = new SetRuntimeConfigRequest();

        SetRuntimeConfigRequest parsedReq = new SetRuntimeConfigRequest();
        parsedReq.parse(expected.getCargo());

        assertHexEquals(Hex.decodeHex("0000"), parsedReq.getCargo());
        assertEquals(0, parsedReq.getConfig0());
        assertEquals(0, parsedReq.getConfig1());
    }

    @Test
    public void testSetRuntimeConfigRequest_values() throws DecoderException {
        SetRuntimeConfigRequest expected = new SetRuntimeConfigRequest(1, 2);

        SetRuntimeConfigRequest parsedReq = new SetRuntimeConfigRequest();
        parsedReq.parse(expected.getCargo());

        assertHexEquals(Hex.decodeHex("0102"), parsedReq.getCargo());
        assertEquals(1, parsedReq.getConfig0());
        assertEquals(2, parsedReq.getConfig1());
    }
}

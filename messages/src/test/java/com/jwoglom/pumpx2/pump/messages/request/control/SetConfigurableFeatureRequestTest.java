package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class SetConfigurableFeatureRequestTest {
    @Test
    public void testSetConfigurableFeatureRequest_enable() throws DecoderException {
        SetConfigurableFeatureRequest expected = new SetConfigurableFeatureRequest(2, 5, true);

        SetConfigurableFeatureRequest parsedReq = new SetConfigurableFeatureRequest();
        parsedReq.parse(expected.getCargo());

        assertHexEquals(Hex.decodeHex("020501"), parsedReq.getCargo());
        assertEquals(2, parsedReq.getGroupIndex());
        assertEquals(5, parsedReq.getFeatureIndex());
        assertTrue(parsedReq.getFeatureState());
    }

    @Test
    public void testSetConfigurableFeatureRequest_disable() throws DecoderException {
        SetConfigurableFeatureRequest expected = new SetConfigurableFeatureRequest(0, 0, false);

        SetConfigurableFeatureRequest parsedReq = new SetConfigurableFeatureRequest();
        parsedReq.parse(expected.getCargo());

        assertHexEquals(Hex.decodeHex("000000"), parsedReq.getCargo());
        assertEquals(0, parsedReq.getGroupIndex());
        assertEquals(0, parsedReq.getFeatureIndex());
        assertFalse(parsedReq.getFeatureState());
    }
}

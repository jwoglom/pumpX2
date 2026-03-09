package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class LastBolusStatusV3ResponseTest {
    @Test
    public void testLastBolusStatusV3Response_parseCargo() throws DecoderException {
        LastBolusStatusV3Response expected = new LastBolusStatusV3Response(
                0,
                0, 0, new byte[]{0, 0}, 0, 0, 0, 0, 0, 0, 4294967295L,
                0, 0, new byte[]{0, 0}, 0, 0, 0, 0, 0, 0, 4294967295L, 0
        );

        LastBolusStatusV3Response parsedRes = new LastBolusStatusV3Response();
        parsedRes.parse(Hex.decodeHex("000000000000000000000000000000000000000000ffffffff0000000000000000000000000000000000000000ffffffff00000000"));

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(0, parsedRes.getLastBolusTypeBitmask());
        assertFalse(parsedRes.isStandardBolusPresent());
        assertFalse(parsedRes.isExtendedBolusPresent());

        assertEquals(0, parsedRes.getStandardBolusStatusId());
        assertEquals(0, parsedRes.getStandardBolusId());
        assertEquals(0L, parsedRes.getStandardBolusTimestamp());
        assertEquals(0L, parsedRes.getStandardBolusDeliveredVolume());
        assertEquals(0, parsedRes.getStandardBolusEndReasonId());
        assertEquals(0, parsedRes.getStandardBolusSourceId());
        assertEquals(0, parsedRes.getStandardBolusTypeBitmask());
        assertEquals(0L, parsedRes.getStandardBolusRequestedVolume());
        assertEquals(4294967295L, parsedRes.getStandardBolusSecondsSincePumpReset());

        assertEquals(0, parsedRes.getExtendedBolusStatusId());
        assertEquals(0, parsedRes.getExtendedBolusId());
        assertEquals(0L, parsedRes.getExtendedBolusTimestamp());
        assertEquals(0L, parsedRes.getExtendedBolusDeliveredVolume());
        assertEquals(0, parsedRes.getExtendedBolusEndReasonId());
        assertEquals(0, parsedRes.getExtendedBolusSourceId());
        assertEquals(0, parsedRes.getExtendedBolusTypeBitmask());
        assertEquals(0L, parsedRes.getExtendedBolusRequestedVolume());
        assertEquals(4294967295L, parsedRes.getExtendedBolusSecondsSincePumpReset());
        assertEquals(0L, parsedRes.getExtendedBolusDuration());
    }
}

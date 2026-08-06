package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CurrentActiveIdpValuesResponseTest {
    /**
     * The one genuine capture of this opcode in the repository, committed well before the
     * byte layout was revised. It is what establishes that layout: every field decodes to a
     * clinically standard value at once -- 6 g/U, 115 mg/dL, 300 minutes, 40 mg/dL/U -- which
     * a wrong set of offsets does not produce by chance. The superseded layout read
     * currentTargetBg from bytes 5-6 and decoded this same capture to 11264 mg/dL.
     */
    @Test
    public void testCurrentActiveIdpValuesResponse_parseCargo_CapturedPayload() throws DecoderException {
        // cargoHex: 7017000073002c012800
        byte[] rawCargo = Hex.decodeHex("7017000073002c012800");

        CurrentActiveIdpValuesResponse parsedRes = new CurrentActiveIdpValuesResponse();
        parsedRes.parse(rawCargo);

        assertHexEquals(rawCargo, parsedRes.getCargo());
        assertEquals(6000L, parsedRes.getCurrentCarbRatio());
        assertEquals(115, parsedRes.getCurrentTargetBg());
        assertEquals(300, parsedRes.getCurrentInsulinDuration());
        assertEquals(40, parsedRes.getCurrentIsf());
    }

    /**
     * A hand-constructed vector, not a capture: it is the payload above with the target BG and
     * ISF fields changed, to check that each field is read from its own bytes rather than
     * sharing one. Kept distinct from the capture above so the evidence for the layout is not
     * confused with values chosen to exercise it.
     */
    @Test
    public void testCurrentActiveIdpValuesResponse_parseCargo_ConstructedPayload() throws DecoderException {
        byte[] rawCargo = Hex.decodeHex("701700006e002c011300");

        CurrentActiveIdpValuesResponse parsedRes = new CurrentActiveIdpValuesResponse();
        parsedRes.parse(rawCargo);

        assertEquals(6000L, parsedRes.getCurrentCarbRatio());
        assertEquals(110, parsedRes.getCurrentTargetBg());
        assertEquals(300, parsedRes.getCurrentInsulinDuration());
        assertEquals(19, parsedRes.getCurrentIsf());
    }

    /**
     * Round-trips the genuine capture, which the superseded buildCargo could not reproduce:
     * it wrote currentTargetBg as a single byte on the assumption that its high byte was
     * always zero and shared with currentInsulinDuration.
     */
    @Test
    public void testCurrentActiveIdpValuesResponse_buildCargo_RoundTripsCapturedPayload() throws DecoderException {
        CurrentActiveIdpValuesResponse response =
                new CurrentActiveIdpValuesResponse(6000, 115, 300, 40);

        assertHexEquals(Hex.decodeHex("7017000073002c012800"), response.getCargo());
    }

    @Test
    public void testCurrentActiveIdpValuesResponse_buildCargo_UsesIndependentUint16Fields() throws DecoderException {
        CurrentActiveIdpValuesResponse response =
                new CurrentActiveIdpValuesResponse(6000, 110, 300, 19);

        assertHexEquals(Hex.decodeHex("701700006e002c011300"), response.getCargo());
    }

    /**
     * Guards the specific failure the superseded layout could not represent: a target BG at or
     * above 256 collided with currentInsulinDuration's low byte. Round-tripping such a value
     * proves the fields no longer share byte 6.
     */
    @Test
    public void testCurrentActiveIdpValuesResponse_targetBgAboveOneByteDoesNotCorruptDuration() {
        CurrentActiveIdpValuesResponse response =
                new CurrentActiveIdpValuesResponse(6000, 300, 300, 40);

        CurrentActiveIdpValuesResponse reparsed = new CurrentActiveIdpValuesResponse();
        reparsed.parse(response.getCargo());

        assertEquals(300, reparsed.getCurrentTargetBg());
        assertEquals(300, reparsed.getCurrentInsulinDuration());
        assertEquals(40, reparsed.getCurrentIsf());
    }
}

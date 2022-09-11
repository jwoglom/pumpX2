package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class RemoteCarbEntryResponseTest {
    @Test
    public void testOpcodeNegative14Response_ID10677() throws DecoderException {
        // TimeSinceResetResponse[pumpTime=1200239,timeSinceReset=461710145]
        initPumpState("6VeDeRAL5DCigGw2", 461710145L);

//        OpcodeNegative14Request expected = new OpcodeNegative14Request(
//                new byte[]{5, 0, 1}, 1200239L, 10677);
        
        RemoteCarbEntryResponse expected = new RemoteCarbEntryResponse(
            0
        );

        RemoteCarbEntryResponse parsedRes = (RemoteCarbEntryResponse) MessageTester.test(
                "00aaf3aa19006023851b4f8ac061f1a3bfc05496c100ab5a42a8e06637ad1fc3",
                -86,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testOpcodeNegative14Response_ID10678() throws DecoderException {
        // TimeSinceResetResponse[pumpTime=1200296,timeSinceReset=461710202]
        initPumpState("6VeDeRAL5DCigGw2", 461710202L);

//        OpcodeNegative14Request expected = new OpcodeNegative14Request(
//                new byte[]{3, 0, 1}, 1200239L, 10678);

        RemoteCarbEntryResponse expected = new RemoteCarbEntryResponse(
                0
        );

        RemoteCarbEntryResponse parsedRes = (RemoteCarbEntryResponse) MessageTester.test(
                "00cff3cf19008423851bdcb7bd0bcb44808c07d71cf37fa8e41b8c1d236a1df6",
                -49,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
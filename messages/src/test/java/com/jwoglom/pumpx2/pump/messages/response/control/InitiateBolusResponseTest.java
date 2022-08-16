package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class InitiateBolusResponseTest {
    @Test
    public void testInitiateBolusResponse() throws DecoderException {
        // $ PUMP_AUTHENTICATION_KEY=6VeDeRAL5DCigGw2 PUMP_TIME_SINCE_RESET=461510642
        // ./scripts/get-single-opcode.py 00399f391e009a29000000f717821b19f3e1e5f2e0950d2ede3572f325e6f400b019c37ce3
        initPumpState("6VeDeRAL5DCigGw2", 461510642L);

        // InitiateBolusResponse[bolusId=10650,status=0,statusType=0,cargo={0,-102,41,0,0,0,-9,23,-126,27,25,-13,-31,-27,-14,-32,-107,13,46,-34,53,114,-13,37,-26,-12,0,-80,25,-61}]
        InitiateBolusResponse expected = new InitiateBolusResponse(
            // int status, int bolusId, int statusType
            0, 10650, 0
        );

        InitiateBolusResponse parsedRes = (InitiateBolusResponse) MessageTester.test(
                "00399f391e009a29000000f717821b19f3e1e5f2e0950d2ede3572f325e6f400b019c37ce3",
                57,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
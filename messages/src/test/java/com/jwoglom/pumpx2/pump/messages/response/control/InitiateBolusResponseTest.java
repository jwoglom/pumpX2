package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.InitiateBolusRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class InitiateBolusResponseTest {
    @Test
    public void testInitiateBolusResponse_ID10650() throws DecoderException {
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

    @Test
    public void testInitiateBolusResponse_ID10652_013u_13g_carbs_142mgdl() throws DecoderException {
        // $ PUMP_AUTHENTICATION_KEY=6VeDeRAL5DCigGw2 PUMP_TIME_SINCE_RESET=461589180
        // ./scripts/get-single-opcode.py '033e9e3e3d820000009c29000001820000000000 023e00000d008e00000000000000000000000000 013e00000000bc4a831b9cbf19ffb856288a8afa 003e8f24a463e00cf3bbe5d305dd'

        // TimeSinceResetResponse[pumpTime=1079274,timeSinceReset=461589180]
        initPumpState("6VeDeRAL5DCigGw2", 461589180L);
        InitiateBolusRequest associatedRequest = new InitiateBolusRequest(new byte[]{-126, 0, 0, 0, -100, 41, 0, 0, 1, -126, 0, 0, 0, 0, 0, 0, 0, 13, 0, -114, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -68, 74, -125, 27, -100, -65, 25, -1, -72, 86, 40, -118, -118, -6, -113, 36, -92, 99, -32, 12, -13, -69, -27, -45});

        // InitiateBolusResponse[bolusId=10652,status=0,statusType=0,cargo={0,-100,41,0,0,0,-26,74,-125,27,73,78,102,122,84,-18,-117,53,4,45,-109,-83,74,-8,-58,18,-106,61,118,-72}]
        InitiateBolusResponse expected = new InitiateBolusResponse(
                // int status, int bolusId, int statusType
                0, 10652, 0
        );

        InitiateBolusResponse parsedRes = (InitiateBolusResponse) MessageTester.test(
                "003e9f3e1e009c29000000e64a831b494e667a54ee8b35042d93ad4af8c612963d76b86f60",
                57,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
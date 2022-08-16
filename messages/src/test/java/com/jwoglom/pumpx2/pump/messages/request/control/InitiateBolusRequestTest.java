package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.InitiateBolusRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class InitiateBolusRequestTest {
    @Test
    public void testInitiateBolusRequest() throws DecoderException {
        // TimeSinceResetResponse[pumpTime=1000735,timeSinceReset=461510642]
        // 1u override bolus

        initPumpState("6VeDeRAL5DCigGw2", 461510642L);
        InitiateBolusRequest expected = new InitiateBolusRequest(new byte[]{-24,3,0,0,-102,41,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-14,23,-126,27,104,-7,76,-21,-26,113,122,93,21,81,39,20,127,-20,-102,-39,121,-110,106,-84});

        InitiateBolusRequest parsedReq = (InitiateBolusRequest) MessageTester.test(
                "03399e393de80300009a29000008000000000000",
                57,
                3,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "0239000000000000000000000000000000000000",
                "013900000000f217821b68f94cebe6717a5d1551",
                "003927147fec9ad979926aaccd74"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}
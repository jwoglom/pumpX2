package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class BolusPermissionReleaseResponseTest {
    @Test
    public void testBolusPermissionReleaseResponse_ID10676() throws DecoderException {
        // TimeSinceResetResponse[pumpTime=1200173,timeSinceReset=461710079]
        initPumpState("6VeDeRAL5DCigGw2", 461710079L);

        // OpcodeNegative16Request expected = new OpcodeNegative16Request(10676);

        // 0 = successfully released
        BolusPermissionReleaseResponse expected = new BolusPermissionReleaseResponse(
            0
        );

        BolusPermissionReleaseResponse parsedRes = (BolusPermissionReleaseResponse) MessageTester.test(
                "003af13a19003923851b8854859ea0fc17fe530b3c3c4208ae30a555356f0985",
                58,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(BolusPermissionReleaseResponse.ReleaseStatus.SUCCESS, parsedRes.getReleaseStatus());
    }


    @Test
    public void testBolusPermissionReleaseResponse_ID10737_initial() throws DecoderException {
        // using authenticationKey=Ns7vSuFYcfTLAXmb pumpTimeSinceReset=467774280
        initPumpState("Ns7vSuFYcfTLAXmb", 461710079L);

        // OpcodeNegative16Request expected = new OpcodeNegative16Request(10737);

        // 0 = successfully released
        BolusPermissionReleaseResponse expected = new BolusPermissionReleaseResponse(
                0
        );

        BolusPermissionReleaseResponse parsedRes = (BolusPermissionReleaseResponse) MessageTester.test(
                "0005f105190099abe11b341684fe037937a533833292ee3df0d4bc008b71fca4",
                5,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(BolusPermissionReleaseResponse.ReleaseStatus.SUCCESS, parsedRes.getReleaseStatus());
    }

    @Test
    public void testBolusPermissionReleaseResponse_ID10737_second_time() throws DecoderException {
        // using authenticationKey=Ns7vSuFYcfTLAXmb pumpTimeSinceReset=467774280
        initPumpState("Ns7vSuFYcfTLAXmb", 461710079L);

        // OpcodeNegative16Request expected = new OpcodeNegative16Request(10737);

        // 0 = successfully released
        BolusPermissionReleaseResponse expected = new BolusPermissionReleaseResponse(
                1
        );

        BolusPermissionReleaseResponse parsedRes = (BolusPermissionReleaseResponse) MessageTester.test(
                "0006f1061901a2abe11b6fbc54b70597cb6d423d18d162cd350efcfcb9f5a16a",
                6,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(BolusPermissionReleaseResponse.ReleaseStatus.FAILURE, parsedRes.getReleaseStatus());
    }
}
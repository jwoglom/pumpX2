package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.RemoteBgEntryRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class RemoteBgEntryResponseTest {
    @Test
    public void testRemoteBgEntryResponseTest_ID1066() throws DecoderException {
        // TimeSinceResetResponse[pumpTime=1200239,timeSinceReset=461710145]
        initPumpState("6VeDeRAL5DCigGw2", 461710145L);
        // RemoteBgEntryRequest expected = new RemoteBgEntryRequest(180, true, 1200173L, 10676);
        
        RemoteBgEntryResponse expected = new RemoteBgEntryResponse(
            0
        );

        RemoteBgEntryResponse parsedRes = (RemoteBgEntryResponse) MessageTester.test(
                "003bb73b19003923851b0b8a07e99fa174bf0e01c333ae169f8d1e836f9f2792",
                59,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }


    @Test
    public void testRemoteBgEntryResponseTest_ID10677() throws DecoderException {
        // TimeSinceResetResponse[pumpTime=1200239,timeSinceReset=461710145]
        initPumpState("6VeDeRAL5DCigGw2", 461710145L);
        // RemoteBgEntryRequest expected = new RemoteBgEntryRequest(185, true, 1200239L, 10677);

        RemoteBgEntryResponse expected = new RemoteBgEntryResponse(
                0
        );

        RemoteBgEntryResponse parsedRes = (RemoteBgEntryResponse) MessageTester.test(
                "00a9b7a919005f23851b79da6d43236154c7564648e5a88ab528951007dcf2c3",
                -87,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testRemoteBgEntryResponseTest_ID10678() throws DecoderException {
        // TimeSinceResetResponse[pumpTime=1200296,timeSinceReset=461710202]
        initPumpState("6VeDeRAL5DCigGw2", 461710202L);
        // RemoteBgEntryRequest expected = new RemoteBgEntryRequest(186, true, 1200239L, 10678);

        RemoteBgEntryResponse expected = new RemoteBgEntryResponse(
                0
        );

        RemoteBgEntryResponse parsedRes = (RemoteBgEntryResponse) MessageTester.test(
                "00ceb7ce19008323851b5a103ba6a9e9dc9ee8799d46bc88ffb6fefe9aaffe5f",
                -50,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testRemoteBgEntryResponseTest_ID10652() throws DecoderException {
        // TimeSinceResetResponse[pumpTime=1079252,timeSinceReset=461589158
        initPumpState("6VeDeRAL5DCigGw2", 461589158L);
        // RemoteBgEntryRequest expected = new RemoteBgEntryRequest(142, true, 1079274L, 10652);

        RemoteBgEntryResponse expected = new RemoteBgEntryResponse(
                0
        );

        RemoteBgEntryResponse parsedRes = (RemoteBgEntryResponse) MessageTester.test(
                "003cb73c1900e54a831ba9ee8424e341a16f6efc75204b5bcb3c91256accd804",
                60,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
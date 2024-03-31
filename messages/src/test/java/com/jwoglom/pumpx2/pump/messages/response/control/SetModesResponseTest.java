package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class SetModesResponseTest {
    @Test
    public void testSetModesResponse_exerciseModeOffToOn() throws DecoderException {
        // TimeSinceResetResponse[currentTime=512443145,pumpTimeSinceReset=1905716,cargo={9,67,-117,30,52,20,29,0}]
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1905716L);
        
        SetModesResponse expected = new SetModesResponse(
            // byte[] raw
                new byte[]{0}
        );

        SetModesResponse parsedRes = (SetModesResponse) MessageTester.test(
                "0079cd791900e8428b1ec8fa29dc63d8f79a1c678b339ba801061a620659e904",
                121,
                2,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(0, parsedRes.getStatus());
    }

    @Test
    public void testSetModesResponse_exerciseModeOnToOff() throws DecoderException {
        // TimeSinceResetResponse[currentTime=512443145,pumpTimeSinceReset=1905716,cargo={9,67,-117,30,52,20,29,0}]
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1905716L);

        SetModesResponse expected = new SetModesResponse(
                // byte[] raw
                new byte[]{0}
        );

        SetModesResponse parsedRes = (SetModesResponse) MessageTester.test(
                "0081cd811900fb428b1ec6956c4b467290f8ba7ee8df78dd2ca119dbef422894",
                -127,
                2,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(0, parsedRes.getStatus());
    }

    @Test
    public void testSetModesResponse_sleepModeOffToOn() throws DecoderException {
        // TimeSinceResetResponse[currentTime=512443145,pumpTimeSinceReset=1905716,cargo={9,67,-117,30,52,20,29,0}]
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1905716L);

        SetModesResponse expected = new SetModesResponse(
                // byte[] raw
                new byte[]{0}
        );

        SetModesResponse parsedRes = (SetModesResponse) MessageTester.test(
                "008acd8a190017438b1e16af1181440210dc1feed9fc36a3882124bc7bd2678e",
                -118,
                2,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(0, parsedRes.getStatus());
    }

    @Test
    public void testSetModesResponse_sleepModeOnToOff() throws DecoderException {
        // TimeSinceResetResponse[currentTime=512443145,pumpTimeSinceReset=1905716,cargo={9,67,-117,30,52,20,29,0}]
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1905716L);

        SetModesResponse expected = new SetModesResponse(
                // byte[] raw
                new byte[]{0}
        );

        SetModesResponse parsedRes = (SetModesResponse) MessageTester.test(
                "0091cd91190034438b1e5858889224eafe67bf93fc50bb2575a514602ff34e62",
                -111,
                2,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(0, parsedRes.getStatus());
    }
}
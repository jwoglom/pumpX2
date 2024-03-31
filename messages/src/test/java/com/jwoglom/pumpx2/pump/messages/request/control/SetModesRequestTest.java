package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class SetModesRequestTest {
    @Test
    public void testSetModesRequest_exerciseModeOffToOn() throws DecoderException {
        // TimeSinceResetResponse[currentTime=512443145,pumpTimeSinceReset=1905716,cargo={9,67,-117,30,52,20,29,0}]
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1905716L);
        // empty cargo
        SetModesRequest expected = new SetModesRequest(new byte[]{3});

        SetModesRequest parsedReq = (SetModesRequest) MessageTester.test(
                // Untitled_2_Live_-_Humans_iPhone - 2024-03-28T00:18:39.016000+00:00
                "0179cc7919034d978b1e32c70c65d5b530fdf45a",
                121,
                2,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "0079b59d63358bab1b849bb1a4e4"
        ); // resp: 0079cd791900e8428b1ec8fa29dc63d8f79a1c678b339ba801061a620659e904

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(3, parsedReq.getBitmap());
        assertEquals(SetModesRequest.ModeCommand.EXERCISE_MODE_ON, parsedReq.getCommand());
    }

    @Test
    public void testSetModesRequest_exerciseModeOnToOff() throws DecoderException {
        // TimeSinceResetResponse[currentTime=512443145,pumpTimeSinceReset=1905716,cargo={9,67,-117,30,52,20,29,0}]
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1905716L);
        // empty cargo
        SetModesRequest expected = new SetModesRequest(new byte[]{4});

        SetModesRequest parsedReq = (SetModesRequest) MessageTester.test(
                // Untitled_2_Live_-_Humans_iPhone - 2024-03-28T00:18:58.516000+00:00
                "0181cc81190461978b1eff1bb7778cd232419aa1",
                -127,
                2,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "0081d97936364e86562320d973e2"
        ); // resp: 0081cd811900fb428b1ec6956c4b467290f8ba7ee8df78dd2ca119dbef422894

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(4, parsedReq.getBitmap());
        assertEquals(SetModesRequest.ModeCommand.EXERCISE_MODE_OFF, parsedReq.getCommand());
    }

    @Test
    public void testSetModesRequest_sleepModeOffToOn() throws DecoderException {
        // TimeSinceResetResponse[currentTime=512443145,pumpTimeSinceReset=1905716,cargo={9,67,-117,30,52,20,29,0}]
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1905716L);
        // empty cargo
        SetModesRequest expected = new SetModesRequest(new byte[]{1});

        SetModesRequest parsedReq = (SetModesRequest) MessageTester.test(
                // Untitled_2_Live_-_Humans_iPhone - 2024-03-28T00:19:26.068000+00:00
                "018acc8a19017d978b1ef95a55180db9a2f1c821",
                -118,
                2,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "008ace22b462fab923c16c2cc428"
        ); // resp: 008acd8a190017438b1e16af1181440210dc1feed9fc36a3882124bc7bd2678e

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(1, parsedReq.getBitmap());
        assertEquals(SetModesRequest.ModeCommand.SLEEP_MODE_ON, parsedReq.getCommand());
    }


    @Test
    public void testSetModesRequest_sleepModeOnToOff() throws DecoderException {
        // TimeSinceResetResponse[currentTime=512443145,pumpTimeSinceReset=1905716,cargo={9,67,-117,30,52,20,29,0}]
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1905716L);
        // empty cargo
        SetModesRequest expected = new SetModesRequest(new byte[]{2});

        SetModesRequest parsedReq = (SetModesRequest) MessageTester.test(
                // Untitled_2_Live_-_Humans_iPhone - 2024-03-28T00:19:55.126000+00:00
                "0191cc9119029a978b1e7ce08a7a68802cca71dc",
                -111,
                2,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "009134fd2877d2a8bd3247ef819d"
        ); // resp: 0091cd91190034438b1e5858889224eafe67bf93fc50bb2575a514602ff34e62

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(2, parsedReq.getBitmap());
        assertEquals(SetModesRequest.ModeCommand.SLEEP_MODE_OFF, parsedReq.getCommand());
    }
}
package com.jwoglom.pumpx2.pump.messages.response.controlStream;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.request.controlStream.NonexistentDetectingCartridgeStateStreamRequest;
import com.jwoglom.pumpx2.pump.messages.request.controlStream.NonexistentEnterChangeCartridgeModeStateStreamRequest;
import com.jwoglom.pumpx2.pump.messages.request.controlStream.NonexistentExitFillTubingModeStateStreamRequest;
import com.jwoglom.pumpx2.pump.messages.request.controlStream.NonexistentFillCannulaStateStreamRequest;
import com.jwoglom.pumpx2.pump.messages.request.controlStream.NonexistentFillTubingStateStreamRequest;
import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ControlStreamMessagesTest {
    @Test
    public void testDetermineRequestMessage_DetectingCartridgeStateStreamResponse() throws DecoderException, InstantiationException, IllegalAccessException {
        byte[] rawHex = Hex.decodeHex("0089e3891a140078c84020f4eabab90ec17db0da1b4d99095f92c063a1b1b9456a");
        assertEquals(NonexistentDetectingCartridgeStateStreamRequest.class, ControlStreamMessages.determineRequestMessage(rawHex).getClass());
    }

    @Test
    public void testDetermineRequestMessage_EnterChangeCartridgeModeStateStreamResponse() throws DecoderException, InstantiationException, IllegalAccessException {
        byte[] rawHex = Hex.decodeHex("0083e18319026dc840203e1a10e4806b4079adf00d1cfe2894949c7d58333915");
        assertEquals(NonexistentEnterChangeCartridgeModeStateStreamRequest.class, ControlStreamMessages.determineRequestMessage(rawHex).getClass());
    }

    @Test
    public void testDetermineRequestMessage_ExitFillTubingModeStateStreamResponse() throws DecoderException, InstantiationException, IllegalAccessException {
        byte[] rawHex = Hex.decodeHex("0091e9911900aec84020f5be25220634beb8ffff2cad2cf936582ee0226d2f28");
        assertEquals(NonexistentExitFillTubingModeStateStreamRequest.class, ControlStreamMessages.determineRequestMessage(rawHex).getClass());
    }

    @Test
    public void testDetermineRequestMessage_FillCannulaStateStreamResponse() throws DecoderException, InstantiationException, IllegalAccessException {
        byte[] rawHex = Hex.decodeHex("0070e7701902e7d64020c63c1d5e2cfb1fecb965df1f825d770093d1d6461306");
        assertEquals(NonexistentFillCannulaStateStreamRequest.class, ControlStreamMessages.determineRequestMessage(rawHex).getClass());
    }

    @Test
    public void testDetermineRequestMessage_FillTubingStateStreamResponse() throws DecoderException, InstantiationException, IllegalAccessException {
        byte[] rawHex = Hex.decodeHex("0076e5761901dc9bee1fc0a8edcd9cbee43c109b046853fab2051262f1b84ca9");
        assertEquals(NonexistentFillTubingStateStreamRequest.class, ControlStreamMessages.determineRequestMessage(rawHex).getClass());
    }
}

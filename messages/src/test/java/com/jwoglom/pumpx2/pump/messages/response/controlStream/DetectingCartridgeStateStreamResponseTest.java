package com.jwoglom.pumpx2.pump.messages.response.controlStream;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class DetectingCartridgeStateStreamResponseTest {
    // ExitChangeCartridgeModeRequest: 0189928918e40e4120bd5bce64b42a9df62d8cae0089e8d1e8325cc6cacddc7790
    // 0089e3891a140078c84020f4eabab90ec17db0da1b4d99095f92c063a1b1b9456a
    // ExitChangeCartridgeModeResponse: 00899389190078c840201a521d880f1dfa4fd153a975571060d9098181614321
    // 0089e3891a280078c840207ae3354ae4a861ca6cbaf2a6acc178441f68714f8a3f
    // 0089e3891a3c0078c840204b662d159bd8d8827045926b2b16c18dc867fa8b2bdd
    // 0089e3891a500078c840209382ea406c228d99294fb75a06b6034720bebf558af6
    // 0089e3891a640078c840200446e27abb9bac31f8d1183f53dae99a2cc61d49b7ba
    @Test
    public void testDetectingCartridgeStateStreamResponse_1() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        DetectingCartridgeStateStreamResponse expected = new DetectingCartridgeStateStreamResponse(
            new byte[]{ 20, 0 }
        );

        DetectingCartridgeStateStreamResponse parsedRes = (DetectingCartridgeStateStreamResponse) MessageTester.test(
                "0089e3891a140078c84020f4eabab90ec17db0da1b4d99095f92c063a1b1b9456a",
                -119,
                1,
                CharacteristicUUID.CONTROL_STREAM_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }


    @Test
    public void testDetectingCartridgeStateStreamResponse_2() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        DetectingCartridgeStateStreamResponse expected = new DetectingCartridgeStateStreamResponse(
                new byte[]{ 40, 0 }
        );

        DetectingCartridgeStateStreamResponse parsedRes = (DetectingCartridgeStateStreamResponse) MessageTester.test(
                "0089e3891a280078c840207ae3354ae4a861ca6cbaf2a6acc178441f68714f8a3f",
                -119,
                1,
                CharacteristicUUID.CONTROL_STREAM_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }


    @Test
    public void testDetectingCartridgeStateStreamResponse_3() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        DetectingCartridgeStateStreamResponse expected = new DetectingCartridgeStateStreamResponse(
                new byte[]{ 60, 0 }
        );

        DetectingCartridgeStateStreamResponse parsedRes = (DetectingCartridgeStateStreamResponse) MessageTester.test(
                "0089e3891a3c0078c840204b662d159bd8d8827045926b2b16c18dc867fa8b2bdd",
                -119,
                1,
                CharacteristicUUID.CONTROL_STREAM_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }


    @Test
    public void testDetectingCartridgeStateStreamResponse_4() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        DetectingCartridgeStateStreamResponse expected = new DetectingCartridgeStateStreamResponse(
                new byte[]{ 80, 0 }
        );

        DetectingCartridgeStateStreamResponse parsedRes = (DetectingCartridgeStateStreamResponse) MessageTester.test(
                "0089e3891a500078c840209382ea406c228d99294fb75a06b6034720bebf558af6",
                -119,
                1,
                CharacteristicUUID.CONTROL_STREAM_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }



    @Test
    public void testDetectingCartridgeStateStreamResponse_5() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        DetectingCartridgeStateStreamResponse expected = new DetectingCartridgeStateStreamResponse(
                new byte[]{ 100, 0 }
        );

        DetectingCartridgeStateStreamResponse parsedRes = (DetectingCartridgeStateStreamResponse) MessageTester.test(
                "0089e3891a640078c840200446e27abb9bac31f8d1183f53dae99a2cc61d49b7ba",
                -119,
                1,
                CharacteristicUUID.CONTROL_STREAM_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
package com.jwoglom.pumpx2.pump.messages.response.controlStream;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class FillTubingStateStreamResponseTest {
    @Test
    public void testFillTubingRequest_1() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        FillTubingStateStreamResponse expected = new FillTubingStateStreamResponse(
            new byte[]{1}
        );

        FillTubingStateStreamResponse parsedRes = (FillTubingStateStreamResponse) MessageTester.test(
                "0076e5761901dc9bee1fc0a8edcd9cbee43c109b046853fab2051262f1b84ca9",
                118,
                1,
                CharacteristicUUID.CONTROL_STREAM_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
    @Test
    public void testFillTubingRequest_2() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        FillTubingStateStreamResponse expected = new FillTubingStateStreamResponse(
                new byte[]{0}
        );

        FillTubingStateStreamResponse parsedRes = (FillTubingStateStreamResponse) MessageTester.test(
                "0076e5761900e29bee1f69b046e6713fd3947e435f7a10381e7864ac91091a79",
                118,
                1,
                CharacteristicUUID.CONTROL_STREAM_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
    @Test
    public void testFillTubingRequest_3() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        FillTubingStateStreamResponse expected = new FillTubingStateStreamResponse(
                new byte[]{1}
        );

        FillTubingStateStreamResponse parsedRes = (FillTubingStateStreamResponse) MessageTester.test(
                "0076e5761901e29bee1fe0744f64ededbc751612b0e00f367de5eaeccf5bfc96",
                118,
                1,
                CharacteristicUUID.CONTROL_STREAM_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testFillTubingRequest_4() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        FillTubingStateStreamResponse expected = new FillTubingStateStreamResponse(
                new byte[]{0}
        );

        FillTubingStateStreamResponse parsedRes = (FillTubingStateStreamResponse) MessageTester.test(
                "0076e5761900e39bee1f9d9e1e59f25f697025e559a930e69f91498ace97b83f",
                118,
                1,
                CharacteristicUUID.CONTROL_STREAM_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
    @Test
    public void testFillTubingRequest_5() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        FillTubingStateStreamResponse expected = new FillTubingStateStreamResponse(
                new byte[]{1}
        );

        FillTubingStateStreamResponse parsedRes = (FillTubingStateStreamResponse) MessageTester.test(
                "0076e5761901e69bee1ffab75ebc85b1a37c7309d479a4038afb7e4d6fdb91c7",
                118,
                1,
                CharacteristicUUID.CONTROL_STREAM_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
    @Test
    public void testFillTubingRequest_6() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        FillTubingStateStreamResponse expected = new FillTubingStateStreamResponse(
                new byte[]{0}
        );

        FillTubingStateStreamResponse parsedRes = (FillTubingStateStreamResponse) MessageTester.test(
                "0076e5761900ed9bee1f2a9429c51924b746e4a16d31d2981ce4e00e06b75eff",
                118,
                1,
                CharacteristicUUID.CONTROL_STREAM_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
    @Test
    public void testFillTubingRequest_7() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        FillTubingStateStreamResponse expected = new FillTubingStateStreamResponse(
                new byte[]{1}
        );

        FillTubingStateStreamResponse parsedRes = (FillTubingStateStreamResponse) MessageTester.test(
                "0076e5761901f49bee1ff790d0d466213d4b56db4072b38c576a9a575be6b650",
                118,
                1,
                CharacteristicUUID.CONTROL_STREAM_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
    @Test
    public void testFillTubingRequest_8() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        FillTubingStateStreamResponse expected = new FillTubingStateStreamResponse(
                new byte[]{0}
        );

        FillTubingStateStreamResponse parsedRes = (FillTubingStateStreamResponse) MessageTester.test(
                "0076e5761900f59bee1f9f053315303c66641ccc9e34555b50e153b58f8bc523",
                118,
                1,
                CharacteristicUUID.CONTROL_STREAM_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    /*
     * New EnterFillTubing request (presumably with txId=-119):
     * {"raw":{"btChar":"","guessedBtChar":"7b83fffc9f774e5c8064aae2c24838b9","type":"ReadResp","value":"009295921900349cee1f36b300799beed4abdcb59d45897223cfbb8ff25ff7d5","ts":"2024-12-22 09:14:16.180000"},"parsed":{"cargoHex":"00","name":"response.control.EnterFillTubingModeResponse","messageProps":{"signed":true,"characteristicUuid":"7b83fffc-9f77-4e5c-8064-aae2c24838b9","type":"RESPONSE","characteristic":"CONTROL","requestName":"request.control.EnterFillTubingModeRequest","minApi":"API_V2_1","size":1,"stream":false,"requestOpCode":-108,"opCode":-107,"supportedDevices":"ALL","variableSize":false,"modifiesInsulinDelivery":true},"params":{"cargo":[0],"status":0}}}
     * {"raw":{"btChar":"","guessedBtChar":"7b83fffc9f774e5c8064aae2c24838b9","type":"WriteReq","value":"019294921887e2ee1fe1b55345f933bb7941d0cb00920f90287c7be754d66af501","ts":"2024-12-22 09:14:16.181000"},"parsed":{"cargoHex":"","name":"request.control.EnterFillTubingModeRequest","messageProps":{"signed":true,"responseName":"response.control.EnterFillTubingModeResponse","responseOpCode":-107,"characteristicUuid":"7b83fffc-9f77-4e5c-8064-aae2c24838b9","type":"REQUEST","characteristic":"CONTROL","minApi":"API_V2_1","size":0,"stream":false,"opCode":-108,"supportedDevices":"ALL","variableSize":false,"modifiesInsulinDelivery":true},"params":{"cargo":[]}}}
     */


    @Test
    public void testFillTubingRequest_B_9() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        FillTubingStateStreamResponse expected = new FillTubingStateStreamResponse(
                new byte[]{0}
        );

        FillTubingStateStreamResponse parsedRes = (FillTubingStateStreamResponse) MessageTester.test(
                "0089e5891900249cee1f9b9e05be753ab197b4036913314c1266af76a668266a",
                -119,
                1,
                CharacteristicUUID.CONTROL_STREAM_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
    @Test
    public void testFillTubingRequest_C_10() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        FillTubingStateStreamResponse expected = new FillTubingStateStreamResponse(
                new byte[]{1}
        );

        FillTubingStateStreamResponse parsedRes = (FillTubingStateStreamResponse) MessageTester.test(
                "0092e5921901379cee1fc0b37184a090390ff6bd41b1ac5566a0d66a53d6f74e",
                -110,
                1,
                CharacteristicUUID.CONTROL_STREAM_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
    @Test
    public void testFillTubingRequest_C_11() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        FillTubingStateStreamResponse expected = new FillTubingStateStreamResponse(
                new byte[]{0}
        );

        FillTubingStateStreamResponse parsedRes = (FillTubingStateStreamResponse) MessageTester.test(
                "0092e5921900449cee1f7714edde7740ac55f395f7c3030daf9da601fb86d7ae",
                -110,
                1,
                CharacteristicUUID.CONTROL_STREAM_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
    @Test
    public void testFillTubingRequest_D_12() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        FillTubingStateStreamResponse expected = new FillTubingStateStreamResponse(
                new byte[]{0}
        );

        FillTubingStateStreamResponse parsedRes = (FillTubingStateStreamResponse) MessageTester.test(
                "009de59d1900639cee1f835f372ea7bf7587005915997b64cc35afd9f06d9b1b",
                -99,
                1,
                CharacteristicUUID.CONTROL_STREAM_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
    @Test
    public void testFillTubingRequest_E_13() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        FillTubingStateStreamResponse expected = new FillTubingStateStreamResponse(
                new byte[]{1}
        );

        FillTubingStateStreamResponse parsedRes = (FillTubingStateStreamResponse) MessageTester.test(
                "00a5e5a51901689cee1f3def52456aa152c855a664ea2f1d7846de2699807f3b",
                -91,
                1,
                CharacteristicUUID.CONTROL_STREAM_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
    @Test
    public void testFillTubingRequest_E_14() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        FillTubingStateStreamResponse expected = new FillTubingStateStreamResponse(
                new byte[]{0}
        );

        FillTubingStateStreamResponse parsedRes = (FillTubingStateStreamResponse) MessageTester.test(
                "00a5e5a51900769cee1fe8533421e37d68f6ee8b2b226361aa9282fe729065a5",
                -91,
                1,
                CharacteristicUUID.CONTROL_STREAM_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

}
package com.jwoglom.pumpx2.pump.messages.response.controlStream;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class EnterChangeCartridgeModeStateStreamResponseTest {
    @Test
    public void testChangeCartridgeStateStreamResponse_ReadyToRemoveCart() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        EnterChangeCartridgeModeStateStreamResponse expected = new EnterChangeCartridgeModeStateStreamResponse(
            2
        );

        // Matching ChangeCartridge call:
        // {"raw":{"btChar":"","guessedBtChar":"7b83fffc9f774e5c8064aae2c24838b9","type":"ReadResp","value":"00839183190069c840209f399a8d16e8607559571b008ffab0b81194aa8eeebb","ts":"2025-02-22 17:09:10.614000"},"parsed":{"cargoHex":"00","name":"response.control.ChangeCartridgeResponse","messageProps":{"signed":true,"characteristicUuid":"7b83fffc-9f77-4e5c-8064-aae2c24838b9","type":"RESPONSE","characteristic":"CONTROL","requestName":"request.control.ChangeCartridgeRequest","minApi":"API_V2_1","size":1,"stream":false,"requestOpCode":-112,"opCode":-111,"supportedDevices":"ALL","variableSize":false,"modifiesInsulinDelivery":true},"params":{"cargo":[0],"status":0}}}
        // {"raw":{"btChar":"","guessedBtChar":"7b83fffc9f774e5c8064aae2c24838b9","type":"WriteReq","value":"0183908318d50e41201b1d58b885508711b9e793008333f7d1dc406edffc7e3c87","ts":"2025-02-22 17:09:10.616000"},"parsed":{"cargoHex":"","name":"request.control.ChangeCartridgeRequest","messageProps":{"signed":true,"responseName":"response.control.ChangeCartridgeResponse","responseOpCode":-111,"characteristicUuid":"7b83fffc-9f77-4e5c-8064-aae2c24838b9","type":"REQUEST","characteristic":"CONTROL","minApi":"API_V2_1","size":0,"stream":false,"opCode":-112,"supportedDevices":"ALL","variableSize":false,"modifiesInsulinDelivery":true},"params":{"cargo":[]}}}

        EnterChangeCartridgeModeStateStreamResponse parsedRes = (EnterChangeCartridgeModeStateStreamResponse) MessageTester.test(
                "0083e18319026dc840203e1a10e4806b4079adf00d1cfe2894949c7d58333915",
                -125,
                1,
                CharacteristicUUID.CONTROL_STREAM_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
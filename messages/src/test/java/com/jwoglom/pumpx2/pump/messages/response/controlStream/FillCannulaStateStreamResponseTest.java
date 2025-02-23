package com.jwoglom.pumpx2.pump.messages.response.controlStream;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class FillCannulaStateStreamResponseTest {
    @Test
    public void testFillCannulaStateStreamResponse_complete() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        FillCannulaStateStreamResponse expected = new FillCannulaStateStreamResponse(
            2
        );

        // FillCannulaRequest/Response:
        // {"raw":{"btChar":"","guessedBtChar":"7b83fffc9f774e5c8064aae2c24838b9","type":"ReadResp","value":"007099701900dbd640206738d8448edcec6808e5f81ae792b07653ae5e14a156","ts":"2025-02-22 18:10:49.126000"},"parsed":{"cargoHex":"00","name":"response.control.FillCannulaResponse","messageProps":{"signed":true,"characteristicUuid":"7b83fffc-9f77-4e5c-8064-aae2c24838b9","type":"RESPONSE","characteristic":"CONTROL","requestName":"request.control.FillCannulaRequest","minApi":"MOBI_API_V3_5","size":1,"stream":false,"requestOpCode":-104,"opCode":-103,"supportedDevices":"MOBI_ONLY","variableSize":false,"modifiesInsulinDelivery":true},"params":{"cargo":[0],"status":0}}}
        // {"raw":{"btChar":"","guessedBtChar":"7b83fffc9f774e5c8064aae2c24838b9","type":"WriteReq","value":"017098701a8403481d4120731d56d45ca28d7339007004f385775953a8977ee6459329","ts":"2025-02-22 18:10:49.127000"},"parsed":{"cargoHex":"8403","name":"request.control.FillCannulaRequest","messageProps":{"signed":true,"responseName":"response.control.FillCannulaResponse","responseOpCode":-103,"characteristicUuid":"7b83fffc-9f77-4e5c-8064-aae2c24838b9","type":"REQUEST","characteristic":"CONTROL","minApi":"MOBI_API_V3_5","size":2,"stream":false,"opCode":-104,"supportedDevices":"MOBI_ONLY","variableSize":false,"modifiesInsulinDelivery":true},"params":{"primeSizeMilliUnits":900,"cargo":[-124,3]}}}

        FillCannulaStateStreamResponse parsedRes = (FillCannulaStateStreamResponse) MessageTester.test(
                "0070e7701902e7d64020c63c1d5e2cfb1fecb965df1f825d770093d1d6461306",
                112,
                1,
                CharacteristicUUID.CONTROL_STREAM_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
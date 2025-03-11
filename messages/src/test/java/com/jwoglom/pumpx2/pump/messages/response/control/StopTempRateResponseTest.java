package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class StopTempRateResponseTest {
    @Test
    public void testStopTempRateResponse() throws DecoderException {
        // TimeSinceResetResponse[currentTime=512443560,pumpTimeSinceReset=1906112,cargo={-88,68,-117,30,-64,21,29,0}]
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1906112L);
        
        StopTempRateResponse expected = new StopTempRateResponse(
            new byte[]{0, 9, 0}
        );

        StopTempRateResponse parsedRes = (StopTempRateResponse) MessageTester.test(
                "002ca72c1b000900d9448b1ea1dcb10f6cd0a00465d9091bad121938e17e51d4c3c9",
                44,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }


    @Test
    public void testStopTempRateResponse_2() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1906112L);

        StopTempRateResponse expected = new StopTempRateResponse(
                new byte[]{0, 15, 0}
        );

        StopTempRateResponse parsedRes = (StopTempRateResponse) MessageTester.test(
                // 2024-12-22-mobi-screenrec.jsonl
                // 2024-12-22 09:46:38.384000
                "0051a7511b000f00caa3ee1f206002d65b54e77f42869917c881cae3dde6ad621928",
                81,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }




    @Test
    public void testStopTempRateResponse_3() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        // {"raw":{"btChar":"","guessedBtChar":"7b83fffc9f774e5c8064aae2c24838b9","type":"WriteReq","value":"017ca67c1842034f2042cfb2e5991ac8c2dd3d40007c96e0e9be28af1ef986ab9b","ts":"2025-03-05 12:11:30.985000"},"parsed":{"cargoHex":"","name":"request.control.StopTempRateRequest","messageProps":{"signed":true,"responseName":"response.control.StopTempRateResponse","responseOpCode":-89,"characteristicUuid":"7b83fffc-9f77-4e5c-8064-aae2c24838b9","type":"REQUEST","characteristic":"CONTROL","minApi":"MOBI_API_V3_5","size":0,"stream":false,"opCode":-90,"supportedDevices":"MOBI_ONLY","variableSize":false,"modifiesInsulinDelivery":true},"params":{"cargo":[]}}}
        // {"raw":{"btChar":"","guessedBtChar":"7b83fffc9f774e5c8064aae2c24838b9","type":"ReadResp","value":"007ca77c1b00610028034f20de866872a5759f0a90733baae767381b6184106f17ed","ts":"2025-03-05 12:11:30.983000"},"parsed":{"cargoHex":"006100","name":"response.control.StopTempRateResponse","messageProps":{"signed":true,"characteristicUuid":"7b83fffc-9f77-4e5c-8064-aae2c24838b9","type":"RESPONSE","characteristic":"CONTROL","requestName":"request.control.StopTempRateRequest","minApi":"MOBI_API_V3_5","size":3,"stream":false,"requestOpCode":-90,"opCode":-89,"supportedDevices":"MOBI_ONLY","variableSize":false,"modifiesInsulinDelivery":false},"params":{"id":97,"cargo":[0,97,0],"status":0}}}

        // TempRateCompletedHistoryLog[tempRateId=97,timeLeft=900000,cargo={15,16,40,3,79,32,-7,-15,0,0,8,0,97,0,-96,-69,13,0,0,0,0,0,0,0,0,0},pumpTimeSec=542049064,sequenceNum=61945]
        StopTempRateResponse expected = new StopTempRateResponse(
                0, 97
        );

        StopTempRateResponse parsedRes = (StopTempRateResponse) MessageTester.test(
                // 2024-12-22-mobi-screenrec.jsonl
                // 2024-12-22 09:46:38.384000
                "007ca77c1b00610028034f20de866872a5759f0a90733baae767381b6184106f17ed",
                124,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

}
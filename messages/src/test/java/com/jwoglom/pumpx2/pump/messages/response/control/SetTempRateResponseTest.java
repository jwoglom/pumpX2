package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.SetTempRateRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class SetTempRateResponseTest {
    @Test
    public void testSetTempRateResponse_101pct_15min() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        // Request:
//        SetTempRateRequest expected = new SetTempRateRequest(
//                new byte[]{-96, -69, 13, 0, 101, 0}
//        );
        // "0148a4481ea0bb0d00650016eaee1fc5720c4461",
        // "0048b8e23a6c4fb71331d0cbf3547996eb6cb5"

        SetTempRateResponse expected = new SetTempRateResponse(
                new byte[]{0, 15, 0, 0}
        );


        SetTempRateResponse parsedReq = (SetTempRateResponse) MessageTester.test(
                // 2024-12-22-mobi-screenrec.jsonl
                // 2024-12-22 09:46:31.486000
                "0048a5481c000f0000c3a3ee1fae161cb447c0235205f7a59796d2457a779c39c91b1a",
                72,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );
        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }


    @Test
    public void testSetTempRateResponse_2() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetTempRateResponse expected = new SetTempRateResponse(
                new byte[]{0, 15, 0, 0}
        );

        // {"raw":{"btChar":"","guessedBtChar":"7b83fff89f774e5c8064aae2c24838b9","type":"ReadResp","value":"000081005003453511c3a3ee1f0ba2000000000f000000000000000000000000002211c3a3ee1f0aa2000016eaee1fa400000000000000000000000210c3a3ee1f09a200000000ca4200ba5b4902000f0000000000b20a","ts":"2025-03-05 12:11:18.171000"},
        // "parsed":{"cargoHex":"03453511c3a3ee1f0ba2000000000f000000000000000000000000002211c3a3ee1f0aa2000016eaee1fa400000000000000000000000210c3a3ee1f09a200000000ca4200ba5b4902000f0000000000","name":"response.historyLog.HistoryLogStreamResponse","messageProps":{"signed":false,"characteristicUuid":"7b83fff8-9f77-4e5c-8064-aae2c24838b9","type":"RESPONSE","characteristic":"HISTORY_LOG","requestName":"request.historyLog.NonexistentHistoryLogStreamRequest","minApi":"API_V2_1","size":28,"stream":true,"requestOpCode":0,"opCode":-127,"supportedDevices":"ALL","variableSize":true,"modifiesInsulinDelivery":false},
        // "params":{"historyLogs":["UnknownHistoryLog[typeId=309,cargo={53,17,-61,-93,-18,31,11,-94,0,0,0,0,15,0,0,0,0,0,0,0,0,0,0,0,0,0},pumpTimeSec=535733187,sequenceNum=41483]","UnknownHistoryLog[typeId=290,cargo={34,17,-61,-93,-18,31,10,-94,0,0,22,-22,-18,31,-92,0,0,0,0,0,0,0,0,0,0,0},pumpTimeSec=535733187,sequenceNum=41482]","TempRateActivatedHistoryLog[duration=900000.0,percent=101.0,tempRateId=15,cargo={2,16,-61,-93,-18,31,9,-94,0,0,0,0,-54,66,0,-70,91,73,2,0,15,0,0,0,0,0},pumpTimeSec=535733187,sequenceNum=41481]"],"streamId":69,"numberOfHistoryLogs":3,"cargo":[3,69,53,17,-61,-93,-18,31,11,-94,0,0,0,0,15,0,0,0,0,0,0,0,0,0,0,0,0,0,34,17,-61,-93,-18,31,10,-94,0,0,22,-22,-18,31,-92,0,0,0,0,0,0,0,0,0,0,0,2,16,-61,-93,-18,31,9,-94,0,0,0,0,-54,66,0,-70,91,73,2,0,15,0,0,0,0,0]}}}

        // TempRateActivatedHistoryLog[duration=900000.0,percent=101.0,tempRateId=15,cargo={2,16,-61,-93,-18,31,9,-94,0,0,0,0,-54,66,0,-70,91,73,2,0,15,0,0,0,0,0},pumpTimeSec=535733187,sequenceNum=41481]

        SetTempRateResponse parsedReq = (SetTempRateResponse) MessageTester.test(
                // 2024-12-22-mobi-screenrec.jsonl
                // 2024-12-22 09:46:31.486000
                "0048a5481c000f0000c3a3ee1fae161cb447c0235205f7a59796d2457a779c39c91b1a",
                72,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );
        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }


    @Test
    public void testSetTempRateResponse_failed() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetTempRateResponse expected = new SetTempRateResponse(
                new byte[]{1,0,0,1}
        );

        assertEquals(1, expected.getStatus());
    }
}
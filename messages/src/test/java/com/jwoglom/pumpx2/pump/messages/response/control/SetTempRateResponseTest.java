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
    public void testSetTempRateResponse_failed() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetTempRateResponse expected = new SetTempRateResponse(
                new byte[]{1,0,0,1}
        );

        assertEquals(1, expected.getStatus());
    }
}
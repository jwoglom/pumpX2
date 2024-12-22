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
}
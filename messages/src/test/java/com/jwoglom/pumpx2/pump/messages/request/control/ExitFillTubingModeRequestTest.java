package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.ExitFillTubingModeRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ExitFillTubingModeRequestTest {
    @Test
    public void testExitFillTubingModeRequest() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        // empty cargo
        ExitFillTubingModeRequest expected = new ExitFillTubingModeRequest();

        ExitFillTubingModeRequest parsedReq = (ExitFillTubingModeRequest) MessageTester.test(
                "01919691181b0f4120ad6869443532522e06f02a",
                -111,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "0091687757e745d1f795a3295f"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}
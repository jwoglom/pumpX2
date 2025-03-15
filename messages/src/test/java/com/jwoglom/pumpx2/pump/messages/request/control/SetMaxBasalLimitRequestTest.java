package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.SetMaxBasalLimitRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class SetMaxBasalLimitRequestTest {
    @Test
    public void testSetMaxBasalLimitRequest_5u() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetMaxBasalLimitRequest expected = new SetMaxBasalLimitRequest(5_000);

        SetMaxBasalLimitRequest parsedReq = (SetMaxBasalLimitRequest) MessageTester.test(
                "017588751c881300003ee34920d580d42e829ec9",
                117,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "00754d9fdc7a1f65f92b9b1f7c227bbb56"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSetMaxBasalLimitRequest_lt1u_invalid() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        new SetMaxBasalLimitRequest(100);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSetMaxBasalLimitRequest_gt15u_invalid() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        new SetMaxBasalLimitRequest(16_000);
    }
}
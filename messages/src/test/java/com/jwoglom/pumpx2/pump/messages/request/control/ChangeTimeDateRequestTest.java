package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.ChangeTimeDateRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import java.time.Instant;

public class ChangeTimeDateRequestTest {
    @Test
    public void testChangeTimeDateRequest_raw() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        ChangeTimeDateRequest expected = new ChangeTimeDateRequest(new byte[]{
                48,
                23,
                57,
                32
        });

        ChangeTimeDateRequest parsedReq = (ChangeTimeDateRequest) MessageTester.test(
                "0143d6431c30173920965d39207d921e9438912a",
                67,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "0043998f1154a96aa50bda699dfde690a9"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(540612400, parsedReq.getTandemEpochTime());
        // 2025-02-17T02:06:40Z
        assertEquals(Instant.ofEpochSecond(1739758000), parsedReq.getInstant());
    }

    @Test
    public void testChangeTimeDateRequest_asInstant() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        ChangeTimeDateRequest expected = new ChangeTimeDateRequest(
                // 2025-02-17T02:06:40Z
                Instant.ofEpochSecond(1739758000)
        );

        ChangeTimeDateRequest parsedReq = (ChangeTimeDateRequest) MessageTester.test(
                "0143d6431c30173920965d39207d921e9438912a",
                67,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "0043998f1154a96aa50bda699dfde690a9"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(540612400, parsedReq.getTandemEpochTime());
        // 2025-02-17T02:06:40Z
        assertEquals(Instant.ofEpochSecond(1739758000), parsedReq.getInstant());
    }
}
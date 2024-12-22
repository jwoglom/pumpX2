package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.ChangeControlIQSettingsRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ChangeControlIQSettingsRequestTest {
    @Test
    public void testChangeControlIQSettingsRequest_turnOn_150lbs_75tdu() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        ChangeControlIQSettingsRequest expected = new ChangeControlIQSettingsRequest(
                true, 150, 75
        );

        ChangeControlIQSettingsRequest parsedReq = (ChangeControlIQSettingsRequest) MessageTester.test(
                // 2024-12-22-mobi-screenrec.jsonl
                // "2024-12-22 09:46:48.205000"
                "0157ca571e019600014b0127eaee1fe2a83a5338",
                87,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "00578298cbe960ee83e35b3d3fde85b38d7eba"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertTrue(parsedReq.isEnabled());
        assertEquals(150, parsedReq.getWeightLbs());
        assertEquals(75, parsedReq.getTotalDailyInsulinUnits());
    }

    @Test
    public void testChangeControlIQSettingsRequest_turnOff_150lbs_75tdu() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        ChangeControlIQSettingsRequest expected = new ChangeControlIQSettingsRequest(
                false, 150, 75
        );

        ChangeControlIQSettingsRequest parsedReq = (ChangeControlIQSettingsRequest) MessageTester.test(
                // 2024-12-22-mobi-screenrec.jsonl
                // "2024-12-22 09:46:52.435000"
                "015cca5c1e009600014b012ceaee1f556e63dee5",
                92,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "005ce9516bf5a6d77eb884dc557e841663bea5"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertFalse(parsedReq.isEnabled());
        assertEquals(150, parsedReq.getWeightLbs());
        assertEquals(75, parsedReq.getTotalDailyInsulinUnits());

    }
}
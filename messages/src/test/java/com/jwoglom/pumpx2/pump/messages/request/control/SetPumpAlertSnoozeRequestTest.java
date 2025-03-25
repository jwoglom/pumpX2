package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.SetPumpAlertSnoozeRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class SetPumpAlertSnoozeRequestTest {


    @Test
    public void testSetPumpAlertSnoozeRequest_enable_10min() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetPumpAlertSnoozeRequest expected = new SetPumpAlertSnoozeRequest(true, 10);

        SetPumpAlertSnoozeRequest parsedReq = (SetPumpAlertSnoozeRequest) MessageTester.test(
                "0116d4161a010a0db76820eabe11bbaf518d5a06",
                22,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "0016ef31958dafac47f38327cad7f7"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

    @Test
    public void testSetPumpAlertSnoozeRequest_enable_20min() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetPumpAlertSnoozeRequest expected = new SetPumpAlertSnoozeRequest(true, 20);

        SetPumpAlertSnoozeRequest parsedReq = (SetPumpAlertSnoozeRequest) MessageTester.test(
                "0118d4181a01142fb768207351c4815476eb350a",
                24,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "00184b210fb8263ff36ec4e138732f"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

    @Test
    public void testSetPumpAlertSnoozeRequest_off() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetPumpAlertSnoozeRequest expected = new SetPumpAlertSnoozeRequest(false, 0);

        SetPumpAlertSnoozeRequest parsedReq = (SetPumpAlertSnoozeRequest) MessageTester.test(
                "0131d4311a0000fbb8682038d7a22100f7646faa",
                49,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "00316f9d827605f665814a30dbe72b"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

}
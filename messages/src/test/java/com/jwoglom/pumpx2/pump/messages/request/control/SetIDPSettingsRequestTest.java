package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.SetIDPSettingsRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class SetIDPSettingsRequestTest {
    @Test
    public void testSetIDPSettingsRequest_profile1_carbEntryOn() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetIDPSettingsRequest expected = new SetIDPSettingsRequest(1, 240, 1, SetIDPSettingsRequest.ChangeType.CHANGE_CARB_ENTRY);

        SetIDPSettingsRequest parsedReq = (SetIDPSettingsRequest) MessageTester.test(
                "0109ac091e0101f00001043d493e20068b9bf3e9",
                9,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "0009a18a33a4d6cb387d989b15ea9fceda7fc7"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }


    @Test
    public void testSetIDPSettingsRequest_profile1_insulinDuration4h() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetIDPSettingsRequest expected = new SetIDPSettingsRequest(1, 240, 1, SetIDPSettingsRequest.ChangeType.CHANGE_INSULIN_DURATION);

        SetIDPSettingsRequest parsedReq = (SetIDPSettingsRequest) MessageTester.test(
                "01f5acf51e0101f000010122493e206e6dc237ab",
                -11,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "00f5dc6d11544e6f1fff07ae7674085f09651d"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
    @Test
    public void testSetIDPSettingsRequest_profile1_insulinDuration5h() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetIDPSettingsRequest expected = new SetIDPSettingsRequest(1, 300, 1, SetIDPSettingsRequest.ChangeType.CHANGE_INSULIN_DURATION);

        SetIDPSettingsRequest parsedReq = (SetIDPSettingsRequest) MessageTester.test(
                "0110ac101e01012c0101014f493e206cd2e4e729",
                16,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "0010597f8b90affef6e13b0d1f36e671197e34"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}
package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.CreateNewIDPRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CreateNewIDPRequestTest {
    @Test
    public void testCreateNewIDPRequest() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        // IDPSettingsResponse(idpId=1)
        // "params":{
        //   "maxBolus":25000,"numberOfProfileSegments":1,"idpId":1,"insulinDuration":300,
        //   "name":"testprofile","carbEntry":true,
        //   "cargo":[1,116,101,115,116,112,114,111,102,105,108,101,0,0,0,0,0,1,44,1,-88,97,1]
        // }

        // IDPSegmentResponse(idpId=1, segmentIndex=0)
        // "params":{
        //   "profileCarbRatio":3000,"statusId":15,"profileISF":2,"idpId":1,"profileStartTime":0,
        //   "segmentIndex":0,"profileTargetBG":100,"profileBasalRate":1000,
        //   "cargo":[1,0,0,0,-24,3,-72,11,0,0,100,0,2,0,15]
        // }

        CreateNewIDPRequest expected = new CreateNewIDPRequest(
                "testprofile",
                3000,
                1000,
                100,
                2,
                300,
                1
        );

        CreateNewIDPRequest parsedReq = (CreateNewIDPRequest) MessageTester.test(
                "03c9e6c93b7465737470726f66696c6500000000",
                -55,
                4,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "02c90000b80b00000000e803640002002c011f05",
                "01c9ff01b5483e20f4256e953cbe7320b25c5de0",
                "00c9f9e8fc891764dd51b865"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}
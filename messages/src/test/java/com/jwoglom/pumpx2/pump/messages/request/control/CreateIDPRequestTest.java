package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CreateIDPRequestTest {
    @Test
    public void testCreateIDPRequest_new1() throws DecoderException {
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

        CreateIDPRequest expected = new CreateIDPRequest(
                "testprofile",
                3000,
                1000,
                100,
                2,
                300,
                1
        );

        CreateIDPRequest parsedReq = (CreateIDPRequest) MessageTester.test(
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


    @Test
    public void testCreateIDPRequest_duplicate1() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        CreateIDPRequest expected = new CreateIDPRequest("dup", 1);

        CreateIDPRequest parsedReq = (CreateIDPRequest) MessageTester.test(
                "0337e6373b647570000000000000000000000000",
                55,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "0237000000000000000000000000000000000000",
                "01370100b0493e20b795d5f162b92bbd30cd8823",
                "00372bc754ba29764a5b4765"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}
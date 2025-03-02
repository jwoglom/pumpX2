package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.IDPSegmentResponse;

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


    @Test
    public void testScenario42_ProfC_Duplicate() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        CreateIDPRequest expected = new CreateIDPRequest("ProfCDup", 2);
        /*
["2025-02-27 10:30:15.211000","ReadResp","CONTROL","response.control.CreateIDPResponse","0068e7681a0002c5484720e3f36a7e0e4cb34a88f643dd0e09086d6e9836377afe",{"newIdpId":2,"cargo":[0,2],"status":0}]
["2025-02-27 10:30:15.213000","WriteReq","CONTROL","request.control.CreateIDPRequest","0368e6683b50726f664344757000000000000000026800000000000000000000000000000000000001680200d648472076197caf3096ac0d1c30075800684ef8851d5c1d36b8bd09",{"profileInsulinDuration":0,"profileName":"ProfCDup","sourceIdpId":2,"firstSegmentProfileTargetBG":0,"firstSegmentProfileBasalRate":0,"firstSegmentProfileISF":0,"profileCarbEntry":0,"firstSegmentProfileCarbRatio":0,"cargo":[80,114,111,102,67,68,117,112,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0]}]
["2025-02-27 10:30:15.272000","WriteReq",null,"QualifyingEvents","00000000",{"events":[]}]
["2025-02-27 10:30:15.333000","ReadResp",null,"QualifyingEvents","00200000",{"events":[{"event":"PROFILE_CHANGE"}]}]
["2025-02-27 10:30:15.336000","WriteReq","CURRENT_STATUS","request.currentStatus.ProfileStatusRequest","00693e69008aa3",{"cargo":[]}]
["2025-02-27 10:30:15.393000","ReadResp","CURRENT_STATUS","response.currentStatus.ProfileStatusResponse","00693f6908050104030200ff00a611",{"idpSlot0Id":1,"activeSegmentIndex":0,"idpSlot1Id":4,"idpSlot2Id":3,"idpSlot3Id":2,"idpSlot4Id":0,"cargo":[5,1,4,3,2,0,-1,0],"idpSlot5Id":-1,"numberOfProfiles":5}]
["2025-02-27 10:30:15.397000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSettingsRequest","006a406a010443c5",{"idpId":4,"cargo":[4]}]
["2025-02-27 10:30:15.453000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSettingsResponse","006a416a170450726f66434475700000000000000000022c01a861010d64",{"maxBolus":25000,"numberOfProfileSegments":2,"idpId":4,"insulinDuration":300,"name":"ProfCDup","cargo":[4,80,114,111,102,67,68,117,112,0,0,0,0,0,0,0,0,2,44,1,-88,97,1],"carbEntry":true}]
["2025-02-27 10:30:15.456000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","006b426b0204008ea1",{"idpId":4,"segmentIndex":0,"cargo":[4,0]}]
["2025-02-27 10:30:15.512000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","006b436b0f040000000000204e000064000a000f17c7",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["CARB_RATIO","BASAL_RATE","TARGET_BG","CORRECTION_FACTOR"],"profileISF":10,"idpId":4,"profileStartTime":0,"segmentIndex":0,"profileTargetBG":100,"cargo":[4,0,0,0,0,0,32,78,0,0,100,0,10,0,15],"profileBasalRate":0}]
["2025-02-27 10:30:18.938000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","006c426c02040182e0",{"idpId":4,"segmentIndex":1,"cargo":[4,1]}]
["2025-02-27 10:30:19.173000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","006c436c0f04013c006400204e000064000a000f1b44",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["CARB_RATIO","BASAL_RATE","TARGET_BG","CORRECTION_FACTOR"],"profileISF":10,"idpId":4,"profileStartTime":60,"segmentIndex":1,"profileTargetBG":100,"cargo":[4,1,60,0,100,0,32,78,0,0,100,0,10,0,15],"profileBasalRate":100}]
["2025-02-27 10:30:19.176000","WriteReq","CURRENT_STATUS","request.currentStatus.TimeSinceResetRequest","006d366d00efc6",{"cargo":[]}]
["2025-02-27 10:30:19.233000","ReadResp","CURRENT_STATUS","response.currentStatus.TimeSinceResetResponse","006d376d08c94847201bdf0200a574",{"currentTime":541542601,"pumpTimeSinceReset":188187,"currentTimeInstant":"2025-02-27T20:30:01Z","cargo":[-55,72,71,32,27,-33,2,0]}]


         */

        CreateIDPRequest parsedReq = (CreateIDPRequest) MessageTester.test(
                "0368e6683b50726f664344757000000000000000",
                104,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "0268000000000000000000000000000000000000",
                "01680200d648472076197caf3096ac0d1c300758",
                "00684ef8851d5c1d36b8bd09"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}
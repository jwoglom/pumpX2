package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.IDPSegmentResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class SetIDPSegmentRequestTest {
    @Test
    public void testSetIDPSegmentRequest_idp1_segment0_midnight() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        SetIDPSegmentRequest expected = new SetIDPSegmentRequest(
                1, 1, 0,
                SetIDPSegmentRequest.IDPSegmentOperation.MODIFY_SEGMENT_ID,
                0, 2000, 3000, 100, 2,
                IDPSegmentResponse.IDPSegmentStatus.toBitmask(IDPSegmentResponse.IDPSegmentStatus.BASAL_RATE)
        );

        // GetIdpSegment
        // "params":{"profileCarbRatio":3000,"statusId":15,"profileISF":2,"idpId":1,"profileStartTime":0,"segmentIndex":0,"profileTargetBG":100,"cargo":[1,0,0,0,-48,7,-72,11,0,0,100,0,2,0,15],"profileBasalRate":2000}}}

        SetIDPSegmentRequest parsedReq = (SetIDPSegmentRequest) MessageTester.test(
                "022daa2d29010100000000d007b80b0000640002",
                45,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "012d00018f493e20e5c9cdb2d482ccc51cb25c03",
                "002dbc127447c7c401957f01"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }


    @Test
    public void testSetIDPSegmentRequest_idp1_segment1new_1am() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        SetIDPSegmentRequest expected = new SetIDPSegmentRequest(
                1, 1, 0,
                SetIDPSegmentRequest.IDPSegmentOperation.CREATE_SEGMENT,
                60, 1000, 3000, 100, 2,
                IDPSegmentResponse.IDPSegmentStatus.toBitmask( // 31
                        IDPSegmentResponse.IDPSegmentStatus.BASAL_RATE,
                        IDPSegmentResponse.IDPSegmentStatus.CARB_RATIO,
                        IDPSegmentResponse.IDPSegmentStatus.CORRECTION_FACTOR,
                        IDPSegmentResponse.IDPSegmentStatus.TARGET_BG,
                        IDPSegmentResponse.IDPSegmentStatus.START_TIME)
        );

        // IdpSegmentResponse(1,0)
        // "params":{"profileCarbRatio":3000,"statusId":15,"profileISF":2,"idpId":1,"profileStartTime":0,"segmentIndex":0,"profileTargetBG":100,"cargo":[1,0,0,0,-24,3,-72,11,0,0,100,0,2,0,15],"profileBasalRate":1000}}}
        // IdpSegmentResponse(1,1) << NEW
        // "params":{"profileCarbRatio":3000,"statusId":15,"profileISF":2,"idpId":1,"profileStartTime":60,"segmentIndex":1,"profileTargetBG":100,"cargo":[1,1,60,0,-24,3,-72,11,0,0,100,0,2,0,15],"profileBasalRate":1000}}}

        SetIDPSegmentRequest parsedReq = (SetIDPSegmentRequest) MessageTester.test(
                "02e1aae129010100013c00e803b80b0000640002",
                -31,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "01e1001ff9483e203d291074f2f3bf6ac7a0cd81",
                "00e12ea287e849d9a355b90b"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }


    @Test
    public void testSetIDPSegmentRequest_idp1_segment1_DELETE() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        SetIDPSegmentRequest expected = new SetIDPSegmentRequest(
                1, 1, 1,
                SetIDPSegmentRequest.IDPSegmentOperation.DELETE_SEGMENT_ID,
                60, 1000, 3000, 100, 2,
                31 // ??
        );

        // IdpSegmentResponse(1,0)
        // "params":{"profileCarbRatio":3000,"statusId":15,"profileISF":2,"idpId":1,"profileStartTime":0,"segmentIndex":0,"profileTargetBG":100,"cargo":[1,0,0,0,-24,3,-72,11,0,0,100,0,2,0,15],"profileBasalRate":1000}}}
        // IdpSegmentResponse(1,1) << NEW
        // "params":{"profileCarbRatio":3000,"statusId":15,"profileISF":2,"idpId":1,"profileStartTime":60,"segmentIndex":1,"profileTargetBG":100,"cargo":[1,1,60,0,-24,3,-72,11,0,0,100,0,2,0,15],"profileBasalRate":1000}}}

        SetIDPSegmentRequest parsedReq = (SetIDPSegmentRequest) MessageTester.test(
                "02ebaaeb29010101023c00e803b80b0000640002",
                -21,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "01eb001f0d493e20f3d3fd4546228ab2f88b6a5c",
                "00ebf1e0a4b8bfe566ed1e18"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }


    @Test
    public void testScenario01_NewIdpSegment8am() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        // Create new IDP segment after segment 0 (midnight) for 480 (8am)
        SetIDPSegmentRequest expected = new SetIDPSegmentRequest(
                1, 0, 0,
                SetIDPSegmentRequest.IDPSegmentOperation.CREATE_SEGMENT,
                480, 800, 20_000, 100, 10,
                IDPSegmentResponse.IDPSegmentStatus.toBitmask( // 31
                        IDPSegmentResponse.IDPSegmentStatus.BASAL_RATE,
                        IDPSegmentResponse.IDPSegmentStatus.CARB_RATIO,
                        IDPSegmentResponse.IDPSegmentStatus.CORRECTION_FACTOR,
                        IDPSegmentResponse.IDPSegmentStatus.TARGET_BG,
                        IDPSegmentResponse.IDPSegmentStatus.START_TIME)
        );
        /*
["2025-02-27 08:28:32.215000","ReadResp","CONTROL","response.control.SetIDPSegmentResponse","00beabbe1a00023e2c4720c4b672744dc4eb533cd0840e563f4bfa168217c0f018",{"cargo":[0,2]}]
["2025-02-27 08:28:32.278000","WriteReq","CONTROL","request.control.SetIDPSegmentRequest","02beaabe2901000001e0012003204e000064000a01be001f4f2c47203ab6442634108746c361cf9300be6a4346676362e432e2f4",{"idpStatusId":31,"profileCarbRatio":20000,"idpStatus":["BASAL_RATE","CARB_RATIO","CORRECTION_FACTOR","TARGET_BG"],"profileISF":10,"idpId":1,"profileStartTime":480,"operationId":1,"segmentIndex":0,"profileTargetBG":100,"cargo":[1,0,0,1,-32,1,32,3,32,78,0,0,100,0,10,0,31],"operation":"CREATE_AFTER","profileBasalRate":800}]
["2025-02-27 08:28:33.029000","WriteReq","CURRENT_STATUS","request.currentStatus.ProfileStatusRequest","00c23ec2000e62",{"cargo":[]}]
["2025-02-27 08:28:33.088000","ReadResp","CURRENT_STATUS","response.currentStatus.ProfileStatusResponse","00c23fc208020100ffffffff01563b",{"idpSlot0Id":1,"activeSegmentIndex":1,"idpSlot1Id":0,"idpSlot2Id":-1,"idpSlot3Id":-1,"idpSlot4Id":-1,"cargo":[2,1,0,-1,-1,-1,-1,1],"idpSlot5Id":-1,"numberOfProfiles":2}]
["2025-02-27 08:28:33.209000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSettingsRequest","00c340c30101ebb6",{"idpId":1,"cargo":[1]}]
["2025-02-27 08:28:33.267000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSettingsResponse","00c341c317017a65726f000000000000000000000000022c01a8610184cf",{"maxBolus":25000,"numberOfProfileSegments":2,"idpId":1,"insulinDuration":300,"name":"zero","cargo":[1,122,101,114,111,0,0,0,0,0,0,0,0,0,0,0,0,2,44,1,-88,97,1],"carbEntry":true}]
["2025-02-27 08:28:33.389000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00c442c4020100e360",{"idpId":1,"segmentIndex":0,"cargo":[1,0]}]
["2025-02-27 08:28:33.447000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","00c443c40f010000000000e8030000640001000f3c52",{"idpStatusId":15,"profileCarbRatio":1000,"idpStatus":["BASAL_RATE","CARB_RATIO","CORRECTION_FACTOR","TARGET_BG"],"profileISF":1,"idpId":1,"profileStartTime":0,"segmentIndex":0,"profileTargetBG":100,"cargo":[1,0,0,0,0,0,-24,3,0,0,100,0,1,0,15],"profileBasalRate":0}]
["2025-02-27 08:28:33.626000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","00c543c50f0101e0012003204e000064000a000fb67a",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["BASAL_RATE","CARB_RATIO","CORRECTION_FACTOR","TARGET_BG"],"profileISF":10,"idpId":1,"profileStartTime":480,"segmentIndex":1,"profileTargetBG":100,"cargo":[1,1,-32,1,32,3,32,78,0,0,100,0,10,0,15],"profileBasalRate":800}]
["2025-02-27 08:28:35.491000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00c542c50201017606",{"idpId":1,"segmentIndex":1,"cargo":[1,1]}]

         */

        SetIDPSegmentRequest parsedReq = (SetIDPSegmentRequest) MessageTester.test(
                "02beaabe2901000001e0012003204e000064000a",
                -66,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "01be001f4f2c47203ab6442634108746c361cf93",
                "00be6a4346676362e432e2f4"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }


    @Test
    public void testScenario02_NewIdpSegment9pm() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        // Create new IDP segment for 1260 (9pm)
        SetIDPSegmentRequest expected = new SetIDPSegmentRequest(
                1, 0, 0,
                SetIDPSegmentRequest.IDPSegmentOperation.CREATE_SEGMENT,
                12*60 + 9*60, 900, 20_000, 100, 10,
                IDPSegmentResponse.IDPSegmentStatus.toBitmask(
                        IDPSegmentResponse.IDPSegmentStatus.BASAL_RATE,
                        IDPSegmentResponse.IDPSegmentStatus.CARB_RATIO,
                        IDPSegmentResponse.IDPSegmentStatus.CORRECTION_FACTOR,
                        IDPSegmentResponse.IDPSegmentStatus.TARGET_BG,
                        IDPSegmentResponse.IDPSegmentStatus.START_TIME)
        );
        /*
["2025-02-27 08:28:59.666000","ReadResp","CONTROL","response.control.SetIDPSegmentResponse","00ccabcc1a0002592c472052a88a7903ce4baf653c00f15a3b3d66d100ed76b531",{"cargo":[0,2]}]
["2025-02-27 08:28:59.789000","WriteReq","CONTROL","request.control.SetIDPSegmentRequest","02ccaacc2901000001ec048403204e000064000a01cc001f6b2c4720123382409ff68413ef1f778a00ccba1823b05b1a2fb748f0",{"idpStatusId":31,"profileCarbRatio":20000,"idpStatus":["CARB_RATIO","CORRECTION_FACTOR","TARGET_BG","BASAL_RATE"],"profileISF":10,"idpId":1,"profileStartTime":1260,"operationId":1,"segmentIndex":0,"profileTargetBG":100,"cargo":[1,0,0,1,-20,4,-124,3,32,78,0,0,100,0,10,0,31],"operation":"CREATE_AFTER","profileBasalRate":900}]
["2025-02-27 08:28:59.817000","ReadResp",null,"QualifyingEvents","00200000",{"events":[{"event":"PROFILE_CHANGE"}]}]
["2025-02-27 08:28:59.847000","WriteReq",null,"QualifyingEvents","00000000",{"events":[]}]
["2025-02-27 08:28:59.942000","WriteReq","CURRENT_STATUS","request.currentStatus.ProfileStatusRequest","00cd3ecd003072",{"cargo":[]}]
["2025-02-27 08:28:59.967000","ReadResp","CURRENT_STATUS","response.currentStatus.ProfileStatusResponse","00cd3fcd08020100ffffffff01e76d",{"idpSlot0Id":1,"activeSegmentIndex":1,"idpSlot1Id":0,"idpSlot2Id":-1,"idpSlot3Id":-1,"idpSlot4Id":-1,"cargo":[2,1,0,-1,-1,-1,-1,1],"idpSlot5Id":-1,"numberOfProfiles":2}]
["2025-02-27 08:29:00.030000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSettingsRequest","00ce40ce0101baf4",{"idpId":1,"cargo":[1]}]
["2025-02-27 08:29:00.056000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSettingsResponse","00ce41ce17017a65726f000000000000000000000000032c01a8610183c4",{"maxBolus":25000,"numberOfProfileSegments":3,"idpId":1,"insulinDuration":300,"name":"zero","cargo":[1,122,101,114,111,0,0,0,0,0,0,0,0,0,0,0,0,3,44,1,-88,97,1],"carbEntry":true}]
["2025-02-27 08:29:00.120000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00cf42cf020100fc7e",{"idpId":1,"segmentIndex":0,"cargo":[1,0]}]
["2025-02-27 08:29:00.148000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","00cf43cf0f010000000000e8030000640001000f0ae4",{"idpStatusId":15,"profileCarbRatio":1000,"idpStatus":["CARB_RATIO","CORRECTION_FACTOR","TARGET_BG","BASAL_RATE"],"profileISF":1,"idpId":1,"profileStartTime":0,"segmentIndex":0,"profileTargetBG":100,"cargo":[1,0,0,0,0,0,-24,3,0,0,100,0,1,0,15],"profileBasalRate":0}]
["2025-02-27 08:29:00.212000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00d042d002010194a1",{"idpId":1,"segmentIndex":1,"cargo":[1,1]}]
["2025-02-27 08:29:00.238000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","00d043d00f0101e0012003204e000064000a000fdd37",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["CARB_RATIO","CORRECTION_FACTOR","TARGET_BG","BASAL_RATE"],"profileISF":10,"idpId":1,"profileStartTime":480,"segmentIndex":1,"profileTargetBG":100,"cargo":[1,1,-32,1,32,3,32,78,0,0,100,0,10,0,15],"profileBasalRate":800}]
["2025-02-27 08:29:00.326000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","00d143d10f0102ec048403204e000064000a000f5dd8",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["CARB_RATIO","CORRECTION_FACTOR","TARGET_BG","BASAL_RATE"],"profileISF":10,"idpId":1,"profileStartTime":1260,"segmentIndex":2,"profileTargetBG":100,"cargo":[1,2,-20,4,-124,3,32,78,0,0,100,0,10,0,15],"profileBasalRate":900}]
["2025-02-27 08:29:02.194000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00d142d102010243e7",{"idpId":1,"segmentIndex":2,"cargo":[1,2]}]


         */

        SetIDPSegmentRequest parsedReq = (SetIDPSegmentRequest) MessageTester.test(
                "02ccaacc2901000001ec048403204e000064000a",
                -52,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "01cc001f6b2c4720123382409ff68413ef1f778a",
                "00ccba1823b05b1a2fb748f0"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }


    @Test
    public void testScenario03_Change9pmTo9am() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        // Modify existing segment for 9pm to start at 540 (9am)
        SetIDPSegmentRequest expected = new SetIDPSegmentRequest(
                1, 0, 2,
                SetIDPSegmentRequest.IDPSegmentOperation.MODIFY_SEGMENT_ID,
                540, 900, 20_000, 100, 10,
                IDPSegmentResponse.IDPSegmentStatus.toBitmask(
                        IDPSegmentResponse.IDPSegmentStatus.START_TIME)
        );
        /*
["2025-02-27 08:29:29.486000","ReadResp","CONTROL","response.control.SetIDPSegmentResponse","00d3abd31a0002772c47207d8c14b777ffea551a23a849b9f5aad3d2ffccfc26a1",{"cargo":[0,2]}]
["2025-02-27 08:29:29.488000","WriteReq","CONTROL","request.control.SetIDPSegmentRequest","02d3aad329010002001c028403204e000064000a01d30010882c4720170d4703a73a2a611b117cf400d3c6271cb71a36f667772e",{"idpStatusId":16,"profileCarbRatio":20000,"idpStatus":[],"profileISF":10,"idpId":1,"profileStartTime":540,"operationId":0,"segmentIndex":2,"profileTargetBG":100,"cargo":[1,0,2,0,28,2,-124,3,32,78,0,0,100,0,10,0,16],"operation":"MODIFY","profileBasalRate":900}]
["2025-02-27 08:29:29.547000","WriteReq",null,"QualifyingEvents","00000000",{"events":[]}]
["2025-02-27 08:29:29.606000","ReadResp",null,"QualifyingEvents","00220000",{"events":[{"event":"BASAL_CHANGE"},{"event":"PROFILE_CHANGE"}]}]
["2025-02-27 08:29:29.614000","WriteReq","CURRENT_STATUS","request.currentStatus.ControlIQIOBRequest","00d46cd40075fb",{"cargo":[]}]
["2025-02-27 08:29:29.697000","ReadResp","CURRENT_STATUS","response.currentStatus.ControlIQIOBResponse","00d46dd411000000000000000000000000000000000084ea",{"mudaliarTotalIOB":0,"iobType":0,"timeRemainingSeconds":0,"swan6hrIOB":0,"cargo":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],"mudaliarIOB":0}]
["2025-02-27 08:29:29.700000","WriteReq","00d528d5002909","Unexpected cargo size: 0, expecting 50 for opCode=40"]
["2025-02-27 08:29:29.757000","ReadResp","00d529d509840300000000000001cf3d","Unexpected cargo size: 9, expecting 50 for opCode=41"]
["2025-02-27 08:29:29.762000","WriteReq","CURRENT_STATUS","request.currentStatus.UnknownMobiOpcode30Request","00d61ed6007f2b",{"cargo":[]}]
["2025-02-27 08:29:29.817000","ReadResp","CURRENT_STATUS","response.currentStatus.UnknownMobiOpcode30Response","00d61fd6100000000000000000ffffffff00000000a9e6",{"unknown4":0,"unknown3":4294967295,"unknown2":0,"unknown1":0,"cargo":[0,0,0,0,0,0,0,0,-1,-1,-1,-1,0,0,0,0]}]
["2025-02-27 08:29:29.820000","WriteReq","CURRENT_STATUS","request.currentStatus.ProfileStatusRequest","00d73ed700889e",{"cargo":[]}]
["2025-02-27 08:29:29.877000","ReadResp","CURRENT_STATUS","response.currentStatus.ProfileStatusResponse","00d73fd708020100ffffffff02d35d",{"idpSlot0Id":1,"activeSegmentIndex":2,"idpSlot1Id":0,"idpSlot2Id":-1,"idpSlot3Id":-1,"idpSlot4Id":-1,"cargo":[2,1,0,-1,-1,-1,-1,2],"idpSlot5Id":-1,"numberOfProfiles":2}]
["2025-02-27 08:29:29.882000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSettingsRequest","00d840d801017905",{"idpId":1,"cargo":[1]}]
["2025-02-27 08:29:29.937000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSettingsResponse","00d841d817017a65726f000000000000000000000000032c01a86101de52",{"maxBolus":25000,"numberOfProfileSegments":3,"idpId":1,"insulinDuration":300,"name":"zero","cargo":[1,122,101,114,111,0,0,0,0,0,0,0,0,0,0,0,0,3,44,1,-88,97,1],"carbEntry":true}]
["2025-02-27 08:29:29.940000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00d942d9020100c242",{"idpId":1,"segmentIndex":0,"cargo":[1,0]}]
["2025-02-27 08:29:29.996000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","00d943d90f010000000000e8030000640001000f4798",{"idpStatusId":15,"profileCarbRatio":1000,"idpStatus":["BASAL_RATE","CARB_RATIO","TARGET_BG","CORRECTION_FACTOR"],"profileISF":1,"idpId":1,"profileStartTime":0,"segmentIndex":0,"profileTargetBG":100,"cargo":[1,0,0,0,0,0,-24,3,0,0,100,0,1,0,15],"profileBasalRate":0}]
["2025-02-27 08:29:30","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00da42da0201013fc9",{"idpId":1,"segmentIndex":1,"cargo":[1,1]}]
["2025-02-27 08:29:30.056000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","00da43da0f0101e0012003204e000064000a000f0991",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["BASAL_RATE","CARB_RATIO","TARGET_BG","CORRECTION_FACTOR"],"profileISF":10,"idpId":1,"profileStartTime":480,"segmentIndex":1,"profileTargetBG":100,"cargo":[1,1,-32,1,32,3,32,78,0,0,100,0,10,0,15],"profileBasalRate":800}]
["2025-02-27 08:29:33.773000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00db42db020102e88f",{"idpId":1,"segmentIndex":2,"cargo":[1,2]}]
["2025-02-27 08:29:34.617000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","00db43db0f01021c028403204e000064000a000f1ba1",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["BASAL_RATE","CARB_RATIO","TARGET_BG","CORRECTION_FACTOR"],"profileISF":10,"idpId":1,"profileStartTime":540,"segmentIndex":2,"profileTargetBG":100,"cargo":[1,2,28,2,-124,3,32,78,0,0,100,0,10,0,15],"profileBasalRate":900}]


         */

        SetIDPSegmentRequest parsedReq = (SetIDPSegmentRequest) MessageTester.test(
                "02d3aad329010002001c028403204e000064000a",
                -45,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "01d30010882c4720170d4703a73a2a611b117cf4",
                "00d3c6271cb71a36f667772e"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

    @Test
    public void testScenario04_New10am() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        // New segment for 10am (600)
        SetIDPSegmentRequest expected = new SetIDPSegmentRequest(
                1, 0, 0,
                SetIDPSegmentRequest.IDPSegmentOperation.CREATE_SEGMENT,
                600, 1_000, 20_000, 100, 10,
                IDPSegmentResponse.IDPSegmentStatus.toBitmask( // 31
                        IDPSegmentResponse.IDPSegmentStatus.BASAL_RATE,
                        IDPSegmentResponse.IDPSegmentStatus.CARB_RATIO,
                        IDPSegmentResponse.IDPSegmentStatus.CORRECTION_FACTOR,
                        IDPSegmentResponse.IDPSegmentStatus.TARGET_BG,
                        IDPSegmentResponse.IDPSegmentStatus.START_TIME)
        );
        /*
["2025-02-27 08:30:04.646000","ReadResp","CONTROL","response.control.SetIDPSegmentResponse","00e5abe51a00029a2c472055c083c0dce86589fe5c23c9a8316a55e917b5070dfb",{"cargo":[0,2]}]
["2025-02-27 08:30:04.648000","WriteReq","CONTROL","request.control.SetIDPSegmentRequest","02e5aae529010000015802e803204e000064000a01e5001fab2c47206889b14804479e1ad72797d400e510ebcdf9ad9a27cd030c",{"idpStatusId":31,"profileCarbRatio":20000,"idpStatus":["CORRECTION_FACTOR","BASAL_RATE","CARB_RATIO","TARGET_BG"],"profileISF":10,"idpId":1,"profileStartTime":600,"operationId":1,"segmentIndex":0,"profileTargetBG":100,"cargo":[1,0,0,1,88,2,-24,3,32,78,0,0,100,0,10,0,31],"operation":"CREATE_AFTER","profileBasalRate":1000}]
["2025-02-27 08:30:04.709000","WriteReq",null,"QualifyingEvents","00000000",{"events":[]}]
["2025-02-27 08:30:04.767000","ReadResp",null,"QualifyingEvents","00220000",{"events":[{"event":"BASAL_CHANGE"},{"event":"PROFILE_CHANGE"}]}]
["2025-02-27 08:30:04.771000","WriteReq","CURRENT_STATUS","request.currentStatus.ControlIQIOBRequest","00e66ce6008298",{"cargo":[]}]
["2025-02-27 08:30:04.827000","ReadResp","CURRENT_STATUS","response.currentStatus.ControlIQIOBResponse","00e66de61100000000000000000000000000000000007c16",{"mudaliarTotalIOB":0,"iobType":0,"timeRemainingSeconds":0,"swan6hrIOB":0,"cargo":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],"mudaliarIOB":0}]
["2025-02-27 08:30:04.830000","WriteReq","00e728e700de6a","Unexpected cargo size: 0, expecting 50 for opCode=40"]
["2025-02-27 08:30:04.916000","ReadResp","00e729e709e803000000000000010e2f","Unexpected cargo size: 9, expecting 50 for opCode=41"]
["2025-02-27 08:30:04.919000","WriteReq","CURRENT_STATUS","request.currentStatus.UnknownMobiOpcode30Request","00e81ee800e50d",{"cargo":[]}]
["2025-02-27 08:30:04.977000","ReadResp","CURRENT_STATUS","response.currentStatus.UnknownMobiOpcode30Response","00e81fe8100000000000000000ffffffff000000000210",{"unknown4":0,"unknown3":4294967295,"unknown2":0,"unknown1":0,"cargo":[0,0,0,0,0,0,0,0,-1,-1,-1,-1,0,0,0,0]}]
["2025-02-27 08:30:04.978000","WriteReq","CURRENT_STATUS","request.currentStatus.ProfileStatusRequest","00e93ee90012b8",{"cargo":[]}]
["2025-02-27 08:30:05.037000","ReadResp","CURRENT_STATUS","response.currentStatus.ProfileStatusResponse","00e93fe908020100ffffffff039dd9",{"idpSlot0Id":1,"activeSegmentIndex":3,"idpSlot1Id":0,"idpSlot2Id":-1,"idpSlot3Id":-1,"idpSlot4Id":-1,"cargo":[2,1,0,-1,-1,-1,-1,3],"idpSlot5Id":-1,"numberOfProfiles":2}]
["2025-02-27 08:30:05.041000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSettingsRequest","00ea40ea0101bcae",{"idpId":1,"cargo":[1]}]
["2025-02-27 08:30:05.097000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSettingsResponse","00ea41ea17017a65726f000000000000000000000000042c01a86101bb3d",{"maxBolus":25000,"numberOfProfileSegments":4,"idpId":1,"insulinDuration":300,"name":"zero","cargo":[1,122,101,114,111,0,0,0,0,0,0,0,0,0,0,0,0,4,44,1,-88,97,1],"carbEntry":true}]
["2025-02-27 08:30:05.100000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00eb42eb0201004383",{"idpId":1,"segmentIndex":0,"cargo":[1,0]}]
["2025-02-27 08:30:05.157000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","00eb43eb0f010000000000e8030000640001000f809b",{"idpStatusId":15,"profileCarbRatio":1000,"idpStatus":["CORRECTION_FACTOR","BASAL_RATE","CARB_RATIO","TARGET_BG"],"profileISF":1,"idpId":1,"profileStartTime":0,"segmentIndex":0,"profileTargetBG":100,"cargo":[1,0,0,0,0,0,-24,3,0,0,100,0,1,0,15],"profileBasalRate":0}]
["2025-02-27 08:30:05.160000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00ec42ec0201014fc2",{"idpId":1,"segmentIndex":1,"cargo":[1,1]}]
["2025-02-27 08:30:05.217000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","00ec43ec0f0101e0012003204e000064000a000f46d1",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["CORRECTION_FACTOR","BASAL_RATE","CARB_RATIO","TARGET_BG"],"profileISF":10,"idpId":1,"profileStartTime":480,"segmentIndex":1,"profileTargetBG":100,"cargo":[1,1,-32,1,32,3,32,78,0,0,100,0,10,0,15],"profileBasalRate":800}]
["2025-02-27 08:30:05.219000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00ed42ed0201029884",{"idpId":1,"segmentIndex":2,"cargo":[1,2]}]
["2025-02-27 08:30:05.277000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","00ed43ed0f01021c028403204e000064000a000f54e1",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["CORRECTION_FACTOR","BASAL_RATE","CARB_RATIO","TARGET_BG"],"profileISF":10,"idpId":1,"profileStartTime":540,"segmentIndex":2,"profileTargetBG":100,"cargo":[1,2,28,2,-124,3,32,78,0,0,100,0,10,0,15],"profileBasalRate":900}]
["2025-02-27 08:30:08.910000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00ee42ee020103650f",{"idpId":1,"segmentIndex":3,"cargo":[1,3]}]
["2025-02-27 08:30:08.996000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","00ee43ee0f01035802e803204e000064000a000f08e9",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["CORRECTION_FACTOR","BASAL_RATE","CARB_RATIO","TARGET_BG"],"profileISF":10,"idpId":1,"profileStartTime":600,"segmentIndex":3,"profileTargetBG":100,"cargo":[1,3,88,2,-24,3,32,78,0,0,100,0,10,0,15],"profileBasalRate":1000}]


         */

        SetIDPSegmentRequest parsedReq = (SetIDPSegmentRequest) MessageTester.test(
                "02e5aae529010000015802e803204e000064000a",
                -27,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "01e5001fab2c47206889b14804479e1ad72797d4",
                "00e510ebcdf9ad9a27cd030c"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

    @Test
    public void testScenario05_New5pm() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        // New segment for 5pm (1380)
        SetIDPSegmentRequest expected = new SetIDPSegmentRequest(
                1, 0, 0,
                SetIDPSegmentRequest.IDPSegmentOperation.CREATE_SEGMENT,
                1380, 1_100, 20_000, 100, 10,
                IDPSegmentResponse.IDPSegmentStatus.toBitmask( // 31
                        IDPSegmentResponse.IDPSegmentStatus.BASAL_RATE,
                        IDPSegmentResponse.IDPSegmentStatus.CARB_RATIO,
                        IDPSegmentResponse.IDPSegmentStatus.CORRECTION_FACTOR,
                        IDPSegmentResponse.IDPSegmentStatus.TARGET_BG,
                        IDPSegmentResponse.IDPSegmentStatus.START_TIME)
        );
        /*
["2025-02-27 08:30:35.936000","ReadResp","CONTROL","response.control.SetIDPSegmentResponse","00f7abf71a0002ba2c4720060f9c190d353adbc1d2164eb9de04deb12df6e2664a",{"cargo":[0,2]}]
["2025-02-27 08:30:36.059000","WriteReq","CONTROL","request.control.SetIDPSegmentRequest","02f7aaf7290100000164054c04204e000064000a01f7001fcb2c4720a80890da27608f1e5c0cb48600f74ab88a5a2659c670bbb2",{"idpStatusId":31,"profileCarbRatio":20000,"idpStatus":["CORRECTION_FACTOR","BASAL_RATE","CARB_RATIO","TARGET_BG"],"profileISF":10,"idpId":1,"profileStartTime":1380,"operationId":1,"segmentIndex":0,"profileTargetBG":100,"cargo":[1,0,0,1,100,5,76,4,32,78,0,0,100,0,10,0,31],"operation":"CREATE_AFTER","profileBasalRate":1100}]
["2025-02-27 08:30:36.088000","ReadResp",null,"QualifyingEvents","00200000",{"events":[{"event":"PROFILE_CHANGE"}]}]
["2025-02-27 08:30:36.118000","WriteReq",null,"QualifyingEvents","00000000",{"events":[]}]
["2025-02-27 08:30:36.181000","WriteReq","CURRENT_STATUS","request.currentStatus.ProfileStatusRequest","00f83ef8005088",{"cargo":[]}]
["2025-02-27 08:30:36.208000","ReadResp","CURRENT_STATUS","response.currentStatus.ProfileStatusResponse","00f83ff808020100ffffffff034e22",{"idpSlot0Id":1,"activeSegmentIndex":3,"idpSlot1Id":0,"idpSlot2Id":-1,"idpSlot3Id":-1,"idpSlot4Id":-1,"cargo":[2,1,0,-1,-1,-1,-1,3],"idpSlot5Id":-1,"numberOfProfiles":2}]
["2025-02-27 08:30:36.270000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSettingsRequest","00f940f901018fb4",{"idpId":1,"cargo":[1]}]
["2025-02-27 08:30:36.327000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSettingsResponse","00f941f917017a65726f000000000000000000000000052c01a8610103ad",{"maxBolus":25000,"numberOfProfileSegments":5,"idpId":1,"insulinDuration":300,"name":"zero","cargo":[1,122,101,114,111,0,0,0,0,0,0,0,0,0,0,0,0,5,44,1,-88,97,1],"carbEntry":true}]
["2025-02-27 08:30:36.510000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00fb42fb020100e498",{"idpId":1,"segmentIndex":0,"cargo":[1,0]}]
["2025-02-27 08:30:36.567000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","00fb43fb0f010000000000e8030000640001000f8185",{"idpStatusId":15,"profileCarbRatio":1000,"idpStatus":["CORRECTION_FACTOR","BASAL_RATE","CARB_RATIO","TARGET_BG"],"profileISF":1,"idpId":1,"profileStartTime":0,"segmentIndex":0,"profileTargetBG":100,"cargo":[1,0,0,0,0,0,-24,3,0,0,100,0,1,0,15],"profileBasalRate":0}]
["2025-02-27 08:30:36.689000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00fc42fc020101e8d9",{"idpId":1,"segmentIndex":1,"cargo":[1,1]}]
["2025-02-27 08:30:36.748000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","00fc43fc0f0101e0012003204e000064000a000f47cf",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["CORRECTION_FACTOR","BASAL_RATE","CARB_RATIO","TARGET_BG"],"profileISF":10,"idpId":1,"profileStartTime":480,"segmentIndex":1,"profileTargetBG":100,"cargo":[1,1,-32,1,32,3,32,78,0,0,100,0,10,0,15],"profileBasalRate":800}]
["2025-02-27 08:30:36.869000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00fd42fd0201023f9f",{"idpId":1,"segmentIndex":2,"cargo":[1,2]}]
["2025-02-27 08:30:36.928000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","00fd43fd0f01021c028403204e000064000a000f55ff",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["CORRECTION_FACTOR","BASAL_RATE","CARB_RATIO","TARGET_BG"],"profileISF":10,"idpId":1,"profileStartTime":540,"segmentIndex":2,"profileTargetBG":100,"cargo":[1,2,28,2,-124,3,32,78,0,0,100,0,10,0,15],"profileBasalRate":900}]
["2025-02-27 08:30:37.049000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00fe42fe020103c214",{"idpId":1,"segmentIndex":3,"cargo":[1,3]}]
["2025-02-27 08:30:37.107000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","00fe43fe0f01035802e803204e000064000a000f09f7",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["CORRECTION_FACTOR","BASAL_RATE","CARB_RATIO","TARGET_BG"],"profileISF":10,"idpId":1,"profileStartTime":600,"segmentIndex":3,"profileTargetBG":100,"cargo":[1,3,88,2,-24,3,32,78,0,0,100,0,10,0,15],"profileBasalRate":1000}]
["2025-02-27 08:30:37.287000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","00ff43ff0f010464054c04204e000064000a000fe0c4",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["CORRECTION_FACTOR","BASAL_RATE","CARB_RATIO","TARGET_BG"],"profileISF":10,"idpId":1,"profileStartTime":1380,"segmentIndex":4,"profileTargetBG":100,"cargo":[1,4,100,5,76,4,32,78,0,0,100,0,10,0,15],"profileBasalRate":1100}]
["2025-02-27 08:30:41.726000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00ff42ff0201049112",{"idpId":1,"segmentIndex":4,"cargo":[1,4]}]


         */

        SetIDPSegmentRequest parsedReq = (SetIDPSegmentRequest) MessageTester.test(
                "02f7aaf7290100000164054c04204e000064000a",
                -9,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "01f7001fcb2c4720a80890da27608f1e5c0cb486",
                "00f74ab88a5a2659c670bbb2"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }


    @Test
    public void testScenario06_DeleteSegment10am() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        // Delete segment for 10am
        SetIDPSegmentRequest expected = new SetIDPSegmentRequest(
                1, 0, 3,
                SetIDPSegmentRequest.IDPSegmentOperation.DELETE_SEGMENT_ID,
                600, 1_000, 20_000, 100, 10,
                IDPSegmentResponse.IDPSegmentStatus.toBitmask( // 31
                        IDPSegmentResponse.IDPSegmentStatus.BASAL_RATE,
                        IDPSegmentResponse.IDPSegmentStatus.CARB_RATIO,
                        IDPSegmentResponse.IDPSegmentStatus.CORRECTION_FACTOR,
                        IDPSegmentResponse.IDPSegmentStatus.TARGET_BG,
                        IDPSegmentResponse.IDPSegmentStatus.START_TIME)
        );
        /*
["2025-02-27 08:31:03.147000","ReadResp","CONTROL","response.control.SetIDPSegmentResponse","0005ab051a0002d52c472027d507e0e3ead117e408c3c0604bd691d61884b673c2",{"cargo":[0,2]}]
["2025-02-27 08:31:03.296000","WriteReq","CONTROL","request.control.SetIDPSegmentRequest","0205aa0529010003025802e803204e000064000a0105001fe62c4720070f244c8b4c8a3b5a3928fd00052a1947f769ca78019ec5",{"idpStatusId":31,"profileCarbRatio":20000,"idpStatus":["BASAL_RATE","CARB_RATIO","CORRECTION_FACTOR","TARGET_BG"],"profileISF":10,"idpId":1,"profileStartTime":600,"operationId":2,"segmentIndex":3,"profileTargetBG":100,"cargo":[1,0,3,2,88,2,-24,3,32,78,0,0,100,0,10,0,31],"operation":"DELETE","profileBasalRate":1000}]
["2025-02-27 08:31:03.328000","ReadResp",null,"QualifyingEvents","00220000",{"events":[{"event":"PROFILE_CHANGE"},{"event":"BASAL_CHANGE"}]}]
["2025-02-27 08:31:03.358000","WriteReq",null,"QualifyingEvents","00000000",{"events":[]}]
["2025-02-27 08:31:03.451000","WriteReq","CURRENT_STATUS","request.currentStatus.ControlIQIOBRequest","00066c06003088",{"cargo":[]}]
["2025-02-27 08:31:03.477000","ReadResp","CURRENT_STATUS","response.currentStatus.ControlIQIOBResponse","00066d061100000000000000000000000000000000007b25",{"mudaliarTotalIOB":0,"iobType":0,"timeRemainingSeconds":0,"swan6hrIOB":0,"cargo":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],"mudaliarIOB":0}]
["2025-02-27 08:31:03.539000","WriteReq","00072807006c7a","Unexpected cargo size: 0, expecting 50 for opCode=40"]
["2025-02-27 08:31:03.567000","ReadResp","00072907098403000000000000013e05","Unexpected cargo size: 9, expecting 50 for opCode=41"]
["2025-02-27 08:31:03.630000","WriteReq","CURRENT_STATUS","request.currentStatus.UnknownMobiOpcode30Request","00081e0800571d",{"cargo":[]}]
["2025-02-27 08:31:03.657000","ReadResp","CURRENT_STATUS","response.currentStatus.UnknownMobiOpcode30Response","00081f08100000000000000000ffffffff000000005df9",{"unknown4":0,"unknown3":4294967295,"unknown2":0,"unknown1":0,"cargo":[0,0,0,0,0,0,0,0,-1,-1,-1,-1,0,0,0,0]}]
["2025-02-27 08:31:03.721000","WriteReq","CURRENT_STATUS","request.currentStatus.ProfileStatusRequest","00093e0900a0a8",{"cargo":[]}]
["2025-02-27 08:31:03.747000","ReadResp","CURRENT_STATUS","response.currentStatus.ProfileStatusResponse","00093f0908020100ffffffff029f66",{"idpSlot0Id":1,"activeSegmentIndex":2,"idpSlot1Id":0,"idpSlot2Id":-1,"idpSlot3Id":-1,"idpSlot4Id":-1,"cargo":[2,1,0,-1,-1,-1,-1,2],"idpSlot5Id":-1,"numberOfProfiles":2}]
["2025-02-27 08:31:03.812000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSettingsRequest","000a400a01018d0e",{"idpId":1,"cargo":[1]}]
["2025-02-27 08:31:03.837000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSettingsResponse","000a410a17017a65726f000000000000000000000000042c01a8610103bb",{"maxBolus":25000,"numberOfProfileSegments":4,"idpId":1,"insulinDuration":300,"name":"zero","cargo":[1,122,101,114,111,0,0,0,0,0,0,0,0,0,0,0,0,4,44,1,-88,97,1],"carbEntry":true}]
["2025-02-27 08:31:03.901000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","000b420b020100a907",{"idpId":1,"segmentIndex":0,"cargo":[1,0]}]
["2025-02-27 08:31:03.928000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","000b430b0f010000000000e8030000640001000f8e2f",{"idpStatusId":15,"profileCarbRatio":1000,"idpStatus":["BASAL_RATE","CARB_RATIO","CORRECTION_FACTOR","TARGET_BG"],"profileISF":1,"idpId":1,"profileStartTime":0,"segmentIndex":0,"profileTargetBG":100,"cargo":[1,0,0,0,0,0,-24,3,0,0,100,0,1,0,15],"profileBasalRate":0}]
["2025-02-27 08:31:03.990000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","000c420c020101a546",{"idpId":1,"segmentIndex":1,"cargo":[1,1]}]
["2025-02-27 08:31:04.017000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","000c430c0f0101e0012003204e000064000a000f4865",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["BASAL_RATE","CARB_RATIO","CORRECTION_FACTOR","TARGET_BG"],"profileISF":10,"idpId":1,"profileStartTime":480,"segmentIndex":1,"profileTargetBG":100,"cargo":[1,1,-32,1,32,3,32,78,0,0,100,0,10,0,15],"profileBasalRate":800}]
["2025-02-27 08:31:04.080000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","000d420d0201027200",{"idpId":1,"segmentIndex":2,"cargo":[1,2]}]
["2025-02-27 08:31:04.107000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","000d430d0f01021c028403204e000064000a000f5a55",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["BASAL_RATE","CARB_RATIO","CORRECTION_FACTOR","TARGET_BG"],"profileISF":10,"idpId":1,"profileStartTime":540,"segmentIndex":2,"profileTargetBG":100,"cargo":[1,2,28,2,-124,3,32,78,0,0,100,0,10,0,15],"profileBasalRate":900}]
["2025-02-27 08:31:04.196000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","000e430e0f010364054c04204e000064000a000f0b0e",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["BASAL_RATE","CARB_RATIO","CORRECTION_FACTOR","TARGET_BG"],"profileISF":10,"idpId":1,"profileStartTime":1380,"segmentIndex":3,"profileTargetBG":100,"cargo":[1,3,100,5,76,4,32,78,0,0,100,0,10,0,15],"profileBasalRate":1100}]
["2025-02-27 08:31:04.349000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","000e420e0201038f8b",{"idpId":1,"segmentIndex":3,"cargo":[1,3]}]


         */

        SetIDPSegmentRequest parsedReq = (SetIDPSegmentRequest) MessageTester.test(
                "0205aa0529010003025802e803204e000064000a",
                5,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "0105001fe62c4720070f244c8b4c8a3b5a3928fd",
                "00052a1947f769ca78019ec5"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

    @Test
    public void testScenario07_NewSegment1am() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        // New segment for 1am (60)
        SetIDPSegmentRequest expected = new SetIDPSegmentRequest(
                1, 0, 0,
                SetIDPSegmentRequest.IDPSegmentOperation.CREATE_SEGMENT,
                60, 100, 1000, 100, 1,
                IDPSegmentResponse.IDPSegmentStatus.toBitmask( // 31
                        IDPSegmentResponse.IDPSegmentStatus.BASAL_RATE,
                        IDPSegmentResponse.IDPSegmentStatus.CARB_RATIO,
                        IDPSegmentResponse.IDPSegmentStatus.CORRECTION_FACTOR,
                        IDPSegmentResponse.IDPSegmentStatus.TARGET_BG,
                        IDPSegmentResponse.IDPSegmentStatus.START_TIME)
        );
        /*
["2025-02-27 08:31:30.085000","ReadResp","CONTROL","response.control.SetIDPSegmentResponse","0010ab101a0002f02c4720f38440797bf92b73933b867851e17016adeca5d34a33",{"cargo":[0,2]}]
["2025-02-27 08:31:30.088000","WriteReq","CONTROL","request.control.SetIDPSegmentRequest","0210aa1029010000013c006400e80300006400010110001f002d4720e8a8dee8607cf1a5abe649c90010bff0bcc438643ccc6621",{"idpStatusId":31,"profileCarbRatio":1000,"idpStatus":["BASAL_RATE","CARB_RATIO","CORRECTION_FACTOR","TARGET_BG"],"profileISF":1,"idpId":1,"profileStartTime":60,"operationId":1,"segmentIndex":0,"profileTargetBG":100,"cargo":[1,0,0,1,60,0,100,0,-24,3,0,0,100,0,1,0,31],"operation":"CREATE_AFTER","profileBasalRate":100}]
["2025-02-27 08:31:30.145000","WriteReq",null,"QualifyingEvents","00000000",{"events":[]}]
["2025-02-27 08:31:30.207000","ReadResp",null,"QualifyingEvents","00200000",{"events":[{"event":"PROFILE_CHANGE"}]}]
["2025-02-27 08:31:30.210000","WriteReq","CURRENT_STATUS","request.currentStatus.ProfileStatusRequest","00113e11007a22",{"cargo":[]}]
["2025-02-27 08:31:30.266000","ReadResp","CURRENT_STATUS","response.currentStatus.ProfileStatusResponse","00113f1108020100ffffffff0363a8",{"idpSlot0Id":1,"activeSegmentIndex":3,"idpSlot1Id":0,"idpSlot2Id":-1,"idpSlot3Id":-1,"idpSlot4Id":-1,"cargo":[2,1,0,-1,-1,-1,-1,3],"idpSlot5Id":-1,"numberOfProfiles":2}]
["2025-02-27 08:31:30.272000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSettingsRequest","0012401201014fe4",{"idpId":1,"cargo":[1]}]
["2025-02-27 08:31:30.327000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSettingsResponse","0012411217017a65726f000000000000000000000000052c01a8610185e8",{"maxBolus":25000,"numberOfProfileSegments":5,"idpId":1,"insulinDuration":300,"name":"zero","cargo":[1,122,101,114,111,0,0,0,0,0,0,0,0,0,0,0,0,5,44,1,-88,97,1],"carbEntry":true}]
["2025-02-27 08:31:30.329000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00134213020100cd99",{"idpId":1,"segmentIndex":0,"cargo":[1,0]}]
["2025-02-27 08:31:30.387000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","001343130f010000000000e8030000640001000f9fb6",{"idpStatusId":15,"profileCarbRatio":1000,"idpStatus":["BASAL_RATE","CARB_RATIO","CORRECTION_FACTOR","TARGET_BG"],"profileISF":1,"idpId":1,"profileStartTime":0,"segmentIndex":0,"profileTargetBG":100,"cargo":[1,0,0,0,0,0,-24,3,0,0,100,0,1,0,15],"profileBasalRate":0}]
["2025-02-27 08:31:30.390000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00144214020101c1d8",{"idpId":1,"segmentIndex":1,"cargo":[1,1]}]
["2025-02-27 08:31:30.446000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","001443140f01013c006400e8030000640001000f9335",{"idpStatusId":15,"profileCarbRatio":1000,"idpStatus":["BASAL_RATE","CARB_RATIO","CORRECTION_FACTOR","TARGET_BG"],"profileISF":1,"idpId":1,"profileStartTime":60,"segmentIndex":1,"profileTargetBG":100,"cargo":[1,1,60,0,100,0,-24,3,0,0,100,0,1,0,15],"profileBasalRate":100}]
["2025-02-27 08:31:30.449000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00154215020102169e",{"idpId":1,"segmentIndex":2,"cargo":[1,2]}]
["2025-02-27 08:31:30.507000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","001543150f0102e0012003204e000064000a000f1861",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["BASAL_RATE","CARB_RATIO","CORRECTION_FACTOR","TARGET_BG"],"profileISF":10,"idpId":1,"profileStartTime":480,"segmentIndex":2,"profileTargetBG":100,"cargo":[1,2,-32,1,32,3,32,78,0,0,100,0,10,0,15],"profileBasalRate":800}]
["2025-02-27 08:31:30.510000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00164216020103eb15",{"idpId":1,"segmentIndex":3,"cargo":[1,3]}]
["2025-02-27 08:31:30.566000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","001643160f01031c028403204e000064000a000f0c86",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["BASAL_RATE","CARB_RATIO","CORRECTION_FACTOR","TARGET_BG"],"profileISF":10,"idpId":1,"profileStartTime":540,"segmentIndex":3,"profileTargetBG":100,"cargo":[1,3,28,2,-124,3,32,78,0,0,100,0,10,0,15],"profileBasalRate":900}]
["2025-02-27 08:31:34.276000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00174217020104b813",{"idpId":1,"segmentIndex":4,"cargo":[1,4]}]
["2025-02-27 08:31:35.126000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","001743170f010464054c04204e000064000a000ffef7",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["BASAL_RATE","CARB_RATIO","CORRECTION_FACTOR","TARGET_BG"],"profileISF":10,"idpId":1,"profileStartTime":1380,"segmentIndex":4,"profileTargetBG":100,"cargo":[1,4,100,5,76,4,32,78,0,0,100,0,10,0,15],"profileBasalRate":1100}]


         */

        SetIDPSegmentRequest parsedReq = (SetIDPSegmentRequest) MessageTester.test(
                "0210aa1029010000013c006400e8030000640001",
                16,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "0110001f002d4720e8a8dee8607cf1a5abe649c9",
                "0010bff0bcc438643ccc6621"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

    @Test
    public void testScenario08_Change8amTo2am() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        // Change segment at 8am (currently segment index 2) to 2am
        SetIDPSegmentRequest expected = new SetIDPSegmentRequest(
                1, 0, 2,
                SetIDPSegmentRequest.IDPSegmentOperation.MODIFY_SEGMENT_ID,
                120, 200, 20_000, 100, 10,
                IDPSegmentResponse.IDPSegmentStatus.toBitmask( // 31
                        IDPSegmentResponse.IDPSegmentStatus.BASAL_RATE,
                        IDPSegmentResponse.IDPSegmentStatus.START_TIME)
        );
        /*
["2025-02-27 08:31:49.619000","ReadResp","","Index 2 out of bounds for length 0"]
["2025-02-27 08:31:57.431000","WriteReq","CONTROL","request.control.SetIDPSegmentRequest","021faa1f29010002007800c800204e000064000a011f00111d2d47200a31dba06b57c2183a139f9b001f6ccc67dd2a85f0d01031",{"idpStatusId":17,"profileCarbRatio":20000,"idpStatus":["BASAL_RATE"],"profileISF":10,"idpId":1,"profileStartTime":120,"operationId":0,"segmentIndex":2,"profileTargetBG":100,"cargo":[1,0,2,0,120,0,-56,0,32,78,0,0,100,0,10,0,17],"operation":"MODIFY","profileBasalRate":200}]
["2025-02-27 08:31:57.447000","ReadResp","CONTROL","response.control.SetIDPSegmentResponse","001fab1f1a00020b2d472066692193750665551f536ee8e21da9ddbe3df4635662",{"cargo":[0,2]}]
["2025-02-27 08:31:57.507000","WriteReq","CURRENT_STATUS","request.currentStatus.TimeSinceResetRequest","00203620007fbd",{"cargo":[]}]
["2025-02-27 08:31:57.625000","WriteReq",null,"QualifyingEvents","00000000",{"events":[]}]
["2025-02-27 08:31:57.688000","WriteReq","CURRENT_STATUS","request.currentStatus.ProfileStatusRequest","00223e2200bc72",{"cargo":[]}]
["2025-02-27 08:31:57.717000","ReadResp","CURRENT_STATUS","response.currentStatus.ProfileStatusResponse","00223f2208020100ffffffff0337b4",{"idpSlot0Id":1,"activeSegmentIndex":3,"idpSlot1Id":0,"idpSlot2Id":-1,"idpSlot3Id":-1,"idpSlot4Id":-1,"cargo":[2,1,0,-1,-1,-1,-1,3],"idpSlot5Id":-1,"numberOfProfiles":2}]
["2025-02-27 08:31:57.778000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSettingsRequest","002340230101da16",{"idpId":1,"cargo":[1]}]
["2025-02-27 08:31:57.807000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSettingsResponse","0023412317017a65726f000000000000000000000000052c01a861017d81",{"maxBolus":25000,"numberOfProfileSegments":5,"idpId":1,"insulinDuration":300,"name":"zero","cargo":[1,122,101,114,111,0,0,0,0,0,0,0,0,0,0,0,0,5,44,1,-88,97,1],"carbEntry":true}]
["2025-02-27 08:31:57.867000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","0024422402010009e4",{"idpId":1,"segmentIndex":0,"cargo":[1,0]}]
["2025-02-27 08:31:57.897000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","002443240f010000000000e8030000640001000f32e6",{"idpStatusId":15,"profileCarbRatio":1000,"idpStatus":["TARGET_BG","BASAL_RATE","CORRECTION_FACTOR","CARB_RATIO"],"profileISF":1,"idpId":1,"profileStartTime":0,"segmentIndex":0,"profileTargetBG":100,"cargo":[1,0,0,0,0,0,-24,3,0,0,100,0,1,0,15],"profileBasalRate":0}]
["2025-02-27 08:31:57.958000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","002542250201019c82",{"idpId":1,"segmentIndex":1,"cargo":[1,1]}]
["2025-02-27 08:31:57.988000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","002543250f01013c006400e8030000640001000f7207",{"idpStatusId":15,"profileCarbRatio":1000,"idpStatus":["TARGET_BG","BASAL_RATE","CORRECTION_FACTOR","CARB_RATIO"],"profileISF":1,"idpId":1,"profileStartTime":60,"segmentIndex":1,"profileTargetBG":100,"cargo":[1,1,60,0,100,0,-24,3,0,0,100,0,1,0,15],"profileBasalRate":100}]
["2025-02-27 08:31:58.048000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","002642260201022329",{"idpId":1,"segmentIndex":2,"cargo":[1,2]}]
["2025-02-27 08:31:58.077000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","002643260f01027800c800204e000064000a000f42ad",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["TARGET_BG","BASAL_RATE","CORRECTION_FACTOR","CARB_RATIO"],"profileISF":10,"idpId":1,"profileStartTime":120,"segmentIndex":2,"profileTargetBG":100,"cargo":[1,2,120,0,-56,0,32,78,0,0,100,0,10,0,15],"profileBasalRate":200}]
["2025-02-27 08:31:58.167000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00274227020103b64f",{"idpId":1,"segmentIndex":3,"cargo":[1,3]}]
["2025-02-27 08:31:58.197000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","002743270f01031c028403204e000064000a000fedb4",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["TARGET_BG","BASAL_RATE","CORRECTION_FACTOR","CARB_RATIO"],"profileISF":10,"idpId":1,"profileStartTime":540,"segmentIndex":3,"profileTargetBG":100,"cargo":[1,3,28,2,-124,3,32,78,0,0,100,0,10,0,15],"profileBasalRate":900}]
["2025-02-27 08:31:58.285000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","002843280f010464054c04204e000064000a000f4320",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["TARGET_BG","BASAL_RATE","CORRECTION_FACTOR","CARB_RATIO"],"profileISF":10,"idpId":1,"profileStartTime":1380,"segmentIndex":4,"profileTargetBG":100,"cargo":[1,4,100,5,76,4,32,78,0,0,100,0,10,0,15],"profileBasalRate":1100}]
["2025-02-27 08:32:01.961000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00284228020104bfeb",{"idpId":1,"segmentIndex":4,"cargo":[1,4]}]

         */

        SetIDPSegmentRequest parsedReq = (SetIDPSegmentRequest) MessageTester.test(
                "021faa1f29010002007800c800204e000064000a",
                31,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "011f00111d2d47200a31dba06b57c2183a139f9b",
                "001f6ccc67dd2a85f0d01031"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

    @Test
    public void testScenario09_Change9amTo3am() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        // Change segment at 8am (currently segment index 2) to 2am
        SetIDPSegmentRequest expected = new SetIDPSegmentRequest(
                1, 0, 3,
                SetIDPSegmentRequest.IDPSegmentOperation.MODIFY_SEGMENT_ID,
                180, 300, 20_000, 100, 10,
                IDPSegmentResponse.IDPSegmentStatus.toBitmask( // 31
                        IDPSegmentResponse.IDPSegmentStatus.BASAL_RATE,
                        IDPSegmentResponse.IDPSegmentStatus.START_TIME)
        );
        /*
["2025-02-27 08:32:07.586000","ReadResp","","Index 2 out of bounds for length 0"]
["2025-02-27 08:32:24.309000","WriteReq","CONTROL","request.control.SetIDPSegmentRequest","0230aa302901000300b4002c01204e000064000a01300011382d4720ca9c5606c6c8673ea18d67e80030952e74edd52c25721c5b",{"idpStatusId":17,"profileCarbRatio":20000,"idpStatus":["BASAL_RATE"],"profileISF":10,"idpId":1,"profileStartTime":180,"operationId":0,"segmentIndex":3,"profileTargetBG":100,"cargo":[1,0,3,0,-76,0,44,1,32,78,0,0,100,0,10,0,17],"operation":"MODIFY","profileBasalRate":300}]
["2025-02-27 08:32:24.386000","ReadResp","CONTROL","response.control.SetIDPSegmentResponse","0030ab301a0002262d4720caff44686792dc9a1c4795ab129e74c2e0be7b7f4000",{"cargo":[0,2]}]
["2025-02-27 08:32:24.597000","ReadResp",null,"QualifyingEvents","00220000",{"events":[{"event":"PROFILE_CHANGE"},{"event":"BASAL_CHANGE"}]}]
["2025-02-27 08:32:24.597000","WriteReq",null,"QualifyingEvents","00000000",{"events":[]}]
["2025-02-27 08:32:24.661000","WriteReq","CURRENT_STATUS","request.currentStatus.ControlIQIOBRequest","00326c32006141",{"cargo":[]}]
["2025-02-27 08:32:24.688000","ReadResp","CURRENT_STATUS","response.currentStatus.ControlIQIOBResponse","00326d32110000000000000000000000000000000000833d",{"mudaliarTotalIOB":0,"iobType":0,"timeRemainingSeconds":0,"swan6hrIOB":0,"cargo":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],"mudaliarIOB":0}]
["2025-02-27 08:32:24.751000","WriteReq","00332833003db3","Unexpected cargo size: 0, expecting 50 for opCode=40"]
["2025-02-27 08:32:24.777000","ReadResp","00332933092c01000000000000012915","Unexpected cargo size: 9, expecting 50 for opCode=41"]
["2025-02-27 08:32:24.839000","WriteReq","CURRENT_STATUS","request.currentStatus.UnknownMobiOpcode30Request","00341e3400af5d",{"cargo":[]}]
["2025-02-27 08:32:24.867000","ReadResp","CURRENT_STATUS","response.currentStatus.UnknownMobiOpcode30Response","00341f34100000000000000000ffffffff00000000b5ff",{"unknown4":0,"unknown3":4294967295,"unknown2":0,"unknown1":0,"cargo":[0,0,0,0,0,0,0,0,-1,-1,-1,-1,0,0,0,0]}]
["2025-02-27 08:32:24.928000","WriteReq","CURRENT_STATUS","request.currentStatus.ProfileStatusRequest","00353e350058e8",{"cargo":[]}]
["2025-02-27 08:32:24.957000","ReadResp","CURRENT_STATUS","response.currentStatus.ProfileStatusResponse","00353f3508020100ffffffff035b3c",{"idpSlot0Id":1,"activeSegmentIndex":3,"idpSlot1Id":0,"idpSlot2Id":-1,"idpSlot3Id":-1,"idpSlot4Id":-1,"cargo":[2,1,0,-1,-1,-1,-1,3],"idpSlot5Id":-1,"numberOfProfiles":2}]
["2025-02-27 08:32:25.021000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSettingsRequest","00364036010149be",{"idpId":1,"cargo":[1]}]
["2025-02-27 08:32:25.047000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSettingsResponse","0036413617017a65726f000000000000000000000000052c01a86101fcd9",{"maxBolus":25000,"numberOfProfileSegments":5,"idpId":1,"insulinDuration":300,"name":"zero","cargo":[1,122,101,114,111,0,0,0,0,0,0,0,0,0,0,0,0,5,44,1,-88,97,1],"carbEntry":true}]
["2025-02-27 08:32:25.109000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","003742370201007264",{"idpId":1,"segmentIndex":0,"cargo":[1,0]}]
["2025-02-27 08:32:25.137000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","003743370f010000000000e8030000640001000f15c9",{"idpStatusId":15,"profileCarbRatio":1000,"idpStatus":["CARB_RATIO","BASAL_RATE","TARGET_BG","CORRECTION_FACTOR"],"profileISF":1,"idpId":1,"profileStartTime":0,"segmentIndex":0,"profileTargetBG":100,"cargo":[1,0,0,0,0,0,-24,3,0,0,100,0,1,0,15],"profileBasalRate":0}]
["2025-02-27 08:32:25.198000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00384238020101bda0",{"idpId":1,"segmentIndex":1,"cargo":[1,1]}]
["2025-02-27 08:32:25.228000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","003843380f01013c006400e8030000640001000f09cd",{"idpStatusId":15,"profileCarbRatio":1000,"idpStatus":["CARB_RATIO","BASAL_RATE","TARGET_BG","CORRECTION_FACTOR"],"profileISF":1,"idpId":1,"profileStartTime":60,"segmentIndex":1,"profileTargetBG":100,"cargo":[1,1,60,0,100,0,-24,3,0,0,100,0,1,0,15],"profileBasalRate":100}]
["2025-02-27 08:32:25.292000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","003942390201026ae6",{"idpId":1,"segmentIndex":2,"cargo":[1,2]}]
["2025-02-27 08:32:25.316000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","003943390f01027800c800204e000064000a000ffd46",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["CARB_RATIO","BASAL_RATE","TARGET_BG","CORRECTION_FACTOR"],"profileISF":10,"idpId":1,"profileStartTime":120,"segmentIndex":2,"profileTargetBG":100,"cargo":[1,2,120,0,-56,0,32,78,0,0,100,0,10,0,15],"profileBasalRate":200}]
["2025-02-27 08:32:25.349000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","003a423a020103976d",{"idpId":1,"segmentIndex":3,"cargo":[1,3]}]
["2025-02-27 08:32:25.407000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","003a433a0f0103b4002c01204e000064000a000f3ab4",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["CARB_RATIO","BASAL_RATE","TARGET_BG","CORRECTION_FACTOR"],"profileISF":10,"idpId":1,"profileStartTime":180,"segmentIndex":3,"profileTargetBG":100,"cargo":[1,3,-76,0,44,1,32,78,0,0,100,0,10,0,15],"profileBasalRate":300}]
["2025-02-27 08:32:29.111000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","003b423b020104c46b",{"idpId":1,"segmentIndex":4,"cargo":[1,4]}]
["2025-02-27 08:32:29.966000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","003b433b0f010464054c04204e000064000a000f640f",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["CARB_RATIO","BASAL_RATE","TARGET_BG","CORRECTION_FACTOR"],"profileISF":10,"idpId":1,"profileStartTime":1380,"segmentIndex":4,"profileTargetBG":100,"cargo":[1,4,100,5,76,4,32,78,0,0,100,0,10,0,15],"profileBasalRate":1100}]

         */

        SetIDPSegmentRequest parsedReq = (SetIDPSegmentRequest) MessageTester.test(
                "0230aa302901000300b4002c01204e000064000a",
                48,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "01300011382d4720ca9c5606c6c8673ea18d67e8",
                "0030952e74edd52c25721c5b"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

    @Test
    public void testScenario10_Delete1am() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        // Delete segment at 1am (segment id 1)
        SetIDPSegmentRequest expected = new SetIDPSegmentRequest(
                1, 0, 1,
                SetIDPSegmentRequest.IDPSegmentOperation.DELETE_SEGMENT_ID,
                60, 100, 1_000, 100, 1,
                IDPSegmentResponse.IDPSegmentStatus.toBitmask( // 31
                        IDPSegmentResponse.IDPSegmentStatus.BASAL_RATE,
                        IDPSegmentResponse.IDPSegmentStatus.CARB_RATIO,
                        IDPSegmentResponse.IDPSegmentStatus.TARGET_BG,
                        IDPSegmentResponse.IDPSegmentStatus.CORRECTION_FACTOR,
                        IDPSegmentResponse.IDPSegmentStatus.START_TIME)
        );
        /*
["2025-02-27 08:32:45.355000","ReadResp","CONTROL","response.control.SetIDPSegmentResponse","0040ab401a00023b2d4720801ef96049a8f33187339b410aef114ab3ac9e54030a",{"cargo":[0,2]}]
["2025-02-27 08:32:45.357000","WriteReq","CONTROL","request.control.SetIDPSegmentRequest","0240aa4029010001023c006400e80300006400010140001f4c2d4720d8f5c1e02bec75b2726879440040f5dd6d354ec61dcf6c4b",{"idpStatusId":31,"profileCarbRatio":1000,"idpStatus":["CARB_RATIO","TARGET_BG","CORRECTION_FACTOR","BASAL_RATE"],"profileISF":1,"idpId":1,"profileStartTime":60,"operationId":2,"segmentIndex":1,"profileTargetBG":100,"cargo":[1,0,1,2,60,0,100,0,-24,3,0,0,100,0,1,0,31],"operation":"DELETE","profileBasalRate":100}]
["2025-02-27 08:32:45.415000","WriteReq",null,"QualifyingEvents","00000000",{"events":[]}]
["2025-02-27 08:32:45.476000","ReadResp",null,"QualifyingEvents","00200000",{"events":[{"event":"PROFILE_CHANGE"}]}]
["2025-02-27 08:32:45.480000","WriteReq","CURRENT_STATUS","request.currentStatus.ProfileStatusRequest","00413e4100c52c",{"cargo":[]}]
["2025-02-27 08:32:45.536000","ReadResp","CURRENT_STATUS","response.currentStatus.ProfileStatusResponse","00413f4108020100ffffffff02ce5e",{"idpSlot0Id":1,"activeSegmentIndex":2,"idpSlot1Id":0,"idpSlot2Id":-1,"idpSlot3Id":-1,"idpSlot4Id":-1,"cargo":[2,1,0,-1,-1,-1,-1,2],"idpSlot5Id":-1,"numberOfProfiles":2}]
["2025-02-27 08:32:45.541000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSettingsRequest","00424042010181ba",{"idpId":1,"cargo":[1]}]
["2025-02-27 08:32:45.598000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSettingsResponse","0042414217017a65726f000000000000000000000000042c01a86101f1d9",{"maxBolus":25000,"numberOfProfileSegments":4,"idpId":1,"insulinDuration":300,"name":"zero","cargo":[1,122,101,114,111,0,0,0,0,0,0,0,0,0,0,0,0,4,44,1,-88,97,1],"carbEntry":true}]
["2025-02-27 08:32:45.601000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00434243020100f6ec",{"idpId":1,"segmentIndex":0,"cargo":[1,0]}]
["2025-02-27 08:32:45.656000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","004343430f010000000000e8030000640001000f9ad0",{"idpStatusId":15,"profileCarbRatio":1000,"idpStatus":["CARB_RATIO","TARGET_BG","CORRECTION_FACTOR","BASAL_RATE"],"profileISF":1,"idpId":1,"profileStartTime":0,"segmentIndex":0,"profileTargetBG":100,"cargo":[1,0,0,0,0,0,-24,3,0,0,100,0,1,0,15],"profileBasalRate":0}]
["2025-02-27 08:32:45.659000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00444244020101faad",{"idpId":1,"segmentIndex":1,"cargo":[1,1]}]
["2025-02-27 08:32:45.716000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","004443440f01017800c800204e000064000a000f2345",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["CARB_RATIO","TARGET_BG","CORRECTION_FACTOR","BASAL_RATE"],"profileISF":10,"idpId":1,"profileStartTime":120,"segmentIndex":1,"profileTargetBG":100,"cargo":[1,1,120,0,-56,0,32,78,0,0,100,0,10,0,15],"profileBasalRate":200}]
["2025-02-27 08:32:45.719000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","004542450201022deb",{"idpId":1,"segmentIndex":2,"cargo":[1,2]}]
["2025-02-27 08:32:45.777000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","004543450f0102b4002c01204e000064000a000fe260",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["CARB_RATIO","TARGET_BG","CORRECTION_FACTOR","BASAL_RATE"],"profileISF":10,"idpId":1,"profileStartTime":180,"segmentIndex":2,"profileTargetBG":100,"cargo":[1,2,-76,0,44,1,32,78,0,0,100,0,10,0,15],"profileBasalRate":300}]
["2025-02-27 08:32:49.480000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00464246020103d060",{"idpId":1,"segmentIndex":3,"cargo":[1,3]}]
["2025-02-27 08:32:50.368000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","004643460f010364054c04204e000064000a000f1ff1",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["CARB_RATIO","TARGET_BG","CORRECTION_FACTOR","BASAL_RATE"],"profileISF":10,"idpId":1,"profileStartTime":1380,"segmentIndex":3,"profileTargetBG":100,"cargo":[1,3,100,5,76,4,32,78,0,0,100,0,10,0,15],"profileBasalRate":1100}]
["2025-02-27 08:32:59.671000","WriteReq","CURRENT_STATUS","request.currentStatus.TimeSinceResetRequest","004b364b00af6a",{"cargo":[]}]
["2025-02-27 08:32:59.847000","ReadResp","CURRENT_STATUS","response.currentStatus.TimeSinceResetResponse","004b374b08482d47209ac3020076d3",{"currentTime":541535560,"pumpTimeSinceReset":181146,"currentTimeInstant":"2025-02-27T18:32:40Z","cargo":[72,45,71,32,-102,-61,2,0]}]

         */

        SetIDPSegmentRequest parsedReq = (SetIDPSegmentRequest) MessageTester.test(
                "0240aa4029010001023c006400e8030000640001",
                64,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "0140001f4c2d4720d8f5c1e02bec75b272687944",
                "0040f5dd6d354ec61dcf6c4b"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

    @Test
    public void testScenario11_Delete2am() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        // Delete segment at 2am (segment id 1)
        SetIDPSegmentRequest expected = new SetIDPSegmentRequest(
                1, 0, 1,
                SetIDPSegmentRequest.IDPSegmentOperation.DELETE_SEGMENT_ID,
                120, 200, 20_000, 100, 10,
                IDPSegmentResponse.IDPSegmentStatus.toBitmask( // 31
                        IDPSegmentResponse.IDPSegmentStatus.BASAL_RATE,
                        IDPSegmentResponse.IDPSegmentStatus.CARB_RATIO,
                        IDPSegmentResponse.IDPSegmentStatus.TARGET_BG,
                        IDPSegmentResponse.IDPSegmentStatus.CORRECTION_FACTOR,
                        IDPSegmentResponse.IDPSegmentStatus.START_TIME)
        );
        /*
["2025-02-27 08:33:14.755000","ReadResp","CONTROL","response.control.SetIDPSegmentResponse","004dab4d1a0002582d4720fc639a26aba7d24638d9f30d09ea4a1f5df30caee306",{"cargo":[0,2]}]
["2025-02-27 08:33:14.757000","WriteReq","CONTROL","request.control.SetIDPSegmentRequest","024daa4d29010001027800c800204e000064000a014d001f6a2d47200397f1bd2565e985aa7e8a79004d344542517418b8e83bf0",{"idpStatusId":31,"profileCarbRatio":20000,"idpStatus":["CARB_RATIO","TARGET_BG","CORRECTION_FACTOR","BASAL_RATE"],"profileISF":10,"idpId":1,"profileStartTime":120,"operationId":2,"segmentIndex":1,"profileTargetBG":100,"cargo":[1,0,1,2,120,0,-56,0,32,78,0,0,100,0,10,0,31],"operation":"DELETE","profileBasalRate":200}]
["2025-02-27 08:33:14.815000","WriteReq",null,"QualifyingEvents","00000000",{"events":[]}]
["2025-02-27 08:33:14.877000","ReadResp",null,"QualifyingEvents","00200000",{"events":[{"event":"PROFILE_CHANGE"}]}]
["2025-02-27 08:33:14.880000","WriteReq","CURRENT_STATUS","request.currentStatus.ProfileStatusRequest","004e3e4e00fb3c",{"cargo":[]}]
["2025-02-27 08:33:14.937000","ReadResp","CURRENT_STATUS","response.currentStatus.ProfileStatusResponse","004e3f4e08020100ffffffff011c38",{"idpSlot0Id":1,"activeSegmentIndex":1,"idpSlot1Id":0,"idpSlot2Id":-1,"idpSlot3Id":-1,"idpSlot4Id":-1,"cargo":[2,1,0,-1,-1,-1,-1,1],"idpSlot5Id":-1,"numberOfProfiles":2}]
["2025-02-27 08:33:14.941000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSettingsRequest","004f404f0101d0f8",{"idpId":1,"cargo":[1]}]
["2025-02-27 08:33:14.998000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSettingsResponse","004f414f17017a65726f000000000000000000000000032c01a86101175f",{"maxBolus":25000,"numberOfProfileSegments":3,"idpId":1,"insulinDuration":300,"name":"zero","cargo":[1,122,101,114,111,0,0,0,0,0,0,0,0,0,0,0,0,3,44,1,-88,97,1],"carbEntry":true}]
["2025-02-27 08:33:15.001000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","005042500201008d6c",{"idpId":1,"segmentIndex":0,"cargo":[1,0]}]
["2025-02-27 08:33:15.056000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","005043500f010000000000e8030000640001000fbdff",{"idpStatusId":15,"profileCarbRatio":1000,"idpStatus":["CARB_RATIO","TARGET_BG","CORRECTION_FACTOR","BASAL_RATE"],"profileISF":1,"idpId":1,"profileStartTime":0,"segmentIndex":0,"profileTargetBG":100,"cargo":[1,0,0,0,0,0,-24,3,0,0,100,0,1,0,15],"profileBasalRate":0}]
["2025-02-27 08:33:15.060000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00514251020101180a",{"idpId":1,"segmentIndex":1,"cargo":[1,1]}]
["2025-02-27 08:33:15.118000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","005143510f0101b4002c01204e000064000a000fc8b0",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["CARB_RATIO","TARGET_BG","CORRECTION_FACTOR","BASAL_RATE"],"profileISF":10,"idpId":1,"profileStartTime":180,"segmentIndex":1,"profileTargetBG":100,"cargo":[1,1,-76,0,44,1,32,78,0,0,100,0,10,0,15],"profileBasalRate":300}]
["2025-02-27 08:33:18.817000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00524252020102a7a1",{"idpId":1,"segmentIndex":2,"cargo":[1,2]}]
["2025-02-27 08:33:19.676000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","005243520f010264054c04204e000064000a000ff7d7",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["CARB_RATIO","TARGET_BG","CORRECTION_FACTOR","BASAL_RATE"],"profileISF":10,"idpId":1,"profileStartTime":1380,"segmentIndex":2,"profileTargetBG":100,"cargo":[1,2,100,5,76,4,32,78,0,0,100,0,10,0,15],"profileBasalRate":1100}]

         */

        SetIDPSegmentRequest parsedReq = (SetIDPSegmentRequest) MessageTester.test(
                "024daa4d29010001027800c800204e000064000a",
                77,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "014d001f6a2d47200397f1bd2565e985aa7e8a79",
                "004d344542517418b8e83bf0"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

    @Test
    public void testScenario12_Delete11pm() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        // Delete segment at 11pm (segment id 2)
        SetIDPSegmentRequest expected = new SetIDPSegmentRequest(
                1, 0, 2,
                SetIDPSegmentRequest.IDPSegmentOperation.DELETE_SEGMENT_ID,
                1380, 1100, 20_000, 100, 10,
                IDPSegmentResponse.IDPSegmentStatus.toBitmask( // 31
                        IDPSegmentResponse.IDPSegmentStatus.BASAL_RATE,
                        IDPSegmentResponse.IDPSegmentStatus.CARB_RATIO,
                        IDPSegmentResponse.IDPSegmentStatus.TARGET_BG,
                        IDPSegmentResponse.IDPSegmentStatus.CORRECTION_FACTOR,
                        IDPSegmentResponse.IDPSegmentStatus.START_TIME)
        );
        /*
["2025-02-27 08:33:48.506000","ReadResp","CONTROL","response.control.SetIDPSegmentResponse","0057ab571a00027a2d47203f7c15e44a5a3bf5aa5eb6dd03a6016e154ddf5b828c",{"cargo":[0,2]}]
["2025-02-27 08:33:48.508000","WriteReq","CONTROL","request.control.SetIDPSegmentRequest","0257aa57290100020264054c04204e000064000a0157001f8b2d4720272dcdaab193bdfda46fe90d005715458a5d6832e01b250b",{"idpStatusId":31,"profileCarbRatio":20000,"idpStatus":["CARB_RATIO","TARGET_BG","CORRECTION_FACTOR","BASAL_RATE"],"profileISF":10,"idpId":1,"profileStartTime":1380,"operationId":2,"segmentIndex":2,"profileTargetBG":100,"cargo":[1,0,2,2,100,5,76,4,32,78,0,0,100,0,10,0,31],"operation":"DELETE","profileBasalRate":1100}]
["2025-02-27 08:33:48.566000","WriteReq",null,"QualifyingEvents","00000000",{"events":[]}]
["2025-02-27 08:33:48.626000","ReadResp",null,"QualifyingEvents","00200000",{"events":[{"event":"PROFILE_CHANGE"}]}]
["2025-02-27 08:33:48.630000","WriteReq","CURRENT_STATUS","request.currentStatus.ProfileStatusRequest","00583e58002e95",{"cargo":[]}]
["2025-02-27 08:33:48.687000","ReadResp","CURRENT_STATUS","response.currentStatus.ProfileStatusResponse","00583f5808020100ffffffff0135df",{"idpSlot0Id":1,"activeSegmentIndex":1,"idpSlot1Id":0,"idpSlot2Id":-1,"idpSlot3Id":-1,"idpSlot4Id":-1,"cargo":[2,1,0,-1,-1,-1,-1,1],"idpSlot5Id":-1,"numberOfProfiles":2}]
["2025-02-27 08:33:48.690000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSettingsRequest","0059405901011309",{"idpId":1,"cargo":[1]}]
["2025-02-27 08:33:48.747000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSettingsResponse","0059415917017a65726f000000000000000000000000022c01a86101ea8c",{"maxBolus":25000,"numberOfProfileSegments":2,"idpId":1,"insulinDuration":300,"name":"zero","cargo":[1,122,101,114,111,0,0,0,0,0,0,0,0,0,0,0,0,2,44,1,-88,97,1],"carbEntry":true}]
["2025-02-27 08:33:48.750000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","005a425a0201002604",{"idpId":1,"segmentIndex":0,"cargo":[1,0]}]
["2025-02-27 08:33:48.807000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","005a435a0f010000000000e8030000640001000f6959",{"idpStatusId":15,"profileCarbRatio":1000,"idpStatus":["CARB_RATIO","TARGET_BG","CORRECTION_FACTOR","BASAL_RATE"],"profileISF":1,"idpId":1,"profileStartTime":0,"segmentIndex":0,"profileTargetBG":100,"cargo":[1,0,0,0,0,0,-24,3,0,0,100,0,1,0,15],"profileBasalRate":0}]
["2025-02-27 08:33:52.482000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","005b425b020101b362",{"idpId":1,"segmentIndex":1,"cargo":[1,1]}]
["2025-02-27 08:33:53.398000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","005b435b0f0101b4002c01204e000064000a000f1c16",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["CARB_RATIO","TARGET_BG","CORRECTION_FACTOR","BASAL_RATE"],"profileISF":10,"idpId":1,"profileStartTime":180,"segmentIndex":1,"profileTargetBG":100,"cargo":[1,1,-76,0,44,1,32,78,0,0,100,0,10,0,15],"profileBasalRate":300}]
["2025-02-27 08:34:00.867000","WriteReq","CURRENT_STATUS","request.currentStatus.TimeSinceResetRequest","0060366000b3b0",{"cargo":[]}]
["2025-02-27 08:34:01.077000","ReadResp","CURRENT_STATUS","response.currentStatus.TimeSinceResetResponse","0060376008852d4720d7c30200d01b",{"currentTime":541535621,"pumpTimeSinceReset":181207,"currentTimeInstant":"2025-02-27T18:33:41Z","cargo":[-123,45,71,32,-41,-61,2,0]}]

         */

        SetIDPSegmentRequest parsedReq = (SetIDPSegmentRequest) MessageTester.test(
                "0257aa57290100020264054c04204e000064000a",
                87,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "0157001f8b2d4720272dcdaab193bdfda46fe90d",
                "005715458a5d6832e01b250b"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

    @Test
    public void testScenario13_ModifyMidnightBasal() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        // Modify midnight segment (0) basal rate
        SetIDPSegmentRequest expected = new SetIDPSegmentRequest(
                1, 0, 0,
                SetIDPSegmentRequest.IDPSegmentOperation.MODIFY_SEGMENT_ID,
                0, 100, 1_000, 100, 1,
                IDPSegmentResponse.IDPSegmentStatus.toBitmask( // 1
                        IDPSegmentResponse.IDPSegmentStatus.BASAL_RATE)
        );
        /*
["2025-02-27 08:35:01.041000","WriteReq","CURRENT_STATUS","request.currentStatus.TimeSinceResetRequest","0065366500464f",{"cargo":[]}]
["2025-02-27 08:35:01.227000","ReadResp","CURRENT_STATUS","response.currentStatus.TimeSinceResetResponse","0065376508c12d472013c40200d8dc",{"currentTime":541535681,"pumpTimeSinceReset":181267,"currentTimeInstant":"2025-02-27T18:34:41Z","cargo":[-63,45,71,32,19,-60,2,0]}]
["2025-02-27 08:35:19.332000","WriteReq","CURRENT_STATUS","request.currentStatus.TimeSinceResetRequest","00673667002429",{"cargo":[]}]
["2025-02-27 08:35:19.436000","ReadResp","CURRENT_STATUS","response.currentStatus.TimeSinceResetResponse","0067376708d52d472027c4020093d6",{"currentTime":541535701,"pumpTimeSinceReset":181287,"currentTimeInstant":"2025-02-27T18:35:01Z","cargo":[-43,45,71,32,39,-60,2,0]}]
["2025-02-27 08:35:20.996000","WriteReq","CONTROL","request.currentStatus.UnknownMobiOpcodeNeg124Request","0168846818e72d472054f3d1adf040f4e3aa53b70068c36bbcc5e4695a3146dbe5",{"cargo":[]}]
["2025-02-27 08:35:21.326000","ReadResp","CONTROL","response.currentStatus.UnknownMobiOpcodeNeg124Response","006885681900d52d47203b02b9f4be2d7d42bbd9bc88031dafb2db8d54d354e2",{"cargo":[0],"status":0}]
["2025-02-27 08:35:26.636000","ReadResp","CONTROL","response.control.SetIDPSegmentResponse","006dab6d1a0002dc2d4720b7ddef94cb9a0ba4f27923c6c1da800a756769cb0786",{"cargo":[0,2]}]
["2025-02-27 08:35:26.639000","WriteReq","CONTROL","request.control.SetIDPSegmentRequest","026daa6d290100000000006400e8030000640001016d0001ed2d47209d4088c5ff6aa859578cb577006d33d6c3067cf35ff71917",{"idpStatusId":1,"profileCarbRatio":1000,"idpStatus":["BASAL_RATE"],"profileISF":1,"idpId":1,"profileStartTime":0,"operationId":0,"segmentIndex":0,"profileTargetBG":100,"cargo":[1,0,0,0,0,0,100,0,-24,3,0,0,100,0,1,0,1],"operation":"MODIFY","profileBasalRate":100}]
["2025-02-27 08:35:26.696000","WriteReq",null,"QualifyingEvents","00000000",{"events":[]}]
["2025-02-27 08:35:26.756000","ReadResp",null,"QualifyingEvents","00200000",{"events":[{"event":"PROFILE_CHANGE"}]}]
["2025-02-27 08:35:26.759000","WriteReq","CURRENT_STATUS","request.currentStatus.ProfileStatusRequest","006e3e6e001d3a",{"cargo":[]}]
["2025-02-27 08:35:26.817000","ReadResp","CURRENT_STATUS","response.currentStatus.ProfileStatusResponse","006e3f6e08020100ffffffff011101",{"idpSlot0Id":1,"activeSegmentIndex":1,"idpSlot1Id":0,"idpSlot2Id":-1,"idpSlot3Id":-1,"idpSlot4Id":-1,"cargo":[2,1,0,-1,-1,-1,-1,1],"idpSlot5Id":-1,"numberOfProfiles":2}]
["2025-02-27 08:35:26.821000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSettingsRequest","006f406f0101167e",{"idpId":1,"cargo":[1]}]
["2025-02-27 08:35:26.877000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSettingsResponse","006f416f17017a65726f000000000000000000000000022c01a861013f2d",{"maxBolus":25000,"numberOfProfileSegments":2,"idpId":1,"insulinDuration":300,"name":"zero","cargo":[1,122,101,114,111,0,0,0,0,0,0,0,0,0,0,0,0,2,44,1,-88,97,1],"carbEntry":true}]
["2025-02-27 08:35:26.879000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00704270020100c35b",{"idpId":1,"segmentIndex":0,"cargo":[1,0]}]
["2025-02-27 08:35:26.936000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","007043700f010000006400e8030000640001000f577c",{"idpStatusId":15,"profileCarbRatio":1000,"idpStatus":["BASAL_RATE","TARGET_BG","CARB_RATIO","CORRECTION_FACTOR"],"profileISF":1,"idpId":1,"profileStartTime":0,"segmentIndex":0,"profileTargetBG":100,"cargo":[1,0,0,0,100,0,-24,3,0,0,100,0,1,0,15],"profileBasalRate":100}]
["2025-02-27 08:35:30.642000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00714271020101563d",{"idpId":1,"segmentIndex":1,"cargo":[1,1]}]
["2025-02-27 08:35:31.526000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","007143710f0101b4002c01204e000064000a000fca8c",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["BASAL_RATE","TARGET_BG","CARB_RATIO","CORRECTION_FACTOR"],"profileISF":10,"idpId":1,"profileStartTime":180,"segmentIndex":1,"profileTargetBG":100,"cargo":[1,1,-76,0,44,1,32,78,0,0,100,0,10,0,15],"profileBasalRate":300}]

         */

        SetIDPSegmentRequest parsedReq = (SetIDPSegmentRequest) MessageTester.test(
                "026daa6d290100000000006400e8030000640001",
                109,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "016d0001ed2d47209d4088c5ff6aa859578cb577",
                "006d33d6c3067cf35ff71917"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

    @Test
    public void testScenario14_Add2am() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        // New segment at 2am
        SetIDPSegmentRequest expected = new SetIDPSegmentRequest(
                1, 0, 0,
                SetIDPSegmentRequest.IDPSegmentOperation.CREATE_SEGMENT,
                120, 200, 1_000, 100, 1,
                IDPSegmentResponse.IDPSegmentStatus.toBitmask( // 31
                        IDPSegmentResponse.IDPSegmentStatus.BASAL_RATE,
                        IDPSegmentResponse.IDPSegmentStatus.CARB_RATIO,
                        IDPSegmentResponse.IDPSegmentStatus.TARGET_BG,
                        IDPSegmentResponse.IDPSegmentStatus.CORRECTION_FACTOR,
                        IDPSegmentResponse.IDPSegmentStatus.START_TIME)
        );
        /*
["2025-02-27 08:36:01.316000","ReadResp","CONTROL","response.control.SetIDPSegmentResponse","0076ab761a0002ff2d47209b53ffa17bfb86d1b923b9a55954ef453bd02d848a5f",{"cargo":[0,2]}]
["2025-02-27 08:36:01.317000","WriteReq","CONTROL","request.control.SetIDPSegmentRequest","0276aa7629010000017800c800e80300006400010176001f102e47206ae87cb08dd47a4c41f4de4c0076429e0b5407e062d3217a",{"idpStatusId":31,"profileCarbRatio":1000,"idpStatus":["CARB_RATIO","TARGET_BG","CORRECTION_FACTOR","BASAL_RATE"],"profileISF":1,"idpId":1,"profileStartTime":120,"operationId":1,"segmentIndex":0,"profileTargetBG":100,"cargo":[1,0,0,1,120,0,-56,0,-24,3,0,0,100,0,1,0,31],"operation":"CREATE_AFTER","profileBasalRate":200}]
["2025-02-27 08:36:01.375000","WriteReq",null,"QualifyingEvents","00000000",{"events":[]}]
["2025-02-27 08:36:01.466000","ReadResp",null,"QualifyingEvents","00200000",{"events":[{"event":"PROFILE_CHANGE"}]}]
["2025-02-27 08:36:01.469000","WriteReq","CURRENT_STATUS","request.currentStatus.ProfileStatusRequest","00773e7700f683",{"cargo":[]}]
["2025-02-27 08:36:01.526000","ReadResp","CURRENT_STATUS","response.currentStatus.ProfileStatusResponse","00773f7708020100ffffffff02ea80",{"idpSlot0Id":1,"activeSegmentIndex":2,"idpSlot1Id":0,"idpSlot2Id":-1,"idpSlot3Id":-1,"idpSlot4Id":-1,"cargo":[2,1,0,-1,-1,-1,-1,2],"idpSlot5Id":-1,"numberOfProfiles":2}]
["2025-02-27 08:36:01.530000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSettingsRequest","007840780101e5b8",{"idpId":1,"cargo":[1]}]
["2025-02-27 08:36:01.587000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSettingsResponse","0078417817017a65726f000000000000000000000000032c01a8610176bb",{"maxBolus":25000,"numberOfProfileSegments":3,"idpId":1,"insulinDuration":300,"name":"zero","cargo":[1,122,101,114,111,0,0,0,0,0,0,0,0,0,0,0,0,3,44,1,-88,97,1],"carbEntry":true}]
["2025-02-27 08:36:01.591000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00794279020100b4a8",{"idpId":1,"segmentIndex":0,"cargo":[1,0]}]
["2025-02-27 08:36:01.646000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","007943790f010000006400e8030000640001000fa5eb",{"idpStatusId":15,"profileCarbRatio":1000,"idpStatus":["CARB_RATIO","TARGET_BG","CORRECTION_FACTOR","BASAL_RATE"],"profileISF":1,"idpId":1,"profileStartTime":0,"segmentIndex":0,"profileTargetBG":100,"cargo":[1,0,0,0,100,0,-24,3,0,0,100,0,1,0,15],"profileBasalRate":100}]
["2025-02-27 08:36:01.649000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","007a427a0201014923",{"idpId":1,"segmentIndex":1,"cargo":[1,1]}]
["2025-02-27 08:36:01.706000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","007a437a0f01017800c800e8030000640001000fad1b",{"idpStatusId":15,"profileCarbRatio":1000,"idpStatus":["CARB_RATIO","TARGET_BG","CORRECTION_FACTOR","BASAL_RATE"],"profileISF":1,"idpId":1,"profileStartTime":120,"segmentIndex":1,"profileTargetBG":100,"cargo":[1,1,120,0,-56,0,-24,3,0,0,100,0,1,0,15],"profileBasalRate":200}]
["2025-02-27 08:36:05.414000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","007b427b0201029e65",{"idpId":1,"segmentIndex":2,"cargo":[1,2]}]
["2025-02-27 08:36:06.297000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","007b437b0f0102b4002c01204e000064000a000fbda7",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["CARB_RATIO","TARGET_BG","CORRECTION_FACTOR","BASAL_RATE"],"profileISF":10,"idpId":1,"profileStartTime":180,"segmentIndex":2,"profileTargetBG":100,"cargo":[1,2,-76,0,44,1,32,78,0,0,100,0,10,0,15],"profileBasalRate":300}]

         */

        SetIDPSegmentRequest parsedReq = (SetIDPSegmentRequest) MessageTester.test(
                "0276aa7629010000017800c800e8030000640001",
                118,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "0176001f102e47206ae87cb08dd47a4c41f4de4c",
                "0076429e0b5407e062d3217a"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

    @Test
    public void testScenario15_Delete3am() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        // Delete segment at 3am (segment index 2)
        SetIDPSegmentRequest expected = new SetIDPSegmentRequest(
                1, 0, 2,
                SetIDPSegmentRequest.IDPSegmentOperation.DELETE_SEGMENT_ID,
                180, 300, 20_000, 100, 10,
                IDPSegmentResponse.IDPSegmentStatus.toBitmask( // 31
                        IDPSegmentResponse.IDPSegmentStatus.BASAL_RATE,
                        IDPSegmentResponse.IDPSegmentStatus.CARB_RATIO,
                        IDPSegmentResponse.IDPSegmentStatus.TARGET_BG,
                        IDPSegmentResponse.IDPSegmentStatus.CORRECTION_FACTOR,
                        IDPSegmentResponse.IDPSegmentStatus.START_TIME)
        );
        /*
["2025-02-27 08:36:19.663000","WriteReq","CURRENT_STATUS","request.currentStatus.TimeSinceResetRequest","008036800001a0",{"cargo":[]}]
["2025-02-27 08:36:20.665000","ReadResp","CURRENT_STATUS","response.currentStatus.TimeSinceResetResponse","0080378008112e472063c40200991d",{"currentTime":541535761,"pumpTimeSinceReset":181347,"currentTimeInstant":"2025-02-27T18:36:01Z","cargo":[17,46,71,32,99,-60,2,0]}]
["2025-02-27 08:36:20.934000","ReadResp","CONTROL","response.control.SetIDPSegmentResponse","0081ab811a0002122e472095b9d024d396aa7590c1a0d63b7bebcf8c0b0c80787e",{"cargo":[0,2]}]
["2025-02-27 08:36:20.937000","WriteReq","CONTROL","request.control.SetIDPSegmentRequest","0281aa812901000202b4002c01204e000064000a0181001f232e47207f6ecb83dfea20ae50b9b3dc0081185ad87a949808499d99",{"idpStatusId":31,"profileCarbRatio":20000,"idpStatus":["CARB_RATIO","TARGET_BG","BASAL_RATE","CORRECTION_FACTOR"],"profileISF":10,"idpId":1,"profileStartTime":180,"operationId":2,"segmentIndex":2,"profileTargetBG":100,"cargo":[1,0,2,2,-76,0,44,1,32,78,0,0,100,0,10,0,31],"operation":"DELETE","profileBasalRate":300}]
["2025-02-27 08:36:20.994000","WriteReq",null,"QualifyingEvents","00000000",{"events":[]}]
["2025-02-27 08:36:21.056000","ReadResp",null,"QualifyingEvents","00220000",{"events":[{"event":"BASAL_CHANGE"},{"event":"PROFILE_CHANGE"}]}]
["2025-02-27 08:36:21.061000","WriteReq","CURRENT_STATUS","request.currentStatus.ControlIQIOBRequest","00826c82006c5f",{"cargo":[]}]
["2025-02-27 08:36:21.118000","ReadResp","CURRENT_STATUS","response.currentStatus.ControlIQIOBResponse","00826d82110000000000000000000000000000000000adff",{"mudaliarTotalIOB":0,"iobType":0,"timeRemainingSeconds":0,"swan6hrIOB":0,"cargo":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],"mudaliarIOB":0}]
["2025-02-27 08:36:21.122000","WriteReq","008328830030ad","Unexpected cargo size: 0, expecting 50 for opCode=40"]
["2025-02-27 08:36:21.175000","ReadResp","0083298309c800000000000000016fff","Unexpected cargo size: 9, expecting 50 for opCode=41"]
["2025-02-27 08:36:21.179000","WriteReq","CURRENT_STATUS","request.currentStatus.UnknownMobiOpcode30Request","00841e8400a243",{"cargo":[]}]
["2025-02-27 08:36:21.236000","ReadResp","CURRENT_STATUS","response.currentStatus.UnknownMobiOpcode30Response","00841f84100000000000000000ffffffff000000008a1f",{"unknown4":0,"unknown3":4294967295,"unknown2":0,"unknown1":0,"cargo":[0,0,0,0,0,0,0,0,-1,-1,-1,-1,0,0,0,0]}]
["2025-02-27 08:36:21.240000","WriteReq","CURRENT_STATUS","request.currentStatus.ProfileStatusRequest","00853e850055f6",{"cargo":[]}]
["2025-02-27 08:36:21.296000","ReadResp","CURRENT_STATUS","response.currentStatus.ProfileStatusResponse","00853f8508020100ffffffff01b655",{"idpSlot0Id":1,"activeSegmentIndex":1,"idpSlot1Id":0,"idpSlot2Id":-1,"idpSlot3Id":-1,"idpSlot4Id":-1,"cargo":[2,1,0,-1,-1,-1,-1,1],"idpSlot5Id":-1,"numberOfProfiles":2}]
["2025-02-27 08:36:21.389000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSettingsRequest","0087408701018677",{"idpId":1,"cargo":[1]}]
["2025-02-27 08:36:21.446000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSettingsResponse","0087418717017a65726f000000000000000000000000022c01a8610165a6",{"maxBolus":25000,"numberOfProfileSegments":2,"idpId":1,"insulinDuration":300,"name":"zero","cargo":[1,122,101,114,111,0,0,0,0,0,0,0,0,0,0,0,0,2,44,1,-88,97,1],"carbEntry":true}]
["2025-02-27 08:36:21.568000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00894289020100f937",{"idpId":1,"segmentIndex":0,"cargo":[1,0]}]
["2025-02-27 08:36:21.626000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","008943890f010000006400e8030000640001000faa41",{"idpStatusId":15,"profileCarbRatio":1000,"idpStatus":["CARB_RATIO","TARGET_BG","BASAL_RATE","CORRECTION_FACTOR"],"profileISF":1,"idpId":1,"profileStartTime":0,"segmentIndex":0,"profileTargetBG":100,"cargo":[1,0,0,0,100,0,-24,3,0,0,100,0,1,0,15],"profileBasalRate":100}]
["2025-02-27 08:36:21.694000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","008a428a02010104bc",{"idpId":1,"segmentIndex":1,"cargo":[1,1]}]
["2025-02-27 08:36:22.585000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","008a438a0f01017800c800e8030000640001000fa2b1",{"idpStatusId":15,"profileCarbRatio":1000,"idpStatus":["CARB_RATIO","TARGET_BG","BASAL_RATE","CORRECTION_FACTOR"],"profileISF":1,"idpId":1,"profileStartTime":120,"segmentIndex":1,"profileTargetBG":100,"cargo":[1,1,120,0,-56,0,-24,3,0,0,100,0,1,0,15],"profileBasalRate":200}]

         */

        SetIDPSegmentRequest parsedReq = (SetIDPSegmentRequest) MessageTester.test(
                "0281aa812901000202b4002c01204e000064000a",
                -127,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "0181001f232e47207f6ecb83dfea20ae50b9b3dc",
                "0081185ad87a949808499d99"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }


    @Test
    public void testScenario16_Delete2am() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        // Delete segment at 2am (segment index 1)
        SetIDPSegmentRequest expected = new SetIDPSegmentRequest(
                1, 0, 1,
                SetIDPSegmentRequest.IDPSegmentOperation.DELETE_SEGMENT_ID,
                120, 200, 1_000, 100, 1,
                IDPSegmentResponse.IDPSegmentStatus.toBitmask( // 31
                        IDPSegmentResponse.IDPSegmentStatus.BASAL_RATE,
                        IDPSegmentResponse.IDPSegmentStatus.CARB_RATIO,
                        IDPSegmentResponse.IDPSegmentStatus.TARGET_BG,
                        IDPSegmentResponse.IDPSegmentStatus.CORRECTION_FACTOR,
                        IDPSegmentResponse.IDPSegmentStatus.START_TIME)
        );
        /*
["2025-02-27 08:36:35.004000","ReadResp","CONTROL","response.control.SetIDPSegmentResponse","008eab8e1a0002212e4720cdac73cfb98c2d8e04d7f482bbd9fa50469d57913c13",{"cargo":[0,2]}]
["2025-02-27 08:36:35.007000","WriteReq","CONTROL","request.control.SetIDPSegmentRequest","028eaa8e29010001027800c800e8030000640001018e001f322e4720d561e184028402d443d2832a008ef5f9e512d5e3f1aaa222",{"idpStatusId":31,"profileCarbRatio":1000,"idpStatus":["CARB_RATIO","TARGET_BG","CORRECTION_FACTOR","BASAL_RATE"],"profileISF":1,"idpId":1,"profileStartTime":120,"operationId":2,"segmentIndex":1,"profileTargetBG":100,"cargo":[1,0,1,2,120,0,-56,0,-24,3,0,0,100,0,1,0,31],"operation":"DELETE","profileBasalRate":200}]
["2025-02-27 08:36:35.065000","WriteReq",null,"QualifyingEvents","00000000",{"events":[]}]
["2025-02-27 08:36:35.125000","ReadResp",null,"QualifyingEvents","00220000",{"events":[{"event":"BASAL_CHANGE"},{"event":"PROFILE_CHANGE"}]}]
["2025-02-27 08:36:35.130000","WriteReq","CURRENT_STATUS","request.currentStatus.ControlIQIOBRequest","008f6c8f003029",{"cargo":[]}]
["2025-02-27 08:36:35.186000","ReadResp","CURRENT_STATUS","response.currentStatus.ControlIQIOBResponse","008f6d8f11000000000000000000000000000000000093f9",{"mudaliarTotalIOB":0,"iobType":0,"timeRemainingSeconds":0,"swan6hrIOB":0,"cargo":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],"mudaliarIOB":0}]
["2025-02-27 08:36:35.189000","WriteReq","009028900010fb","Unexpected cargo size: 0, expecting 50 for opCode=40"]
["2025-02-27 08:36:35.246000","ReadResp","0090299009640000000000000001f67f","Unexpected cargo size: 9, expecting 50 for opCode=41"]
["2025-02-27 08:36:35.250000","WriteReq","CURRENT_STATUS","request.currentStatus.UnknownMobiOpcode30Request","00911e910024bf",{"cargo":[]}]
["2025-02-27 08:36:35.306000","ReadResp","CURRENT_STATUS","response.currentStatus.UnknownMobiOpcode30Response","00911f91100000000000000000ffffffff00000000e3ed",{"unknown4":0,"unknown3":4294967295,"unknown2":0,"unknown1":0,"cargo":[0,0,0,0,0,0,0,0,-1,-1,-1,-1,0,0,0,0]}]
["2025-02-27 08:36:35.310000","WriteReq","CURRENT_STATUS","request.currentStatus.ProfileStatusRequest","00923e9200b16c",{"cargo":[]}]
["2025-02-27 08:36:35.366000","ReadResp","CURRENT_STATUS","response.currentStatus.ProfileStatusResponse","00923f9208020100ffffffff00fbcd",{"idpSlot0Id":1,"activeSegmentIndex":0,"idpSlot1Id":0,"idpSlot2Id":-1,"idpSlot3Id":-1,"idpSlot4Id":-1,"cargo":[2,1,0,-1,-1,-1,-1,0],"idpSlot5Id":-1,"numberOfProfiles":2}]
["2025-02-27 08:36:35.369000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSettingsRequest","00934093010125e8",{"idpId":1,"cargo":[1]}]
["2025-02-27 08:36:35.426000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSettingsResponse","0093419317017a65726f000000000000000000000000012c01a86101b075",{"maxBolus":25000,"numberOfProfileSegments":1,"idpId":1,"insulinDuration":300,"name":"zero","cargo":[1,122,101,114,111,0,0,0,0,0,0,0,0,0,0,0,0,1,44,1,-88,97,1],"carbEntry":true}]
["2025-02-27 08:36:39.133000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00944294020100d815",{"idpId":1,"segmentIndex":0,"cargo":[1,0]}]
["2025-02-27 08:36:39.986000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","009443940f010000006400e8030000640001000fd18b",{"idpStatusId":15,"profileCarbRatio":1000,"idpStatus":["CARB_RATIO","TARGET_BG","CORRECTION_FACTOR","BASAL_RATE"],"profileISF":1,"idpId":1,"profileStartTime":0,"segmentIndex":0,"profileTargetBG":100,"cargo":[1,0,0,0,100,0,-24,3,0,0,100,0,1,0,15],"profileBasalRate":100}]

         */

        SetIDPSegmentRequest parsedReq = (SetIDPSegmentRequest) MessageTester.test(
                "028eaa8e29010001027800c800e8030000640001",
                -114,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "018e001f322e4720d561e184028402d443d2832a",
                "008ef5f9e512d5e3f1aaa222"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

    @Test
    public void testScenario20_ProfCIQ_ChangeMidnight0Basal() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        // CIQ profile idpid 0, set midnight basal
        SetIDPSegmentRequest expected = new SetIDPSegmentRequest(
                0, 1, 0,
                SetIDPSegmentRequest.IDPSegmentOperation.MODIFY_SEGMENT_ID,
                0, 0, 6_000, 110, 30,
                IDPSegmentResponse.IDPSegmentStatus.toBitmask( // 1
                        IDPSegmentResponse.IDPSegmentStatus.BASAL_RATE)
        );
        /*
["2025-02-27 10:19:55.591000","WriteReq","CURRENT_STATUS","request.currentStatus.TimeSinceResetRequest","00d036d000beae",{"cargo":[]}]
["2025-02-27 10:19:55.797000","ReadResp","CURRENT_STATUS","response.currentStatus.TimeSinceResetResponse","00d037d00858464720aadc02006ade",{"currentTime":541541976,"pumpTimeSinceReset":187562,"currentTimeInstant":"2025-02-27T20:19:36Z","cargo":[88,70,71,32,-86,-36,2,0]}]
["2025-02-27 10:20:25.345000","ReadResp","CONTROL","response.control.SetIDPSegmentResponse","00d5abd51a000277464720152c0aaf880f0d8b9a634e0ef975512d1aa62f0a5571",{"cargo":[0,2]}]
["2025-02-27 10:20:25.347000","WriteReq","CONTROL","request.control.SetIDPSegmentRequest","02d5aad5290001000000000000701700006e001e01d5000188464720bb94e711b450f18986ad5a0000d51a73d679f451f005bbb5",{"idpStatusId":1,"idpId":0,"unknownId":1,"profileBasalRate":0,"profileCarbRatio":6000,"idpStatus":["BASAL_RATE"],"profileISF":30,"profileStartTime":0,"operationId":0,"segmentIndex":0,"profileTargetBG":110,"cargo":[0,1,0,0,0,0,0,0,112,23,0,0,110,0,30,0,1],"operation":"MODIFY_SEGMENT_ID"}]
["2025-02-27 10:20:25.406000","WriteReq",null,"QualifyingEvents","00000000",{"events":[]}]
["2025-02-27 10:20:25.466000","ReadResp",null,"QualifyingEvents","00200000",{"events":[{"event":"PROFILE_CHANGE"}]}]
["2025-02-27 10:20:25.471000","WriteReq","CURRENT_STATUS","request.currentStatus.ProfileStatusRequest","00d63ed600b9ad",{"cargo":[]}]
["2025-02-27 10:20:25.527000","ReadResp","CURRENT_STATUS","response.currentStatus.ProfileStatusResponse","00d63fd608020100ffffffff00d412",{"idpSlot0Id":1,"activeSegmentIndex":0,"idpSlot1Id":0,"idpSlot2Id":-1,"idpSlot3Id":-1,"idpSlot4Id":-1,"cargo":[2,1,0,-1,-1,-1,-1,0],"idpSlot5Id":-1,"numberOfProfiles":2}]
["2025-02-27 10:20:25.531000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSettingsRequest","00d740d701006939",{"idpId":0,"cargo":[0]}]
["2025-02-27 10:20:25.617000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSettingsResponse","00d741d7170063697100000000000000000000000000042c01a861019149",{"maxBolus":25000,"numberOfProfileSegments":4,"idpId":0,"insulinDuration":300,"name":"ciq","cargo":[0,99,105,113,0,0,0,0,0,0,0,0,0,0,0,0,0,4,44,1,-88,97,1],"carbEntry":true}]
["2025-02-27 10:20:25.622000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00d842d80200004707",{"idpId":0,"segmentIndex":0,"cargo":[0,0]}]
["2025-02-27 10:20:25.676000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","00d843d80f000000000000701700006e001e000fcff8",{"idpStatusId":15,"profileCarbRatio":6000,"idpStatus":["CORRECTION_FACTOR","BASAL_RATE","TARGET_BG","CARB_RATIO"],"profileISF":30,"idpId":0,"profileStartTime":0,"segmentIndex":0,"profileTargetBG":110,"cargo":[0,0,0,0,0,0,112,23,0,0,110,0,30,0,15],"profileBasalRate":0}]
["2025-02-27 10:20:25.679000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00d942d9020001d261",{"idpId":0,"segmentIndex":1,"cargo":[0,1]}]
["2025-02-27 10:20:25.737000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","00d943d90f0001e001b004701700006e001e000ff989",{"idpStatusId":15,"profileCarbRatio":6000,"idpStatus":["CORRECTION_FACTOR","BASAL_RATE","TARGET_BG","CARB_RATIO"],"profileISF":30,"idpId":0,"profileStartTime":480,"segmentIndex":1,"profileTargetBG":110,"cargo":[0,1,-32,1,-80,4,112,23,0,0,110,0,30,0,15],"profileBasalRate":1200}]
["2025-02-27 10:20:25.740000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00da42da0200026dca",{"idpId":0,"segmentIndex":2,"cargo":[0,2]}]
["2025-02-27 10:20:25.797000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","00da43da0f00029402e803701700006e001e000fdefb",{"idpStatusId":15,"profileCarbRatio":6000,"idpStatus":["CORRECTION_FACTOR","BASAL_RATE","TARGET_BG","CARB_RATIO"],"profileISF":30,"idpId":0,"profileStartTime":660,"segmentIndex":2,"profileTargetBG":110,"cargo":[0,2,-108,2,-24,3,112,23,0,0,110,0,30,0,15],"profileBasalRate":1000}]
["2025-02-27 10:20:29.512000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00db42db020003f8ac",{"idpId":0,"segmentIndex":3,"cargo":[0,3]}]
["2025-02-27 10:20:30.357000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","00db43db0f0003d002e803701700006e001e000f20cf",{"idpStatusId":15,"profileCarbRatio":6000,"idpStatus":["CORRECTION_FACTOR","BASAL_RATE","TARGET_BG","CARB_RATIO"],"profileISF":30,"idpId":0,"profileStartTime":720,"segmentIndex":3,"profileTargetBG":110,"cargo":[0,3,-48,2,-24,3,112,23,0,0,110,0,30,0,15],"profileBasalRate":1000}]
["2025-02-27 10:20:55.841000","WriteReq","CURRENT_STATUS","request.currentStatus.TimeSinceResetRequest","00e036e0002bab",{"cargo":[]}]
["2025-02-27 10:20:56.007000","ReadResp","CURRENT_STATUS","response.currentStatus.TimeSinceResetResponse","00e037e00894464720e6dc0200b948",{"currentTime":541542036,"pumpTimeSinceReset":187622,"currentTimeInstant":"2025-02-27T20:20:36Z","cargo":[-108,70,71,32,-26,-36,2,0]}]

         */

        SetIDPSegmentRequest parsedReq = (SetIDPSegmentRequest) MessageTester.test(
                "02d5aad5290001000000000000701700006e001e",
                -43,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "01d5000188464720bb94e711b450f18986ad5a00",
                "00d51a73d679f451f005bbb5"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }


    @Test
    public void testScenario21_ProfCIQ_New1am() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        // CIQ profile idpid 0, new 1am profile
        SetIDPSegmentRequest expected = new SetIDPSegmentRequest(
                0, 1, 0,
                SetIDPSegmentRequest.IDPSegmentOperation.CREATE_SEGMENT,
                60, 1_000, 3_000, 110, 2,
                IDPSegmentResponse.IDPSegmentStatus.toBitmask( // 1
                        IDPSegmentResponse.IDPSegmentStatus.BASAL_RATE,
                        IDPSegmentResponse.IDPSegmentStatus.CARB_RATIO,
                        IDPSegmentResponse.IDPSegmentStatus.TARGET_BG,
                        IDPSegmentResponse.IDPSegmentStatus.CORRECTION_FACTOR,
                        IDPSegmentResponse.IDPSegmentStatus.START_TIME)
        );
        /*
["2025-02-27 10:21:56.529000","WriteReq","CURRENT_STATUS","request.currentStatus.TimeSinceResetRequest","00e236e20049cd",{"cargo":[]}]
["2025-02-27 10:21:56.728000","ReadResp","CURRENT_STATUS","response.currentStatus.TimeSinceResetResponse","00e237e208d146472022dd02003866",{"currentTime":541542097,"pumpTimeSinceReset":187682,"currentTimeInstant":"2025-02-27T20:21:37Z","cargo":[-47,70,71,32,34,-35,2,0]}]
["2025-02-27 10:22:00.777000","ReadResp","CONTROL","response.control.SetIDPSegmentResponse","00e4abe41a0002d64647206da5d142271c7253b8e3bd8c340ea72095cade12c50f",{"cargo":[0,2]}]
["2025-02-27 10:22:00.779000","WriteReq","CONTROL","request.control.SetIDPSegmentRequest","02e4aae429000100013c00e803b80b00006e000201e4001fe746472011dddb73e713298984cb0ef600e4cf0e98f48a383e0ff345",{"idpStatusId":31,"idpId":0,"unknownId":1,"profileBasalRate":1000,"profileCarbRatio":3000,"idpStatus":["BASAL_RATE","UNKNOWN_16","TARGET_BG","CARB_RATIO","CORRECTION_FACTOR"],"profileISF":2,"profileStartTime":60,"operationId":1,"segmentIndex":0,"profileTargetBG":110,"cargo":[0,1,0,1,60,0,-24,3,-72,11,0,0,110,0,2,0,31],"operation":"CREATE_SEGMENT"}]
["2025-02-27 10:22:00.839000","WriteReq",null,"QualifyingEvents","00000000",{"events":[]}]
["2025-02-27 10:22:00.898000","ReadResp",null,"QualifyingEvents","00200000",{"events":[{"event":"PROFILE_CHANGE"}]}]
["2025-02-27 10:22:00.902000","WriteReq","CURRENT_STATUS","request.currentStatus.ProfileStatusRequest","00e53ee5007ffd",{"cargo":[]}]
["2025-02-27 10:22:00.958000","ReadResp","CURRENT_STATUS","response.currentStatus.ProfileStatusResponse","00e53fe508020100ffffffff00800e",{"idpSlot0Id":1,"activeSegmentIndex":0,"idpSlot1Id":0,"idpSlot2Id":-1,"idpSlot3Id":-1,"idpSlot4Id":-1,"cargo":[2,1,0,-1,-1,-1,-1,0],"idpSlot5Id":-1,"numberOfProfiles":2}]
["2025-02-27 10:22:00.964000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSettingsRequest","00e640e60100fccb",{"idpId":0,"cargo":[0]}]
["2025-02-27 10:22:01.019000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSettingsResponse","00e641e6170063697100000000000000000000000000052c01a86101c965",{"maxBolus":25000,"numberOfProfileSegments":5,"idpId":0,"insulinDuration":300,"name":"ciq","cargo":[0,99,105,113,0,0,0,0,0,0,0,0,0,0,0,0,0,5,44,1,-88,97,1],"carbEntry":true}]
["2025-02-27 10:22:01.022000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00e742e702000040ff",{"idpId":0,"segmentIndex":0,"cargo":[0,0]}]
["2025-02-27 10:22:01.078000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","00e743e70f000000000000701700006e001e000f722f",{"idpStatusId":15,"profileCarbRatio":6000,"idpStatus":["BASAL_RATE","TARGET_BG","CARB_RATIO","CORRECTION_FACTOR"],"profileISF":30,"idpId":0,"profileStartTime":0,"segmentIndex":0,"profileTargetBG":110,"cargo":[0,0,0,0,0,0,112,23,0,0,110,0,30,0,15],"profileBasalRate":0}]
["2025-02-27 10:22:01.081000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00e842e80200018f3b",{"idpId":0,"segmentIndex":1,"cargo":[0,1]}]
["2025-02-27 10:22:01.139000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","00e843e80f00013c00e803b80b00006e0002000f5733",{"idpStatusId":15,"profileCarbRatio":3000,"idpStatus":["BASAL_RATE","TARGET_BG","CARB_RATIO","CORRECTION_FACTOR"],"profileISF":2,"idpId":0,"profileStartTime":60,"segmentIndex":1,"profileTargetBG":110,"cargo":[0,1,60,0,-24,3,-72,11,0,0,110,0,2,0,15],"profileBasalRate":1000}]
["2025-02-27 10:22:01.142000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00e942e9020002587d",{"idpId":0,"segmentIndex":2,"cargo":[0,2]}]
["2025-02-27 10:22:01.198000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","00e943e90f0002e001b004701700006e001e000f5926",{"idpStatusId":15,"profileCarbRatio":6000,"idpStatus":["BASAL_RATE","TARGET_BG","CARB_RATIO","CORRECTION_FACTOR"],"profileISF":30,"idpId":0,"profileStartTime":480,"segmentIndex":2,"profileTargetBG":110,"cargo":[0,2,-32,1,-80,4,112,23,0,0,110,0,30,0,15],"profileBasalRate":1200}]
["2025-02-27 10:22:01.201000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00ea42ea020003a5f6",{"idpId":0,"segmentIndex":3,"cargo":[0,3]}]
["2025-02-27 10:22:01.258000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","00ea43ea0f00039402e803701700006e001e000fbca2",{"idpStatusId":15,"profileCarbRatio":6000,"idpStatus":["BASAL_RATE","TARGET_BG","CARB_RATIO","CORRECTION_FACTOR"],"profileISF":30,"idpId":0,"profileStartTime":660,"segmentIndex":3,"profileTargetBG":110,"cargo":[0,3,-108,2,-24,3,112,23,0,0,110,0,30,0,15],"profileBasalRate":1000}]
["2025-02-27 10:22:04.963000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00eb42eb020004f6f0",{"idpId":0,"segmentIndex":4,"cargo":[0,4]}]
["2025-02-27 10:22:05.818000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","00eb43eb0f0004d002e803701700006e001e000f259d",{"idpStatusId":15,"profileCarbRatio":6000,"idpStatus":["BASAL_RATE","TARGET_BG","CARB_RATIO","CORRECTION_FACTOR"],"profileISF":30,"idpId":0,"profileStartTime":720,"segmentIndex":4,"profileTargetBG":110,"cargo":[0,4,-48,2,-24,3,112,23,0,0,110,0,30,0,15],"profileBasalRate":1000}]

         */

        SetIDPSegmentRequest parsedReq = (SetIDPSegmentRequest) MessageTester.test(
                "02e4aae429000100013c00e803b80b00006e0002",
                -28,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "01e4001fe746472011dddb73e713298984cb0ef6",
                "00e4cf0e98f48a383e0ff345"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }


    @Test
    public void testScenario22_ProfCIQ_Delete1am() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        // CIQ profile idpid 0, new 1am profile
        SetIDPSegmentRequest expected = new SetIDPSegmentRequest(
                0, 1, 1,
                SetIDPSegmentRequest.IDPSegmentOperation.DELETE_SEGMENT_ID,
                60, 1_000, 3_000, 110, 2,
                IDPSegmentResponse.IDPSegmentStatus.toBitmask( // 1
                        IDPSegmentResponse.IDPSegmentStatus.BASAL_RATE,
                        IDPSegmentResponse.IDPSegmentStatus.CARB_RATIO,
                        IDPSegmentResponse.IDPSegmentStatus.TARGET_BG,
                        IDPSegmentResponse.IDPSegmentStatus.CORRECTION_FACTOR,
                        IDPSegmentResponse.IDPSegmentStatus.START_TIME)
        );
        /*
["2025-02-27 10:22:34.888000","ReadResp","CONTROL","response.control.SetIDPSegmentResponse","00f0abf01a0002f84647207cc3c9700946c75bec6cbac35f39d6cba31b459d0f62",{"cargo":[0,2]}]
["2025-02-27 10:22:34.890000","WriteReq","02f0aaf029000101023c00e803b80b00006e000201f0001f0a4747209039e5d573e0bce25dfea9b100f0730c022161c4e63e9ca5","org.apache.commons.codec.DecoderException: Odd length for hex string"]
["2025-02-27 10:22:34.948000","WriteReq",null,"QualifyingEvents","00000000",{"events":[]}]
["2025-02-27 10:22:35.008000","ReadResp",null,"QualifyingEvents","00200000",{"events":[{"event":"PROFILE_CHANGE"}]}]
["2025-02-27 10:22:35.012000","WriteReq","CURRENT_STATUS","request.currentStatus.ProfileStatusRequest","00f13ef100c832",{"cargo":[]}]
["2025-02-27 10:22:35.069000","ReadResp","CURRENT_STATUS","response.currentStatus.ProfileStatusResponse","00f13ff108020100ffffffff002337",{"idpSlot0Id":1,"activeSegmentIndex":0,"idpSlot1Id":0,"idpSlot2Id":-1,"idpSlot3Id":-1,"idpSlot4Id":-1,"cargo":[2,1,0,-1,-1,-1,-1,0],"idpSlot5Id":-1,"numberOfProfiles":2}]
["2025-02-27 10:22:35.073000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSettingsRequest","00f240f201005f54",{"idpId":0,"cargo":[0]}]
["2025-02-27 10:22:35.129000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSettingsResponse","00f241f2170063697100000000000000000000000000042c01a861015c3d",{"maxBolus":25000,"numberOfProfileSegments":4,"idpId":0,"insulinDuration":300,"name":"ciq","cargo":[0,99,105,113,0,0,0,0,0,0,0,0,0,0,0,0,0,4,44,1,-88,97,1],"carbEntry":true}]
["2025-02-27 10:22:35.132000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00f342f3020000162e",{"idpId":0,"segmentIndex":0,"cargo":[0,0]}]
["2025-02-27 10:22:35.189000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","00f343f30f000000000000701700006e001e000ffb72",{"idpStatusId":15,"profileCarbRatio":6000,"idpStatus":["TARGET_BG","BASAL_RATE","CARB_RATIO","CORRECTION_FACTOR"],"profileISF":30,"idpId":0,"profileStartTime":0,"segmentIndex":0,"profileTargetBG":110,"cargo":[0,0,0,0,0,0,112,23,0,0,110,0,30,0,15],"profileBasalRate":0}]
["2025-02-27 10:22:35.192000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00f442f40200011a6f",{"idpId":0,"segmentIndex":1,"cargo":[0,1]}]
["2025-02-27 10:22:35.249000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","00f443f40f0001e001b004701700006e001e000f8161",{"idpStatusId":15,"profileCarbRatio":6000,"idpStatus":["TARGET_BG","BASAL_RATE","CARB_RATIO","CORRECTION_FACTOR"],"profileISF":30,"idpId":0,"profileStartTime":480,"segmentIndex":1,"profileTargetBG":110,"cargo":[0,1,-32,1,-80,4,112,23,0,0,110,0,30,0,15],"profileBasalRate":1200}]
["2025-02-27 10:22:35.252000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00f542f5020002cd29",{"idpId":0,"segmentIndex":2,"cargo":[0,2]}]
["2025-02-27 10:22:35.309000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","00f543f50f00029402e803701700006e001e000f6232",{"idpStatusId":15,"profileCarbRatio":6000,"idpStatus":["TARGET_BG","BASAL_RATE","CARB_RATIO","CORRECTION_FACTOR"],"profileISF":30,"idpId":0,"profileStartTime":660,"segmentIndex":2,"profileTargetBG":110,"cargo":[0,2,-108,2,-24,3,112,23,0,0,110,0,30,0,15],"profileBasalRate":1000}]
["2025-02-27 10:22:39.169000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00f642f602000330a2",{"idpId":0,"segmentIndex":3,"cargo":[0,3]}]
["2025-02-27 10:22:39.869000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","00f643f60f0003d002e803701700006e001e000f5827",{"idpStatusId":15,"profileCarbRatio":6000,"idpStatus":["TARGET_BG","BASAL_RATE","CARB_RATIO","CORRECTION_FACTOR"],"profileISF":30,"idpId":0,"profileStartTime":720,"segmentIndex":3,"profileTargetBG":110,"cargo":[0,3,-48,2,-24,3,112,23,0,0,110,0,30,0,15],"profileBasalRate":1000}]

         */

        SetIDPSegmentRequest parsedReq = (SetIDPSegmentRequest) MessageTester.test(
                "02f0aaf029000101023c00e803b80b00006e0002",
                -16,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "01f0001f0a4747209039e5d573e0bce25dfea9b1",
                "00f0730c022161c4e63e9ca5"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

    @Test
    public void testScenario23_ProfCIQ_Delete8am() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        // CIQ profile idpid 0, new 1am profile
        SetIDPSegmentRequest expected = new SetIDPSegmentRequest(
                0, 1, 1,
                SetIDPSegmentRequest.IDPSegmentOperation.DELETE_SEGMENT_ID,
                480, 1_200, 6_000, 110, 30,
                IDPSegmentResponse.IDPSegmentStatus.toBitmask( // 1
                        IDPSegmentResponse.IDPSegmentStatus.BASAL_RATE,
                        IDPSegmentResponse.IDPSegmentStatus.CARB_RATIO,
                        IDPSegmentResponse.IDPSegmentStatus.TARGET_BG,
                        IDPSegmentResponse.IDPSegmentStatus.CORRECTION_FACTOR,
                        IDPSegmentResponse.IDPSegmentStatus.START_TIME)
        );
        /*
["2025-02-27 10:23:13.678000","ReadResp","CONTROL","response.control.SetIDPSegmentResponse","0001ab011a00021f4747204cb7a85c00624f0cb1d9ce4cb17274d922b3178359a6",{"cargo":[0,2]}]
["2025-02-27 10:23:13.738000","WriteReq",null,"QualifyingEvents","00000000",{"events":[]}]
["2025-02-27 10:23:13.799000","ReadResp",null,"QualifyingEvents","00200000",{"events":[{"event":"PROFILE_CHANGE"}]}]
["2025-02-27 10:23:13.803000","WriteReq","CURRENT_STATUS","request.currentStatus.ProfileStatusRequest","00023e02005a74",{"cargo":[]}]
["2025-02-27 10:23:13.859000","ReadResp","CURRENT_STATUS","response.currentStatus.ProfileStatusResponse","00023f0208020100ffffffff0059bd",{"idpSlot0Id":1,"activeSegmentIndex":0,"idpSlot1Id":0,"idpSlot2Id":-1,"idpSlot3Id":-1,"idpSlot4Id":-1,"cargo":[2,1,0,-1,-1,-1,-1,0],"idpSlot5Id":-1,"numberOfProfiles":2}]
["2025-02-27 10:23:13.864000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSettingsRequest","0003400301003d80",{"idpId":0,"cargo":[0]}]
["2025-02-27 10:23:13.920000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSettingsResponse","00034103170063697100000000000000000000000000032c01a86101d52d",{"maxBolus":25000,"numberOfProfileSegments":3,"idpId":0,"insulinDuration":300,"name":"ciq","cargo":[0,99,105,113,0,0,0,0,0,0,0,0,0,0,0,0,0,3,44,1,-88,97,1],"carbEntry":true}]
["2025-02-27 10:23:13.922000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","0004420402000076e0",{"idpId":0,"segmentIndex":0,"cargo":[0,0]}]
["2025-02-27 10:23:13.978000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","000443040f000000000000701700006e001e000f5aaa",{"idpStatusId":15,"profileCarbRatio":6000,"idpStatus":["BASAL_RATE","CARB_RATIO","CORRECTION_FACTOR","TARGET_BG"],"profileISF":30,"idpId":0,"profileStartTime":0,"segmentIndex":0,"profileTargetBG":110,"cargo":[0,0,0,0,0,0,112,23,0,0,110,0,30,0,15],"profileBasalRate":0}]
["2025-02-27 10:23:13.985000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00054205020001e386",{"idpId":0,"segmentIndex":1,"cargo":[0,1]}]
["2025-02-27 10:23:14.039000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","000543050f00019402e803701700006e001e000fce15",{"idpStatusId":15,"profileCarbRatio":6000,"idpStatus":["BASAL_RATE","CARB_RATIO","CORRECTION_FACTOR","TARGET_BG"],"profileISF":30,"idpId":0,"profileStartTime":660,"segmentIndex":1,"profileTargetBG":110,"cargo":[0,1,-108,2,-24,3,112,23,0,0,110,0,30,0,15],"profileBasalRate":1000}]
["2025-02-27 10:23:17.747000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","000642060200025c2d",{"idpId":0,"segmentIndex":2,"cargo":[0,2]}]
["2025-02-27 10:23:18.629000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","000643060f0002d002e803701700006e001e000f36f6",{"idpStatusId":15,"profileCarbRatio":6000,"idpStatus":["BASAL_RATE","CARB_RATIO","CORRECTION_FACTOR","TARGET_BG"],"profileISF":30,"idpId":0,"profileStartTime":720,"segmentIndex":2,"profileTargetBG":110,"cargo":[0,2,-48,2,-24,3,112,23,0,0,110,0,30,0,15],"profileBasalRate":1000}]
["2025-02-27 10:23:19.101000","WriteReq","CURRENT_STATUS","request.currentStatus.TimeSinceResetRequest","00093609000101",{"cargo":[]}]
["2025-02-27 10:23:19.769000","ReadResp","CURRENT_STATUS","response.currentStatus.TimeSinceResetResponse","00093709082547472076dd0200d477",{"currentTime":541542181,"pumpTimeSinceReset":187766,"currentTimeInstant":"2025-02-27T20:23:01Z","cargo":[37,71,71,32,118,-35,2,0]}]

0201aa012900010102e001b004701700006e001e
0101001f3047472038bce4d7667982c1938b392a
0001b68487f1b19428ac1da8
         */

        SetIDPSegmentRequest parsedReq = (SetIDPSegmentRequest) MessageTester.test(
                "0201aa012900010102e001b004701700006e001e",
                1,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "0101001f3047472038bce4d7667982c1938b392a",
                "0001b68487f1b19428ac1da8"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }


    @Test
    public void testScenario32_ProfC_New1am() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        // ProfC profile idpid 2, new 1am profile
        SetIDPSegmentRequest expected = new SetIDPSegmentRequest(
                2, 1, 0,
                SetIDPSegmentRequest.IDPSegmentOperation.CREATE_SEGMENT,
                60, 100, 20_000, 100, 10,
                IDPSegmentResponse.IDPSegmentStatus.toBitmask( // 31
                        IDPSegmentResponse.IDPSegmentStatus.BASAL_RATE,
                        IDPSegmentResponse.IDPSegmentStatus.CARB_RATIO,
                        IDPSegmentResponse.IDPSegmentStatus.TARGET_BG,
                        IDPSegmentResponse.IDPSegmentStatus.CORRECTION_FACTOR,
                        IDPSegmentResponse.IDPSegmentStatus.START_TIME)
        );
        /*
["2025-02-27 10:27:58.950000","ReadResp","CONTROL","response.control.SetIDPSegmentResponse","0046ab461a00023c4847204815136d41e8916346003b52d1bc7fce302e70e949bd",{"cargo":[0,2]}]
["2025-02-27 10:27:58.952000","WriteReq","CONTROL","request.control.SetIDPSegmentRequest","0246aa4629020100013c006400204e000064000a0146001f4e48472048909b74af7e295eea7f822700461e2555dacc8c479a682f",{"idpStatusId":31,"idpId":2,"unknownId":1,"profileBasalRate":100,"profileCarbRatio":20000,"idpStatus":["CARB_RATIO","UNKNOWN_16","TARGET_BG","CORRECTION_FACTOR","BASAL_RATE"],"profileISF":10,"profileStartTime":60,"operationId":1,"segmentIndex":0,"profileTargetBG":100,"cargo":[2,1,0,1,60,0,100,0,32,78,0,0,100,0,10,0,31],"operation":"CREATE_SEGMENT"}]
["2025-02-27 10:27:59.011000","WriteReq",null,"QualifyingEvents","00000000",{"events":[]}]
["2025-02-27 10:27:59.072000","ReadResp",null,"QualifyingEvents","00200000",{"events":[{"event":"PROFILE_CHANGE"}]}]
["2025-02-27 10:27:59.076000","WriteReq","CURRENT_STATUS","request.currentStatus.ProfileStatusRequest","00473e47006386",{"cargo":[]}]
["2025-02-27 10:27:59.132000","ReadResp","CURRENT_STATUS","response.currentStatus.ProfileStatusResponse","00473f470803010200ffffff000f9b",{"idpSlot0Id":1,"activeSegmentIndex":0,"idpSlot1Id":2,"idpSlot2Id":0,"idpSlot3Id":-1,"idpSlot4Id":-1,"cargo":[3,1,2,0,-1,-1,-1,0],"idpSlot5Id":-1,"numberOfProfiles":3}]
["2025-02-27 10:27:59.136000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSettingsRequest","004840480102234d",{"idpId":2,"cargo":[2]}]
["2025-02-27 10:27:59.192000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSettingsResponse","00484148170250726f66430000000000000000000000022c01a861012d95",{"maxBolus":25000,"numberOfProfileSegments":2,"idpId":2,"insulinDuration":300,"name":"ProfC","cargo":[2,80,114,111,102,67,0,0,0,0,0,0,0,0,0,0,0,2,44,1,-88,97,1],"carbEntry":true}]
["2025-02-27 10:27:59.195000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","004942490202000ed1",{"idpId":2,"segmentIndex":0,"cargo":[2,0]}]
["2025-02-27 10:27:59.252000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","004943490f020000000000204e000064000a000fba0c",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["CARB_RATIO","TARGET_BG","CORRECTION_FACTOR","BASAL_RATE"],"profileISF":10,"idpId":2,"profileStartTime":0,"segmentIndex":0,"profileTargetBG":100,"cargo":[2,0,0,0,0,0,32,78,0,0,100,0,10,0,15],"profileBasalRate":0}]
["2025-02-27 10:28:02.962000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","004a424a020201f35a",{"idpId":2,"segmentIndex":1,"cargo":[2,1]}]
["2025-02-27 10:28:03.811000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","004a434a0f02013c006400204e000064000a000f3ecc",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["CARB_RATIO","TARGET_BG","CORRECTION_FACTOR","BASAL_RATE"],"profileISF":10,"idpId":2,"profileStartTime":60,"segmentIndex":1,"profileTargetBG":100,"cargo":[2,1,60,0,100,0,32,78,0,0,100,0,10,0,15],"profileBasalRate":100}]

         */

        SetIDPSegmentRequest parsedReq = (SetIDPSegmentRequest) MessageTester.test(
                "0246aa4629020100013c006400204e000064000a",
                70,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "0146001f4e48472048909b74af7e295eea7f8227",
                "00461e2555dacc8c479a682f"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

    @Test
    public void testScenario45_Set1amProfCDup() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        SetIDPSegmentRequest expected = new SetIDPSegmentRequest(
                4, 1, 1,
                SetIDPSegmentRequest.IDPSegmentOperation.MODIFY_SEGMENT_ID,
                60, 110, 20_000, 100, 10,
                IDPSegmentResponse.IDPSegmentStatus.toBitmask( // 1
                        IDPSegmentResponse.IDPSegmentStatus.BASAL_RATE)
        );
        /*
["2025-02-27 10:31:21.311000","WriteReq","CURRENT_STATUS","request.currentStatus.TimeSinceResetRequest","007c367c00adf6",{"cargo":[]}]
["2025-02-27 10:31:21.483000","ReadResp","CURRENT_STATUS","response.currentStatus.TimeSinceResetResponse","007c377c080549472057df02005f0c",{"currentTime":541542661,"pumpTimeSinceReset":188247,"currentTimeInstant":"2025-02-27T20:31:01Z","cargo":[5,73,71,32,87,-33,2,0]}]
["2025-02-27 10:31:36.363000","ReadResp","CONTROL","response.control.SetIDPSegmentResponse","007eab7e1a0002164947205ae8ef35c4160093b25f3f16d378f24cffc415268662",{"cargo":[0,2]}]
["2025-02-27 10:31:36.365000","WriteReq","CONTROL","request.control.SetIDPSegmentRequest","027eaa7e29040101003c006e00204e000064000a017e000127494720a102f4a7574aad168a3d3c6c007e6009cc5ed6116ec63515",{"idpStatusId":1,"idpId":4,"unknownId":1,"profileBasalRate":110,"profileCarbRatio":20000,"idpStatus":["BASAL_RATE"],"profileISF":10,"profileStartTime":60,"operationId":0,"segmentIndex":1,"profileTargetBG":100,"cargo":[4,1,1,0,60,0,110,0,32,78,0,0,100,0,10,0,1],"operation":"MODIFY_SEGMENT_ID"}]
["2025-02-27 10:31:36.423000","WriteReq",null,"QualifyingEvents","00000000",{"events":[]}]
["2025-02-27 10:31:36.484000","ReadResp",null,"QualifyingEvents","00200000",{"events":[{"event":"PROFILE_CHANGE"}]}]
["2025-02-27 10:31:36.487000","WriteReq","CURRENT_STATUS","request.currentStatus.ProfileStatusRequest","007f3e7f005f0a",{"cargo":[]}]
["2025-02-27 10:31:36.544000","ReadResp","CURRENT_STATUS","response.currentStatus.ProfileStatusResponse","007f3f7f08050104030200ff008ff6",{"idpSlot0Id":1,"activeSegmentIndex":0,"idpSlot1Id":4,"idpSlot2Id":3,"idpSlot3Id":2,"idpSlot4Id":0,"cargo":[5,1,4,3,2,0,-1,0],"idpSlot5Id":-1,"numberOfProfiles":5}]
["2025-02-27 10:31:36.548000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSettingsRequest","008040800104b3a2",{"idpId":4,"cargo":[4]}]
["2025-02-27 10:31:36.603000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSettingsResponse","00804180170450726f66434475700000000000000000022c01a861013f64",{"maxBolus":25000,"numberOfProfileSegments":2,"idpId":4,"insulinDuration":300,"name":"ProfCDup","cargo":[4,80,114,111,102,67,68,117,112,0,0,0,0,0,0,0,0,2,44,1,-88,97,1],"carbEntry":true}]
["2025-02-27 10:31:36.608000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","00814281020400cf4d",{"idpId":4,"segmentIndex":0,"cargo":[4,0]}]
["2025-02-27 10:31:36.692000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","008143810f040000000000204e000064000a000fcdd5",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["BASAL_RATE","TARGET_BG","CORRECTION_FACTOR","CARB_RATIO"],"profileISF":10,"idpId":4,"profileStartTime":0,"segmentIndex":0,"profileTargetBG":100,"cargo":[4,0,0,0,0,0,32,78,0,0,100,0,10,0,15],"profileBasalRate":0}]
["2025-02-27 10:31:40.401000","WriteReq","CURRENT_STATUS","request.currentStatus.IDPSegmentRequest","0082428202040132c6",{"idpId":4,"segmentIndex":1,"cargo":[4,1]}]
["2025-02-27 10:31:41.284000","ReadResp","CURRENT_STATUS","response.currentStatus.IDPSegmentResponse","008243820f04013c006e00204e000064000a000f7417",{"idpStatusId":15,"profileCarbRatio":20000,"idpStatus":["BASAL_RATE","TARGET_BG","CORRECTION_FACTOR","CARB_RATIO"],"profileISF":10,"idpId":4,"profileStartTime":60,"segmentIndex":1,"profileTargetBG":100,"cargo":[4,1,60,0,110,0,32,78,0,0,100,0,10,0,15],"profileBasalRate":110}]

         */

        SetIDPSegmentRequest parsedReq = (SetIDPSegmentRequest) MessageTester.test(
                "027eaa7e29040101003c006e00204e000064000a",
                126,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "017e000127494720a102f4a7574aad168a3d3c6c",
                "007e6009cc5ed6116ec63515"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

}
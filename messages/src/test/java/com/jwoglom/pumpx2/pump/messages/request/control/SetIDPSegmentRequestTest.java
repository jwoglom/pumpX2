package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.SetIDPSegmentRequest;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.IDPSegmentResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class SetIDPSegmentRequestTest {
    @Test
    public void testSetIDPSegmentRequest_idp1_segment0_midnight() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        SetIDPSegmentRequest expected = new SetIDPSegmentRequest(
                1, 0,
                SetIDPSegmentRequest.IDPSegmentOperation.MODIFY,
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
                1, 0,
                SetIDPSegmentRequest.IDPSegmentOperation.CREATE_AFTER,
                60, 1000, 3000, 100, 2,
                31 // ??
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
                1, 1,
                SetIDPSegmentRequest.IDPSegmentOperation.DELETE,
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
}
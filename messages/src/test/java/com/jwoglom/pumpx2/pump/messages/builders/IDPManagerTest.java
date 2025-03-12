package com.jwoglom.pumpx2.pump.messages.builders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.jwoglom.pumpx2.pump.messages.request.currentStatus.IDPSegmentRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.IDPSettingsRequest;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.IDPSegmentResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.IDPSettingsResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ProfileStatusResponse;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IDPManagerTest {
    static final IDPSettingsResponse PROFILE_0 = new IDPSettingsResponse(0, "PROFILE0", 4, 300, 25_000, true);
    static final IDPSettingsResponse PROFILE_1 = new IDPSettingsResponse(1, "PROFILE1", 2, 200, 20_000, false);
    static final IDPSettingsResponse PROFILE_2 = new IDPSettingsResponse(2, "PROFILE2", 1, 200, 20_000, false);

    @Test
    public void testNextMessages() {
        ProfileStatusResponse profileStatus = new ProfileStatusResponse(
                3,
                2,
                1,
                0,
                -1,
                -1,
                -1,
                2
        );
        IDPManager idpManager = new IDPManager(profileStatus);
        assertFalse(idpManager.isComplete());
        assertEquals(List.of(
            new IDPSettingsRequest(2),
                new IDPSettingsRequest(1),
                new IDPSettingsRequest(0)
        ), idpManager.nextMessages());

        idpManager.processMessage(PROFILE_0);
        idpManager.processMessage(PROFILE_1);
        assertFalse(idpManager.isComplete());


        assertEquals(List.of(
                new IDPSettingsRequest(2),
                new IDPSegmentRequest(1, 0),
                new IDPSegmentRequest(1, 1),
                new IDPSegmentRequest(0, 0),
                new IDPSegmentRequest(0, 1),
                new IDPSegmentRequest(0, 2),
                new IDPSegmentRequest(0, 3)
        ), idpManager.nextMessages());
        idpManager.processMessage(new IDPSegmentResponse(0, 3, 300, 3_000, 30, 103, 3, IDPSegmentResponse.IDPSegmentStatus.toBitmask(IDPSegmentResponse.IDPSegmentStatus.values())));


        assertEquals(List.of(
                new IDPSettingsRequest(2),
                new IDPSegmentRequest(1, 0),
                new IDPSegmentRequest(1, 1),
                new IDPSegmentRequest(0, 0),
                new IDPSegmentRequest(0, 1),
                new IDPSegmentRequest(0, 2)
        ), idpManager.nextMessages());
        idpManager.processMessage(new IDPSegmentResponse(0, 2, 200, 2_000, 20, 102, 2, IDPSegmentResponse.IDPSegmentStatus.toBitmask(IDPSegmentResponse.IDPSegmentStatus.values())));
        idpManager.processMessage(new IDPSegmentResponse(0, 1, 100, 1_000, 10, 101, 1, IDPSegmentResponse.IDPSegmentStatus.toBitmask(IDPSegmentResponse.IDPSegmentStatus.values())));
        idpManager.processMessage(new IDPSegmentResponse(0, 0, 0, 500, 5, 100, 1, IDPSegmentResponse.IDPSegmentStatus.toBitmask(IDPSegmentResponse.IDPSegmentStatus.values())));

        assertFalse(idpManager.isComplete());
        assertEquals(List.of(
                new IDPSettingsRequest(2),
                new IDPSegmentRequest(1, 0),
                new IDPSegmentRequest(1, 1)
        ), idpManager.nextMessages());
        idpManager.processMessage(PROFILE_2);

        assertFalse(idpManager.isComplete());
        assertEquals(List.of(
                new IDPSegmentRequest(2, 0),
                new IDPSegmentRequest(1, 0),
                new IDPSegmentRequest(1, 1)
        ), idpManager.nextMessages());

        idpManager.processMessage(new IDPSegmentResponse(1, 1, 100, 1_000, 10, 101, 1, IDPSegmentResponse.IDPSegmentStatus.toBitmask(IDPSegmentResponse.IDPSegmentStatus.values())));
        idpManager.processMessage(new IDPSegmentResponse(1, 0, 0, 500, 5, 100, 1, IDPSegmentResponse.IDPSegmentStatus.toBitmask(IDPSegmentResponse.IDPSegmentStatus.values())));
        idpManager.processMessage(new IDPSegmentResponse(2, 0, 0, 500, 5, 100, 1, IDPSegmentResponse.IDPSegmentStatus.toBitmask(IDPSegmentResponse.IDPSegmentStatus.values())));

        assertTrue(idpManager.isComplete());
        assertEquals(List.of(), idpManager.nextMessages());

        assertEquals("IDPManager[" +
                "profileStatusResponse=ProfileStatusResponse[activeSegmentIndex=2,idpSlot0Id=2,idpSlot1Id=1,idpSlot2Id=0,idpSlot3Id=-1,idpSlot4Id=-1,idpSlot5Id=-1,numberOfProfiles=3,cargo={3,2,1,0,-1,-1,-1,2}]," +
                "profiles=[" +
                    "IDPManager.Profile[" +
                        "idpSettingsResponse=IDPSettingsResponse[carbEntry=false,idpId=2,insulinDuration=200,maxBolus=20000,name=PROFILE2,numberOfProfileSegments=1,cargo={2,80,82,79,70,73,76,69,50,0,0,0,0,0,0,0,0,1,-56,0,32,78,0}]," +
                        "isActiveProfile=false," +
                        "segments=[" +
                            "IDPSegmentResponse[idpId=2,idpStatus=[CARB_RATIO, CORRECTION_FACTOR, TARGET_BG, BASAL_RATE, START_TIME],idpStatusId=31,profileBasalRate=500,profileCarbRatio=5,profileISF=1,profileProcessedStartTime=00:00,profileStartTime=0,profileTargetBG=100,segmentIndex=0,cargo={2,0,0,0,-12,1,5,0,0,0,100,0,1,0,31}]" +
                        "]" +
                    "], " +
                    "IDPManager.Profile[" +
                        "idpSettingsResponse=IDPSettingsResponse[carbEntry=false,idpId=1,insulinDuration=200,maxBolus=20000,name=PROFILE1,numberOfProfileSegments=2,cargo={1,80,82,79,70,73,76,69,49,0,0,0,0,0,0,0,0,2,-56,0,32,78,0}]," +
                        "isActiveProfile=false," +
                        "segments=[" +
                            "IDPSegmentResponse[idpId=1,idpStatus=[CARB_RATIO, CORRECTION_FACTOR, TARGET_BG, BASAL_RATE, START_TIME],idpStatusId=31,profileBasalRate=500,profileCarbRatio=5,profileISF=1,profileProcessedStartTime=00:00,profileStartTime=0,profileTargetBG=100,segmentIndex=0,cargo={1,0,0,0,-12,1,5,0,0,0,100,0,1,0,31}], " +
                            "IDPSegmentResponse[idpId=1,idpStatus=[CARB_RATIO, CORRECTION_FACTOR, TARGET_BG, BASAL_RATE, START_TIME],idpStatusId=31,profileBasalRate=1000,profileCarbRatio=10,profileISF=1,profileProcessedStartTime=01:40,profileStartTime=100,profileTargetBG=101,segmentIndex=1,cargo={1,1,100,0,-24,3,10,0,0,0,101,0,1,0,31}]" +
                        "]" +
                    "], " +
                    "IDPManager.Profile[" +
                        "idpSettingsResponse=IDPSettingsResponse[carbEntry=true,idpId=0,insulinDuration=300,maxBolus=25000,name=PROFILE0,numberOfProfileSegments=4,cargo={0,80,82,79,70,73,76,69,48,0,0,0,0,0,0,0,0,4,44,1,-88,97,1}]," +
                        "isActiveProfile=true," +
                        "segments=[" +
                            "IDPSegmentResponse[idpId=0,idpStatus=[CARB_RATIO, CORRECTION_FACTOR, TARGET_BG, BASAL_RATE, START_TIME],idpStatusId=31,profileBasalRate=500,profileCarbRatio=5,profileISF=1,profileProcessedStartTime=00:00,profileStartTime=0,profileTargetBG=100,segmentIndex=0,cargo={0,0,0,0,-12,1,5,0,0,0,100,0,1,0,31}], " +
                            "IDPSegmentResponse[idpId=0,idpStatus=[CARB_RATIO, CORRECTION_FACTOR, TARGET_BG, BASAL_RATE, START_TIME],idpStatusId=31,profileBasalRate=1000,profileCarbRatio=10,profileISF=1,profileProcessedStartTime=01:40,profileStartTime=100,profileTargetBG=101,segmentIndex=1,cargo={0,1,100,0,-24,3,10,0,0,0,101,0,1,0,31}], " +
                            "IDPSegmentResponse[idpId=0,idpStatus=[CARB_RATIO, CORRECTION_FACTOR, TARGET_BG, BASAL_RATE, START_TIME],idpStatusId=31,profileBasalRate=2000,profileCarbRatio=20,profileISF=2,profileProcessedStartTime=03:20,profileStartTime=200,profileTargetBG=102,segmentIndex=2,cargo={0,2,-56,0,-48,7,20,0,0,0,102,0,2,0,31}], " +
                            "IDPSegmentResponse[idpId=0,idpStatus=[CARB_RATIO, CORRECTION_FACTOR, TARGET_BG, BASAL_RATE, START_TIME],idpStatusId=31,profileBasalRate=3000,profileCarbRatio=30,profileISF=3,profileProcessedStartTime=05:00,profileStartTime=300,profileTargetBG=103,segmentIndex=3,cargo={0,3,44,1,-72,11,30,0,0,0,103,0,3,0,31}]" +
                        "]" +
                    "]" +
                "]" +
            "]", idpManager.toString());
    }
}

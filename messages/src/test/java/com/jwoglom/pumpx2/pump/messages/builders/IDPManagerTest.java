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
    }
}

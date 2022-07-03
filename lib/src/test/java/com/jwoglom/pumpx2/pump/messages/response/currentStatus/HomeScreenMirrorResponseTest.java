package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.HomeScreenMirrorResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class HomeScreenMirrorResponseTest {
    @Test
    public void testHomeScreenMirrorResponse() throws DecoderException {
        HomeScreenMirrorResponse expected = new HomeScreenMirrorResponse(
            // int cgmTrendIcon, int cgmAlertIcon, int statusIcon0, int statusIcon1, int bolusStatusIcon, int basalStatusIcon, int apControlStateIcon, boolean remainingInsulinPlusIcon, boolean cgmDisplayData
                0, 255, 200, 200, 200, 4, 0, false, false
        );

        HomeScreenMirrorResponse parsedRes = (HomeScreenMirrorResponse) MessageTester.test(
                "000339030900ffc8c8c804000000c1a1",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(parsedRes.getCgmTrendIcon(), HomeScreenMirrorResponse.CGMTrendIcon.NO_ARROW);
        assertEquals(parsedRes.getCgmAlertIcon(), HomeScreenMirrorResponse.CGMAlertIcon.NO_ERROR);
        assertEquals(parsedRes.getStatusIcon0(), HomeScreenMirrorResponse.StatusIcon0.HIDE_ICON);
        assertEquals(parsedRes.getStatusIcon1(), HomeScreenMirrorResponse.StatusIcon1.HIDE_ICON);
        assertEquals(parsedRes.getBolusStatusIcon(), HomeScreenMirrorResponse.BolusStatusIcon.HIDE_ICON);
        assertEquals(parsedRes.getBasalStatusIcon(), HomeScreenMirrorResponse.BasalStatusIcon.SUSPEND);
        assertEquals(parsedRes.getApControlStateIcon(), HomeScreenMirrorResponse.ApControlStateIcon.STATE_GRAY);
    }
}
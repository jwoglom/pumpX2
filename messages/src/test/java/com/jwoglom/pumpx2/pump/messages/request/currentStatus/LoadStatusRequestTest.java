package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.jwoglom.pumpx2.pump.messages.models.KnownDeviceModel;
import com.jwoglom.pumpx2.pump.messages.models.SupportedDevices;
import org.junit.Test;

public class LoadStatusRequestTest {
    @Test
    public void testLoadStatusIsMobiOnly() {
        SupportedDevices devices = new LoadStatusRequest().props().supportedDevices();
        assertEquals(SupportedDevices.MOBI_ONLY, devices);
        assertTrue(devices.supports(KnownDeviceModel.MOBI));
        assertFalse(devices.supports(KnownDeviceModel.TSLIM_X2));
    }

    @Test
    public void testLoadStatusRequestHasEmptyCargo() {
        LoadStatusRequest request = new LoadStatusRequest();
        assertEquals(0, request.getCargo().length);

        request.parse(new byte[]{});
        assertEquals(0, request.getCargo().length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoadStatusRequestRejectsNonEmptyCargo() {
        new LoadStatusRequest().parse(new byte[]{1});
    }
}

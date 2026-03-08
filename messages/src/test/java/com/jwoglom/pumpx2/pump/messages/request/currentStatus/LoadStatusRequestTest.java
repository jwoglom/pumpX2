package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LoadStatusRequestTest {
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

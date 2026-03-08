package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

@SuppressWarnings("deprecation")
public class LoadStatusResponseTest {
    @Test
    public void testLoadStatusResponsePrimeTubingSchema() {
        LoadStatusResponse response = new LoadStatusResponse(new byte[]{1, 2, 3});

        assertEquals(1, response.getIsLoadingActiveId());
        assertTrue(response.getIsLoadingActive());
        assertEquals(LoadStatusResponse.LoadState.PRIME_TUBING, response.getLoadState());
        assertEquals(2, response.getLoadStateId());
        assertEquals(3, response.getPrimeStatusId());
        assertEquals(LoadStatusResponse.PrimeTubingStatus.ENTERED_CANNOT_EXIT, response.getPrimeTubingStatus());
        assertNull(response.getPrimeNudgeStatus());
        assertTrue(response.getIsInLoadingState());

        // Legacy compatibility aliases
        assertEquals(1, response.getStatus());
        assertEquals(0x0302, response.getUnknown());
    }

    @Test
    public void testLoadStatusResponsePrimeNudgeSchema() {
        LoadStatusResponse response = new LoadStatusResponse(new byte[]{0, 4, 1});

        assertEquals(0, response.getIsLoadingActiveId());
        assertFalse(response.getIsLoadingActive());
        assertEquals(LoadStatusResponse.LoadState.PRIME_NUDGE, response.getLoadState());
        assertEquals(4, response.getLoadStateId());
        assertEquals(1, response.getPrimeStatusId());
        assertNull(response.getPrimeTubingStatus());
        assertEquals(LoadStatusResponse.PrimeNudgeStatus.COMPLETE, response.getPrimeNudgeStatus());
        assertFalse(response.getIsInLoadingState());
    }

    @Test
    public void testLoadStatusResponseNonPrimingSchema() {
        LoadStatusResponse response = new LoadStatusResponse(new byte[]{1, 1, 7});

        assertEquals(LoadStatusResponse.LoadState.LOAD_CARTRIDGE, response.getLoadState());
        assertEquals(7, response.getPrimeStatusId());
        assertNull(response.getPrimeTubingStatus());
        assertNull(response.getPrimeNudgeStatus());
        assertTrue(response.getIsInLoadingState());
    }

    @Test
    public void testLoadStatusResponseBuildCargo() {
        byte[] built = LoadStatusResponse.buildCargo(
                true,
                LoadStatusResponse.LoadState.PRIME_TUBING,
                LoadStatusResponse.PrimeTubingStatus.SUSPENDED,
                null
        );
        assertArrayEquals(new byte[]{1, 2, 4}, built);

        LoadStatusResponse parsed = new LoadStatusResponse(built);
        assertEquals(LoadStatusResponse.PrimeTubingStatus.SUSPENDED, parsed.getPrimeTubingStatus());
        assertNull(parsed.getPrimeNudgeStatus());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoadStatusResponseRejectsWrongSize() {
        new LoadStatusResponse().parse(new byte[]{1, 2});
    }
}

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
    public void testLoadStatusResponse_fillTubingInProgress() {
        LoadStatusResponse response = new LoadStatusResponse(new byte[]{1, 2, 0});

        assertEquals(1, response.getIsLoadingActiveId());
        assertTrue(response.getIsLoadingActive());
        assertEquals(LoadStatusResponse.LoadState.PRIME_TUBING, response.getLoadState());
        assertEquals(2, response.getLoadStateId());
        assertEquals(0, response.getPrimeStatusId());
        assertEquals(LoadStatusResponse.PrimeTubingStatus.STOP, response.getPrimeTubingStatus());
        assertNull(response.getPrimeNudgeStatus());
        assertTrue(response.getIsInLoadingState());
    }

    @Test
    public void testLoadStatusResponse_fillTubingComplete() {
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
    public void testLoadStatusResponse_fillCannulaInProgress() {
        LoadStatusResponse response = new LoadStatusResponse(new byte[]{1, 4, 1});

        assertEquals(1, response.getIsLoadingActiveId());
        assertTrue(response.getIsLoadingActive());
        assertEquals(LoadStatusResponse.LoadState.PRIME_NUDGE, response.getLoadState());
        assertEquals(4, response.getLoadStateId());
        assertEquals(1, response.getPrimeStatusId());
        assertNull(response.getPrimeTubingStatus());
        assertEquals(LoadStatusResponse.PrimeNudgeStatus.COMPLETE, response.getPrimeNudgeStatus());
        assertFalse(response.getIsInLoadingState());
    }

    @Test
    public void testLoadStatusResponse_fillCannulaFinished() {
        LoadStatusResponse response = new LoadStatusResponse(new byte[]{0, 3, 0});

        assertEquals(0, response.getIsLoadingActiveId());
        assertFalse(response.getIsLoadingActive());
        assertEquals(LoadStatusResponse.LoadState.PRIME_CANNULA, response.getLoadState());
        assertEquals(3, response.getLoadStateId());
        assertEquals(0, response.getPrimeStatusId());
        assertNull(response.getPrimeTubingStatus());
        assertNull(response.getPrimeNudgeStatus());
        assertFalse(response.getIsInLoadingState());
    }

    @Test
    public void testLoadStatusResponse_changeCartridgeInProgress() {
        LoadStatusResponse response = new LoadStatusResponse(new byte[]{1, 0, 0});

        assertEquals(1, response.getIsLoadingActiveId());
        assertTrue(response.getIsLoadingActive());
        assertEquals(LoadStatusResponse.LoadState.CHANGE_CARTRIDGE, response.getLoadState());
        assertEquals(0, response.getLoadStateId());
        assertEquals(0, response.getPrimeStatusId());
        assertNull(response.getPrimeTubingStatus());
        assertNull(response.getPrimeNudgeStatus());
        assertTrue(response.getIsInLoadingState());
    }

    @Test
    public void testLoadStatusResponse_cartridgeChanged() {
        LoadStatusResponse response = new LoadStatusResponse(new byte[]{1, 1, 0});

        assertEquals(LoadStatusResponse.LoadState.LOAD_CARTRIDGE, response.getLoadState());
        assertEquals(1, response.getIsLoadingActiveId());
        assertTrue(response.getIsLoadingActive());
        assertEquals(1, response.getLoadStateId());
        assertEquals(0, response.getPrimeStatusId());
        assertNull(response.getPrimeTubingStatus());
        assertNull(response.getPrimeNudgeStatus());
        assertTrue(response.getIsInLoadingState());
    }

    @Test
    public void testLoadStatusResponseInvalidFixture() {
        LoadStatusResponse response = new LoadStatusResponse(new byte[]{0, 5, 0});

        assertEquals(0, response.getIsLoadingActiveId());
        assertFalse(response.getIsLoadingActive());
        assertEquals(LoadStatusResponse.LoadState.INVALID, response.getLoadState());
        assertEquals(5, response.getLoadStateId());
        assertEquals(0, response.getPrimeStatusId());
        assertNull(response.getPrimeTubingStatus());
        assertNull(response.getPrimeNudgeStatus());
        assertFalse(response.getIsInLoadingState());
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

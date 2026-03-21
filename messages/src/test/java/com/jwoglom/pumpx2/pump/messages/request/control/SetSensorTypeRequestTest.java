package com.jwoglom.pumpx2.pump.messages.request.control;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CgmStatusV2Response;

import org.junit.Test;

public class SetSensorTypeRequestTest {

    @Test
    public void testBuildCargo_dexcomG7() {
        byte[] cargo = SetSensorTypeRequest.buildCargo(3);
        assertArrayEquals(new byte[]{0x03}, cargo);
    }

    @Test
    public void testBuildCargo_notApplicable() {
        byte[] cargo = SetSensorTypeRequest.buildCargo(0);
        assertArrayEquals(new byte[]{0x00}, cargo);
    }

    @Test
    public void testConstructAndParse_dexcomG6() {
        SetSensorTypeRequest req = new SetSensorTypeRequest(1);
        assertEquals(1, req.getCgmSensorType());
        assertEquals(CgmStatusV2Response.CgmSensorType.DEXCOM_G6, req.getCgmSensorTypeType());
    }

    @Test
    public void testConstructFromEnum() {
        SetSensorTypeRequest req = new SetSensorTypeRequest(CgmStatusV2Response.CgmSensorType.DEXCOM_G7);
        assertEquals(3, req.getCgmSensorType());
        assertEquals(CgmStatusV2Response.CgmSensorType.DEXCOM_G7, req.getCgmSensorTypeType());
    }

    @Test
    public void testParse() {
        SetSensorTypeRequest req = new SetSensorTypeRequest();
        req.parse(new byte[]{0x02});
        assertEquals(2, req.getCgmSensorType());
        assertEquals(CgmStatusV2Response.CgmSensorType.FSL2, req.getCgmSensorTypeType());
    }
}

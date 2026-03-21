package com.jwoglom.pumpx2.pump.messages.response.control;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SetSensorTypeResponseTest {

    @Test
    public void testBuildCargo_ackSuccess() {
        byte[] cargo = SetSensorTypeResponse.buildCargo(0, 0);
        assertArrayEquals(new byte[]{0x00, 0x00}, cargo);
    }

    @Test
    public void testConstructAndGetters_ackSuccess() {
        SetSensorTypeResponse resp = new SetSensorTypeResponse(0, 0);
        assertEquals(0, resp.getStatus());
        assertEquals(0, resp.getStatusAcknowledgement());
        assertEquals(SetSensorTypeResponse.StatusAcknowledgement.SUCCESS, resp.getStatusAcknowledgementType());
    }

    @Test
    public void testParse_nackInvalidCgmType() {
        SetSensorTypeResponse resp = new SetSensorTypeResponse();
        resp.parse(new byte[]{0x01, 0x02});
        assertEquals(1, resp.getStatus());
        assertEquals(2, resp.getStatusAcknowledgement());
        assertEquals(SetSensorTypeResponse.StatusAcknowledgement.INVALID_CGM_TYPE, resp.getStatusAcknowledgementType());
    }

    @Test
    public void testParse_nackMissingPackage() {
        SetSensorTypeResponse resp = new SetSensorTypeResponse();
        resp.parse(new byte[]{0x01, 0x04});
        assertEquals(1, resp.getStatus());
        assertEquals(SetSensorTypeResponse.StatusAcknowledgement.MISSING_OR_INVALID_CGM_SUPPORT_PACKAGE,
                resp.getStatusAcknowledgementType());
    }

    @Test
    public void testParse_unknownCode() {
        SetSensorTypeResponse resp = new SetSensorTypeResponse();
        resp.parse(new byte[]{0x01, 0x63});
        assertEquals(SetSensorTypeResponse.StatusAcknowledgement.UNKNOWN, resp.getStatusAcknowledgementType());
    }
}

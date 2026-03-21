package com.jwoglom.pumpx2.pump.messages.request.control;

import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.SetSensorTypeResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CgmStatusV2Response;

import org.apache.commons.lang3.Validate;

/**
 * Sets the CGM sensor type on the pump (SetCGMSensorType).
 * Required when changing between CGM sensor types before pair.
 *
 * Cargo layout (1 byte):
 *   [0] cgmSensorType  uint8  - CGMSensorType ordinal
 */
@MessageProps(
    opCode=-64,
    size=1,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    response=SetSensorTypeResponse.class,
    signed=true
)
public class SetSensorTypeRequest extends Message {
    private int cgmSensorType;

    public SetSensorTypeRequest() {}

    public SetSensorTypeRequest(int cgmSensorType) {
        this.cgmSensorType = cgmSensorType;
        this.cargo = buildCargo(cgmSensorType);
    }

    public SetSensorTypeRequest(CgmStatusV2Response.CgmSensorType sensorType) {
        this(sensorType.ordinal());
    }

    public static byte[] buildCargo(int cgmSensorType) {
        return new byte[]{ (byte) cgmSensorType };
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.cgmSensorType = raw[0] & 0xFF;
    }

    public int getCgmSensorType() {
        return cgmSensorType;
    }

    public CgmStatusV2Response.CgmSensorType getCgmSensorTypeType() {
        return CgmStatusV2Response.CgmSensorType.fromId(cgmSensorType);
    }
}

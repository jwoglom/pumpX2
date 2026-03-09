package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CgmSupportPackageStatusRequest;

/**
 * Response to CgmSupportPackageStatusRequest.
 *
 * Binary layout (2 bytes total):
 *   offset 0 (1 byte): status        - 0 = ACK/success, 1 = NACK/failure
 *   offset 1 (1 byte): validity      - nonzero = true, CGM support package is valid
 *
 * The Tandem app reads byte[0] as ResponseStatus (ACK=0, NACK=1) and
 * byte[1] as a boolean validity flag via Byte_ExtKt.readBoolean (nonzero = true).
 */
@MessageProps(
    opCode=-55,
    size=2,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CURRENT_STATUS,
    request=CgmSupportPackageStatusRequest.class,
    minApi=KnownApiVersion.API_FUTURE
)
public class CgmSupportPackageStatusResponse extends Message {
    private int status;
    private boolean validity;

    public CgmSupportPackageStatusResponse() {}

    public CgmSupportPackageStatusResponse(int status, boolean validity) {
        this.cargo = buildCargo(status, validity);
        this.status = status;
        this.validity = validity;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0] & 0xFF;
        this.validity = (raw[1] & 0xFF) != 0;
    }

    public static byte[] buildCargo(int status, boolean validity) {
        return Bytes.combine(
            new byte[]{ (byte) (status & 0xFF) },
            new byte[]{ (byte) (validity ? 1 : 0) });
    }

    /**
     * @return the raw status byte: 0 = ACK (success), 1 = NACK (failure)
     */
    public int getStatus() {
        return status;
    }

    /**
     * @return true if the status is ACK (success), false if NACK
     */
    public boolean isSuccess() {
        return status == 0;
    }

    /**
     * @return true if the CGM support package is valid (byte[1] nonzero),
     *         as reported by the Tandem app's CGMSupportPackageStatusCargo.validity()
     */
    public boolean getValidity() {
        return validity;
    }
}

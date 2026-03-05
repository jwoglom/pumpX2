package com.jwoglom.pumpx2.pump.messages.response.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.StatusMessage;
import com.jwoglom.pumpx2.pump.messages.request.control.AdditionalBolusRequest;

/**
 * Response to AdditionalBolusRequest.
 * OpCode 0xFB (251).
 *
 * Cargo layout:
 *   byte[0]   : status (0 = ACK/success)
 *   bytes[1-2]: bolusId (uint16 little-endian)
 *   bytes[3-4]: reserve (uint16 little-endian)
 */
@MessageProps(
    opCode=-5,
    size=5,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    request=AdditionalBolusRequest.class,
    signed=true,
    modifiesInsulinDelivery=true
)
public class AdditionalBolusResponse extends StatusMessage {

    private int status;
    private int bolusId;
    private int reserve;

    public AdditionalBolusResponse() {}

    public AdditionalBolusResponse(int status, int bolusId, int reserve) {
        this.cargo = buildCargo(status, bolusId, reserve);
        this.status = status;
        this.bolusId = bolusId;
        this.reserve = reserve;
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0];
        this.bolusId = Bytes.readShort(raw, 1);
        this.reserve = Bytes.readShort(raw, 3);
    }

    public static byte[] buildCargo(int status, int bolusId, int reserve) {
        return Bytes.combine(
            new byte[]{ (byte) status },
            Bytes.firstTwoBytesLittleEndian(bolusId),
            Bytes.firstTwoBytesLittleEndian(reserve)
        );
    }

    /**
     * @return 0 if successful (ACK)
     */
    public int getStatus() {
        return status;
    }

    public int getBolusId() {
        return bolusId;
    }

    public int getReserve() {
        return reserve;
    }

    /**
     * @return true when the additional bolus was accepted
     */
    public boolean wasAccepted() {
        return status == 0;
    }
}

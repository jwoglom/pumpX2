package com.jwoglom.pumpx2.pump.messages.response.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.control.InitiateBolusRequest;

@MessageProps(
    opCode=-97,
    size=6, // 30 with 24 byte hmac padding
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    request=InitiateBolusRequest.class,
    signed=true,
    modifiesInsulinDelivery=true
)
public class InitiateBolusResponse extends Message {
    
    private int status;
    private int bolusId;
    private int statusTypeId;
    
    public InitiateBolusResponse() {}
    
    public InitiateBolusResponse(int status, int bolusId, int statusTypeId) {
        this.cargo = buildCargo(status, bolusId, statusTypeId);
        this.status = status;
        this.bolusId = bolusId;
        this.statusTypeId = statusTypeId;
        
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0];
        this.bolusId = Bytes.readShort(raw, 1);
        this.statusTypeId = raw[5];
        
    }

    
    public static byte[] buildCargo(int status, int bolusId, int statusType) {
        return Bytes.combine(
            new byte[]{ (byte) status }, 
            Bytes.firstTwoBytesLittleEndian(bolusId),
            new byte[]{ 0, 0},
            new byte[]{ (byte) statusType });
    }

    /**
     * @return 0 if successful
     */
    public int getStatus() {
        return status;
    }
    public int getBolusId() {
        return bolusId;
    }
    public int getStatusTypeId() {
        return statusTypeId;
    }
    public BolusResponseStatus getStatusType() {
        return BolusResponseStatus.fromId(statusTypeId);
    }


    // identical to ChangeReason??
    public enum BolusResponseStatus {
        SUCCESS(0), // 0 = successBolusIsPending
        REVOKED_PRIORITY(2)

        ;

        private final int id;
        BolusResponseStatus(int id) {
            this.id = id;
        }

        public static BolusResponseStatus fromId(int id) {
            for (BolusResponseStatus s : values()) {
                if (s.id == id) {
                    return s;
                }
            }
            return null;
        }
    }

    /**
     * @return true when bolus was initiated
     */
    public boolean wasBolusInitiated() {
        return status == 0 && getStatusType() == BolusResponseStatus.SUCCESS;
    }
}
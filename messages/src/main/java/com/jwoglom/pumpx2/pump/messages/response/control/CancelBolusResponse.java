package com.jwoglom.pumpx2.pump.messages.response.control;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.request.control.CancelBolusRequest;

/**
 * also known as BolusTerminationResponse
 */
@MessageProps(
    opCode=-95,
    size=5, // 29 with 24 byte padding
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    request=CancelBolusRequest.class,
    signed=true,
    minApi=KnownApiVersion.API_V2_5
)
public class CancelBolusResponse extends Message {
    
    private int statusId;
    private int bolusId;
    private int reasonId;
    
    public CancelBolusResponse() {}
    
    public CancelBolusResponse(int statusId, int bolusId, int reasonId) {
        this.cargo = buildCargo(statusId, bolusId, reasonId);
        this.statusId = statusId;
        this.bolusId = bolusId;
        this.reasonId = reasonId;
        
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.statusId = raw[0];
        this.bolusId = Bytes.readShort(raw, 1);
        this.reasonId = Bytes.readShort(raw, 3);
        
    }

    
    public static byte[] buildCargo(int status, int bolusId, int reason) {
        return Bytes.combine(
            new byte[]{ (byte) status }, 
            Bytes.firstTwoBytesLittleEndian(bolusId),
            Bytes.firstTwoBytesLittleEndian(reason));
    }
    
    public int getStatusId() {
        return statusId;
    }
    public CancelStatus getStatus() {
        return CancelStatus.fromId(statusId);
    }
    public int getBolusId() {
        return bolusId;
    }
    public int getReasonId() {
        return reasonId;
    }
    public CancelReason getReason() {
        return CancelReason.fromId(reasonId);
    }

    public enum CancelStatus {
        SUCCESS(0),
        FAILED(1),

        ;
        private final int id;
        CancelStatus(int id) {
            this.id = id;
        }

        public static CancelStatus fromId(int id) {
            for (CancelStatus s : values()) {
                if (s.id == id) {
                    return s;
                }
            }
            return null;
        }
    }

    public enum CancelReason {
        NO_ERROR(0),
        INVALID_OR_ALREADY_DELIVERED(2)

        ;
        private final int id;
        CancelReason(int id) {
            this.id = id;
        }

        public static CancelReason fromId(int id) {
            for (CancelReason s : values()) {
                if (s.id == id) {
                    return s;
                }
            }
            return null;
        }
    }

    /**
     * @return true when the bolus was successfully cancelled
     */
    public boolean wasCancelled() {
        return getStatus() == CancelStatus.SUCCESS && getReason() == CancelReason.NO_ERROR;
    }
}
package com.jwoglom.pumpx2.pump.messages.response.control;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.request.control.BolusPermissionRequest;

@MessageProps(
    opCode=-93,
    size=6, // 30 with 24 byte hmac padding
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    request=BolusPermissionRequest.class,
    minApi=KnownApiVersion.API_V2_5,
    signed=true
)
public class BolusPermissionResponse extends Message {
    
    private int status;
    private int bolusId;
    private int nackReasonId;
    
    public BolusPermissionResponse() {}
    
    public BolusPermissionResponse(int status, int bolusId, int nackReasonId) {
        this.cargo = buildCargo(status, bolusId, nackReasonId);
        this.status = status;
        this.bolusId = bolusId;
        this.nackReasonId = nackReasonId;
        
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0];
        this.bolusId = Bytes.readShort(raw, 1);
        this.nackReasonId = raw[5];
        
    }

    
    public static byte[] buildCargo(int status, int bolusId, int nackReason) {
        return Bytes.combine(
            new byte[]{ (byte) status }, 
            Bytes.firstTwoBytesLittleEndian(bolusId), 
            new byte[]{ (byte) nackReason },
            new byte[]{0, 0});
    }
    
    public int getStatus() {
        return status;
    }
    public int getBolusId() {
        return bolusId;
    }
    public int getNackReasonId() {
        return nackReasonId;
    }

    public NackReason getNackReason() {
        return NackReason.fromId(nackReasonId);
    }

    public enum NackReason {
        PERMISSION_GRANTED(0),
        INVALID_PUMPING_STATE(1),
        PUMP_HAS_PERMISSION(3),
        MPP_STATE_WAITING_FOR_RESPONSE(-1),
        MPP_STATE_UNKNOWN_NACK_REASON(-3),

        ;
        private int id;
        NackReason(int id) {
            this.id = id;
        }

        public static NackReason fromId(int id) {
            for (NackReason r : values()) {
                if (r.id == id) {
                    return r;
                }
            }
            return null;
        }
    }
    
}
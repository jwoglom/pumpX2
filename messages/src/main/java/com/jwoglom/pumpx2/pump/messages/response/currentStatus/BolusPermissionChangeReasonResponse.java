package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.BolusPermissionChangeReasonRequest;

@MessageProps(
    opCode=-87, // 169
    size=5,
    type=MessageType.RESPONSE,
    request=BolusPermissionChangeReasonRequest.class,
    minApi=KnownApiVersion.API_FUTURE
)
public class BolusPermissionChangeReasonResponse extends Message {
    
    private int bolusId;
    private boolean isAcked;
    private int lastChangeReasonId;
    private boolean currentPermissionHolder;
    
    public BolusPermissionChangeReasonResponse() {}
    
    public BolusPermissionChangeReasonResponse(int bolusId, boolean isAcked, int lastChangeReasonId, boolean currentPermissionHolder) {
        this.cargo = buildCargo(bolusId, isAcked, lastChangeReasonId, currentPermissionHolder);
        this.bolusId = bolusId;
        this.isAcked = isAcked;
        this.lastChangeReasonId = lastChangeReasonId;
        this.currentPermissionHolder = currentPermissionHolder;
        
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.bolusId = Bytes.readShort(raw, 0);
        this.isAcked = raw[2] != 0;
        this.lastChangeReasonId = raw[3];
        this.currentPermissionHolder = raw[4] != 0;
        
    }

    
    public static byte[] buildCargo(int bolusId, boolean isAcked, int lastChangeReason, boolean currentPermissionHolder) {
        return Bytes.combine(
            Bytes.firstTwoBytesLittleEndian(bolusId), 
            new byte[]{ (byte) (isAcked ? 1 : 0) }, 
            new byte[]{ (byte) lastChangeReason }, 
            new byte[]{ (byte) (currentPermissionHolder ? 1 : 0) });
    }
    
    public int getBolusId() {
        return bolusId;
    }
    public boolean getIsAcked() {
        return isAcked;
    }
    public int getLastChangeReasonId() {
        return lastChangeReasonId;
    }
    public ChangeReason getLastChangeReason() {
        return ChangeReason.fromId(lastChangeReasonId);
    }
    public boolean getCurrentPermissionHolder() {
        return currentPermissionHolder;
    }

    public enum ChangeReason {
        GRANTED(0),
        RELEASED(1),
        REVOKED_PRIORITY(2),
        REVOKED_TIMEOUT(3),
        REVOKED_SETTINGS_CHANGED(4),
        REVOKED_PUMP_SUSPEND(5),
        REVOKED_INVALID_REQUEST(6),
        UNKNOWN(7),
        ;
        private final int id;
        ChangeReason(int id) {
            this.id = id;
        }

        public int getId() { return id; }

        public static ChangeReason fromId(int id) {
            for (ChangeReason r : values()) {
                if (r.id == id) {
                    return r;
                }
            }
            return null;
        }
    }
}
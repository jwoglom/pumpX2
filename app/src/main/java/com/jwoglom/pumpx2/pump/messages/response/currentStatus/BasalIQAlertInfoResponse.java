package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.BasalIQAlertInfoRequest;

import java.math.BigInteger;

@MessageProps(
    opCode=103,
    size=4,
    type=MessageType.RESPONSE,
    request=BasalIQAlertInfoRequest.class
)
public class BasalIQAlertInfoResponse extends Message {
    
    private long alertId;
    
    public BasalIQAlertInfoResponse() {}
    
    public BasalIQAlertInfoResponse(long alertId) {
        this.cargo = buildCargo(alertId);
        this.alertId = alertId;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.alertId = Bytes.readUint32(raw, 0);
        
    }

    
    public static byte[] buildCargo(long alertId) {
        return Bytes.combine(
            Bytes.toUint32(alertId));
    }
    
    public long getAlertId() {
        return alertId;
    }
    public BasalIQAlert getAlert() {
        return BasalIQAlert.fromId(alertId);
    }

    public enum BasalIQAlert {
        NO_ALERT(0),
        INSULIN_SUSPENDED_ALERT(24576),
        INSULIN_RESUMED_ALERT(24577),
        INSULIN_RESUMED_TIMEOUT(24578),
        ;

        private final long id;
        BasalIQAlert(long id) {
            this.id = id;
        }

        public long id() {
            return id;
        }

        public static BasalIQAlert fromId(long id) {
            for (BasalIQAlert a : values()) {
                if (a.id() == id) {
                    return a;
                }
            }
            return null;
        }
    }
    
}
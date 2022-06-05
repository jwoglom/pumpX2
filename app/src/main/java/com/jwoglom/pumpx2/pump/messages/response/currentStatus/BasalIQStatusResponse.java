package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.BasalIQStatusRequest;

import java.math.BigInteger;

@MessageProps(
    opCode=113,
    size=2,
    type=MessageType.RESPONSE,
    request=BasalIQStatusRequest.class
)
public class BasalIQStatusResponse extends Message {
    
    private int basalIQStatusState;
    private boolean deliveringTherapy;
    
    public BasalIQStatusResponse() {}
    
    public BasalIQStatusResponse(int basalIQStatusState, boolean deliveringTherapy) {
        this.cargo = buildCargo(basalIQStatusState, deliveringTherapy);
        this.basalIQStatusState = basalIQStatusState;
        this.deliveringTherapy = deliveringTherapy;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.basalIQStatusState = raw[0];
        this.deliveringTherapy = raw[1] != 0;
        
    }

    
    public static byte[] buildCargo(int basalIQStatusState, boolean deliveringTherapy) {
        return Bytes.combine(
            new byte[]{ (byte) basalIQStatusState }, 
            new byte[]{ (byte) (deliveringTherapy ? 1 : 0) });
    }
    
    public int getBasalIQStatusStateId() {
        return basalIQStatusState;
    }
    public BasalIQStatusState getBasalIQStatusState() {
        return BasalIQStatusState.fromId(basalIQStatusState);
    }
    public boolean getDeliveringTherapy() {
        return deliveringTherapy;
    }

    public enum BasalIQStatusState {
        Idle(0),
        Suspend(1),
        Disabled(2),
        Unavailable(3),
        ;

        private final int id;
        BasalIQStatusState(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }

        public static BasalIQStatusState fromId(int id) {
            for (BasalIQStatusState s : values()) {
                if (s.id() == id) {
                    return s;
                }
            }
            return null;
        }
    }
    
}
package com.jwoglom.pumpx2.pump.messages.response.controlStream;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.controlStream.NonexistentFillCannulaStateStreamRequest;

@MessageProps(
    opCode=-25,
    size=1,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL_STREAM,
    request= NonexistentFillCannulaStateStreamRequest.class,
    stream=true,
    signed=true
)
public class FillCannulaStateStreamResponse extends Message {
    
    private int stateId;
    private FillCannulaState state;
    
    public FillCannulaStateStreamResponse() {}
    
    public FillCannulaStateStreamResponse(int stateId) {
        this.cargo = buildCargo(stateId);
        parse(cargo);
        
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.stateId = raw[0];
        this.state = getState();
        
    }

    
    public static byte[] buildCargo(int state) {
        return Bytes.combine(
            new byte[]{ (byte) state });
    }
    
    public int getStateId() {
        return stateId;
    }

    public FillCannulaState getState() {
        return FillCannulaState.fromId(stateId);
    }


    public enum FillCannulaState {
        CANNULA_FILLED(2),
        ;

        private final int id;
        FillCannulaState(int id) {
            this.id = id;
        }


        public int getId() {
            return id;
        }

        public static FillCannulaState fromId(int id) {
            for (FillCannulaState c : values()) {
                if (c.id == id) return c;
            }
            return null;
        }
    }
    
}
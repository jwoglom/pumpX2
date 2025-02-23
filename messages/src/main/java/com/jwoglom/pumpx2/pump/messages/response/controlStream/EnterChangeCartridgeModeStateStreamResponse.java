package com.jwoglom.pumpx2.pump.messages.response.controlStream;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.controlStream.NonexistentEnterChangeCartridgeModeStateStreamRequest;

/**
 * Transaction ID of this stream message matches the call to EnterChangeCartridgeModeRequest
 */
@MessageProps(
    opCode=-31,
    size=1,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL_STREAM,
    request=NonexistentEnterChangeCartridgeModeStateStreamRequest.class,
    stream=true,
    signed=true
)
public class EnterChangeCartridgeModeStateStreamResponse extends Message {
    
    private int stateId;
    private ChangeCartridgeState state;
    
    public EnterChangeCartridgeModeStateStreamResponse() {}
    
    public EnterChangeCartridgeModeStateStreamResponse(int stateId) {
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

    public ChangeCartridgeState getState() {
        return ChangeCartridgeState.fromId(stateId);
    }

    public enum ChangeCartridgeState {
        READY_TO_CHANGE(2),
        ;

        private final int id;
        ChangeCartridgeState(int id) {
            this.id = id;
        }


        public int getId() {
            return id;
        }

        public static ChangeCartridgeState fromId(int id) {
            for (ChangeCartridgeState c : values()) {
                if (c.id == id) return c;
            }
            return null;
        }
    }
    
}
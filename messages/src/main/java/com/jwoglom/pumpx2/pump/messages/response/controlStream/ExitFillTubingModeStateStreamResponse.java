package com.jwoglom.pumpx2.pump.messages.response.controlStream;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.controlStream.NonexistentExitFillTubingModeStateStreamRequest;

/**
 * Stream message sent by pump while exiting fill tubing mode.
 *
 * Transaction ID of this stream message matches the call to ExitFillTubingModeRequest
 */
@MessageProps(
    opCode=-23,
    size=1,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL_STREAM,
    request=NonexistentExitFillTubingModeStateStreamRequest.class,
    stream=true,
    signed=true
)
public class ExitFillTubingModeStateStreamResponse extends Message {
    
    private int stateId;
    private ExitFillTubingModeState state;
    
    public ExitFillTubingModeStateStreamResponse() {}
    
    public ExitFillTubingModeStateStreamResponse(int stateId) {
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


    public ExitFillTubingModeState getState() {
        return ExitFillTubingModeState.fromId(stateId);
    }

    public enum ExitFillTubingModeState {
        TUBING_FILLED(0),
        ;

        private final int id;
        ExitFillTubingModeState(int id) {
            this.id = id;
        }


        public int getId() {
            return id;
        }

        public static ExitFillTubingModeState fromId(int id) {
            for (ExitFillTubingModeState c : values()) {
                if (c.id == id) return c;
            }
            return null;
        }
    }
    
}
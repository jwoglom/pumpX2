package com.jwoglom.pumpx2.pump.messages.response.controlStream;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.controlStream.NonexistentExitFillTubingModeStateStreamRequest;

import java.util.Set;
import java.util.TreeSet;

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
    private boolean isPumpingStateSetAfterStartUp;
    private long stateBitmask;
    
    public ExitFillTubingModeStateStreamResponse() {}
    
    public ExitFillTubingModeStateStreamResponse(int stateId) {
        this.cargo = buildCargo(stateId);
        parse(cargo);
        
    }

    public ExitFillTubingModeStateStreamResponse(boolean isPumpingStateSetAfterStartUp, long stateBitmask) {
        this.cargo = buildPumpingCargo(isPumpingStateSetAfterStartUp, stateBitmask);
        parse(cargo);
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == 1 || raw.length == 5);
        this.cargo = raw;
        this.stateId = raw[0] & 0xFF;
        this.state = getState();
        if (raw.length == 5) {
            this.isPumpingStateSetAfterStartUp = raw[0] != 0;
            this.stateBitmask = Bytes.readUint32(raw, 1);
        } else {
            this.isPumpingStateSetAfterStartUp = false;
            this.stateBitmask = 0;
        }
        
    }

    
    public static byte[] buildCargo(int state) {
        return Bytes.combine(
            new byte[]{ (byte) state });
    }

    public static byte[] buildPumpingCargo(boolean isPumpingStateSetAfterStartUp, long stateBitmask) {
        return Bytes.combine(
            new byte[]{ (byte) (isPumpingStateSetAfterStartUp ? 1 : 0) },
            Bytes.toUint32(stateBitmask));
    }
    
    public int getStateId() {
        return stateId;
    }


    public ExitFillTubingModeState getState() {
        return ExitFillTubingModeState.fromId(stateId);
    }

    public boolean getIsPumpingStateSetAfterStartUp() {
        return isPumpingStateSetAfterStartUp;
    }

    public long getStateBitmask() {
        return stateBitmask;
    }

    public Set<PumpingState> getStates() {
        return PumpingState.fromBitmask(stateBitmask);
    }

    public enum PumpingState {
        IS_DELIVERING_THERAPY(1),
        CAN_RESUME_THERAPY(2),
        CAN_AUTO_RESUME(4),
        BOLUS_ALLOWED(8),
        TUBING_FILLED(16),
        DELIVERING_BASAL(32),
        DELIVERING_BOLUS(64),
        DELIVERING_NORMAL_BOLUS(128),
        IS_FILL_TUBING_ALLOWED(256),
        IS_BASAL_STATE(512),
        IS_PREP_CARTRIDGE_STATE(1024),
        IS_LOAD_CARTRIDGE_STATE(2048),
        IS_FILL_STATE(4096),
        IS_ESTIMATE_STATE(8192),
        CARTRIDGE_IS_INSTALLED(16384),
        CAN_SNOOZE(32768),
        ;

        private final long mask;
        PumpingState(long mask) {
            this.mask = mask;
        }
        public long mask() {
            return mask;
        }
        public static Set<PumpingState> fromBitmask(long bitmask) {
            Set<PumpingState> ret = new TreeSet<>();
            for (PumpingState s : values()) {
                if ((bitmask & s.mask()) != 0) {
                    ret.add(s);
                }
            }
            return ret;
        }
        public static int toBitmask(PumpingState...states) {
            int bitmask = 0;
            for (PumpingState s : states) {
                bitmask |= s.mask();
            }
            return bitmask;
        }
    }

    public enum ExitFillTubingModeState {
        NOT_COMPLETE(0),
        TUBING_FILLED(1),
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

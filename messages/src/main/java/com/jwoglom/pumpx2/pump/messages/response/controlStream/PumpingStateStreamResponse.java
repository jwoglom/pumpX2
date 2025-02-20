package com.jwoglom.pumpx2.pump.messages.response.controlStream;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.controlStream.NonexistentPumpingStateStreamRequest;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.BolusDeliveryHistoryLog;

import java.util.HashSet;
import java.util.Set;

@MessageProps(
    opCode=-23,
    size=5, // 29 with 24 byte padding
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL_STREAM,
    request=NonexistentPumpingStateStreamRequest.class,
    stream=true,
    signed=true
)
public class PumpingStateStreamResponse extends Message {
    
    private boolean isPumpingStateSetAfterStartUp;
    private long stateBitmask;
    
    public PumpingStateStreamResponse() {}
    
    public PumpingStateStreamResponse(boolean isPumpingStateSetAfterStartUp, long stateBitmask) {
        this.cargo = buildCargo(isPumpingStateSetAfterStartUp, stateBitmask);
        this.isPumpingStateSetAfterStartUp = isPumpingStateSetAfterStartUp;
        this.stateBitmask = stateBitmask;
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.isPumpingStateSetAfterStartUp = raw[0] != 0;
        this.stateBitmask = Bytes.readUint32(raw, 1);
    }

    
    public static byte[] buildCargo(boolean isPumpingStateSetAfterStartUp, long stateBitmask) {
        return Bytes.combine(
            new byte[]{ (byte) (isPumpingStateSetAfterStartUp ? 1 : 0) }, 
            Bytes.toUint32(stateBitmask));
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
            Set<PumpingState> ret = new HashSet<>();
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
}
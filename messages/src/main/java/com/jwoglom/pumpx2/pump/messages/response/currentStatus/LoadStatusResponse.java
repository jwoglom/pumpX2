package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.LoadStatusRequest;

@MessageProps(
    opCode=21,
    size=3,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CURRENT_STATUS,
    request=LoadStatusRequest.class
)
// NOT a StatusMessage: byte 0 is "isLoadingActive", not a command status/NACK byte.
public class LoadStatusResponse extends Message {
    private int isLoadingActiveId;
    private boolean isLoadingActive;
    private int loadStateId;
    private LoadState loadState;
    private int primeStatusId;
    private PrimeTubingStatus primeTubingStatus;
    private PrimeNudgeStatus primeNudgeStatus;

    public LoadStatusResponse() {
        this.cargo = EMPTY;
    }

    public LoadStatusResponse(byte[] raw) {
        parse(raw);
    }

    public LoadStatusResponse(boolean isLoadingActive, int loadStateId, int primeStatusId) {
        this.cargo = buildCargo(isLoadingActive, loadStateId, primeStatusId);
        parse(this.cargo);
    }

    public LoadStatusResponse(boolean isLoadingActive, LoadState loadState, int primeStatusId) {
        this.cargo = buildCargo(isLoadingActive, loadState, primeStatusId);
        parse(this.cargo);
    }

    /**
     * Legacy constructor maintained for compatibility with prior pumpx2 releases where
     * byte 0 was incorrectly treated as "status" and bytes 1/2 as an opaque short.
     */
    @Deprecated
    public LoadStatusResponse(int status, int unknown) {
        this.cargo = buildCargo(status, unknown);
        parse(this.cargo);
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.isLoadingActiveId = raw[0] & 0xFF;
        this.isLoadingActive = this.isLoadingActiveId != 0;
        this.loadStateId = raw[1] & 0xFF;
        this.loadState = LoadState.fromId(loadStateId);
        this.primeStatusId = raw[2] & 0xFF;
        this.primeTubingStatus = loadState == LoadState.PRIME_TUBING ? PrimeTubingStatus.fromId(primeStatusId) : null;
        this.primeNudgeStatus = loadState == LoadState.PRIME_NUDGE ? PrimeNudgeStatus.fromId(primeStatusId) : null;
    }

    public static byte[] buildCargo(boolean isLoadingActive, int loadStateId, int primeStatusId) {
        return new byte[]{
                (byte) (isLoadingActive ? 1 : 0),
                (byte) (loadStateId & 0xFF),
                (byte) (primeStatusId & 0xFF)
        };
    }

    public static byte[] buildCargo(boolean isLoadingActive, LoadState loadState, int primeStatusId) {
        return buildCargo(isLoadingActive, loadState.getId(), primeStatusId);
    }

    public static byte[] buildCargo(boolean isLoadingActive, LoadState loadState, PrimeTubingStatus primeTubingStatus, PrimeNudgeStatus primeNudgeStatus) {
        int primeStatusId = 0;
        if (loadState == LoadState.PRIME_TUBING && primeTubingStatus != null) {
            primeStatusId = primeTubingStatus.getId();
        } else if (loadState == LoadState.PRIME_NUDGE && primeNudgeStatus != null) {
            primeStatusId = primeNudgeStatus.getId();
        }
        return buildCargo(isLoadingActive, loadState, primeStatusId);
    }

    /**
     * Legacy helper maintained for compatibility with older code.
     * Equivalent to {@code byte[0]=status, byte[1]=unknown&0xFF, byte[2]=(unknown>>8)&0xFF}.
     */
    @Deprecated
    public static byte[] buildCargo(int status, int unknown) {
        return new byte[]{
                (byte) (status & 0xFF),
                (byte) (unknown & 0xFF),
                (byte) ((unknown >> 8) & 0xFF)
        };
    }

    public int getIsLoadingActiveId() {
        return isLoadingActiveId;
    }

    public boolean getIsLoadingActive() {
        return isLoadingActive;
    }

    public int getLoadStateId() {
        return loadStateId;
    }

    public LoadState getLoadState() {
        return loadState;
    }

    public int getPrimeStatusId() {
        return primeStatusId;
    }

    public PrimeTubingStatus getPrimeTubingStatus() {
        return primeTubingStatus;
    }

    public PrimeNudgeStatus getPrimeNudgeStatus() {
        return primeNudgeStatus;
    }

    /**
     * Matches Tandem's derived helper: true when loadState is one of
     * CHANGE_CARTRIDGE / LOAD_CARTRIDGE / PRIME_TUBING.
     */
    public boolean getIsInLoadingState() {
        return loadState == LoadState.CHANGE_CARTRIDGE
                || loadState == LoadState.LOAD_CARTRIDGE
                || loadState == LoadState.PRIME_TUBING;
    }

    /**
     * Legacy compatibility alias for older pumpx2 versions where this was interpreted
     * as a command status.
     */
    @Deprecated
    public int getStatus() {
        return getIsLoadingActiveId();
    }

    /**
     * Legacy compatibility alias for older pumpx2 versions.
     */
    @Deprecated
    public int getUnknown() {
        return (getLoadStateId() & 0xFF) | ((getPrimeStatusId() & 0xFF) << 8);
    }

    public enum LoadState {
        CHANGE_CARTRIDGE(0),
        LOAD_CARTRIDGE(1),
        PRIME_TUBING(2),
        PRIME_CANNULA(3),
        PRIME_NUDGE(4),
        INVALID(5),
        UNKNOWN(6),
        ;

        private final int id;
        LoadState(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static LoadState fromId(int id) {
            for (LoadState s : values()) {
                if (s.id == id) {
                    return s;
                }
            }
            return UNKNOWN;
        }
    }

    public enum PrimeTubingStatus {
        STOP(0),
        START(1),
        ENTERED_CAN_EXIT(2),
        ENTERED_CANNOT_EXIT(3),
        SUSPENDED(4),
        UNKNOWN(5),
        ;

        private final int id;
        PrimeTubingStatus(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static PrimeTubingStatus fromId(int id) {
            for (PrimeTubingStatus s : values()) {
                if (s.id == id) {
                    return s;
                }
            }
            return UNKNOWN;
        }
    }

    public enum PrimeNudgeStatus {
        START(0),
        COMPLETE(1),
        UNKNOWN(2),
        ;

        private final int id;
        PrimeNudgeStatus(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static PrimeNudgeStatus fromId(int id) {
            for (PrimeNudgeStatus s : values()) {
                if (s.id == id) {
                    return s;
                }
            }
            return UNKNOWN;
        }
    }
}

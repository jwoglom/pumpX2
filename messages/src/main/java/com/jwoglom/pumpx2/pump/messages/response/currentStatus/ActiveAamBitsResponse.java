package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.models.NotificationMessage;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ActiveAamBitsRequest;

import java.math.BigInteger;
import java.util.Set;
import java.util.TreeSet;

@MessageProps(
    opCode=-109,
    size=17,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CURRENT_STATUS,
    request=ActiveAamBitsRequest.class,
    minApi=KnownApiVersion.MOBI_API_V3_5
)
public class ActiveAamBitsResponse extends NotificationMessage {
    private BigInteger unacknowledgedBitmask;
    private BigInteger activeBitmask;
    private AamType aamType;
    private Set<MalfunctionBitmaskStatusResponse.MalfunctionType> unacknowledgedMalfunctions;
    private Set<MalfunctionBitmaskStatusResponse.MalfunctionType> activeMalfunctions;

    public ActiveAamBitsResponse() {
        this.cargo = EMPTY;
        this.unacknowledgedBitmask = BigInteger.ZERO;
        this.activeBitmask = BigInteger.ZERO;
        this.aamType = AamType.UNKNOWN;
        this.unacknowledgedMalfunctions = new TreeSet<>();
        this.activeMalfunctions = new TreeSet<>();
    }

    public ActiveAamBitsResponse(BigInteger unacknowledgedBitmask, BigInteger activeBitmask, AamType aamType) {
        this.cargo = buildCargo(unacknowledgedBitmask, activeBitmask, aamType);
        parse(cargo);
    }

    public ActiveAamBitsResponse(byte[] raw) {
        this.cargo = raw;
        parse(raw);
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.unacknowledgedBitmask = Bytes.readUint64(raw, 0);
        this.activeBitmask = Bytes.readUint64(raw, 8);
        this.aamType = AamType.fromId(raw[16] & 0xFF);

        if (this.aamType == AamType.MALFUNCTION) {
            this.unacknowledgedMalfunctions = MalfunctionBitmaskStatusResponse.MalfunctionType.fromBitmask(this.unacknowledgedBitmask);
            this.activeMalfunctions = MalfunctionBitmaskStatusResponse.MalfunctionType.fromBitmask(this.activeBitmask);
        } else {
            this.unacknowledgedMalfunctions = new TreeSet<>();
            this.activeMalfunctions = new TreeSet<>();
        }
    }

    public static byte[] buildCargo(BigInteger unacknowledgedBitmask, BigInteger activeBitmask, AamType aamType) {
        return Bytes.combine(
            Bytes.toUint64(unacknowledgedBitmask.longValue()),
            Bytes.toUint64(activeBitmask.longValue()),
            new byte[]{(byte) aamType.id()}
        );
    }

    public BigInteger getUnacknowledgedBitmask() {
        return unacknowledgedBitmask;
    }

    public BigInteger getActiveBitmask() {
        return activeBitmask;
    }

    public AamType getAamType() {
        return aamType;
    }

    public boolean hasActiveBit(int bit) {
        return bit >= 0 && bit < 64 && activeBitmask != null && activeBitmask.testBit(bit);
    }

    public boolean hasUnacknowledgedBit(int bit) {
        return bit >= 0 && bit < 64 && unacknowledgedBitmask != null && unacknowledgedBitmask.testBit(bit);
    }

    public Set<MalfunctionBitmaskStatusResponse.MalfunctionType> getUnacknowledgedMalfunctions() {
        return unacknowledgedMalfunctions;
    }

    public Set<MalfunctionBitmaskStatusResponse.MalfunctionType> getActiveMalfunctions() {
        return activeMalfunctions;
    }

    @Override
    public int size() {
        return activeBitmask == null ? 0 : activeBitmask.bitCount();
    }

    public enum AamType {
        REMINDER(0),
        ALERT(1),
        ALARM(2),
        CGM_ALERT(3),
        MALFUNCTION(4),
        NONE(5),
        UNKNOWN(-1)
        ;

        private final int id;

        AamType(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }

        public static AamType fromId(int id) {
            for (AamType t : values()) {
                if (t.id() == id) {
                    return t;
                }
            }
            return UNKNOWN;
        }
    }
}

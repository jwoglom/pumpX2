package com.jwoglom.pumpx2.pump.messages.response;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.PumpFeaturesRequest;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

@MessageProps(
    opCode=79,
    size=8,
    type=MessageType.RESPONSE,
    request=PumpFeaturesRequest.class
)
public class PumpFeaturesResponse extends Message {
    
    private BigInteger intMap;

    public PumpFeaturesResponse() {}

    public PumpFeaturesResponse(BigInteger intMap) {
        this.cargo = buildCargo(intMap);
        this.intMap = intMap;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.intMap = Bytes.readUint64(raw, 0);
    }

    
    public static byte[] buildCargo(BigInteger intMap) {
        return Bytes.combine(
            Bytes.toUint64(intMap.longValue())
        );
    }
    
    public BigInteger getIntMap() {
        return intMap;
    }

    public enum PumpFeatureType {
        DEXCOM_G5_SUPPORTED(1),
        DEXCOM_G6_SUPPORTED(2),
        BASAL_IQ_SUPPORTED(4),
        CONTROL_IQ_SUPPORTED(1024),
        WOMBAT_SUPPORTED(65536),
        AUTO_POP_SUPPORTED(33554432),
        BASAL_LIMIT_SUPPORTED(262144),
        BLE_PUMP_CONTROL_SUPPORTED(268435456),
        PUMP_SETTINGS_IN_IDP_GUI_SUPPORTED(536870912),

        ;

        private final long bitmask;
        PumpFeatureType(long bitmask) {
            this.bitmask = bitmask;
        }

        public BigInteger bitmask() {
            return BigInteger.valueOf(bitmask);
        }

        public String toString() {
            return name();
        }

        public static BigInteger build(PumpFeatureType ...types) {
            BigInteger ret = BigInteger.ZERO;
            for (PumpFeatureType type : types) {
                ret.setBit(type.bitmask().getLowestSetBit());
            }

            return ret;
        }
    }

    public Set<PumpFeatureType> getFeatures() {
        Set<PumpFeatureType> current = new HashSet<>();
        for (PumpFeatureType type : PumpFeatureType.values()) {
            if (!intMap.and(type.bitmask()).equals(BigInteger.ZERO)) {
                current.add(type);
            }
        }

        return current;
    }
}
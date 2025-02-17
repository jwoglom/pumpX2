package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.PumpFeaturesV1Request;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

@MessageProps(
    opCode=79,
    size=8,
    type=MessageType.RESPONSE,
    request= PumpFeaturesV1Request.class
)
public class PumpFeaturesV1Response extends PumpFeaturesAbstractResponse {
    
    private BigInteger intMap;
    private Set<PumpFeatureType> features;

    public PumpFeaturesV1Response() {}

    public PumpFeaturesV1Response(BigInteger intMap) {
        this.cargo = buildCargo(intMap);
        this.intMap = intMap;
        this.features = PumpFeatureType.fromBitmask(intMap);
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.intMap = Bytes.readUint64(raw, 0);
        this.features = PumpFeatureType.fromBitmask(intMap);
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
        DEXCOM_G5_SUPPORTED(1),                        // position: 0
        DEXCOM_G6_SUPPORTED(2),                        // position: 1
        BASAL_IQ_SUPPORTED(4),                         // position: 2
//        UNKNOWN_3(8),
//        UNKNOWN_4(16),
//        UNKNOWN_5(32),
//        UNKNOWN_6(64),
//        UNKNOWN_7(128),
//        UNKNOWN_8(256),
//        UNKNOWN_9(512),
        CONTROL_IQ_SUPPORTED(1024),                    // position: 10
//        UNKNOWN_11(2048),
//        UNKNOWN_12(4096),
//        UNKNOWN_13(8192),
//        UNKNOWN_14(16384),
//        UNKNOWN_15(32768),
        WOMBAT_SUPPORTED(65536),                       // position: 16
//        UNKNOWN_17(131072),
        BASAL_LIMIT_SUPPORTED(262144),                 // position: 18
//        UNKNOWN_19(524288),
//        UNKNOWN_20(1048576),
//        UNKNOWN_21(2097152),
//        UNKNOWN_22(4194304),
//        UNKNOWN_23(8388608),
//        UNKNOWN_24(16777216),
        AUTO_POP_SUPPORTED(33554432),                  // position: 25
//        UNKNOWN_26(67108864),
//        UNKNOWN_27(134217728),
        BLE_PUMP_CONTROL_SUPPORTED(268435456),         // position: 28
        PUMP_SETTINGS_IN_IDP_GUI_SUPPORTED(536870912), // position: 29
//        UNKNOWN_30(1073741824),

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
                ret = ret.setBit(type.bitmask().getLowestSetBit());
            }

            return ret;
        }

        public static Set<PumpFeatureType> fromBitmask(BigInteger intMap) {
            Set<PumpFeatureType> current = new HashSet<>();
            for (PumpFeatureType type : PumpFeatureType.values()) {
                if (!intMap.and(type.bitmask()).equals(BigInteger.ZERO)) {
                    current.add(type);
                }
            }

            return current;
        }
    }

    public Set<PumpFeatureType> getPrimaryFeatures() {
        return features;
    }
}
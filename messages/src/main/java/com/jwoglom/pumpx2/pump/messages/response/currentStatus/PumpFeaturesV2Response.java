package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.ApiVersionDependent;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.PumpFeaturesV2Request;

import java.math.BigInteger;
import java.util.Set;
import java.util.TreeSet;

@MessageProps(
    opCode=-95,
    size=6,
    type=MessageType.RESPONSE,
    request=PumpFeaturesV2Request.class,
    minApi=KnownApiVersion.API_V2_5
)
@ApiVersionDependent
public class PumpFeaturesV2Response extends PumpFeaturesAbstractResponse {
    
    private int status;
    private int supportedFeatureIndexId;
    private long pumpFeaturesBitmask;
    private Set<PumpFeaturesV1Response.PumpFeatureType> primaryFeatures;
    private Set<ControlIqProFeatureType> controlIqProFeatures;
    private Set<ControlFeatureType> controlFeatures;
    private Set<ControlIqFeatureType> controlIqFeatures;
    private Set<DexcomFeatureType> dexcomFeatures;
    private Set<AbbottFeatureType> abbottFeatures;
    private Set<SecondaryFeatureType> secondaryFeatures;
    
    public PumpFeaturesV2Response() {}
    
    public PumpFeaturesV2Response(int status, int supportedFeatureIndex, long pumpFeaturesBitmask) {
        this.cargo = buildCargo(status, supportedFeatureIndex, pumpFeaturesBitmask);
        this.status = status;
        this.supportedFeatureIndexId = supportedFeatureIndex;
        this.pumpFeaturesBitmask = pumpFeaturesBitmask;
        initializeFeatureSets();
        
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0];
        this.supportedFeatureIndexId = raw[1];
        this.pumpFeaturesBitmask = Bytes.readUint32(raw, 2);
        initializeFeatureSets();
        
    }

    
    public static byte[] buildCargo(int status, int supportedFeaturesIndex, long pumpFeaturesBitmask) {
        return Bytes.combine(
            new byte[]{ (byte) status }, 
            new byte[]{ (byte) supportedFeaturesIndex }, 
            Bytes.toUint32(pumpFeaturesBitmask));
    }

    /**
     * @return 0 if successful
     */
    public int getStatus() {
        return status;
    }

    public int getSupportedFeatureIndexId() {
        return supportedFeatureIndexId;
    }
    public SupportedFeatureIndex getSupportedFeatureIndex() {
        return SupportedFeatureIndex.fromId(getSupportedFeatureIndexId());
    }
    public long getPumpFeaturesBitmask() {
        return pumpFeaturesBitmask;
    }

    public Set<PumpFeaturesV1Response.PumpFeatureType> getPrimaryFeatures() {
        return primaryFeatures;
    }

    public Set<ControlIqProFeatureType> getControlIqProFeatures() {
        return controlIqProFeatures;
    }

    public Set<ControlFeatureType> getControlFeatures() {
        return controlFeatures;
    }

    public Set<ControlIqFeatureType> getControlIqFeatures() {
        return controlIqFeatures;
    }

    public Set<DexcomFeatureType> getDexcomFeatures() {
        return dexcomFeatures;
    }

    public Set<AbbottFeatureType> getAbbottFeatures() {
        return abbottFeatures;
    }

    public Set<SecondaryFeatureType> getSecondaryFeatures() {
        return secondaryFeatures;
    }

    private void initializeFeatureSets() {
        SupportedFeatureIndex featureIndex = getSupportedFeatureIndex();

        this.primaryFeatures = featureIndex == SupportedFeatureIndex.MAIN_FEATURES
                ? PumpFeaturesV1Response.PumpFeatureType.fromBitmask(BigInteger.valueOf(getPumpFeaturesBitmask()))
                : null;
        this.controlIqProFeatures = featureIndex == SupportedFeatureIndex.CONTROL_IQ_PRO_FEATURES
                ? ControlIqProFeatureType.fromBitmask(getPumpFeaturesBitmask())
                : null;
        this.controlFeatures = featureIndex == SupportedFeatureIndex.CONTROL_FEATURES
                ? ControlFeatureType.fromBitmask(getPumpFeaturesBitmask())
                : null;
        this.controlIqFeatures = featureIndex == SupportedFeatureIndex.CONTROL_IQ_FEATURES
                ? ControlIqFeatureType.fromBitmask(getPumpFeaturesBitmask())
                : null;
        this.dexcomFeatures = featureIndex == SupportedFeatureIndex.DEXCOM_FEATURES
                ? DexcomFeatureType.fromBitmask(getPumpFeaturesBitmask())
                : null;
        this.abbottFeatures = featureIndex == SupportedFeatureIndex.ABBOTT_FEATURES
                ? AbbottFeatureType.fromBitmask(getPumpFeaturesBitmask())
                : null;
        this.secondaryFeatures = featureIndex == SupportedFeatureIndex.SECONDARY_FEATURES
                ? SecondaryFeatureType.fromBitmask(getPumpFeaturesBitmask())
                : null;
    }

    public enum SupportedFeatureIndex {
        MAIN_FEATURES(0),
        CONTROL_IQ_PRO_FEATURES(1),
        CONTROL_FEATURES(2),
        CONTROL_IQ_FEATURES(3),
        DEXCOM_FEATURES(4),
        ABBOTT_FEATURES(5),
        SECONDARY_FEATURES(6),
        UNKNOWN(-1)
        ;

        private final int id;
        SupportedFeatureIndex(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }

        public static SupportedFeatureIndex fromId(int id) {
            for (SupportedFeatureIndex f : values()) {
                if (f.id() == id) {
                    return f;
                }
            }
            return UNKNOWN;
        }

    }

    public enum ControlIqProFeatureType {
        BLE_CONTROL(1),               // position: 0
        GUI_CHANGES(2),               // position: 1
        RANGE_CHANGES(4),             // position: 2
        NVM_PARAM(8),                 // position: 3
        EXTENDED_BOLUS_DURATION(16),  // position: 4
        AUTO_BOLUS(32),               // position: 5
        ;

        private final long bitmask;

        ControlIqProFeatureType(long bitmask) {
            this.bitmask = bitmask;
        }

        public long bitmask() {
            return bitmask;
        }

        public static Set<ControlIqProFeatureType> fromBitmask(long intMap) {
            Set<ControlIqProFeatureType> current = new TreeSet<>();
            for (ControlIqProFeatureType type : ControlIqProFeatureType.values()) {
                if ((intMap & type.bitmask()) != 0) {
                    current.add(type);
                }
            }
            return current;
        }
    }

    public enum ControlFeatureType {
        CHANGE_CARTRIDGE_CONTROL(1),              // position: 0
        BASAL_DELIVERY_CONTROL(2),                // position: 1
        STANDARD_BOLUS_CONTROL(4),                // position: 2
        EXTENDED_BOLUS_CONTROL(8),                // position: 3
        TEMP_RATE_CONTROL(16),                    // position: 4
        CGM_CONTROL(32),                          // position: 5
        AAM_CONTROL(64),                          // position: 6
        SHELF_MODE_CONTROL(128),                  // position: 7
        IDP_CONTROL(256),                         // position: 8
        LED_PATTERN_CONTROL(512),                 // position: 9
        UNREGISTER_CONTROL(1024),                 // position: 10
        PUMP_TIME_CONTROL(2048),                  // position: 11
        PLGS_CONTROL(4096),                       // position: 12
        TZ_CONTROL(8192),                         // position: 13
        REMINDER_SETTINGS_CONTROL(16384),         // position: 14
        ALERT_SETTINGS_CONTROL(32768),            // position: 15
        ANNUM_CONTROL(65536),                     // position: 16
        AAM_NOTIFY_ME_ON_CONTROL(131072),         // position: 17
        PUMP_GLOBAL_SETTINGS_CONTROL(262144),     // position: 18
        FACTORY_CONFIG_CONTROL(524288),           // position: 19
        SNOOZE_SETTINGS_CONTROL(1048576),         // position: 20
        UPDATED_SETTINGS_QE_CONTROL(2097152),     // position: 21
        FIND_MY_PUMP_CONTROL(4194304),            // position: 22
        BOLUS_OVERFLOW_CONTROL(8388608),          // position: 23
        PREFLIGHT_CONTROL(16777216),              // position: 24
        USER_INTERACTION_CONTROL(33554432),       // position: 25
        BLOOD_GLUCOSE_ENTRY_CONTROL(67108864),    // position: 26
        HLOG_CREATOR_CONTROL(134217728),          // position: 27
        FEATURE_MANAGEMENT(268435456),            // position: 28
        MOBI_RESEARCH_PUMP(536870912),            // position: 29
        TIPS_CONTROL_GENERIC_TEST(1073741824),    // position: 30
        ;

        private final long bitmask;

        ControlFeatureType(long bitmask) {
            this.bitmask = bitmask;
        }

        public long bitmask() {
            return bitmask;
        }

        public static Set<ControlFeatureType> fromBitmask(long intMap) {
            Set<ControlFeatureType> current = new TreeSet<>();
            for (ControlFeatureType type : ControlFeatureType.values()) {
                if ((intMap & type.bitmask()) != 0) {
                    current.add(type);
                }
            }
            return current;
        }
    }

    public enum ControlIqFeatureType {
        TIMED_EXERCISE(1),                   // position: 0
        CONTROL_IQ_VERSION_ONE_FIVE(256),    // position: 8
        TEMP_RATE_ENABLED(512),              // position: 9
        EXTENDED_BOLUS_ENABLED(2048),        // position: 11
        ;

        private final long bitmask;

        ControlIqFeatureType(long bitmask) {
            this.bitmask = bitmask;
        }

        public long bitmask() {
            return bitmask;
        }

        public static Set<ControlIqFeatureType> fromBitmask(long intMap) {
            Set<ControlIqFeatureType> current = new TreeSet<>();
            for (ControlIqFeatureType type : ControlIqFeatureType.values()) {
                if ((intMap & type.bitmask()) != 0) {
                    current.add(type);
                }
            }
            return current;
        }
    }

    public enum DexcomFeatureType {
        G5_CGM(1),                    // position: 0
        G6_CGM(2),                    // position: 1
        G7_CGM(4),                    // position: 2
        AUTO_CAL(8),                  // position: 3
        DISABLE_GRAPH_SMOOTHING(16),  // position: 4
        ;

        private final long bitmask;

        DexcomFeatureType(long bitmask) {
            this.bitmask = bitmask;
        }

        public long bitmask() {
            return bitmask;
        }

        public static Set<DexcomFeatureType> fromBitmask(long intMap) {
            Set<DexcomFeatureType> current = new TreeSet<>();
            for (DexcomFeatureType type : DexcomFeatureType.values()) {
                if ((intMap & type.bitmask()) != 0) {
                    current.add(type);
                }
            }
            return current;
        }
    }

    public enum AbbottFeatureType {
        IS_FSL2(1),  // position: 0
        ;

        private final long bitmask;

        AbbottFeatureType(long bitmask) {
            this.bitmask = bitmask;
        }

        public long bitmask() {
            return bitmask;
        }

        public static Set<AbbottFeatureType> fromBitmask(long intMap) {
            Set<AbbottFeatureType> current = new TreeSet<>();
            for (AbbottFeatureType type : AbbottFeatureType.values()) {
                if ((intMap & type.bitmask()) != 0) {
                    current.add(type);
                }
            }
            return current;
        }
    }

    public enum SecondaryFeatureType {
        BASELINE_PROFILES(1),  // position: 0
        MULTI_CGM(2),          // position: 1
        BLE_TDU(4),            // position: 2
        ;

        private final long bitmask;

        SecondaryFeatureType(long bitmask) {
            this.bitmask = bitmask;
        }

        public long bitmask() {
            return bitmask;
        }

        public static Set<SecondaryFeatureType> fromBitmask(long intMap) {
            Set<SecondaryFeatureType> current = new TreeSet<>();
            for (SecondaryFeatureType type : SecondaryFeatureType.values()) {
                if ((intMap & type.bitmask()) != 0) {
                    current.add(type);
                }
            }
            return current;
        }
    }
}

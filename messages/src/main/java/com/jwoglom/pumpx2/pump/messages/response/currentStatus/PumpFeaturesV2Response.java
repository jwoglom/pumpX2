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
    
    public PumpFeaturesV2Response() {}
    
    public PumpFeaturesV2Response(int status, int supportedFeatureIndex, long pumpFeaturesBitmask) {
        this.cargo = buildCargo(status, supportedFeatureIndex, pumpFeaturesBitmask);
        this.status = status;
        this.supportedFeatureIndexId = supportedFeatureIndex;
        this.pumpFeaturesBitmask = pumpFeaturesBitmask;
        
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0];
        this.supportedFeatureIndexId = raw[1];
        this.pumpFeaturesBitmask = Bytes.readUint32(raw, 2);
        
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
        if (getSupportedFeatureIndex() != SupportedFeatureIndex.MAIN_FEATURES) {
            return null;
        }

        return PumpFeaturesV1Response.PumpFeatureType.fromBitmask(BigInteger.valueOf(getPumpFeaturesBitmask()));
    }

    public enum SupportedFeatureIndex {
        MAIN_FEATURES(0),
        CIQ_PRO_FEATURES(1),
        CONTROL_FEATURES(3)
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
            return null;
        }

    }
}
package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.PumpFeaturesV2Request;

import java.math.BigInteger;
import java.util.Set;

@MessageProps(
    opCode=-95,
    size=6,
    type=MessageType.RESPONSE,
    request=PumpFeaturesV2Request.class
)
public class PumpFeaturesV2Response extends PumpFeaturesAbstractResponse {
    
    private int status;
    private int supportedFeatureIndex;
    private long pumpFeaturesBitmask;
    
    public PumpFeaturesV2Response() {}
    
    public PumpFeaturesV2Response(int status, int supportedFeatureIndex, long pumpFeaturesBitmask) {
        this.cargo = buildCargo(status, supportedFeatureIndex, pumpFeaturesBitmask);
        this.status = status;
        this.supportedFeatureIndex = supportedFeatureIndex;
        this.pumpFeaturesBitmask = pumpFeaturesBitmask;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0];
        this.supportedFeatureIndex = raw[1];
        this.pumpFeaturesBitmask = Bytes.readUint32(raw, 2);
        
    }

    
    public static byte[] buildCargo(int status, int supportedFeaturesIndex, long pumpFeaturesBitmask) {
        return Bytes.combine(
            new byte[]{ (byte) status }, 
            new byte[]{ (byte) supportedFeaturesIndex }, 
            Bytes.toUint32(pumpFeaturesBitmask));
    }
    
    public int getStatus() {
        return status;
    }
    public int getSupportedFeatureIndex() {
        return supportedFeatureIndex;
    }
    public SupportedFeature getSupportedFeature() {
        return SupportedFeature.fromId(getSupportedFeatureIndex());
    }
    public long getPumpFeaturesBitmask() {
        return pumpFeaturesBitmask;
    }

    public Set<PumpFeaturesV1Response.PumpFeatureType> getFeatures() {
        return PumpFeaturesV1Response.PumpFeatureType.fromBitmask(BigInteger.valueOf(getPumpFeaturesBitmask()));
    }

    public enum SupportedFeature {
        MAIN_FEATURES(0),
        CIQ_PRO_FEATURES(1),
        CONTROL_FEATURES(3)
        ;

        private final int id;
        SupportedFeature(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }

        public static SupportedFeature fromId(int id) {
            for (SupportedFeature f : values()) {
                if (f.id() == id) {
                    return f;
                }
            }
            return null;
        }

    }
}
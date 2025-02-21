package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.models.StatusMessage;

import java.util.Set;

public abstract class PumpFeaturesAbstractResponse extends Message {
    public abstract Set<PumpFeaturesV1Response.PumpFeatureType> getPrimaryFeatures();
}

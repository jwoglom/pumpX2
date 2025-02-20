package com.jwoglom.pumpx2.pump.messages.models;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum SupportedDevices {
    ALL(KnownDeviceModel.TSLIM_X2, KnownDeviceModel.MOBI),
    TSLIM_X2_ONLY(KnownDeviceModel.TSLIM_X2),
    MOBI_ONLY(KnownDeviceModel.MOBI),
    ;

    private final Set<KnownDeviceModel> models;
    SupportedDevices(KnownDeviceModel... models) {
        this.models = new HashSet<>();
        this.models.addAll(Arrays.asList(models));
    }

    public Set<KnownDeviceModel> getModels() {
        return models;
    }

    public boolean supports(KnownDeviceModel model) {
        return this.models.stream().anyMatch(m -> m == model);
    }
}

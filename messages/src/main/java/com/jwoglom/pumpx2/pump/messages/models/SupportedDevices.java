package com.jwoglom.pumpx2.pump.messages.models;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

public enum SupportedDevices {
    ALL(KnownDeviceModel.TSLIM_X2, KnownDeviceModel.MOBI),
    TSLIM_X2_ONLY(KnownDeviceModel.TSLIM_X2),
    MOBI_ONLY(KnownDeviceModel.MOBI),
    ;

    private final Set<KnownDeviceModel> models;
    SupportedDevices(KnownDeviceModel... models) {
        this.models = new TreeSet<>();
        this.models.addAll(Arrays.asList(models));
    }

    public Set<KnownDeviceModel> getModels() {
        return models;
    }

    public boolean supports(KnownDeviceModel model) {
        return this.models.stream().anyMatch(m -> m == model);
    }
}

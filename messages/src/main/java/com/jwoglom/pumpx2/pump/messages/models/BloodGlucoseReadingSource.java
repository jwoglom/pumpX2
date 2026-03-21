package com.jwoglom.pumpx2.pump.messages.models;

/**
 * The source of a blood glucose reading: entered on the pump itself or remotely via BLE.
 * Maps to byte[4] of RemoteBgEntryRequest cargo.
 */
public enum BloodGlucoseReadingSource {
    PUMP(0),
    REMOTE(1),
    UNKNOWN(2);

    private final int id;

    BloodGlucoseReadingSource(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    public static BloodGlucoseReadingSource fromId(int id) {
        for (BloodGlucoseReadingSource s : values()) {
            if (s.id == id) return s;
        }
        return UNKNOWN;
    }
}

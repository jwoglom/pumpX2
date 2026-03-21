package com.jwoglom.pumpx2.pump.messages.models;

/**
 * The type of blood glucose reading: manually entered or auto-populated from CGM.
 * Maps to byte[3] of RemoteBgEntryRequest cargo.
 */
public enum BloodGlucoseReadingType {
    MANUAL(0),
    AUTO(1),
    UNKNOWN(2);

    private final int id;

    BloodGlucoseReadingType(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    public static BloodGlucoseReadingType fromId(int id) {
        for (BloodGlucoseReadingType t : values()) {
            if (t.id == id) return t;
        }
        return UNKNOWN;
    }
}

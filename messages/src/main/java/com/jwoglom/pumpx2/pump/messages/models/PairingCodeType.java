package com.jwoglom.pumpx2.pump.messages.models;

/**
 * PairingCodeType represents different types of Tandem pairing codes:
 * LONG_16CHAR is used on t:slim X2 before firmware v7.7 (KnownApiVersion.API_V2_5)
 * SHORT_6CHAR is used on t:slim X2 on firmware v7.7 and above (KnownApiVersion.API_V3_2)
 */
public enum PairingCodeType {
    LONG_16CHAR("LONG_16CHAR"),
    SHORT_6CHAR("SHORT_6CHAR"),

    ;

    private final String label;
    PairingCodeType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

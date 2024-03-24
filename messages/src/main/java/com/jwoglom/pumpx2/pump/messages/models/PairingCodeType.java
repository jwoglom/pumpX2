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

    public String filterCharacters(String pairingCode) {
        String processed = "";
        for (Character c : pairingCode.toCharArray()) {
            if (this == LONG_16CHAR) {
                // Remove all dashes and spaces
                if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
                    processed += c;
                }
            } else if (this == SHORT_6CHAR) {
                // Remove all non-numbers and spaces
                if ((c >= '0' && c <= '9')) {
                    processed += c;
                }
            }
        }
        return processed;
    }

    public static PairingCodeType fromLabel(String label) {
        for (PairingCodeType t : values()) {
            if (t.getLabel().equals(label)) {
                return t;
            }
        }
        return null;
    }
}

package com.jwoglom.pumpx2.pump.bluetooth;

public enum PumpReadyState {
    UNKNOWN(-1),
    ON_PAD(0x10),
    PICKED_UP(0x11),
    PICKED_UP_WITH_TAP(0x12);

    private final int manufacturerStateByte;

    PumpReadyState(int manufacturerStateByte) {
        this.manufacturerStateByte = manufacturerStateByte;
    }

    public int getManufacturerStateByte() {
        return manufacturerStateByte;
    }

    public static PumpReadyState fromManufacturerStateByte(int state) {
        int normalized = state & 0xFF;
        for (PumpReadyState readyState : values()) {
            if (readyState.manufacturerStateByte == normalized) {
                return readyState;
            }
        }
        return UNKNOWN;
    }
}

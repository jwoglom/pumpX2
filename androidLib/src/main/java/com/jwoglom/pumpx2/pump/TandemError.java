package com.jwoglom.pumpx2.pump;

public enum TandemError {
    NOTIFICATION_STATE_FAILED("Changing notification state"),
    PAIRING_CANNOT_BEGIN("Pairing cannot begin because the pump has not generated a pairing code.\n\nPlease open Options > Device Settings > Bluetooth Settings > Pair Device and hit OK to display the pairing code"),
    CHARACTERISTIC_WRITE_FAILED("Writing characteristic"),
    CONNECTION_UPDATE_FAILED("Updating connection"),
    BT_CONNECTION_FAILED("Bluetooth connection failed"),
    UNEXPECTED_TRANSACTION_ID("Unexpected transaction ID")

    ;

    private final String message;
    private String extra = "";
    TandemError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getExtra() {
        return extra;
    }

    public TandemError withExtra(String extra) {
        this.extra = extra;
        return this;
    }
}

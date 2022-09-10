package com.jwoglom.pumpx2.pump;

public enum TandemError {
    NOTIFICATION_STATE_FAILED("Changing notification state"),
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
        if (extra.isEmpty()) {
            return message;
        }
        return message + ": " + extra;
    }

    public TandemError withExtra(String extra) {
        this.extra = extra;
        return this;
    }
}

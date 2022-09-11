package com.jwoglom.pumpx2.pump;

import com.google.common.base.Strings;

public enum TandemError {
    PAIRING_CANNOT_BEGIN("Pairing cannot begin because the pump has not generated a pairing code.\n\nPlease open Options > Device Settings > Bluetooth Settings > Pair Device and hit OK to display the pairing code"),

    NOTIFICATION_STATE_FAILED("Changing notification state"),
    CHARACTERISTIC_WRITE_FAILED("Writing characteristic"),
    CONNECTION_UPDATE_FAILED("Updating connection"),
    BT_CONNECTION_FAILED("Bluetooth connection failed"),
    UNEXPECTED_TRANSACTION_ID("Unexpected transaction ID")
    ;

    private String message;
    private String messageSuffix = "";
    private String extra = "";
    TandemError(String message) {
        this.message = message;
        this.messageSuffix = "";
        this.extra = "";
    }

    public String getMessage() {
        return message + messageSuffix;
    }

    public String getExtra() {
        return extra;
    }

    public TandemError withCause(TandemError reason) {
        this.messageSuffix = " (cause: " + reason.name() + ")";
        this.extra = "cause: " + reason.extra;
        return this;
    }

    public TandemError withExtra(String extra) {
        this.extra = extra;
        return this;
    }
}

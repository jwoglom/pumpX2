package com.jwoglom.pumpx2.pump;

public enum TandemError {
    PAIRING_CANNOT_BEGIN("Pairing cannot begin because the pump has not generated a pairing code.\n\nPlease open Options > Device Settings > Bluetooth Settings > Pair Device and hit OK to display the pairing code"),
    SHARING_CONNECTION_WITH_TCONNECT_APP("The t:connect app is open and currently connected to the pump. Please close it:\n\nLong-press the t:connect app, and hit App Info, Force Stop"),

    NOTIFICATION_STATE_FAILED("Changing notification state"),
    CHARACTERISTIC_WRITE_FAILED("Writing characteristic"),
    CONNECTION_UPDATE_FAILED("Updating connection"),
    BT_CONNECTION_FAILED("Bluetooth connection failed"),
    UNEXPECTED_TRANSACTION_ID("Unexpected transaction ID"),
    SET_MTU_FAILED("Set MTU"),
    ERROR_RESPONSE("Error response from pump"),
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

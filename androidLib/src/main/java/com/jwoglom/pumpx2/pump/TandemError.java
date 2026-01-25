package com.jwoglom.pumpx2.pump;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.bluetooth.models.PumpResponseMessage;
import com.jwoglom.pumpx2.pump.messages.response.ErrorResponse;

public enum TandemError {
    PAIRING_CANNOT_BEGIN("Pairing cannot begin because the pump has not generated a pairing code.\n\nPlease open Options > Device Settings > Bluetooth Settings > Pair Device and hit OK to display the pairing code"),
    SHARING_CONNECTION_WITH_TCONNECT_APP("The t:connect app is open and currently connected to the pump. Please close it:\n\nLong-press the t:connect app, and hit App Info, Force Stop"),

    NOTIFICATION_STATE_FAILED("Changing notification state"),
    CHARACTERISTIC_WRITE_FAILED("Writing characteristic"),
    CONNECTION_UPDATE_FAILED("Updating connection"),
    BT_CONNECTION_FAILED("Bluetooth connection failed"),
    UNEXPECTED_TRANSACTION_ID("Unexpected transaction ID"),
    UNEXPECTED_OPCODE_REPLY("Unexpected opcode reply"),
    SET_MTU_FAILED("Set MTU"),
    ERROR_RESPONSE("Error response from pump"),
    INVALID_SIGNED_HMAC_SIGNATURE("Invalid signed HMAC signature"),
    ;

    private String message;
    private String messageSuffix = "";
    private String extra = "";
    private ErrorResponse errorResponse = null;
    private Message initiatingMessage = null;
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

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }

    public Message getInitiatingMessage() {
        return initiatingMessage;
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

    public TandemError withErrorResponse(ErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
        return this;
    }

    public TandemError withInitiatingMessage(Message initiatingMessage) {
        this.initiatingMessage = initiatingMessage;
        return this;
    }

    public boolean equals(TandemError other) {
        return this.name().equals(other.name());
    }
}

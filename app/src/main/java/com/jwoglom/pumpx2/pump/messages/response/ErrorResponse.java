package com.jwoglom.pumpx2.pump.messages.response;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.UndefinedRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * With an ErorrCode of INVALID_PARAMETER, requestCodeId contains the request opcode which failed
 */
@MessageProps(
    opCode=77,
    size=2,
    type=MessageType.RESPONSE,
    request=UndefinedRequest.class
)
public class ErrorResponse extends Message {
    private int requestCodeId;
    private int errorCodeId;
    private ErrorCode errorCode;

    public ErrorResponse() {}

    public ErrorResponse(int requestCodeId, int errorCodeId) {
        parse(buildCargo(requestCodeId, errorCodeId));
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        requestCodeId = raw[0];
        errorCodeId = raw[1];
        errorCode = ErrorCode.fromByte(errorCodeId);
        cargo = raw;
    }

    public static byte[] buildCargo(int unknownByte0, int errorCodeId) {
        return Bytes.combine(
                new byte[]{ (byte) unknownByte0 },
                new byte[]{ (byte) errorCodeId });
    }

    public int getRequestCodeId() {
        return requestCodeId;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public enum ErrorCode {
        UNDEFINED_ERROR(0),
        CRC_MISMATCH(1),
        TRANSACTION_ID_MISMATCH(3),
        BAD_CARGO_LENGTH(4),
        BAD_OPCODE(6),
        INVALID_REQUIRED_PARAMETER(7),
        MESSAGE_BUFFER_FULL(8),
        INVALID_AUTHENTICATION_ERROR(9)
        ;

        private static final Map<Integer, ErrorCode> idMap = new HashMap<>();
        static {
            for (ErrorCode item : values()) {
                idMap.put(item.id(), item);
            }
        }

        ErrorCode(int i) {
            this.id = i;
        }
        private final int id;

        public int id() {
            return id;
        }

        public static ErrorCode fromByte(int id) {
            return idMap.get(id);
        }
    }
}

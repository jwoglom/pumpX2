package com.jwoglom.pumpx2.pump.messages.response.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.StatusMessage;
import com.jwoglom.pumpx2.pump.messages.request.control.SendTipsControlGenericTestRequest;

@MessageProps(
    opCode=119,
    size=1,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    request=SendTipsControlGenericTestRequest.class,
    signed=true
)
public class SendTipsControlGenericTestResponse extends StatusMessage {

    private int status;

    public SendTipsControlGenericTestResponse() {}

    public SendTipsControlGenericTestResponse(int status) {
        this.cargo = buildCargo(status);
        this.status = status;
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0] & 0xFF;
    }

    public static byte[] buildCargo(int status) {
        return new byte[]{(byte) status};
    }

    @Override
    public int getStatus() {
        return status;
    }

    public enum StatusCode {
        ACK(0),
        INVALID_TEST_FEATURE_ID(1),
        INVALID_FEATURE_COMMAND(2),
        UNKNOWN(255),
        ;

        private final int code;

        StatusCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static StatusCode fromCode(int code) {
            for (StatusCode s : values()) {
                if (s.code == code) return s;
            }
            return UNKNOWN;
        }
    }
}

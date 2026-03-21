package com.jwoglom.pumpx2.pump.messages.request.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.SendTipsControlGenericTestResponse;

@MessageProps(
    opCode=118,
    size=24,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    response=SendTipsControlGenericTestResponse.class,
    signed=true
)
public class SendTipsControlGenericTestRequest extends Message {
    private long param1;
    private long param2;
    private long param3;
    private long param4;
    private long param5;
    private long param6;

    public SendTipsControlGenericTestRequest() {}

    public SendTipsControlGenericTestRequest(long param1, long param2, long param3, long param4, long param5, long param6) {
        this.cargo = buildCargo(param1, param2, param3, param4, param5, param6);
        this.param1 = param1;
        this.param2 = param2;
        this.param3 = param3;
        this.param4 = param4;
        this.param5 = param5;
        this.param6 = param6;
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.param1 = Bytes.readUint32(raw, 0);
        this.param2 = Bytes.readUint32(raw, 4);
        this.param3 = Bytes.readUint32(raw, 8);
        this.param4 = Bytes.readUint32(raw, 12);
        this.param5 = Bytes.readUint32(raw, 16);
        this.param6 = Bytes.readUint32(raw, 20);
    }

    public static byte[] buildCargo(long param1, long param2, long param3, long param4, long param5, long param6) {
        return Bytes.combine(
            Bytes.toUint32(param1),
            Bytes.toUint32(param2),
            Bytes.toUint32(param3),
            Bytes.toUint32(param4),
            Bytes.toUint32(param5),
            Bytes.toUint32(param6)
        );
    }

    public long getParam1() {
        return param1;
    }

    public long getParam2() {
        return param2;
    }

    public long getParam3() {
        return param3;
    }

    public long getParam4() {
        return param4;
    }

    public long getParam5() {
        return param5;
    }

    public long getParam6() {
        return param6;
    }
}

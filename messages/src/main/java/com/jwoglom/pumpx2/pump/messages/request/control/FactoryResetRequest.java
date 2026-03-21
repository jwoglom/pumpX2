package com.jwoglom.pumpx2.pump.messages.request.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.FactoryResetResponse;

@MessageProps(
    opCode=-24,
    size=8,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    response=FactoryResetResponse.class,
    signed=true
)
public class FactoryResetRequest extends Message {
    private long key;
    private long serialNumber;

    public FactoryResetRequest() {}

    public FactoryResetRequest(long key, long serialNumber) {
        this.cargo = buildCargo(key, serialNumber);
        this.key = key;
        this.serialNumber = serialNumber;
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.key = Bytes.readUint32(raw, 0);
        this.serialNumber = Bytes.readUint32(raw, 4);
    }

    public static byte[] buildCargo(long key, long serialNumber) {
        return Bytes.combine(
                Bytes.toUint32(key),
                Bytes.toUint32(serialNumber)
        );
    }

    public long getKey() {
        return key;
    }

    public long getSerialNumber() {
        return serialNumber;
    }
}

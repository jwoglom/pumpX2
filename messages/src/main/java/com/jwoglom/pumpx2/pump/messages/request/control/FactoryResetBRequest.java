package com.jwoglom.pumpx2.pump.messages.request.control;

import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.FactoryResetBResponse;

@MessageProps(
    opCode=124,
    size=9,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    response=FactoryResetBResponse.class,
    signed=true
)
public class FactoryResetBRequest extends Message {
    private long key;
    private long serialNumber;
    private boolean enableShelfMode;

    public FactoryResetBRequest() {}

    public FactoryResetBRequest(long key, long serialNumber, boolean enableShelfMode) {
        this.cargo = buildCargo(key, serialNumber, enableShelfMode);
        this.key = key;
        this.serialNumber = serialNumber;
        this.enableShelfMode = enableShelfMode;
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        this.cargo = raw;
        this.key = Bytes.readUint32(raw, 0);
        this.serialNumber = Bytes.readUint32(raw, 4);
        this.enableShelfMode = raw[8] != 0;
    }

    public static byte[] buildCargo(long key, long serialNumber, boolean enableShelfMode) {
        return Bytes.combine(
                Bytes.toUint32(key),
                Bytes.toUint32(serialNumber),
                new byte[]{(byte) (enableShelfMode ? 1 : 0)}
        );
    }

    public long getKey() {
        return key;
    }

    public long getSerialNumber() {
        return serialNumber;
    }

    public boolean getEnableShelfMode() {
        return enableShelfMode;
    }
}

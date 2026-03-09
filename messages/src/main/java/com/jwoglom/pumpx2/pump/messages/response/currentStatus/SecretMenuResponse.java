package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.SecretMenuRequest;

@MessageProps(
    opCode=-67,
    size=8,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CURRENT_STATUS,
    request=SecretMenuRequest.class
)
public class SecretMenuResponse extends Message {
    private long timeOfLastConnectionTimestampSeconds;
    private long reservedValue;

    public SecretMenuResponse() {}

    public SecretMenuResponse(long timeOfLastConnectionTimestampSeconds, long reservedValue) {
        this.cargo = buildCargo(timeOfLastConnectionTimestampSeconds, reservedValue);
        this.timeOfLastConnectionTimestampSeconds = timeOfLastConnectionTimestampSeconds;
        this.reservedValue = reservedValue;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.timeOfLastConnectionTimestampSeconds = Bytes.readUint32(raw, 0);
        this.reservedValue = Bytes.readUint32(raw, 4);
    }

    public static byte[] buildCargo(long timeOfLastConnectionTimestampSeconds, long reservedValue) {
        return Bytes.combine(
            Bytes.toUint32(timeOfLastConnectionTimestampSeconds),
            Bytes.toUint32(reservedValue));
    }

    public long getTimeOfLastConnectionTimestampSeconds() {
        return timeOfLastConnectionTimestampSeconds;
    }

    public long getReservedValue() {
        return reservedValue;
    }
}

package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.StreamDataReadinessResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.StreamDataReadinessResponse.StreamDataType;

@MessageProps(
    opCode=-58,
    size=1,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CURRENT_STATUS,
    response=StreamDataReadinessResponse.class
)
public class StreamDataReadinessRequest extends Message {
    private int streamDataTypeId;
    private StreamDataType streamDataType;

    public StreamDataReadinessRequest() {
        this(StreamDataType.FREESTYLE_LIBRE_2_SENSOR_CONTEXT);
    }

    public StreamDataReadinessRequest(int streamDataTypeId) {
        this.cargo = buildCargo(streamDataTypeId);
        this.streamDataTypeId = streamDataTypeId;
        this.streamDataType = StreamDataType.fromOrdinal(streamDataTypeId);
    }

    public StreamDataReadinessRequest(StreamDataType streamDataType) {
        this(streamDataType.ordinalValue());
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.streamDataTypeId = raw[0] & 0xFF;
        this.streamDataType = StreamDataType.fromOrdinal(streamDataTypeId);
    }

    public static byte[] buildCargo(int streamDataType) {
        return new byte[]{ (byte) streamDataType };
    }

    public int getStreamDataTypeId() {
        return streamDataTypeId;
    }

    public StreamDataType getStreamDataType() {
        return streamDataType;
    }
}

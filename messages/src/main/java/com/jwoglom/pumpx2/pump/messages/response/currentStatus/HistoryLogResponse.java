package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.models.StatusMessage;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.HistoryLogRequest;

@MessageProps(
    opCode=61,
    size=2,
    type=MessageType.RESPONSE,
    request=HistoryLogRequest.class
)
public class HistoryLogResponse extends StatusMessage {
    
    private int status;
    private int streamId;
    
    public HistoryLogResponse() {}
    
    public HistoryLogResponse(int status, int streamId) {
        this.cargo = buildCargo(status, streamId);
        this.status = status;
        this.streamId = streamId;
        
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0];
        this.streamId = raw[1];
        
    }

    
    public static byte[] buildCargo(int status, int streamId) {
        return Bytes.combine(
            new byte[]{ (byte) status }, 
            new byte[]{ (byte) streamId });
    }

    /**
     * @return 0 if successful
     */
    public int getStatus() {
        return status;
    }
    public int getStreamId() {
        return streamId;
    }
    
}
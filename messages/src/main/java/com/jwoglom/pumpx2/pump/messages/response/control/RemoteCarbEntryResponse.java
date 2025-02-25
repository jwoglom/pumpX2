package com.jwoglom.pumpx2.pump.messages.response.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.models.StatusMessage;
import com.jwoglom.pumpx2.pump.messages.request.control.RemoteCarbEntryRequest;

@MessageProps(
    opCode=-13,
    size=1,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    request= RemoteCarbEntryRequest.class,
    signed=true,
    minApi=KnownApiVersion.API_V2_5
)
public class RemoteCarbEntryResponse extends StatusMessage {
    private int status;
    
    public RemoteCarbEntryResponse() {}

    public RemoteCarbEntryResponse(int status) {
        this.cargo = buildCargo(status);
        this.status = status;
    }

    public static byte[] buildCargo(int status) {
        return Bytes.combine(
                new byte[]{(byte) status}
        );
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size(), "size " + raw.length);
        this.cargo = raw;
        this.status = raw[0];
    }

    /**
     * @return 0 if successful
     */
    public int getStatus() {
        return status;
    }
}
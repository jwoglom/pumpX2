package com.jwoglom.pumpx2.pump.messages.request.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.response.control.SetG6TransmitterIdResponse;

@MessageProps(
    opCode=-80,
    size=16,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    signed=true,
    response=SetG6TransmitterIdResponse.class,
    minApi=KnownApiVersion.MOBI_API_V3_5
)
public class SetG6TransmitterIdRequest extends Message {
    public static final int TXID_LENGTH = 6;

    private String txId;

    public SetG6TransmitterIdRequest() {
        this.cargo = EMPTY;
    }

    public SetG6TransmitterIdRequest(byte[] raw) {
        this.cargo = raw;
        parse(raw);
    }

    public SetG6TransmitterIdRequest(String txId) {
        this.cargo = buildCargo(txId);
        this.txId = txId;
    }

    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.txId = Bytes.readString(raw, 0, TXID_LENGTH);
    }


    public static byte[] buildCargo(String txId) {
        return Bytes.combine(
                Bytes.writeString(txId, 6),
                new byte[10]
        );
    }

    public String getTxId() {
        return txId;
    }
}
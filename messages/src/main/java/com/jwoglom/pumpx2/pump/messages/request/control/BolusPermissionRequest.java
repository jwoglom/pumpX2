package com.jwoglom.pumpx2.pump.messages.request.control;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.response.control.BolusPermissionResponse;

import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;

@MessageProps(
    opCode=-94,
    size=0,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    response=BolusPermissionResponse.class,
    minApi=KnownApiVersion.API_V2_5,
    signed=true
)
public class BolusPermissionRequest extends Message {
    public BolusPermissionRequest() {
        this.cargo = new byte[]{};
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
    }
}
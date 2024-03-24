package com.jwoglom.pumpx2.pump.messages.response.authentication;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.request.authentication.FourthChallengeV2Request;
import com.jwoglom.pumpx2.shared.Hex;

import java.util.Arrays;

@MessageProps(
    opCode=39,
    size=18,
    type=MessageType.RESPONSE,
    minApi=KnownApiVersion.API_V3_2,
    characteristic=Characteristic.AUTHORIZATION,
    request=FourthChallengeV2Request.class
)
public class FourthChallengeV2Response extends Message {
    private byte[] unknownField1;

    public FourthChallengeV2Response() {}

    public FourthChallengeV2Response(byte[] unknownField1) {
        parse(buildCargo(unknownField1));
        Preconditions.checkState(Hex.encodeHexString(this.unknownField1).equals(Hex.encodeHexString(unknownField1)));
    }

    //public FourthChallengeV2Response(byte[] raw) {
    //    parse(raw);
    //}

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.unknownField1 = raw;
    }

    public static byte[] buildCargo(byte[] unknownField1) {
        return Bytes.combine(
                unknownField1
        );
    }

    public byte[] getUnknownField1() {
        return unknownField1;
    }
}

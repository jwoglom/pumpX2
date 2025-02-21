package com.jwoglom.pumpx2.pump.messages.response.authentication;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.request.authentication.Jpake2Request;

import java.util.Arrays;

@MessageProps(
    opCode=37,
    size=170, // 3 bytes longer than second
    type=MessageType.RESPONSE,
    minApi=KnownApiVersion.API_V3_2,
    characteristic=Characteristic.AUTHORIZATION,
    request= Jpake2Request.class
)
public class Jpake2Response extends Message {
    private int appInstanceId;
    private byte[] centralChallengeHash;

    public Jpake2Response() {}

    public Jpake2Response(int appInstanceId, byte[] centralChallengeHash) {
        parse(buildCargo(appInstanceId, centralChallengeHash));
        Validate.isTrue(this.appInstanceId == appInstanceId);
        Validate.isTrue(Arrays.equals(this.centralChallengeHash, centralChallengeHash));
    }

    public Jpake2Response(byte[] raw) {
        parse(raw);
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        appInstanceId = Bytes.readShort(raw, 0);
        centralChallengeHash = Arrays.copyOfRange(raw, 2, 170); // 168
    }

    public static byte[] buildCargo(int byte0short, byte[] bytes2to170) {
        return Bytes.combine(Bytes.firstTwoBytesLittleEndian(byte0short), bytes2to170);
    }

    public int getAppInstanceId() {
        return appInstanceId;
    }

    public byte[] getCentralChallengeHash() {
        return centralChallengeHash;
    }
}

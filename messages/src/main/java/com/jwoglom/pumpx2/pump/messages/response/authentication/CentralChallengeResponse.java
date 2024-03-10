package com.jwoglom.pumpx2.pump.messages.response.authentication;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.request.authentication.CentralChallengeRequest;

import java.util.Arrays;

@MessageProps(
    opCode=17,
    size=30,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.AUTHORIZATION,
    request=CentralChallengeRequest.class
)
public class CentralChallengeResponse extends AbstractCentralChallengeResponse {
    private int appInstanceId;
    private byte[] centralChallengeHash;
    private byte[] hmacKey;

    public CentralChallengeResponse() {}

    public CentralChallengeResponse(int appInstanceId, byte[] centralChallengeHash, byte[] hmacKey) {
        parse(buildCargo(appInstanceId, centralChallengeHash, hmacKey));
        Preconditions.checkState(this.appInstanceId == appInstanceId);
        Preconditions.checkState(Arrays.equals(this.centralChallengeHash, centralChallengeHash));
        Preconditions.checkState(Arrays.equals(this.hmacKey, hmacKey));
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        appInstanceId = Bytes.readShort(raw, 0);
        centralChallengeHash = Arrays.copyOfRange(raw, 2, 22); // len=10 == Request.centralChallenge
        hmacKey = Arrays.copyOfRange(raw, 22, 30); // len=8
    }

    public static byte[] buildCargo(int byte0short, byte[] bytes2to22, byte[] bytes22to30) {
        return Bytes.combine(Bytes.firstTwoBytesLittleEndian(byte0short), Bytes.combine(bytes2to22, bytes22to30));
    }

    public int getAppInstanceId() {
        return appInstanceId;
    }

    public byte[] getCentralChallengeHash() {
        return centralChallengeHash;
    }

    public byte[] getHmacKey() {
        return hmacKey;
    }
}

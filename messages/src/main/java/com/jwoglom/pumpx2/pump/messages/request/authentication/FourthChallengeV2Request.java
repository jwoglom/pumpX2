package com.jwoglom.pumpx2.pump.messages.request.authentication;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.response.authentication.FourthChallengeV2Response;

import java.util.Arrays;

/**
 * The fourth authorization message sent to the pump with V2 authorization style which returns success??
 *
 * When the Android libary is used, this message is invoked automatically by PumpX2 on Bluetooth
 * connection initialization as part of performing the initial pump pairing process.
 */
@MessageProps(
    opCode=38,
    size=2,
    type=MessageType.REQUEST,
    characteristic=Characteristic.AUTHORIZATION,
    response=FourthChallengeV2Response.class
)
public class FourthChallengeV2Request extends Message {
    private int challengeParam;

    public FourthChallengeV2Request() {}

    public FourthChallengeV2Request(int challengeParam) {
        this.cargo = buildCargo(challengeParam);
        this.challengeParam = challengeParam;
    }

    public FourthChallengeV2Request(byte[] rawCargo) {
        parse(rawCargo);
    }

    public int getChallengeParam() {
        return challengeParam;
    }

    private static byte[] buildCargo(int appInstanceId) {
        byte[] cargo = new byte[2];
        System.arraycopy(Bytes.firstTwoBytesLittleEndian(appInstanceId), 0, cargo, 0, 2);

        return cargo;
    }

    public void parse(byte[] raw) {
        this.cargo = raw;
        this.challengeParam = Bytes.readShort(Arrays.copyOfRange(raw, 0, 2), 0);
    }
}

package com.jwoglom.pumpx2.pump.messages.request.authentication;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.response.authentication.PumpChallengeResponse;

import java.util.Arrays;

import kotlin.collections.ArraysKt;

/**
 * The second authorization message sent to the pump which contains the HMACed pairing code.
 *
 * When the Android libary is used, this message is invoked automatically by PumpX2 on Bluetooth
 * connection initialization as part of performing the initial pump pairing process.
 */
@MessageProps(
    opCode=18,
    size=22,
    type=MessageType.REQUEST,
    characteristic=Characteristic.AUTHORIZATION,
    response=PumpChallengeResponse.class
)
public class PumpChallengeRequest extends Message {
    private int appInstanceId;
    private byte[] pumpChallengeHash;

    public PumpChallengeRequest() {}

    public PumpChallengeRequest(int appInstanceId, byte[] pumpChallengeHash) {
        parse(buildCargo(appInstanceId, pumpChallengeHash));
        Validate.isTrue(this.appInstanceId == appInstanceId);
        Validate.isTrue(Arrays.equals(this.pumpChallengeHash, pumpChallengeHash));
    }

    public int getAppInstanceId() {
        return appInstanceId;
    }

    public byte[] getPumpChallengeHash() {
        return pumpChallengeHash;
    }

    private static byte[] buildCargo(int appInstanceId, byte[] pumpChallengeHash) {
        byte[] cargo = new byte[22];
        System.arraycopy(Bytes.combine(Bytes.firstTwoBytesLittleEndian(appInstanceId), pumpChallengeHash), 0, cargo, 0, 22);

        return cargo;
    }

    public void parse(byte[] raw) {
        this.cargo = raw;
        this.appInstanceId = Bytes.readShort(Arrays.copyOfRange(raw, 0, 2), 0);
        this.pumpChallengeHash = Arrays.copyOfRange(raw, 2, raw.length);
    }
}

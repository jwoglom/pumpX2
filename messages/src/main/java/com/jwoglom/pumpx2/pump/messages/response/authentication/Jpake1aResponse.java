package com.jwoglom.pumpx2.pump.messages.response.authentication;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.request.authentication.Jpake1aRequest;

import java.util.Arrays;

@MessageProps(
    opCode=33, // or 35?
    size=167,
    type=MessageType.RESPONSE,
    minApi=KnownApiVersion.API_V3_2,
    characteristic=Characteristic.AUTHORIZATION,
    request= Jpake1aRequest.class
)
public class Jpake1aResponse extends AbstractCentralChallengeResponse {
    private int appInstanceId;
    private byte[] centralChallengeHash;

    public Jpake1aResponse() {}

    public Jpake1aResponse(int appInstanceId, byte[] centralChallengeHash) {
        parse(buildCargo(appInstanceId, centralChallengeHash));
        Validate.isTrue(this.appInstanceId == appInstanceId);
        // this.centralChallengeHash is zero-padded to the fixed cargo size by parse(), so a
        // minimal-length (short) round from the peer is compared against its padded form.
        Validate.isTrue(Arrays.equals(this.centralChallengeHash,
                Arrays.copyOf(centralChallengeHash, this.centralChallengeHash.length)));
    }

    public Jpake1aResponse(byte[] raw) {
        parse(raw);
    }

    public void parse(byte[] raw) {
        // A peer's zero-knowledge-proof scalar is encoded with its minimal length, so a
        // legitimate JPAKE round can be a byte or two shorter than the fixed-size cargo the pump
        // frames it in (about 1 time in 256 per scalar). Accept a short cargo and zero-pad it back
        // to the declared size instead of throwing: every field inside the round is
        // length-prefixed, so EcJpake reads it correctly and ignores the padding. A cargo longer
        // than the declared size is still rejected.
        Validate.isTrue(raw.length <= props().size(), "size is "+raw.length+" not "+props().size());
        this.cargo = Arrays.copyOf(raw, props().size());
        appInstanceId = Bytes.readShort(this.cargo, 0);
        centralChallengeHash = Arrays.copyOfRange(this.cargo, 2, 167); // 165 == Request.centralChallenge.length
    }

    public static byte[] buildCargo(int byte0short, byte[] bytes2to167) {
        return Bytes.combine(Bytes.firstTwoBytesLittleEndian(byte0short), bytes2to167);
    }

    public int getAppInstanceId() {
        return appInstanceId;
    }

    public byte[] getCentralChallengeHash() {
        return centralChallengeHash;
    }
}

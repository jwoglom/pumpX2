package com.jwoglom.pumpx2.pump.messages.builders.jpake;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.Packetize;
import com.jwoglom.pumpx2.pump.messages.bluetooth.BTResponseParser;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.bluetooth.TronMessageWrapper;
import com.jwoglom.pumpx2.pump.messages.bluetooth.models.Packet;
import com.jwoglom.pumpx2.pump.messages.bluetooth.models.PumpResponseMessage;
import com.jwoglom.pumpx2.pump.messages.builders.JpakeAuthBuilder;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.request.authentication.Jpake2Request;
import com.jwoglom.pumpx2.pump.messages.response.authentication.Jpake1aResponse;
import com.jwoglom.pumpx2.pump.messages.response.authentication.Jpake1bResponse;
import com.jwoglom.pumpx2.pump.messages.response.authentication.Jpake2Response;
import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import io.particle.crypto.EcJpake;

/**
 * Regression tests for the ~1-in-256 case where a JPAKE zero-knowledge-proof scalar r is smaller
 * than 2^248, so its minimal-length encoding is 31 bytes instead of the curve's 32.
 *
 * The SecureRandomMock byte streams below are not arbitrary: they were found by search as inputs
 * which deterministically hit that case, so these tests exercise the short-scalar path on every
 * run without sampling thousands of random handshakes.
 */
public class JpakeShortScalarTest {
    private static final byte[] PAIRING_CODE = JpakeAuthBuilder.pairingCodeToBytes("738006");

    /** Produces a client round 1 whose second ZKP scalar has a zero most significant byte. */
    private static final String SHORT_ROUND1_MOCK =
            "21e1cdc336a5ebe5a3ad88ad9b963bdff01c691269001f5502cc784a8ec5ad5d" +
            "65c46bed3636c196b5e12735b98ed93f96b93576a5faaa3d5331f665c85c31ad" +
            "005b4142dd564185a8b62e8b742663a76bc1350b73465df1d2bbbc2448787ac7" +
            "7e955aac1037b87b40bdb307bb1cd10f0d8b1825898f11f299a6f0c21e0001b1";

    /** Paired with SHORT_ROUND2_SERVER_MOCK, produces a server round 2 with a short ZKP scalar. */
    private static final String SHORT_ROUND2_CLIENT_MOCK =
            "515e467783b1c2809a924d0407e3be4b0111672db3c50902c4199dfc41ddcdd8" +
            "93d39b467e1118e36b05c78929b8279bbfa2635420492c295386261eeeff6bd6" +
            "3ea1fc2b2b14a1fc19ae1f8b728132423fb1485d23cabce20ef61af9dd2f5b67" +
            "fc6ceb3f5b40ae88d026e879336e752cea6f433627f2208937e8e21bf1646843" +
            "4f605fdc5fa4717d340f62a8180da40a" +
            "33f2683a9dd28177860316f34f131d19ea4c120a247339c96f03a15a5098d15d" +
            "04a2fda1f032c4f25fd9b40072ade0ea";

    private static final String SHORT_ROUND2_SERVER_MOCK =
            "22833d406f0f0da3aec2b19dd1dca0aee046b2eaf4b0b58b70f61d939641bbc9" +
            "20df12ce64acb000381d31ce31ed8446b4fde6aa00de616b5ee6f93b40e01809" +
            "5e7dd835682ccdf3e093e4c760755a28f78d64c344362379c5caa7139cd80ea3" +
            "38033042361f1a7052cf4a7dadf706f421614ae43049f9d889bb2080db1916f0" +
            "987f4daaa963489102674ef6545155d3" +
            "a44565da793488802702070c09490b27d0cbcfaec77e28633a66d8e87c000076" +
            "20973d8a22096d9cc7accc8f34691d61";

    /**
     * Returns the offset of the length byte of the ZKP scalar r within the ECJPAKEKeyKP structure
     * (ECPoint X, ECPoint V, uint8 rLen, r) which starts at kkpOffset.
     */
    private static int scalarLenOffset(byte[] blob, int kkpOffset) {
        int p = kkpOffset;
        p += 1 + (blob[p] & 0xff); // X
        p += 1 + (blob[p] & 0xff); // V
        return p;
    }

    /** Asserts that the scalar at the given KKP offset is full length with a zero leading byte. */
    private static void assertPaddedShortScalar(byte[] blob, int kkpOffset) {
        int lenOffset = scalarLenOffset(blob, kkpOffset);
        assertEquals("scalar should be written at the full 32-byte curve scalar length",
                32, blob[lenOffset] & 0xff);
        assertEquals("this fixture is only meaningful if the scalar really is short",
                0, blob[lenOffset + 1] & 0xff);
    }

    /**
     * Re-encodes the ZKP scalar at the given KKP offset with its minimal length, i.e. what a peer
     * implementing mbedTLS's ecjpake_zkp_write (which writes mbedtls_mpi_size(&r) bytes) sends.
     */
    private static byte[] toMinimalScalarEncoding(byte[] blob, int kkpOffset) {
        int lenOffset = scalarLenOffset(blob, kkpOffset);
        int len = blob[lenOffset] & 0xff;
        int start = lenOffset + 1;
        int strip = 0;
        while (strip < len - 1 && blob[start + strip] == 0) {
            strip++;
        }
        byte[] out = new byte[blob.length - strip];
        System.arraycopy(blob, 0, out, 0, lenOffset);
        out[lenOffset] = (byte) (len - strip);
        System.arraycopy(blob, start + strip, out, start, blob.length - start - strip);
        return out;
    }

    @Test
    public void clientRound1WithShortScalarIsFullLength() throws DecoderException {
        EcJpake cli = new EcJpake(EcJpake.Role.CLIENT, PAIRING_CODE,
                new SecureRandomMock(Hex.decodeHex(SHORT_ROUND1_MOCK)));
        byte[] round1 = cli.getRound1();

        assertEquals("round 1 must always be exactly 330 bytes so the 165+165 split is exact",
                330, round1.length);
        int secondKkp = scalarLenOffset(round1, 0) + 1 + (round1[scalarLenOffset(round1, 0)] & 0xff);
        assertPaddedShortScalar(round1, secondKkp);

        // A peer reads the whole blob with no trailing bytes left over.
        EcJpake srv = new EcJpake(EcJpake.Role.SERVER, PAIRING_CODE);
        assertEquals(330, srv.readRound1(round1));
    }

    @Test
    public void serverRound2WithShortScalarIsFullLengthAndDerivesMatchingSecret() throws DecoderException {
        EcJpake cli = new EcJpake(EcJpake.Role.CLIENT, PAIRING_CODE,
                new SecureRandomMock(Hex.decodeHex(SHORT_ROUND2_CLIENT_MOCK)));
        EcJpake srv = new EcJpake(EcJpake.Role.SERVER, PAIRING_CODE,
                new SecureRandomMock(Hex.decodeHex(SHORT_ROUND2_SERVER_MOCK)));

        byte[] cliRound1 = cli.getRound1();
        byte[] srvRound1 = srv.getRound1();
        assertEquals(330, cliRound1.length);
        assertEquals(330, srvRound1.length);
        srv.readRound1(cliRound1);
        cli.readRound1(srvRound1);

        byte[] srvRound2 = srv.getRound2();
        assertEquals("server round 2 must always be exactly 168 bytes", 168, srvRound2.length);
        // 3 bytes of ECParameters precede the KKP in a server round 2.
        assertPaddedShortScalar(srvRound2, 3);

        byte[] cliRound2 = cli.getRound2();
        assertEquals("client round 2 must always be exactly 165 bytes", 165, cliRound2.length);

        assertEquals(168, cli.readRound2(srvRound2));
        assertEquals(165, srv.readRound2(cliRound2));
        assertArrayEquals(cli.deriveSecret(), srv.deriveSecret());
    }

    @Test
    public void jpake2ResponseAcceptsShortCargoFromPeer() throws DecoderException {
        EcJpake cli = new EcJpake(EcJpake.Role.CLIENT, PAIRING_CODE,
                new SecureRandomMock(Hex.decodeHex(SHORT_ROUND2_CLIENT_MOCK)));
        EcJpake srv = new EcJpake(EcJpake.Role.SERVER, PAIRING_CODE,
                new SecureRandomMock(Hex.decodeHex(SHORT_ROUND2_SERVER_MOCK)));
        srv.readRound1(cli.getRound1());
        cli.readRound1(srv.getRound1());

        // What a peer which writes minimal-length scalars puts on the wire: 167 bytes, not 168.
        byte[] shortRound2 = toMinimalScalarEncoding(srv.getRound2(), 3);
        assertEquals(167, shortRound2.length);

        Jpake2Response response = new Jpake2Response(0, shortRound2);
        assertEquals("the cargo stays at the fixed framed size", 170, response.getCargo().length);
        assertEquals(0, response.getAppInstanceId());
        assertEquals(168, response.getCentralChallengeHash().length);
        assertEquals("the short round is zero-padded", 0,
                response.getCentralChallengeHash()[167]);

        // The padded round is still readable and produces the same shared secret.
        assertEquals(167, cli.readRound2(response.getCentralChallengeHash()));
        srv.readRound2(cli.getRound2());
        assertArrayEquals(srv.deriveSecret(), cli.deriveSecret());

        // Parsing the raw framed cargo (as PacketArrayList would hand it over) also works.
        Jpake2Response fromCargo = new Jpake2Response(response.getCargo());
        assertArrayEquals(response.getCargo(), fromCargo.getCargo());
    }

    @Test
    public void jpake1ResponsesAcceptShortCargoFromPeer() throws DecoderException {
        EcJpake peer = new EcJpake(EcJpake.Role.CLIENT, PAIRING_CODE,
                new SecureRandomMock(Hex.decodeHex(SHORT_ROUND1_MOCK)));
        byte[] round1 = peer.getRound1();
        byte[] shortRound1 = toMinimalScalarEncoding(round1,
                scalarLenOffset(round1, 0) + 1 + (round1[scalarLenOffset(round1, 0)] & 0xff));
        assertEquals(329, shortRound1.length);

        // The 165/165 split of a short round 1 leaves the second half one byte short.
        Jpake1aResponse resp1a = new Jpake1aResponse(0, Arrays.copyOfRange(shortRound1, 0, 165));
        assertEquals(167, resp1a.getCargo().length);
        Jpake1bResponse resp1b = new Jpake1bResponse(0, Arrays.copyOfRange(shortRound1, 165, 329));
        assertEquals(167, resp1b.getCargo().length);
        assertEquals(165, resp1b.getCentralChallengeHash().length);

        // Reassembled and zero-padded back to 330 bytes, a peer still parses it: every field is
        // length-prefixed, so the trailing pad byte is ignored.
        byte[] reassembled = new byte[330];
        System.arraycopy(resp1a.getCentralChallengeHash(), 0, reassembled, 0, 165);
        System.arraycopy(resp1b.getCentralChallengeHash(), 0, reassembled, 165, 165);
        EcJpake reader = new EcJpake(EcJpake.Role.SERVER, PAIRING_CODE);
        assertEquals(329, reader.readRound1(reassembled));
    }

    /**
     * A pump which frames the cargo at the round's actual length (169 instead of 170 bytes) must
     * still be parseable: PacketArrayList used to reject the packet outright on the cargo size
     * byte, before the message class ever saw it.
     */
    @Test
    public void packetArrayListAcceptsShortFramedJpake2Response() throws DecoderException {
        EcJpake cli = new EcJpake(EcJpake.Role.CLIENT, PAIRING_CODE,
                new SecureRandomMock(Hex.decodeHex(SHORT_ROUND2_CLIENT_MOCK)));
        EcJpake srv = new EcJpake(EcJpake.Role.SERVER, PAIRING_CODE,
                new SecureRandomMock(Hex.decodeHex(SHORT_ROUND2_SERVER_MOCK)));
        srv.readRound1(cli.getRound1());
        cli.readRound1(srv.getRound1());
        byte[] shortRound2 = toMinimalScalarEncoding(srv.getRound2(), 3);
        assertEquals(167, shortRound2.length);

        byte txId = 3;
        byte[] cargo = Bytes.combine(Bytes.firstTwoBytesLittleEndian(0), shortRound2);
        assertEquals(169, cargo.length);
        byte[] packet = new byte[3 + cargo.length];
        packet[0] = 37; // Jpake2Response opcode
        packet[1] = txId;
        packet[2] = (byte) cargo.length;
        System.arraycopy(cargo, 0, packet, 3, cargo.length);
        byte[] withCrc = Bytes.combine(packet, Bytes.calculateCRC16(packet));

        Jpake2Request request = new Jpake2Request(0, new byte[165]);
        TronMessageWrapper wrapper = new TronMessageWrapper(request, txId);
        PacketArrayList packetArrayList = wrapper.buildPacketArrayList(MessageType.RESPONSE);

        List<List<Byte>> chunks = Packetize.partitionList(withCrc, 18);
        PumpResponseMessage response = null;
        int packetsRemaining = chunks.size() - 1;
        for (List<Byte> chunk : chunks) {
            byte[] chunkBytes = new byte[chunk.size()];
            for (int i = 0; i < chunk.size(); i++) {
                chunkBytes[i] = chunk.get(i);
            }
            response = BTResponseParser.parse(request, packetArrayList,
                    new Packet((byte) packetsRemaining--, txId, chunkBytes).build(),
                    CharacteristicUUID.AUTHORIZATION_CHARACTERISTICS);
        }

        assertTrue("no message parsed from a short-framed Jpake2Response", response.message().isPresent());
        Jpake2Response parsed = (Jpake2Response) response.message().get();
        assertEquals(170, parsed.getCargo().length);
        assertArrayEquals(Arrays.copyOf(shortRound2, 168), parsed.getCentralChallengeHash());
    }

    @Test
    public void jpakeResponsesRejectOversizedCargo() {
        try {
            new Jpake2Response(new byte[171]);
            fail("expected an oversized Jpake2Response cargo to be rejected");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("171"));
        }
        try {
            new Jpake1aResponse(new byte[168]);
            fail("expected an oversized Jpake1aResponse cargo to be rejected");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("168"));
        }
        try {
            new Jpake1bResponse(new byte[168]);
            fail("expected an oversized Jpake1bResponse cargo to be rejected");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("168"));
        }
    }
}

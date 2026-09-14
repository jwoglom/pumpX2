// This code is based on the implementation of EC-JPAKE from mbed TLS

/*
 * Copyright (C) 2006-2015, ARM Limited, All Rights Reserved
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.particle.crypto;

import io.particle.util.Streams;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.BigIntegers;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.security.Security;

/**
 * A class implementing the EC-JPAKE protocol as defined by the Thread specification.
 */
public class EcJpake {
    /**
     * Peer role.
     */
    public enum Role {
        /**
         * Client.
         */
        CLIENT,
        /**
         * Server.
         */
        SERVER
    }

    private class KeyPair {
        public BigInteger priv;
        public ECPoint pub;
    }

    private BigInteger xm1;
    private ECPoint Xm1;
    private BigInteger xm2;
    private ECPoint Xm2;
    private ECPoint Xp1;
    private ECPoint Xp2;
    private ECPoint Xp;
    private BigInteger s;

    private boolean hasPeerRound1;
    private boolean hasPeerRound2;
    private byte[] myRound1;
    private byte[] myRound2;
    private byte[] derivedSecret;

    private Role role;
    private byte[] myId;
    private byte[] peerId;
    private ECParameterSpec ec;
    private MessageDigest hash;
    private SecureRandom rand;

    private static final String CURVE_NAME = "P-256";
    private static final int CURVE_ID = 23; // RFC 4492, 5.1.1
    private static final String HASH_NAME = "SHA256";
    private static final byte[] CLIENT_ID = "client".getBytes();
    private static final byte[] SERVER_ID = "server".getBytes();
    private static final boolean ENCODED_COMPRESSED = false;

    static {
        // Remove builtin Android BouncyCastle provider and replace with newer version
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) != null) {
            Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        }
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Constructor.
     *
     * @param role The role of this peer.
     * @param secret The pre-shared secret.
     */
    public EcJpake(Role role, byte[] secret) {
        this(role, secret, new SecureRandom());
    }

    /**
     * Constructor.
     *
     * @param role The role of this peer.
     * @param secret The pre-shared secret.
     * @param random The secure random number generator to use.
     */
    public EcJpake(Role role, byte[] secret, SecureRandom random) {
        this.ec = ECNamedCurveTable.getParameterSpec(CURVE_NAME);
        if (this.ec == null) {
            throw new UnsupportedOperationException("Unsupported curve type");
        }
        try {
            this.hash = MessageDigest.getInstance(HASH_NAME);
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException("Unsupported hash algorithm", e);
        }
        this.rand = random;
        this.role = role;
        if (this.role == Role.CLIENT) {
            this.myId = CLIENT_ID;
            this.peerId = SERVER_ID;
        } else {
            this.myId = SERVER_ID;
            this.peerId = CLIENT_ID;
        }
        this.s = new BigInteger(1, secret);
        this.xm1 = null;
        this.Xm1 = null;
        this.xm2 = null;
        this.Xm2 = null;
        this.Xp1 = null;
        this.Xp2 = null;
        this.Xp = null;
        this.hasPeerRound1 = false;
        this.hasPeerRound2 = false;
        this.myRound1 = null;
        this.myRound2 = null;
        this.derivedSecret = null;
    }

    /**
     * Generate a message for the first round of the protocol.
     *
     * @return The message data.
     */
    public byte[] getRound1() {
        if (this.myRound1 == null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            KeyPair kp = this.genKeyPair(this.ec.getG());
            this.xm1 = kp.priv;
            this.Xm1 = kp.pub;
            this.writePoint(out, this.Xm1);
            this.writeZkp(out, this.ec.getG(), this.xm1, this.Xm1, this.myId);
            kp = this.genKeyPair(this.ec.getG());
            this.xm2 = kp.priv;
            this.Xm2 = kp.pub;
            this.writePoint(out, this.Xm2);
            this.writeZkp(out, this.ec.getG(), this.xm2, this.Xm2, this.myId);
            this.myRound1 = out.toByteArray();
        }
        return this.myRound1;
    }

    /**
     * Read a message generated by the peer for the first round of the protocol.
     *
     * @param data The message data.
     * @return The number of bytes read.
     */
    public int readRound1(byte[] data) {
        if (this.hasPeerRound1) {
            throw new IllegalStateException("Invalid protocol state");
        }
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        this.Xp1 = this.readPoint(in);
        this.readZkp(in, this.ec.getG(), this.Xp1, this.peerId);
        this.Xp2 = this.readPoint(in);
        this.readZkp(in, this.ec.getG(), this.Xp2, this.peerId);
        this.hasPeerRound1 = true;
        return data.length - in.available();
    }

    /**
     * Generate a message for the second round of the protocol.
     *
     * @return The message data.
     */
    public byte[] getRound2() {
        if (this.myRound2 == null) {
            if (!this.hasPeerRound1 || this.myRound1 == null) {
                throw new IllegalStateException("Invalid protocol state");
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ECPoint G = this.Xp1.add(this.Xp2).add(this.Xm1);
            BigInteger xm = this.mulSecret(this.xm2, this.s, false /* negate */);
            ECPoint Xm = G.multiply(xm);
            if (this.role == Role.SERVER) {
                this.writeCurveId(out);
            }
            this.writePoint(out, Xm);
            this.writeZkp(out, G, xm, Xm, this.myId);
            this.myRound2 = out.toByteArray();
        }
        return this.myRound2;
    }

    /**
     * Read a message generated by the peer for the second round of the protocol.
     *
     * @param data The message data.
     * @return The number of bytes read.
     */
    public int readRound2(byte[] data) {
        if (!this.hasPeerRound1 || this.myRound1 == null || this.hasPeerRound2) {
            throw new IllegalStateException("Invalid protocol state");
        }
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        if (this.role == Role.CLIENT) {
            this.readCurveId(in);
        }
        ECPoint G = this.Xm1.add(this.Xm2).add(this.Xp1);
        this.Xp = this.readPoint(in);
        this.readZkp(in, G, this.Xp, this.peerId);
        this.hasPeerRound2 = true;
        return data.length - in.available();
    }

    /**
     * Derive a shared secret.
     *
     * @return The shared secret.
     */
    public byte[] deriveSecret() {
        if (this.derivedSecret == null) {
            if (!this.hasPeerRound2) {
                throw new IllegalStateException("Invalid protocol state");
            }
            BigInteger xm2s = this.mulSecret(this.xm2, this.s, true /* negate */);
            ECPoint K = this.Xp.add(this.Xp2.multiply(xm2s)).multiply(this.xm2);
            // PMS = SHA-256(K.X), where K.X is the x coordinate written out at the full field
            // length. Using a minimal-length encoding here would hash 31 bytes instead of 32
            // roughly 1 time in 256 (whenever the top byte of K.X is zero) and derive a secret
            // which does not match the peer's. mbedtls_ecjpake_derive_secret writes
            // x_bytes = (grp.pbits + 7) / 8 bytes via mbedtls_mpi_write_binary before hashing.
            ECPoint K1 = K.normalize();
            int fieldLen = (this.ec.getCurve().getFieldSize() + 7) / 8;
            this.derivedSecret = this.hash.digest(
                    BigIntegers.asUnsignedByteArray(fieldLen, K1.getXCoord().toBigInteger()));
        }
        return this.derivedSecret;
    }

    private void readZkp(InputStream in, ECPoint G, ECPoint X, byte[] id) {
        ECPoint V = this.readPoint(in);
        BigInteger r = this.readNum(in);
        BigInteger h = this.zkpHash(G, V, X, id);
        ECPoint VV = G.multiply(r).add(X.multiply(h.mod(this.ec.getN())));
        if (!VV.equals(V)) {
            throw new RuntimeException("Validation failed");
        }
    }

    private void writeZkp(OutputStream out, ECPoint G, BigInteger x, ECPoint X, byte[] id) {
        KeyPair kp = this.genKeyPair(G);
        BigInteger v = kp.priv;
        ECPoint V = kp.pub;
        BigInteger h = this.zkpHash(G, V, X, id);
        BigInteger r = v.subtract(x.multiply(h)).mod(this.ec.getN());
        this.writePoint(out, V);
        this.writeNum(out, r);
    }

    private BigInteger zkpHash(ECPoint G, ECPoint V, ECPoint X, byte[] id) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.writeZkpHashPoint(out, G);
        this.writeZkpHashPoint(out, V);
        this.writeZkpHashPoint(out, X);
        Streams.writeUint32Be(out, id.length);
        Streams.write(out, id);
        byte[] hash = this.hash.digest(out.toByteArray());
        BigInteger h = new BigInteger(1, hash);
        return h.mod(this.ec.getN());
    }

    private void writeZkpHashPoint(OutputStream out, ECPoint point) {
        byte[] encoded = point.getEncoded(ENCODED_COMPRESSED /* compressed */);
        Streams.writeUint32Be(out, encoded.length);
        Streams.write(out, encoded);
    }

    private BigInteger mulSecret(BigInteger X, BigInteger S, boolean negate) {
        BigInteger b = new BigInteger(1, this.randBytes(16));
        b = b.multiply(this.ec.getN()).add(S);
        BigInteger R = X.multiply(b);
        if (negate) {
            R = R.negate();
        }
        return R.mod(this.ec.getN());
    }

    private ECPoint readPoint(InputStream in) {
        int len = Streams.readUint8(in);
        byte[] encoded = Streams.read(in, len);
        return this.ec.getCurve().decodePoint(encoded);
    }

    private void writePoint(OutputStream out, ECPoint point) {
        byte[] encoded = point.getEncoded(ENCODED_COMPRESSED /* compressed */);
        if (encoded.length > 255) {
            throw new RuntimeException("Encoded ECPoint is too long");
        }
        Streams.writeUint8(out, encoded.length);
        Streams.write(out, encoded);
    }

    private BigInteger readNum(InputStream in) {
        int len = Streams.readUint8(in);
        byte[] encoded = Streams.read(in, len);
        return new BigInteger(1, encoded);
    }

    /**
     * Returns the length in bytes of a scalar modulo the curve order (32 for P-256).
     */
    private int scalarLen() {
        return (this.ec.getN().bitLength() + 7) / 8;
    }

    private void writeNum(OutputStream out, BigInteger val) {
        // The ZKP scalar r is uniform modulo the curve order, so roughly 1 time in 256 its most
        // significant byte is zero. A minimal-length encoding (the literal mbedTLS behaviour,
        // which writes mbedtls_mpi_size(&r) bytes) therefore makes the serialized round shorter
        // than its nominal length, and the Tandem pairing protocol carries these rounds inside
        // fixed-size message cargos (165/165 bytes for round 1, 165 or 168 for round 2). A short
        // round gets zero-padded up to the fixed cargo size by the framing, which leaves a
        // trailing junk byte that a strict peer (mbedTLS requires p == end after parsing a round)
        // rejects -- so about 1 pairing in 256 failed.
        //
        // Writing the scalar zero-padded to the full curve length keeps our output deterministic
        // and exactly the nominal size. It stays wire-compatible with a peer that expects the
        // minimal-length encoding, because the field is length-prefixed and every reader turns
        // those bytes into an integer: this class's readNum uses new BigInteger(1, encoded) and
        // mbedTLS's ecjpake_zkp_read uses mbedtls_mpi_read_binary, both of which ignore leading
        // zeroes and accept any length from 1 to 255.
        byte[] encoded = BigIntegers.asUnsignedByteArray(this.scalarLen(), val);
        if (encoded.length > 255) {
            throw new RuntimeException("Encoded BigInteger is too long");
        }
        Streams.writeUint8(out, encoded.length);
        Streams.write(out, encoded);
    }

    private void readCurveId(InputStream in) {
        int type = Streams.readUint8(in);
        if (type != 3) { // ECCurveType.named_curve
            throw new RuntimeException("Invalid message");
        }
        int id = Streams.readUint16Be(in);
        if (id != CURVE_ID) {
            throw new RuntimeException("Unexpected curve type");
        }
    }

    private void writeCurveId(OutputStream out) {
        Streams.writeUint8(out, 3); // ECCurveType.named_curve
        Streams.writeUint16Be(out, CURVE_ID);
    }

    private KeyPair genKeyPair(ECPoint G) {
        KeyPair kp = new KeyPair();
        kp.priv = BigIntegers.createRandomInRange(BigInteger.ONE, this.ec.getN().subtract(BigInteger.ONE), this.rand);
        kp.pub = G.multiply(kp.priv);
        return kp;
    }

    private byte[] randBytes(int bytes) {
        byte[] b = new byte[bytes];
        this.rand.nextBytes(b);
        return b;
    }
}

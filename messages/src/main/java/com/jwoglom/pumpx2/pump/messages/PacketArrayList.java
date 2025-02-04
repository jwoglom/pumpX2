package com.jwoglom.pumpx2.pump.messages;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.bluetooth.PumpStateSupplier;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.models.UnexpectedOpCodeException;
import com.jwoglom.pumpx2.pump.messages.models.UnexpectedTransactionIdException;
import com.jwoglom.pumpx2.shared.JavaHelpers;
import com.jwoglom.pumpx2.shared.L;

import com.jwoglom.pumpx2.shared.Hex;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;

public class PacketArrayList {
    protected static final String TAG = "PacketArrayList";
    public static final String IGNORE_INVALID_HMAC = "IGNORE_HMAC_SIGNATURE_EXCEPTION";
    public static boolean ignoreInvalidTxId = false;

    protected byte expectedOpCode;
    protected byte expectedCargoSize;
    protected int actualExpectedCargoSize;
    protected byte expectedTxId;
    protected boolean isSigned;
    protected byte[] fullCargo;
    protected byte[] messageData;

    protected byte firstByteMod15;
    protected byte opCode;
    protected boolean empty = true;
    protected final byte[] expectedCrc = {0, 0};

    protected PacketArrayList(byte expectedopCode, byte expectedCargoSize, byte expectedTxId, boolean isSigned) {
        Preconditions.checkArgument(expectedopCode != 0);
        Preconditions.checkArgument(expectedCargoSize >= 0 || (256 + (int)expectedCargoSize) >= 0);
        this.expectedCargoSize = expectedCargoSize;
        if (expectedCargoSize < 0 && expectedCargoSize > -128) {
            this.actualExpectedCargoSize = 256 + expectedCargoSize;
        } else {
            this.actualExpectedCargoSize = expectedCargoSize;
        }
        this.expectedOpCode = expectedopCode;
        this.expectedTxId = expectedTxId;
        this.isSigned = isSigned;
        this.fullCargo = new byte[(this.actualExpectedCargoSize + 2)];
        this.messageData = new byte[3];
    }

    // Returns either PacketArrayList or StreamPacketArrayList depending on the opcode
    public static PacketArrayList build(byte expectedopCode, Characteristic characteristic, byte expectedCargoSize, byte expectedTxId, boolean isSigned) {
        Class<? extends Message> messageClass = Messages.fromOpcode(expectedopCode, characteristic);
        L.d(TAG, String.format("queried messageClass for expectedOpcode %d, %s: expectedCargoSize=%d", expectedopCode, messageClass, expectedCargoSize));
        MessageProps messageProps = messageClass.getAnnotation(MessageProps.class);
        if (messageProps.stream()) {
            return new StreamPacketArrayList(expectedopCode, expectedCargoSize, expectedTxId, isSigned);
        }

        return new PacketArrayList(expectedopCode, expectedCargoSize, expectedTxId, isSigned);
    }

    public byte[] messageData() {
        return messageData;
    }

    public byte opCode() {
        return opCode;
    }

    public final boolean needsMorePacket() {
        byte b = this.firstByteMod15;
        return b >= 0;
    }

    public boolean validate(byte[] authKey) {
        Intrinsics.checkParameterIsNotNull(authKey, "ak");
        if (needsMorePacket()) {
            return false;
        }
        createMessageData();
        if (this.fullCargo.length >= 2) {
            System.arraycopy(this.fullCargo, this.fullCargo.length - 2, this.expectedCrc, 0, 2);
        }
        byte[] a = Bytes.calculateCRC16(this.messageData);
        byte[] lastTwoB = this.expectedCrc;
        boolean ok = true;

        if (!(a[0] == lastTwoB[0] && a[1] == lastTwoB[1])) {
            ok = false;
        }
        // the fullCargo size is 2 + the message length.
        L.d(TAG, "validate fullCargo size="+fullCargo.length+" ok="+ok);
        if (!ok) {
            if (shouldIgnoreInvalidHmac(authKey)) {
                L.e(TAG, "CRC validation failed for: " + ((int) this.expectedOpCode) + ". a: " + Hex.encodeHexString(a) + " lastTwoB: " + Hex.encodeHexString(lastTwoB) + ". fullCargo len=" + fullCargo.length + " opCode="+opCode);
            } else {
                throw new InvalidCRCException("CRC validation failed for: " + ((int) this.expectedOpCode) + ". a: " + Hex.encodeHexString(a) + " lastTwoB: " + Hex.encodeHexString(lastTwoB) + ". fullCargo len=" + fullCargo.length + " opCode="+opCode+" authKey="+new String(authKey));
            }
        } else if (this.isSigned) {
            L.d(TAG, "validate(" + Hex.encodeHexString(authKey) + ") messageData: " + Hex.encodeHexString(messageData) + " len: " + messageData.length + " fullCargo: " + Hex.encodeHexString(fullCargo) + " len: " + fullCargo.length);
            byte[] byteArray = Bytes.dropLastN(this.messageData, 20);
            byte[] bArr2 = this.messageData;
            byte[] expectedHmac = Bytes.dropFirstN(bArr2, bArr2.length - 20);
            byte[] hmacSha = Packetize.doHmacSha1(byteArray, authKey);
            if (!Arrays.equals(expectedHmac, hmacSha)) {
                L.e(TAG, "Pump response invalid signature: expectedHmac=" + Hex.encodeHexString(expectedHmac)+" hmacSha="+Hex.encodeHexString(hmacSha));
                if (shouldIgnoreInvalidHmac(authKey)) {
                    return true;
                }


                throw new InvalidSignedMessageHMACSignatureException("Pump response invalid: SIGNATURE");
            }
        }
        return ok;
    }

    protected void createMessageData() {
        byte[] bArr = this.messageData;
        bArr[0] = this.expectedOpCode;
        bArr[1] = this.expectedTxId;
        bArr[2] = this.expectedCargoSize;
        this.messageData = Bytes.combine(bArr, Bytes.dropLastN(this.fullCargo, 2));
    }

    protected void parse(byte[] bArr) {
        byte opCode = bArr[2];
        int cargoSize = bArr[4];
        if (cargoSize < 0 && cargoSize > -128) {
            cargoSize += 256;
        }
        // ErrorResponse handling. Can have a dynamic cargo size in different situations.
        if (77 == opCode && 2 == cargoSize) {
            this.expectedOpCode = 77;
            this.expectedCargoSize = 2;
            this.actualExpectedCargoSize = 2;
            this.fullCargo = new byte[4];
        } else if (77 == opCode && 26 == cargoSize) {
            this.expectedOpCode = 77;
            this.expectedCargoSize = 26;
            this.actualExpectedCargoSize = 26;
            this.fullCargo = new byte[28];
        }
        // ApiVersionRequest can either have 0 cargo size or 2
        // its cargo size is set as 2 in the MessageProps but we allow the 0-cargo here as well
        if (32 == opCode && 0 == cargoSize) {
            this.expectedCargoSize = 0;
            this.actualExpectedCargoSize = 0;
        }
        if (opCode == this.expectedOpCode) {
            this.firstByteMod15 = (byte) (bArr[0] & 15);
            this.opCode = bArr[2];
            byte txId = bArr[3];
            L.d(TAG, "PacketArrayList firstByteMod15="+firstByteMod15+" opCode="+opCode+" txId="+txId+" expectedTxId="+expectedTxId+" bArr="+JavaHelpers.display(bArr));
            if (txId != this.expectedTxId) {
                throwUnexpectedTransactionIdException(txId);
            } else if (cargoSize != this.actualExpectedCargoSize) {
                if (cargoSize == this.actualExpectedCargoSize + 24 && isSigned) {
                    L.i(TAG, "adding +24 expectedCargoSize for already signed request which contains an existing trailer");
                    expectedCargoSize += 24;
                    actualExpectedCargoSize += 24;
                } else {
                    throw new IllegalArgumentException("Unexpected cargo size: " + ((int) cargoSize) + ", expecting " + ((int) this.actualExpectedCargoSize) + " for opCode="+opCode);
                }
            }
            this.fullCargo = Bytes.dropFirstN(bArr, 5);
        } else {
            throw new UnexpectedOpCodeException(opCode, this.expectedOpCode);
        }
        L.d(TAG, "PacketArrayParse ok: opCode="+opCode+" firstByteMod15="+firstByteMod15+" cargoSize="+cargoSize);
    }


    public void validatePacket(byte[] packetData) {
        Intrinsics.checkParameterIsNotNull(packetData, "packetData");
        if (packetData.length == 0) {
            throw new IllegalArgumentException("Empty data");
        } else if (packetData.length >= 3) {
            byte firstByteMod15 = (byte) (packetData[0] & 15);
            byte secondByte = packetData[1];
            L.d(TAG, "Found tx id: "+secondByte+" expected "+this.expectedTxId);
            if (secondByte == this.expectedTxId) {
                if (this.empty) {
                    this.firstByteMod15 = firstByteMod15;
                    L.d(TAG, "txid matches, firstByteMod15="+firstByteMod15+", parsing packetData");
                    parse(packetData);
                } else if (((byte) 0) == firstByteMod15) {
                    L.d(TAG, "firstByteMod15=0");
                    if (this.firstByteMod15 == firstByteMod15) {
                        this.fullCargo = Bytes.combine(this.fullCargo, Bytes.dropFirstN(packetData, 2));
                        L.d(TAG, "firstByteMod15 matches expected, fullCargo="+ Hex.encodeHexString(fullCargo));
                    } else {
                        throw new IllegalArgumentException("Unexpected packets remaining 3: " + ((int) firstByteMod15) + ", expected " + ((int) this.firstByteMod15) + ", opCode: " + ((int) this.expectedOpCode));
                    }
                } else if (this.firstByteMod15 == firstByteMod15) {
                    this.fullCargo = Bytes.combine(this.fullCargo, Bytes.dropFirstN(packetData, 2));
                    L.d(TAG, "firstByteMod15 matches expected, nonzero, fullCargo=" + Hex.encodeHexString(fullCargo));
                } else {
                    throw new IllegalArgumentException("Unexpected packets remaining 2: " + ((int) firstByteMod15) + ", expected " + ((int) this.firstByteMod15) + ", opCode: " + ((int) this.expectedOpCode));
                }
                this.empty = false;
                this.firstByteMod15 = (byte) (this.firstByteMod15 - 1);
                L.d(TAG, "validatePacket: decrementing firstByteMod15 to " + firstByteMod15);
                return;
            }
            throwUnexpectedTransactionIdException(secondByte);
        } else {
            throw new IllegalArgumentException("Invalid data size: " + packetData.length);
        }
    }

    private void throwUnexpectedTransactionIdException(byte foundTxId) {
        RuntimeException ex = new UnexpectedTransactionIdException(foundTxId, this.expectedTxId, this.expectedOpCode);
        if (ignoreInvalidTxId) {
            L.e(TAG, ex);
            return;
        }
        throw ex;
    }

    private boolean shouldIgnoreInvalidHmac(byte[] authKey) {
        return Arrays.equals(
                Arrays.copyOfRange(authKey, 0, IGNORE_INVALID_HMAC.length()),
                Arrays.copyOfRange(IGNORE_INVALID_HMAC.getBytes(StandardCharsets.UTF_8), 0, IGNORE_INVALID_HMAC.length())
        );
    }

    public String toString() {
        return JavaHelpers.autoToString(this, ImmutableSet.of());
    }

    public static class InvalidCRCException extends RuntimeException {
        InvalidCRCException(String message) {
            super(message);
        }
    }

    public static class InvalidSignedMessageHMACSignatureException extends RuntimeException {
        InvalidSignedMessageHMACSignatureException(String message) {
            super(message);
        }
    }
}

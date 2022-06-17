package com.jwoglom.pumpx2.pump.messages;

import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.HistoryLogStreamResponse;
import com.jwoglom.pumpx2.shared.L;

import org.apache.commons.codec.binary.Hex;

import java.util.Arrays;

import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import timber.log.Timber;

public class PacketArrayList {
    private static final String TAG = "X2-PacketArrayList";

    private byte expectedOpCode;
    private byte expectedCargoSize;
    private byte expectedTxId;
    private boolean isSigned;
    private byte[] fullCargo;
    private byte[] messageData;

    // Saved original messageData for history log request
    private byte[] originalMessageData;

    private byte firstByteMod15;
    private byte opCode;
    private boolean empty = true;
    private final byte[] expectedCrc = {0, 0};

    public PacketArrayList(byte expectedopCode, byte expectedCargoSize, byte expectedTxId, boolean isSigned) {
        this.expectedOpCode = expectedopCode;
        this.expectedCargoSize = expectedCargoSize;
        this.expectedTxId = expectedTxId;
        this.isSigned = isSigned;
        this.fullCargo = new byte[(expectedCargoSize + 2)];
        this.messageData = new byte[3];
    }

    public byte[] messageData() {
        return messageData;
    }

    public byte opCode() {
        return opCode;
    }

    private boolean isStream() {
        return expectedOpCode == Messages.HISTORY_LOG_STREAM.responseOpCode();
    }

    public final boolean needsMorePacket() {
        byte b = this.firstByteMod15;
        return b >= 0;
    }

    public final boolean validate(String str) {
        Intrinsics.checkParameterIsNotNull(str, "ak");
        if (needsMorePacket()) {
            return false;
        }
        if (isStream()) {
            expectedTxId = 0;
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
        if (!ok) {
            throw new RuntimeException("CRC validation failed for: " + ((int) this.expectedOpCode));
        } else if (this.isSigned) {
            byte[] byteArray = CollectionsKt.toByteArray(ArraysKt.dropLast(this.messageData, 20));
            byte[] bArr2 = this.messageData;
            byte[] byteArray2 = CollectionsKt.toByteArray(ArraysKt.drop(bArr2, bArr2.length - 20));
            byte[] bytes = str.getBytes(Charsets.UTF_8);
            Intrinsics.checkExpressionValueIsNotNull(bytes, "(this as java.lang.String).getBytes(charset)");
            if (!Arrays.equals(byteArray2, Packetize.doHmacSha1(byteArray, bytes))) {
                throw new RuntimeException("Pump response invalid: SIGNATURE");
                //return false;
            }
        }
        return ok;
    }

    private void createMessageData() {
        byte[] bArr = this.messageData;
        bArr[0] = this.expectedOpCode;
        bArr[1] = this.expectedTxId;
        bArr[2] = this.expectedCargoSize;
        this.messageData = ArraysKt.plus(bArr, CollectionsKt.toByteArray(ArraysKt.dropLast(this.fullCargo, 2)));
    }

    private void parse(byte[] bArr) {
        byte opCode = bArr[2];
        byte cargoSize = bArr[4];
        if (77 == opCode && 2 == cargoSize) {
            this.expectedOpCode = 77;
            this.expectedCargoSize = 2;
            this.fullCargo = new byte[4];
        } else if (77 == opCode && 26 == cargoSize) {
            this.expectedOpCode = 77;
            this.expectedCargoSize = 26;
            this.fullCargo = new byte[28];
        }
        if (opCode == this.expectedOpCode) {
            this.firstByteMod15 = (byte) (bArr[0] & 15);
            this.opCode = bArr[2];
            byte txId = bArr[3];
            if (opCode == Messages.HISTORY_LOG_STREAM.responseOpCode() && cargoSize != this.expectedCargoSize) {
                expectedCargoSize = bArr[4];
            }
            if (txId != this.expectedTxId) {
                throw new IllegalArgumentException("Unexpected transaction ID in packet: " + ((int) txId) + ", expecting " + ((int) this.expectedTxId));
            } else if (cargoSize != this.expectedCargoSize) {
                if (opCode == Messages.HISTORY_LOG_STREAM.responseOpCode()) {
                    // ignore
                    Timber.i("For HistoryLogStreamResponse, ignoring cargo size %d instead of expected %d", cargoSize, expectedCargoSize);
                } else {
                    throw new IllegalArgumentException("Unexpected cargo size: " + ((int) cargoSize) + ", expecting " + ((int) this.expectedCargoSize));
                }
            } else if (cargoSize <= 255) {
                this.fullCargo = CollectionsKt.toByteArray(ArraysKt.drop(bArr, 5));
            } else {
                throw new IllegalArgumentException("Cargo size beyond maximum: " + ((int) cargoSize));
            }
        } else {
            throw new IllegalArgumentException("Unexpected opCode: " + ((int) opCode) + ", expecting " + ((int) this.expectedOpCode));
        }
    }

    public final void validatePacket(byte[] packetData) {
        Intrinsics.checkParameterIsNotNull(packetData, "packetData");
        if (packetData.length == 0) {
            throw new IllegalArgumentException("Empty data");
        } else if (packetData.length >= 3) {
            byte firstByteMod15 = (byte) (packetData[0] & 15);
            byte secondByte = packetData[1];
            L.w(TAG, "Found tx id: "+secondByte+" expected "+this.expectedTxId);
            if (secondByte == this.expectedTxId) {
                if (this.empty) {
                    this.firstByteMod15 = firstByteMod15;
                    L.w(TAG, "txid matches, firstByteMod15="+firstByteMod15+", parsing packetData");
                    parse(packetData);
                } else if (((byte) 0) == firstByteMod15) {
                    L.w(TAG, "firstByteMod15=0");
                    if (this.firstByteMod15 == firstByteMod15) {
                        this.fullCargo = ArraysKt.plus(this.fullCargo, CollectionsKt.toByteArray(ArraysKt.drop(packetData, 2)));
                        L.w(TAG, "firstByteMod15 matches expected, fullCargo="+ Hex.encodeHexString(fullCargo));
                    } else {
                        throw new IllegalArgumentException("Unexpected packets remaining 3: " + ((int) firstByteMod15) + ", expected " + ((int) this.firstByteMod15) + ", opCode: " + ((int) this.expectedOpCode));
                    }
                } else if (this.firstByteMod15 == firstByteMod15) {
                    this.fullCargo = ArraysKt.plus(this.fullCargo, CollectionsKt.toByteArray(ArraysKt.drop(packetData, 2)));
                    L.w(TAG, "firstByteMod15 matches expected, nonzero, fullCargo=" + Hex.encodeHexString(fullCargo));
                } else {
                    throw new IllegalArgumentException("Unexpected packets remaining 2: " + ((int) firstByteMod15) + ", expected " + ((int) this.firstByteMod15) + ", opCode: " + ((int) this.expectedOpCode));
                }
                if (isStream()) {
                    if (moreHistoryLogsRemaining()) {
                        empty = true;
                    }
                } else {
                    this.empty = false;
                }
                this.firstByteMod15 = (byte) (this.firstByteMod15 - 1);
                return;
            }
            throw new IllegalArgumentException("Unexpected transaction ID 1: " + ((int) secondByte) + ", expecting " + ((int) this.expectedTxId) + ", opCode: " + ((int) this.expectedOpCode));
        } else {
            throw new IllegalArgumentException("Invalid data size: " + packetData.length);
        }
    }

    private boolean moreHistoryLogsRemaining() {
        if (firstByteMod15 == 0) {
            return !checkHistoryLogCRC();
        }
        return firstByteMod15 > 0;
    }

    private boolean checkHistoryLogCRC() {
        originalMessageData = messageData;
        messageData = Bytes.combine(
                new byte[]{ (byte) Messages.HISTORY_LOG_STREAM.responseOpCode() },
                new byte[]{ 0 },
                new byte[]{ expectedCargoSize },
                CollectionsKt.toByteArray(ArraysKt.dropLast(messageData, 2))
        );

        if (this.fullCargo.length >= 2) {
            System.arraycopy(this.fullCargo, this.fullCargo.length - 2, this.expectedCrc, 0, 2);
        }
        byte[] crcOut = Bytes.calculateCRC16(messageData);
        if (crcOut[0] == expectedCrc[0] && crcOut[1] == expectedCrc[1]) {
            return true;
        }

        throw new IllegalArgumentException("CRC error with history log: originalMessageData: "+Hex.encodeHexString(originalMessageData)+" messageData: "+Hex.encodeHexString(messageData)+" expectedCrc: "+Hex.encodeHexString(expectedCrc)+" crcOut: "+Hex.encodeHexString(crcOut));

    }
}

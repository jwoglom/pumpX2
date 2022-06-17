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
    protected static final String TAG = "X2-PacketArrayList";

    protected byte expectedOpCode;
    protected byte expectedCargoSize;
    protected byte expectedTxId;
    protected boolean isSigned;
    protected byte[] fullCargo;
    protected byte[] messageData;

    protected byte firstByteMod15;
    protected byte opCode;
    protected boolean empty = true;
    protected final byte[] expectedCrc = {0, 0};

    protected PacketArrayList(byte expectedopCode, byte expectedCargoSize, byte expectedTxId, boolean isSigned) {
        this.expectedOpCode = expectedopCode;
        this.expectedCargoSize = expectedCargoSize;
        this.expectedTxId = expectedTxId;
        this.isSigned = isSigned;
        this.fullCargo = new byte[(expectedCargoSize + 2)];
        this.messageData = new byte[3];
    }

    // Returns either PacketArrayList or StreamPacketArrayList depending on the opcode
    public static PacketArrayList build(byte expectedopCode, byte expectedCargoSize, byte expectedTxId, boolean isSigned) {
        Class<? extends Message> messageClass = Messages.OPCODES.get((int) expectedopCode);
        try {
            Timber.i("queried messageClass for expectedOpcode %d, %s", expectedopCode, messageClass);
            if (messageClass.newInstance().stream()) {
                return new StreamPacketArrayList(expectedopCode, expectedCargoSize, expectedTxId, isSigned);
            }
        } catch (IllegalAccessException|InstantiationException e) {
            Timber.e(e);
            e.printStackTrace();
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

    public boolean validate(String str) {
        Intrinsics.checkParameterIsNotNull(str, "ak");
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

    protected void createMessageData() {
        byte[] bArr = this.messageData;
        bArr[0] = this.expectedOpCode;
        bArr[1] = this.expectedTxId;
        bArr[2] = this.expectedCargoSize;
        this.messageData = ArraysKt.plus(bArr, CollectionsKt.toByteArray(ArraysKt.dropLast(this.fullCargo, 2)));
    }

    protected void parse(byte[] bArr) {
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
            if (txId != this.expectedTxId) {
                throw new IllegalArgumentException("Unexpected transaction ID in packet: " + ((int) txId) + ", expecting " + ((int) this.expectedTxId));
            } else if (cargoSize != this.expectedCargoSize) {
                throw new IllegalArgumentException("Unexpected cargo size: " + ((int) cargoSize) + ", expecting " + ((int) this.expectedCargoSize));
            } else if (cargoSize <= 255) {
                this.fullCargo = CollectionsKt.toByteArray(ArraysKt.drop(bArr, 5));
            } else {
                throw new IllegalArgumentException("Cargo size beyond maximum: " + ((int) cargoSize));
            }
        } else {
            throw new IllegalArgumentException("Unexpected opCode: " + ((int) opCode) + ", expecting " + ((int) this.expectedOpCode));
        }
    }

    public void validatePacket(byte[] packetData) {
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
                this.empty = false;
                this.firstByteMod15 = (byte) (this.firstByteMod15 - 1);
                return;
            }
            throw new IllegalArgumentException("Unexpected transaction ID 1: " + ((int) secondByte) + ", expecting " + ((int) this.expectedTxId) + ", opCode: " + ((int) this.expectedOpCode));
        } else {
            throw new IllegalArgumentException("Invalid data size: " + packetData.length);
        }
    }
}

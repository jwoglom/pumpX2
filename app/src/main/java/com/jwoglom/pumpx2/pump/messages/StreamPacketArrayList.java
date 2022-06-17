package com.jwoglom.pumpx2.pump.messages;

import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.shared.L;

import org.apache.commons.codec.binary.Hex;

import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import timber.log.Timber;

public class StreamPacketArrayList extends PacketArrayList {
    protected static final String TAG = "X2-StreamPacketArrayList";

    private byte[] originalMessageData = new byte[0];

    // Streams don't have transaction IDs
    public StreamPacketArrayList(byte expectedopCode, byte expectedCargoSize, byte expectedTxId, boolean isSigned) {
        super(expectedopCode, expectedCargoSize, (byte) 0, isSigned);
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
                //if (!moreHistoryLogsRemaining()) {
                if (!needsMorePacket()) {
                    Timber.i("Completed parsing historyLog: %s", Hex.encodeHexString(this.messageData));
                    this.empty = true;
                }
                this.firstByteMod15 = (byte) (this.firstByteMod15 - 1);
                return;
            }
            throw new IllegalArgumentException("Unexpected transaction ID 1: " + ((int) secondByte) + ", expecting " + ((int) this.expectedTxId) + ", opCode: " + ((int) this.expectedOpCode));
        } else {
            throw new IllegalArgumentException("Invalid data size: " + packetData.length);
        }
    }

    protected void parse(byte[] bArr) {
        Timber.i("Parsing historyLog byte array: %s", Hex.encodeHexString(bArr));
        byte opCode = bArr[2];
        byte cargoSize = bArr[4];
        if (opCode == this.expectedOpCode) {
            this.firstByteMod15 = (byte) (bArr[0] & 15);
            this.opCode = bArr[2];
            byte txId = bArr[3];

            // Replace cargo size
            expectedCargoSize = cargoSize;
            if (txId != this.expectedTxId) {
                throw new IllegalArgumentException("Unexpected transaction ID in packet: " + ((int) txId) + ", expecting " + ((int) this.expectedTxId));
            } else if (cargoSize <= 255) {
                byte numHistoryLogs = bArr[5];
                if (cargoSize == (numHistoryLogs*26) + 2) {
                    this.fullCargo = CollectionsKt.toByteArray(ArraysKt.drop(bArr, 5));
                    this.empty = false;
                    return;
                }
                throw new IllegalArgumentException("Cargo size doesn't match number of history logs requested");
            } else {
                throw new IllegalArgumentException("Cargo size beyond maximum: " + ((int) cargoSize));
            }
        } else {
            throw new IllegalArgumentException("Unexpected opCode: " + ((int) opCode) + ", expecting " + ((int) this.expectedOpCode));
        }
    }


    private boolean moreHistoryLogsRemaining() {
        if (firstByteMod15 == 0) {
            return !validate("");
        }
        return firstByteMod15 > 0;
    }

//    private void rebuildMessageDataForCRC() {
//        originalMessageData = messageData;
//        messageData = Bytes.combine(
//                new byte[]{ (byte) Messages.HISTORY_LOG_STREAM.responseOpCode() },
//                new byte[]{ 0 },
//                new byte[]{ expectedCargoSize },
//                CollectionsKt.toByteArray(ArraysKt.dropLast(fullCargo, 2))
//        );
//    }

    private void setExpectedCRC() {
        if (this.fullCargo.length >= 2) {
            System.arraycopy(this.fullCargo, this.fullCargo.length - 2, this.expectedCrc, 0, 2);
        }
    }

    public boolean validate(String unused) {
        createMessageData();
        setExpectedCRC();

        byte[] crcOut = Bytes.calculateCRC16(messageData);
        if (crcOut[0] == expectedCrc[0] && crcOut[1] == expectedCrc[1]) {
            return true;
        }

        throw new IllegalArgumentException("CRC error with history log: originalMessageData: "+ Hex.encodeHexString(originalMessageData)+" messageData: "+Hex.encodeHexString(messageData)+" expectedCrc: "+Hex.encodeHexString(expectedCrc)+" crcOut: "+Hex.encodeHexString(crcOut));

    }
}

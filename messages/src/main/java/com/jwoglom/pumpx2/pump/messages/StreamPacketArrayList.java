package com.jwoglom.pumpx2.pump.messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.models.UnexpectedOpCodeException;
import com.jwoglom.pumpx2.pump.messages.models.UnexpectedTransactionIdException;
import com.jwoglom.pumpx2.shared.L;
import com.jwoglom.pumpx2.shared.Hex;
import org.apache.commons.lang3.Validate;

public class StreamPacketArrayList extends PacketArrayList {
    private static final Logger log = LoggerFactory.getLogger(StreamPacketArrayList.class);
    private static final int HISTORY_LOG_STREAM_OPCODE = -127;

    private byte[] originalMessageData = new byte[0];

    // Streams sometimes(?) have transaction IDs
    public StreamPacketArrayList(byte expectedopCode, byte expectedCargoSize, byte expectedTxId, boolean isSigned) {
        super(expectedopCode, expectedCargoSize, expectedTxId, isSigned);
    }

    public void validatePacket(byte[] packetData) {
        Validate.notNull(packetData, "packetData");
        if (packetData.length == 0) {
            throw new IllegalArgumentException("Empty data");
        } else if (packetData.length >= 3) {
            byte firstByteMod15 = (byte) (packetData[0] & 15);
            byte secondByte = packetData[1];
            log.debug("Found tx id: "+secondByte+" expected "+this.expectedTxId);
            if (secondByte == this.expectedTxId) {
                if (this.empty) {
                    this.firstByteMod15 = firstByteMod15;
                    log.debug("txid matches, firstByteMod15="+firstByteMod15+", parsing packetData");
                    parse(packetData);
                } else if (((byte) 0) == firstByteMod15) {
                    log.debug("firstByteMod15=0");
                    if (this.firstByteMod15 == firstByteMod15) {
                        this.fullCargo = Bytes.combine(this.fullCargo, Bytes.dropFirstN(packetData, 2));
                        log.debug("firstByteMod15 matches expected, fullCargo="+ Hex.encodeHexString(fullCargo));
                    } else {
                        throw new IllegalArgumentException("Unexpected packets remaining 3: " + ((int) firstByteMod15) + ", expected " + ((int) this.firstByteMod15) + ", opCode: " + ((int) this.expectedOpCode));
                    }
                } else if (this.firstByteMod15 == firstByteMod15) {
                    this.fullCargo = Bytes.combine(this.fullCargo, Bytes.dropFirstN(packetData, 2));
                    log.debug("firstByteMod15 matches expected, nonzero, fullCargo=" + Hex.encodeHexString(fullCargo));
                } else {
                    throw new IllegalArgumentException("Unexpected packets remaining 2: " + ((int) firstByteMod15) + ", expected " + ((int) this.firstByteMod15) + ", opCode: " + ((int) this.expectedOpCode));
                }
                //if (!moreHistoryLogsRemaining()) {
                if (!needsMorePacket()) {
                    log.debug(String.format("Completed parsing historyLog: %s", Hex.encodeHexString(this.messageData)));
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
        log.debug(String.format("Parsing stream byte array opCode=%d cargoSize=%d: %s", bArr[2], bArr[4], Hex.encodeHexString(bArr)));
        byte opCode = bArr[2];
        byte cargoSize = bArr[4];
        if (opCode == this.expectedOpCode) {
            this.firstByteMod15 = (byte) (bArr[0] & 15);
            this.opCode = bArr[2];
            byte txId = bArr[3];

            if (txId != this.expectedTxId) {
                throw new UnexpectedTransactionIdException(txId, this.expectedTxId, this.expectedOpCode);
            } else if (cargoSize != this.actualExpectedCargoSize) {
                if (cargoSize == this.actualExpectedCargoSize + 24 && isSigned) {
                    log.debug("adding +24 expectedCargoSize for already signed request which contains an existing trailer");
                    this.expectedCargoSize += 24;
                    this.actualExpectedCargoSize += 24;
                } else if (opCode != HISTORY_LOG_STREAM_OPCODE) {
                    throw new IllegalArgumentException("Unexpected cargo size: " + ((int) cargoSize) + ", expecting " + ((int) this.actualExpectedCargoSize) + " for opCode="+opCode);
                }
            }
            this.fullCargo = Bytes.dropFirstN(bArr, 5);

            // specific checks for HistoryLog stream
            if ((byte) opCode == HISTORY_LOG_STREAM_OPCODE) {
                if (cargoSize <= 255) {
                    byte numHistoryLogs = bArr[5];
                    log.debug("StreamPacketArrayParse check: opCode="+opCode+" numHistoryLogs="+numHistoryLogs+" expHistoryLogs="+((numHistoryLogs * 26) + 2)+" cargoSize="+cargoSize);
                    log.debug("StreamPacketArrayParse bArr="+Hex.encodeHexString(bArr)+" oldFullCargo="+Hex.encodeHexString(fullCargo)+" newFullCargo="+Hex.encodeHexString(Bytes.dropFirstN(bArr, 5)));
                    if (cargoSize != (numHistoryLogs * 26) + 2) {
                        throw new IllegalArgumentException("Cargo size doesn't match number of history logs requested");
                    } else {
                        expectedCargoSize = cargoSize;
                    }
                } else {
                    throw new IllegalArgumentException("Cargo size beyond maximum: " + ((int) cargoSize));
                }

            }
        } else {
            throw new UnexpectedOpCodeException(opCode, this.expectedOpCode);
        }

        log.debug("StreamPacketArrayParse ok: opCode="+opCode+" firstByteMod15="+firstByteMod15+" cargoSize="+cargoSize);

    }

}

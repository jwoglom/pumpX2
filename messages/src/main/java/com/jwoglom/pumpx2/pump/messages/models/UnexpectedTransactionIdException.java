package com.jwoglom.pumpx2.pump.messages.models;


public class UnexpectedTransactionIdException extends RuntimeException {
    public int foundTxId;
    public UnexpectedTransactionIdException(int foundTxId, int expectedTxId, int expectedOpCode) {
        super("Unexpected transaction ID: " + foundTxId + ", expecting " + expectedTxId + ", opCode: " + expectedOpCode);
        this.foundTxId = foundTxId;
    }
}

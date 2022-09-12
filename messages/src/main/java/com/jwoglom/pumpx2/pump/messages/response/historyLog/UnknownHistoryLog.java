package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

public class UnknownHistoryLog extends HistoryLog {
    private int typeId = 0;
    public UnknownHistoryLog() {}

    public UnknownHistoryLog(byte[] raw) {
        this.cargo = raw;
        
    }

    public int typeId() {
        return typeId;
    }

    public void parse(byte[] raw) {
        this.cargo = raw;
        this.typeId = Bytes.readShort(raw, 0) & 4095;
        parseBase(raw);
    }
}
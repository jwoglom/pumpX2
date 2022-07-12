package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.collect.ImmutableSet;
import com.jwoglom.pumpx2.shared.JavaHelpers;

import java.util.HashSet;

public abstract class HistoryLog {
    protected byte[] cargo = null;

    public abstract int typeId();

    public abstract void parse(byte[] raw);

    public byte[] getCargo() {
        return this.cargo;
    }

    public String toString() {
        return JavaHelpers.autoToString(this, new HashSet<>());
    }

    public String verboseToString() {
        return JavaHelpers.autoToStringVerbose(this, ImmutableSet.of("cargo"));
    }
}

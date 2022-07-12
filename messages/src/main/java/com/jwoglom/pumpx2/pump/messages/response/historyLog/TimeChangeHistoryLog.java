package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.util.Arrays;

public class TimeChangeHistoryLog extends HistoryLog {
    private byte[] unknownFirst8Bytes;
    private long timePrior;
    private long timeAfter;
    private long rawRTC;
    private byte[] unknownLast6Bytes;

    public TimeChangeHistoryLog() {}

    public TimeChangeHistoryLog(byte[] unknownFirst8Bytes, long timePrior, long timeAfter, long rawRTC, byte[] unknownLast6Bytes) {
        this.cargo = buildCargo(unknownFirst8Bytes, timePrior, timeAfter, rawRTC, unknownLast6Bytes);
        this.unknownFirst8Bytes = unknownFirst8Bytes;
        this.timePrior = timePrior;
        this.timeAfter = timeAfter;
        this.rawRTC = rawRTC;
        this.unknownLast6Bytes = unknownLast6Bytes;
    }

    public TimeChangeHistoryLog(byte[] raw) {
        parse(raw);
    }

    @Override
    public int typeId() {
        return 13;
    }

    @Override
    public void parse(byte[] raw) {
        this.cargo = raw;
        this.unknownFirst8Bytes = Arrays.copyOfRange(raw, 0, 8);
        this.timePrior = Bytes.readUint32(raw, 8);
        this.timeAfter = Bytes.readUint32(raw, 12);
        this.rawRTC = Bytes.readUint32(raw, 16);
        this.unknownLast6Bytes = Arrays.copyOfRange(raw, 20, 26);
    }

    public static byte[] buildCargo(byte[] unknownFirst8Bytes, long timePrior, long timeAfter, long rawRTC, byte[] unknownLast6Bytes) {
        return Bytes.combine(
                unknownFirst8Bytes,
                Bytes.toUint32(timePrior),
                Bytes.toUint32(timeAfter),
                Bytes.toUint32(rawRTC),
                unknownLast6Bytes
        );
    }

    public long getTimePrior() {
        return timePrior;
    }

    public long getTimeAfter() {
        return timeAfter;
    }

    public long getRawRTC() {
        return rawRTC;
    }


}

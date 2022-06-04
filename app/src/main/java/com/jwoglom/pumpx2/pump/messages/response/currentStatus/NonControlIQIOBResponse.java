package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.NonControlIQIOBRequest;

@MessageProps(
        opCode=39,
        size=12,
        type=MessageType.RESPONSE,
        request= NonControlIQIOBRequest.class
)
public class NonControlIQIOBResponse extends Message {
    private long iob;
    private long timeRemaining;
    private long totalIOB;

    public NonControlIQIOBResponse() {}

    public NonControlIQIOBResponse(long iob, long timeRemaining, long totalIOB) {
        this.cargo = buildCargo(iob, timeRemaining, totalIOB);
        this.iob = iob;
        this.timeRemaining = timeRemaining;
        this.totalIOB = totalIOB;
    }

    private static byte[] buildCargo(long iob, long timeRemaining, long totalIOB) {
        return Bytes.combine(
                Bytes.toUint32(iob),
                Bytes.toUint32(timeRemaining),
                Bytes.toUint32(totalIOB));
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        iob = Bytes.readUint32(raw, 0);
        timeRemaining = Bytes.readUint32(raw, 4);
        totalIOB = Bytes.readUint32(raw, 8);
        cargo = raw;
    }

    public long getIOB() {
        return iob;
    }

    public long getTimeRemaining() {
        return timeRemaining;
    }

    public long getTotalIOB() {
        return totalIOB;
    }
}

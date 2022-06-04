package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ControlIQIOBRequest;

@MessageProps(
        opCode=109,
        size=17,
        type=MessageType.RESPONSE,
        request= ControlIQIOBRequest.class
)
public class ControlIQIOBResponse extends Message {

    public ControlIQIOBResponse() {}

    private long mudaliarIOB;
    private long timeRemainingSeconds;
    private long mudaliarTotalIOB;
    private long swan6hrIOB;
    private int iobType;

    public ControlIQIOBResponse(long mudaliarIOB, long timeRemainingSeconds, long mudaliarTotalIOB, long swan6hrIOB, int iobType) {
        this.cargo = buildCargo(mudaliarIOB, timeRemainingSeconds, mudaliarTotalIOB, swan6hrIOB, iobType);
        this.mudaliarIOB = mudaliarIOB;
        this.timeRemainingSeconds = timeRemainingSeconds;
        this.mudaliarTotalIOB = mudaliarTotalIOB;
        this.swan6hrIOB = swan6hrIOB;
        this.iobType = iobType;
    }

    private static byte[] buildCargo(long mudaliarIOB, long timeRemaining, long mudaliarTotalIOB, long swan6hrIOB, int iobType) {
        return Bytes.combine(
            Bytes.toUint32(mudaliarIOB),
            Bytes.toUint32(timeRemaining),
            Bytes.toUint32(mudaliarTotalIOB),
            Bytes.toUint32(swan6hrIOB),
            new byte[]{(byte) iobType});
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        mudaliarIOB = Bytes.readUint32(raw, 0);
        timeRemainingSeconds = Bytes.readUint32(raw, 4);
        mudaliarTotalIOB = Bytes.readUint32(raw, 8);
        swan6hrIOB = Bytes.readUint32(raw, 12);
        iobType = raw[16];
        cargo = raw;
    }

    public long getMudaliarIOB() {
        return mudaliarIOB;
    }

    public long getMudaliarTotalIOB() {
        return mudaliarTotalIOB;
    }

    public long getSwan6hrIOB() {
        return swan6hrIOB;
    }

    public long getTimeRemainingSeconds() {
        return timeRemainingSeconds;
    }

    public int getIOBTypeInt() {
        return iobType;
    }

    public enum IOBType {
        MUDALIAR(0),
        SWAN_6HR(1)

        ;

        private final int id;
        IOBType(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }

        public String toString() {
            return name();
        }
    }

    public IOBType getIOBType() {
        for (IOBType t : IOBType.values()) {
            if (t.id() == iobType) {
                return t;
            }
        }
        return null;
    }
}

package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CurrentEGVGuiDataRequest;

@MessageProps(
    opCode=35,
    size=8,
    type=MessageType.RESPONSE,
    request=CurrentEGVGuiDataRequest.class
)
public class CurrentEGVGuiDataResponse extends Message {
    
    private long bgReadingTimestampSeconds;
    private int cgmReading;
    private int egvStatus;
    private int trendRate;
    
    public CurrentEGVGuiDataResponse() {}
    
    public CurrentEGVGuiDataResponse(long bgReadingTimestampSeconds, int cgmReading, int egvStatus, int trendRate) {
        this.cargo = buildCargo(bgReadingTimestampSeconds, cgmReading, egvStatus, trendRate);
        this.bgReadingTimestampSeconds = bgReadingTimestampSeconds;
        this.cgmReading = cgmReading;
        this.egvStatus = egvStatus;
        this.trendRate = trendRate;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.bgReadingTimestampSeconds = Bytes.readUint32(raw, 0);
        this.cgmReading = Bytes.readShort(raw, 4);
        this.egvStatus = raw[6];
        this.trendRate = raw[7];
        
    }

    
    public static byte[] buildCargo(long bgReadingTimestampSeconds, int cgmReading, int egvStatus, int trendRate) {
        return Bytes.combine(
            Bytes.toUint32(bgReadingTimestampSeconds), 
            Bytes.firstTwoBytesLittleEndian(cgmReading), 
            new byte[]{ (byte) egvStatus }, 
            new byte[]{ (byte) trendRate });
    }
    
    public long getBgReadingTimestampSeconds() {
        return bgReadingTimestampSeconds;
    }
    public int getCgmReading() {
        return cgmReading;
    }
    public EGVStatus getEgvStatus() {
        return EGVStatus.fromId(egvStatus);
    }
    public int getTrendRate() {
        return trendRate;
    }

    public enum EGVStatus {
        INVALID(0),
        VALID(1),
        LOW(2),
        HIGH(3),
        UNAVAILABLE(4),
        ;

        private final int id;
        EGVStatus(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }

        public static EGVStatus fromId(int id) {
            for (EGVStatus e : values()) {
                if (e.id() == id) {
                    return e;
                }
            }
            return null;
        }
    }
    
}
package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CurrentEGVGuiDataRequest;

/**
 * Gets the current CGM reading
 */
@MessageProps(
    opCode=35,
    size=8,
    type=MessageType.RESPONSE,
    request=CurrentEGVGuiDataRequest.class
)
public class CurrentEGVGuiDataResponse extends Message {
    
    private long bgReadingTimestampSeconds;
    private int cgmReading;
    private int egvStatusId;
    private EGVStatus egvStatus;
    private int trendRate;
    
    public CurrentEGVGuiDataResponse() {}
    
    public CurrentEGVGuiDataResponse(long bgReadingTimestampSeconds, int cgmReading, int egvStatusId, int trendRate) {
        this.cargo = buildCargo(bgReadingTimestampSeconds, cgmReading, egvStatusId, trendRate);
        this.bgReadingTimestampSeconds = bgReadingTimestampSeconds;
        this.cgmReading = cgmReading;
        this.egvStatusId = egvStatusId;
        this.egvStatus = getEgvStatus();
        this.trendRate = trendRate;
        
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.bgReadingTimestampSeconds = Bytes.readUint32(raw, 0);
        this.cgmReading = Bytes.readShort(raw, 4);
        this.egvStatusId = raw[6];
        this.egvStatus = getEgvStatus();
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
        return EGVStatus.fromId(egvStatusId);
    }

    public int getEgvStatusId() {
        return egvStatusId;
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
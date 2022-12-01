package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.helpers.Dates;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.LastBGRequest;

import java.time.Instant;

/**
 * This message ONLY returns BGs entered via the Bolus Calculator, and NOT the most recent CGM
 * reading.
 */
@MessageProps(
    opCode=51,
    size=7,
    type=MessageType.RESPONSE,
    request=LastBGRequest.class
)
public class LastBGResponse extends Message {
    
    private long bgTimestamp;
    private int bgValue;
    private int bgSourceId;
    
    public LastBGResponse() {}
    
    public LastBGResponse(long bgTimestamp, int bgValue, int bgSourceId) {
        this.cargo = buildCargo(bgTimestamp, bgValue, bgSourceId);
        this.bgTimestamp = bgTimestamp;
        this.bgValue = bgValue;
        this.bgSourceId = bgSourceId;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.bgTimestamp = Bytes.readUint32(raw, 0);
        this.bgValue = Bytes.readShort(raw, 4);
        this.bgSourceId = raw[6];
        
    }

    
    public static byte[] buildCargo(long bgTimestamp, int bgValue, int bgSource) {
        return Bytes.combine(
            Bytes.toUint32(bgTimestamp), 
            Bytes.firstTwoBytesLittleEndian(bgValue), 
            new byte[]{ (byte) bgSource });
    }
    
    public long getBgTimestamp() {
        return bgTimestamp;
    }

    public Instant getBgTimestampInstant() {
        return Dates.fromJan12008EpochSecondsToDate(bgTimestamp);
    }

    public int getBgValue() {
        return bgValue;
    }
    public int getBgSourceId() {
        return bgSourceId;
    }
    public BgSource getBgSource() {
        return BgSource.fromId(bgSourceId);
    }

    public enum BgSource {
        // TODO: confirm
        MANUAL(0),
        CGM(1),
        ;

        private final int id;
        BgSource(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static BgSource fromId(int id) {
            for (BgSource b : values()) {
                if (b.id == id) {
                    return b;
                }
            }
            return null;
        }
    }
    
}
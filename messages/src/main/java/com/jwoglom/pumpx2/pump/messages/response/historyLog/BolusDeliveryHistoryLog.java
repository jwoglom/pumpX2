package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.util.Set;
import java.util.TreeSet;

@HistoryLogProps(
    opCode = 280,
    internalName = "LID_BOLUS_DELIVERY",
    usedByAndroid = true
)
public class BolusDeliveryHistoryLog extends HistoryLog {
    
    private int bolusID;
    private int bolusDeliveryStatusId;
    private int bolusTypeBitmask;
    private int bolusSource;
    private int reserved;
    private int requestedNow;
    private int requestedLater;
    private int correction;
    private int extendedDurationRequested;
    private int deliveredTotal;
    
    public BolusDeliveryHistoryLog() {}
    
    public BolusDeliveryHistoryLog(long pumpTimeSec, long sequenceNum, int bolusID, int bolusDeliveryStatusId, Set<BolusType> bolusTypes, BolusSource bolusSource, int reserved, int requestedNow, int requestedLater, int correction, int extendedDurationRequested, int deliveredTotal) {
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, bolusID, bolusDeliveryStatusId, BolusType.toBitmask(bolusTypes.toArray(new BolusType[]{})), bolusSource.id(), reserved, requestedNow, requestedLater, correction, extendedDurationRequested, deliveredTotal);
        this.pumpTimeSec = pumpTimeSec;
        this.sequenceNum = sequenceNum;
        this.bolusID = bolusID;
        this.bolusDeliveryStatusId = bolusDeliveryStatusId;
        this.bolusTypeBitmask = BolusType.toBitmask(bolusTypes.toArray(new BolusType[]{}));
        this.bolusSource = bolusSource.id();
        this.reserved = reserved;
        this.requestedNow = requestedNow;
        this.requestedLater = requestedLater;
        this.correction = correction;
        this.extendedDurationRequested = extendedDurationRequested;
        this.deliveredTotal = deliveredTotal;
        
    }

    public int typeId() {
        return 280;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.bolusID = Bytes.readShort(raw, 10);
        this.bolusDeliveryStatusId = raw[12];
        this.bolusTypeBitmask = raw[13];
        this.bolusSource = raw[14];
        this.reserved = raw[15];
        this.requestedNow = Bytes.readShort(raw, 16);
        this.requestedLater = Bytes.readShort(raw, 18);
        this.correction = Bytes.readShort(raw, 20);
        this.extendedDurationRequested = Bytes.readShort(raw, 22);
        this.deliveredTotal = Bytes.readShort(raw, 24);
    }

    
    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int bolusID, int bolusDeliveryStatus, int bolusType, int bolusSource, int reserved, int requestedNow, int requestedLater, int correction, int extendedDurationRequested, int deliveredTotal) {
        return Bytes.combine(
            new byte[]{24, 1}, // 280 across 2 bytes
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(bolusID), 
            new byte[]{ (byte) bolusDeliveryStatus }, 
            new byte[]{ (byte) bolusType }, 
            new byte[]{ (byte) bolusSource }, 
            new byte[]{ (byte) reserved },
            Bytes.firstTwoBytesLittleEndian(requestedNow),
            Bytes.firstTwoBytesLittleEndian(requestedLater), 
            Bytes.firstTwoBytesLittleEndian(correction), 
            Bytes.firstTwoBytesLittleEndian(extendedDurationRequested), 
            Bytes.firstTwoBytesLittleEndian(deliveredTotal));
    }
    
    public int getBolusID() {
        return bolusID;
    }
    public int getBolusDeliveryStatusId() {
        return bolusDeliveryStatusId;
    }
    public int getBolusTypeBitmask() {
        return bolusTypeBitmask;
    }
    public int getBolusSourceId() {
        return bolusSource;
    }
    public int getReserved() {
        return reserved;
    }
    public int getRequestedNow() {
        return requestedNow;
    }
    public int getRequestedLater() {
        return requestedLater;
    }
    public int getCorrection() {
        return correction;
    }
    public int getExtendedDurationRequested() {
        return extendedDurationRequested;
    }
    public int getDeliveredTotal() {
        return deliveredTotal;
    }

    public BolusSource getBolusSource() {
        return BolusSource.fromId(bolusSource);
    }

    public enum BolusSource {
        QUICK_BOLUS(0),
        GUI(1),
        CONTROL_IQ_AUTO_BOLUS(7),
        BLUETOOTH_REMOTE_BOLUS(8),

        ;
        private int id;
        BolusSource(int id) {
            this.id = id;
        }
        public int id() {
            return id;
        }
        public static BolusSource fromId(int id) {
            for (BolusSource b : values()) {
                if (b.id == id) return b;
            }
            return null;
        }
    }

    public Set<BolusType> getBolusTypes() {
        return BolusType.fromBitmask(getBolusTypeBitmask());
    }

    public enum BolusType {
        FOOD1(1), // used when there is carbs
        CORRECTION(2), // used when there is a correction amount
        EXTENDED(4),
        FOOD2(8), // used when there is no carbs and just a distinct bolus amount
        ;
        private int mask;
        BolusType(int mask) {
            this.mask = mask;
        }
        public int mask() {
            return mask;
        }
        public static Set<BolusType> fromBitmask(int bitmask) {
            Set<BolusType> ret = new TreeSet<>();
            for (BolusType b : values()) {
                if ((bitmask & b.mask()) != 0) {
                    ret.add(b);
                }
            }
            return ret;
        }
        public static int toBitmask(BolusType ...types) {
            int bitmask = 0;
            for (BolusType b : types) {
                bitmask |= b.mask();
            }
            return bitmask;
        }
    }
    
}
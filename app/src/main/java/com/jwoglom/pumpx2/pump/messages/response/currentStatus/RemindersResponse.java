package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.MinsTime;
import com.jwoglom.pumpx2.pump.messages.models.MultiDay;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.RemindersRequest;
import com.jwoglom.pumpx2.shared.JavaHelpers;

import java.util.HashSet;
import java.util.Set;

@MessageProps(
    opCode=89,
    size=105,
    type=MessageType.RESPONSE,
    request=RemindersRequest.class
)
public class RemindersResponse extends Message {
    
    private Reminder lowBGReminder;
    private Reminder highBGReminder;
    private Reminder siteChangeReminder;
    private Reminder missedBolusReminder0;
    private Reminder missedBolusReminder1;
    private Reminder missedBolusReminder2;
    private Reminder missedBolusReminder3;
    private Reminder afterBolusReminder;
    private Reminder additionalBolusReminder;
    private int lowBGThreshold;
    private int highBGThreshold;
    private int siteChangeDays;
    private int status;
    
    public RemindersResponse() {}
    
    public RemindersResponse(Reminder lowBGReminder, Reminder highBGReminder, Reminder siteChangeReminder, Reminder missedBolusReminder0, Reminder missedBolusReminder1, Reminder missedBolusReminder2, Reminder missedBolusReminder3, Reminder afterBolusReminder, Reminder additionalBolusReminder, int lowBGThreshold, int highBGThreshold, int siteChangeDays, int status) {
        this.cargo = buildCargo(lowBGReminder, highBGReminder, siteChangeReminder, missedBolusReminder0, missedBolusReminder1, missedBolusReminder2, missedBolusReminder3, afterBolusReminder, additionalBolusReminder, lowBGThreshold, highBGThreshold, siteChangeDays, status);
        this.lowBGReminder = lowBGReminder;
        this.highBGReminder = highBGReminder;
        this.siteChangeReminder = siteChangeReminder;
        this.missedBolusReminder0 = missedBolusReminder0;
        this.missedBolusReminder1 = missedBolusReminder1;
        this.missedBolusReminder2 = missedBolusReminder2;
        this.missedBolusReminder3 = missedBolusReminder3;
        this.afterBolusReminder = afterBolusReminder;
        this.additionalBolusReminder = additionalBolusReminder;
        this.lowBGThreshold = lowBGThreshold;
        this.highBGThreshold = highBGThreshold;
        this.siteChangeDays = siteChangeDays;
        this.status = status;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.lowBGReminder = new Reminder(raw, 0);
        this.highBGReminder = new Reminder(raw, 11);
        this.siteChangeReminder = new Reminder(raw, 22);
        this.missedBolusReminder0 = new Reminder(raw, 33);
        this.missedBolusReminder1 = new Reminder(raw, 44);
        this.missedBolusReminder2 = new Reminder(raw, 55);
        this.missedBolusReminder3 = new Reminder(raw, 66);
        this.afterBolusReminder = new Reminder(raw, 77);
        this.additionalBolusReminder = new Reminder(raw, 88);
        this.lowBGThreshold = Bytes.readShort(raw, 99);
        this.highBGThreshold = Bytes.readShort(raw, 101);
        this.siteChangeDays = raw[103];
        this.status = raw[104];
        
    }

    
    public static byte[] buildCargo(Reminder lowBGReminder, Reminder highBGReminder, Reminder siteChangeReminder, Reminder missedBolusReminder0, Reminder missedBolusReminder1, Reminder missedBolusReminder2, Reminder missedBolusReminder3, Reminder afterBolusReminder, Reminder additionalBolusReminder, int lowBGThreshold, int highBGThreshold, int siteChangeDays, int status)  {
        return Bytes.combine(
            lowBGReminder.buildCargo(),
            highBGReminder.buildCargo(),
            siteChangeReminder.buildCargo(),
            missedBolusReminder0.buildCargo(),
            missedBolusReminder1.buildCargo(),
            missedBolusReminder2.buildCargo(),
            missedBolusReminder3.buildCargo(),
            afterBolusReminder.buildCargo(),
            additionalBolusReminder.buildCargo(),
            Bytes.firstTwoBytesLittleEndian(lowBGThreshold), 
            Bytes.firstTwoBytesLittleEndian(highBGThreshold), 
            new byte[]{ (byte) siteChangeDays }, 
            new byte[]{ (byte) status });
    }
    
    public Reminder getLowBGReminder() {
        return lowBGReminder;
    }
    public Reminder getHighBGReminder() {
        return highBGReminder;
    }
    public Reminder getSiteChangeReminder() {
        return siteChangeReminder;
    }
    public Reminder getMissedBolusReminder0() {
        return missedBolusReminder0;
    }
    public Reminder getMissedBolusReminder1() {
        return missedBolusReminder1;
    }
    public Reminder getMissedBolusReminder2() {
        return missedBolusReminder2;
    }
    public Reminder getMissedBolusReminder3() {
        return missedBolusReminder3;
    }
    public Reminder getAfterBolusReminder() {
        return afterBolusReminder;
    }
    public Reminder getAdditionalBolusReminder() {
        return additionalBolusReminder;
    }
    public int getLowBGThreshold() {
        return lowBGThreshold;
    }
    public int getHighBGThreshold() {
        return highBGThreshold;
    }
    public int getSiteChangeDays() {
        return siteChangeDays;
    }
    public int getStatus() {
        return status;
    }

    public static class Reminder {
        private final long frequency;
        private final int startTime;
        private final int endTime;
        private final int activeDays;
        private final int enabled;
        private final int validityStatus;

        public Reminder(long frequency, int startTime, int endTime, int activeDays, int enabled, int validityStatus) {
            this.frequency = frequency;
            this.startTime = startTime;
            this.endTime = endTime;
            this.activeDays = activeDays;
            this.enabled = enabled;
            this.validityStatus = validityStatus; // TODO: figure out what this represents
        }

        public Reminder(byte[] cargo, int i) {
            this.frequency = Bytes.readUint32(cargo, i);
            this.startTime = Bytes.readShort(cargo, i+4);
            this.endTime = Bytes.readShort(cargo, i+6);
            this.activeDays = cargo[i+8];
            this.enabled = cargo[i+9];
            this.validityStatus = cargo[i+10];
        }

        public byte[] buildCargo() {
            return Bytes.combine(
                Bytes.toUint32(frequency),
                    Bytes.firstTwoBytesLittleEndian(startTime),
                    Bytes.firstTwoBytesLittleEndian(endTime),
                    new byte[]{ (byte) activeDays},
                    new byte[]{ (byte) enabled},
                    new byte[]{ (byte) validityStatus});
        }

        public long getFrequencyMins() {
            return frequency;
        }

        public MinsTime getFrequency() {
            return new MinsTime((int) frequency);
        }

        public int getStartTimeMins() {
            return startTime;
        }

        public MinsTime getStartTime() {
            return new MinsTime(startTime);
        }

        public int getEndTimeMins() {
            return endTime;
        }

        public MinsTime getEndTime() {
            return new MinsTime(endTime);
        }

        public int getActiveDaysBitmask() {
            return activeDays;
        }

        public Set<MultiDay> getActiveDays() {
            return MultiDay.fromBitmask(activeDays);
        }

        public int getEnabled() {
            return enabled;
        }

        public int getValidityStatus() {
            return validityStatus;
        }

        public String toString() {
            return JavaHelpers.autoToString(this, new HashSet<String>());
        }
    }
    
}
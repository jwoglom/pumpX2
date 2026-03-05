package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 29,
    displayName = "Reminder Dismissed",
    internalName = "LID_REMINDER_DISMISSED"
)
public class ReminderDismissedHistoryLog extends HistoryLog {

    private long reminderId;

    public ReminderDismissedHistoryLog() {}
    public ReminderDismissedHistoryLog(long pumpTimeSec, long sequenceNum, long reminderId) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, reminderId);
        this.reminderId = reminderId;
    }

    public ReminderDismissedHistoryLog(long reminderId) {
        this(0, 0, reminderId);
    }

    public int typeId() {
        return 29;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.reminderId = Bytes.readUint32(raw, 10);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long reminderId) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{29, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(reminderId)));
    }

    public long getReminderId() {
        return reminderId;
    }

}

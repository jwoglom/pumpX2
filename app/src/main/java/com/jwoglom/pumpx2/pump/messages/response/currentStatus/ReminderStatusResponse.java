package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ReminderStatusRequest;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

@MessageProps(
    opCode=73,
    size=8,
    type=MessageType.RESPONSE,
    request=ReminderStatusRequest.class
)
public class ReminderStatusResponse extends Message {
    
    private BigInteger reminderBitmask;
    
    public ReminderStatusResponse() {}
    
    public ReminderStatusResponse(BigInteger reminderBitmask) {
        this.cargo = buildCargo(reminderBitmask);
        this.reminderBitmask = reminderBitmask;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.reminderBitmask = Bytes.readUint64(raw, 0);
        
    }

    
    public static byte[] buildCargo(BigInteger reminderBitmask) {
        return Bytes.combine(
            Bytes.toUint64(reminderBitmask.longValue()));
    }
    
    public BigInteger getReminderBitmask() {
        return reminderBitmask;
    }


    public Set<ReminderType> getReminders() {
        return ReminderType.fromBitmask(getReminderBitmask());
    }

    public enum ReminderType {
        LOW_BG_REMINDER(0),
        HIGH_BG_REMINDER(1),
        SITE_CHANGE_REMINDER(2),
        MISSED_MEAL_REMINDER(3),
        MISSED_MEAL_REMINDER1(4),
        MISSED_MEAL_REMINDER2(5),
        MISSED_MEAL_REMINDER3(6),
        AFTER_BOLUS_BG_REMINDER(7),
        ADDITIONAL_BOLUS_REMINDER(8),
        DEFAULT_REMINDER_9(9),
        DEFAULT_REMINDER_10(10),
        DEFAULT_REMINDER_11(11),
        DEFAULT_REMINDER_12(12),
        DEFAULT_REMINDER_13(13),
        DEFAULT_REMINDER_14(14),
        DEFAULT_REMINDER_15(15),
        DEFAULT_REMINDER_16(16),
        DEFAULT_REMINDER_17(17),
        DEFAULT_REMINDER_18(18),
        DEFAULT_REMINDER_19(19),
        DEFAULT_REMINDER_20(20),
        DEFAULT_REMINDER_21(21),
        DEFAULT_REMINDER_22(22),
        DEFAULT_REMINDER_23(23),
        DEFAULT_REMINDER_24(24),
        DEFAULT_REMINDER_25(25),
        DEFAULT_REMINDER_26(26),
        DEFAULT_REMINDER_27(27),
        DEFAULT_REMINDER_28(28),
        DEFAULT_REMINDER_29(29),
        DEFAULT_REMINDER_30(30),
        DEFAULT_REMINDER_31(31),
        DEFAULT_REMINDER_32(32),
        DEFAULT_REMINDER_33(33),
        DEFAULT_REMINDER_34(34),
        DEFAULT_REMINDER_35(35),
        DEFAULT_REMINDER_36(36),
        DEFAULT_REMINDER_37(37),
        DEFAULT_REMINDER_38(38),
        DEFAULT_REMINDER_39(39),
        DEFAULT_REMINDER_40(40),
        DEFAULT_REMINDER_41(41),
        DEFAULT_REMINDER_42(42),
        DEFAULT_REMINDER_43(42),
        DEFAULT_REMINDER_44(44),
        DEFAULT_REMINDER_45(45),
        DEFAULT_REMINDER_46(46),
        DEFAULT_REMINDER_47(47),
        DEFAULT_REMINDER_48(48),
        DEFAULT_REMINDER_49(49),
        DEFAULT_REMINDER_50(50),
        DEFAULT_REMINDER_51(51),
        DEFAULT_REMINDER_52(52),
        DEFAULT_REMINDER_53(53),
        DEFAULT_REMINDER_54(54),
        DEFAULT_REMINDER_55(55),
        DEFAULT_REMINDER_56(56),
        DEFAULT_REMINDER_57(57),
        DEFAULT_REMINDER_58(58),
        DEFAULT_REMINDER_59(59),
        DEFAULT_REMINDER_60(60),
        DEFAULT_REMINDER_61(61),
        DEFAULT_REMINDER_62(62),
        DEFAULT_REMINDER_63(63),
        ;

        private final int id;
        ReminderType(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }

        public static Set<ReminderType> fromBitmask(BigInteger bitmask) {
            Set<ReminderType> set = new HashSet<>();
            for (ReminderType a : values()) {
                if (bitmask.testBit(a.id())) {
                    set.add(a);
                }
            }
            return set;
        }

        public static BigInteger toBitmask(ReminderType...alerts) {
            BigInteger i = BigInteger.ZERO;
            for (ReminderType a : alerts) {
                i = i.setBit(a.id());
            }
            return i;
        }
    }
    
}
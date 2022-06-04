package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.AlarmStatusRequest;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

@MessageProps(
        opCode=71,
        size=8,
        type=MessageType.RESPONSE,
        request= AlarmStatusRequest.class
)
public class AlarmStatusResponse extends Message {
    private BigInteger intMap;

    public AlarmStatusResponse() {}

    public AlarmStatusResponse(BigInteger intMap) {
        this.cargo = buildCargo(intMap);
        this.intMap = intMap;
    }

    private static byte[] buildCargo(BigInteger byte0uint64) {
        return Bytes.toUint64(byte0uint64.longValue());
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        intMap = Bytes.readUint64(raw, 0);
        cargo = raw;
    }

    public BigInteger getIntMap() {
        return intMap;
    }

    public enum AlarmResponseType {
        CARTRIDGE_ALARM(0),
        CARTRIDGE_ALARM2(1),
        OCCLUSION_ALARM(2),
        PUMP_RESET_ALARM(3),
        DEFAULT_ALARM_4(4),
        CARTRIDGE_ALARM3(5),
        CARTRIDGE_ALARM4(6),
        AUTO_OFF_ALARM(7),
        EMPTY_CARTRIDGE_ALARM(8),
        CARTRIDGE_ALARM5(9),
        TEMPERATURE_ALARM(10),
        TEMPERATURE_ALARM2(11),
        SHUTDOWN_ALARM(12),
        DEFAULT_ALARM_13(13),
        INVALID_DATE_ALARM(14),
        TEMPERATURE_ALARM3(15),
        CARTRIDGE_ALARM6(16),
        DEFAULT_ALARM_17(17),
        RESUME_PUMP_ALARM(18),
        DEFAULT_ALARM_19(19),
        CARTRIDGE_ALARM7(20),
        ALTITUDE_ALARM(21),
        BUTTON_ALARM(22),
        RESUME_PUMP_ALARM2(23),
        CARTRIDGE_ALARM8(24),
        CARTRIDGE_ALARM9(25),
        OCCLUSION_ALARM2(26),
        DEFAULT_ALARM_27(27),
        DEFAULT_ALARM_28(28),
        CARTRIDGE_ALARM10(29),
        CARTRIDGE_ALARM11(30),
        CARTRIDGE_ALARM12(31),
        DEFAULT_ALARM_32(32),
        DEFAULT_ALARM_33(33),
        DEFAULT_ALARM_34(34),
        DEFAULT_ALARM_35(35),
        DEFAULT_ALARM_36(36),
        DEFAULT_ALARM_37(37),
        DEFAULT_ALARM_38(38),
        DEFAULT_ALARM_39(39),
        DEFAULT_ALARM_40(40),
        DEFAULT_ALARM_41(41),
        DEFAULT_ALARM_42(42),
        DEFAULT_ALARM_43(43),
        DEFAULT_ALARM_44(44),
        DEFAULT_ALARM_45(45),
        DEFAULT_ALARM_46(46),
        DEFAULT_ALARM_47(47),
        DEFAULT_ALARM_48(48),
        DEFAULT_ALARM_49(49),
        DEFAULT_ALARM_50(50),
        DEFAULT_ALARM_51(51),
        DEFAULT_ALARM_52(52),
        DEFAULT_ALARM_53(53),
        DEFAULT_ALARM_54(54),
        DEFAULT_ALARM_55(55),
        DEFAULT_ALARM_56(56),
        DEFAULT_ALARM_57(57),
        DEFAULT_ALARM_58(58),
        DEFAULT_ALARM_59(59),
        DEFAULT_ALARM_60(60),
        DEFAULT_ALARM_61(61),
        DEFAULT_ALARM_62(62),
        DEFAULT_ALARM_63(63)

        ;

        private final int bitmask;
        AlarmResponseType(int bitmask) {
            this.bitmask = bitmask;
        }

        public int bitmask() {
            return bitmask;
        }

        public String toString() {
            return name();
        }
    }

    public Set<AlarmResponseType> getAlarms() {
        Set<AlarmResponseType> current = new HashSet<>();
        for (AlarmResponseType type : AlarmResponseType.values()) {
            if (intMap.testBit(type.bitmask())) {
                current.add(type);
            }
        }

        return current;
    }
}

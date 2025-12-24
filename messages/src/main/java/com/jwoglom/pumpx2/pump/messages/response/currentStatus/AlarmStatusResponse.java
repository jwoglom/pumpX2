package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.models.NotificationEnum;
import com.jwoglom.pumpx2.pump.messages.models.NotificationMessage;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.AlarmStatusRequest;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@MessageProps(
        opCode=71,
        size=8,
        type=MessageType.RESPONSE,
        request= AlarmStatusRequest.class
)
public class AlarmStatusResponse extends NotificationMessage {
    private BigInteger intMap;

    // private val unused, but placed to force java tostring to include the formatted alerts set
    private Set<AlarmResponseType> alarms;

    public AlarmStatusResponse() {}

    public AlarmStatusResponse(BigInteger intMap) {
        this.cargo = buildCargo(intMap);
        parse(cargo);
    }

    public AlarmStatusResponse(AlarmResponseType... alarms) {
        this.cargo = buildCargo(AlarmResponseType.toBitmask(alarms));
        parse(cargo);
    }

    private static byte[] buildCargo(BigInteger byte0uint64) {
        return Bytes.toUint64(byte0uint64.longValue());
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        intMap = Bytes.readUint64(raw, 0);
        cargo = raw;
        alarms = getAlarms();
    }

    public BigInteger getIntMap() {
        return intMap;
    }

    @Override
    public int size() {
        return alarms==null ? 0 : alarms.size();
    }

    public enum AlarmResponseType implements NotificationEnum {
        CARTRIDGE_ALARM(0, "There is an issue with the cartridge and it needs to be replaced."),
        CARTRIDGE_ALARM2(1, "There is an issue with the cartridge and it needs to be replaced."),
        OCCLUSION_ALARM(2, "An occlusion has occurred. Please check your pump site and tubing and restart insulin delivery."),
        PUMP_RESET_ALARM(3, "The pump was reset. IOB has been reset to 0 and CGM may need to be re-activated."),
        DEFAULT_ALARM_4(4),
        CARTRIDGE_ALARM3(5, "There is an issue with the cartridge and it needs to be replaced."),
        CARTRIDGE_ALARM4(6, "There is an issue with the cartridge and it needs to be replaced."),
        AUTO_OFF_ALARM(7, "Pump will stop delivering insulin automatically soon because no user activity has occurred and the auto-off setting is enabled."),
        EMPTY_CARTRIDGE_ALARM(8, "Cartridge is out of insulin and insulin delivery cannot occur. Please fill a new cartridge."),
        CARTRIDGE_ALARM5(9, "There is an issue with the cartridge and it needs to be replaced."),
        TEMPERATURE_ALARM(10, "Pump temperature is out of range and insulin cannot be safely delivered."),
        TEMPERATURE_ALARM2(11, "Pump temperature is out of range and insulin cannot be safely delivered."),
        BATTERY_SHUTDOWN_ALARM(12, "Pump battery level is critically low and the device will shut down. Please charge pump immediately."),
        DEFAULT_ALARM_13(13),
        INVALID_DATE_ALARM(14, "The pump's configured date is invalid."),
        TEMPERATURE_ALARM3(15, "Pump temperature is out of range and insulin cannot be safely delivered."),
        CARTRIDGE_ALARM6(16, "There is an issue with the cartridge and it needs to be replaced."),
        DEFAULT_ALARM_17(17),
        RESUME_PUMP_ALARM(18, "Insulin delivery is currently off. Please restart insulin delivery soon."),
        DEFAULT_ALARM_19(19),
        CARTRIDGE_ALARM7(20, "There is an issue with the cartridge and it needs to be replaced."),
        ALTITUDE_ALARM(21, "Pump altitude is out of range and insulin cannot be safely delivered."),
        STUCK_BUTTON_ALARM(22, "The pump button may be stuck or has been pressed for too long a period of time."),
        RESUME_PUMP_ALARM2(23, "Insulin delivery is currently off. Please restart insulin delivery soon."),
        ATMOSPHERIC_PRESSURE_OUT_OF_RANGE_ALARM(24, "Pump atmospheric pressure is out of range and insulin cannot be safely delivered."),
        CARTRIDGE_REMOVED_ALARM(25, "The cartridge was removed from the pump. Please fill a new cartridge."),
        OCCLUSION_ALARM2(26, "An occlusion has occurred. Please check your pump site and tubing and restart insulin delivery."),
        DEFAULT_ALARM_27(27),
        DEFAULT_ALARM_28(28),
        CARTRIDGE_ALARM10(29, "There is an issue with the cartridge and it needs to be replaced."),
        CARTRIDGE_ALARM11(30, "There is an issue with the cartridge and it needs to be replaced."),
        CARTRIDGE_ALARM12(31, "There is an issue with the cartridge and it needs to be replaced."),
        DEFAULT_ALARM_32(32),
        DEFAULT_ALARM_33(33),
        CARTRIDGE_ALARM_34(34, "There is an issue with the cartridge and it needs to be replaced."),
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
        private final String description;
        AlarmResponseType(int bitmask) {
            this.bitmask = bitmask;
            this.description = null;
        }
        AlarmResponseType(int bitmask, String description) {
            this.bitmask = bitmask;
            this.description = description;
        }

        public int bitmask() {
            return bitmask;
        }

        public int getId() {
            return bitmask;
        }
        public String getDescription() {
            return description;
        }

        public boolean isKnownAlarm() {
            return description != null && !description.isBlank();
        }

        public static BigInteger toBitmask(AlarmResponseType ...types) {
            BigInteger ret = BigInteger.ZERO;
            for (AlarmResponseType type : types) {
                ret = ret.setBit(type.bitmask());
            }

            return ret;
        }

        public static Set<AlarmResponseType> fromBitmask(BigInteger intMap) {
            Set<AlarmResponseType> current = new TreeSet<>();
            for (AlarmResponseType type : AlarmResponseType.values()) {
                if (intMap.testBit(type.bitmask())) {
                    current.add(type);
                }
            }

            return current;
        }

        public static AlarmResponseType fromSingularId(long id) {
            for (AlarmResponseType type : AlarmResponseType.values()) {
                if (id == type.bitmask()) {
                    return type;
                }
            }
            return null;
        }

        public String toString() {
            return name();
        }
    }

    public Set<AlarmResponseType> getAlarms() {
        return AlarmResponseType.fromBitmask(getIntMap());
    }


    @Override
    public Set<Integer> notificationIds() {
        Set<Integer> ids = new HashSet<>();

        for (AlarmResponseType alert : getAlarms()) {
            if (alert.isKnownAlarm()) {
                ids.add(alert.getId());
            }
        }

        return ids;
    }
}

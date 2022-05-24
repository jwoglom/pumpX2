package com.jwoglom.pumpx2.pump.messages.response;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.AlarmStatusRequest;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

@MessageProps(
        opCode=69,
        size=8,
        type=MessageType.RESPONSE,
        request= AlarmStatusRequest.class
)
public class AlertStatusResponse extends Message {
    private BigInteger intMap;

    public AlertStatusResponse() {}

    public AlertStatusResponse(BigInteger intMap) {
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

    public enum AlertResponseType {
        LOW_INSULIN_ALERT(0),
        USB_CONNECTION_ALERT(1),
        LOW_POWER_ALERT(2),
        LOW_POWER_ALERT2(3),
        DATA_ERROR_ALERT(4),
        AUTO_OFF_ALERT(5),
        MAX_BASAL_RATE_ALERT(6),
        POWER_SOURCE_ALERT(7),
        MIN_BASAL_ALERT(8),
        CONNECTION_ERROR_ALERT(9),
        CONNECTION_ERROR_ALERT2(10),
        INCOMPLETE_BOLUS_ALERT(11),
        INCOMPLETE_TEMP_RATE_ALERT(12),
        INCOMPLETE_CARTRIDGE_CHANGE_ALERT(13),
        INCOMPLETE_FILL_TUBING_ALERT(14),
        INCOMPLETE_FILL_CANNULA_ALERT(15),
        INCOMPLETE_SETTING_ALERT(16),
        LOW_INSULIN_ALERT2(17),
        MAX_BASAL_ALERT(18),
        LOW_TRANSMITTER_ALERT(19),
        TRANSMITTER_ALERT(20),
        DEFAULT_ALERT_21(21),
        SENSOR_EXPIRING_ALERT(22),
        PUMP_REBOOTING_ALERT(23),
        DEVICE_CONNECTION_ERROR(24),
        CGM_GRAPH_REMOVED(25),
        MIN_BASAL_ALERT2(26),
        INCOMPLETE_CALIBRATION(27),
        CALIBRATION_TIMEOUT(28),
        INVALID_TRANSMITTER_ID(29),
        DEFAULT_ALERT_30(30),
        DEFAULT_ALERT_32(32),
        BUTTON_ALERT(33),
        QUICK_BOLUS_ALERT(34),
        BASAL_IQ_ALERT(35),
        DEFAULT_ALERT_36(36),
        DEFAULT_ALERT_37(37),
        DEFAULT_ALERT_38(38),
        TRANSMITTER_END_OF_LIFE(39),
        CGM_ERROR(40),
        CGM_ERROR2(41),
        CGM_ERROR3(42),
        DEFAULT_ALERT_43(43),
        TRANSMITTER_EXPIRING_ALERT(44),
        TRANSMITTER_EXPIRING_ALERT2(45),
        TRANSMITTER_EXPIRING_ALERT3(46),
        DEFAULT_ALERT_47(47),
        CGM_UNAVAILABLE(48),
        DEFAULT_ALERT_49(49),
        DEFAULT_ALERT_50(50),
        DEFAULT_ALERT_51(51),
        DEFAULT_ALERT_52(52),
        DEFAULT_ALERT_53(53),
        DEVICE_PAIRED(54),
        DEFAULT_ALERT_55(55),
        DEFAULT_ALERT_56(56),
        DEFAULT_ALERT_57(57),
        DEFAULT_ALERT_58(58),
        DEFAULT_ALERT_59(59),
        DEFAULT_ALERT_60(60),
        DEFAULT_ALERT_61(61),
        DEFAULT_ALERT_62(62),
        DEFAULT_ALERT_63(63)
        ;

        private final int bitmask;
        AlertResponseType(int bitmask) {
            this.bitmask = bitmask;
        }

        public int bitmask() {
            return bitmask;
        }

        public String toString() {
            return name();
        }

        public BigInteger withBit() {
            return BigInteger.ZERO.setBit(bitmask);
        }
    }

    public Set<AlertResponseType> getAlerts() {
        Set<AlertResponseType> current = new HashSet<>();
        for (AlertResponseType type : AlertResponseType.values()) {
            if (intMap.testBit(type.bitmask())) {
                current.add(type);
            }
        }

        return current;
    }
}

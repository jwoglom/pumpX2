package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import androidx.annotation.Nullable;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.models.NotificationEnum;
import com.jwoglom.pumpx2.pump.messages.models.NotificationMessage;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CGMAlertStatusRequest;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@MessageProps(
    opCode=75,
    size=8,
    type=MessageType.RESPONSE,
    request=CGMAlertStatusRequest.class
)
public class CGMAlertStatusResponse extends NotificationMessage {
    
    private BigInteger intMap;
    
    public CGMAlertStatusResponse() {}
    
    public CGMAlertStatusResponse(long cgmAlertBitmask) {
        this.cargo = buildCargo(cgmAlertBitmask);
        this.intMap = BigInteger.valueOf(cgmAlertBitmask);
        
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.intMap = Bytes.readUint64(raw, 0);
        
    }

    
    public static byte[] buildCargo(long cgmAlertBitmask) {
        return Bytes.combine(
            Bytes.toUint64(cgmAlertBitmask));
    }
    
    public BigInteger getIntMap() {
        return intMap;
    }
    public long getBitMap() {
        return intMap.longValue();
    }

    public Set<CGMAlert> getCgmAlerts() {
        return CGMAlert.fromBitmask(intMap);
    }

    @Override
    public Set<Integer> notificationIds() {
        return getCgmAlerts()
            .stream()
            .filter(CGMAlert::isKnown)
            .map(CGMAlert::getId)
            .collect(Collectors.toSet());
    }

    @Override
    public int size() {
        return getCgmAlerts().size();
    }

    public enum CGMAlert implements NotificationEnum {
        /** Tandem firmware name: Alert_CGM_DEPRECATED_3 */
        DEFAULT_CGM_ALERT_0(0),
        /** Tandem firmware name: Alert_CGM_FIXED_LOW */
        FIXED_LOW_CGM_ALERT(1),
        /** Tandem firmware name: Alert_CGM_USER_HIGH_EGV */
        HIGH_CGM_ALERT(2),
        /** Tandem firmware name: Alert_CGM_USER_LOW_EGV */
        LOW_CGM_ALERT(3),
        /** Tandem firmware name: Alert_CGM_CALIBRATION_REQUEST */
        CALIBRATION_REQUEST_CGM_ALERT(4),
        /** Tandem firmware name: Alert_CGM_RISE */
        RISE_CGM_ALERT(5),
        /** Tandem firmware name: Alert_CGM_RAPID_RISE */
        RAPID_RISE_CGM_ALERT(6),
        /** Tandem firmware name: Alert_CGM_FALL */
        FALL_CGM_ALERT(7),
        /** Tandem firmware name: Alert_CGM_RAPID_FALL */
        RAPID_FALL_CGM_ALERT(8),
        /** Tandem firmware name: Alert_CGM_CALIB_ERROR */
        LOW_CALIBRATION_ERROR_CGM_ALERT(9),
        /** Tandem firmware name: Alert_CGM_CALIB_HIGH_WEDGE */
        HIGH_CALIBRATION_ERROR_CGM_ALERT(10),
        /** Tandem firmware name: Alert_CGM_SENSOR_FAILED */
        SENSOR_FAILED_CGM_ALERT(11),
        /** Tandem firmware name: Alert_CGM_SENSOR_EXPIRING */
        SENSOR_EXPIRING_CGM_ALERT(12),
        /** Tandem firmware name: Alert_CGM_SENSOR_EXPIRED */
        SENSOR_EXPIRED_CGM_ALERT(13),
        /** Tandem firmware name: Alert_CGM_OUT_OF_RANGE */
        OUT_OF_RANGE_CGM_ALERT(14),
        /** Tandem firmware name: Alert_CGM_DEPRECATED */
        DEFAULT_CGM_ALERT_15(15),
        /** Tandem firmware name: Alert_CGM_CALIB_START_FIRST */
        FIRST_START_CALIBRATION_CGM_ALERT(16),
        /** Tandem firmware name: Alert_CGM_CALIB_START_SECOND */
        SECOND_START_CALIBRATION_CGM_ALERT(17),
        /** Tandem firmware name: Alert_CGM_CALIB_REQUIRED */
        CALIBRATION_REQUIRED_CGM_ALERT(18),
        /** Tandem firmware name: Alert_CGM_LOW_TX_BATTERY */
        LOW_TRANSMITTER_CGM_ALERT(19),
        /** Tandem firmware name: Alert_CGM_TRANSMITTER_ERROR */
        TRANSMITTER_CGM_ALERT(20),
        /** Tandem firmware name: Alert_CGM_DEPRECATED_2 */
        DEFAULT_CGM_ALERT_21(21),
        /** Tandem firmware name: Alert_CGM_SENSOR_EXPIRING_SOON */
        SENSOR_EXPIRING_CGM_ALERT2(22),
        /** Tandem firmware name: Alert_CGM_DEPRECATED_4 */
        DEFAULT_CGM_ALERT_23(23),
        /** Tandem firmware name: Alert_CGM_URGENT_LOW_SOON */
        URGENT_LOW_SOON_CGM_ALERT(24),
        /** Tandem firmware name: Alert_CGM_SENSOR_REUSE */
        SENSOR_REUSE(25),
        /** Tandem firmware name: Alert_CGM_SENSOR_TEMP */
        TEMPERATURE_CGM_ALERT(26),
        /** Tandem firmware name: Alert_CGM_CONNECTION_ERROR */
        FAILED_CONNECTION_CGM_ALERT(27),
        /** Tandem firmware name: Alert_CGM_CHECK_SENSOR */
        CHECK_SENSOR_CGM_ALERT(28),
        /** Tandem firmware name: Alert_CGM_EGV_UNUSABLE */
        EGV_UNUSABLE_CGM_ALERT(29),
        /** Tandem firmware name: Alert_CGM_UNUSED1 */
        DEFAULT_CGM_ALERT_30(30),
        /** Tandem firmware name: Alert_CGM_GRACE_PERIOD_START */
        GRACE_PERIOD_START_CGM_ALERT(31),
        /** Tandem firmware name: Alert_CGM_GRACE_PERIOD_EXPIRING_SOON */
        GRACE_PERIOD_EXPIRING_SOON_CGM_ALERT(32),
        /** Tandem firmware name: NUM_CGM_ALERTS */
        DEFAULT_CGM_ALERT_33(33),
        DEFAULT_CGM_ALERT_34(34),
        DEFAULT_CGM_ALERT_35(35),
        DEFAULT_CGM_ALERT_36(36),
        DEFAULT_CGM_ALERT_37(37),
        DEFAULT_CGM_ALERT_38(38),
        TRANSMITTER_EXPIRED_CGM_ALERT(39),
        PUMP_BLUETOOTH_ERROR_CGM_ALERT(40),
        DEFAULT_CGM_ALERT_41(41),
        DEFAULT_CGM_ALERT_42(42),
        DEFAULT_CGM_ALERT_43(43),
        DEFAULT_CGM_ALERT_44(44),
        // Names derived from tconnectsync's cloud-export dictionaries (CGM_ALERTS_DICT).
        TRANSMITTER_EXPIRING_CGM_ALERT(45), // CGM_ALERTS_DICT["45"] == "CGM Transmitter Expiring Soon"
        TRANSMITTER_EXPIRING_CGM_ALERT2(46), // CGM_ALERTS_DICT["46"] == "CGM Transmitter Expiring 2"
        DEFAULT_CGM_ALERT_47(47),
        UNAVAILABLE_CGM_ALERT(48), // CGM_ALERTS_DICT["48"] == "CGM Unavailable"
        DEFAULT_CGM_ALERT_49(49),
        DEFAULT_CGM_ALERT_50(50),
        DEFAULT_CGM_ALERT_51(51),
        DEFAULT_CGM_ALERT_52(52),
        DEFAULT_CGM_ALERT_53(53),
        DEFAULT_CGM_ALERT_55(55),
        DEFAULT_CGM_ALERT_56(56),
        DEFAULT_CGM_ALERT_57(57),
        DEFAULT_CGM_ALERT_58(58),
        DEFAULT_CGM_ALERT_59(59),
        DEFAULT_CGM_ALERT_60(60),
        DEFAULT_CGM_ALERT_61(61),
        DEFAULT_CGM_ALERT_62(62),
        DEFAULT_CGM_ALERT_63(63),

        ;

        private final int id;
        CGMAlert(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }

        @Override
        public int getId() {
            return id;
        }

        public String toString() {
            return name();
        }

        public boolean isKnown() {
            return !name().startsWith("DEFAULT_CGM_ALERT");
        }

        @Nullable
        @Override
        public String getDescription() {
            if (!isKnown()) return null;
            return name();
        }

        public static CGMAlert fromId(int id) {
            for (CGMAlert alert : values()) {
                if (alert.id() == id) {
                    return alert;
                }
            }
            return null;
        }

        public static Set<CGMAlert> fromBitmask(BigInteger bitmask) {
            Set<CGMAlert> set = new TreeSet<>();
            for (CGMAlert a : values()) {
                if (bitmask.testBit(a.id())) {
                    set.add(a);
                }
            }
            return set;
        }

        public static BigInteger toBitmask(CGMAlert ...alerts) {
            BigInteger i = BigInteger.ZERO;
            for (CGMAlert a : alerts) {
                i.setBit(a.id());
            }
            return i;
        }
    }
    
}

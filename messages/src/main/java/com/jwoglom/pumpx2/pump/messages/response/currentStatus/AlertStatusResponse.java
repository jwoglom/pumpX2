package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.models.NotificationEnum;
import com.jwoglom.pumpx2.pump.messages.models.NotificationMessage;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.AlarmStatusRequest;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@MessageProps(
        opCode=69,
        size=8,
        type=MessageType.RESPONSE,
        request= AlarmStatusRequest.class
)
public class AlertStatusResponse extends NotificationMessage {
    private BigInteger intMap;

    // private val unused, but placed to force java tostring to include the formatted alerts set
    private Set<AlertResponseType> alerts;

    public AlertStatusResponse() {}

    public AlertStatusResponse(BigInteger intMap) {
        this.cargo = buildCargo(intMap);
        parse(cargo);
    }

    public AlertStatusResponse(byte[] raw) {
        parse(raw);
    }

    private static byte[] buildCargo(BigInteger byte0uint64) {
        return Bytes.toUint64(byte0uint64.longValue());
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        intMap = Bytes.readUint64(raw, 0);
        cargo = raw;
        alerts = getAlerts();
    }

    public BigInteger getIntMap() {
        return intMap;
    }
    public long getBitMap() {
        return intMap.longValue();
    }


    @Override
    public int size() {
        return alerts==null ? 0 : alerts.size();
    }

    public enum AlertResponseType implements NotificationEnum {
        LOW_INSULIN_ALERT(0, "Low amount of insulin remaining in the cartridge."),
        USB_CONNECTION_ALERT(1, "Pump is not charging over USB."),
        LOW_POWER_ALERT(2, "Power level is low and the pump needs to be charged."),
        LOW_POWER_ALERT2(3, "Power level is low and the pump needs to be charged."),
        DATA_ERROR_ALERT(4),
        AUTO_OFF_ALERT(5, "The pump is about to turn off due to the configured auto-off interval."),
        MAX_BASAL_RATE_ALERT(6, "The pump is delivering at the maximum allowed basal rate."),
        POWER_SOURCE_ALERT(7, "The power source provided is not able to charge the pump."),
        MIN_BASAL_ALERT(8, "The pump is delivering at the minimum allowed basal rate."),
        CONNECTION_ERROR_ALERT(9),
        CONNECTION_ERROR_ALERT2(10),
        INCOMPLETE_BOLUS_ALERT(11, "The bolus window was opened but a bolus was not started."),
        INCOMPLETE_TEMP_RATE_ALERT(12, "The temp rate window was opened but the temp rate was not started."),
        INCOMPLETE_CARTRIDGE_CHANGE_ALERT(13, "The cartridge change was started but not completed."),
        INCOMPLETE_FILL_TUBING_ALERT(14, "Fill tubing was started but not completed."),
        INCOMPLETE_FILL_CANNULA_ALERT(15, "Fill cannula was started but not completed."),
        INCOMPLETE_SETTING_ALERT(16),
        LOW_INSULIN_ALERT2(17, "Low amount of insulin remaining in the cartridge."),
        MAX_BASAL_ALERT(18, "The maxmium basal was reached."),
        LOW_TRANSMITTER_ALERT(19, "The CGM transmitter battery is low."),
        TRANSMITTER_ALERT(20, "There is an alert from the CGM transmitter."),
        DEFAULT_ALERT_21(21),
        SENSOR_EXPIRING_ALERT(22, "The CGM sensor is expiring soon."),
        PUMP_REBOOTING_ALERT(23, "The pump is rebooting."),
        DEVICE_CONNECTION_ERROR(24),
        CGM_GRAPH_REMOVED(25,
                "It has been 24 hours since your last sensor session ended, so your " +
                          "current glucose reading now displays the last glucose value entered in the bolus calculator."),
        MIN_BASAL_ALERT2(26, "The pump is delivering at the minimum allowed basal rate."),
        INCOMPLETE_CALIBRATION(27, "The CGM calibration was incomplete."),
        CALIBRATION_TIMEOUT(28, "The timeout was reached for CGM calibration."),
        INVALID_TRANSMITTER_ID(29,
                "The Dexcom G6 or G7 transmitter ID is invalid."),
        DEFAULT_ALERT_30(30),
        DEFAULT_ALERT_32(32),
        BUTTON_ALERT(33, "The pump button was held down for too long and is temporarily disabled to avoid accidental delivery of insulin."),
        QUICK_BOLUS_ALERT(34, "Quick bolus mode was entered but no bolus was started."),
        BASAL_IQ_ALERT(35, "BasalIQ has reduced basal insulin to avoid a low."),
        DEFAULT_ALERT_36(36),
        DEFAULT_ALERT_37(37),
        DEFAULT_ALERT_38(38),
        TRANSMITTER_END_OF_LIFE(39, "The CGM transmitter is reaching its end of life and cannot be used."),
        CGM_ERROR(40, "CGM error reported"),
        CGM_ERROR2(41, "CGM error reported"),
        CGM_ERROR3(42, "CGM error reported"),
        DEFAULT_ALERT_43(43),
        TRANSMITTER_EXPIRING_ALERT(44, "The CGM transmitter is expiring soon."),
        TRANSMITTER_EXPIRING_ALERT2(45, "The CGM transmitter is expiring soon."),
        TRANSMITTER_EXPIRING_ALERT3(46, "The CGM transmitter is expiring soon."),
        DEFAULT_ALERT_47(47),
        CGM_UNAVAILABLE(48, "The CGM is unavailable due to a problem with the sensor."),
        FILL_TUBING_STILL_IN_PROGRESS(49, "The fill tubing process is still in progress and was not completed."),
        DEFAULT_ALERT_50(50),
        DEFAULT_ALERT_51(51),
        DEFAULT_ALERT_52(52),
        DEFAULT_ALERT_53(53),
        DEVICE_PAIRED(54, "The pump was paired successfully to a Bluetooth device."),
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
        private final String description;
        AlertResponseType(int bitmask) {
            this.bitmask = bitmask;
            this.description = null;
        }

        AlertResponseType(int bitmask, String description) {
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

        public boolean isKnownAlert() {
            return description != null && !description.isBlank();
        }

        public String toString() {
            return name();
        }

        public BigInteger withBit() {
            return BigInteger.ZERO.setBit(bitmask);
        }

        public static AlertResponseType fromSingularId(long id) {
            for (AlertResponseType type : values()) {
                if (type.bitmask == id) {
                    return type;
                }
            }
            return null;
        }
    }

    public Set<AlertResponseType> getAlerts() {
        Set<AlertResponseType> current = new TreeSet<>();
        for (AlertResponseType type : AlertResponseType.values()) {
            if (intMap.testBit(type.bitmask())) {
                current.add(type);
            }
        }

        return current;
    }

    @Override
    public Set<Integer> notificationIds() {
        Set<Integer> ids = new HashSet<>();

        for (AlertResponseType alert : getAlerts()) {
            if (alert.isKnownAlert()) {
                ids.add(alert.getId());
            }
        }

        return ids;
    }
}

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
        /** Tandem firmware name: oaAlert_LIQ_LOW */
        LOW_INSULIN_ALERT(0, "Low amount of insulin remaining in the cartridge."),
        /** Tandem firmware name: oaAlert_USB_INSUFF_CURRENT */
        USB_CONNECTION_ALERT(1, "Pump is not charging over USB."),
        /** Tandem firmware name: aaAlert_LIPO_LOW */
        LOW_POWER_ALERT(2, "Power level is low and the pump needs to be charged."),
        /** Tandem firmware name: aaAlert_LIPO_VERY_LOW */
        LOW_POWER_ALERT2(3, "Power level is low and the pump needs to be charged."),
        /** Tandem firmware name: oaAlert_NVM */
        DATA_ERROR_ALERT(4),
        /** Tandem firmware name: oaAlert_AUTO_OFF */
        AUTO_OFF_ALERT(5, "The pump is about to turn off due to the configured auto-off interval."),
        /** Tandem firmware name: oaAlert_BASAL_RATE_LIMIT */
        MAX_BASAL_RATE_ALERT(6, "The pump is delivering at the maximum allowed basal rate."),
        /** Tandem firmware name: oaAlert_LIPO_CANT_CHARGE */
        POWER_SOURCE_ALERT(7, "The power source provided is not able to charge the pump."),
        /** Tandem firmware name: oaAlert_BASAL_RATE_TOO_LOW */
        MIN_BASAL_ALERT(8, "The pump is delivering at the minimum allowed basal rate."),
        /** Tandem firmware name: aaAlert_USB_NOT_RECOGNIZED */
        CONNECTION_ERROR_ALERT(9),
        /** Tandem firmware name: aaAlert_BAD_PC_COM */
        CONNECTION_ERROR_ALERT2(10),
        /** Tandem firmware name: oaAlert_INCOMPLETE_BOLUS */
        INCOMPLETE_BOLUS_ALERT(11, "The bolus window was opened but a bolus was not started."),
        /** Tandem firmware name: oaAlert_INCOMPLETE_TEMP_RATE */
        INCOMPLETE_TEMP_RATE_ALERT(12, "The temp rate window was opened but the temp rate was not started."),
        /** Tandem firmware name: oaAlert_INCOMPLETE_CARTRIDGE */
        INCOMPLETE_CARTRIDGE_CHANGE_ALERT(13, "The cartridge change was started but not completed."),
        /** Tandem firmware name: oaAlert_INCOMPLETE_TUBING */
        INCOMPLETE_FILL_TUBING_ALERT(14, "Fill tubing was started but not completed."),
        /** Tandem firmware name: oaAlert_INCOMPLETE_CANNULA */
        INCOMPLETE_FILL_CANNULA_ALERT(15, "Fill cannula was started but not completed."),
        /** Tandem firmware name: oaAlert_INCOMPLETE_SETTING */
        INCOMPLETE_SETTING_ALERT(16),
        /** Tandem firmware name: oaAlert_LIQ_VERY_LOW */
        LOW_INSULIN_ALERT2(17, "Low amount of insulin remaining in the cartridge."),
        /** Tandem firmware name: oaAlert_ABS_MAX_BASAL_LIMIT */
        MAX_BASAL_ALERT(18, "The maxmium basal was reached."),
        /** Tandem firmware name: aaAlert_STUCK_BUTTON */
        LOW_TRANSMITTER_ALERT(19, "The CGM transmitter battery is low."),
        /** Tandem firmware name: aaAlert_CGM_SUPPORT_PACKAGE_ERROR */
        TRANSMITTER_ALERT(20, "There is an alert from the CGM transmitter."),
        /** Tandem firmware name: aaAlert_FSL2_SUPPORT_PACKAGE_ERROR */
        FSL2_SUPPORT_PACKAGE_ERROR_ALERT(21),
        /** Tandem firmware name: aaAlert_STK_CSP_REQ */
        SENSOR_EXPIRING_ALERT(22, "The CGM sensor is expiring soon."),
        /** Tandem firmware name: oaAlert_USB_SHUTDOWN */
        PUMP_REBOOTING_ALERT(23, "The pump is rebooting."),
        /** Tandem firmware name: aaAlert_CGM_MANY_SENSORS */
        DEVICE_CONNECTION_ERROR(24),
        /** Tandem firmware name: aaAlert_CGM_GRAPH_REMOVAL */
        CGM_GRAPH_REMOVED(25,
                "It has been 24 hours since your last sensor session ended, so your " +
                          "current glucose reading now displays the last glucose value entered in the bolus calculator."),
        /** Tandem firmware name: oaAlert_BASAL_RATE_LOW */
        MIN_BASAL_ALERT2(26, "The pump is delivering at the minimum allowed basal rate."),
        /** Tandem firmware name: aaAlert_CGM_INCOMPLETE_CAL */
        INCOMPLETE_CALIBRATION(27, "The CGM calibration was incomplete."),
        /** Tandem firmware name: aaAlert_CGM_CAL_TIMEOUT */
        CALIBRATION_TIMEOUT(28, "The timeout was reached for CGM calibration."),
        /** Tandem firmware name: aaAlert_CGM_INVALID_TX_ID */
        INVALID_TRANSMITTER_ID(29,
                "The Dexcom G6 or G7 transmitter ID is invalid."),
        /** Tandem firmware name: aaAlert_DEBUG_INFO_DECLARED */
        DEBUG_INFO_DECLARED_ALERT(30),
        /** Tandem firmware name: oaAlert_OPEN_SESAME */
        OPEN_SESAME_ALERT(32),
        /** Tandem firmware name: aaAlert_QUICK_BOLUS_ABORT */
        BUTTON_ALERT(33, "The pump button was held down for too long and is temporarily disabled to avoid accidental delivery of insulin."),
        /** Tandem firmware name: aaAlert_MANY_FAILED_QUICK_BOLUS */
        QUICK_BOLUS_ALERT(34, "Quick bolus mode was entered but no bolus was started."),
        /** Tandem firmware name: Alert_AP_ALERT */
        BASAL_IQ_ALERT(35, "BasalIQ has reduced basal insulin to avoid a low."),
        /** Tandem firmware name: aaAlert_STK_EXCHANGE_REQ */
        STK_EXCHANGE_REQ_ALERT(36),
        /** Tandem firmware name: aaAlert_STK_ODYSSEY_REQ */
        STK_ODYSSEY_REQ_ALERT(37),
        /** Tandem firmware name: aaAlert_STK_INVALID */
        STK_INVALID_ALERT(38),
        /** Tandem firmware name: aaAlert_CGM_TX_EOL */
        TRANSMITTER_END_OF_LIFE(39, "The CGM transmitter is reaching its end of life and cannot be used."),
        /** Tandem firmware name: aaAlert_CGM_BLE_SD */
        CGM_ERROR(40, "CGM error reported"),
        /** Tandem firmware name: aaAlert_CGM_BLE_HW_WATCHDOG */
        CGM_ERROR2(41, "CGM error reported"),
        /** Tandem firmware name: aaAlert_CGM_BLE_HW_TIMEOUT */
        CGM_ERROR3(42, "CGM error reported"),
        /** Tandem firmware name: aaAlert_CGM_INCOMPATIBLE */
        CGM_INCOMPATIBLE_ALERT(43),
        /** Tandem firmware name: aaAlert_CGM_TX_EXPIRING_SOON_1 */
        TRANSMITTER_EXPIRING_ALERT(44, "The CGM transmitter is expiring soon."),
        /** Tandem firmware name: aaAlert_CGM_TX_EXPIRING_SOON_2 */
        TRANSMITTER_EXPIRING_ALERT2(45, "The CGM transmitter is expiring soon."),
        /** Tandem firmware name: aaAlert_CGM_TX_EXPIRING_STARTUP */
        TRANSMITTER_EXPIRING_ALERT3(46, "The CGM transmitter is expiring soon."),
        /** Tandem firmware name: oaAlert_TZ_INCOMPLETE_SETTING */
        TZ_INCOMPLETE_SETTING_ALERT(47),
        /** Tandem firmware name: aaAlert_CGM_NO_READINGS */
        CGM_UNAVAILABLE(48, "The CGM is unavailable due to a problem with the sensor."),
        /** Tandem firmware name: aaAlert_PRIME_LIMIT */
        FILL_TUBING_STILL_IN_PROGRESS(49, "The fill tubing process is still in progress and was not completed."),
        /** Tandem firmware name: aaAlert_TZ_HYPER_LIGHT */
        TZ_HYPER_LIGHT_ALERT(50),
        /** Tandem firmware name: aaAlert_TZ_HYPO_LIGHT */
        CONTROL_IQ_LOW(51, "Control-IQ has reduced basal insulin delivery due to a low or predicted low glucose value."),
        /** Tandem firmware name: aaAlert_TZ_IIDM */
        TZ_IIDM_ALERT(52),
        /** Tandem firmware name: aaAlert_BLE_CRASH_DUMP */
        BLE_CRASH_DUMP_ALERT(53),
        /** Tandem firmware name: aaAlert_MH_PAIRED */
        DEVICE_PAIRED(54, "The pump was paired successfully to a Bluetooth device."),
        /** Tandem firmware name: aaAlert_BLE_HW_ERROR */
        BLE_HW_ERROR_ALERT(55),
        /** Tandem firmware name: aaAlert_USER_SET_MAX_BASAL */
        USER_SET_MAX_BASAL_ALERT(56),
        /** Tandem firmware name: aaAlert_SENSOR */
        SENSOR_ALERT(57),
        /** Tandem firmware name: aaAlert_COM_LOG_ERROR_LOGGED */
        COM_LOG_ERROR_LOGGED_ALERT(58),
        /** Tandem firmware name: alert_FCL_AUTOMATION_OFF */
        FCL_AUTOMATION_OFF_ALERT(59),
        /** Tandem firmware name: oaAlert_CONTROLLER_CONNECTION_LOST */
        CONTROLLER_CONNECTION_LOST_ALERT(60),
        /** Tandem firmware name: aaAlert_LIPO_SUPER_LOW */
        LIPO_SUPER_LOW_ALERT(61),
        /** Tandem firmware name: aaAlert_LIPO_INSUFF_POWER */
        LIPO_INSUFF_POWER_ALERT(62),
        /** Tandem firmware name: oaAlert_BTLE_PAIRING_TIMEOUT */
        BTLE_PAIRING_TIMEOUT_ALERT(63)
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

        public boolean isKnown() {
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
            if (alert.isKnown()) {
                ids.add(alert.getId());
            }
        }

        return ids;
    }
}

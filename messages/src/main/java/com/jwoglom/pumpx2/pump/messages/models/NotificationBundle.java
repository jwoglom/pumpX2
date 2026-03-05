package com.jwoglom.pumpx2.pump.messages.models;

import org.apache.commons.lang3.StringUtils;
import java.util.Arrays;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ActiveAamStatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.AlarmStatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.AlertStatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CGMAlertStatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.HighestAamRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.MalfunctionStatusRequest;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ActiveAamStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.AlarmStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.AlertStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CGMAlertStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.HighestAamResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.MalfunctionStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ReminderStatusResponse;
import com.jwoglom.pumpx2.shared.JavaHelpers;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Bundle of notification related responses for easier processing
 */
public class NotificationBundle {
    private AlertStatusResponse alertStatusResponse;
    private ReminderStatusResponse reminderStatusResponse;
    private AlarmStatusResponse alarmStatusResponse;
    private CGMAlertStatusResponse cgmAlertStatusResponse;
    private MalfunctionStatusResponse malfunctionStatusResponse;
    private ActiveAamStatusResponse activeAamStatusResponse;
    private HighestAamResponse highestAamResponse;

    private Map<Class<? extends Message>, Instant> lastUpdatedTimes = new HashMap<>();

    public static List<Message> allRequests() {
        return Arrays.asList(
                new AlertStatusRequest(),
                new AlarmStatusRequest(),
                new CGMAlertStatusRequest(),
                new MalfunctionStatusRequest(),
                new HighestAamRequest(),
                new ActiveAamStatusRequest(new byte[]{2}),
                new ActiveAamStatusRequest(new byte[]{4})
        );
    }

    public static List<Class<? extends Message>> responseClasses() {
        return Arrays.asList(
                AlertStatusResponse.class,
                ReminderStatusResponse.class,
                AlarmStatusResponse.class,
                CGMAlertStatusResponse.class,
                MalfunctionStatusResponse.class,
                HighestAamResponse.class,
                ActiveAamStatusResponse.class
        );
    }

    public static boolean isNotificationResponse(Message message) {
        for (Class<? extends Message> m : responseClasses()) {
            if (m.isInstance(message)) return true;
        }

        return false;
    }

    public NotificationBundle() {
    }

    public NotificationBundle(NotificationBundle bundle) {
        this.alertStatusResponse = bundle.alertStatusResponse;
        this.reminderStatusResponse = bundle.reminderStatusResponse;
        this.alarmStatusResponse = bundle.alarmStatusResponse;
        this.cgmAlertStatusResponse = bundle.cgmAlertStatusResponse;
        this.malfunctionStatusResponse = bundle.malfunctionStatusResponse;
        this.activeAamStatusResponse = bundle.activeAamStatusResponse;
        this.highestAamResponse = bundle.highestAamResponse;
        this.lastUpdatedTimes = bundle.lastUpdatedTimes;
    }

    public NotificationBundle add(Message message) {
        if (!isNotificationResponse(message)) return this;

        if (message instanceof AlertStatusResponse) {
            alertStatusResponse = (AlertStatusResponse) message;
            lastUpdatedTimes.put(AlertStatusResponse.class, Instant.now());
        } else if (message instanceof ReminderStatusResponse) {
            reminderStatusResponse = (ReminderStatusResponse) message;
            lastUpdatedTimes.put(ReminderStatusResponse.class, Instant.now());
        } else if (message instanceof AlarmStatusResponse) {
            alarmStatusResponse = (AlarmStatusResponse) message;
            lastUpdatedTimes.put(AlarmStatusResponse.class, Instant.now());
        } else if (message instanceof CGMAlertStatusResponse) {
            cgmAlertStatusResponse = (CGMAlertStatusResponse) message;
            lastUpdatedTimes.put(CGMAlertStatusResponse.class, Instant.now());
        } else if (message instanceof MalfunctionStatusResponse) {
            malfunctionStatusResponse = (MalfunctionStatusResponse) message;
            lastUpdatedTimes.put(MalfunctionStatusResponse.class, Instant.now());
        } else if (message instanceof ActiveAamStatusResponse) {
            activeAamStatusResponse = (ActiveAamStatusResponse) message;
            lastUpdatedTimes.put(ActiveAamStatusResponse.class, Instant.now());
        } else if (message instanceof HighestAamResponse) {
            highestAamResponse = (HighestAamResponse) message;
            lastUpdatedTimes.put(HighestAamResponse.class, Instant.now());
        }

        return this;
    }

    /**
     * @return list of raw alert/alarm enums or string errors. Union type of {@link NotificationEnum}:
     * <ul>
     *     <li>{@link com.jwoglom.pumpx2.pump.messages.response.currentStatus.AlertStatusResponse.AlertResponseType}</li>
     *     <li>{@link com.jwoglom.pumpx2.pump.messages.response.currentStatus.AlarmStatusResponse.AlarmResponseType}</li>
     *     <li>{@link com.jwoglom.pumpx2.pump.messages.response.currentStatus.ReminderStatusResponse.ReminderType}</li>
     *     <li>{@link com.jwoglom.pumpx2.pump.messages.response.currentStatus.CGMAlertStatusResponse.CGMAlert}</li>
     *     <li>{@link HighestAamResponse}</li>
     * </ul>
     */
    public List<NotificationEnum> get() {
        ArrayList<NotificationMessage> notificationMessages = new ArrayList<>();
        ArrayList<NotificationEnum> slugs = new ArrayList<>();

        if (alertStatusResponse != null) {
            notificationMessages.add(alertStatusResponse);
            alertStatusResponse.getAlerts().stream().sorted().forEach(slugs::add);
        }
        if (alarmStatusResponse != null) {
            notificationMessages.add(alarmStatusResponse);
            alarmStatusResponse.getAlarms().stream().sorted().forEach(slugs::add);
        }
        if (reminderStatusResponse != null) {
            notificationMessages.add(reminderStatusResponse);
            reminderStatusResponse.getReminders().stream().sorted().forEach(slugs::add);
        }
        if (cgmAlertStatusResponse != null) {
            notificationMessages.add(cgmAlertStatusResponse);
            cgmAlertStatusResponse.getCgmAlerts().stream().sorted().forEach(slugs::add);
        }
        if (highestAamResponse != null) {
            if (highestAamResponse.hasMalfunction(notificationMessages.toArray(new NotificationMessage[0])) &&
                !StringUtils.isBlank(highestAamResponse.getErrorString()))
            {
                slugs.add(highestAamResponse);
            }
        }
        // todo remaining once understood

        return slugs;
    }

    public Map<Class<? extends Message>, Instant> getLastUpdatedTimes() {
        return lastUpdatedTimes;
    }

    public boolean isEmpty() {
        return getNotificationCount()==0;
    }

    public int getNotificationCount() {
        return get().size();
    }



    public String toString() {
        return JavaHelpers.autoToString(this, new HashSet<>(Arrays.asList("lastUpdatedTimes")));
    }
}

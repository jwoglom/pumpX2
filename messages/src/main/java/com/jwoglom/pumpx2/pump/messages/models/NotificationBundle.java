package com.jwoglom.pumpx2.pump.messages.models;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.AlarmStatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.AlertStatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CGMAlertStatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.MalfunctionStatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.OtherNotification2StatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.OtherNotificationStatusRequest;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.AlarmStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.AlertStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CGMAlertStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.MalfunctionStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.OtherNotification2StatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.OtherNotificationStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ReminderStatusResponse;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
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
    private OtherNotificationStatusResponse otherNotificationStatusResponse;
    private OtherNotification2StatusResponse otherNotification2StatusResponse;

    private Map<Class<? extends Message>, Instant> lastUpdatedTimes = new HashMap<>();

    public static List<Message> allRequests() {
        return ImmutableList.of(
                new AlertStatusRequest(),
                new AlarmStatusRequest(),
                new CGMAlertStatusRequest(),
                new MalfunctionStatusRequest(),
                new OtherNotification2StatusRequest(),
                new OtherNotificationStatusRequest(new byte[]{2}),
                new OtherNotificationStatusRequest(new byte[]{4})
        );
    }

    public static List<Class<? extends Message>> responseClasses() {
        return ImmutableList.of(
                AlertStatusResponse.class,
                ReminderStatusResponse.class,
                AlarmStatusResponse.class,
                CGMAlertStatusResponse.class,
                MalfunctionStatusResponse.class,
                OtherNotification2StatusResponse.class,
                OtherNotificationStatusResponse.class
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
        } else if (message instanceof OtherNotificationStatusResponse) {
            otherNotificationStatusResponse = (OtherNotificationStatusResponse) message;
            lastUpdatedTimes.put(OtherNotificationStatusResponse.class, Instant.now());
        } else if (message instanceof OtherNotification2StatusResponse) {
            otherNotification2StatusResponse = (OtherNotification2StatusResponse) message;
            lastUpdatedTimes.put(OtherNotification2StatusResponse.class, Instant.now());
        }

        return this;
    }

    /**
     * @return list of raw alert/alarm enums or string errors
     */
    public List<Object> get() {
        ArrayList<Object> slugs = new ArrayList<>();

        if (alertStatusResponse != null) {
            alertStatusResponse.getAlerts().stream().sorted().forEach(alert -> {
                slugs.add(alert);
            });
        }
        if (reminderStatusResponse != null) {
            reminderStatusResponse.getReminders().stream().sorted().forEach(alert -> {
                slugs.add(alert);
            });
        }
        if (alarmStatusResponse != null) {
            alarmStatusResponse.getAlarms().stream().sorted().forEach(alarm -> {
                slugs.add(alarm);
            });
        }
        if (cgmAlertStatusResponse != null) {
            cgmAlertStatusResponse.getCgmAlerts().stream().sorted().forEach(cgmAlert -> {
                slugs.add(cgmAlert);
            });
        }
        if (malfunctionStatusResponse != null) {
            if (!Strings.isNullOrEmpty(malfunctionStatusResponse.getErrorString())) {
                slugs.add(malfunctionStatusResponse);
            }
        }
        // todo remaining

        return slugs;
    }

    public Map<Class<? extends Message>, Instant> getLastUpdatedTimes() {
        return lastUpdatedTimes;
    }

}

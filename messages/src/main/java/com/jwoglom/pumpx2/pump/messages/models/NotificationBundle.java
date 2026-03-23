package com.jwoglom.pumpx2.pump.messages.models;

import org.apache.commons.lang3.StringUtils;
import java.util.Arrays;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ActiveAamBitsRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.AlarmStatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.AlertStatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CGMAlertStatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.HighestAamRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.MalfunctionStatusRequest;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ActiveAamBitsResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.AlarmStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.AlertStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CGMAlertStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.HighestAamResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.MalfunctionBitmaskStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ReminderStatusResponse;
import com.jwoglom.pumpx2.shared.JavaHelpers;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Bundle of notification related responses for easier processing
 */
public class NotificationBundle {
    private ApiVersion apiVersion;
    private AlertStatusResponse alertStatusResponse;
    private ReminderStatusResponse reminderStatusResponse;
    private AlarmStatusResponse alarmStatusResponse;
    private CGMAlertStatusResponse cgmAlertStatusResponse;
    private MalfunctionBitmaskStatusResponse malfunctionBitmaskStatusResponse;
    private Map<ActiveAamBitsResponse.AamType, ActiveAamBitsResponse> activeAamBitsResponses = new HashMap<>();
    private HighestAamResponse highestAamResponse;

    private Map<Class<? extends Message>, Instant> lastUpdatedTimes = new HashMap<>();

    public static List<Message> allRequests() {
        return allRequests(null);
    }

    public List<Message> allRequestsForPump() {
        return allRequests(apiVersion);
    }

    public static List<Message> allRequests(ApiVersion apiVersion) {
        List<Message> requests = Arrays.asList(
                new AlertStatusRequest(),
                new AlarmStatusRequest(),
                new CGMAlertStatusRequest(),
                new MalfunctionStatusRequest(),
                new HighestAamRequest(),
                new ActiveAamBitsRequest(new byte[]{2}),
                new ActiveAamBitsRequest(new byte[]{4})
        );

        if (apiVersion == null) {
            return requests;
        }

        return requests.stream()
                .filter(request -> supportsApiVersion(request, apiVersion))
                .collect(Collectors.toList());
    }

    private static boolean supportsApiVersion(Message message, ApiVersion apiVersion) {
        ApiVersion minApi = message.props().minApi().get();
        return apiVersion.greaterThan(minApi) ||
                (apiVersion.getMajor() == minApi.getMajor() && apiVersion.getMinor() == minApi.getMinor());
    }

    public static List<Class<? extends Message>> responseClasses() {
        return Arrays.asList(
                AlertStatusResponse.class,
                ReminderStatusResponse.class,
                AlarmStatusResponse.class,
                CGMAlertStatusResponse.class,
                MalfunctionBitmaskStatusResponse.class,
                HighestAamResponse.class,
                ActiveAamBitsResponse.class
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

    public NotificationBundle(ApiVersion apiVersion) {
        this.apiVersion = apiVersion;
    }

    public NotificationBundle(NotificationBundle bundle) {
        this.apiVersion = bundle.apiVersion;
        this.alertStatusResponse = bundle.alertStatusResponse;
        this.reminderStatusResponse = bundle.reminderStatusResponse;
        this.alarmStatusResponse = bundle.alarmStatusResponse;
        this.cgmAlertStatusResponse = bundle.cgmAlertStatusResponse;
        this.malfunctionBitmaskStatusResponse = bundle.malfunctionBitmaskStatusResponse;
        this.activeAamBitsResponses = new HashMap<>(bundle.activeAamBitsResponses);
        this.highestAamResponse = bundle.highestAamResponse;
        this.lastUpdatedTimes = bundle.lastUpdatedTimes;
    }

    public void setApiVersion(ApiVersion apiVersion) {
        this.apiVersion = apiVersion;
    }

    public ApiVersion getApiVersion() {
        return apiVersion;
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
        } else if (message instanceof MalfunctionBitmaskStatusResponse) {
            malfunctionBitmaskStatusResponse = (MalfunctionBitmaskStatusResponse) message;
            lastUpdatedTimes.put(MalfunctionBitmaskStatusResponse.class, Instant.now());
        } else if (message instanceof ActiveAamBitsResponse) {
            ActiveAamBitsResponse activeAamBitsResponse = (ActiveAamBitsResponse) message;
            activeAamBitsResponses.put(activeAamBitsResponse.getAamType(), activeAamBitsResponse);
            lastUpdatedTimes.put(ActiveAamBitsResponse.class, Instant.now());
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
            ActiveAamBitsResponse activeMalfunctionBits =
                activeAamBitsResponses.get(ActiveAamBitsResponse.AamType.MALFUNCTION);
            if (highestAamResponse.hasMalfunction(notificationMessages.toArray(new NotificationMessage[0])) &&
                highestAamResponse.matchesActiveMalfunction(activeMalfunctionBits, malfunctionBitmaskStatusResponse) &&
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

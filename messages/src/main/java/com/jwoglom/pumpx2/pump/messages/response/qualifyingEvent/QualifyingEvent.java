package com.jwoglom.pumpx2.pump.messages.response.qualifyingEvent;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.bluetooth.PumpStateSupplier;
import com.jwoglom.pumpx2.pump.messages.builders.ControlIQInfoRequestBuilder;
import com.jwoglom.pumpx2.pump.messages.builders.CurrentBatteryRequestBuilder;
import com.jwoglom.pumpx2.pump.messages.builders.IOBRequestBuilder;
import com.jwoglom.pumpx2.pump.messages.builders.LastBolusStatusRequestBuilder;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.AlarmStatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.AlertStatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.BasalIQSettingsRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.BasalIQStatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.BolusPermissionChangeReasonRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CGMAlertStatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CGMStatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ControlIQSleepScheduleRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CurrentBasalStatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CurrentBolusStatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CurrentEGVGuiDataRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ExtendedBolusStatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.HomeScreenMirrorRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.InsulinStatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.LastBGRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.MalfunctionStatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ProfileStatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ReminderStatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.TempRateRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.TimeSinceResetRequest;

import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A list of all QualifyingEvents which can be sent from the pump. For some events, a set of request
 * messages which can be sent (via `sendPumpCommand`) to receive more information about the event
 * which just occurred, in logic matching the Tandem mobile app, is included. To get these
 * instantiated request messages, you can invoke:
 *     QualifyingEvent.groupSuggestedHandlers(qualifyingEvents)
 *
 * Note that if t:connect app connection sharing is enabled and the t:connect app is running,
 * you can use enableSendSharedConnectionResponseMessages and get the response from these callbacks
 * for free, without duplicating requests.
 */
public enum QualifyingEvent {
    ALERT(1, Set.of(
            AlertStatusRequest::new)),
    ALARM(2, Set.of(
            AlarmStatusRequest::new)),
    REMINDER(4, Set.of(
            ReminderStatusRequest::new)),
    MALFUNCTION(8, Set.of(
            MalfunctionStatusRequest::new)),
    CGM_ALERT(16, Set.of(
            CGMAlertStatusRequest::new)),
    HOME_SCREEN_CHANGE(32, Set.of(
            CurrentBasalStatusRequest::new,
            CurrentEGVGuiDataRequest::new,
            HomeScreenMirrorRequest::new,
            () -> ControlIQInfoRequestBuilder.create(PumpStateSupplier.pumpApiVersion.get()))),
    PUMP_SUSPEND(64, Set.of(
            InsulinStatusRequest::new,
            () -> IOBRequestBuilder.create(PumpStateSupplier.controlIQSupported.get()))),
    PUMP_RESUME(128, Set.of(
            InsulinStatusRequest::new,
            () -> IOBRequestBuilder.create(PumpStateSupplier.controlIQSupported.get()),
            CurrentEGVGuiDataRequest::new,
            ProfileStatusRequest::new)),
    TIME_CHANGE(256, Set.of(
            () -> IOBRequestBuilder.create(PumpStateSupplier.controlIQSupported.get()),
            CGMStatusRequest::new,
            TimeSinceResetRequest::new)),
    BASAL_CHANGE(512, Set.of(
            () -> IOBRequestBuilder.create(PumpStateSupplier.controlIQSupported.get()),
            HomeScreenMirrorRequest::new,
            CurrentBasalStatusRequest::new,
            TempRateRequest::new)),
    BOLUS_CHANGE(1024, Set.of(
            CurrentBolusStatusRequest::new,
            ExtendedBolusStatusRequest::new,
            () -> LastBolusStatusRequestBuilder.create(PumpStateSupplier.pumpApiVersion.get()))),
    IOB_CHANGE(2048, Set.of(
            () -> IOBRequestBuilder.create(PumpStateSupplier.controlIQSupported.get()))),
    EXTENDED_BOLUS_CHANGE(4096, Set.of(
            ExtendedBolusStatusRequest::new,
            () -> LastBolusStatusRequestBuilder.create(PumpStateSupplier.pumpApiVersion.get()))),
    PROFILE_CHANGE(8192, Set.of(
            ProfileStatusRequest::new)),
    BG(16384, Set.of(
            LastBGRequest::new)),
    CGM_CHANGE(32768, Set.of(
            CGMStatusRequest::new,
            CurrentEGVGuiDataRequest::new,
            HomeScreenMirrorRequest::new)),
    BATTERY(65536, Set.of(
            () -> CurrentBatteryRequestBuilder.create(PumpStateSupplier.pumpApiVersion.get()))),
    BASAL_IQ(131072, Set.of(
            BasalIQSettingsRequest::new)),
    REMAINING_INSULIN(262144, Set.of(
            InsulinStatusRequest::new)),
    SUSPEND_COMM(524288),
    ACTIVE_SEGMENT_CHANGE(1048576, Set.of(
            ProfileStatusRequest::new)),
    BASAL_IQ_STATUS(2097152, Set.of(
            BasalIQSettingsRequest::new,
            BasalIQStatusRequest::new)),
    CONTROL_IQ_INFO(4194304, Set.of(
            () -> IOBRequestBuilder.create(PumpStateSupplier.controlIQSupported.get()),
            () -> ControlIQInfoRequestBuilder.create(PumpStateSupplier.pumpApiVersion.get()),
            ControlIQSleepScheduleRequest::new)),
    CONTROL_IQ_SLEEP(8388608, Set.of(
            ControlIQSleepScheduleRequest::new)),
    BOLUS_PERMISSION_REVOKED(2147483648L, Set.of(
            () -> {
                if (PumpStateSupplier.inProgressBolusId != null && PumpStateSupplier.inProgressBolusId.get() != null) {
                    return new BolusPermissionChangeReasonRequest(PumpStateSupplier.inProgressBolusId.get());
                }
                return null;
            }
    )),

    ;
    private final long id;
    private final Set<Supplier<? extends Message>> suggestedHandlers;
    QualifyingEvent(long id) {
        this.id = id;
        this.suggestedHandlers = new HashSet<>();
    }

    QualifyingEvent(long id, Set<Supplier<? extends Message>> suggestedHandlers) {
        this.id = id;
        this.suggestedHandlers = suggestedHandlers;
    }

    public final long getId() {
        return id;
    }

    public final Set<Supplier<? extends Message>> getSuggestedHandlers() {
        return suggestedHandlers;
    }

    public static Set<QualifyingEvent> fromBitmask(long bitmask) {
        Set<QualifyingEvent> ret = new TreeSet<>();
        for (QualifyingEvent e : values()) {
            if ((bitmask & e.getId()) != 0) {
                ret.add(e);
            }
        }
        return ret;
    }

    public static Set<QualifyingEvent> fromRawBtBytes(byte[] raw) {
        return fromBitmask(Bytes.readUint32(raw, 0));
    }

    public static Set<? extends Message> groupSuggestedHandlers(Set<QualifyingEvent> events) {
        Set<Supplier<? extends Message>> messages = new HashSet<>();
        for (QualifyingEvent e : events) {
            messages.addAll(e.suggestedHandlers);
        }
        return messages.stream().map(Supplier::get).collect(Collectors.toSet());
    }

    public static int toBitmask(QualifyingEvent...states) {
        int bitmask = 0;
        for (QualifyingEvent e : states) {
            bitmask |= e.getId();
        }
        return bitmask;
    }

    public String jsonToString() {
        JSONObject obj = new JSONObject();
        obj.put("event", name());
        return obj.toString(0);
    }

    public JSONObject asJsonObject() {
        JSONObject obj = new JSONObject();
        obj.put("event", name());
        return obj;
    }

}

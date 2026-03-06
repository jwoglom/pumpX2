package com.jwoglom.pumpx2.pump.messages.response.qualifyingEvent;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ActiveAamBitsRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.AlarmStatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.BasalLimitSettingsRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.GlobalMaxBolusSettingsRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.HighestAamRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.LoadStatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.LocalizationRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.PumpGlobalsRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.PumpSettingsRequest;
import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static com.jwoglom.pumpx2.pump.messages.response.qualifyingEvent.QualifyingEvent.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class QualifyingEventTest {
    @Test
    public void testQualifyingEvent_1() throws DecoderException {
        Set<QualifyingEvent> events = QualifyingEvent.fromRawBtBytes(Hex.decodeHex("210c0000"));
        assertEquals(events, Set.of(HOME_SCREEN_CHANGE, ALERT, BOLUS_CHANGE, IOB_CHANGE));
        System.out.println(events);
    }

    @Test
    public void testQualifyingEvent_2() throws DecoderException {
        Set<QualifyingEvent> events = QualifyingEvent.fromRawBtBytes(Hex.decodeHex("210c4000"));
        assertEquals(events, Set.of(HOME_SCREEN_CHANGE, ALERT, BOLUS_CHANGE, IOB_CHANGE, CONTROL_IQ_INFO));
        System.out.println(events);
    }

    @Test
    public void testQualifyingEvent_3() throws DecoderException {
        Set<QualifyingEvent> events = QualifyingEvent.fromRawBtBytes(Hex.decodeHex("210c5000"));
        assertEquals(events, Set.of(HOME_SCREEN_CHANGE, ALERT, BOLUS_CHANGE, IOB_CHANGE, ACTIVE_PROFILE_SEGMENT_CHANGE, CONTROL_IQ_INFO));
        System.out.println(events);
    }

    @Test
    public void testQualifyingEvent_4() throws DecoderException {
        Set<QualifyingEvent> events = QualifyingEvent.fromRawBtBytes(Hex.decodeHex("20040000"));
        assertEquals(events, Set.of(HOME_SCREEN_CHANGE, BOLUS_CHANGE));
        System.out.println(events);
    }

    @Test
    public void testQualifyingEvent_5() throws DecoderException {
        Set<QualifyingEvent> events = QualifyingEvent.fromRawBtBytes(Hex.decodeHex("00040000"));
        assertEquals(events, Set.of(BOLUS_CHANGE));
        System.out.println(events);
    }

    @Test
    public void testQualifyingEvent_6() throws DecoderException {
        Set<QualifyingEvent> events = QualifyingEvent.fromRawBtBytes(Hex.decodeHex("00040080"));
        assertEquals(events, Set.of(BOLUS_PERMISSION_REVOKED, BOLUS_CHANGE));
        System.out.println(events);
    }

    @Test
    public void testQualifyingEvent_7() throws DecoderException {
        Set<QualifyingEvent> events = QualifyingEvent.fromRawBtBytes(Hex.decodeHex("200c0000"));
        assertEquals(events, Set.of(HOME_SCREEN_CHANGE, BOLUS_CHANGE, IOB_CHANGE));
        System.out.println(events);
    }

    @Test
    public void testQualifyingEvent_8() throws DecoderException {
        Set<QualifyingEvent> events = QualifyingEvent.fromRawBtBytes(Hex.decodeHex("21080000"));
        assertEquals(events, Set.of(HOME_SCREEN_CHANGE, ALERT, IOB_CHANGE));
        System.out.println(events);
    }

    @Test
    public void testQualifyingEvent_9() throws DecoderException {
        Set<QualifyingEvent> events = QualifyingEvent.fromRawBtBytes(Hex.decodeHex("00080000"));
        assertEquals(events, Set.of(IOB_CHANGE));
        System.out.println(events);
    }

    @Test
    public void testQualifyingEvent_10() throws DecoderException {
        Set<QualifyingEvent> events = QualifyingEvent.fromRawBtBytes(Hex.decodeHex("e34e4100"));
        assertEquals(events, Set.of(PUMP_RESUME, BATTERY, ALERT, ALARM, PUMP_SUSPEND, BASAL_CHANGE, BOLUS_CHANGE, HOME_SCREEN_CHANGE, IOB_CHANGE, CONTROL_IQ_INFO, BG));
        System.out.println(events);
    }

    @Test
    public void testQualifyingEvent_startChangeCart() throws DecoderException {
        Set<QualifyingEvent> events = QualifyingEvent.fromRawBtBytes(Hex.decodeHex("00000404"));
        assertEquals(events, Set.of(REMAINING_INSULIN, PUMPING_STATUS));
        System.out.println(events);
    }

    @Test
    public void testQualifyingEvent_malfunction() throws DecoderException {
        Set<QualifyingEvent> events = QualifyingEvent.fromRawBtBytes(Hex.decodeHex("08000100"));
        assertEquals(events, Set.of(MALFUNCTION, BATTERY));
        System.out.println(events);
    }

    @Test
    public void testQualifyingEvent_newBits() throws DecoderException {
        assertEquals(Set.of(GLOBAL_PUMP_SETTINGS), QualifyingEvent.fromRawBtBytes(Hex.decodeHex("00000001")));
        assertEquals(Set.of(SNOOZE_STATUS), QualifyingEvent.fromRawBtBytes(Hex.decodeHex("00000002")));
        assertEquals(Set.of(PUMPING_STATUS), QualifyingEvent.fromRawBtBytes(Hex.decodeHex("00000004")));
        assertEquals(Set.of(PUMP_RESET), QualifyingEvent.fromRawBtBytes(Hex.decodeHex("00000008")));
        assertEquals(Set.of(HEARTBEAT), QualifyingEvent.fromRawBtBytes(Hex.decodeHex("00000010")));
    }

    @Test
    public void testQualifyingEvent_groupSuggestedHandlers_alarmIncludesAamRequests() {
        Set<? extends Message> messages = QualifyingEvent.groupSuggestedHandlers(Set.of(ALARM));
        assertEquals(Set.of(
                AlarmStatusRequest.class,
                HighestAamRequest.class,
                ActiveAamBitsRequest.class),
                messageClasses(messages));
        assertTrue(messages.stream().anyMatch(message ->
                message instanceof ActiveAamBitsRequest &&
                        Arrays.equals(message.getCargo(), new byte[]{2})));
    }

    @Test
    public void testQualifyingEvent_groupSuggestedHandlers_globalPumpSettingsChangeUsesExistingRequests() {
        Set<? extends Message> messages = QualifyingEvent.groupSuggestedHandlers(Set.of(GLOBAL_PUMP_SETTINGS));
        assertEquals(Set.of(
                GlobalMaxBolusSettingsRequest.class,
                BasalLimitSettingsRequest.class,
                LocalizationRequest.class,
                PumpGlobalsRequest.class,
                PumpSettingsRequest.class),
                messageClasses(messages));
    }

    @Test
    public void testQualifyingEvent_groupSuggestedHandlers_pumpingStatusChangeUsesLoadStatus() {
        Set<? extends Message> messages = QualifyingEvent.groupSuggestedHandlers(Set.of(PUMPING_STATUS));
        assertEquals(Set.of(LoadStatusRequest.class), messageClasses(messages));
    }

    private static Set<Class<? extends Message>> messageClasses(Set<? extends Message> messages) {
        return messages.stream().map(Message::getClass).collect(Collectors.toSet());
    }
}

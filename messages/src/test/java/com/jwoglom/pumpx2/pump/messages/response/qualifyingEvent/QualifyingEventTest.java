package com.jwoglom.pumpx2.pump.messages.response.qualifyingEvent;

import java.util.Set;
import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import java.util.Set;

import static com.jwoglom.pumpx2.pump.messages.response.qualifyingEvent.QualifyingEvent.*;
import static org.junit.Assert.assertEquals;

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
        assertEquals(events, Set.of(HOME_SCREEN_CHANGE, ALERT, BOLUS_CHANGE, IOB_CHANGE, ACTIVE_SEGMENT_CHANGE, CONTROL_IQ_INFO));
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
        assertEquals(events, Set.of(REMAINING_INSULIN));
        System.out.println(events);
    }

    @Test
    public void testQualifyingEvent_malfunction() throws DecoderException {
        Set<QualifyingEvent> events = QualifyingEvent.fromRawBtBytes(Hex.decodeHex("08000100"));
        assertEquals(events, Set.of(MALFUNCTION, BATTERY));
        System.out.println(events);
    }
}

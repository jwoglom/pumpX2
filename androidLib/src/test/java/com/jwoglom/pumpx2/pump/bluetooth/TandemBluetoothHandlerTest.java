package com.jwoglom.pumpx2.pump.bluetooth;

import static com.jwoglom.pumpx2.pump.messages.response.qualifyingEvent.QualifyingEvent.ALERT;
import static com.jwoglom.pumpx2.pump.messages.response.qualifyingEvent.QualifyingEvent.BOLUS_CHANGE;
import static com.jwoglom.pumpx2.pump.messages.response.qualifyingEvent.QualifyingEvent.EXTENDED_BOLUS_CHANGE;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.response.qualifyingEvent.QualifyingEvent;

import org.junit.Test;

import java.util.Set;

public class TandemBluetoothHandlerTest {
    @Test
    public void testNormalizeQualifyingEvents_prefersBolusChangeOverExtendedBolusChange() {
        assertEquals(
                Set.of(ALERT, BOLUS_CHANGE),
                TandemBluetoothHandler.normalizeQualifyingEvents(Set.of(ALERT, BOLUS_CHANGE, EXTENDED_BOLUS_CHANGE)));
    }

    @Test
    public void testNormalizeQualifyingEvents_preservesExtendedBolusChangeWhenBolusChangeMissing() {
        assertEquals(
                Set.of(ALERT, EXTENDED_BOLUS_CHANGE),
                TandemBluetoothHandler.normalizeQualifyingEvents(Set.of(ALERT, EXTENDED_BOLUS_CHANGE)));
    }
}

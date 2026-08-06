package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.List;

public class HistoryLogParserRegistryTest {
    /**
     * HistoryLogParser does not throw on a malformed registry, because a throwing static
     * initializer would make the class permanently unusable at runtime. This test is what
     * catches such a defect instead, so a duplicate typeId or a HistoryLog subclass without
     * a no-arg constructor fails the build rather than a user's history-log sync.
     */
    @Test
    public void testRegistryBuildsWithoutErrors() {
        List<String> errors = HistoryLogParser.getRegistrationErrors();

        assertEquals("HistoryLogParser registry has defects: " + errors, List.of(), errors);
        assertEquals(HistoryLogParser.LOG_MESSAGE_TYPES.size(), HistoryLogParser.LOG_MESSAGE_IDS.size());
        assertEquals(HistoryLogParser.LOG_MESSAGE_TYPES.size(), HistoryLogParser.LOG_MESSAGE_CLASS_TO_ID.size());
    }

    @Test
    public void testParserDispatchesPreviouslyUnregisteredHistoryLogs() {
        assertParserClass(8, AlarmAckHistoryLog.class);
        assertParserClass(142, BasalIqSettingsChangeHistoryLog.class);
        assertParserClass(288, AaAutoBolusRejectedHistoryLog.class);
        assertParserClass(395, CgmPairingCodeG7HistoryLog.class);
        assertParserClass(438, CgmCalibrationG7HistoryLog.class);
    }

    @Test
    public void testParserIgnoresBitsAboveTwelveInTypeId() {
        // 395 = 0x018B, 438 = 0x01B6; set bits above the low 12 bits in the high byte.
        assertParserClass(CgmPairingCodeG7HistoryLog.class, new byte[]{(byte) 0x8B, (byte) 0x11});
        assertParserClass(CgmCalibrationG7HistoryLog.class, new byte[]{(byte) 0xB6, (byte) 0x21});
    }

    @Test
    public void testParserReturnsUnknownHistoryLogForUnregisteredTypeId() {
        byte[] cargo = new byte[26];
        cargo[0] = 0x34;
        cargo[1] = 0x0C; // 0x0C34 = 3124, above the current registry max of 486

        assertEquals(UnknownHistoryLog.class, HistoryLogParser.parse(cargo).getClass());
    }

    private static void assertParserClass(int typeId, Class<? extends HistoryLog> expectedClass) {
        byte[] cargo = new byte[26];
        cargo[0] = (byte) (typeId & 0xFF);
        cargo[1] = (byte) ((typeId >> 8) & 0xFF);
        assertParserClass(expectedClass, cargo);
    }

    private static void assertParserClass(Class<? extends HistoryLog> expectedClass, byte[] idBytes) {
        byte[] cargo = new byte[26];
        cargo[0] = idBytes[0];
        cargo[1] = idBytes[1];

        assertEquals(expectedClass, HistoryLogParser.parse(cargo).getClass());
    }
}

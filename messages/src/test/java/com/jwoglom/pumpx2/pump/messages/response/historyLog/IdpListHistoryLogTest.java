package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.IdpListHistoryLog;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class IdpListHistoryLogTest {
    @Test
    public void testIdpListHistoryLog() throws DecoderException {
        IdpListHistoryLog expected = new IdpListHistoryLog(
            // int numProfiles, int slot1, int slot2, int slot3, int slot4, int slot5, int slot6
        );

        IdpListHistoryLog parsedRes = (IdpListHistoryLog) HistoryLogMessageTester.testSingle(
                "xxxx",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
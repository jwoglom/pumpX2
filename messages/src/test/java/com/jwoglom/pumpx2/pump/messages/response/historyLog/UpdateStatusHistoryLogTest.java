package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class UpdateStatusHistoryLogTest {
    /**
     * Byte layout per the Tandem Source event schema for LID_UPDATE_STATUS:
     * swUpdateStatus u16 @0, metadataAndVersionStatus u16 @2, fullDlAndCrcStatus u16 @4,
     * fileDlAndSideloadStatus u16 @6, externalFlashStatus u16 @8, updateSuccessful u8 @10, swPartNum u32 @12.
     */
    @Test
    public void testUpdateStatusHistoryLogLayout() throws DecoderException {
        UpdateStatusHistoryLog expected = new UpdateStatusHistoryLog(
                // long pumpTimeSec, long sequenceNum, int swUpdateStatus, int metadataAndVersionStatus, int fullDlAndCrcStatus, int fileDlAndSideloadStatus, int externalFlashStatus, int updateSuccessful, long swPartNum
                566516808L, 506706L, 0x0101, 0x0202, 0x0303, 0x0404, 0x0505, 1, 1009000L
        );

        UpdateStatusHistoryLog parsedRes = (UpdateStatusHistoryLog) HistoryLogMessageTester.testSingle(
                // header: 0x00CB | pumpTime | seqNum || 0101 | 0202 | 0303 | 0404 | 0505 | 01 | 00 | 68650f00
                "cb00485cc42152bb070001010202030304040505010068650f00",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(0x0101, parsedRes.getSwUpdateStatus());
        assertEquals(0x0202, parsedRes.getMetadataAndVersionStatus());
        assertEquals(0x0303, parsedRes.getFullDlAndCrcStatus());
        assertEquals(0x0404, parsedRes.getFileDlAndSideloadStatus());
        assertEquals(0x0505, parsedRes.getExternalFlashStatus());
        assertEquals(UpdateStatusHistoryLog.UpdateSuccessful.SUCCESSFUL, parsedRes.getUpdateSuccessfulEnum());
        assertEquals(1009000L, parsedRes.getSwPartNum());
    }
}

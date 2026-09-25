package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

/**
 * HistoryLogParser.parse returns the typed class, not UnknownHistoryLog, for each opcode that was
 * previously missing from LOG_MESSAGE_TYPES. Real records are from the maintainer's own t:slim X2
 * and Mobi captures (2022-2026). Opcodes with no captured record use a synthetic header-only record.
 */
@RunWith(Parameterized.class)
public class HistoryLogParserDispatchTest {
    @Parameterized.Parameters(name = "{0} {1}")
    public static Collection<Object[]> records() {
        return Arrays.asList(new Object[][]{
            {7, MalfunctionAckHistoryLog.class, "0710607a2e202adf01000c000000000000000000000000000000"},
            {30, ReminderSnoozedHistoryLog.class, synthetic(30)},
            {31, CartridgeRemovedHistoryLog.class, "1f00770f951a2100000000000000000000000000000000000000"},
            {32, CartridgeInsertedHistoryLog.class, "2010e267312378830a0000000000000000000000000000000000"},
            {41, ConfirmCartridgeFilledHistoryLog.class, "2900cdf3951a01ca0200f000000060984743d70000003ecac841"},
            {98, FillEstimateFinalHistoryLog.class, "6210e267312377830a00f2ef4b43000000000000000000000000"},
            {142, BasalIqSettingsChangeHistoryLog.class, synthetic(142)},
            {156, CgmTransmitterIdHistoryLog.class, synthetic(156)},
            {157, CgmAnnuSettingsHistoryLog.class, "9d0004456a1ec900000000000000000000000000000000000000"},
            {162, CgmStopSessionMsg1HistoryLog.class, synthetic(162)},
            {163, CgmStopSessionMsg2HistoryLog.class, synthetic(163)},
            {165, CgmHgaSettingsHistoryLog.class, "a50092b0a21afc070000c8003c00000100000000000000000000"},
            {166, CgmLgaSettingsHistoryLog.class, "a610b848fd1f8701000050000f00010102000000000000000000"},
            {167, CgmRraSettingsHistoryLog.class, "a710fd48fd1f9e01000002000000010001000000000000000000"},
            {168, CgmFraSettingsHistoryLog.class, "a80066b4a21a1f08000002000000010001000000000000000000"},
            {169, CgmOorSettingsHistoryLog.class, "a9105749fd1fb301000014000000000100000000000000000000"},
            {173, CgmAlertAckHistoryLog.class, "ad00d843961affcb020003000000000000000000000000000000"},
            {187, CgmUnexpectedGeAlertHistoryLog.class, synthetic(187)},
            {215, CgmInactiveGxHistoryLog.class, "d700faba061e6b29200000000000000000000000000000000000"},
            {216, CgmTransmitterIdGxHistoryLog.class, synthetic(216)},
            {217, CgmStartSessionReqGxHistoryLog.class, synthetic(217)},
            {218, CgmStopSessionReqGxHistoryLog.class, synthetic(218)},
            {220, CgmTransmitterVersionGxHistoryLog.class, "dc0093c0061e1f2a2000021b0267412f00000300000000000000"},
            {235, AaSleepScheduleChangeHistoryLog.class, "eb00339ba21aa807000000011f00460558020300000000000000"},
            {238, AaDeliveryStatusChangeHistoryLog.class, "ee009286941a8ec0020000020000000000000000000000000000"},
            {244, AaEnableSettingChangeHistoryLog.class, "f400031c821b365f000000010000000000000000000000000000"},
            {245, AaTdiSettingChangeHistoryLog.class, "f50038de271eac0000004b000000000000000000000000000000"},
            {246, AaWeightSettingChangeHistoryLog.class, "f60010a9a21ace0700008c009600020100000000000000000000"},
            {267, CgmSessionTypeChangeHistoryLog.class, "0b0193c0061e212a200001000000000000000000000000000000"},
            {283, WumpOcclusionDebugHistoryLog.class, "1b1196455c20dc9903000a00000079ca070079ca070037000100"},
            {286, SnoozeActivatedHistoryLog.class, "1e112c713823d5e20a0000020000000000000000000000000000"},
            {288, AaAutoBolusRejectedHistoryLog.class, synthetic(288)},
            {291, TipscReqPrimeCannulaHistoryLog.class, synthetic(291)},
            {301, WumpCartridgeFilledHistoryLog.class, "2d110c683123ae830a0000004843c0179141b900000000000000"},
            {302, WumpCartridgeRemovedHistoryLog.class, "2e11a767312374830a00ed9294405b4b474391fe0700a1fe0700"},
            {318, AAExerciseTimeChangeHistoryLog.class, synthetic(318)},
            {319, AAExerciseChoiceChangeHistoryLog.class, synthetic(319)},
            {332, AATdiEstChangeHistoryLog.class, "4c11de642c23913c0a00698f7042a40170423c00000000000000"},
            {348, PrimeInprocessHistoryLog.class, "5c11ec6731237d830a0000000000f2ff07000000000000000000"},
            {367, CgmRejoinSessionHistoryLog.class, synthetic(367)},
            {368, CgmSensorTypeChangeHistoryLog.class, "700192496a1e1a01000000030000000000000000000000000000"},
            {390, CgmStartSensorReqG7HistoryLog.class, "86019d496a1e2001000038383838000000000000000000000000"},
            {438, CgmCalibrationG7HistoryLog.class, "b61114975a2070880300a9000000000000000000000000000000"},
            {439, CgmBleCalibrationEvtG7HistoryLog.class, synthetic(439)},
            {441, CgmInactiveG7HistoryLog.class, "b911a524f020be61090000000000000000000000000000000000"},
            {443, CgmStopSessionReqG7HistoryLog.class, "bb1154975a207e88030000000000000000000000000000000000"},
        });
    }

    private final int opCode;
    private final Class<? extends HistoryLog> expectedClass;
    private final String rawHex;

    public HistoryLogParserDispatchTest(int opCode, Class<? extends HistoryLog> expectedClass, String rawHex) {
        this.opCode = opCode;
        this.expectedClass = expectedClass;
        this.rawHex = rawHex;
    }

    @Test
    public void testParseReturnsTypedClass() throws DecoderException {
        byte[] raw = Hex.decodeHex(rawHex);
        assertEquals(opCode, HistoryLogParser.typeIdOf(raw));
        assertEquals(opCode, expectedClass.getAnnotation(HistoryLogProps.class).opCode());

        HistoryLog parsed = HistoryLogParser.parse(raw);

        assertEquals(expectedClass, parsed.getClass());
        assertEquals(opCode, parsed.typeId());
        assertEquals(Bytes.readUint32(raw, 2), parsed.getPumpTimeSec());
        assertEquals(Bytes.readUint32(raw, 6), parsed.getSequenceNum());
        assertHexEquals(raw, parsed.getCargo());
    }

    // Synthetic: header only (pumpTimeSec 500000000, sequenceNum 100000 + opCode), zero payload.
    private static String synthetic(int opCode) {
        return Hex.encodeHexString(HistoryLog.fillCargo(Bytes.combine(
                HistoryLog.typeIdBytes(opCode, 0),
                Bytes.toUint32(500000000L),
                Bytes.toUint32(100000L + opCode))));
    }
}

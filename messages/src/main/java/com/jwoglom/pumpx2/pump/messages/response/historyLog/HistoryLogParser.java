package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.util.MessageHelpers;
import com.jwoglom.pumpx2.shared.L;

import com.jwoglom.pumpx2.shared.Hex;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HistoryLogParser {
    private static final String TAG = "HistoryLogParser";

    public static final Set<Class<? extends HistoryLog>> LOG_MESSAGE_TYPES = Set.of(
        DateChangeHistoryLog.class,
        BGHistoryLog.class,
        DexcomG6CGMHistoryLog.class,
        BolusDeliveryHistoryLog.class,
        BolusCompletedHistoryLog.class,
        BolusRequestedMsg1HistoryLog.class,
        BolusRequestedMsg2HistoryLog.class,
        BolusRequestedMsg3HistoryLog.class,
        BolexCompletedHistoryLog.class,
        BasalRateChangeHistoryLog.class,
        BolexActivatedHistoryLog.class,
        BolusActivatedHistoryLog.class,
        CannulaFilledHistoryLog.class,
        CarbEnteredHistoryLog.class,
        CartridgeFilledHistoryLog.class,
        CorrectionDeclinedHistoryLog.class,
        DailyBasalHistoryLog.class,
        DataLogCorruptionHistoryLog.class,
        IdpActionHistoryLog.class,
        IdpBolusHistoryLog.class,
        IdpListHistoryLog.class,
        FactoryResetHistoryLog.class,
        IdpActionMsg2HistoryLog.class,
        IdpTimeDependentSegmentHistoryLog.class,
        LogErasedHistoryLog.class,
        NewDayHistoryLog.class,
        ParamChangeGlobalSettingsHistoryLog.class,
        ParamChangePumpSettingsHistoryLog.class,
        //ParamChangePumpSettingsHistoryLog.class,
        ParamChangeRemSettingsHistoryLog.class,
        ParamChangeReminderHistoryLog.class,
        PumpingResumedHistoryLog.class,
        PumpingSuspendedHistoryLog.class,
        TempRateActivatedHistoryLog.class,
        TempRateCompletedHistoryLog.class,
        TimeChangedHistoryLog.class,
        TubingFilledHistoryLog.class,
        UsbConnectedHistoryLog.class,
        //UsbConnectedHistoryLog.class,
        UsbDisconnectedHistoryLog.class,
        UsbEnumeratedHistoryLog.class,
        AlarmActivatedHistoryLog.class,
        AlertActivatedHistoryLog.class,
        AlarmClearedHistoryLog.class,
        CgmDataSampleHistoryLog.class,
        CgmCalibrationHistoryLog.class,
        CgmDataGxHistoryLog.class,
        CgmCalibrationGxHistoryLog.class,
        CgmAlertAckDexHistoryLog.class,
        CgmAlertActivatedDexHistoryLog.class,
        CgmAlertActivatedFsl2HistoryLog.class,
        CgmAlertActivatedHistoryLog.class,
        CgmAlertClearedDexHistoryLog.class,
        CgmAlertClearedFsl2HistoryLog.class,
        CgmAlertClearedHistoryLog.class,
        CgmDataFsl2HistoryLog.class,
        CgmDataFsl3HistoryLog.class,
        HypoMinimizerSuspendHistoryLog.class,
        HypoMinimizerResumeHistoryLog.class,
        BasalDeliveryHistoryLog.class,
        CgmJoinSessionFsl2HistoryLog.class,
        CgmJoinSessionFsl3HistoryLog.class,
        CgmJoinSessionG7HistoryLog.class,
        CgmJoinSessionHistoryLog.class,
        CgmStartSessionHistoryLog.class,
        CgmStartSessionFsl2HistoryLog.class,
        CgmStopSessionHistoryLog.class,
        CgmStopSessionFsl2HistoryLog.class,
        CgmStopSessionFsl3HistoryLog.class,
        CgmStopSessionG7HistoryLog.class,
        DailyStatusHistoryLog.class,
        ControlIQPcmChangeHistoryLog.class,
        ControlIQUserModeChangeHistoryLog.class,
        DexcomG7CGMHistoryLog.class,
        MalfunctionHistoryLog.class,
        PlgsPeriodicHistoryLog.class,
        ShelfModeHistoryLog.class,
        ArmInitHistoryLog.class,
        AlertClearedHistoryLog.class,
        VersionInfoHistoryLog.class,
        UpdateStatusHistoryLog.class,
        VersionsAHistoryLog.class,
        AlarmAckHistoryLog.class,
        AlertAckHistoryLog.class,
        ReminderActivatedHistoryLog.class,
        ReminderDismissedHistoryLog.class,
        CgmPairingCodeG7HistoryLog.class,
        TipsErrorHistoryLog.class
        // MESSAGES_END
    );

    public static Map<Integer, Class<? extends HistoryLog>> LOG_MESSAGE_IDS = new HashMap<>();
    public static Map<Class<? extends HistoryLog>, Integer> LOG_MESSAGE_CLASS_TO_ID = new HashMap<>();

    static {
        for (Class<? extends HistoryLog> clazz : LOG_MESSAGE_TYPES) {
            try {
                LOG_MESSAGE_IDS.put(clazz.newInstance().typeId(), clazz);
                LOG_MESSAGE_CLASS_TO_ID.put(clazz, clazz.newInstance().typeId());
            } catch (IllegalAccessException|InstantiationException e) {
                L.e(TAG, String.format("could not instantiate %s", clazz), e);
                e.printStackTrace();
            }
        }
    }

    /**
     * Reads the typeId from the first two bytes of a raw history log.
     *
     * <p>Those bytes are a little-endian uint16 whose low 12 bits are the typeId. The top 4 bits
     * are not part of the id and their meaning is unknown, see
     * {@link HistoryLog#getHeaderHighNibble()}. This applies the same mask
     * {@link HistoryLog#parseBase} already used, and matches Tandem's own Mobi Android app, which
     * reads these two bytes little endian and masks with 4095 before resolving the log type.
     *
     * <p>Without the mask, a record carrying a nonzero high nibble produces an inflated typeId
     * (opCode 55 with a nibble of 1 reads as 4151), misses {@link #LOG_MESSAGE_IDS}, and is only
     * recovered by the retry ladder in {@link #parse}, which logs a warning for every such record.
     * The masking also corrects dispatch for typeIds of 128-255, which the previous signed-byte
     * arithmetic resolved to typeId+256.
     */
    public static int typeIdOf(byte[] rawStream) {
        return Bytes.readShort(rawStream, 0) & 4095;
    }

    public static HistoryLog parse(byte[] rawStream) {
        int typeId = typeIdOf(rawStream);
        HistoryLog ret = parseWithTypeId(rawStream, typeId);
        if (ret instanceof UnknownHistoryLog) {
            L.w(TAG, "retry1 HistoryLog parse on typeId " + typeId + " => " + ((byte) typeId));
            HistoryLog two = parseWithTypeId(rawStream, (byte) typeId);
            if (!(two instanceof UnknownHistoryLog)) {
                return two;
            }
            if ((byte) typeId < 0) {
                L.w(TAG, "retry2 HistoryLog parse on typeId " + ((byte) typeId) + " => " + (((byte) typeId) + 512));
                HistoryLog three = parseWithTypeId(rawStream, (((byte) typeId) + 512));
                if (!(three instanceof UnknownHistoryLog)) {
                    return three;
                }
            }
        }
        return ret;
    }

    private static HistoryLog parseWithTypeId(byte[] rawStream, int typeId) {
        HistoryLog historyLog = null;
        if (!LOG_MESSAGE_IDS.containsKey(typeId)) {
            L.t(TAG, "unknown HistoryLog typeId "+typeId+": "+ Hex.encodeHexString(rawStream));
            historyLog = new UnknownHistoryLog();
            historyLog.parse(rawStream);
            L.i(TAG, String.format("PARSED-EMBEDDED-HISTORY-LOG(typeId=%-3d, UNKNOWN): %s", typeId, Hex.encodeHexString(rawStream)));
            return historyLog;
        }

        try {
            historyLog = LOG_MESSAGE_IDS.get(typeId).newInstance();
        } catch (IllegalAccessException|InstantiationException e) {
            L.e(TAG, "could not instantiate "+typeId, e);
            e.printStackTrace();
            return null;
        }

        String name = MessageHelpers.lastTwoParts(historyLog.getClass().getName());
        L.t(TAG, "found matching "+name+" HistoryLog typeId "+typeId+": "+ Hex.encodeHexString(rawStream));
        historyLog.parse(rawStream);
        L.i(TAG, String.format("PARSED-EMBEDDED-HISTORY-LOG(typeId=%-3d, %s): %s", typeId, name, historyLog.toString()));
        return historyLog;
    }
}

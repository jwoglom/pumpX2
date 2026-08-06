package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.util.MessageHelpers;
import com.jwoglom.pumpx2.shared.L;

import com.jwoglom.pumpx2.shared.Hex;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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
        MalfunctionAckHistoryLog.class,
        AlarmAckHistoryLog.class,
        ReminderActivatedHistoryLog.class,
        AlertAckHistoryLog.class,
        ReminderDismissedHistoryLog.class,
        ReminderSnoozedHistoryLog.class,
        CartridgeRemovedHistoryLog.class,
        CartridgeInsertedHistoryLog.class,
        ConfirmCartridgeFilledHistoryLog.class,
        FillEstimateFinalHistoryLog.class,
        BasalIqSettingsChangeHistoryLog.class,
        CgmTransmitterIdHistoryLog.class,
        CgmAnnuSettingsHistoryLog.class,
        CgmStopSessionMsg1HistoryLog.class,
        CgmStopSessionMsg2HistoryLog.class,
        CgmHgaSettingsHistoryLog.class,
        CgmLgaSettingsHistoryLog.class,
        CgmRraSettingsHistoryLog.class,
        CgmFraSettingsHistoryLog.class,
        CgmOorSettingsHistoryLog.class,
        CgmAlertAckHistoryLog.class,
        CgmUnexpectedGeAlertHistoryLog.class,
        CgmInactiveGxHistoryLog.class,
        CgmTransmitterIdGxHistoryLog.class,
        CgmStartSessionReqGxHistoryLog.class,
        CgmStopSessionReqGxHistoryLog.class,
        CgmTransmitterVersionGxHistoryLog.class,
        AaSleepScheduleChangeHistoryLog.class,
        AaDeliveryStatusChangeHistoryLog.class,
        AaEnableSettingChangeHistoryLog.class,
        AaTdiSettingChangeHistoryLog.class,
        AaWeightSettingChangeHistoryLog.class,
        CgmSessionTypeChangeHistoryLog.class,
        WumpOcclusionDebugHistoryLog.class,
        SnoozeActivatedHistoryLog.class,
        AaAutoBolusRejectedHistoryLog.class,
        TipscReqPrimeCannulaHistoryLog.class,
        WumpCartridgeFilledHistoryLog.class,
        WumpCartridgeRemovedHistoryLog.class,
        AAExerciseTimeChangeHistoryLog.class,
        AAExerciseChoiceChangeHistoryLog.class,
        AATdiEstChangeHistoryLog.class,
        PrimeInprocessHistoryLog.class,
        CgmRejoinSessionHistoryLog.class,
        CgmSensorTypeChangeHistoryLog.class,
        CgmStartSensorReqG7HistoryLog.class,
        CgmPairingCodeG7HistoryLog.class,
        TipsErrorHistoryLog.class,
        CgmCalibrationG7HistoryLog.class,
        CgmBleCalibrationEvtG7HistoryLog.class,
        CgmInactiveG7HistoryLog.class,
        CgmStopSessionReqG7HistoryLog.class
        // MESSAGES_END
    );

    public static Map<Integer, Class<? extends HistoryLog>> LOG_MESSAGE_IDS = new HashMap<>();
    public static Map<Class<? extends HistoryLog>, Integer> LOG_MESSAGE_CLASS_TO_ID = new HashMap<>();
    private static final Map<Integer, Constructor<? extends HistoryLog>> LOG_MESSAGE_CONSTRUCTORS = new HashMap<>();

    /**
     * Problems found while building the registry: duplicate typeIds, or classes without a
     * usable no-arg constructor.
     *
     * Registry construction deliberately never throws. A throwing static initializer leaves
     * the class permanently unusable -- every subsequent call, including for the types that
     * registered fine, fails with NoClassDefFoundError for the remaining life of the process.
     * A registry defect must not take down history-log parsing wholesale on a user's device.
     * These are build-time defects, so they are asserted on by HistoryLogParserRegistryTest
     * and logged at runtime rather than raised.
     */
    private static final List<String> REGISTRATION_ERRORS = new ArrayList<>();

    static {
        // LOG_MESSAGE_TYPES is a Set.of(), whose iteration order is randomized per JVM run.
        // Register in a stable order so that duplicate resolution (first registration wins)
        // and the reported errors are deterministic rather than varying between runs.
        List<Class<? extends HistoryLog>> orderedTypes = new ArrayList<>(LOG_MESSAGE_TYPES);
        orderedTypes.sort(Comparator.comparing(Class::getName));

        for (Class<? extends HistoryLog> clazz : orderedTypes) {
            try {
                Constructor<? extends HistoryLog> constructor = clazz.getDeclaredConstructor();
                int typeId = constructor.newInstance().typeId();
                Class<? extends HistoryLog> existing = LOG_MESSAGE_IDS.get(typeId);
                if (existing != null) {
                    String error = "duplicate HistoryLog typeId " + typeId + ": keeping "
                            + existing.getName() + ", ignoring " + clazz.getName();
                    REGISTRATION_ERRORS.add(error);
                    L.e(TAG, error);
                    continue;
                }
                LOG_MESSAGE_IDS.put(typeId, clazz);
                LOG_MESSAGE_CLASS_TO_ID.put(clazz, typeId);
                LOG_MESSAGE_CONSTRUCTORS.put(typeId, constructor);
            } catch (ReflectiveOperationException e) {
                String error = "could not register HistoryLog " + clazz.getName() + ": " + e;
                REGISTRATION_ERRORS.add(error);
                L.e(TAG, error, e);
            }
        }
    }

    /**
     * @return problems detected while building the registry at class-init time, in a stable
     *         order. Empty when every entry in LOG_MESSAGE_TYPES registered cleanly.
     */
    public static List<String> getRegistrationErrors() {
        return Collections.unmodifiableList(REGISTRATION_ERRORS);
    }

    /**
     * Parses a 26-byte history-log record.
     *
     * The record begins with a 2-byte little-endian type ID, of which the low
     * 12 bits identify the log type (all registered types currently use IDs
     * 0-486; for example 395 = 0x018B). Bits above the 12-bit field are
     * ignored so records with non-zero upper nibbles dispatch deterministically.
     *
     * @return a typed HistoryLog for registered type IDs, or an UnknownHistoryLog
     *         for unregistered or unparseable records (never null)
     */
    public static HistoryLog parse(byte[] rawStream) {
        int typeId = Bytes.readShort(rawStream, 0) & 0x0FFF;
        return parseWithTypeId(rawStream, typeId);
    }

    private static HistoryLog parseWithTypeId(byte[] rawStream, int typeId) {
        Class<? extends HistoryLog> historyLogClass = LOG_MESSAGE_IDS.get(typeId);
        if (historyLogClass == null) {
            L.t(TAG, "unknown HistoryLog typeId "+typeId+": "+ Hex.encodeHexString(rawStream));
            HistoryLog historyLog = new UnknownHistoryLog();
            historyLog.parse(rawStream);
            L.i(TAG, String.format("PARSED-EMBEDDED-HISTORY-LOG(typeId=%-3d, UNKNOWN): %s", typeId, Hex.encodeHexString(rawStream)));
            return historyLog;
        }

        HistoryLog historyLog;
        try {
            Constructor<? extends HistoryLog> constructor = LOG_MESSAGE_CONSTRUCTORS.get(typeId);
            if (constructor == null || constructor.getDeclaringClass() != historyLogClass) {
                constructor = historyLogClass.getDeclaredConstructor();
                LOG_MESSAGE_CONSTRUCTORS.put(typeId, constructor);
            }
            historyLog = constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            L.e(TAG, "could not instantiate "+typeId+", falling back to UnknownHistoryLog", e);
            HistoryLog unknown = new UnknownHistoryLog();
            unknown.parse(rawStream);
            return unknown;
        }

        String name = MessageHelpers.lastTwoParts(historyLog.getClass().getName());
        L.t(TAG, "found matching "+name+" HistoryLog typeId "+typeId+": "+ Hex.encodeHexString(rawStream));
        historyLog.parse(rawStream);
        L.i(TAG, String.format("PARSED-EMBEDDED-HISTORY-LOG(typeId=%-3d, %s): %s", typeId, name, historyLog.toString()));
        return historyLog;
    }
}

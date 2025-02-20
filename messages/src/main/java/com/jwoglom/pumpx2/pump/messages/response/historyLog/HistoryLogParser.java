package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.util.MessageHelpers;
import com.jwoglom.pumpx2.shared.L;

import com.jwoglom.pumpx2.shared.Hex;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HistoryLogParser {
    private static final String TAG = "HistoryLogParser";

    public static final Set<Class<? extends HistoryLog>> LOG_MESSAGE_TYPES = Set.of(
        DateChangeHistoryLog.class,
        BGHistoryLog.class,
        CGMHistoryLog.class,
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
        CgmDataSampleHistoryLog.class,
        CgmCalibrationHistoryLog.class,
        CgmDataGxHistoryLog.class,
        CgmCalibrationGxHistoryLog.class,
        HypoMinimizerSuspendHistoryLog.class,
        HypoMinimizerResumeHistoryLog.class,
        BasalDeliveryHistoryLog.class,
        ControlIQPcmChangeHistoryLog.class,
        ControlIQUserModeChangeHistoryLog.class
        // MESSAGES_END
    );

    public static Map<Integer, Class<? extends HistoryLog>> LOG_MESSAGE_IDS = new HashMap<>();

    static {
        for (Class<? extends HistoryLog> clazz : LOG_MESSAGE_TYPES) {
            try {
                LOG_MESSAGE_IDS.put(clazz.newInstance().typeId(), clazz);
            } catch (IllegalAccessException|InstantiationException e) {
                L.e(TAG, String.format("could not instantiate %s", clazz), e);
                e.printStackTrace();
            }
        }
    }

    public static HistoryLog parse(byte[] rawStream) {
        int typeId = Bytes.readShort(rawStream, 0) & 4095;
//        if (typeId % 256 != typeId) {
//            L.w(TAG, "typeId "+typeId+" is being corrected to "+(typeId % 256));
//            typeId = typeId % 256;
//        }
        HistoryLog ret = parseWithTypeId(rawStream, typeId);
        if (ret instanceof UnknownHistoryLog) {
            L.w(TAG, "retry HistoryLog parse on typeId " + typeId + " => " + ((byte) typeId));
            HistoryLog two = parseWithTypeId(rawStream, (byte) typeId);
            if (two instanceof UnknownHistoryLog) {
                return ret;
            }
            return two;
        }
        return ret;
    }

    private static HistoryLog parseWithTypeId(byte[] rawStream, int typeId) {
        HistoryLog historyLog = null;
        if (!LOG_MESSAGE_IDS.containsKey(typeId)) {
            L.w(TAG, "unknown HistoryLog typeId "+typeId+": "+ Hex.encodeHexString(rawStream));
            historyLog = new UnknownHistoryLog();
            historyLog.parse(rawStream);
            L.i(TAG, String.format("Processed embedded HistoryLog: UnknownHistoryLog (%d): %s", typeId, Hex.encodeHexString(rawStream)));
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
        L.d(TAG, "found matching "+name+" HistoryLog typeId "+typeId+": "+ Hex.encodeHexString(rawStream));
        historyLog.parse(rawStream);
        L.i(TAG, String.format("Processed embedded HistoryLog: %s (%d): %s", name, typeId, Hex.encodeHexString(rawStream)));
        return historyLog;
    }
}

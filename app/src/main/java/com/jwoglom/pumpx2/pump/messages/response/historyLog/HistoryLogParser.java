package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.collect.ImmutableSet;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.shared.L;

import org.apache.commons.codec.binary.Hex;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

public class HistoryLogParser {
    private static final String TAG = "X2-HistoryLogParser";

    public static final Set<Class<? extends HistoryLog>> LOG_MESSAGE_TYPES = ImmutableSet.of(
        TimeChangeHistoryLog.class,
        DateChangeHistoryLog.class
    );

    public static Map<Integer, Class<? extends HistoryLog>> LOG_MESSAGE_IDS = new HashMap<>();

    static {
        for (Class<? extends HistoryLog> clazz : LOG_MESSAGE_TYPES) {
            try {
                LOG_MESSAGE_IDS.put(clazz.newInstance().typeId(), clazz);
            } catch (IllegalAccessException|InstantiationException e) {
                Timber.e("could not instantiate %s: %s", clazz, e);
                e.printStackTrace();
            }
        }
    }

    public static HistoryLog parse(byte[] rawStream) {
        int typeId = Bytes.readShort(rawStream, 0);

        if (!LOG_MESSAGE_IDS.containsKey(typeId)) {
            L.w(TAG, "unknown HistoryLog typeId "+typeId+": "+ Hex.encodeHexString(rawStream));
            return null;
        }

        HistoryLog historyLog = null;
        try {
            historyLog = LOG_MESSAGE_IDS.get(typeId).newInstance();
        } catch (IllegalAccessException|InstantiationException e) {
            L.w(TAG, "could not instantiate "+typeId+" "+e);
            Timber.e("could not instantiate %s: %s", typeId, e);
            e.printStackTrace();
            return null;
        }

        L.w(TAG, "found matching "+historyLog.getClass().getName()+" HistoryLog typeId "+typeId+": "+ Hex.encodeHexString(rawStream));
        historyLog.parse(rawStream);
        return historyLog;
    }
}

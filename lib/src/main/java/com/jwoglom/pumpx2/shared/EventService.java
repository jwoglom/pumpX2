package com.jwoglom.pumpx2.shared;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.google.common.base.Strings;

public class EventService extends Service {
    public static String TAG = "X2-" + Strings.nullToEmpty(new Object(){}.getClass().getEnclosingClass().getSimpleName());

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
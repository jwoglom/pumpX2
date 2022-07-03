package com.jwoglom.pumpx2.util.timber;

import androidx.annotation.NonNull;

import timber.log.Timber;

public class DebugTree extends Timber.DebugTree {
    @Override
    public void log(int priority, String tag, @NonNull String message, Throwable t) {
        super.log(priority, "X2:"+tag, message, t);
    }
}

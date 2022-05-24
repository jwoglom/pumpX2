package com.jwoglom.pumpx2.pump.events;

import android.app.Activity;

public class BTRequestPermissionsEvent {
    private int attempt;
    private final Activity activity;
    public BTRequestPermissionsEvent(int attempt, Activity activity) {
        this.attempt = attempt;
        this.activity = activity;
    }

    public int attempt() {
        return attempt;
    }

    public Activity activity() {
        return activity;
    }

    public String toString() {
        return "BTRequestPermissionsEvent(attempt="+attempt+", activity="+activity+")";
    }
}

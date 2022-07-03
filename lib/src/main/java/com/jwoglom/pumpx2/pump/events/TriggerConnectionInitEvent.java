package com.jwoglom.pumpx2.pump.events;

import android.app.Activity;

public class TriggerConnectionInitEvent {

    private final Activity activity;
    public TriggerConnectionInitEvent(Activity activity) {
        this.activity = activity;
    }

    public Activity activity() {
        return activity;
    }

    public String toString() {
        return "TriggerConnectionInitEvent(activity=" + activity + ")";
    }
}

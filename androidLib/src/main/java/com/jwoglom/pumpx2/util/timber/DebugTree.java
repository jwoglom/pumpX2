package com.jwoglom.pumpx2.util.timber;

import androidx.annotation.NonNull;

import com.jwoglom.pumpx2.shared.L;

import timber.log.Timber;

/**
 * A custom {@link Timber.DebugTree} which prefixes "PumpX2" as the tag on all messages
 */
public class DebugTree extends Timber.DebugTree {
    @Override
    public void log(int priority, String tag, @NonNull String message, Throwable t) {
        // Move the tag at the prefix of the message as the tag suffix.
        if ("LConfigurator".equals(tag)) {
            tag = L.LOG_PREFIX;
            int colon = message.indexOf(": ");
            if (colon > -1) {
                String tagSuffix = message.substring(0, colon);
                if (!tagSuffix.contains(" ")) {
                    tag += ":" + tagSuffix;
                    message = message.substring(colon+2);
                }
            }
            super.log(priority, tag, message, t);
        } else {
            super.log(priority, L.LOG_PREFIX + ":" + tag, message, t);
        }
    }
}

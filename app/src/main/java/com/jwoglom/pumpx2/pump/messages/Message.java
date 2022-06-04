package com.jwoglom.pumpx2.pump.messages;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.shared.JavaHelpers;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import timber.log.Timber;

public abstract class Message {
    public static final byte[] EMPTY = new byte[]{};
    protected byte[] cargo = null;

    public abstract void parse(byte[] raw);

    public MessageProps props() {
        return getClass().getAnnotation(MessageProps.class);
    }

    public int opCode() {
        return props().opCode();
    }

    public boolean signed() {
        return props().signed();
    }

    public MessageType type() {
        return props().type();
    }

    public Class<? extends Message> getResponseClass() {
        Preconditions.checkState(type() == MessageType.REQUEST);
        return props().response();
    }

    public MessageProps getResponseProps() {
        Preconditions.checkState(type() == MessageType.REQUEST);
        return props().response().getAnnotation(MessageProps.class);
    }

    public int getResponseOpCode() {
        if (type() == MessageType.REQUEST) {
            return getResponseProps().opCode();
        }
        return opCode();
    }

    public int getResponseSize() {
        if (type() == MessageType.REQUEST) {
            return getResponseProps().size();
        }
        return props().size();
    }

    public Class<? extends Message> getRequestClass() {
        Preconditions.checkState(type() == MessageType.RESPONSE);
        return props().request();
    }

    public MessageProps getRequestProps() {
        Preconditions.checkState(type() == MessageType.RESPONSE);
        return props().request().getAnnotation(MessageProps.class);
    }

    public byte[] getCargo() {
        return this.cargo;
    }

    private static final Set<String> IGNORED_PROPERTY_NAMES = ImmutableSet.of("requestClass", "requestProps");

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String toString() {
        return JavaHelpers.autoToString(this, IGNORED_PROPERTY_NAMES);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String verboseToString() {
        return JavaHelpers.autoToStringVerbose(this, IGNORED_PROPERTY_NAMES);
    }
}

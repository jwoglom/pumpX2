package com.jwoglom.pumpx2.pump.messages;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.models.SupportedDevices;
import com.jwoglom.pumpx2.shared.Hex;
import com.jwoglom.pumpx2.shared.JavaHelpers;

import org.json.JSONObject;

import java.util.Set;

public abstract class Message {
    public static final byte[] EMPTY = new byte[]{};
    protected byte[] cargo = null;

    public abstract void parse(byte[] raw);

    public MessageProps props() {
        return getClass().getAnnotation(MessageProps.class);
    }

    public byte opCode() {
        return props().opCode();
    }

    public boolean signed() {
        return props().signed();
    }

    public boolean stream() {
        return props().stream();
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

    public byte getResponseOpCode() {
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

    public Characteristic getCharacteristic() {
        return props().characteristic();
    }

    public byte[] getCargo() {
        return this.cargo;
    }

    public void fillWithEmptyCargo() {
        this.cargo = EMPTY;
    }

    protected byte[] removeSignedRequestHmacBytes(byte[] raw) {
        if (signed() && raw.length == props().size() + 24) {
            return Bytes.dropLastN(raw, 24);
        }
        return raw;
    }

    private static final Set<String> IGNORED_PROPERTY_NAMES = ImmutableSet.of("requestClass", "requestProps", "historyLogStreamBytes", "intMap");

    public String toString() {
        return JavaHelpers.autoToString(this, IGNORED_PROPERTY_NAMES);
    }

    public String verboseToString() {
        return JavaHelpers.autoToStringVerbose(this, IGNORED_PROPERTY_NAMES);
    }

    public String messageName() {
        return getClass().getName().replace("com.jwoglom.pumpx2.pump.messages.", "");
    }

    public String jsonToString() {
        String generatedToString = JavaHelpers.autoToStringJson(this, IGNORED_PROPERTY_NAMES);
        JSONObject obj = new JSONObject();
        obj.put("name", messageName());
        obj.put("messageProps", new JSONObject(propsJsonToString()));
        obj.put("params", new JSONObject(generatedToString));
        obj.put("cargoHex", Hex.encodeHexString(getCargo()));

        return obj.toString(0);
    }

    private String propsJsonToString() {
        JSONObject props = new JSONObject();
        props.putOpt("opCode", props().opCode());
        props.putOpt("size", props().size());
        props.putOpt("variableSize", props().variableSize());
        props.putOpt("stream", props().stream());
        props.putOpt("signed", props().signed());
        props.putOpt("type", props().type());
        if (!props().response().equals(UndefinedMessage.class)) {
            props.putOpt("responseName", props().response().getName().replace("com.jwoglom.pumpx2.pump.messages.", ""));
            props.putOpt("responseOpCode", props().response().getAnnotation(MessageProps.class).opCode());
        }
        if (!props().request().equals(UndefinedMessage.class)) {
            props.putOpt("requestName", props().request().getName().replace("com.jwoglom.pumpx2.pump.messages.", ""));
            props.putOpt("requestOpCode", props().request().getAnnotation(MessageProps.class).opCode());
        }
        props.putOpt("minApi", props().minApi());
        props.putOpt("supportedDevices", props().supportedDevices());
        props.putOpt("characteristicUuid", props().characteristic().getUuid());
        props.putOpt("characteristic", props().characteristic().name());
        props.putOpt("modifiesInsulinDelivery", props().modifiesInsulinDelivery());
        return props.toString(0);
    }
}

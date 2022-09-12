package com.jwoglom.pumpx2.pump.messages.util;

import com.jwoglom.pumpx2.pump.messages.Messages;
import com.jwoglom.pumpx2.shared.JavaHelpers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MessageHelpers {
    public static final String REQUEST_PACKAGE = "com.jwoglom.pumpx2.pump.messages.request";
    public static List<String> getAllPumpRequestMessages() {
        return Arrays.stream(Messages.values())
                .map(message -> message.request().getClass().getName())
                .filter(name -> name.startsWith(REQUEST_PACKAGE))
                .filter(name -> !name.endsWith("Test"))
                .map(MessageHelpers::lastTwoParts)
                .sorted()
                .collect(Collectors.toList());
    }

    public static String lastTwoParts(String name) {
        String[] parts = name.split("\\.");
        return parts[parts.length-2] + "." + parts[parts.length-1];
    }
}

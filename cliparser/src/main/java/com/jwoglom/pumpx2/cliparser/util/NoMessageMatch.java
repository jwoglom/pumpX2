package com.jwoglom.pumpx2.cliparser.util;

import java.util.List;
import java.util.UUID;

public class NoMessageMatch extends RuntimeException {
    public static class NonPumpBtMessage extends NoMessageMatch {
        public NonPumpBtMessage(UUID uuid, String uuidName, String value) {
            super("Non-pump bluetooth message found", uuid, uuidName, value, "");
        }
    }

    public static class NoOpCodeForCharacteristicException extends NoMessageMatch {
        public byte opCode;
        public NoOpCodeForCharacteristicException(byte opCode, UUID uuid, String uuidName, String value, List<String> knownMessages) {
            super("Invalid opCode "+opCode, uuid, uuidName, value, "known messages for this characteristic: "+ (knownMessages == null ? "<none>" : String.join(", ", knownMessages)));
            this.opCode = opCode;
        }
    }

    public UUID uuid;
    public String uuidName;
    public String value;
    public String debugMsg;
    public NoMessageMatch(String text, UUID uuid, String uuidName, String value, String debugMsg) {
        super(text+" with characteristic " + uuid + " (" + uuidName + "): " + value);
        this.uuid = uuid;
        this.uuidName = uuidName;
        this.value = value;
        this.debugMsg = debugMsg;
    }
}

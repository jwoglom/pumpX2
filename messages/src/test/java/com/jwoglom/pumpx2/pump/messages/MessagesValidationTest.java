package com.jwoglom.pumpx2.pump.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class MessagesValidationTest {
    @Test
    public void testAllMessagesRequestResponseMetadataConsistency() {
        for (Messages message : Messages.values()) {
            assertNotNull("request props missing for " + message, message.requestProps());
            assertNotNull("response props missing for " + message, message.responseProps());

            assertEquals("request type should be REQUEST for " + message,
                    MessageType.REQUEST, message.requestProps().type());
            assertEquals("response type should be RESPONSE for " + message,
                    MessageType.RESPONSE, message.responseProps().type());

            assertEquals("the minApi should match for " + message,
                    message.requestProps().minApi(), message.responseProps().minApi());
            assertEquals("supportedDevices should match for " + message,
                    message.requestProps().supportedDevices(), message.responseProps().supportedDevices());
            assertEquals("characteristic should match for " + message,
                    message.requestProps().characteristic(), message.responseProps().characteristic());

            assertNotEquals("request and response opcode should differ for " + message,
                    message.requestOpCode(), message.responseOpCode());
        }
    }
}

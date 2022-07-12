package com.jwoglom.pumpx2.pump.messages;

import static org.junit.Assert.assertFalse;

import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ApiVersionRequest;
import com.jwoglom.pumpx2.pump.messages.util.MessageHelpers;

import org.junit.Test;
import java.util.List;

public class MessageHelpersTest {
    @Test
    public void testGetAllPumpRequestMessages() {
        // Ensure loaded
        ApiVersionRequest foo;
        List<String> requestMessages = MessageHelpers.getAllPumpRequestMessages();
        assertFalse(requestMessages.isEmpty());
    }
}

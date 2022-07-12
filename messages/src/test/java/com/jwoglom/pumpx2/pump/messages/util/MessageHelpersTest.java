package com.jwoglom.pumpx2.pump.messages.util;

import static org.junit.Assert.assertFalse;

import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ApiVersionRequest;
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

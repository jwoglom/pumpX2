package com.jwoglom.pumpx2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ApiVersionRequest;
import com.jwoglom.pumpx2.shared.JavaHelpers;

import org.junit.Test;

import java.util.List;

public class JavaHelpersTest {

    @Test
    public void testGetClassNamesWithPackage() {
        // Ensure loaded
        ApiVersionRequest foo;
        List<String> requestMessages = JavaHelpers.getClassNamesWithPackage("com.jwoglom.pumpx2.pump.messages.request");
        assertFalse(requestMessages.isEmpty());
    }

    @Test
    public void testGetAllPumpRequestMessages() {
        // Ensure loaded
        ApiVersionRequest foo;
        List<String> requestMessages = JavaHelpers.getAllPumpRequestMessages();
        assertFalse(requestMessages.isEmpty());
    }
}

package com.jwoglom.pumpx2.pump.messages;

import static org.junit.Assert.assertThrows;

import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ApiVersionRequest;

import java.lang.reflect.Field;

import org.junit.Test;

public class PacketizeTest {
    @Test
    public void testPacketizeRejectsNullMessage() {
        assertThrows(IllegalArgumentException.class, () -> Packetize.packetize(null, new byte[0], (byte) 0));
        assertThrows(IllegalArgumentException.class, () -> Packetize.packetize(null, new byte[0], (byte) 0, 18));
    }

    @Test
    public void testPacketizeRejectsMessageWithNullCargo() throws Exception {
        ApiVersionRequest request = new ApiVersionRequest();
        Field cargoField = Message.class.getDeclaredField("cargo");
        cargoField.setAccessible(true);
        cargoField.set(request, null);

        assertThrows(IllegalArgumentException.class, () -> Packetize.packetize(request, new byte[0], (byte) 0));
    }
}

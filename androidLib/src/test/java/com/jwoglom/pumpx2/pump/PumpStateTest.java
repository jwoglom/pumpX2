package com.jwoglom.pumpx2.pump;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.bluetooth.TronMessageWrapper;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ApiVersionRequest;

import org.junit.After;
import org.junit.Test;

public class PumpStateTest {
    private static final Characteristic CHARACTERISTIC = Characteristic.CURRENT_STATUS;
    private static final byte TX_ID = 42;

    @After
    public void tearDown() {
        PumpState.clearRequestMessages();
        PumpState.clearSavedPacketArrayLists();
    }

    @Test
    public void savedPacketArrayListIsPeekedUntilRemoved() {
        PacketArrayList packets = packetArrayList((byte) 1);

        PumpState.savePacketArrayList(CHARACTERISTIC, TX_ID, packets);

        assertSame(packets, PumpState.checkForSavedPacketArrayList(CHARACTERISTIC, TX_ID).orElse(null));
        assertSame(packets, PumpState.checkForSavedPacketArrayList(CHARACTERISTIC, TX_ID).orElse(null));

        PumpState.removeSavedPacketArrayList(CHARACTERISTIC, TX_ID);

        assertFalse(PumpState.checkForSavedPacketArrayList(CHARACTERISTIC, TX_ID).isPresent());
    }

    @Test
    public void removingUnknownTransactionIsIdempotent() {
        PumpState.removeSavedPacketArrayList(CHARACTERISTIC, TX_ID);

        assertFalse(PumpState.checkForSavedPacketArrayList(CHARACTERISTIC, TX_ID).isPresent());
    }

    @Test
    public void savingSameTransactionAgainReplacesStaleAccumulator() {
        PacketArrayList first = packetArrayList((byte) 1);
        PacketArrayList second = packetArrayList((byte) 2);

        PumpState.savePacketArrayList(CHARACTERISTIC, TX_ID, first);
        PumpState.savePacketArrayList(CHARACTERISTIC, TX_ID, second);

        assertSame(second, PumpState.checkForSavedPacketArrayList(CHARACTERISTIC, TX_ID).orElse(null));
    }

    @Test
    public void finishingUnknownRequestReportsTheMissingKey() {
        try {
            PumpState.finishRequestMessage(CHARACTERISTIC, TX_ID);
            fail("expected missing request validation to fail");
        } catch (NullPointerException e) {
            assertEquals(
                    "could not find requestMessage for txId 42 and char CURRENT_STATUS",
                    e.getMessage());
        }
    }

    private static PacketArrayList packetArrayList(byte txId) {
        return new TronMessageWrapper(new ApiVersionRequest(), txId)
                .buildPacketArrayList(MessageType.RESPONSE);
    }
}

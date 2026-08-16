package com.jwoglom.pumpx2.pump;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;

import org.junit.Test;

/**
 * Covers the accumulator map's lifecycle directly. TandemBluetoothHandler's use of it needs a
 * BLE peripheral, so it is not exercised here.
 */
public class PumpStateSavedPacketArrayListTest {

    private static PacketArrayList accumulator() {
        // opCode and cargo size are irrelevant here; only map identity is under test.
        return new TestPacketArrayList();
    }

    private static class TestPacketArrayList extends PacketArrayList {
        TestPacketArrayList() {
            super((byte) 1, (byte) 0, (byte) 0, false);
        }
    }

    @Test
    public void testSaveIsIdempotentForTheSameTransaction() {
        // A response spanning three or more packets re-saves the same accumulator once per
        // packet. This used to assert the key was absent and threw on the second save.
        PacketArrayList l = accumulator();
        PumpState.savePacketArrayList(Characteristic.CURRENT_STATUS, (byte) 7, l);
        PumpState.savePacketArrayList(Characteristic.CURRENT_STATUS, (byte) 7, l);

        assertSame(l, PumpState.checkForSavedPacketArrayList(Characteristic.CURRENT_STATUS, (byte) 7).get());
        PumpState.removeSavedPacketArrayList(Characteristic.CURRENT_STATUS, (byte) 7);
    }

    @Test
    public void testSaveAcceptsAReusedTransactionId() {
        // Transaction ids wrap at 256, so the same (characteristic, txId) key recurs for a
        // genuinely new response.
        PacketArrayList first = accumulator();
        PacketArrayList second = accumulator();

        PumpState.savePacketArrayList(Characteristic.CURRENT_STATUS, (byte) 9, first);
        PumpState.savePacketArrayList(Characteristic.CURRENT_STATUS, (byte) 9, second);

        assertSame(second, PumpState.checkForSavedPacketArrayList(Characteristic.CURRENT_STATUS, (byte) 9).get());
        PumpState.removeSavedPacketArrayList(Characteristic.CURRENT_STATUS, (byte) 9);
    }

    @Test
    public void testRemoveDropsTheEntry() {
        PumpState.savePacketArrayList(Characteristic.CURRENT_STATUS, (byte) 11, accumulator());
        assertTrue(PumpState.checkForSavedPacketArrayList(Characteristic.CURRENT_STATUS, (byte) 11).isPresent());

        PumpState.removeSavedPacketArrayList(Characteristic.CURRENT_STATUS, (byte) 11);
        assertEquals(0, PumpState.savedPacketArrayListSize());
    }

    @Test
    public void testRemoveOfAnAbsentEntryIsANoOp() {
        PumpState.removeSavedPacketArrayList(Characteristic.CONTROL, (byte) 33);
        assertEquals(0, PumpState.savedPacketArrayListSize());
    }

    @Test
    public void testEntriesAreScopedPerCharacteristic() {
        PacketArrayList status = accumulator();
        PacketArrayList control = accumulator();
        PumpState.savePacketArrayList(Characteristic.CURRENT_STATUS, (byte) 4, status);
        PumpState.savePacketArrayList(Characteristic.CONTROL, (byte) 4, control);

        assertSame(status, PumpState.checkForSavedPacketArrayList(Characteristic.CURRENT_STATUS, (byte) 4).get());
        assertSame(control, PumpState.checkForSavedPacketArrayList(Characteristic.CONTROL, (byte) 4).get());

        PumpState.removeSavedPacketArrayList(Characteristic.CURRENT_STATUS, (byte) 4);
        assertTrue(PumpState.checkForSavedPacketArrayList(Characteristic.CONTROL, (byte) 4).isPresent());

        PumpState.removeSavedPacketArrayList(Characteristic.CONTROL, (byte) 4);
        assertEquals(0, PumpState.savedPacketArrayListSize());
    }
}

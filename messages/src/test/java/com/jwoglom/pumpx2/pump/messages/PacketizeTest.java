package com.jwoglom.pumpx2.pump.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import com.jwoglom.pumpx2.pump.messages.bluetooth.models.Packet;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ApiVersionRequest;

import org.junit.Test;

import java.util.List;

public class PacketizeTest {

    private static final byte[] EMPTY_KEY = new byte[0];

    @Test
    public void testPartitionListChunksEvenly() {
        byte[] data = new byte[6];
        assertEquals(3, Packetize.partitionList(data, 2).size());
        assertEquals(2, Packetize.partitionList(data, 3).size());
        // A trailing short partition is kept.
        assertEquals(2, Packetize.partitionList(data, 4).size());
        assertEquals(1, Packetize.partitionList(data, 6).size());
        assertEquals(1, Packetize.partitionList(data, 100).size());
        assertEquals(0, Packetize.partitionList(new byte[0], 4).size());
    }

    @Test
    public void testPartitionListRejectsNonPositiveSize() {
        // Previously these silently produced one unchunked partition holding the whole packet,
        // which packetize then sent to the pump as a single Packet.
        byte[] data = new byte[6];
        assertThrows(IllegalArgumentException.class, () -> Packetize.partitionList(data, 0));
        assertThrows(IllegalArgumentException.class, () -> Packetize.partitionList(data, -1));
    }

    @Test
    public void testPacketizeRejectsNullMessage() {
        // Both overloads: the three-arg one dereferenced the message in determineMaxChunkSize
        // before reaching the four-arg one's checks.
        assertThrows(NullPointerException.class,
                () -> Packetize.packetize(null, EMPTY_KEY, (byte) 0));
        assertThrows(NullPointerException.class,
                () -> Packetize.packetize(null, EMPTY_KEY, (byte) 0, 18));
    }

    @Test
    public void testPacketizeRejectsNullCargo() {
        // Previously logged and then fell through into message.getCargo().length one line later.
        assertThrows(NullPointerException.class,
                () -> Packetize.packetize(new NullCargoRequest(), EMPTY_KEY, (byte) 0, 18));
    }

    private static class NullCargoRequest extends ApiVersionRequest {
        NullCargoRequest() {
            this.cargo = null;
        }
    }

    @Test
    public void testPacketizeStillChunksANormalMessage() {
        List<Packet> packets = Packetize.packetize(new ApiVersionRequest(), EMPTY_KEY, (byte) 0, 18);
        assertEquals(1, packets.size());
    }
}

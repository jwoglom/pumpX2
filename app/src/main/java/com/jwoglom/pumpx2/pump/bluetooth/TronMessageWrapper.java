package com.jwoglom.pumpx2.pump.bluetooth;

import com.jwoglom.pumpx2.pump.PumpState;
import com.jwoglom.pumpx2.pump.bluetooth.models.Packet;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.Packetize;

import java.util.List;

public class TronMessageWrapper {

    private final Message message;
    private final List<Packet> packets;

    public TronMessageWrapper(Message requestMessage, byte currentTxId) {
        this.message = requestMessage;

        String authKey = "";
        if (requestMessage.signed()) {
            authKey = com.jwoglom.pumpx2.pump.PumpState.getAuthenticationKey();
        }
        this.packets = Packetize.packetize(requestMessage, authKey, currentTxId);
    }

    // When viewing BT responses in Wireshark, what would be split into two packets has been seen as one packet.
    // The maxChunkSize overrides the default 18-size
    public TronMessageWrapper(Message requestMessage, byte currentTxId, int maxChunkSize) {
        this.message = requestMessage;

        String authKey = "";
        if (requestMessage.signed()) {
            authKey = PumpState.getAuthenticationKey();
        }
        this.packets = Packetize.packetize(requestMessage, authKey, currentTxId, maxChunkSize);
    }


    public List<Packet> packets() {
        return packets;
    }

    public Packet mergeIntoSinglePacket() {
        Packet packet = null;
        for (Packet pkt : packets()) {
            if (packet == null) {
                packet = pkt;
            } else {
                packet = packet.merge(pkt);
            }
        }
        return packet;
    }

    public Message message() {
        return message;
    }

    public PacketArrayList buildPacketArrayList(MessageType type) {
        int opCode = message.getResponseOpCode();
        int size = message.getResponseSize();

        // When parsing a request message, set the expected response opcode/size
        // to the request's size.
        if (type == MessageType.REQUEST) {
            opCode = message.opCode();
            size = message.props().size();
        }
        return PacketArrayList.build(
                (byte) opCode,
                (byte) size,
                packets.get(0).transactionId(),
                message.signed()
        );
    }
}

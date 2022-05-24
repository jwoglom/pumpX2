package com.jwoglom.pumpx2.pump;

import android.util.Pair;

import com.jwoglom.pumpx2.pump.messages.Message;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.SynchronousQueue;

import timber.log.Timber;

public class PumpState {

    public static String pairingCode = null;

    private static Queue<Pair<Message, Byte>> requestMessages = new LinkedList<>();
    public static synchronized void pushRequestMessage(Message m, byte txId) {
        requestMessages.add(Pair.create(m, txId));
    }

    public static synchronized Pair<Message, Byte> popRequestMessage() {
        return requestMessages.poll();
    }
}

package com.jwoglom.pumpx2.pump.messages.request.authentication;

import com.jwoglom.pumpx2.pump.messages.Message;

public abstract class AbstractCentralChallengeRequest extends Message {
    public abstract int getAppInstanceId();

    public abstract byte[] getCentralChallenge();

    public abstract void parse(byte[] raw);
}

package com.jwoglom.pumpx2.pump.messages.response.authentication;

import com.jwoglom.pumpx2.pump.messages.Message;

/*
 * Abstraction over the first step of V1 vs V2 style pairing
 */
public abstract class AbstractChallengeResponse extends Message {
    public abstract void parse(byte[] raw);
    public abstract int getAppInstanceId();
}

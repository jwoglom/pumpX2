package com.jwoglom.pumpx2.pump.messages.response.authentication;

import com.jwoglom.pumpx2.pump.messages.Message;

/*
 * Abstraction over the final step of V1 vs V2 style pairing used to report a bad pairing code
 */
public abstract class AbstractPumpChallengeResponse extends Message {
    public abstract void parse(byte[] raw);
    public abstract int getAppInstanceId();
}

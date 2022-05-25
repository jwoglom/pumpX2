package com.jwoglom.pumpx2.pump.messages.response;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.PumpSettingsRequest;

import java.math.BigInteger;

@MessageProps(
    opCode=83,
    size=9,
    type=MessageType.RESPONSE,
    request=PumpSettingsRequest.class
)
public class PumpSettingsResponse extends Message {
    
    private int lowInsulinThreshold;
    private int cannulaPrimeSize;
    private int autoShutdownEnabled;
    private int autoShutdownDuration;
    private int featureLock;
    private int oledTimeout;
    private int status;
    
    public PumpSettingsResponse() {}
    
    public PumpSettingsResponse(int lowInsulinThreshold, int cannulaPrimeSize, int autoShutdownEnabled, int autoShutdownDuration, int featureLock, int oledTimeout, int status) {
        this.cargo = buildCargo(lowInsulinThreshold, cannulaPrimeSize, autoShutdownEnabled, autoShutdownDuration, featureLock, oledTimeout, status);
        this.lowInsulinThreshold = lowInsulinThreshold;
        this.cannulaPrimeSize = cannulaPrimeSize;
        this.autoShutdownEnabled = autoShutdownEnabled;
        this.autoShutdownDuration = autoShutdownDuration;
        this.featureLock = featureLock;
        this.oledTimeout = oledTimeout;
        this.status = status;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.lowInsulinThreshold = raw[0];
        this.cannulaPrimeSize = raw[1];
        this.autoShutdownEnabled = raw[2];
        this.autoShutdownDuration = Bytes.readShort(raw, 3);
        this.featureLock = raw[5];
        this.oledTimeout = raw[6];
        this.status = Bytes.readShort(raw, 7);
        
    }

    
    public static byte[] buildCargo(int lowInsulinThreshold, int cannulaPrimeSize, int autoShutdownEnabled, int autoShutdownDuration, int featureLock, int oledTimeout, int status) {
        return Bytes.combine(
            new byte[]{ (byte) lowInsulinThreshold }, 
            new byte[]{ (byte) cannulaPrimeSize }, 
            new byte[]{ (byte) autoShutdownEnabled }, 
            Bytes.firstTwoBytesLittleEndian(autoShutdownDuration), 
            new byte[]{ (byte) featureLock }, 
            new byte[]{ (byte) oledTimeout }, 
            Bytes.firstTwoBytesLittleEndian(status));
    }
    
    public int getLowInsulinThreshold() {
        return lowInsulinThreshold;
    }
    public int getCannulaPrimeSize() {
        return cannulaPrimeSize;
    }
    public int getAutoShutdownEnabled() {
        return autoShutdownEnabled;
    }
    public int getAutoShutdownDuration() {
        return autoShutdownDuration;
    }
    public int getFeatureLock() {
        return featureLock;
    }
    public int getOledTimeout() {
        return oledTimeout;
    }
    public int getStatus() {
        return status;
    }
    
}
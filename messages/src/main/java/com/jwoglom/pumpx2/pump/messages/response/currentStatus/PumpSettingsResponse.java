package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.models.StatusMessage;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.PumpSettingsRequest;

@MessageProps(
    opCode=83,
    size=9,
    type=MessageType.RESPONSE,
    request=PumpSettingsRequest.class
)
// NOT a StatusMessage
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

    public PumpSettingsResponse(byte[] raw) {
        parse(raw);
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
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
    /**
     * @return 0 if successful
     */
    public int getStatus() {
        return status;
    }
    
}
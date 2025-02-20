package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.ApiVersionDependent;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CurrentBatteryV2Request;

/**
 * Battery information from the pump. The additional data from the V2 API is not known, and it
 * always seems to have value 0.
 * For API_V2_5
 *
 * To get the standard battery percent use getCurrentBatteryIbc() or getBatteryPercent() as a convenience
 *
 * Use {@link com.jwoglom.pumpx2.pump.messages.response.currentStatus.CurrentBatteryAbstractResponse}
 */
@MessageProps(
    opCode=-111,
    size=11,
    type=MessageType.RESPONSE,
    request=CurrentBatteryV2Request.class,
    minApi=KnownApiVersion.API_V2_5
)
@ApiVersionDependent
public class CurrentBatteryV2Response extends CurrentBatteryAbstractResponse {
    
    private int currentBatteryAbc;
    private int currentBatteryIbc;
    private int chargingStatus;
    private int unknown1;
    private int unknown2;
    private int unknown3;
    private int unknown4;
    
    public CurrentBatteryV2Response() {}
    
    public CurrentBatteryV2Response(int currentBatteryAbc, int currentBatteryIbc, int chargingStatus, int unknown1, int unknown2, int unknown3, int unknown4) {
        this.cargo = buildCargo(currentBatteryAbc, currentBatteryIbc, chargingStatus, unknown1, unknown2, unknown3, unknown4);
        this.currentBatteryAbc = currentBatteryAbc;
        this.currentBatteryIbc = currentBatteryIbc;
        this.chargingStatus = chargingStatus;
        this.unknown1 = unknown1;
        this.unknown2 = unknown2;
        this.unknown3 = unknown3;
        this.unknown4 = unknown4;
        
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.currentBatteryAbc = raw[0];
        this.currentBatteryIbc = raw[1];
        this.chargingStatus = raw[2];
        this.unknown1 = Bytes.readShort(raw, 3);
        this.unknown2 = Bytes.readShort(raw, 5);
        this.unknown3 = Bytes.readShort(raw, 7);
        this.unknown4 = Bytes.readShort(raw, 9);
        
    }

    
    public static byte[] buildCargo(int currentBatteryAbc, int currentBatteryIbc, int chargingStatus, int unknown1, int unknown2, int unknown3, int unknown4) {
        return Bytes.combine(
            new byte[]{ (byte) currentBatteryAbc }, 
            new byte[]{ (byte) currentBatteryIbc }, 
            new byte[]{ (byte) chargingStatus }, 
            Bytes.firstTwoBytesLittleEndian(unknown1), 
            Bytes.firstTwoBytesLittleEndian(unknown2), 
            Bytes.firstTwoBytesLittleEndian(unknown3), 
            Bytes.firstTwoBytesLittleEndian(unknown4));
    }
    
    public int getCurrentBatteryAbc() {
        return currentBatteryAbc;
    }
    public int getCurrentBatteryIbc() {
        return currentBatteryIbc;
    }
    public int getChargingStatus() {
        return chargingStatus;
    }

    public boolean isCharging() {
        return chargingStatus == 1;
    }

    public int getUnknown1() {
        return unknown1;
    }
    public int getUnknown2() {
        return unknown2;
    }
    public int getUnknown3() {
        return unknown3;
    }
    public int getUnknown4() {
        return unknown4;
    }
}
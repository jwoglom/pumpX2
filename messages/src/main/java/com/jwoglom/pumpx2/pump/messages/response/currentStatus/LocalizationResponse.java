package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.LocalizationRequest;

import java.math.BigInteger;

@MessageProps(
    opCode=-89,
    size=7,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CURRENT_STATUS,
    request=LocalizationRequest.class
)
public class LocalizationResponse extends Message {
    
    private int glucoseOUM;
    private int regionSetting;
    private int languageSelected;
    private long languagesAvailableBitmask;
    
    public LocalizationResponse() {}
    
    public LocalizationResponse(int glucoseOUM, int regionSetting, int languageSelected, long languagesAvailableBitmask) {
        this.cargo = buildCargo(glucoseOUM, regionSetting, languageSelected, languagesAvailableBitmask);
        // note: these may be out of order
        this.glucoseOUM = glucoseOUM;
        this.regionSetting = regionSetting;
        this.languageSelected = languageSelected;
        this.languagesAvailableBitmask = languagesAvailableBitmask;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        // note: these may be out of order
        this.glucoseOUM = raw[0];
        this.regionSetting = raw[1];
        this.languageSelected = raw[2];
        this.languagesAvailableBitmask = Bytes.readUint32(raw, 3);
        
    }

    
    public static byte[] buildCargo(int glucoseOUM, int regionSetting, int languageSelected, long languagesAvailableBitmask) {
        return Bytes.combine(
            // note: these may be out of order
            new byte[]{ (byte) glucoseOUM }, 
            new byte[]{ (byte) regionSetting }, 
            new byte[]{ (byte) languageSelected }, 
            Bytes.toUint32(languagesAvailableBitmask));
    }
    
    public int getGlucoseOUM() {
        return glucoseOUM;
    }
    public int getRegionSetting() {
        return regionSetting;
    }
    public int getLanguageSelected() {
        return languageSelected;
    }
    public long getLanguagesAvailableBitmask() {
        return languagesAvailableBitmask;
    }
    
}
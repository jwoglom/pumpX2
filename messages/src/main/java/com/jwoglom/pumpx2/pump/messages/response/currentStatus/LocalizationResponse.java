package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.LocalizationRequest;

@MessageProps(
    opCode=-89,
    size=7,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CURRENT_STATUS,
    request=LocalizationRequest.class
)
public class LocalizationResponse extends Message {
    
    private int glucoseUOM;
    private int languageSelected;
    private int regionSetting;
    private long languagesAvailableBitmask;
    
    public LocalizationResponse() {}
    
    public LocalizationResponse(int glucoseUOM, int languageSelected, int regionSetting, long languagesAvailableBitmask) {
        this.cargo = buildCargo(glucoseUOM, languageSelected, regionSetting, languagesAvailableBitmask);
        this.glucoseUOM = glucoseUOM;
        this.languageSelected = languageSelected;
        this.regionSetting = regionSetting;
        this.languagesAvailableBitmask = languagesAvailableBitmask;
        
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.glucoseUOM = raw[0];
        this.languageSelected = raw[1];
        this.regionSetting = raw[2];
        this.languagesAvailableBitmask = Bytes.readUint32(raw, 3);
        
    }

    
    public static byte[] buildCargo(int glucoseUOM, int languageSelected, int regionSetting, long languagesAvailableBitmask) {
        return Bytes.combine(
            new byte[]{ (byte) glucoseUOM },
            new byte[]{ (byte) languageSelected }, 
            new byte[]{ (byte) regionSetting },
            Bytes.toUint32(languagesAvailableBitmask));
    }
    
    public int getGlucoseUOM() {
        return glucoseUOM;
    }

    public int getGlucoseOUM() {
        return glucoseUOM;
    }
    public int getLanguageSelected() {
        return languageSelected;
    }
    public int getRegionSetting() {
        return regionSetting;
    }
    public long getLanguagesAvailableBitmask() {
        return languagesAvailableBitmask;
    }
    
}

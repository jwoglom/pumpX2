package com.jwoglom.pumpx2.pump.messages.request.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.SupportedDevices;
import com.jwoglom.pumpx2.pump.messages.response.control.CreateNewIDPResponse;

@MessageProps(
    opCode=-26,
    size=35,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    signed=true,
    response=CreateNewIDPResponse.class,
    modifiesInsulinDelivery=true,
    supportedDevices=SupportedDevices.MOBI_ONLY
)
public class CreateNewIDPRequest extends Message {
    private String profileName;
    private int firstSegmentProfileCarbRatio;
    private int firstSegmentProfileBasalRate;
    private int firstSegmentProfileTargetBG;
    private int firstSegmentProfileISF;
    private int profileInsulinDuration;
    private int profileCarbEntry;
    
    public CreateNewIDPRequest() {}

    public CreateNewIDPRequest(String profileName, int firstSegmentProfileCarbRatio, int firstSegmentProfileBasalRate, int firstSegmentProfileTargetBG, int firstSegmentProfileISF, int profileInsulinDuration, int profileCarbEntry) {
        this.cargo = buildCargo(profileName, firstSegmentProfileCarbRatio, firstSegmentProfileBasalRate, firstSegmentProfileTargetBG, firstSegmentProfileISF, profileInsulinDuration, profileCarbEntry);
        this.profileName = profileName;
        this.firstSegmentProfileCarbRatio = firstSegmentProfileCarbRatio;
        this.firstSegmentProfileBasalRate = firstSegmentProfileBasalRate;
        this.firstSegmentProfileTargetBG = firstSegmentProfileTargetBG;
        this.firstSegmentProfileISF = firstSegmentProfileISF;
        this.profileInsulinDuration = profileInsulinDuration;
        this.profileCarbEntry = profileCarbEntry;
        
    }

    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.profileName = Bytes.readString(raw, 0, 16);
        this.firstSegmentProfileCarbRatio = Bytes.readShort(raw, 17);
        // missing 19, 20
        // missing 21, 22
        this.firstSegmentProfileBasalRate = Bytes.readShort(raw, 23);
        this.firstSegmentProfileTargetBG = Bytes.readShort(raw, 25);
        this.firstSegmentProfileISF = Bytes.readShort(raw, 27);
        this.profileInsulinDuration = Bytes.readShort(raw, 29);
        // missing 31, 32
        // missing 33
        // ??
        this.profileCarbEntry = raw[34];
        
    }

    
    public static byte[] buildCargo(String name, int firstSegmentProfileCarbRatio, int firstSegmentProfileBasalRate, int firstSegmentProfileTargetBG, int firstSegmentProfileISF, int profileInsulinDuration, int carbEntry) {
        return Bytes.combine(
            Bytes.writeString(name, 17),
            Bytes.firstTwoBytesLittleEndian(firstSegmentProfileCarbRatio),
            new byte[] {0, 0 }, // ?
            new byte[] {0, 0 }, // ?
            Bytes.firstTwoBytesLittleEndian(firstSegmentProfileBasalRate),
            Bytes.firstTwoBytesLittleEndian(firstSegmentProfileTargetBG),
            Bytes.firstTwoBytesLittleEndian(firstSegmentProfileISF),
            Bytes.firstTwoBytesLittleEndian(profileInsulinDuration),
            // new byte[]{ 0, 0, 0 }, // ?
            new byte[] {31,5,-1}, // ?
            new byte[]{ (byte) carbEntry }
        );
    }
    public String getProfileName() {
        return profileName;
    }
    public int getFirstSegmentProfileCarbRatio() {
        return firstSegmentProfileCarbRatio;
    }
    public int getFirstSegmentProfileBasalRate() {
        return firstSegmentProfileBasalRate;
    }
    public int getProfileCarbEntry() {
        return profileCarbEntry;
    }


    public int getFirstSegmentProfileTargetBG() {
        return firstSegmentProfileTargetBG;
    }

    public int getFirstSegmentProfileISF() {
        return firstSegmentProfileISF;
    }

    public int getProfileInsulinDuration() {
        return profileInsulinDuration;
    }
}
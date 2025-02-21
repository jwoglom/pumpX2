package com.jwoglom.pumpx2.pump.messages.request.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.SupportedDevices;
import com.jwoglom.pumpx2.pump.messages.response.control.CreateIDPResponse;

@MessageProps(
    opCode=-26,
    size=35,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    signed=true,
    response= CreateIDPResponse.class,
    modifiesInsulinDelivery=true,
    supportedDevices=SupportedDevices.MOBI_ONLY
)
public class CreateIDPRequest extends Message {
    private String profileName;
    private int firstSegmentProfileCarbRatio;
    private int firstSegmentProfileBasalRate;
    private int firstSegmentProfileTargetBG;
    private int firstSegmentProfileISF;
    private int profileInsulinDuration;
    private int profileCarbEntry;
    private int sourceIdpId;
    
    public CreateIDPRequest() {}

    /**
     * Creating a new profile.
     */
    public CreateIDPRequest(String profileName, int firstSegmentProfileCarbRatio, int firstSegmentProfileBasalRate, int firstSegmentProfileTargetBG, int firstSegmentProfileISF, int profileInsulinDuration, int profileCarbEntry) {
        this.cargo = buildCargo(profileName, firstSegmentProfileCarbRatio, firstSegmentProfileBasalRate, firstSegmentProfileTargetBG, firstSegmentProfileISF, profileInsulinDuration, profileCarbEntry, -1);
        this.profileName = profileName;
        this.firstSegmentProfileCarbRatio = firstSegmentProfileCarbRatio;
        this.firstSegmentProfileBasalRate = firstSegmentProfileBasalRate;
        this.firstSegmentProfileTargetBG = firstSegmentProfileTargetBG;
        this.firstSegmentProfileISF = firstSegmentProfileISF;
        this.profileInsulinDuration = profileInsulinDuration;
        this.profileCarbEntry = profileCarbEntry;
        this.sourceIdpId = -1;
    }

    /**
     * Duplicating an existing profile. The given IDP id is copied to the new profile.
     */
    public CreateIDPRequest(String profileName, int sourceIdpId) {
        this.cargo = buildCargo(profileName, 0, 0, 0, 0, 0, 0, sourceIdpId);
        this.profileName = profileName;
        this.firstSegmentProfileCarbRatio = 0;
        this.firstSegmentProfileBasalRate = 0;
        this.firstSegmentProfileTargetBG = 0;
        this.firstSegmentProfileISF = 0;
        this.profileInsulinDuration = 0;
        this.profileCarbEntry = 0;
        this.sourceIdpId = sourceIdpId;

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
        this.sourceIdpId = raw[33];
        this.profileCarbEntry = raw[34];
        
    }

    
    public static byte[] buildCargo(String name, int firstSegmentProfileCarbRatio, int firstSegmentProfileBasalRate, int firstSegmentProfileTargetBG, int firstSegmentProfileISF, int profileInsulinDuration, int carbEntry, int idpSourceId) {
        return Bytes.combine(
            Bytes.writeString(name, 17),
            Bytes.firstTwoBytesLittleEndian(firstSegmentProfileCarbRatio),
            new byte[] {0, 0 }, // ?
            new byte[] {0, 0 }, // ?
            Bytes.firstTwoBytesLittleEndian(firstSegmentProfileBasalRate),
            Bytes.firstTwoBytesLittleEndian(firstSegmentProfileTargetBG),
            Bytes.firstTwoBytesLittleEndian(firstSegmentProfileISF),
            Bytes.firstTwoBytesLittleEndian(profileInsulinDuration),
            idpSourceId == -1 ? new byte[] {31,5} : new byte[] {0, 0},
            new byte[]{ (byte) idpSourceId },
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

    public int getSourceIdpId() {
        return sourceIdpId;
    }
}
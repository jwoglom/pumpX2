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
    private String profileName; // 17 bytes at offset 0-16 (null-padded)
    private int firstSegmentProfileCarbRatio; // uint32 at offset 17-20 (milligrams per unit, e.g. 10000 = 10g/unit). Stored in int; values always fit in uint16 range in practice.
    private int firstSegmentProfileStartTime; // uint16 at offset 21-22 (minutes since start of day, 0 = midnight)
    private int firstSegmentProfileBasalRate; // uint16 at offset 23-24
    private int firstSegmentProfileTargetBG; // uint16 at offset 25-26
    private int firstSegmentProfileISF; // uint16 at offset 27-28
    private int profileInsulinDuration; // uint16 at offset 29-30
    private int timeSegmentBitmask; // uint8 at offset 31. Bitmask of which time segment fields are set: bit0=basalRate(1), bit1=carbRatio(2), bit2=targetBG(4), bit3=correctionFactor(8), bit4=startTime(16). 31=all for new profiles, 0 for duplicates.
    private int bolusSettingsBitmask; // uint8 at offset 32. Bitmask of which bolus settings are set: bit0=insulinDuration(1), bit1=maxBolus(2), bit2=carbEntry(4). 5=insulinDuration+carbEntry for new profiles, 0 for duplicates.
    private int profileCarbEntry; // uint8 at offset 34 (boolean: 1=enabled)
    private int sourceIdpId; // uint8 at offset 33 (-1/0xFF = new profile, otherwise source IDP id to duplicate)
    
    public CreateIDPRequest() {}

    /**
     * Creating a new profile.
     */
    public CreateIDPRequest(String profileName, int firstSegmentProfileCarbRatio, int firstSegmentProfileBasalRate, int firstSegmentProfileTargetBG, int firstSegmentProfileISF, int profileInsulinDuration, int profileCarbEntry) {
        this(profileName, firstSegmentProfileCarbRatio, firstSegmentProfileBasalRate, firstSegmentProfileTargetBG, firstSegmentProfileISF, profileInsulinDuration, profileCarbEntry, 31, 5);
    }

    /**
     * Creating a new profile with explicit bitmask values.
     */
    public CreateIDPRequest(String profileName, int firstSegmentProfileCarbRatio, int firstSegmentProfileBasalRate, int firstSegmentProfileTargetBG, int firstSegmentProfileISF, int profileInsulinDuration, int profileCarbEntry, int timeSegmentBitmask, int bolusSettingsBitmask) {
        this.cargo = buildCargo(profileName, firstSegmentProfileCarbRatio, 0, firstSegmentProfileBasalRate, firstSegmentProfileTargetBG, firstSegmentProfileISF, profileInsulinDuration, timeSegmentBitmask, bolusSettingsBitmask, profileCarbEntry, -1);
        this.profileName = profileName;
        this.firstSegmentProfileCarbRatio = firstSegmentProfileCarbRatio;
        this.firstSegmentProfileStartTime = 0;
        this.firstSegmentProfileBasalRate = firstSegmentProfileBasalRate;
        this.firstSegmentProfileTargetBG = firstSegmentProfileTargetBG;
        this.firstSegmentProfileISF = firstSegmentProfileISF;
        this.profileInsulinDuration = profileInsulinDuration;
        this.timeSegmentBitmask = timeSegmentBitmask;
        this.bolusSettingsBitmask = bolusSettingsBitmask;
        this.profileCarbEntry = profileCarbEntry;
        this.sourceIdpId = -1;
    }

    /**
     * Duplicating an existing profile. The given IDP id is copied to the new profile.
     */
    public CreateIDPRequest(String profileName, int sourceIdpId) {
        this.cargo = buildCargo(profileName, 0, 0, 0, 0, 0, 0, 0, 0, 0, sourceIdpId);
        this.profileName = profileName;
        this.firstSegmentProfileCarbRatio = 0;
        this.firstSegmentProfileStartTime = 0;
        this.firstSegmentProfileBasalRate = 0;
        this.firstSegmentProfileTargetBG = 0;
        this.firstSegmentProfileISF = 0;
        this.profileInsulinDuration = 0;
        this.timeSegmentBitmask = 0;
        this.bolusSettingsBitmask = 0;
        this.profileCarbEntry = 0;
        this.sourceIdpId = sourceIdpId;
    }

    public void parse(byte[] raw) {
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.profileName = Bytes.readString(raw, 0, 16);
        this.firstSegmentProfileCarbRatio = (int) Bytes.readUint32(raw, 17); // uint32 at offset 17-20
        this.firstSegmentProfileStartTime = Bytes.readShort(raw, 21); // uint16 at offset 21-22 (minutes since start of day)
        this.firstSegmentProfileBasalRate = Bytes.readShort(raw, 23);
        this.firstSegmentProfileTargetBG = Bytes.readShort(raw, 25);
        this.firstSegmentProfileISF = Bytes.readShort(raw, 27);
        this.profileInsulinDuration = Bytes.readShort(raw, 29);
        this.timeSegmentBitmask = raw[31] & 0xFF;
        this.bolusSettingsBitmask = raw[32] & 0xFF;
        this.sourceIdpId = raw[33];
        this.profileCarbEntry = raw[34];

    }

    
    public static byte[] buildCargo(String name, int firstSegmentProfileCarbRatio, int firstSegmentProfileBasalRate, int firstSegmentProfileTargetBG, int firstSegmentProfileISF, int profileInsulinDuration, int timeSegmentBitmask, int bolusSettingsBitmask, int carbEntry, int idpSourceId) {
        return buildCargo(name, firstSegmentProfileCarbRatio, 0, firstSegmentProfileBasalRate, firstSegmentProfileTargetBG, firstSegmentProfileISF, profileInsulinDuration, timeSegmentBitmask, bolusSettingsBitmask, carbEntry, idpSourceId);
    }

    public static byte[] buildCargo(String name, int firstSegmentProfileCarbRatio, int firstSegmentProfileStartTime, int firstSegmentProfileBasalRate, int firstSegmentProfileTargetBG, int firstSegmentProfileISF, int profileInsulinDuration, int timeSegmentBitmask, int bolusSettingsBitmask, int carbEntry, int idpSourceId) {
        return Bytes.combine(
            Bytes.writeString(name, 17),
            Bytes.toUint32(firstSegmentProfileCarbRatio), // uint32 at offset 17-20
            Bytes.firstTwoBytesLittleEndian(firstSegmentProfileStartTime), // uint16 at offset 21-22 (minutes since start of day)
            Bytes.firstTwoBytesLittleEndian(firstSegmentProfileBasalRate),
            Bytes.firstTwoBytesLittleEndian(firstSegmentProfileTargetBG),
            Bytes.firstTwoBytesLittleEndian(firstSegmentProfileISF),
            Bytes.firstTwoBytesLittleEndian(profileInsulinDuration),
            new byte[]{ (byte) timeSegmentBitmask, (byte) bolusSettingsBitmask },
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
    public int getFirstSegmentProfileStartTime() {
        return firstSegmentProfileStartTime;
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

    /**
     * @return bitmask of which time segment fields are set.
     * Bit 0: basalRate (1), Bit 1: carbRatio (2), Bit 2: targetBG (4),
     * Bit 3: correctionFactor (8), Bit 4: startTime (16).
     * 31 = all fields set (new profile), 0 = none (duplicate).
     */
    public int getTimeSegmentBitmask() {
        return timeSegmentBitmask;
    }

    /**
     * @return bitmask of which bolus settings are set.
     * Bit 0: insulinDuration (1), Bit 1: maxBolus (2), Bit 2: carbEntry (4).
     * 5 = insulinDuration + carbEntry (new profile), 0 = none (duplicate).
     */
    public int getBolusSettingsBitmask() {
        return bolusSettingsBitmask;
    }
}
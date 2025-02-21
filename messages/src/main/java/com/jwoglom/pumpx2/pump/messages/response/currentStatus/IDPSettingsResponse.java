package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.IDPSettingsRequest;

/**
 * Contains information about a given insulin delivery profile.
 * Some information is specific to a time segment on the profile.
 *
 * @see com.jwoglom.pumpx2.pump.messages.response.currentStatus.IDPSegmentResponse
 */
@MessageProps(
    opCode=65,
    size=23,
    type=MessageType.RESPONSE,
    request=IDPSettingsRequest.class
)
public class IDPSettingsResponse extends Message {
    
    private int idpId;
    private String name;
    private int numberOfProfileSegments;
    private int insulinDuration;
    private int maxBolus;
    private boolean carbEntry;
    
    public IDPSettingsResponse() {}
    
    public IDPSettingsResponse(int idpId, String name, int numberOfProfileSegments, int insulinDuration, int maxBolus, boolean carbEntry) {
        this.cargo = buildCargo(idpId, name, numberOfProfileSegments, insulinDuration, maxBolus, carbEntry);
        this.idpId = idpId;
        this.name = name;
        this.numberOfProfileSegments = numberOfProfileSegments;
        this.insulinDuration = insulinDuration;
        this.maxBolus = maxBolus;
        this.carbEntry = carbEntry;
        
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.idpId = raw[0];
        this.name = Bytes.readString(raw, 1, 16);
        this.numberOfProfileSegments = raw[17];
        this.insulinDuration = Bytes.readShort(raw, 18);
        this.maxBolus = Bytes.readShort(raw, 20);
        this.carbEntry = raw[22] != 0;
        
    }

    
    public static byte[] buildCargo(int idpId, String name, int numberOfProfileSegments, int insulinDuration, int maxBolus, boolean carbEntry) {
        return Bytes.combine(
            new byte[]{ (byte) idpId },
            Bytes.writeString(name, 16),
            new byte[]{ (byte) numberOfProfileSegments },
            Bytes.firstTwoBytesLittleEndian(insulinDuration), 
            Bytes.firstTwoBytesLittleEndian(maxBolus), 
            new byte[]{ (byte) (carbEntry ? 1 : 0) });
    }
    
    public int getIdpId() {
        return idpId;
    }
    public String getName() {
        return name;
    }
    public int getNumberOfProfileSegments() {
        return numberOfProfileSegments;
    }
    public int getInsulinDuration() {
        return insulinDuration;
    }
    public int getMaxBolus() {
        return maxBolus;
    }
    public boolean getCarbEntry() {
        return carbEntry;
    }
    
}
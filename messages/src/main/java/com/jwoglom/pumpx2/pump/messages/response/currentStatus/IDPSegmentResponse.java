package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.IDPSegmentRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * If the segment index is invalid, returns ErrorResponse with INVALID_PARAMETER, which causes the
 * pump to terminate the Bluetooth connection, so do NOT arbitrarily query for all segments.
 *
 * @see com.jwoglom.pumpx2.pump.messages.builders.InsulinDeliveryProfileRequestBuilder
 */
@MessageProps(
    opCode=67,
    size=15,
    type=MessageType.RESPONSE,
    request=IDPSegmentRequest.class
)
public class IDPSegmentResponse extends Message {
    
    private int idpId;
    private int segmentIndex;
    private int profileStartTime;
    private int profileBasalRate;
    private long profileCarbRatio;
    private int profileTargetBG;
    private int profileISF;
    private int idpStatusId;
    private Set<IDPSegmentStatus> idpStatus;
    
    public IDPSegmentResponse() {}
    
    public IDPSegmentResponse(int idpId, int segmentIndex, int profileStartTime, int profileBasalRate, long profileCarbRatio, int profileTargetBG, int profileISF, int statusId) {
        this.cargo = buildCargo(idpId, segmentIndex, profileStartTime, profileBasalRate, profileCarbRatio, profileTargetBG, profileISF, statusId);
        this.idpId = idpId;
        this.segmentIndex = segmentIndex;
        this.profileStartTime = profileStartTime;
        this.profileBasalRate = profileBasalRate;
        this.profileCarbRatio = profileCarbRatio;
        this.profileTargetBG = profileTargetBG;
        this.profileISF = profileISF;
        this.idpStatusId = statusId;
        this.idpStatus = getIdpStatus();
        
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.idpId = raw[0];
        this.segmentIndex = raw[1];
        this.profileStartTime = Bytes.readShort(raw, 2);
        this.profileBasalRate = Bytes.readShort(raw, 4);
        this.profileCarbRatio = Bytes.readUint32(raw, 6);
        this.profileTargetBG = Bytes.readShort(raw, 10);
        this.profileISF = Bytes.readShort(raw, 12);
        this.idpStatusId = raw[14];
        this.idpStatus = getIdpStatus();
        
    }

    
    public static byte[] buildCargo(int idpId, int segmentIndex, int profileStartTime, int profileBasalRate, long profileCarbRatio, int profileTargetBG, int profileISF, int idpStatusId) {
        return Bytes.combine(
            new byte[]{ (byte) idpId }, 
            new byte[]{ (byte) segmentIndex }, 
            Bytes.firstTwoBytesLittleEndian(profileStartTime), 
            Bytes.firstTwoBytesLittleEndian(profileBasalRate), 
            Bytes.toUint32(profileCarbRatio), 
            Bytes.firstTwoBytesLittleEndian(profileTargetBG), 
            Bytes.firstTwoBytesLittleEndian(profileISF), 
            new byte[]{ (byte) idpStatusId });
    }

    /**
     * @return ID of the insulin delivery profile in which this segment exists. Not to be confused with "slot"
     */
    public int getIdpId() {
        return idpId;
    }

    /**
     * @return 0-indexed spot in the segments list in which this segment occurs in the profile
     */
    public int getSegmentIndex() {
        return segmentIndex;
    }

    /**
     * @return start time of the profile segment. The profile segment runs from this time until the
     * next-configured profile segment start time in this insulin delivery profile. Expressed in
     * minutes since midnight, see {@link com.jwoglom.pumpx2.pump.messages.models.MinsTime}
     *
     */
    public int getProfileStartTime() {
        return profileStartTime;
    }

    /**
     * @return basal rate of the profile segment in milliunits.
     * See {@link com.jwoglom.pumpx2.pump.messages.models.InsulinUnit#from1000To1(Long)} to convert
     * to a displayable value.
     */
    public int getProfileBasalRate() {
        return profileBasalRate;
    }

    /**
     * @return carb ratio, expressed in 1000-increments. A carb ratio of 5g:1u is expressed as '5000',
     * a carb ratio of 10g:1u is expressed as '10000', etc.
     */
    public long getProfileCarbRatio() {
        return profileCarbRatio;
    }

    /**
     * @return target BG in mg/dL used for corrections
     */
    public int getProfileTargetBG() {
        return profileTargetBG;
    }

    /**
     * @return insulin sensitivity factor. A "correction factor" of 1u:30mg/dL is expressed as '30'.
     */
    public int getProfileISF() {
        return profileISF;
    }
    public int getIdpStatusId() {
        return idpStatusId;
    }

    /**
     * @return which fields in the time segment are filled out
     */
    public Set<IDPSegmentStatus> getIdpStatus() {
        return IDPSegmentStatus.fromBitmask(idpStatusId);
    }


    public enum IDPSegmentStatus {
        BASAL_RATE(1),
        CARB_RATIO(2),
        TARGET_BG(4),
        CORRECTION_FACTOR(8),
        START_TIME(16),
        ;

        private final int id;
        IDPSegmentStatus(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static Set<IDPSegmentStatus> fromBitmask(int mask) {
            Set<IDPSegmentStatus> items = new HashSet<>();
            for (IDPSegmentStatus status : values()) {
                if ((mask & status.getId()) != 0) {
                    items.add(status);
                }
            }

            return items;
        }

        public static int toBitmask(IDPSegmentStatus ...items) {
            int mask = 0;
            for (IDPSegmentStatus item : items) {
                mask += item.getId();
            }
            return mask;
        }
    }
    
}
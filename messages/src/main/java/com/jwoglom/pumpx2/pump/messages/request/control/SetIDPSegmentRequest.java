package com.jwoglom.pumpx2.pump.messages.request.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.SetIDPSegmentResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.IDPSegmentResponse;

import java.util.Set;

@MessageProps(
    opCode=-86,
    size=17,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    signed=true,
    response=SetIDPSegmentResponse.class
)
public class SetIDPSegmentRequest extends Message {
    private int idpId;
    private int segmentIndex;
    private IDPSegmentOperation operation;
    private int operationId;
    private int profileStartTime;
    private int profileBasalRate;
    private long profileCarbRatio;
    private int profileTargetBG;
    private int profileISF;
    private int idpStatusId;
    private Set<IDPSegmentResponse.IDPSegmentStatus> idpStatus;

    public SetIDPSegmentRequest() {}

    public SetIDPSegmentRequest(byte[] raw) {
        parse(raw);
    }

    /**
     * @param idpId the insulin delivery profile ID (NOT SLOT)
     */
    public SetIDPSegmentRequest(int idpId, int segmentIndex, IDPSegmentOperation operation, int profileStartTime, int profileBasalRate, long profileCarbRatio, int profileTargetBG, int profileISF, int idpStatusId) {
        this.cargo = buildCargo(idpId, segmentIndex, operation.id, profileStartTime, profileBasalRate, profileCarbRatio, profileTargetBG, profileISF, idpStatusId);
        this.idpId = idpId;
        this.segmentIndex = segmentIndex;
        this.operation = operation;
        this.operationId = operation.id;
        this.profileStartTime = profileStartTime;
        this.profileBasalRate = profileBasalRate;
        this.profileCarbRatio = profileCarbRatio;
        this.profileTargetBG = profileTargetBG;
        this.profileISF = profileISF;
        this.idpStatusId = idpStatusId;
        this.idpStatus = getIdpStatus();

    }

    public void parse(byte[] raw) {
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.idpId = raw[0];
        this.segmentIndex = raw[2];
        this.operationId = raw[3];
        this.operation = getOperation();
        this.profileStartTime = Bytes.readShort(raw, 4);
        this.profileBasalRate = Bytes.readShort(raw, 6);
        this.profileCarbRatio = Bytes.readUint32(raw, 8);
        this.profileTargetBG = Bytes.readShort(raw, 12);
        this.profileISF = Bytes.readShort(raw, 14);
        this.idpStatusId = raw[16];
        this.idpStatus = getIdpStatus();

    }


    public static byte[] buildCargo(int idpId, int segmentIndex, int operationId, int profileStartTime, int profileBasalRate, long profileCarbRatio, int profileTargetBG, int profileISF, int idpStatusId) {
        return Bytes.combine(
                new byte[]{ (byte) idpId, 1 },
                new byte[]{ (byte) segmentIndex, (byte) operationId },
                Bytes.firstTwoBytesLittleEndian(profileStartTime),
                Bytes.firstTwoBytesLittleEndian(profileBasalRate),
                Bytes.toUint32(profileCarbRatio),
                Bytes.firstTwoBytesLittleEndian(profileTargetBG),
                Bytes.firstTwoBytesLittleEndian(profileISF),
                new byte[]{ (byte) idpStatusId });
    }

    public int getIdpId() {
        return idpId;
    }
    public int getSegmentIndex() {
        return segmentIndex;
    }
    public int getProfileStartTime() {
        return profileStartTime;
    }
    public int getProfileBasalRate() {
        return profileBasalRate;
    }
    public long getProfileCarbRatio() {
        return profileCarbRatio;
    }
    public int getProfileTargetBG() {
        return profileTargetBG;
    }
    public int getProfileISF() {
        return profileISF;
    }
    public int getStatusId() {
        return idpStatusId;
    }
    public Set<IDPSegmentResponse.IDPSegmentStatus> getIdpStatus() {
        return IDPSegmentResponse.IDPSegmentStatus.fromBitmask(idpStatusId);
    }

    public enum IDPSegmentOperation {
        /**
         * Modifies the given segmentId
         */
        MODIFY(0),
        /**
         * Creates a new segment AFTER the current segmentId
         */
        CREATE_AFTER(1),
        /**
         * Deletes the given segmentId
         */
        DELETE(2),
        ;

        private final int id;
        IDPSegmentOperation(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static IDPSegmentOperation fromId(int id) {
            for (IDPSegmentOperation o : values()) {
                if (o.id == id) {
                    return o;
                }
            }
            return null;
        }
    }

    public int getOperationId() {
        return operationId;
    }

    public IDPSegmentOperation getOperation() {
        return IDPSegmentOperation.fromId(operationId);
    }
}
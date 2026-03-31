package com.jwoglom.pumpx2.pump.messages.request.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.SetIDPSettingsResponse;

@MessageProps(
    opCode=-84,
    size=6,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    signed=true,
    response=SetIDPSettingsResponse.class
)
public class SetIDPSettingsRequest extends Message {

    private int idpId;
    private int profileIndex;
    private int profileInsulinDuration;
    private int profileCarbEntry;
    private int changeTypeId;
    private ChangeType changeType;

    public SetIDPSettingsRequest() {}

    /**
     * @deprecated Use {@link #SetIDPSettingsRequest(int, int, int, int, ChangeType)} instead to pass the correct profileIndex.
     */
    @Deprecated
    public SetIDPSettingsRequest(int idpId, int profileInsulinDuration, int profileCarbEntry, ChangeType changeType) {
        this(idpId, 0, profileInsulinDuration, profileCarbEntry, changeType);
    }

    /**
     * @param idpId the insulin delivery profile ID (NOT SLOT)
     * @param profileIndex the slot index (0-5) of this profile in ProfileStatusResponse
     */
    public SetIDPSettingsRequest(int idpId, int profileIndex, int profileInsulinDuration, int profileCarbEntry, ChangeType changeType) {
        this.cargo = buildCargo(idpId, profileIndex, profileInsulinDuration, profileCarbEntry, changeType.id);
        this.idpId = idpId;
        this.profileIndex = profileIndex;
        this.profileInsulinDuration = profileInsulinDuration;
        this.profileCarbEntry = profileCarbEntry;
        this.changeTypeId = changeType.id;
        this.changeType = getChangeType();

    }

    public void parse(byte[] raw) {
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.idpId = raw[0];
        this.profileIndex = raw[1];
        this.profileInsulinDuration = Bytes.readShort(raw, 2);
        this.profileCarbEntry = raw[4];
        this.changeTypeId = raw[5];
        this.changeType = getChangeType();

    }


    public static byte[] buildCargo(int idpId, int profileIndex, int profileInsulinDuration, int profileCarbEntry, int changeTypeId) {
        return Bytes.combine(
            new byte[] {(byte) idpId, (byte) profileIndex},
            Bytes.firstTwoBytesLittleEndian(profileInsulinDuration),
            new byte[]{ (byte) profileCarbEntry, (byte) changeTypeId }
        );
    }

    public int getChangeTypeId() {
        return changeTypeId;
    }

    public int getIdpId() {
        return idpId;
    }

    public int getProfileIndex() {
        return profileIndex;
    }

    public enum ChangeType {
        CHANGE_INSULIN_DURATION(1),
        CHANGE_CARB_ENTRY(4),

        ;

        private final int id;
        ChangeType(int id) {
            this.id = id;
        }

        public static ChangeType fromId(int id) {
            for (ChangeType type : values()) {
                if (type.id == id) {
                    return type;
                }
            }
            return null;
        }
    }

    public ChangeType getChangeType() {
        return ChangeType.fromId(changeTypeId);
    }

    public int getProfileInsulinDuration() {
        return profileInsulinDuration;
    }
    public int getProfileCarbEntry() {
        return profileCarbEntry;
    }
    
    
}
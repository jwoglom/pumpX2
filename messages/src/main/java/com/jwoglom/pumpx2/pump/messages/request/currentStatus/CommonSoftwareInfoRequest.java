package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CommonSoftwareInfoResponse;

/**
 * Requests software info for a specific MCU type.
 */
@MessageProps(
    opCode=-114,
    size=1,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CURRENT_STATUS,
    response=CommonSoftwareInfoResponse.class,
    minApi=KnownApiVersion.API_FUTURE
)
public class CommonSoftwareInfoRequest extends Message {
    private int mcuTypeId;
    private MCUType mcuType;

    public CommonSoftwareInfoRequest() {
        this(MCUType.ARM);
    }

    public CommonSoftwareInfoRequest(int mcuTypeId) {
        this.cargo = buildCargo(mcuTypeId);
        this.mcuTypeId = mcuTypeId;
        this.mcuType = MCUType.fromId(mcuTypeId);
    }

    public CommonSoftwareInfoRequest(MCUType mcuType) {
        this(mcuType.id());
    }

    public CommonSoftwareInfoRequest(byte[] raw) {
        parse(raw);
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.mcuTypeId = raw[0] & 0xFF;
        this.mcuType = MCUType.fromId(mcuTypeId);
    }

    public static byte[] buildCargo(int mcuType) {
        return new byte[]{ (byte) mcuType };
    }

    public int getMcuTypeId() {
        return mcuTypeId;
    }

    public MCUType getMcuType() {
        return mcuType;
    }

    public enum MCUType {
        ARM(0),
        BLE(1),
        MSP(2),
        UNKNOWN(3),
        ;

        private final int id;
        MCUType(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }

        public static MCUType fromId(int id) {
            for (MCUType m : values()) {
                if (m.id == id) return m;
            }
            return UNKNOWN;
        }
    }
}

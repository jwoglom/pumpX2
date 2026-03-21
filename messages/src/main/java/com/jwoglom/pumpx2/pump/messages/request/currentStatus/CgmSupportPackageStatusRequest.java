package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CgmSupportPackageStatusResponse;

@MessageProps(
    opCode=-56,
    size=1,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CURRENT_STATUS,
    response=CgmSupportPackageStatusResponse.class,
    minApi=KnownApiVersion.API_FUTURE
)
public class CgmSupportPackageStatusRequest extends Message {
    private int deviceTypeId;
    private DeviceType deviceType;

    public CgmSupportPackageStatusRequest() {
        this(DeviceType.DEXCOM_G7_ICGM);
    }

    public CgmSupportPackageStatusRequest(int deviceTypeId) {
        this.cargo = buildCargo(deviceTypeId);
        this.deviceTypeId = deviceTypeId;
        this.deviceType = DeviceType.fromId(deviceTypeId);
    }

    public CgmSupportPackageStatusRequest(DeviceType deviceType) {
        this(deviceType.id());
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.deviceTypeId = raw[0] & 0xFF;
        this.deviceType = DeviceType.fromId(deviceTypeId);
    }

    public static byte[] buildCargo(int deviceType) {
        return new byte[]{ (byte) deviceType };
    }

    public int getDeviceTypeId() {
        return deviceTypeId;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public enum DeviceType {
        DEXCOM_G7_ICGM(0),
        UNKNOWN(1),
        ;

        private final int id;
        DeviceType(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }

        public static DeviceType fromId(int id) {
            for (DeviceType d : values()) {
                if (d.id == id) return d;
            }
            return UNKNOWN;
        }
    }
}

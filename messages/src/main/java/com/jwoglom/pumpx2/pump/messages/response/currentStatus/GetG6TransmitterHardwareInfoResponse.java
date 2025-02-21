package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.GetG6TransmitterHardwareInfoRequest;

@MessageProps(
    opCode=-59,
    size=96,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CURRENT_STATUS,
    request= GetG6TransmitterHardwareInfoRequest.class
)
public class GetG6TransmitterHardwareInfoResponse extends Message {

    private String transmitterFirmwareVersion;
    private String transmitterHardwareRevision;
    private String transmitterBleHardwareId;
    private String transmitterSoftwareNumber;
    private byte[] unusedRemaining;
    
    public GetG6TransmitterHardwareInfoResponse() {
        this.cargo = EMPTY;
        
    }

    public GetG6TransmitterHardwareInfoResponse(byte[] raw) {
        this.cargo = raw;
        parse(raw);
    }

    public void parse(byte[] raw) { 
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.transmitterFirmwareVersion = Bytes.readString(raw, 0, 16);
        this.transmitterHardwareRevision = Bytes.readString(raw, 16, 16);
        this.transmitterBleHardwareId = Bytes.readString(raw, 32, 16);
        this.transmitterSoftwareNumber = Bytes.readString(raw, 48, 16);
        this.unusedRemaining = Bytes.dropFirstN(raw, 64);
        
    }


    public String getTransmitterFirmwareVersion() {
        return transmitterFirmwareVersion;
    }

    public String getTransmitterHardwareRevision() {
        return transmitterHardwareRevision;
    }

    public String getTransmitterBleHardwareId() {
        return transmitterBleHardwareId;
    }

    public String getTransmitterSoftwareNumber() {
        return transmitterSoftwareNumber;
    }

    public byte[] getUnusedRemaining() {
        return unusedRemaining;
    }
}
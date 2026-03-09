package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.PumpVersionBRequest;

@MessageProps(
    opCode=-123,
    size=60,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CURRENT_STATUS,
    request=PumpVersionBRequest.class
)
public class PumpVersionBResponse extends Message {
    private String softwareName;
    private long configurationBitsA;
    private long configurationBitsB;
    private long serialNumber;
    private long modelNumber;
    private String pumpRevision;
    private long pcbPartNumberA;
    private long pcbSerialNumberA;
    private String pcbRevisionNumberA;

    public PumpVersionBResponse() {}

    public PumpVersionBResponse(String softwareName, long configurationBitsA, long configurationBitsB,
                                long serialNumber, long modelNumber, String pumpRevision,
                                long pcbPartNumberA, long pcbSerialNumberA, String pcbRevisionNumberA) {
        this.cargo = buildCargo(softwareName, configurationBitsA, configurationBitsB, serialNumber,
            modelNumber, pumpRevision, pcbPartNumberA, pcbSerialNumberA, pcbRevisionNumberA);
        this.softwareName = softwareName;
        this.configurationBitsA = configurationBitsA;
        this.configurationBitsB = configurationBitsB;
        this.serialNumber = serialNumber;
        this.modelNumber = modelNumber;
        this.pumpRevision = pumpRevision;
        this.pcbPartNumberA = pcbPartNumberA;
        this.pcbSerialNumberA = pcbSerialNumberA;
        this.pcbRevisionNumberA = pcbRevisionNumberA;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.softwareName = Bytes.readString(raw, 0, 20);
        this.configurationBitsA = Bytes.readUint32(raw, 20);
        this.configurationBitsB = Bytes.readUint32(raw, 24);
        this.serialNumber = Bytes.readUint32(raw, 28);
        this.modelNumber = Bytes.readUint32(raw, 32);
        this.pumpRevision = Bytes.readString(raw, 36, 8);
        this.pcbPartNumberA = Bytes.readUint32(raw, 44);
        this.pcbSerialNumberA = Bytes.readUint32(raw, 48);
        this.pcbRevisionNumberA = Bytes.readString(raw, 52, 8);
    }

    public static byte[] buildCargo(String softwareName, long configurationBitsA, long configurationBitsB,
                                    long serialNumber, long modelNumber, String pumpRevision,
                                    long pcbPartNumberA, long pcbSerialNumberA, String pcbRevisionNumberA) {
        return Bytes.combine(
            Bytes.writeString(softwareName, 20),
            Bytes.toUint32(configurationBitsA),
            Bytes.toUint32(configurationBitsB),
            Bytes.toUint32(serialNumber),
            Bytes.toUint32(modelNumber),
            Bytes.writeString(pumpRevision, 8),
            Bytes.toUint32(pcbPartNumberA),
            Bytes.toUint32(pcbSerialNumberA),
            Bytes.writeString(pcbRevisionNumberA, 8));
    }

    public String getSoftwareName() {
        return softwareName;
    }

    public long getConfigurationBitsA() {
        return configurationBitsA;
    }

    public long getConfigurationBitsB() {
        return configurationBitsB;
    }

    public long getSerialNumber() {
        return serialNumber;
    }

    public long getModelNumber() {
        return modelNumber;
    }

    public String getPumpRevision() {
        return pumpRevision;
    }

    public long getPcbPartNumberA() {
        return pcbPartNumberA;
    }

    public long getPcbSerialNumberA() {
        return pcbSerialNumberA;
    }

    public String getPcbRevisionNumberA() {
        return pcbRevisionNumberA;
    }
}

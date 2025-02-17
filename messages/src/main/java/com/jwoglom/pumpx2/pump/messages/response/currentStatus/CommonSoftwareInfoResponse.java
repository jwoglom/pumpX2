package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CommonSoftwareInfoRequest;

/**
 * TODO: size may always be 51 and not 60, collect more samples
 */
@MessageProps(
    opCode=-113,
    size=60, // OR 51
    variableSize=true,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CURRENT_STATUS,
    request=CommonSoftwareInfoRequest.class
)
public class CommonSoftwareInfoResponse extends Message {
    
    private String appSoftwareVersion;
    private long appSoftwarePartNumber;
    private long appSoftwarePartDashNumber;
    private long appSoftwarePartRevisionNumber;
    private String bootloaderVersion;
    private long bootloaderPartNumber;
    private Long bootloaderPartDashNumber;
    private Long bootloaderPartRevisionNumber;
    
    public CommonSoftwareInfoResponse() {}

    public CommonSoftwareInfoResponse(byte[] raw) {
        this.cargo = raw;
        parse(raw);
    }
    
    public CommonSoftwareInfoResponse(String appSoftwareVersion, long appSoftwarePartNumber, long appSoftwarePartDashNumber, long appSoftwarePartRevisionNumber, String bootloaderVersion, long bootloaderPartNumber, long bootloaderPartDashNumber, long bootloaderPartRevisionNumber) {
        this.cargo = buildCargo(appSoftwareVersion, appSoftwarePartNumber, appSoftwarePartDashNumber, appSoftwarePartRevisionNumber, bootloaderVersion, bootloaderPartNumber, bootloaderPartDashNumber, bootloaderPartRevisionNumber);
        this.appSoftwareVersion = appSoftwareVersion;
        this.appSoftwarePartNumber = appSoftwarePartNumber;
        this.appSoftwarePartDashNumber = appSoftwarePartDashNumber;
        this.appSoftwarePartRevisionNumber = appSoftwarePartRevisionNumber;
        this.bootloaderVersion = bootloaderVersion;
        this.bootloaderPartNumber = bootloaderPartNumber;
        this.bootloaderPartDashNumber = bootloaderPartDashNumber;
        this.bootloaderPartRevisionNumber = bootloaderPartRevisionNumber;
        
    }

    public CommonSoftwareInfoResponse(String appSoftwareVersion, long appSoftwarePartNumber, long appSoftwarePartDashNumber, long appSoftwarePartRevisionNumber, String bootloaderVersion, long bootloaderPartNumber) {
        this.cargo = buildCargo(appSoftwareVersion, appSoftwarePartNumber, appSoftwarePartDashNumber, appSoftwarePartRevisionNumber, bootloaderVersion, bootloaderPartNumber, null, null);
        this.appSoftwareVersion = appSoftwareVersion;
        this.appSoftwarePartNumber = appSoftwarePartNumber;
        this.appSoftwarePartDashNumber = appSoftwarePartDashNumber;
        this.appSoftwarePartRevisionNumber = appSoftwarePartRevisionNumber;
        this.bootloaderVersion = bootloaderVersion;
        this.bootloaderPartNumber = bootloaderPartNumber;
        this.bootloaderPartDashNumber = null;
        this.bootloaderPartRevisionNumber = null;

    }

    public void parse(byte[] raw) {
        //Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.appSoftwareVersion = Bytes.readString(raw, 0, 18);
        this.appSoftwarePartNumber = Bytes.readUint32(raw, 18);
        this.appSoftwarePartDashNumber = Bytes.readUint32(raw, 22);
        this.appSoftwarePartRevisionNumber = Bytes.readUint32(raw, 26);
        this.bootloaderVersion = Bytes.readString(raw, 30, 17);
        this.bootloaderPartNumber = Bytes.readUint32(raw, 47);
        if (raw.length == 60) {
            this.bootloaderPartDashNumber = Bytes.readUint32(raw, 51);
            this.bootloaderPartRevisionNumber = Bytes.readUint32(raw, 55);
        }
        
    }

    
    public static byte[] buildCargo(String appSoftwareVersion, long appSoftwarePartNumber, long appSoftwareDashNumber, long appSoftwarePartRevisionNumber, String bootloaderVersion, long bootloaderPartNumber, Long bootloaderPartDashNumber, Long bootloaderPartRevisionNumber) {
        return Bytes.combine(
            Bytes.writeString(appSoftwareVersion, 18),
            Bytes.toUint32(appSoftwarePartNumber), 
            Bytes.toUint32(appSoftwareDashNumber), 
            Bytes.toUint32(appSoftwarePartRevisionNumber), 
            Bytes.writeString(bootloaderVersion, 17),
            Bytes.toUint32(bootloaderPartNumber),
                (bootloaderPartDashNumber == null && bootloaderPartRevisionNumber == null) ?
                    new byte[0] : Bytes.combine(
                        Bytes.toUint32(bootloaderPartDashNumber),
                        Bytes.toUint32(bootloaderPartRevisionNumber)));
    }
    
    public String getAppSoftwareVersion() {
        return appSoftwareVersion;
    }
    public long getAppSoftwarePartNumber() {
        return appSoftwarePartNumber;
    }
    public long getAppSoftwarePartDashNumber() {
        return appSoftwarePartDashNumber;
    }
    public long getAppSoftwarePartRevisionNumber() {
        return appSoftwarePartRevisionNumber;
    }
    public String getBootloaderVersion() {
        return bootloaderVersion;
    }
    public long getBootloaderPartNumber() {
        return bootloaderPartNumber;
    }
    public Long getBootloaderPartDashNumber() {
        return bootloaderPartDashNumber;
    }
    public Long getBootloaderPartRevisionNumber() {
        return bootloaderPartRevisionNumber;
    }
    
}
package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CommonSoftwareInfoRequest;

/**
 * Software version string and part numbers of the application and bootloader images of one MCU, in
 * reply to {@link CommonSoftwareInfoRequest}. The pump uses one of two layouts:
 *
 * <ul>
 *   <li><b>51 bytes (Tandem Mobi).</b> Byte 0 echoes the requested MCU type. Then the app version (a
 *   NUL-terminated string in bytes 1-17), its part number (uint32 at 18) and part revision number
 *   (uint32 at 22), then the bootloader version (bytes 26-42), part number (43) and part revision
 *   number (47). This is the layout Tandem's Mobi app reads, and every Mobi response captured so far
 *   (50 of them, from Control-IQ 7.6 to Control-IQ+ 7.9 firmware, for MCU types 0 and 1) is 51 bytes.
 *   In all of them the two version strings are equal: the 16-hex-digit software ID that history log
 *   449 ({@code SoftwareIdHistoryLog}) also records.</li>
 *   <li><b>60 bytes (t:slim X2).</b> No MCU byte. The app version (bytes 0-17), part number, dash
 *   number and revision number (uint32 at 18, 22 and 26), then the bootloader version (bytes 30-46),
 *   part number, dash number and revision number (47, 51 and 55). This is the layout the t:slim X2
 *   t:connect app reads; no X2 response has been captured to confirm it.</li>
 * </ul>
 *
 * The 51-byte layout has no dash numbers: {@link #getAppSoftwarePartDashNumber()} is 0 and
 * {@link #getBootloaderPartDashNumber()} is null for it.
 */
@MessageProps(
    opCode=-113,
    size=60, // OR 51
    variableSize=true,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CURRENT_STATUS,
    request=CommonSoftwareInfoRequest.class,
    minApi=KnownApiVersion.API_FUTURE
)
public class CommonSoftwareInfoResponse extends Message {
    private static final int MOBI_SIZE = 51;

    private Integer mcuTypeId;
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

    /**
     * Builds the 51-byte Tandem Mobi layout.
     */
    public CommonSoftwareInfoResponse(int mcuTypeId, String appSoftwareVersion, long appSoftwarePartNumber, long appSoftwarePartRevisionNumber, String bootloaderVersion, long bootloaderPartNumber, long bootloaderPartRevisionNumber) {
        this.cargo = buildMobiCargo(mcuTypeId, appSoftwareVersion, appSoftwarePartNumber, appSoftwarePartRevisionNumber, bootloaderVersion, bootloaderPartNumber, bootloaderPartRevisionNumber);
        parse(cargo);
    }

    /**
     * @deprecated builds the t:slim X2 field order cut off after the bootloader part number, a 51-byte
     * cargo no pump sends, which {@link #parse(byte[])} reads as the Mobi layout. Use
     * {@link #CommonSoftwareInfoResponse(int, String, long, long, String, long, long)} for the Mobi
     * layout or the eight-argument constructor for the t:slim X2 layout.
     */
    @Deprecated
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
        this.cargo = raw;
        if (raw.length == MOBI_SIZE) {
            this.mcuTypeId = raw[0] & 0xFF;
            this.appSoftwareVersion = Bytes.readString(raw, 1, 17);
            this.appSoftwarePartNumber = Bytes.readUint32(raw, 18);
            this.appSoftwarePartDashNumber = 0;
            this.appSoftwarePartRevisionNumber = Bytes.readUint32(raw, 22);
            this.bootloaderVersion = Bytes.readString(raw, 26, 17);
            this.bootloaderPartNumber = Bytes.readUint32(raw, 43);
            this.bootloaderPartDashNumber = null;
            this.bootloaderPartRevisionNumber = Bytes.readUint32(raw, 47);
            return;
        }
        this.mcuTypeId = null;
        this.appSoftwareVersion = Bytes.readString(raw, 0, 18);
        this.appSoftwarePartNumber = Bytes.readUint32(raw, 18);
        this.appSoftwarePartDashNumber = Bytes.readUint32(raw, 22);
        this.appSoftwarePartRevisionNumber = Bytes.readUint32(raw, 26);
        this.bootloaderVersion = Bytes.readString(raw, 30, 17);
        this.bootloaderPartNumber = Bytes.readUint32(raw, 47);
        // The t:slim X2 app reads through byte 58; buildCargo writes 59 bytes.
        if (raw.length >= 59) {
            this.bootloaderPartDashNumber = Bytes.readUint32(raw, 51);
            this.bootloaderPartRevisionNumber = Bytes.readUint32(raw, 55);
        }
        
    }


    public static byte[] buildMobiCargo(int mcuTypeId, String appSoftwareVersion, long appSoftwarePartNumber, long appSoftwarePartRevisionNumber, String bootloaderVersion, long bootloaderPartNumber, long bootloaderPartRevisionNumber) {
        return Bytes.combine(
            new byte[]{ (byte) mcuTypeId },
            Bytes.writeString(appSoftwareVersion, 17),
            Bytes.toUint32(appSoftwarePartNumber),
            Bytes.toUint32(appSoftwarePartRevisionNumber),
            Bytes.writeString(bootloaderVersion, 17),
            Bytes.toUint32(bootloaderPartNumber),
            Bytes.toUint32(bootloaderPartRevisionNumber));
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
    
    /**
     * The MCU type echoed from the request (51-byte Mobi layout only), else null.
     */
    public Integer getMcuTypeId() {
        return mcuTypeId;
    }

    public CommonSoftwareInfoRequest.MCUType getMcuType() {
        return mcuTypeId == null ? null : CommonSoftwareInfoRequest.MCUType.fromId(mcuTypeId);
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

package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CurrentEgvGuiDataV2Request;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CurrentEGVGuiDataResponse.EGVStatus;

/**
 * Gets the current CGM reading (B variant).
 * This response has the same 8-byte payload structure as CurrentEGVGuiDataResponse:
 * - bytes 0-3: uint32 timestamp (seconds since pump epoch)
 * - bytes 4-5: uint16 CGM reading
 * - byte 6: uint8 EGV status
 * - byte 7: uint8 trend rate
 */
@MessageProps(
    opCode=-63,
    size=8,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CURRENT_STATUS,
    request=CurrentEgvGuiDataV2Request.class,
    minApi=KnownApiVersion.API_FUTURE
)
public class CurrentEgvGuiDataV2Response extends Message {

    private long bgReadingTimestampSeconds;
    private int cgmReading;
    private int egvStatusId;
    private EGVStatus egvStatus;
    private int trendRate;

    public CurrentEgvGuiDataV2Response() {}

    public CurrentEgvGuiDataV2Response(long bgReadingTimestampSeconds, int cgmReading, int egvStatusId, int trendRate) {
        this.cargo = buildCargo(bgReadingTimestampSeconds, cgmReading, egvStatusId, trendRate);
        this.bgReadingTimestampSeconds = bgReadingTimestampSeconds;
        this.cgmReading = cgmReading;
        this.egvStatusId = egvStatusId;
        this.egvStatus = getEgvStatus();
        this.trendRate = trendRate;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.bgReadingTimestampSeconds = Bytes.readUint32(raw, 0);
        this.cgmReading = Bytes.readShort(raw, 4);
        this.egvStatusId = raw[6];
        this.egvStatus = getEgvStatus();
        this.trendRate = raw[7];
    }

    public static byte[] buildCargo(long bgReadingTimestampSeconds, int cgmReading, int egvStatus, int trendRate) {
        return Bytes.combine(
            Bytes.toUint32(bgReadingTimestampSeconds),
            Bytes.firstTwoBytesLittleEndian(cgmReading),
            new byte[]{ (byte) egvStatus },
            new byte[]{ (byte) trendRate });
    }

    public long getBgReadingTimestampSeconds() {
        return bgReadingTimestampSeconds;
    }

    public int getCgmReading() {
        return cgmReading;
    }

    public EGVStatus getEgvStatus() {
        return EGVStatus.fromId(egvStatusId);
    }

    public int getEgvStatusId() {
        return egvStatusId;
    }

    public int getTrendRate() {
        return trendRate;
    }
}

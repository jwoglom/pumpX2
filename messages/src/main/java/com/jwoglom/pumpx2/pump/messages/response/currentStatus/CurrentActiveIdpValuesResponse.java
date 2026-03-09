package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CurrentActiveIdpValuesRequest;

import org.apache.commons.lang3.Validate;

/**
 * Returns the currently active insulin delivery profile (IDP) parameter values
 * in effect right now, based on the active time segment.
 *
 * Byte layout of the cargo (10 bytes total):
 *   bytes 0-3:  currentCarbRatio (uint32 LE) - carb ratio in 1000-increments
 *   byte  4:    unknown/padding
 *   bytes 5-6:  currentTargetBg (uint16 LE) - target blood glucose in mg/dL
 *   bytes 6-7:  currentInsulinDuration (uint16 LE) - insulin duration in minutes
 *   bytes 8-9:  currentIsf (uint16 LE) - insulin sensitivity factor in mg/dL per unit
 *
 * Note: bytes 5-6 (currentTargetBg) and bytes 6-7 (currentInsulinDuration) share byte 6.
 * In practice, currentTargetBg is always less than 256 (its high byte at position 6 is 0),
 * so byte 6 carries only the low byte of currentInsulinDuration. This matches the decompiled
 * Tandem Mobi Android app (CurrentActiveIDPValuesResponse$CurrentActiveIDPValuesCargo):
 *   field c (C8235b, index 29) → currentCarbRatio  (readUint32 from offset 0)
 *   field d (C8708a, index 0)  → currentTargetBg   (readUint16 from copyFrom(bytes, 5, 2))
 *   field e (C8708a, index 1)  → currentInsulinDuration (readUint16 from copyFrom(bytes, 6, 2))
 *   field f (C8708a, index 2)  → currentIsf         (readUint16 from copyFrom(bytes, 8, 2))
 *
 * @see com.jwoglom.pumpx2.pump.messages.response.currentStatus.IDPSegmentResponse
 * @see com.jwoglom.pumpx2.pump.messages.response.currentStatus.IDPSettingsResponse
 */
@MessageProps(
    opCode=-105,
    size=10,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CURRENT_STATUS,
    request=CurrentActiveIdpValuesRequest.class
)
public class CurrentActiveIdpValuesResponse extends Message {

    /**
     * Carb ratio expressed in 1000-increments.
     * A value of 10000 means 10 g/U.
     * See {@link com.jwoglom.pumpx2.pump.messages.models.InsulinUnit#from1000To1(Long)}.
     */
    private long currentCarbRatio;

    /**
     * Target blood glucose in mg/dL used for Control-IQ corrections.
     * Typical range: 80-200 mg/dL (always less than 256, so fits in a single byte).
     */
    private int currentTargetBg;

    /**
     * Duration of insulin action in minutes (Control-IQ insulin duration setting).
     * Typical values: 120, 180, 240, or 300 minutes (2-5 hours).
     */
    private int currentInsulinDuration;

    /**
     * Insulin sensitivity factor (correction factor) in mg/dL per unit.
     * A value of 30 means 1 U corrects 30 mg/dL.
     */
    private int currentIsf;

    public CurrentActiveIdpValuesResponse() {}

    public CurrentActiveIdpValuesResponse(long currentCarbRatio, int currentTargetBg, int currentInsulinDuration, int currentIsf) {
        this.cargo = buildCargo(currentCarbRatio, currentTargetBg, currentInsulinDuration, currentIsf);
        this.currentCarbRatio = currentCarbRatio;
        this.currentTargetBg = currentTargetBg;
        this.currentInsulinDuration = currentInsulinDuration;
        this.currentIsf = currentIsf;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        // bytes 0-3: currentCarbRatio (uint32 LE)
        this.currentCarbRatio = Bytes.readUint32(raw, 0);
        // byte 4: unknown/padding — not parsed
        // bytes 5-6: currentTargetBg (uint16 LE)
        // Note: byte 6 is shared with currentInsulinDuration's low byte.
        // In practice currentTargetBg < 256, so its high byte (byte 6) = 0.
        this.currentTargetBg = Bytes.readShort(raw, 5);
        // bytes 6-7: currentInsulinDuration in minutes (uint16 LE)
        this.currentInsulinDuration = Bytes.readShort(raw, 6);
        // bytes 8-9: currentIsf (uint16 LE)
        this.currentIsf = Bytes.readShort(raw, 8);
    }

    /**
     * Builds the cargo byte array for this response.
     *
     * Because {@code currentTargetBg} (bytes 5-6) and {@code currentInsulinDuration} (bytes 6-7)
     * share byte 6, this method assumes {@code currentTargetBg < 256} (its high byte = 0) so
     * that byte 6 carries only the low byte of {@code currentInsulinDuration} without conflict.
     */
    public static byte[] buildCargo(long currentCarbRatio, int currentTargetBg, int currentInsulinDuration, int currentIsf) {
        // The 3-byte region [5,6,7] covers:
        //   byte 5 = targetBg low byte
        //   byte 6 = targetBg high byte (0, since targetBg < 256) = insulinDuration low byte
        //   byte 7 = insulinDuration high byte
        byte byte5 = (byte) (currentTargetBg & 0xFF);
        byte byte6 = (byte) (currentInsulinDuration & 0xFF);  // targetBg high byte assumed 0
        byte byte7 = (byte) ((currentInsulinDuration >> 8) & 0xFF);

        return Bytes.combine(
            Bytes.toUint32(currentCarbRatio),           // bytes 0-3
            new byte[]{0},                              // byte  4: unknown/padding
            new byte[]{byte5, byte6, byte7},            // bytes 5-7: targetBg + insulinDuration (overlapping)
            Bytes.firstTwoBytesLittleEndian(currentIsf) // bytes 8-9: ISF
        );
    }

    /**
     * @return carb ratio in 1000-increments. A value of 10000 means 10 g/U.
     * See {@link com.jwoglom.pumpx2.pump.messages.models.InsulinUnit#from1000To1(Long)}.
     */
    public long getCurrentCarbRatio() {
        return currentCarbRatio;
    }

    /**
     * @return target blood glucose in mg/dL used for Control-IQ corrections
     */
    public int getCurrentTargetBg() {
        return currentTargetBg;
    }

    /**
     * @return Control-IQ insulin duration in minutes
     */
    public int getCurrentInsulinDuration() {
        return currentInsulinDuration;
    }

    /**
     * @return insulin sensitivity factor in mg/dL per unit
     */
    public int getCurrentIsf() {
        return currentIsf;
    }
}

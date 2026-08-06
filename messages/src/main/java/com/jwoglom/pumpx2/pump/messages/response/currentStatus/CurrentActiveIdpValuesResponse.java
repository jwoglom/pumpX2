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
 * Byte layout of the cargo (10 bytes total), contiguous and non-overlapping:
 *   bytes 0-3:  currentCarbRatio (uint32 LE) - carb ratio in 1000-increments
 *   bytes 4-5:  currentTargetBg (uint16 LE) - target blood glucose in mg/dL
 *   bytes 6-7:  currentInsulinDuration (uint16 LE) - insulin duration in minutes
 *   bytes 8-9:  currentIsf (uint16 LE) - insulin sensitivity factor in mg/dL per unit
 *
 * This layout is confirmed by the captured payload 7017000073002c012800, which decodes to
 * a carb ratio of 6 g/U, a target of 115 mg/dL, a 300 minute (5 hour) insulin duration and
 * an ISF of 40 mg/dL/U -- four independent, clinically standard values at once. See
 * CurrentActiveIdpValuesResponseTest.
 *
 * An earlier revision of this class read currentTargetBg from bytes 5-6 and documented byte 4
 * as padding, which made currentTargetBg and currentInsulinDuration overlap on byte 6 and was
 * only self-consistent while currentTargetBg stayed below 256. That layout decoded the capture
 * above to a target of 11264 mg/dL. It was introduced with the initial schema fill rather than
 * derived from a capture, and no other IDP message overlaps its fields: IDPSegmentResponse
 * packs the same carb ratio / target BG / ISF trio contiguously as uint32, uint16, uint16.
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
        this.currentCarbRatio = Bytes.readUint32(raw, 0);
        this.currentTargetBg = Bytes.readShort(raw, 4);
        this.currentInsulinDuration = Bytes.readShort(raw, 6);
        this.currentIsf = Bytes.readShort(raw, 8);
    }

    public static byte[] buildCargo(long currentCarbRatio, int currentTargetBg, int currentInsulinDuration, int currentIsf) {
        return Bytes.combine(
            Bytes.toUint32(currentCarbRatio),
            Bytes.firstTwoBytesLittleEndian(currentTargetBg),
            Bytes.firstTwoBytesLittleEndian(currentInsulinDuration),
            Bytes.firstTwoBytesLittleEndian(currentIsf)
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

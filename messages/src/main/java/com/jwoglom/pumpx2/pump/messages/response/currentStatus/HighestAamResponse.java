package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.NotificationEnum;
import com.jwoglom.pumpx2.pump.messages.models.NotificationMessage;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.HighestAamRequest;

import java.util.Locale;

/**
 * Malfunction code to report to Tandem is getErrorString
 */
@MessageProps(
    opCode=121,
    size=11, // or 10
    variableSize=true,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CURRENT_STATUS,
    request=HighestAamRequest.class
)
public class HighestAamResponse extends NotificationMessage implements NotificationEnum {

    private long aamId;
    private long faultId;
    private byte[] remaining;

    private String errorString;

    public HighestAamResponse() {}

    public HighestAamResponse(long aamId, long faultId, byte[] remaining) {
        this.cargo = buildCargo(aamId, faultId, remaining);
        parse(cargo);
    }

    public void parse(byte[] raw) {
        Validate.isTrue(
            raw.length == props().size() || raw.length == props().size() - 1,
            "Unexpected HighestAamResponse payload length: %d",
            raw.length
        );
        this.cargo = raw;
        this.aamId = Bytes.readUint32(raw, 0);
        this.faultId = Bytes.readUint32(raw, 4);
        this.remaining = Bytes.dropFirstN(raw, 8);
        this.errorString = getErrorString();
    }

    public static byte[] buildCargo(long aamId, long faultId, byte[] remaining) {
        return Bytes.combine(
            Bytes.toUint32(aamId),
            Bytes.toUint32(faultId),
            remaining
        );
    }

    public long getAamId() {
        return aamId;
    }

    public long getFaultId() {
        return faultId;
    }

    public byte[] getRemaining() {
        return remaining;
    }

    public String getFaultIdAsHex() {
        return Long.toString(getFaultId(), 16);
    }

    /**
     * Backward-compatible alias for getAamId().
     */
    public long getCodeA() {
        return getAamId();
    }

    /**
     * Backward-compatible alias for getFaultId().
     */
    public long getCodeB() {
        return getFaultId();
    }

    public String getErrorString() {
        if (!hasMalfunction()) return "";
        return String.format(Locale.US, "%d-0x%s", getAamId(), getFaultIdAsHex());
    }

    public long[][] IGNORABLE_CODES = new long[][]{
            {0, 0}, // empty
            {3, 8230}, // 3-0x2026
            {18, 8311}, // 18-0x2077 (aamId=18, faultId=8311, remaining={2,3,10}
            {26, 8322}, // 26-0x2082 (cargo: 26,0,0,0,-126,32,0,0,1,1,0)
    };

    /**
     * If the provided response ACTUALLY appears to be a pump malfunction,
     * and not just a matching ID from a concurrent alert or alarm code.
     *
     * @param otherMessages other {@link NotificationMessage} implementations (e.g.
     *                      {@link AlarmStatusResponse}, {@link AlertStatusResponse}) which are also
     *                      present as concurrent notifications along with this reported malfunction
     * @return if the malfunction code appears to be valid due to not having any other matching
     *         concurrent notifications
     */
    public boolean hasMalfunction(NotificationMessage... otherMessages) {
        for (long[] code : IGNORABLE_CODES) {
            if (code[0] == getAamId() && code[1] == getFaultId()) {
                return false;
            }
        }

        if (matchesKnownIds(otherMessages)) {
            return false;
        }

        return true;
    }

    public boolean matchesKnownIds(NotificationMessage... otherMessages) {
        if (otherMessages == null) {
            return false;
        }

        for (NotificationMessage message : otherMessages) {
            if (message.notificationIds().contains((int) getAamId())) {
                return true;
            }
        }

        return false;
    }

    public boolean matchesActiveMalfunction(
        ActiveAamBitsResponse activeMalfunctionBits,
        MalfunctionBitmaskStatusResponse malfunctionBitmaskStatusResponse
    ) {
        if (activeMalfunctionBits != null &&
            activeMalfunctionBits.getAamType() == ActiveAamBitsResponse.AamType.MALFUNCTION)
        {
            return activeMalfunctionBits.hasActiveBit((int) getAamId());
        }

        if (malfunctionBitmaskStatusResponse != null) {
            return malfunctionBitmaskStatusResponse.hasMalfunctionBit((int) getAamId());
        }

        return true;
    }


    @Override
    public int size() {
        return (hasMalfunction() &&
                !StringUtils.isBlank(getErrorString())) ? 1 : 0;
    }

    @Override
    public int getId() {
        return (int) getAamId();
    }

    @Override
    public String getDescription() {
        return getErrorString();
    }
}

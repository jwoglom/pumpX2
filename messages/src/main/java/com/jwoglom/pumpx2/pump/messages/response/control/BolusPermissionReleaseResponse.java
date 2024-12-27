package com.jwoglom.pumpx2.pump.messages.response.control;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.request.control.BolusPermissionReleaseRequest;

/**
 * Returns whether the bolus for which permission was requested by the pump could have its permission
 * be successfully released. Doesn't appear to do anything user-facing, but probably closes out the
 * potential bolus in the history logs.
 */
@MessageProps(
    opCode=-15,
    size=1,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    request= BolusPermissionReleaseRequest.class,
    signed=true,
    minApi=KnownApiVersion.API_V2_5
)
public class BolusPermissionReleaseResponse extends Message {
    private int status;
    
    public BolusPermissionReleaseResponse() {}

    public BolusPermissionReleaseResponse(int status) {
        this.cargo = buildCargo(status);
        this.status = status;
    }

    public static byte[] buildCargo(int status) {
        return Bytes.combine(
                Bytes.firstByteLittleEndian(status)
        );
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Preconditions.checkArgument(raw.length == props().size(), "length: " + raw.length);
        this.status = raw[0];
        this.cargo = raw;
    }


    /**
     * @return 0 if successful
     */
    public int getStatus() {
        return status;
    }

    public ReleaseStatus getReleaseStatus() {
        return ReleaseStatus.fromId(status);
    }

    public enum ReleaseStatus {
        SUCCESS(0),
        FAILURE(1),

        ;

        private final int id;
        ReleaseStatus(int id) {
            this.id = id;
        }

        public static ReleaseStatus fromId(int id) {
            for (ReleaseStatus r : values()) {
                if (r.id == id) {
                    return r;
                }
            }
            return null;
        }
    }
}
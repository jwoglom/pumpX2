package com.jwoglom.pumpx2.pump.messages.response.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.models.StatusMessage;
import com.jwoglom.pumpx2.pump.messages.models.SupportedDevices;
import com.jwoglom.pumpx2.pump.messages.request.control.StopTempRateRequest;

@MessageProps(
    opCode=-89,
    size=3,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    signed=true,
    minApi=KnownApiVersion.MOBI_API_V3_5,
    supportedDevices=SupportedDevices.MOBI_ONLY,
    request=StopTempRateRequest.class
)
public class StopTempRateResponse extends StatusMessage {

    private int status;
    private int tempRateId;
    
    public StopTempRateResponse() {
    }

    public StopTempRateResponse(byte[] raw) {
        parse(raw);
    }

    public StopTempRateResponse(int status, int tempRateId) {
        parse(buildCargo(status, tempRateId));
    }

    public static byte[] buildCargo(int status, int tempRateId) {
        return Bytes.combine(
                new byte[]{(byte) status},
                Bytes.firstTwoBytesLittleEndian(tempRateId));
    }


    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0];
        this.tempRateId = Bytes.readShort(raw, 1);
        
    }

    /**
     * @return 0 if successful
     */
    public int getStatus() {
        return status;
    }

    /**
     * @return the ID of the temp rate which was stopped (see TempRateCompletedHistoryLog)
     */
    public int getTempRateId() {
        return tempRateId;
    }
}
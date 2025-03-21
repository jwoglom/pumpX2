package com.jwoglom.pumpx2.pump.messages.response.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.models.StatusMessage;
import com.jwoglom.pumpx2.pump.messages.models.SupportedDevices;
import com.jwoglom.pumpx2.pump.messages.request.control.SetTempRateRequest;

@MessageProps(
    opCode=-91,
    size=4,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    signed=true,
    minApi=KnownApiVersion.MOBI_API_V3_5,
    supportedDevices=SupportedDevices.MOBI_ONLY,
    request=SetTempRateRequest.class
)
public class SetTempRateResponse extends StatusMessage {

    private int status;
    private int tempRateId;


    public SetTempRateResponse() {}
    
    public SetTempRateResponse(byte[] raw) {
        parse(raw);
    }

    public SetTempRateResponse(int status, int tempRateId) {
        parse(buildCargo(status, tempRateId));
    }

    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0];
        this.tempRateId = Bytes.readShort(raw, 1);
        
    }

    
    public static byte[] buildCargo(int status, int tempRateId) {
        return Bytes.combine(
            new byte[]{ (byte) status },
            Bytes.firstTwoBytesLittleEndian(tempRateId));
    }


    /**
     * @return 0 if successful
     */
    public int getStatus() {
        return status;
    }

    /**
     * @return ID of temp rate which exists in history log (see TempRateActivatedHistoryLog)
     */
    public int getTempRateId() {
        return tempRateId;
    }
}
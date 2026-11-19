package com.jwoglom.pumpx2.pump.messages.request.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.SetMaxBasalLimitResponse;

@MessageProps(
    opCode=-120,
    size=4,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    signed=true,
    response=SetMaxBasalLimitResponse.class
)
public class SetMaxBasalLimitRequest extends Message { 
    private long maxHourlyBasalMilliunits;
    
    public SetMaxBasalLimitRequest() {}

    // t:slim X2 User Guide section 4.10 (pp. 65-66) and Mobi User Guide
    // section 6.3 (pp. 104-105) both specify a 0.2-15 U/hr Basal Rate Limit.
    public static final int MIN_BASAL_LIMIT_MILLIUNITS = 200;
    public static final int MAX_BASAL_LIMIT_MILLIUNITS = 15_000;
    public SetMaxBasalLimitRequest(long maxHourlyBasalMilliunits) {
        Validate.isTrue(maxHourlyBasalMilliunits >= MIN_BASAL_LIMIT_MILLIUNITS, "hourly basal limit must be at least 0.2 units");
        Validate.isTrue(maxHourlyBasalMilliunits <= MAX_BASAL_LIMIT_MILLIUNITS, "basal limit must be less than or equal to 15 units");
        this.cargo = buildCargo(maxHourlyBasalMilliunits);
        this.maxHourlyBasalMilliunits = maxHourlyBasalMilliunits;
        
    }

    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.maxHourlyBasalMilliunits = Bytes.readUint32(raw, 0);
        
    }

    
    public static byte[] buildCargo(long maxHourlyBasalMilliunits) {
        return Bytes.toUint32(maxHourlyBasalMilliunits);
    }
    public long getMaxHourlyBasalMilliunits() {
        return maxHourlyBasalMilliunits;
    }
    
    
}
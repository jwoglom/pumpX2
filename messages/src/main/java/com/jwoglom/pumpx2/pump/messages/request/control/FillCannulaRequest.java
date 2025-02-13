package com.jwoglom.pumpx2.pump.messages.request.control;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.models.SupportedDevices;
import com.jwoglom.pumpx2.pump.messages.response.control.FillCannulaResponse;

/**
 * Fill cannula. Precondition: insulin must be suspended.
 *
 * primeSize is in milliunits.
 */
@MessageProps(
    opCode=-104,
    size=2, // +24
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    signed=true,
    response=FillCannulaResponse.class,
    supportedDevices=SupportedDevices.MOBI_ONLY,
    minApi=KnownApiVersion.MOBI_API_V3_5,
    modifiesInsulinDelivery=true
)
public class FillCannulaRequest extends Message { 
    private int primeSizeMilliUnits;
    
    public FillCannulaRequest() {}

    /**
     *
     * @param primeSizeMilliUnits insulin prime amount in milliunits
     */
    public FillCannulaRequest(int primeSizeMilliUnits) {
        Preconditions.checkArgument(primeSizeMilliUnits > 0, "must have positive prime size");
        // haven't tested this limitation, just a sanity check
        Preconditions.checkArgument(primeSizeMilliUnits <= 3000, "cannot prime more than 3 units");
        this.cargo = buildCargo(primeSizeMilliUnits);
        this.primeSizeMilliUnits = primeSizeMilliUnits;
    }

    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.primeSizeMilliUnits = Bytes.readShort(raw, 0);
        
    }

    
    public static byte[] buildCargo(int primeSize) {
        return Bytes.combine(
            Bytes.firstTwoBytesLittleEndian(primeSize)
        );
    }
    public int getPrimeSizeMilliUnits() {
        return primeSizeMilliUnits;
    }
    
    
}
package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.MalfunctionStatusRequest;

import java.math.BigInteger;
import java.util.Locale;

/**
 * Malfunction code to report to Tandem is getErrorString
 */
@MessageProps(
    opCode=121,
    size=11,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CURRENT_STATUS,
    request= MalfunctionStatusRequest.class
)
public class MalfunctionStatusResponse extends Message {
    
    private long codeA;
    private long codeB;
    private byte[] remaining;

    private String errorString;
    
    public MalfunctionStatusResponse() {}
    
    public MalfunctionStatusResponse(long codeA, long codeB, byte[] remaining) {
        this.cargo = buildCargo(codeA, codeB, remaining);
        parse(cargo);
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.codeA = Bytes.readUint32(raw, 0);
        this.codeB = Bytes.readUint32(raw, 4);
        this.remaining = Bytes.dropFirstN(raw, 8);
        this.errorString = getErrorString();
        
    }

    
    public static byte[] buildCargo(long codeA, long codeB, byte[] remaining) {
        return Bytes.combine(
            Bytes.toUint32(codeA),
            Bytes.toUint32(codeB),
            remaining
        );
    }

    public long getCodeA() {
        return codeA;
    }

    public long getCodeB() {
        return codeB;
    }

    public byte[] getRemaining() {
        return remaining;
    }

    public String getErrorString() {
        // no error
        if (getCodeA() == 0 && getCodeB() == 0) {
            return "";
        }
        return String.format(Locale.US, "%d-%#x", getCodeA(), getCodeB());
    }
}
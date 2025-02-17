package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.UnknownMobiOpcode30Request;

import java.math.BigInteger;

@MessageProps(
    opCode=31,
    size=16,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CURRENT_STATUS,
    request=UnknownMobiOpcode30Request.class
)
public class UnknownMobiOpcode30Response extends Message {
    private long unknown1;
    private long unknown2;
    private long unknown3;
    private long unknown4;
    
    public UnknownMobiOpcode30Response() {
        this.cargo = EMPTY;
    }


    public UnknownMobiOpcode30Response(byte[] raw) {
        this.cargo = raw;
        parse(raw);
    }

    public void parse(byte[] raw) { 
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.unknown1 = Bytes.readUint32(raw, 0);
        this.unknown2 = Bytes.readUint32(raw, 4);
        this.unknown3 = Bytes.readUint32(raw, 8);
        this.unknown4 = Bytes.readUint32(raw, 12);
        
    }


    public long getUnknown1() {
        return unknown1;
    }

    public long getUnknown2() {
        return unknown2;
    }

    public long getUnknown3() {
        return unknown3;
    }

    public long getUnknown4() {
        return unknown4;
    }
}
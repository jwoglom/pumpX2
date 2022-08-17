package com.jwoglom.pumpx2.pump.messages.request.control;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.response.control.BolusPermissionResponse;

import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;

@MessageProps(
    opCode=-94,
    size=24,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    response=BolusPermissionResponse.class,
    minApi=KnownApiVersion.API_V2_5,
    signed=true
)
public class BolusPermissionRequest extends Message {
    private long timeSinceReset;
    private byte[] remainingBytes;
    public BolusPermissionRequest() {
        this.cargo = new byte[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    }

    public BolusPermissionRequest(byte[] raw) {
        parse(raw);
    }

    public BolusPermissionRequest(long timeSinceReset, byte[] remainingBytes) {
        Preconditions.checkArgument(remainingBytes.length == 20, "remainingBytes should be 20 len");
        this.cargo = buildCargo(timeSinceReset);
        this.timeSinceReset = timeSinceReset;
        this.remainingBytes = remainingBytes;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.timeSinceReset = Bytes.readUint32(raw, 0);
        this.remainingBytes = Bytes.dropFirstN(raw, 4);
    }

    public static byte[] buildCargo(long timeSinceReset) {
        return Bytes.combine(
                Bytes.toUint32(timeSinceReset),
                new byte[20]
        );
    }

    public long getTimeSinceReset() {
        return timeSinceReset;
    }

    /*
    for (int i=0; i<24; i++) {
            System.out.print(i+" "+parsedMessage.cargo[i]+" ");
            if (i+4 <= 24) {System.out.print("float: "+Bytes.readFloat(parsedMessage.cargo, i)+" ");}
            if (i+4 <= 24) {System.out.print("uint32: "+Bytes.readUint32(parsedMessage.cargo, i)+" ");}
            if (i+2 <= 24) {System.out.print("short: "+Bytes.readShort(parsedMessage.cargo,i));}
            System.out.println();
        }
     */

    
}
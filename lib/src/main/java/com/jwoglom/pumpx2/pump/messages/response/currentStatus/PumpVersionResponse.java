package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.PumpVersionRequest;

@MessageProps(
    opCode=85,
    size=48,
    type=MessageType.RESPONSE,
    request=PumpVersionRequest.class
)
public class PumpVersionResponse extends Message {
    
    private long armSwVer;
    private long mspSwVer;
    private long configABits;
    private long configBBits;
    private long serialNum;
    private long partNum;
    private String pumpRev;
    private long pcbaSN;
    private String pcbaRev;
    private long modelNum;
    
    public PumpVersionResponse() {}
    
    public PumpVersionResponse(long armSwVer, long mspSwVer, long configABits, long configBBits, long serialNum, long partNum, String pumpRev, long pcbaSN, String pcbaRev, long modelNum) {
        this.cargo = buildCargo(armSwVer, mspSwVer, configABits, configBBits, serialNum, partNum, pumpRev, pcbaSN, pcbaRev, modelNum);
        this.armSwVer = armSwVer;
        this.mspSwVer = mspSwVer;
        this.configABits = configABits;
        this.configBBits = configBBits;
        this.serialNum = serialNum;
        this.partNum = partNum;
        this.pumpRev = pumpRev;
        this.pcbaSN = pcbaSN;
        this.pcbaRev = pcbaRev;
        this.modelNum = modelNum;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.armSwVer = Bytes.readUint32(raw, 0);
        this.mspSwVer = Bytes.readUint32(raw, 4);
        this.configABits = Bytes.readUint32(raw, 8);
        this.configBBits = Bytes.readUint32(raw, 12);
        this.serialNum = Bytes.readUint32(raw, 16);
        this.partNum = Bytes.readUint32(raw, 20);
        this.pumpRev = Bytes.readString(raw, 24, 8);
        this.pcbaSN = Bytes.readUint32(raw, 32);
        this.pcbaRev = Bytes.readString(raw, 36, 8);
        this.modelNum = Bytes.readUint32(raw, 44);
        
    }

    
    public static byte[] buildCargo(long armSwVer, long mspSwVer, long configABits, long configBBits, long serialNum, long partNum, String pumpRev, long pcbaSN, String pcbaRev, long modelNum) {
        return Bytes.combine(
            Bytes.toUint32(armSwVer), 
            Bytes.toUint32(mspSwVer), 
            Bytes.toUint32(configABits), 
            Bytes.toUint32(configBBits), 
            Bytes.toUint32(serialNum), 
            Bytes.toUint32(partNum), 
            Bytes.writeString(pumpRev, 8), 
            Bytes.toUint32(pcbaSN), 
            Bytes.writeString(pcbaRev, 8), 
            Bytes.toUint32(modelNum));
    }
    
    public long getArmSwVer() {
        return armSwVer;
    }
    public long getMspSwVer() {
        return mspSwVer;
    }
    public long getConfigABits() {
        return configABits;
    }
    public long getConfigBBits() {
        return configBBits;
    }
    public long getSerialNum() {
        return serialNum;
    }
    public long getPartNum() {
        return partNum;
    }
    public String getPumpRev() {
        return pumpRev;
    }
    public long getPcbaSN() {
        return pcbaSN;
    }
    public String getPcbaRev() {
        return pcbaRev;
    }
    public long getModelNum() {
        return modelNum;
    }
    
}
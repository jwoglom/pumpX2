package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 69,
    displayName = "Personal Profile (IDP) Action 1/2",
    usedByTidepool = true
)
public class IdpActionHistoryLog extends HistoryLog {
    
    private int idp;
    private int status;
    private int sourceIdp;
    private String name;
    
    public IdpActionHistoryLog() {}
    public IdpActionHistoryLog(long pumpTimeSec, long sequenceNum, int idp, int status, int sourceIdp, String name) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, idp, status, sourceIdp, name);
        this.idp = idp;
        this.status = status;
        this.sourceIdp = sourceIdp;
        this.name = name;
        
    }

    public IdpActionHistoryLog(int idp, int status, int sourceIdp, String name) {
        this(0, 0, idp, status, sourceIdp, name);
    }

    public int typeId() {
        return 69;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.idp = raw[10];
        this.status = raw[11];
        this.sourceIdp = raw[12];
        this.name = Bytes.readString(raw, 18, 8);
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int idp, int status, int sourceIdp, String name) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{69, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{ (byte) idp }, 
            new byte[]{ (byte) status }, 
            new byte[]{ (byte) sourceIdp }, 
            Bytes.writeString(name, 8)));
    }
    public int getIdp() {
        return idp;
    }
    public int getStatus() {
        return status;
    }
    public int getSourceIdp() {
        return sourceIdp;
    }
    public String getName() {
        return name;
    }
    
}
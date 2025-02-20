package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 57,
    displayName = "Personal Profile (IDP) Action 2/2",
    usedByTidepool = true
)
public class IdpActionMsg2HistoryLog extends HistoryLog {
    
    private int idp;
    private String name;
    
    public IdpActionMsg2HistoryLog() {}
    public IdpActionMsg2HistoryLog(long pumpTimeSec, long sequenceNum, int idp, String name) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, idp, name);
        this.idp = idp;
        this.name = name;
        
    }

    public IdpActionMsg2HistoryLog(int idp, String name) {
        this(0, 0, idp, name);
    }

    public int typeId() {
        return 57;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.idp = raw[10];
        this.name = Bytes.readString(raw, 18, 8);
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int idp, String name) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{57, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{ (byte) idp }, 
            Bytes.writeString(name, 8)));
    }
    public int getIdp() {
        return idp;
    }
    public String getName() {
        return name;
    }
    
}
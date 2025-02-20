package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 33,
    displayName = "Cartridge Filled",
    usedByTidepool = true
)
public class CartridgeFilledHistoryLog extends HistoryLog {
    
    private long insulinDisplay;
    private float insulinActual;
    
    public CartridgeFilledHistoryLog() {}
    public CartridgeFilledHistoryLog(long pumpTimeSec, long sequenceNum, long insulinDisplay, float insulinActual) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, insulinDisplay, insulinActual);
        this.insulinDisplay = insulinDisplay;
        this.insulinActual = insulinActual;
        
    }

    public CartridgeFilledHistoryLog(long insulinDisplay, float insulinActual) {
        this(0, 0, insulinDisplay, insulinActual);
    }

    public int typeId() {
        return 33;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.insulinDisplay = Bytes.readUint32(raw, 10);
        this.insulinActual = Bytes.readFloat(raw, 14);
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long insulinDisplay, float insulinActual) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{33, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(insulinDisplay), 
            Bytes.toFloat(insulinActual)));
    }

    /**
     * @return a user-facing displayable format (rounded) of the insulin filled
     */
    public long getInsulinDisplay() {
        return insulinDisplay;
    }

    /**
     * @return the actual amount of insulin filled as reported by the pump mechanically
     */
    public float getInsulinActual() {
        return insulinActual;
    }
    
}
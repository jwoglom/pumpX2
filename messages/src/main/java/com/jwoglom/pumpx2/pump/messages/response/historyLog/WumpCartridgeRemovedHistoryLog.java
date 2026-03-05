package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 302,
    displayName = "WUMP Cartridge Removed",
    internalName = "LID_WUMP_CARTRIDGE_REMOVED"
)
public class WumpCartridgeRemovedHistoryLog extends HistoryLog {


    public WumpCartridgeRemovedHistoryLog() {}
    public WumpCartridgeRemovedHistoryLog(long pumpTimeSec, long sequenceNum) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum);

    }

    public int typeId() {
        return 302;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{46, 1}, // typeId=302 (256+46)
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum)));
    }

}

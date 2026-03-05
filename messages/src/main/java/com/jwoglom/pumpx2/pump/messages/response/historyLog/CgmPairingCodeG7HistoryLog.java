package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 395,
    displayName = "CGM Pairing Code G7",
    internalName = "LID_CGM_PAIRING_CODE_G7"
)
public class CgmPairingCodeG7HistoryLog extends HistoryLog {

    private String pairingCode;

    public CgmPairingCodeG7HistoryLog() {}
    public CgmPairingCodeG7HistoryLog(long pumpTimeSec, long sequenceNum, String pairingCode) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, pairingCode);
        this.pairingCode = pairingCode;

    }

    public CgmPairingCodeG7HistoryLog(String pairingCode) {
        this(0, 0, pairingCode);
    }

    public int typeId() {
        return 395;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.pairingCode = Bytes.readString(raw, 10, 16);

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, String pairingCode) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{(byte)(395 & 0xFF), (byte)(395 >> 8)},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.writeString(pairingCode, 16)));
    }

    public String getPairingCode() {
        return pairingCode;
    }

}

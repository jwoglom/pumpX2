package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 279,
    displayName = "Basal Delivery",
    internalName = "LID_BASAL_DELIVERY",
    usedByTidepool = true
)
public class BasalDeliveryHistoryLog extends HistoryLog {
    
    private int commandedRateSource;
    private int basalDeliveryFlags;
    private int commandedRate;
    private int profileBasalRate;
    private int algorithmRate;
    private int tempRate;
    
    public BasalDeliveryHistoryLog() {}
    public BasalDeliveryHistoryLog(long pumpTimeSec, long sequenceNum, int commandedRateSource, int basalDeliveryFlags, int commandedRate, int profileBasalRate, int algorithmRate, int tempRate) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, commandedRateSource, basalDeliveryFlags, commandedRate, profileBasalRate, algorithmRate, tempRate);
        this.commandedRateSource = commandedRateSource;
        this.basalDeliveryFlags = basalDeliveryFlags;
        this.commandedRate = commandedRate;
        this.profileBasalRate = profileBasalRate;
        this.algorithmRate = algorithmRate;
        this.tempRate = tempRate;
        
    }

    public BasalDeliveryHistoryLog(long pumpTimeSec, long sequenceNum, int commandedRateSource, int commandedRate, int profileBasalRate, int algorithmRate, int tempRate) {
        this(pumpTimeSec, sequenceNum, commandedRateSource, 0, commandedRate, profileBasalRate, algorithmRate, tempRate);
    }

    public BasalDeliveryHistoryLog(int commandedRateSource, int commandedRate, int profileBasalRate, int algorithmRate, int tempRate) {
        this(0, 0, commandedRateSource, commandedRate, profileBasalRate, algorithmRate, tempRate);
    }

    public int typeId() {
        return 279;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.commandedRateSource = Bytes.readShort(raw, 10);
        this.basalDeliveryFlags = Bytes.readShort(raw, 12);
        this.commandedRate = Bytes.readShort(raw, 14);
        this.profileBasalRate = Bytes.readShort(raw, 16);
        this.algorithmRate = Bytes.readShort(raw, 18);
        this.tempRate = Bytes.readShort(raw, 20);
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int commandedRateSource, int basalDeliveryFlags, int commandedRate, int profileBasalRate, int algorithmRate, int tempRate) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(279, 0), // 279 = 256 + 23 (byte1 must be 1, not 0)
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(commandedRateSource),
            Bytes.firstTwoBytesLittleEndian(basalDeliveryFlags),
            Bytes.firstTwoBytesLittleEndian(commandedRate),
            Bytes.firstTwoBytesLittleEndian(profileBasalRate), 
            Bytes.firstTwoBytesLittleEndian(algorithmRate), 
            Bytes.firstTwoBytesLittleEndian(tempRate)));
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int commandedRateSource, int commandedRate, int profileBasalRate, int algorithmRate, int tempRate) {
        return buildCargo(pumpTimeSec, sequenceNum, commandedRateSource, 0, commandedRate, profileBasalRate, algorithmRate, tempRate);
    }

    public int getCommandedRateSource() {
        return commandedRateSource;
    }

    /**
     * Raw value of bytes 12-13. Tandem's export does not name this field, so it is exposed as-is.
     *
     * <p>Only the values 0 and 3 have been observed, across roughly 49k records in two captures.
     * 3 is written when any rate field changed versus the previous record, or when a
     * TempRateActivated / TempRateCompleted / PumpingSuspended / PumpingResumed occurred in the
     * 5-minute interval, and in nearly every record written while suspended. 0 is written for
     * steady-state delivery. It is not a temp-rate indicator: it reads 0 in most records with an
     * active temp rate. The meaning of the two individual bits is unknown; they have never been
     * seen set independently. See https://github.com/jwoglom/pumpx2/issues/77.
     */
    public int getBasalDeliveryFlags() {
        return basalDeliveryFlags;
    }
    public int getCommandedRate() {
        return commandedRate;
    }
    public int getProfileBasalRate() {
        return profileBasalRate;
    }
    public int getAlgorithmRate() {
        return algorithmRate;
    }
    public int getTempRate() {
        return tempRate;
    }

    /**
     * Source of {@code commandedRate} (bytes 10-11).
     *
     * <p>Values 0, 1 and 2 are confirmed on the wire in a Control-IQ-off capture: for 2,
     * {@code commandedRate == tempRate} in 768/768 records; for 0, the pump was suspended with
     * {@code commandedRate} 0 and {@code tempRate}/{@code algorithmRate} both 0xFFFF in 20/20
     * records; for 1, {@code commandedRate == profileBasalRate} in 10/10 records. 3 was observed
     * in an earlier Control-IQ capture, where it predicted {@code algorithmRate != 0xFFFF}.
     * 4 comes from Tandem's export value map and has not yet been observed.
     * See https://github.com/jwoglom/pumpx2/issues/77.
     */
    public enum CommandedRateSource {
        SUSPENDED(0),
        PROFILE(1),
        TEMP_RATE(2),
        ALGORITHM(3),
        TEMP_RATE_AND_ALGORITHM(4),
        ;

        private int id;
        CommandedRateSource(int id) {
            this.id = id;
        }
        public int id() {
            return id;
        }
        public static CommandedRateSource fromId(int id) {
            for (CommandedRateSource s : values()) {
                if (s.id == id) return s;
            }
            return null;
        }
    }

    /**
     * @return the {@link CommandedRateSource} for {@code commandedRateSource}, or null if unknown
     */
    public CommandedRateSource getCommandedRateSourceEnum() {
        return CommandedRateSource.fromId(commandedRateSource);
    }
    
}

package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.NotificationEnum;
import com.jwoglom.pumpx2.pump.messages.models.NotificationMessage;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.MalfunctionStatusRequest;

import java.math.BigInteger;
import java.util.Set;
import java.util.TreeSet;

@MessageProps(
    opCode=119,
    size=8,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CURRENT_STATUS,
    request=MalfunctionStatusRequest.class
)
public class MalfunctionBitmaskStatusResponse extends NotificationMessage {

    private BigInteger bitmask;
    private Set<MalfunctionType> malfunctions;

    public MalfunctionBitmaskStatusResponse() {}

    public MalfunctionBitmaskStatusResponse(BigInteger bitmask) {
        this.cargo = buildCargo(bitmask);
        parse(cargo);
    }

    public MalfunctionBitmaskStatusResponse(long codeA, long codeB) {
        this.cargo = buildCargo(codeA, codeB);
        parse(cargo);
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.bitmask = Bytes.readUint64(raw, 0);
        this.malfunctions = MalfunctionType.fromBitmask(bitmask);
    }


    public static byte[] buildCargo(BigInteger bitmask) {
        return Bytes.toUint64(bitmask.longValue());
    }

    public static byte[] buildCargo(long codeA, long codeB) {
        return Bytes.combine(
            Bytes.toUint32(codeA),
            Bytes.toUint32(codeB)
        );
    }

    public BigInteger getBitmask() {
        return bitmask;
    }

    public Set<MalfunctionType> getMalfunctions() {
        return malfunctions == null ? new TreeSet<>() : malfunctions;
    }

    public long getCodeA() {
        return Bytes.readUint32(getCargo(), 0);
    }

    public long getCodeB() {
        return Bytes.readUint32(getCargo(), 4);
    }

    public boolean hasMalfunctionBit(int bit) {
        return bit >= 0 && bit < 64 && bitmask != null && bitmask.testBit(bit);
    }

    public boolean hasMalfunction(MalfunctionType malfunction) {
        return malfunction != null && hasMalfunctionBit(malfunction.bit());
    }

    @Override
    public int size() {
        return getMalfunctions().size();
    }

    public enum MalfunctionType implements NotificationEnum {
        SOFTWARE(0),
        CPU_CORE(1),
        ARM_MSP_COM(2),
        SENSOR(3),
        LIPO(4),
        TOUCHSCREEN(5),
        NVM(6),
        DISPLAY(7),
        MSP(8),
        MOTOR(9),
        EXTERNAL_BINS(10),
        SW_INIT(11),
        VIBE(12),
        PERIPH_POWER(13),
        P2(14),
        DATALOG(15),
        SPEAKER(16),
        MSP_SUPPLY(17),
        CAL_DATA(18),
        BTLE(19),
        AP(20),
        RTC(21),
        ARM_BLE_COM(22),
        OVERTRAVEL_STALL(23),
        UNDERTRAVEL_STALL(24),
        PUSHOFF_STALL(25)
        ;

        private final int bit;

        MalfunctionType(int bit) {
            this.bit = bit;
        }

        public int bit() {
            return bit;
        }

        @Override
        public int getId() {
            return bit;
        }

        @Override
        public String getDescription() {
            return null;
        }

        public static Set<MalfunctionType> fromBitmask(BigInteger intMap) {
            Set<MalfunctionType> current = new TreeSet<>();
            for (MalfunctionType type : values()) {
                if (intMap.testBit(type.bit())) {
                    current.add(type);
                }
            }
            return current;
        }

        public String toString() {
            return name();
        }
    }
}

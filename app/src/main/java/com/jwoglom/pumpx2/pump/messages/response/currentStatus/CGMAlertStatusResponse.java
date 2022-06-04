package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CGMAlertStatusRequest;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

@MessageProps(
    opCode=75,
    size=8,
    type=MessageType.RESPONSE,
    request=CGMAlertStatusRequest.class
)
public class CGMAlertStatusResponse extends Message {
    
    private BigInteger cgmAlertBitmask;
    
    public CGMAlertStatusResponse() {}
    
    public CGMAlertStatusResponse(long cgmAlertBitmask) {
        this.cargo = buildCargo(cgmAlertBitmask);
        this.cgmAlertBitmask = BigInteger.valueOf(cgmAlertBitmask);
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.cgmAlertBitmask = Bytes.readUint64(raw, 0);
        
    }

    
    public static byte[] buildCargo(long cgmAlertBitmask) {
        return Bytes.combine(
            Bytes.toUint64(cgmAlertBitmask));
    }
    
    public BigInteger getCgmAlertBitmask() {
        return cgmAlertBitmask;
    }

    public Set<CGMAlert> getCgmAlerts() {
        return CGMAlert.fromBitmask(cgmAlertBitmask);
    }

    public enum CGMAlert {
        DEFAULT_CGM_ALERT_0(0),
        FIXED_LOW_CGM_ALERT(1),
        HIGH_CGM_ALERT(2),
        LOW_CGM_ALERT(3),
        CALIBRATION_REQUEST_CGM_ALERT(4),
        RISE_CGM_ALERT(5),
        RAPID_RISE_CGM_ALERT(6),
        FALL_CGM_ALERT(7),
        RAPID_FALL_CGM_ALERT(8),
        LOW_CALIBRATION_ERROR_CGM_ALERT(9),
        HIGH_CALIBRATION_ERROR_CGM_ALERT(10),
        SENSOR_FAILED_CGM_ALERT(11),
        SENSOR_EXPIRING_CGM_ALERT(12),
        SENSOR_EXPIRED_CGM_ALERT(13),
        OUT_OF_RANGE_CGM_ALERT(14),
        DEFAULT_CGM_ALERT_15(15),
        FIRST_START_CALIBRATION_CGM_ALERT(16),
        SECOND_START_CALIBRATION_CGM_ALERT(17),
        CALIBRATION_REQUIRED_CGM_ALERT(18),
        LOW_TRANSMITTER_CGM_ALERT(19),
        TRANSMITTER_CGM_ALERT(20),
        DEFAULT_CGM_ALERT_21(21),
        SENSOR_EXPIRING_CGM_ALERT2(22),
        DEFAULT_CGM_ALERT_23(23),
        DEFAULT_CGM_ALERT_24(24),
        SENSOR_REUSE(25),
        DEFAULT_CGM_ALERT_26(26),
        DEFAULT_CGM_ALERT_27(27),
        DEFAULT_CGM_ALERT_28(28),
        DEFAULT_CGM_ALERT_29(29),
        DEFAULT_CGM_ALERT_30(30),
        DEFAULT_CGM_ALERT_31(31),
        DEFAULT_CGM_ALERT_32(32),
        DEFAULT_CGM_ALERT_33(33),
        DEFAULT_CGM_ALERT_34(34),
        DEFAULT_CGM_ALERT_35(35),
        DEFAULT_CGM_ALERT_36(36),
        DEFAULT_CGM_ALERT_37(37),
        DEFAULT_CGM_ALERT_38(38),
        DEFAULT_CGM_ALERT_39(39),
        DEFAULT_CGM_ALERT_40(40),
        DEFAULT_CGM_ALERT_41(41),
        DEFAULT_CGM_ALERT_42(42),
        DEFAULT_CGM_ALERT_43(43),
        DEFAULT_CGM_ALERT_44(44),
        DEFAULT_CGM_ALERT_45(45),
        DEFAULT_CGM_ALERT_46(46),
        DEFAULT_CGM_ALERT_47(47),
        DEFAULT_CGM_ALERT_48(48),
        DEFAULT_CGM_ALERT_49(49),
        DEFAULT_CGM_ALERT_50(50),
        DEFAULT_CGM_ALERT_51(51),
        DEFAULT_CGM_ALERT_52(52),
        DEFAULT_CGM_ALERT_53(53),
        DEFAULT_CGM_ALERT_55(55),
        DEFAULT_CGM_ALERT_56(56),
        DEFAULT_CGM_ALERT_57(57),
        DEFAULT_CGM_ALERT_58(58),
        DEFAULT_CGM_ALERT_59(59),
        DEFAULT_CGM_ALERT_60(60),
        DEFAULT_CGM_ALERT_61(61),
        DEFAULT_CGM_ALERT_62(62),
        DEFAULT_CGM_ALERT_63(63),

        ;

        private final int id;
        CGMAlert(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }

        public static Set<CGMAlert> fromBitmask(BigInteger bitmask) {
            Set<CGMAlert> set = new HashSet<>();
            for (CGMAlert a : values()) {
                if (bitmask.testBit(a.id())) {
                    set.add(a);
                }
            }
            return set;
        }

        public static BigInteger toBitmask(CGMAlert ...alerts) {
            BigInteger i = BigInteger.ZERO;
            for (CGMAlert a : alerts) {
                i.setBit(a.id());
            }
            return i;
        }
    }
    
}
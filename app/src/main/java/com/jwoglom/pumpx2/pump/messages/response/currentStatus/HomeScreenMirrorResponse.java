package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.HomeScreenMirrorRequest;

import java.math.BigInteger;

import kotlin.UByte;

@MessageProps(
    opCode=57,
    size=9,
    type=MessageType.RESPONSE,
    request=HomeScreenMirrorRequest.class
)
public class HomeScreenMirrorResponse extends Message {
    
    private int cgmTrendIcon;
    private int cgmAlertIcon;
    private int statusIcon0;
    private int statusIcon1;
    private int bolusStatusIcon;
    private int basalStatusIcon;
    private int apControlStateIcon;
    private boolean remainingInsulinPlusIcon;
    private boolean cgmDisplayData;
    
    public HomeScreenMirrorResponse() {}
    
    public HomeScreenMirrorResponse(int cgmTrendIcon, int cgmAlertIcon, int statusIcon0, int statusIcon1, int bolusStatusIcon, int basalStatusIcon, int apControlStateIcon, boolean remainingInsulinPlusIcon, boolean cgmDisplayData) {
        this.cargo = buildCargo(cgmTrendIcon, cgmAlertIcon, statusIcon0, statusIcon1, bolusStatusIcon, basalStatusIcon, apControlStateIcon, remainingInsulinPlusIcon, cgmDisplayData);
        this.cgmTrendIcon = cgmTrendIcon;
        this.cgmAlertIcon = cgmAlertIcon;
        this.statusIcon0 = statusIcon0;
        this.statusIcon1 = statusIcon1;
        this.bolusStatusIcon = bolusStatusIcon;
        this.basalStatusIcon = basalStatusIcon;
        this.apControlStateIcon = apControlStateIcon;
        this.remainingInsulinPlusIcon = remainingInsulinPlusIcon;
        this.cgmDisplayData = cgmDisplayData;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.cgmTrendIcon = Byte.toUnsignedInt(raw[0]);
        this.cgmAlertIcon = Byte.toUnsignedInt(raw[1]);
        this.statusIcon0 = Byte.toUnsignedInt(raw[2]);
        this.statusIcon1 = Byte.toUnsignedInt(raw[3]);
        this.bolusStatusIcon = Byte.toUnsignedInt(raw[4]);
        this.basalStatusIcon = Byte.toUnsignedInt(raw[5]);
        this.apControlStateIcon = Byte.toUnsignedInt(raw[6]);
        this.remainingInsulinPlusIcon = raw[7] != 0;
        this.cgmDisplayData = raw[8] != 0;
        
    }

    
    public static byte[] buildCargo(int cgmTrendIcon, int cgmAlertIcon, int statusIcon0, int statusIcon1, int bolusStatusIcon, int basalStatusIcon, int apControlStateIcon, boolean remainingInsulinPlusIcon, boolean cgmDisplayData) {
        return Bytes.combine(
            new byte[]{ (byte) cgmTrendIcon }, 
            new byte[]{ (byte) cgmAlertIcon }, 
            new byte[]{ (byte) statusIcon0 }, 
            new byte[]{ (byte) statusIcon1 }, 
            new byte[]{ (byte) bolusStatusIcon }, 
            new byte[]{ (byte) basalStatusIcon }, 
            new byte[]{ (byte) apControlStateIcon }, 
            new byte[]{ (byte) (remainingInsulinPlusIcon ? 1 : 0) }, 
            new byte[]{ (byte) (cgmDisplayData ? 1 : 0) });
    }

    public CGMTrendIcon getCgmTrendIcon() {
        return CGMTrendIcon.fromId(cgmTrendIcon);
    }
    public CGMAlertIcon getCgmAlertIcon() {
        return CGMAlertIcon.fromId(cgmAlertIcon);
    }
    public StatusIcon0 getStatusIcon0() {
        return StatusIcon0.fromId(statusIcon0);
    }
    public StatusIcon1 getStatusIcon1() {
        return StatusIcon1.fromId(statusIcon1);
    }
    public BolusStatusIcon getBolusStatusIcon() {
        return BolusStatusIcon.fromId(bolusStatusIcon);
    }
    public BasalStatusIcon getBasalStatusIcon() {
        return BasalStatusIcon.fromId(basalStatusIcon);
    }
    public ApControlStateIcon getApControlStateIcon() {
        return ApControlStateIcon.fromId(apControlStateIcon);
    }
    public boolean getRemainingInsulinPlusIcon() {
        return remainingInsulinPlusIcon;
    }
    public boolean getCgmDisplayData() {
        return cgmDisplayData;
    }

    public enum CGMTrendIcon {
        NO_ARROW(0),
        ARROW_DOUBLE_UP(1),
        ARROW_UP(2),
        ARROW_UP_RIGHT(3),
        ARROW_FLAT(4),
        ARROW_DOWN_RIGHT(5),
        ARROW_DOWN(6),
        ARROW_DOUBLE_DOWN(7)
        ;

        private final int id;
        CGMTrendIcon(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }

        public static CGMTrendIcon fromId(int id) {
            for (CGMTrendIcon i : values()) {
                if (i.id() == id) {
                    return i;
                }
            }
            return null;
        }
    }

    public enum CGMAlertIcon {
        STARTUP_1(0),
        STARTUP_2(1),
        STARTUP_3(2),
        STARTUP_4(3),
        CALIBRATE(4),
        STARTUP_CALIBRATE(5),
        CHECKMARK_BLOOD_DROP(6),
        ERROR_HIGH_WEDGE(7),
        ERROR_LOW_WEDGE(8),
        REPLACE_SENSOR(11),
        REPLACE_TRANSMITTER(12),
        OUT_OF_RANGE(13),
        FAILED_SENSOR(14),
        TRIPLE_DASHES(15),
        LOW(16),
        HIGH(17),
        NO_ERROR(255),
        ;

        private final int id;
        CGMAlertIcon(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }

        public static CGMAlertIcon fromId(int id) {
            for (CGMAlertIcon i : values()) {
                if (i.id() == id) {
                    return i;
                }
            }
            return null;
        }
    }

    public enum StatusIcon0 {
        AAM(0),
        CGM_TRANSMITTER(1),
        CGM_TRANSMITTER_OOR(4),
        HIDE_ICON(200);
        ;

        private final int id;
        StatusIcon0(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }

        public static StatusIcon0 fromId(int id) {
            for (StatusIcon0 i : values()) {
                if (i.id() == id) {
                    return i;
                }
            }
            return null;
        }
    }

    public enum StatusIcon1 {
        AAM(0),
        BLOOD_DROP(1),
        HIDE_ICON(200);
        ;

        private final int id;
        StatusIcon1(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }

        public static StatusIcon1 fromId(int id) {
            for (StatusIcon1 i : values()) {
                if (i.id() == id) {
                    return i;
                }
            }
            return null;
        }
    }

    public enum BolusStatusIcon {
        BOLUS(0),
        AUTO_BOLUS(1),
        BLACK_LOCK_SECURITY_PIN(2),
        GREY_LOCK_SECURITY_PIN_CGM_GRAPH_ACTIVE(3),
        HIDE_ICON(200)
        ;

        private final int id;
        BolusStatusIcon(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }

        public static BolusStatusIcon fromId(int id) {
            for (BolusStatusIcon i : values()) {
                if (i.id() == id) {
                    return i;
                }
            }
            return null;
        }
    }

    public enum BasalStatusIcon {
        BASAL(0),
        ZERO_BASAL(1),
        TEMP_RATE(2),
        ZERO_TEMP_RATE(3),
        SUSPEND(4),
        HYPO_SUSPEND_BASAL_IQ(5),
        INCREASE_BASAL(6),
        ATTENUATED_BASAL(7),
        HIDE(200)
        ;

        private final int id;
        BasalStatusIcon(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }

        public static BasalStatusIcon fromId(int id) {
            for (BasalStatusIcon i : values()) {
                if (i.id() == id) {
                    return i;
                }
            }
            return null;
        }
    }

    public enum ApControlStateIcon {
        STATE_GRAY(0),
        STATE_GRAY_RED_BIQ_CIQ_BASAL_SUSPENDED(1),
        STATE_GRAY_BLUE_CIQ_INCREASE_BASAL(2),
        STATE_GRAY_ORANGE_CIQ_ATTENUATION_BASAL(3),
        HIDE_ICON(200)
        ;

        private final int id;
        ApControlStateIcon(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }

        public static ApControlStateIcon fromId(int id) {
            for (ApControlStateIcon i : values()) {
                if (i.id() == id) {
                    return i;
                }
            }
            return null;
        }
    }
    
}
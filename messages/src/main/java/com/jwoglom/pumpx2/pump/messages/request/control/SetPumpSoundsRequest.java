package com.jwoglom.pumpx2.pump.messages.request.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.MultiDay;
import com.jwoglom.pumpx2.pump.messages.response.control.SetPumpSoundsResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.PumpGlobalsResponse;

import java.util.Set;
import java.util.TreeSet;

@MessageProps(
    opCode=-28,
    size=9,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    signed=true,
    response=SetPumpSoundsResponse.class
)
public class SetPumpSoundsRequest extends Message {
    private int quickBolusAnnunRaw;
    private int generalAnnunRaw;
    private int reminderAnnunRaw;
    private int alertAnnunRaw;
    private int alarmAnnunRaw;
    private int cgmAlertAnnunA;
    private int cgmAlertAnnunB;
    private int changeBitmaskRaw;

    private PumpGlobalsResponse.AnnunciationEnum quickBolusAnnun;
    private PumpGlobalsResponse.AnnunciationEnum generalAnnun;
    private PumpGlobalsResponse.AnnunciationEnum reminderAnnun;
    private PumpGlobalsResponse.AnnunciationEnum alertAnnun;
    private PumpGlobalsResponse.AnnunciationEnum alarmAnnun;
    private CgmAlertAnnunciationEnum cgmAlertAnnun;
    private Set<ChangeBitmask> changeBitmask;

    public SetPumpSoundsRequest() {}

    public SetPumpSoundsRequest(int quickBolusAnnunRaw, int generalAnnunRaw, int reminderAnnunRaw, int alertAnnunRaw, int alarmAnnunRaw, int cgmAlertAnnunA, int cgmAlertAnnunB, int changeBitmaskRaw) {
        this.cargo = buildCargo(quickBolusAnnunRaw, generalAnnunRaw, reminderAnnunRaw, alertAnnunRaw, alarmAnnunRaw, cgmAlertAnnunA, cgmAlertAnnunB, changeBitmaskRaw);
        parse(cargo);
    }

    public SetPumpSoundsRequest(byte[] raw) {
        parse(raw);
    }

    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.quickBolusAnnunRaw = raw[1];
        this.generalAnnunRaw = raw[2];
        this.reminderAnnunRaw = raw[3];
        this.alertAnnunRaw = raw[4];
        this.alarmAnnunRaw = raw[5];
        this.cgmAlertAnnunA = raw[6];
        this.cgmAlertAnnunB = raw[7];
        this.changeBitmaskRaw = raw[8];

        this.quickBolusAnnun = PumpGlobalsResponse.AnnunciationEnum.fromId(quickBolusAnnunRaw);
        this.generalAnnun = PumpGlobalsResponse.AnnunciationEnum.fromId(generalAnnunRaw);
        this.reminderAnnun = PumpGlobalsResponse.AnnunciationEnum.fromId(reminderAnnunRaw);
        this.alertAnnun = PumpGlobalsResponse.AnnunciationEnum.fromId(alertAnnunRaw);
        this.alarmAnnun = PumpGlobalsResponse.AnnunciationEnum.fromId(alarmAnnunRaw);
        this.cgmAlertAnnun = CgmAlertAnnunciationEnum.fromIds(cgmAlertAnnunA, cgmAlertAnnunB);
        this.changeBitmask = ChangeBitmask.fromBitmask(changeBitmaskRaw);
        
    }

    
    public static byte[] buildCargo(int quickBolusAnnunRaw, int generalAnnunRaw, int reminderAnnunRaw, int alertAnnunRaw, int alarmAnnunRaw, int cgmAlertAnnunA, int cgmAlertAnnunB, int changeBitmask) {
        return Bytes.combine(
            new byte[]{0},
            new byte[]{ (byte) quickBolusAnnunRaw },
            new byte[]{ (byte) generalAnnunRaw },
            new byte[]{ (byte) reminderAnnunRaw },
            new byte[]{ (byte) alertAnnunRaw },
            new byte[]{ (byte) alarmAnnunRaw },
            new byte[]{ (byte) cgmAlertAnnunA },
            new byte[]{ (byte) cgmAlertAnnunB },
            new byte[]{ (byte) changeBitmask }

        );
    }

    public int getQuickBolusAnnunRaw() {
        return quickBolusAnnunRaw;
    }

    public int getGeneralAnnunRaw() {
        return generalAnnunRaw;
    }

    public int getReminderAnnunRaw() {
        return reminderAnnunRaw;
    }

    public int getAlertAnnunRaw() {
        return alertAnnunRaw;
    }

    public int getAlarmAnnunRaw() {
        return alarmAnnunRaw;
    }

    public int getCgmAlertAnnunA() {
        return cgmAlertAnnunA;
    }

    public int getCgmAlertAnnunB() {
        return cgmAlertAnnunB;
    }

    public int getChangeBitmaskRaw() {
        return changeBitmaskRaw;
    }

    public PumpGlobalsResponse.AnnunciationEnum getQuickBolusAnnun() {
        return quickBolusAnnun;
    }

    public PumpGlobalsResponse.AnnunciationEnum getGeneralAnnun() {
        return generalAnnun;
    }

    public PumpGlobalsResponse.AnnunciationEnum getReminderAnnun() {
        return reminderAnnun;
    }

    public PumpGlobalsResponse.AnnunciationEnum getAlertAnnun() {
        return alertAnnun;
    }

    public PumpGlobalsResponse.AnnunciationEnum getAlarmAnnun() {
        return alarmAnnun;
    }


    public enum CgmAlertAnnunciationEnum {
        VIBRATE(0, 0),
        BEEP(0, 2),
        HYPO_REPEAT(0, 3),

        ;

        private final int idA;
        private final int idB;
        CgmAlertAnnunciationEnum(int idA, int idB) {
            this.idA = idA;
            this.idB = idB;
        }

        public String toString() {
            return name();
        }

        public static CgmAlertAnnunciationEnum fromIds(int idA, int idB) {
            for (CgmAlertAnnunciationEnum i : CgmAlertAnnunciationEnum.values()) {
                if (i.idA == idA && i.idB == idB) {
                    return i;
                }
            }
            return null;
        }
    }

    public CgmAlertAnnunciationEnum getCgmAlertAnnun() {
        return cgmAlertAnnun;
    }

    public enum ChangeBitmask {
        QUICK_BOLUS(2),
        GENERAL(4),
        REMINDER(8),
        ALERT(16),
        ALARM(32),
        CGM_ALERT(64),


        ;

        private final int id;

        ChangeBitmask(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }

        public static Set<ChangeBitmask> fromBitmask(int bitmask) {
            Set<ChangeBitmask> set = new TreeSet<>();
            for (ChangeBitmask i : values()) {
                if (bitmask % i.id() == 0) {
                    set.add(i);
                }
            }

            return set;
        }

        public static int toBitmask(ChangeBitmask... items) {
            int mask = 0;
            for (ChangeBitmask i : items) {
                mask += i.id();
            }

            return mask;
        }
    }

    public Set<ChangeBitmask> getChangeBitmask() {
        return changeBitmask;
    }

}
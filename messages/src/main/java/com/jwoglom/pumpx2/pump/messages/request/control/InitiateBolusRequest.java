package com.jwoglom.pumpx2.pump.messages.request.control;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.InitiateBolusResponse;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.BolusDeliveryHistoryLog;

import java.util.Set;

@MessageProps(
    opCode=-98,
    size=61,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    response=InitiateBolusResponse.class,
    signed=true
)
public class InitiateBolusRequest extends Message {
    private long totalVolume;
    private int bolusID;
    private int bolusTypeBitmask;
    private long foodVolume;
    private long correctionVolume;
    private int bolusCarbs;
    private int bolusBG;
    private long bolusIOB;
    private long timestamp;

    public InitiateBolusRequest() {}

    public InitiateBolusRequest(byte[] raw) {
        parse(raw);
    }

    // all parameters:
    // (C49011.this.bolusId, MobileBolusModel.mobileBolusModel.bolusType(), BolusConfirmFragment.this.bolusTotalVol(), BolusConfirmFragment.this.bolusFoodVol(), C49011.this.bolusCorrectionVol, BolusConfirmFragment.this.bolusCarbs(), BolusConfirmFragment.this.bolusBG(), BolusConfirmFragment.this.bolusIOB(), 0L, 0L, 0L)
    // There is one additional parameter used by Tandem which we do not know (the final long), except that it is 0
    public InitiateBolusRequest(long totalVolume, int bolusID, int bolusTypeBitmask, long foodVolume, long correctionVolume, int bolusCarbs, int bolusBG, long bolusIOB, long timestamp) {
        this.cargo = buildCargo(totalVolume, bolusID, bolusTypeBitmask, foodVolume, correctionVolume, bolusCarbs, bolusBG, bolusIOB, timestamp);
        this.totalVolume = totalVolume; //
        this.bolusID = bolusID;
        this.bolusTypeBitmask = bolusTypeBitmask; //
        this.foodVolume = foodVolume; //
        this.correctionVolume = correctionVolume; //
        this.bolusCarbs = bolusCarbs; //
        this.bolusBG = bolusBG; //
        this.bolusIOB = bolusIOB; //
        this.timestamp = timestamp;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        // argument positions are guesses!!
        // 1u bolus
        // LastBolusStatusV2Response[bit0=1,bolusId=10650,bolusSourceId=8,bolusStatusId=3,bolusTypeBitmask=8,deliveredVolume=1000,extendedBolusDuration=0,requestedVolume=1000,timestamp=461510714,cargo={1,-102,41,0,0,58,24,-126,27,-24,3,0,0,3,8,8,0,0,0,0,-24,3,0,0}
        //   0  1 2 3 4    5  6 7 8 9 101112131415161718192021222324252627282930313233343536 37 38 39   40 41  42 43 44  45  46  47  48 49 50 51 52 53  54  55   56  57  58   59  60
        // {-24,3,0,0,-102,41,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-14,23,-126,27,104,-7,76,-21,-26,113,122,93,21,81,39,20,127,-20,-102,-39,121,-110,106,-84}


        // 142 mg/dl, 0.13u, 13g carbs (food)
        // LastBolusStatusV2Response[bit0=1,bolusId=10652,bolusSourceId=8,bolusStatusId=3,bolusTypeBitmask=1,deliveredVolume=130,extendedBolusDuration=0,requestedVolume=130,timestamp=461589271,cargo={1,-100,41,0,0,23,75,-125,27,-126,0,0,0,3,8,1,0,0,0,0,-126,0,0,0}]
        // TimeSinceResetResponse[pumpTime=1079274,timeSinceReset=461589180]
        // $ PUMP_AUTHENTICATION_KEY=6VeDeRAL5DCigGw2 PUMP_TIME_SINCE_RESET=461589180 ./scripts/get-single-opcode.py '033e9e3e3d820000009c29000001820000000000 023e00000d008e00000000000000000000000000 013e00000000bc4a831b9cbf19ffb856288a8afa 003e8f24a463e00cf3bbe5d305dd'
        //  0     1  2  3  4     5   6  7  8  9    10 11 12 13 14 15 16  17 18    19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36   37  38    39  40    41   42  43  44   45  46  47    48    49  50    51  52   53  54   55  56   57   58   59   60
        // {-126, 0, 0, 0, -100, 41, 0, 0, 1, -126, 0, 0, 0, 0, 0, 0, 0, 13, 0, -114, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -68, 74, -125, 27, -100, -65, 25, -1, -72, 86, 40, -118, -118, -6, -113, 36, -92, 99, -32, 12, -13, -69, -27, -45}


        // 161 mg/dl, 0.11u, 11g carbs (food), 0.13u IOB
        // LastBolusStatusV2Response[bit0=1,bolusId=10653,bolusSourceId=8,bolusStatusId=3,bolusTypeBitmask=1,deliveredVolume=110,extendedBolusDuration=0,requestedVolume=110,timestamp=461589462,cargo={1,-99,41,0,0,-42,75,-125,27,110,0,0,0,3,8,1,0,0,0,0,110,0,0,0}
        // ControlIQIOBResponse[iobType=0,mudaliarIOB=240,mudaliarTotalIOB=240,swan6hrIOB=240,timeRemainingSeconds=17940,cargo={-16,0,0,0,20,70,0,0,-16,0,0,0,-16,0,0,0,0}]
        // ControlIQInfoV2Response[byte6=1,byte7=2,byte8=4,closedLoopEnabled=false,controlStateType=1,currentUserModeType=0,exerciseChoice=0,exerciseDuration=0,totalDailyInsulin=75,weight=140,weightUnit=2,cargo={0,-116,0,2,75,0,1,2,4,1,0,0,0,0,0,0,0,0,0}]
        // CurrentBasalStatusResponse[basalModifiedBitmask=0,currentBasalRate=0,profileBasalRate=0,cargo={0,0,0,0,0,0,0,0,0}]
        // CurrentBolusStatusResponse[bolusId=0,bolusSource=0,bolusTypeBitmask=0,requestedVolume=0,status=0,timestamp=0,cargo={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}]
        // LastBGResponse[bgSource=0,bgTimestamp=461589432,bgValue=161,cargo={-72,75,-125,27,-95,0,0}]
        // $ PUMP_AUTHENTICATION_KEY=6VeDeRAL5DCigGw2 PUMP_TIME_SINCE_RESET=461589420
        // ./scripts/get-single-opcode.py '03399e393d6e0000009d290000016e0000000000 023900000b00a100820000000000000000000000 013900000000ac4b831b7a0b7cfc14a30b9c3995 0039dc8bbbdfa2ce2ce995725407'

        // for (int i=0; i<56; i++){System.out.println(i+" float: "+Bytes.readFloat(parsedMessage.cargo, i)+" uint32: "+Bytes.readUint32(parsedMessage.cargo, i)+" short: "+Bytes.readShort(parsedMessage.cargo,i)); }
        /*
        for (int i=0; i<61; i++) {
            System.out.print(i+" "+parsedMessage.cargo[i]+" ");
            if (i+4 <= 61) {System.out.print("float: "+Bytes.readFloat(parsedMessage.cargo, i)+" ");}
            if (i+4 <= 61) {System.out.print("uint32: "+Bytes.readUint32(parsedMessage.cargo, i)+" ");}
            if (i+2 <= 61) {System.out.print("short: "+Bytes.readShort(parsedMessage.cargo,i));}
            System.out.println();
        }
         */
        this.totalVolume = Bytes.readUint32(raw, 0); // correct
        this.bolusID = Bytes.readShort(raw, 4); // correct
        // 6, 7 empty
        this.bolusTypeBitmask = raw[8]; // correct, may be a short?
        this.foodVolume = Bytes.readUint32(raw, 9); // correct
        this.correctionVolume = Bytes.readUint32(raw, 13); // double check
        this.bolusCarbs = Bytes.readShort(raw, 17); // correct
        this.bolusBG = Bytes.readShort(raw, 19); // correct
        this.bolusIOB = Bytes.readUint32(raw, 21);  // correct
        // 25 - 36 inclusive are 0s
        this.timestamp = Bytes.readUint32(raw, 37); // equal to pumpTimeSinceReset
        // 41 - 61 unknown
    }


    public static byte[] buildCargo(long totalVolume, int bolusID, int bolusTypeId, long foodVolume, long correctionVolume, int bolusCarbs, int bolusBG, long bolusIOB, long timestamp) {
        return Bytes.combine(
                Bytes.toUint32(totalVolume),
                Bytes.firstTwoBytesLittleEndian(bolusID),
                new byte[]{0, 0},
                new byte[]{(byte) bolusTypeId},
                Bytes.toUint32(foodVolume),
                Bytes.toUint32(correctionVolume),
                Bytes.firstTwoBytesLittleEndian(bolusCarbs),
                Bytes.firstTwoBytesLittleEndian(bolusBG),
                Bytes.toUint32(bolusIOB),
                new byte[12],
                Bytes.toUint32(timestamp),
                new byte[20]
        );
    }

    public long getTotalVolume() {
        return totalVolume;
    }

    public int getBolusID() {
        return bolusID;
    }

    public int getBolusTypeBitmask() {
        return bolusTypeBitmask;
    }

    public Set<BolusDeliveryHistoryLog.BolusType> getBolusTypes() {
        return BolusDeliveryHistoryLog.BolusType.fromBitmask(bolusTypeBitmask);
    }

    public long getFoodVolume() {
        return foodVolume;
    }

    public long getCorrectionVolume() {
        return correctionVolume;
    }

    public int getBolusCarbs() {
        return bolusCarbs;
    }

    public int getBolusBG() {
        return bolusBG;
    }

    public long getBolusIOB() {
        return bolusIOB;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ControlIQInfoV2Request;

@MessageProps(
    opCode=-77,
    size=19,
    type=MessageType.RESPONSE,
    request=ControlIQInfoV2Request.class
)
public class ControlIQInfoV2Response extends Message {
    
    private boolean closedLoopEnabled;
    private int weight;
    private int weightUnit;
    private int totalDailyInsulin;
    private int currentUserModeType;
    private int byte6;
    private int byte7;
    private int byte8;
    private int controlStateType;
    private int exerciseChoice;
    private int exerciseDuration;
    
    public ControlIQInfoV2Response() {}
    
    public ControlIQInfoV2Response(boolean closedLoopEnabled, int weight, int weightUnit, int totalDailyInsulin, int currentUserModeType, int byte6, int byte7, int byte8, int controlStateType, int exerciseChoice, int exerciseDuration) {
        this.cargo = buildCargo(closedLoopEnabled, weight, weightUnit, totalDailyInsulin, currentUserModeType, byte6, byte7, byte8, controlStateType, exerciseChoice, exerciseDuration);
        this.closedLoopEnabled = closedLoopEnabled;
        this.weight = weight;
        this.weightUnit = weightUnit;
        this.totalDailyInsulin = totalDailyInsulin;
        this.currentUserModeType = currentUserModeType;
        this.byte6 = byte6;
        this.byte7 = byte7;
        this.byte8 = byte8;
        this.controlStateType = controlStateType;
        this.exerciseChoice = exerciseChoice;
        this.exerciseDuration = exerciseDuration;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.closedLoopEnabled = raw[0] != 0;
        this.weight = Bytes.readShort(raw, 1);
        this.weightUnit = raw[3];
        this.totalDailyInsulin = raw[4];
        this.currentUserModeType = raw[5];
        this.byte6 = raw[6];
        this.byte7 = raw[7];
        this.byte8 = raw[8];
        this.controlStateType = raw[9];
        this.exerciseChoice = raw[10];
        this.exerciseDuration = raw[11];
        
    }

    
    public static byte[] buildCargo(boolean closedLoopEnabled, int weight, int weightUnit, int totalDailyInsulin, int currentUserModeType, int byte6, int byte7, int byte8, int controlStateType, int exerciseChoice, int exerciseDuration) {
        return Bytes.combine(
            new byte[]{ (byte) (closedLoopEnabled ? 1 : 0) }, 
            Bytes.firstTwoBytesLittleEndian(weight), 
            new byte[]{ (byte) weightUnit }, 
            new byte[]{ (byte) totalDailyInsulin }, 
            new byte[]{ (byte) currentUserModeType }, 
            new byte[]{ (byte) byte6 }, 
            new byte[]{ (byte) byte7 }, 
            new byte[]{ (byte) byte8 }, 
            new byte[]{ (byte) controlStateType }, 
            new byte[]{ (byte) exerciseChoice }, 
            new byte[]{ (byte) exerciseDuration },
            Bytes.emptyBytes(7));
    }
    
    public boolean getClosedLoopEnabled() {
        return closedLoopEnabled;
    }
    public int getWeight() {
        return weight;
    }
    public int getWeightUnit() {
        return weightUnit;
    }
    public int getTotalDailyInsulin() {
        return totalDailyInsulin;
    }
    public int getCurrentUserModeType() {
        return currentUserModeType;
    }
    public int getByte6() {
        return byte6;
    }
    public int getByte7() {
        return byte7;
    }
    public int getByte8() {
        return byte8;
    }
    public int getControlStateType() {
        return controlStateType;
    }
    public int getExerciseChoice() {
        return exerciseChoice;
    }
    public int getExerciseDuration() {
        return exerciseDuration;
    }
    
}
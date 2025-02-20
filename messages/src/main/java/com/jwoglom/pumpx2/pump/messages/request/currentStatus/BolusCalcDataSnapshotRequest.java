package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.BolusCalcDataSnapshotResponse;

/**
 * As part of the Bolus Wizard, returns a snapshot of data obtained from various other locations
 * in the pump for determining how to render the initiate bolus screen and potentially pre-fill
 * the BG and correction dose information.
 */
@MessageProps(
    opCode=114,
    size=0,
    type=MessageType.REQUEST,
    response=BolusCalcDataSnapshotResponse.class,
    minApi=KnownApiVersion.API_V2_5
)
public class BolusCalcDataSnapshotRequest extends Message { 
    public BolusCalcDataSnapshotRequest() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        
    }

    
}
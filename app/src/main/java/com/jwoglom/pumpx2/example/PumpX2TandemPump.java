package com.jwoglom.pumpx2.example;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.jwoglom.pumpx2.pump.PumpState;
import com.jwoglom.pumpx2.pump.TandemError;
import com.jwoglom.pumpx2.pump.bluetooth.TandemPump;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.response.authentication.CentralChallengeResponse;
import com.jwoglom.pumpx2.pump.messages.response.authentication.PumpChallengeResponse;
import com.jwoglom.pumpx2.pump.messages.response.control.BolusPermissionResponse;
import com.jwoglom.pumpx2.pump.messages.response.control.CancelBolusResponse;
import com.jwoglom.pumpx2.pump.messages.response.control.InitiateBolusResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.AlarmStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.AlertStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ApiVersionResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CGMHardwareInfoResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ControlIQIOBResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.HistoryLogStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.LastBolusStatusV2Response;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.NonControlIQIOBResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.PumpFeaturesV1Response;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.PumpGlobalsResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.TimeSinceResetResponse;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.HistoryLogStreamResponse;
import com.jwoglom.pumpx2.pump.messages.response.qualifyingEvent.QualifyingEvent;
import com.welie.blessed.BluetoothPeripheral;

import com.jwoglom.pumpx2.shared.Hex;
import com.welie.blessed.HciStatus;

import java.util.Set;

import timber.log.Timber;

public class PumpX2TandemPump extends TandemPump {
    public static final String PUMP_CONNECTED_STAGE1_INTENT = "jwoglom.pumpx2.pumpconnected.stage1";
    public static final String PUMP_CONNECTED_STAGE2_INTENT = "jwoglom.pumpx2.pumpconnected.stage2";
    public static final String PUMP_CONNECTED_STAGE3_INTENT = "jwoglom.pumpx2.pumpconnected.stage3";
    public static final String PUMP_CONNECTED_COMPLETE_INTENT = "jwoglom.pumpx2.pumpconnected.complete";
    public static final String UPDATE_TEXT_RECEIVER = "jwoglom.pumpx2.updatetextreceiver";
    public static final String GOT_HISTORY_LOG_STATUS_RECEIVER = "jwoglom.pumpx2.gotHistoryLogStatus";
    public static final String GOT_HISTORY_LOG_STREAM_RECEIVER = "jwoglom.pumpx2.gotHistoryLogStream";
    public static final String GOT_BOLUS_PERMISSION_RESPONSE_RECEIVER = "jwoglom.pumpx2.gotBolusPermissionResponse";
    public static final String GOT_INITIATE_BOLUS_RESPONSE_RECEIVER = "jwoglom.pumpx2.gotInitiateBolusResponse";
    public static final String GOT_CANCEL_BOLUS_RESPONSE_RECEIVER = "jwoglom.pumpx2.gotCancelBolusResponse";
    public static final String GOT_LAST_BOLUS_RESPONSE_AFTER_CANCEL_RECEIVER = "jwoglom.pumpx2.gotLastBolusResponseAfterCancel";
    public static final String PUMP_INVALID_CHALLENGE_INTENT = "jwoglom.pumpx2.invalidchallenge";
    public static final String PUMP_ERROR_INTENT = "jwoglom.pumpx2.error";

    boolean bolusInProgress = false;
    int lastBolusId = -1;

    public PumpX2TandemPump(Context context) {
        super(context);
    }

    @Override
    public void onInitialPumpConnection(BluetoothPeripheral peripheral) {
        super.onInitialPumpConnection(peripheral);

        Intent intent = new Intent(PUMP_CONNECTED_STAGE1_INTENT);
        intent.putExtra("address", peripheral.getAddress());
        intent.putExtra("name", peripheral.getName());
        context.sendBroadcast(intent);
    }

    @Override
    public void onInvalidPairingCode(BluetoothPeripheral peripheral, PumpChallengeResponse resp) {
        Intent intent = new Intent(PUMP_INVALID_CHALLENGE_INTENT);
        intent.putExtra("address", peripheral.getAddress());
        intent.putExtra("appInstanceId", resp.getAppInstanceId());
        context.sendBroadcast(intent);
    }

    @Override
    public void onReceiveMessage(BluetoothPeripheral peripheral, Message message) {
        if (message instanceof ApiVersionResponse) {
            ApiVersionResponse resp = (ApiVersionResponse) message;
            Timber.i("Got ApiVersionRequest: %s", resp);
            checkPumpInitMessagesReceived(peripheral);
        } else if (message instanceof TimeSinceResetResponse) {
            TimeSinceResetResponse resp = (TimeSinceResetResponse) message;
            Timber.i("Got TimeSinceResetResponse: %s", resp);
            checkPumpInitMessagesReceived(peripheral);
        } else {
            if (message instanceof HistoryLogStatusResponse) {
                HistoryLogStatusResponse resp = (HistoryLogStatusResponse) message;
                Intent intent = new Intent(GOT_HISTORY_LOG_STATUS_RECEIVER);
                intent.putExtra("address", peripheral.getAddress());
                intent.putExtra("numEntries", resp.getNumEntries());
                intent.putExtra("firstSequenceNum", resp.getFirstSequenceNum());
                intent.putExtra("lastSequenceNum", resp.getLastSequenceNum());
                context.sendBroadcast(intent);
            } else if (message instanceof HistoryLogStreamResponse) {
                HistoryLogStreamResponse resp = (HistoryLogStreamResponse) message;
                Intent intent = new Intent(GOT_HISTORY_LOG_STREAM_RECEIVER);
                intent.putExtra("address", peripheral.getAddress());
                intent.putExtra("numberOfHistoryLogs", resp.getNumberOfHistoryLogs());
                context.sendBroadcast(intent);
            }

            if (message instanceof BolusPermissionResponse && bolusInProgress) {
                BolusPermissionResponse resp = (BolusPermissionResponse) message;
                Intent intent = new Intent(GOT_BOLUS_PERMISSION_RESPONSE_RECEIVER);
                intent.putExtra("address", peripheral.getAddress());
                intent.putExtra("bolusId", resp.getBolusId());
                intent.putExtra("status", resp.getStatus());
                intent.putExtra("nackReasonId", resp.getNackReasonId());
                context.sendBroadcast(intent);
                return;
            } else if (message instanceof InitiateBolusResponse && bolusInProgress) {
                InitiateBolusResponse resp = (InitiateBolusResponse) message;
                Intent intent = new Intent(GOT_INITIATE_BOLUS_RESPONSE_RECEIVER);
                intent.putExtra("address", peripheral.getAddress());
                intent.putExtra("bolusId", resp.getBolusId());
                intent.putExtra("status", resp.getStatus());
                intent.putExtra("statusTypeId", resp.getStatusTypeId());
                intent.putExtra("statusType", String.valueOf(resp.getStatusType()));
                context.sendBroadcast(intent);
                return;
            } else if (message instanceof CancelBolusResponse && bolusInProgress) {
                CancelBolusResponse resp = (CancelBolusResponse) message;
                Intent intent = new Intent(GOT_CANCEL_BOLUS_RESPONSE_RECEIVER);
                intent.putExtra("address", peripheral.getAddress());
                intent.putExtra("bolusId", resp.getBolusId());
                intent.putExtra("status", resp.getStatusId());
                intent.putExtra("reason", resp.getReasonId());
                context.sendBroadcast(intent);
                return;
            } else if (message instanceof LastBolusStatusV2Response && bolusInProgress) {
                LastBolusStatusV2Response resp = (LastBolusStatusV2Response) message;
                Intent intent = new Intent(GOT_LAST_BOLUS_RESPONSE_AFTER_CANCEL_RECEIVER);
                intent.putExtra("address", peripheral.getAddress());
                intent.putExtra("bolusId", resp.getBolusId());
                intent.putExtra("status", String.valueOf(resp.getBolusStatus()));
                intent.putExtra("statusId", resp.getBolusStatusId());
                intent.putExtra("deliveredVolume", resp.getDeliveredVolume());
                intent.putExtra("requestedVolume", resp.getRequestedVolume());
                intent.putExtra("source", String.valueOf(resp.getBolusSource()));
                intent.putExtra("sourceId", resp.getBolusSourceId());
                context.sendBroadcast(intent);
                return;
            }

            if (message instanceof AlarmStatusResponse) {
                AlarmStatusResponse resp = (AlarmStatusResponse) message;
                Intent intent = new Intent(UPDATE_TEXT_RECEIVER);
                intent.putExtra("text", "Alarms: "+resp.getAlarms().toString());
                context.sendBroadcast(intent);
            } else if (message instanceof AlertStatusResponse) {
                AlertStatusResponse resp = (AlertStatusResponse) message;
                Intent intent = new Intent(UPDATE_TEXT_RECEIVER);
                intent.putExtra("text", "Alerts: "+resp.getAlerts().toString());
                context.sendBroadcast(intent);
            } else if (message instanceof CGMHardwareInfoResponse) {
                CGMHardwareInfoResponse resp = (CGMHardwareInfoResponse) message;
                Intent intent = new Intent(UPDATE_TEXT_RECEIVER);
                intent.putExtra("text", "CGMHardware: "+resp.getHardwareInfoString());
                context.sendBroadcast(intent);
            } else if (message instanceof ControlIQIOBResponse) {
                ControlIQIOBResponse resp = (ControlIQIOBResponse) message;
                Intent intent = new Intent(UPDATE_TEXT_RECEIVER);
                intent.putExtra("text", "ControlIQIOB: "+resp);
                context.sendBroadcast(intent);
            } else if (message instanceof NonControlIQIOBResponse) {
                NonControlIQIOBResponse resp = (NonControlIQIOBResponse) message;
                Intent intent = new Intent(UPDATE_TEXT_RECEIVER);
                intent.putExtra("text", "NonControlIQIOB: "+resp);
                context.sendBroadcast(intent);
            } else if (message instanceof PumpFeaturesV1Response) {
                PumpFeaturesV1Response resp = (PumpFeaturesV1Response) message;
                Intent intent = new Intent(UPDATE_TEXT_RECEIVER);
                intent.putExtra("text", "Features: "+resp);
                context.sendBroadcast(intent);
            } else if (message instanceof PumpGlobalsResponse) {
                PumpGlobalsResponse resp = (PumpGlobalsResponse) message;
                Intent intent = new Intent(UPDATE_TEXT_RECEIVER);
                intent.putExtra("text", "Globals: "+resp);
                context.sendBroadcast(intent);
            } else {
                Intent intent = new Intent(UPDATE_TEXT_RECEIVER);
                intent.putExtra("text", message.toString());
                context.sendBroadcast(intent);
            }
        }
    }

    @Override
    public void onReceiveQualifyingEvent(BluetoothPeripheral peripheral, Set<QualifyingEvent> events) {
        Timber.i("Received QualifyingEvents: %s", events);
        Toast.makeText(context, "Received QualifyingEvents: " + events, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onWaitingForPairingCode(BluetoothPeripheral peripheral, CentralChallengeResponse centralChallenge) {
        // checkHmac(authKey, centralChallenge we sent, new byte[0])
        // doHmacSha1(10 bytes from central challenge request, bytes from authKey/pairing code) == 2-22 of response
        Intent intent = new Intent(PUMP_CONNECTED_STAGE2_INTENT);
        intent.putExtra("address", peripheral.getAddress());
        intent.putExtra("centralChallengeCargo", Hex.encodeHexString(centralChallenge.getCargo()));
        context.sendBroadcast(intent);
    }

    @Override
    public void onPumpConnected(BluetoothPeripheral peripheral) {
        super.onPumpConnected(peripheral);

        Intent intent = new Intent(PUMP_CONNECTED_STAGE3_INTENT);
        intent.putExtra("address", peripheral.getAddress());
        context.sendBroadcast(intent);
    }


    @Override
    public boolean onPumpDisconnected(BluetoothPeripheral peripheral, HciStatus status) {
        Toast.makeText(context, "Pump disconnected: " + status, Toast.LENGTH_SHORT).show();
        return super.onPumpDisconnected(peripheral, status);
    }

    public void checkPumpInitMessagesReceived(BluetoothPeripheral peripheral) {
        if (PumpState.getPumpAPIVersion() == null || PumpState.getPumpTimeSinceReset() == null) {
            return;
        }

        Intent intent = new Intent(PUMP_CONNECTED_COMPLETE_INTENT);
        intent.putExtra("address", peripheral.getAddress());
        context.sendBroadcast(intent);
    }

    @Override
    public void onPumpCriticalError(BluetoothPeripheral peripheral, TandemError reason) {
        Intent intent = new Intent(PUMP_ERROR_INTENT);
        intent.putExtra("reason", reason.name());
        intent.putExtra("message", reason.getMessage());
        intent.putExtra("extra", reason.getExtra());
        context.sendBroadcast(intent);
    }
}

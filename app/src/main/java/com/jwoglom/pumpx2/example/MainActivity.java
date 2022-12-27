package com.jwoglom.pumpx2.example;

import static com.jwoglom.pumpx2.example.PumpX2TandemPump.GOT_BOLUS_CALC_LAST_BG_RESPONSE_RECEIVER;
import static com.jwoglom.pumpx2.example.PumpX2TandemPump.GOT_BOLUS_CALC_RESPONSE_RECEIVER;
import static com.jwoglom.pumpx2.example.PumpX2TandemPump.GOT_BOLUS_PERMISSION_RESPONSE_RECEIVER;
import static com.jwoglom.pumpx2.example.PumpX2TandemPump.GOT_BOLUS_PERMISSION_REVOKED;
import static com.jwoglom.pumpx2.example.PumpX2TandemPump.GOT_CANCEL_BOLUS_RESPONSE_RECEIVER;
import static com.jwoglom.pumpx2.example.PumpX2TandemPump.GOT_HISTORY_LOG_STATUS_RECEIVER;
import static com.jwoglom.pumpx2.example.PumpX2TandemPump.GOT_HISTORY_LOG_STREAM_RECEIVER;
import static com.jwoglom.pumpx2.example.PumpX2TandemPump.GOT_INITIATE_BOLUS_RESPONSE_RECEIVER;
import static com.jwoglom.pumpx2.example.PumpX2TandemPump.GOT_LAST_BOLUS_RESPONSE_AFTER_CANCEL_RECEIVER;
import static com.jwoglom.pumpx2.example.PumpX2TandemPump.PUMP_CONNECTED_COMPLETE_INTENT;
import static com.jwoglom.pumpx2.example.PumpX2TandemPump.PUMP_CONNECTED_STAGE3_INTENT;
import static com.jwoglom.pumpx2.example.PumpX2TandemPump.PUMP_CONNECTED_STAGE1_INTENT;
import static com.jwoglom.pumpx2.example.PumpX2TandemPump.PUMP_CONNECTED_STAGE2_INTENT;
import static com.jwoglom.pumpx2.example.PumpX2TandemPump.PUMP_ERROR_INTENT;
import static com.jwoglom.pumpx2.example.PumpX2TandemPump.PUMP_INVALID_CHALLENGE_INTENT;
import static com.jwoglom.pumpx2.example.PumpX2TandemPump.UPDATE_TEXT_RECEIVER;
import static com.jwoglom.pumpx2.pump.messages.bluetooth.PumpStateSupplier.authenticationKey;
import static com.jwoglom.pumpx2.pump.messages.bluetooth.PumpStateSupplier.pumpTimeSinceReset;
import static java.util.stream.Collectors.toList;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.jwoglom.pumpx2.pump.PumpState;
import com.jwoglom.pumpx2.pump.bluetooth.TandemBluetoothHandler;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.Packetize;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.bluetooth.ServiceUUID;
import com.jwoglom.pumpx2.pump.messages.bluetooth.TronMessageWrapper;
import com.jwoglom.pumpx2.pump.messages.bluetooth.models.Packet;
import com.jwoglom.pumpx2.pump.messages.builders.CurrentBatteryRequestBuilder;
import com.jwoglom.pumpx2.pump.messages.helpers.Dates;
import com.jwoglom.pumpx2.pump.messages.models.ApiVersion;
import com.jwoglom.pumpx2.pump.messages.models.InsulinUnit;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.request.control.BolusPermissionRequest;
import com.jwoglom.pumpx2.pump.messages.request.control.CancelBolusRequest;
import com.jwoglom.pumpx2.pump.messages.request.control.InitiateBolusRequest;
import com.jwoglom.pumpx2.pump.messages.request.control.BolusPermissionReleaseRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.BolusCalcDataSnapshotRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.BolusPermissionChangeReasonRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.HistoryLogRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.HistoryLogStatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.IDPSegmentRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.IDPSettingsRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.LastBGRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.LastBolusStatusV2Request;
import com.jwoglom.pumpx2.pump.messages.response.authentication.CentralChallengeResponse;
import com.jwoglom.pumpx2.pump.messages.response.control.BolusPermissionResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.LastBGResponse;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.BolusDeliveryHistoryLog;
import com.jwoglom.pumpx2.pump.messages.util.MessageHelpers;
import com.jwoglom.pumpx2.shared.JavaHelpers;
import com.jwoglom.pumpx2.shared.L;
import com.welie.blessed.BluetoothCentralManager;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.WriteType;

import org.apache.commons.codec.DecoderException;
import com.jwoglom.pumpx2.shared.Hex;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.UUID;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int ACCESS_LOCATION_REQUEST = 2;

    private Handler handler;
    private TextView statusText;
    private Button retryConnectButton;
    private Spinner requestMessageSpinner;
    private Button requestSendButton;
    private Button batteryRequestButton;
    private Button fetchHistoryLogsButton;
    private Button recentHistoryLogsButton;
    private PumpX2TandemPump tandemEventCallback;
    private TandemBluetoothHandler bluetoothHandler;
    private LinearLayout basicLinearLayout;
    private LinearLayout advancedLinearLayout;
    private Button showMoreButton;
    private Button bolusButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler();

        statusText = findViewById(R.id.statusText);
        initStatusText();
        retryConnectButton = findViewById(R.id.retryConnect);
        retryConnectButton.setOnClickListener((view) -> resetBluetoothHandler().startScan());

        requestMessageSpinner = findViewById(R.id.request_message_spinner);

        List<String> requestMessages = MessageHelpers.getAllPumpRequestMessages()
                .stream().filter(m -> !m.startsWith("authentication.") && !m.startsWith("historyLog.")).collect(toList());
        Timber.i("requestMessages: %s", requestMessages);
        ArrayAdapter<String> adapter  = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, requestMessages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        requestMessageSpinner.setAdapter(adapter);

        requestSendButton = findViewById(R.id.request_message_send);
        batteryRequestButton = findViewById(R.id.battery_request_button);
        fetchHistoryLogsButton = findViewById(R.id.fetch_history_logs);
        recentHistoryLogsButton = findViewById(R.id.recent_history_logs);
        bolusButton = findViewById(R.id.bolusButton);
        bolusButton.setEnabled(false);

        basicLinearLayout = findViewById(R.id.basicLinearLayout);
        advancedLinearLayout = findViewById(R.id.advancedLinearLayout);
        showMoreButton = findViewById(R.id.show_more);
        showMoreButton.setOnClickListener((view) -> {
            if (advancedLinearLayout.getVisibility() == View.INVISIBLE) {
                advancedLinearLayout.setVisibility(View.VISIBLE);
                advancedLinearLayout.postInvalidate();
            } else {
                advancedLinearLayout.setVisibility(View.INVISIBLE);
                advancedLinearLayout.postInvalidate();
            }
        });

        initWearUIFixes();

        registerReceiver(pumpConnectedStage1Receiver, new IntentFilter(PUMP_CONNECTED_STAGE1_INTENT));
        registerReceiver(pumpConnectedStage2Receiver, new IntentFilter(PUMP_CONNECTED_STAGE2_INTENT));
        registerReceiver(pumpConnectedStage3Receiver, new IntentFilter(PUMP_CONNECTED_STAGE3_INTENT));
        registerReceiver(pumpConnectedCompleteReceiver, new IntentFilter(PUMP_CONNECTED_COMPLETE_INTENT));
        registerReceiver(updateTextReceiver, new IntentFilter(UPDATE_TEXT_RECEIVER));
        registerReceiver(gotHistoryLogStatusReceiver, new IntentFilter(GOT_HISTORY_LOG_STATUS_RECEIVER));
        registerReceiver(gotHistoryLogStreamReceiver, new IntentFilter(GOT_HISTORY_LOG_STREAM_RECEIVER));
        registerReceiver(gotBolusPermissionResponseReceiver, new IntentFilter(GOT_BOLUS_PERMISSION_RESPONSE_RECEIVER));
        registerReceiver(gotBolusCalcResponseReceiver, new IntentFilter(GOT_BOLUS_CALC_RESPONSE_RECEIVER));
        registerReceiver(gotBolusCalcLastBgResponseReceiver, new IntentFilter(GOT_BOLUS_CALC_LAST_BG_RESPONSE_RECEIVER));
        registerReceiver(gotInitiateBolusResponseReceiver, new IntentFilter(GOT_INITIATE_BOLUS_RESPONSE_RECEIVER));
        registerReceiver(gotCancelBolusResponseReceiver, new IntentFilter(GOT_CANCEL_BOLUS_RESPONSE_RECEIVER));
        registerReceiver(gotLastBolusStatusResponseAfterCancelReceiver, new IntentFilter(GOT_LAST_BOLUS_RESPONSE_AFTER_CANCEL_RECEIVER));
        registerReceiver(gotBolusPermissionRevokedReceiver, new IntentFilter(GOT_BOLUS_PERMISSION_REVOKED));
        registerReceiver(pumpConnectedInvalidChallengeReceiver, new IntentFilter(PUMP_INVALID_CHALLENGE_INTENT));
        registerReceiver(pumpErrorReceiver, new IntentFilter(PUMP_ERROR_INTENT));

        L.i("MainActivity", "Build.MANUFACTURER=" + Build.MANUFACTURER+" Build.MODEL=" + Build.MODEL + " Build.SDK_INT=" + Build.VERSION.SDK_INT);
    }

    @Override
    protected void onResume() {
        super.onResume();

        firstRunMessages();

        if (getBluetoothManager().getAdapter() != null) {
            if (!isBluetoothEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                checkPermissions();
            }
        } else {
            Timber.e("This device has no Bluetooth hardware");
        }
    }

    private void initStatusText() {
        String[] waitingStatusTexts = new String[]{
                "Looking for Bluetooth devices...",
                "Looking for Bluetooth devices...\n\n\nTurn on your pump and make sure Mobile Connection is enabled within Options > Device Settings > Bluetooth Settings.",
                "Looking for Bluetooth devices...\n\n\nTurn on your pump and make sure Mobile Connection is enabled within Options > Device Settings > Bluetooth Settings.\n\nToggle the setting if the device still won't connect."
        };
        statusText.setText(waitingStatusTexts[0]);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (statusText.getText().equals(waitingStatusTexts[0])) {
                    statusText.setText(waitingStatusTexts[1]);
                }
            }
        }, 10000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (statusText.getText().equals(waitingStatusTexts[1])) {
                    statusText.setText(waitingStatusTexts[2]);
                }
            }
        }, 20000);
    }

    public void initWearUIFixes() {
        if (isWear()) {
            statusText.setPadding(0, 20, 0, 0);
            statusText.setTextSize(16);
            for (int id : new int[]{R.id.basicLinearLayout, R.id.advancedLinearLayout}) {
                ViewGroup.LayoutParams layoutParams = findViewById(id).getLayoutParams();
                layoutParams.height = ActionBar.LayoutParams.WRAP_CONTENT;
                layoutParams.width = ActionBar.LayoutParams.WRAP_CONTENT;
                findViewById(id).setLayoutParams(layoutParams);
            }
            for (Button btn : new Button[]{bolusButton, batteryRequestButton, showMoreButton, requestSendButton, retryConnectButton, fetchHistoryLogsButton, recentHistoryLogsButton}) {
                if (btn == null) {
                    continue;
                }
                btn.setTextSize(8);
                ViewGroup.LayoutParams layoutParams = btn.getLayoutParams();
                layoutParams.height = ActionBar.LayoutParams.WRAP_CONTENT;
                layoutParams.width = ActionBar.LayoutParams.WRAP_CONTENT;
                btn.setLayoutParams(layoutParams);
            }
        }
    }

    private void firstRunMessages() {
        SharedPreferences prefs = getSharedPreferences("com.jwoglom.pumpx2.example", MODE_PRIVATE);
        if (prefs.getBoolean("firstrun", true)) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("HEALTH AND SAFETY WARNING")
                    .setMessage("The PumpX2 sample application is deprecated. Please use https://github.com/jwoglom/wearx2 instead.\n\nThis application is for EXPERIMENTAL USE ONLY and can be used to MODIFY ACTIVE INSULIN DELIVERY ON YOUR INSULIN PUMP.\n\n" +
                            "There is NO WARRANTY IMPLIED OR EXPRESSED DUE TO USE OF THIS SOFTWARE. YOU ASSUME ALL RISK FOR ANY MALFUNCTIONS, BUGS, OR INSULIN DELIVERY ACTIONS.")
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            prefs.edit().putBoolean("firstrun", false).apply();
                        }
                    })
                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .create()
                    .show();
        }
    }

    private boolean isBluetoothEnabled() {
        BluetoothAdapter bluetoothAdapter = getBluetoothManager().getAdapter();
        if(bluetoothAdapter == null) return false;

        return bluetoothAdapter.isEnabled();
    }

    private TandemBluetoothHandler getBluetoothHandler() {
        if (bluetoothHandler != null) {
            return bluetoothHandler;
        }
        tandemEventCallback = new PumpX2TandemPump(getApplicationContext());
        tandemEventCallback.enableActionsAffectingInsulinDelivery();
        bluetoothHandler = TandemBluetoothHandler.getInstance(getApplicationContext(), tandemEventCallback);
        return bluetoothHandler;
    }

    private TandemBluetoothHandler resetBluetoothHandler() {
        bluetoothHandler.stop();
        bluetoothHandler = null;
        return getBluetoothHandler();
    }

    @NotNull
    private BluetoothManager getBluetoothManager() {
        return Objects.requireNonNull((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE),"cannot get BluetoothManager");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(pumpConnectedStage1Receiver);
    }


    private final BroadcastReceiver pumpConnectedStage1Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String address = intent.getStringExtra("address");
            Timber.d("PUMP STAGE1: BT connection initiated with address: %s", address);
            BluetoothPeripheral peripheral = getPeripheral(address);
            Timber.d("got peripheral object: %s", peripheral.getName());

            String name = intent.getStringExtra("name");

            statusText.setText("Connecting to " + name);
            statusText.postInvalidate();
        }
    };

    private final BroadcastReceiver pumpConnectedStage2Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String address = intent.getStringExtra("address");
            Timber.d("PUMP STAGE2: waiting for pairing code with address: %s", address);
            BluetoothPeripheral peripheral = getPeripheral(address);
            Timber.d("got peripheral object: %s", peripheral.getName());
            String cargoHex = intent.getStringExtra("centralChallengeCargo");

            byte[] centralChallengeCargo = new byte[0];
            try {
                centralChallengeCargo = Hex.decodeHex(cargoHex);
            } catch (DecoderException e) {
                Timber.e(e);
                e.printStackTrace();
            }

            statusText.setText("Stage2: Waiting for pairing code");
            statusText.postInvalidate();
            
            CentralChallengeResponse challenge = new CentralChallengeResponse();
            challenge.parse(centralChallengeCargo);

            Timber.d("Waiting for central challenge response w appInstanceId");
            triggerPairDialog(peripheral, address, challenge);
        }
    };

    private final BroadcastReceiver pumpConnectedStage3Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String address = intent.getStringExtra("address");
            Timber.d("PUMP STAGE3: waiting for initial messages: %s", address);
            BluetoothPeripheral peripheral = getPeripheral(address);
            Timber.d("got peripheral object: %s", peripheral.getName());

            statusText.setText("Stage3: Waiting for response");
            statusText.postInvalidate();

        }
    };

    private boolean waitingOnHistoryLogStatus = false;
    private int onHistoryLogStatusFetchN = 0;
    private final BroadcastReceiver pumpConnectedCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String address = intent.getStringExtra("address");
            Timber.d("PUMP STAGE5: done with address: %s", address);
            BluetoothPeripheral peripheral = getPeripheral(address);
            Timber.d("got peripheral object: %s", peripheral.getName());

            statusText.setText("Connected to pump!");
            statusText.postInvalidate();

            Timber.i("CONNECTED: PUMP_AUTHENTICATION_KEY=" + authenticationKey.get() + " PUMP_TIME_SINCE_RESET=" + pumpTimeSinceReset.get());

            basicLinearLayout.setVisibility(View.VISIBLE);

            requestMessageSpinner.setVisibility(View.VISIBLE);
            requestMessageSpinner.postInvalidate();

            bolusButton.setVisibility(View.VISIBLE);
            bolusButton.setOnClickListener((z) -> startBolusProcess(peripheral));

            showMoreButton.setVisibility(View.VISIBLE);
            showMoreButton.postInvalidate();

            requestSendButton.setVisibility(View.VISIBLE);
            requestSendButton.setOnClickListener((z) -> {
                String itemName = requestMessageSpinner.getSelectedItem().toString();
                try {
                    String className = MessageHelpers.REQUEST_PACKAGE + "." + itemName;

                    // Custom processing for arguments
                    if (className.equals(IDPSegmentRequest.class.getName())) {
                        triggerIDPSegmentDialog(peripheral);
                        return;
                    } else if (className.equals(IDPSettingsRequest.class.getName())) {
                        triggerIDPSettingsDialog(peripheral);
                        return;
                    } else if (className.equals(HistoryLogRequest.class.getName())) {
                        triggerHistoryLogRequestDialog(peripheral);
                        return;
                    } else if (className.equals(InitiateBolusRequest.class.getName())) {
                        triggerInitiateBolusRequestDialog(peripheral);
                        return;
                    } else if (className.equals(CancelBolusRequest.class.getName())) {
                        triggerCancelBolusRequestDialog(peripheral);
                        return;
                    } else if (className.equals(BolusPermissionChangeReasonRequest.class.getName())) {
                        triggerMessageWithBolusIdParameter(peripheral, BolusPermissionChangeReasonRequest.class);
                        return;
                    } else if (className.equals(BolusPermissionReleaseRequest.class.getName())) {
                        triggerMessageWithBolusIdParameter(peripheral, BolusPermissionReleaseRequest.class);
                        return;
                    } else if (className.equals(BolusPermissionRequest.class.getName())) {
                        writePumpMessage(new BolusPermissionRequest(), peripheral);
                        return;
                    }

                    Class<?> clazz = Class.forName(className);
                    Timber.i("Instantiated %s: %s", className, clazz);
                    writePumpMessage((Message) clazz.newInstance(), peripheral);
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                    Timber.e(e);
                    e.printStackTrace();
                }
            });
            requestSendButton.postInvalidate();

            batteryRequestButton.setVisibility(View.VISIBLE);
            batteryRequestButton.setOnClickListener((z) -> {
                writePumpMessage(CurrentBatteryRequestBuilder.create(PumpState.getPumpAPIVersion()), peripheral);
            });


            fetchHistoryLogsButton.setVisibility(View.VISIBLE);
            fetchHistoryLogsButton.setOnClickListener((z) -> {
                waitingOnHistoryLogStatus = true;
                writePumpMessage(new HistoryLogStatusRequest(), peripheral);
            });

            recentHistoryLogsButton.setVisibility(View.VISIBLE);
            recentHistoryLogsButton.setOnClickListener((z) -> {
                waitingOnHistoryLogStatus = true;
                onHistoryLogStatusFetchN = 50;
                writePumpMessage(new HistoryLogStatusRequest(), peripheral);
            });
        }
    };

    private Queue<Integer> remainingSequenceNums = new LinkedList<>();
    private int lastRemainingSequenceNum = -1;
    private int remainingSequenceNumsInBatch = 0;
    private int lastFetchedHistoryLogSequenceNum = -1;

    private static final int sequenceNumBatchSize = 250;

    private final BroadcastReceiver gotHistoryLogStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String address = intent.getStringExtra("address");
            BluetoothPeripheral peripheral = getPeripheral(address);

            long numEntries = intent.getLongExtra("numEntries", 0L);
            long firstSequenceNum = intent.getLongExtra("firstSequenceNum", 0L);
            long lastSequenceNum = intent.getLongExtra("lastSequenceNum", 0L);

            Timber.d("Received HistoryLogStatus: %d count, %d - %d", numEntries, firstSequenceNum, lastSequenceNum);
            if (waitingOnHistoryLogStatus) {
                if (onHistoryLogStatusFetchN > 0) {
                    int start = (int)(lastSequenceNum) - onHistoryLogStatusFetchN;
                    if (lastFetchedHistoryLogSequenceNum > -1) {
                        if (lastFetchedHistoryLogSequenceNum == lastSequenceNum) {
                            Timber.i("No more HistoryLog events to fetch");
                            return;
                        }
                        Timber.d("lastFetchedSequenceNum=%d", lastFetchedHistoryLogSequenceNum);
                        start = lastFetchedHistoryLogSequenceNum+1;
                    }
                    remainingSequenceNums = sequenceNumberList(start, (int)(lastSequenceNum));
                    remainingSequenceNumsInBatch = Math.min((int)(lastSequenceNum) - start + 1, sequenceNumBatchSize);
                    Timber.i("Fetching HistoryLog events from %d - %d", start, (int)(lastSequenceNum));

                    HistoryLogRequest req = new HistoryLogRequest(start, remainingSequenceNumsInBatch);
                    Timber.d("Writing HistoryLogRequest: %s", req);
                    tandemEventCallback.requestedHistoryLogStartId = start;
                    writePumpMessage(req, peripheral);
                    onHistoryLogStatusFetchN = 0;
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("History Logs");
                builder.setMessage("Start number.\n" +
                        "Total count: "+numEntries+" from "+firstSequenceNum+" - "+lastSequenceNum);

                final EditText input = new EditText(context);
                input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                input.setText(String.valueOf(Math.max(0, numEntries - 250)));
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int start = Integer.parseInt(input.getText().toString());
                        Timber.i("requested start: %d", start);

                        AlertDialog.Builder builder2 = new AlertDialog.Builder(context);

                        builder2.setTitle("History Logs");
                        builder2.setMessage("End number.\n" +
                                "Total count: "+numEntries+" from "+firstSequenceNum+" - "+lastSequenceNum);

                        final EditText input2 = new EditText(context);
                        input2.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                        input2.setText(String.valueOf(Math.min(lastSequenceNum, start + 250)));
                        builder2.setView(input2);

                        builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int end = Integer.parseInt(input2.getText().toString());
                                Timber.i("requested end: %d", end);

                                remainingSequenceNums = sequenceNumberList(start, end);
                                lastRemainingSequenceNum = end;
                                remainingSequenceNumsInBatch = Math.min(end - start, sequenceNumBatchSize);

                                HistoryLogRequest req = new HistoryLogRequest(start, remainingSequenceNumsInBatch);
                                Timber.d("Writing HistoryLogRequest: %s", req);
                                tandemEventCallback.requestedHistoryLogStartId = start;
                                writePumpMessage(req, peripheral);

                            }
                        });
                        builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder2.show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        }
    };

    private Queue<Integer> sequenceNumberList(int start, int end) {
        Queue<Integer> ret = new LinkedList<>();
        for (int i=start; i<=end; i++) {
            ret.add(i);
        }
        return ret;
    }

    private final BroadcastReceiver gotHistoryLogStreamReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String address = intent.getStringExtra("address");
            BluetoothPeripheral peripheral = getPeripheral(address);

            int numHistoryLogs = intent.getIntExtra("numberOfHistoryLogs", 0);
            int firstSequenceNum = intent.getIntExtra("firstSequenceNum", 0);
            Timber.d("HistoryLog stream: Got history logs count for request: %d", numHistoryLogs);
            int sequenceNum = firstSequenceNum;
            for (int i=0; i<numHistoryLogs; i++) {
                Integer poll = remainingSequenceNums.poll();
                lastFetchedHistoryLogSequenceNum = poll == null ? -1 : poll;
                if (lastFetchedHistoryLogSequenceNum < sequenceNum) {
                    Timber.d("MISSED SEQUENCE NUMBER %d, got %d", lastFetchedHistoryLogSequenceNum, sequenceNum);
                    for (int j=0; j<(sequenceNum-lastFetchedHistoryLogSequenceNum); j++) {
                        remainingSequenceNums.poll();
                    }
                    lastFetchedHistoryLogSequenceNum = sequenceNum;
                }
                remainingSequenceNumsInBatch--;
                sequenceNum++;
            }

            if (remainingSequenceNumsInBatch > 0) {
                Timber.d("HistoryLog stream: Sequence nums remaining in batch: %d", remainingSequenceNumsInBatch);
                return;
//                Timber.i("Missed %d sequence nums (peek: %d)", remainingSequenceNumsInBatch, remainingSequenceNums.peek());
//                for (int i=0; i<remainingSequenceNumsInBatch; i++) {
//                    Timber.i("MISSED SEQUENCE NUMBER %d", remainingSequenceNums.poll());
//                    remainingSequenceNumsInBatch--;
//                }
            }

            if (remainingSequenceNums.isEmpty()) {
                Timber.d("HistoryLog stream: No sequence numbers remaining!");
                return;
            }

            Timber.i("Done with HistoryLog batch ending with %d", sequenceNum);
            int count = Math.min(remainingSequenceNums.size(), sequenceNumBatchSize);
            remainingSequenceNumsInBatch = sequenceNumBatchSize;
            if (sequenceNum+remainingSequenceNumsInBatch > lastRemainingSequenceNum) {
                remainingSequenceNumsInBatch = lastRemainingSequenceNum-sequenceNum;
            }
            HistoryLogRequest req = new HistoryLogRequest(remainingSequenceNums.peek(), count);
            Timber.d("Writing HistoryLogRequest: %s", req);
            tandemEventCallback.requestedHistoryLogStartId = remainingSequenceNums.peek();
            writePumpMessage(req, peripheral);
        }
    };

    private final BroadcastReceiver pumpConnectedInvalidChallengeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String address = intent.getStringExtra("address");
            BluetoothPeripheral peripheral = getPeripheral(address);
            Timber.d("Invalid challenge: %s", peripheral.getName());

            PumpState.failedPumpConnectionAttempts++;

            new AlertDialog.Builder(context)
                    .setTitle("Pump Connection")
                    .setMessage("The pump rejected the pairing code. You need to unpair and re-pair the device in Bluetooth Settings. Press OK to enter the new code.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(PUMP_CONNECTED_STAGE1_INTENT);
                            intent.putExtra("address", peripheral.getAddress());
                            intent.putExtra("name", peripheral.getName());
                            context.sendBroadcast(intent);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    };

    private final BroadcastReceiver pumpErrorReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String reason = intent.getStringExtra("reason");
            String message = intent.getStringExtra("message");
            String extra = intent.getStringExtra("extra");
            if (Strings.isNullOrEmpty(extra)) {
                extra = "The pump is not responding as expected.";
            }
            new AlertDialog.Builder(context)
                    .setTitle("Error connecting to pump: " + reason)
                    .setMessage(message + "\n\n" + extra)
                    .setNegativeButton(android.R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    };

    // Adds the given pump message to the BT device's characteristic write queue.
    private void writePumpMessage(Message message, BluetoothPeripheral peripheral) {
        ArrayList<byte[]> bytes = new ArrayList<>();
        {
            byte currentTxId = Packetize.txId.get();
            PumpState.pushRequestMessage(message, currentTxId);
            TronMessageWrapper wrapper = new TronMessageWrapper(message, currentTxId);
            Packetize.txId.increment();

            for (Packet packet : wrapper.packets()) {
                bytes.add(packet.build());
            }
        }

        int c = 1;
        for (byte[] b : bytes) {
            UUID uuid = CharacteristicUUID.determine(message);
            Timber.d("Writing "+c+"/"+bytes.size()+" characteristics to " + CharacteristicUUID.which(uuid) + ": " + Hex.encodeHexString(b));
            peripheral.writeCharacteristic(ServiceUUID.PUMP_SERVICE_UUID,
                    uuid,
                    b,
                    WriteType.WITH_RESPONSE);
            c++;
        }
    }


    private final BroadcastReceiver updateTextReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra("text");
            statusText.setText(text);
            statusText.postInvalidate();

        }
    };

    private BluetoothCentralManager getCentral() {
        return getBluetoothHandler().central;
    }

    private BluetoothPeripheral getPeripheral(String peripheralAddress) {
        return getCentral().getPeripheral(peripheralAddress);
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] missingPermissions = getMissingPermissions(getRequiredPermissions());
            if (missingPermissions.length > 0) {
                requestPermissions(missingPermissions, ACCESS_LOCATION_REQUEST);
            } else {
                permissionsGranted();
            }
        }
    }

    private String[] getMissingPermissions(String[] requiredPermissions) {
        List<String> missingPermissions = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String requiredPermission : requiredPermissions) {
                if (getApplicationContext().checkSelfPermission(requiredPermission) != PackageManager.PERMISSION_GRANTED) {
                    missingPermissions.add(requiredPermission);
                }
            }
        }
        return missingPermissions.toArray(new String[0]);
    }

    private String[] getRequiredPermissions() {
        int targetSdkVersion = getApplicationInfo().targetSdkVersion;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && targetSdkVersion >= Build.VERSION_CODES.S) {
            return new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT};
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && targetSdkVersion >= Build.VERSION_CODES.Q) {
            return new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        } else return new String[]{Manifest.permission.ACCESS_COARSE_LOCATION};
    }

    private void permissionsGranted() {
        // Check if Location services are on because they are required to make scanning work for SDK < 31
        int targetSdkVersion = getApplicationInfo().targetSdkVersion;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S && targetSdkVersion < Build.VERSION_CODES.S) {
            if (checkLocationServices()) {
                getBluetoothHandler().startScan();
            }
        } else {
            getBluetoothHandler().startScan();
        }
    }

    private boolean areLocationServicesEnabled() {
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            Timber.e("could not get location manager");
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return locationManager.isLocationEnabled();
        } else {
            boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            return isGpsEnabled || isNetworkEnabled;
        }
    }

    private boolean checkLocationServices() {
        if (!areLocationServicesEnabled()) {
            new AlertDialog.Builder(this)
                    .setTitle("Location services are not enabled")
                    .setMessage("Scanning for Bluetooth peripherals requires locations services to be enabled.") // Want to enable?
                    .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    })
                    .create()
                    .show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Check if all permission were granted
        boolean allGranted = true;
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        if (allGranted) {
            permissionsGranted();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Permission is required for scanning Bluetooth peripherals")
                    .setMessage("Please grant permissions")
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            checkPermissions();
                        }
                    })
                    .create()
                    .show();
        }
    }

    private void triggerIDPSegmentDialog(BluetoothPeripheral peripheral) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter IDP ID");
        builder.setMessage("Enter the ID for the Insulin Delivery Profile");

        final EditText input1 = new EditText(this);
        final Context context = this;
        input1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(input1);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String idpId = input1.getText().toString();
                Timber.i("idp id: %s", idpId);

                AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                builder2.setTitle("Enter segment index");
                builder2.setMessage("Enter the index for the Insulin Delivery Profile segment");

                final EditText input2 = new EditText(context);
                input2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                builder2.setView(input2);

                builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String idpSegment = input2.getText().toString();
                        Timber.i("idp segment: %s", idpSegment);

                        writePumpMessage(new IDPSegmentRequest(Integer.parseInt(idpId), Integer.parseInt(idpSegment)), peripheral);
                    }
                });
                builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder2.show();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void triggerIDPSettingsDialog(BluetoothPeripheral peripheral) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter IDP ID");
        builder.setMessage("Enter the ID for the Insulin Delivery Profile");

        final EditText input1 = new EditText(this);
        final Context context = this;
        input1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(input1);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String idpId = input1.getText().toString();
                Timber.i("idp id: %s", idpId);

                writePumpMessage(new IDPSettingsRequest(Integer.parseInt(idpId)), peripheral);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void triggerHistoryLogRequestDialog(BluetoothPeripheral peripheral) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter start log ID");
        builder.setMessage("Enter the ID of the first history log item to return from");

        final EditText input1 = new EditText(this);
        final Context context = this;
        input1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(input1);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String startLog = input1.getText().toString();
                Timber.i("startLog id: %s", startLog);

                AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                builder2.setTitle("Enter number of logs ");
                builder2.setMessage("Enter the max number of logs to return");

                final EditText input2 = new EditText(context);
                input2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                builder2.setView(input2);

                builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String maxLogs = input2.getText().toString();
                        Timber.i("idp segment: %s", maxLogs);

                        writePumpMessage(new HistoryLogRequest(Integer.parseInt(startLog), Integer.parseInt(maxLogs)), peripheral);
                        tandemEventCallback.requestedHistoryLogStartId = Integer.parseInt(startLog);
                    }
                });
                builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder2.show();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void triggerInitiateBolusRequestDialog(BluetoothPeripheral peripheral) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter units to deliver bolus");
        builder.setMessage("Enter the number of units in INTEGER FORM: 1000 = 1 unit, 100 = 0.1 unit, 10 = 0.01 unit. Minimum value is 50 (0.05 unit)");

        final EditText input1 = new EditText(this);
        final Context context = this;
        input1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(input1);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String numUnitsStr = input1.getText().toString();
                Timber.i("numUnits: %s", numUnitsStr);

                if ("".equals(numUnitsStr)) {
                    Timber.e("Not delivering bolus because no units entered.");
                    return;
                }

                AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                builder2.setTitle("CONFIRM BOLUS!!");
                builder2.setMessage("Enter the bolus ID from BolusPermissionRequest. THIS WILL ACTUALLY DELIVER THE BOLUS. Enter a blank value to cancel.");

                final EditText input2 = new EditText(context);
                input2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                builder2.setView(input2);

                builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String bolusIdStr = input2.getText().toString();
                        Timber.i("currentIob: %s", bolusIdStr);

                        if ("".equals(bolusIdStr)) {
                            Timber.e("Not delivering bolus because no bolus ID entered.");
                            return;
                        }

                        int numUnits = Integer.parseInt(numUnitsStr);
                        int bolusId = Integer.parseInt(bolusIdStr);

                        tandemEventCallback.lastBolusId = bolusId;
                        // InitiateBolusRequest(long totalVolume, int bolusTypeBitmask, long foodVolume, long correctionVolume, int bolusCarbs, int bolusBG, long bolusIOB)
                        writePumpMessage(new InitiateBolusRequest(
                                numUnits,
                                bolusId,
                                BolusDeliveryHistoryLog.BolusType.toBitmask(BolusDeliveryHistoryLog.BolusType.FOOD2),
                                0L,
                                0L,
                                0,
                                0,
                                0
                        ), peripheral);
                    }
                });
                builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder2.show();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void triggerCancelBolusRequestDialog(BluetoothPeripheral peripheral) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("CancelBolusRequest");
        builder.setMessage("Enter the bolus ID (this can be received from currentStatus.LastBolusStatusV2)");

        final EditText input1 = new EditText(this);
        final Context context = this;
        input1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        if (tandemEventCallback.lastBolusId > 0) {
            input1.setText(String.valueOf(tandemEventCallback.lastBolusId));
        }
        builder.setView(input1);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String bolusIdStr = input1.getText().toString();
                Timber.i("bolusId: %s", bolusIdStr);

                if ("".equals(bolusIdStr)) {
                    Timber.e("Not cancelling bolus because no units entered.");
                    return;
                }

                int bolusId = Integer.parseInt(bolusIdStr);
                writePumpMessage(new CancelBolusRequest(bolusId), peripheral);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    private void triggerMessageWithBolusIdParameter(BluetoothPeripheral peripheral, Class<? extends Message> messageClass) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(messageClass.getSimpleName());
        builder.setMessage("Enter the bolus ID (this can be received from the in-progress bolus)");

        final EditText input1 = new EditText(this);
        final Context context = this;
        input1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        if (tandemEventCallback.lastBolusId > 0) {
            input1.setText(String.valueOf(tandemEventCallback.lastBolusId));
        }
        builder.setView(input1);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String bolusIdStr = input1.getText().toString();
                Timber.i("bolusId: %s", bolusIdStr);

                if ("".equals(bolusIdStr)) {
                    Timber.e("Not sending message because no bolus ID entered.");
                    return;
                }

                int bolusId = Integer.parseInt(bolusIdStr);
                Class<?>[] constructorType = {long.class};
                Message message;
                try {
                    message = messageClass.getConstructor(constructorType).newInstance(bolusId);
                } catch (IllegalAccessException|InstantiationException|InvocationTargetException|NoSuchMethodException e) {
                    Timber.e(e);
                    return;
                }
                writePumpMessage(message, peripheral);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void triggerPairDialog(BluetoothPeripheral peripheral, String btAddress, CentralChallengeResponse challenge) {
        String btName = peripheral.getName();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter pairing code (case-sensitive)");
        builder.setMessage("Enter the pairing code from Bluetooth Settings > Pair Device to connect to:\n\n" + btName + " (" + btAddress + ")");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);

        String savedPairingCode = PumpState.getPairingCode(getApplicationContext());
        if (!Strings.isNullOrEmpty(savedPairingCode)) {
            input.setText(savedPairingCode);

            if (PumpState.failedPumpConnectionAttempts == 0) {
                triggerImmediatePair(peripheral, savedPairingCode, challenge);
                return;
            }
        }
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String pairingCode = input.getText().toString();
                Timber.i("pairing code inputted: %s", pairingCode);

                triggerImmediatePair(peripheral, pairingCode, challenge);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    //
    // bolus
    //

    static class BolusParameters {
        boolean windowOpen;
        // bolus calc
        boolean carbEntryEnabled;
        long carbRatio;
        double iob;
        int cartridgeRemainingInsulin;
        int correctionFactor;
        int isf;
        boolean isAutopopAllowed;
        int targetBg;
        boolean exceeded;
        int maxBolusAmount;
        // bolus internal parameters
        double calculatedUnitsFromGlucose;
        double calculatedUnitsFromCarbs;
        // bolus parameters
        double units;
        int carbsGrams;
        int glucoseMgdl;
        BolusParameters(double units, int carbsGrams, int glucoseMgdl) {
            this.windowOpen = false;
            this.units = units;
            this.carbsGrams = carbsGrams;
            this.glucoseMgdl = glucoseMgdl;
        }
        BolusParameters() {
            this.windowOpen = true;
        }

        public void fillParameters(double units, int carbsGrams, int glucoseMgdl) {
            this.units = units;
            this.carbsGrams = carbsGrams;
            this.glucoseMgdl = glucoseMgdl;

            this.calculatedUnitsFromGlucose = 0;
            this.calculatedUnitsFromCarbs = 0;
        }

        public void fillBolusCalcDataSnapshot(
            boolean carbEntryEnabled,
            long carbRatio,
            long iob,
            int cartridgeRemainingInsulin,
            int correctionFactor,
            int isf,
            boolean isAutopopAllowed,
            int targetBg,
            boolean exceeded,
            int maxBolusAmount)
        {
            this.carbEntryEnabled = carbEntryEnabled;
            this.carbRatio = carbRatio;
            this.iob = InsulinUnit.from1000To1(iob); // 1000-unit
            this.cartridgeRemainingInsulin = cartridgeRemainingInsulin;
            this.correctionFactor = correctionFactor;
            this.isf = isf;
            this.isAutopopAllowed = isAutopopAllowed;
            this.targetBg = targetBg;
            this.exceeded = exceeded;
            this.maxBolusAmount = maxBolusAmount;

            this.calculatedUnitsFromGlucose = -1 * this.iob;

        }

        public void updateUnits() {
            units = Math.max(0, calculatedUnitsFromCarbs + calculatedUnitsFromGlucose);
        }

        public boolean hasData() {
            return units > 0;
        }

        public boolean hasBolusCalcData() {
            return targetBg > 0;
        }

        public String toString() {
            return JavaHelpers.autoToString(this, ImmutableSet.of());
        }

        public boolean invalid() {
            return !Strings.isNullOrEmpty(invalidReason());
        }

        public String invalidReason() {
            if (units > cartridgeRemainingInsulin) {
                return units + " units is greater than cartridge amount (" + cartridgeRemainingInsulin + ")";
            }
            double maxBolus = InsulinUnit.from1000To1((long) maxBolusAmount);
            if (units > maxBolus) {
                return units + " units is greater than max bolus (" + maxBolus + ")";
            }

            return "";
        }
    }

    private BolusParameters bolusParameters = null;
    private EditText bolusUnitsView;
    private EditText carbsGramsView;
    private EditText glucoseMgdlView;
    private TextView bolusCalcDetails;
    private void startBolusProcess(BluetoothPeripheral peripheral) {
        ApiVersion apiVer = PumpState.getPumpAPIVersion();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (!apiVer.greaterThan(KnownApiVersion.API_V2_1)) {
            builder.setTitle("Bolus Not Supported");
            builder.setMessage("The API version of this pump does not support remote bolus:\n\n" + apiVer);
            builder.setPositiveButton("OK", null);
            builder.show();
            return;
        }

        builder.setTitle("Enter bolus");
        builder.setMessage("Enter bolus information\n(THIS SCREEN IS A WORK IN PROGRESS! PLEASE VERIFY INSULIN CORRECTION CALCULATIONS ON YOUR PUMP!)");

        // TODO: automatically adjust insulin based on carbs, and send RemoteBgEntry/RemoteCarbEntry

//        final EditText input = new EditText(this);
//        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
//        builder.setView(input);
        final View bolusWindow = getLayoutInflater().inflate(R.layout.bolus_window, null);
        builder.setView(bolusWindow);

        bolusUnitsView = bolusWindow.findViewById(R.id.bolusUnits);
        carbsGramsView = bolusWindow.findViewById(R.id.carbsGrams);
        glucoseMgdlView = bolusWindow.findViewById(R.id.glucoseMgdl);
        bolusCalcDetails = bolusWindow.findViewById(R.id.bolusCalcDetails);
        bolusCalcDetails.setText("Waiting for bolus calculator data...");

        if (isWear()) {
            View carbsAndGlucoseLayout = bolusWindow.findViewById(R.id.carbsAndGlucoseLayout);
            ViewGroup.LayoutParams layoutParams = carbsAndGlucoseLayout.getLayoutParams();
            layoutParams.width = ActionBar.LayoutParams.WRAP_CONTENT;
            layoutParams.height = ActionBar.LayoutParams.WRAP_CONTENT;
            carbsAndGlucoseLayout.setLayoutParams(layoutParams);

            for (View view : new View[]{
                    carbsGramsView, bolusWindow.findViewById(R.id.carbsGramsText),
                    glucoseMgdlView, bolusWindow.findViewById(R.id.glucoseMgdlText)
            }) {
                layoutParams = view.getLayoutParams();
                layoutParams.width = 180;
                view.setLayoutParams(layoutParams);
            }
        }

        bolusParameters = new BolusParameters();
        tandemEventCallback.bolusInProgress = true;

        writePumpMessage(new BolusCalcDataSnapshotRequest(), peripheral);

        final MainActivity mainAct = this;
        builder.setPositiveButton("Bolus", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String bolusUnitsStr = bolusUnitsView.getText().toString();
                if (Strings.isNullOrEmpty(bolusUnitsStr)) {
                    bolusUnitsStr = "0.0";
                }
                double bolusUnits = Double.parseDouble(bolusUnitsStr);

                String carbsGramsStr = carbsGramsView.getText().toString();
                if (Strings.isNullOrEmpty(carbsGramsStr) || carbsGramsStr.equals("Disabled")) {
                    carbsGramsStr = "0";
                }
                int carbsGrams = Integer.parseInt(carbsGramsStr);

                String glucoseMgdlStr = glucoseMgdlView.getText().toString();
                if (Strings.isNullOrEmpty(glucoseMgdlStr)) {
                    glucoseMgdlStr = "0";
                }
                int glucoseMgdl = Integer.parseInt(glucoseMgdlStr);
                Timber.i("bolusUnits: %f carbsGrams: %d glucoseMgdl: %d", bolusUnits, carbsGrams, glucoseMgdl);

                mainAct.bolusParameters.fillParameters(bolusUnits, carbsGrams, glucoseMgdl);

                if (mainAct.bolusParameters.invalid()) {
                    dialog.cancel();
                    new AlertDialog.Builder(mainAct)
                            .setTitle("Invalid bolus entry")
                            .setMessage(mainAct.bolusParameters.invalidReason())
                            .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    startBolusProcess(peripheral);
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return;
                }

                writePumpMessage(new BolusPermissionRequest(), peripheral);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                tandemEventCallback.bolusInProgress = false;
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                tandemEventCallback.bolusInProgress = false;
            }
        });

        carbsGramsView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            private Toast lastToast = null;

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editable.toString();
                if (str.isEmpty()) {
                    str = "0";
                }
                if (!bolusParameters.carbEntryEnabled) {
                    Toast.makeText(mainAct, "Carb entry disabled!", Toast.LENGTH_SHORT).show();
                    return;
                }
                int carbsGrams = Integer.parseInt(str);
                bolusParameters.carbsGrams = carbsGrams;
                Timber.i("carbsGrams changed: " + carbsGrams);
                double ratio = InsulinUnit.from1000To1(bolusParameters.carbRatio);
                Preconditions.checkState(ratio > 0, "ratio is invalid: " + bolusParameters.carbRatio);
                // keep 2 decimal places
                bolusParameters.calculatedUnitsFromCarbs = Double.parseDouble(String.format("%.2f", carbsGrams / ratio));
                updateBolusParameterUnits();
            }
        });

        glucoseMgdlView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editable.toString();
                if (str.isEmpty()) {
                    str = "0";
                }
                int glucoseMgdl = Integer.parseInt(str);
                Timber.i("glucoseMgdl changed: " + glucoseMgdl);
                bolusParameters.glucoseMgdl = glucoseMgdl;

                if (glucoseMgdl > 40) {
                    checkAutomaticCorrection();
                }
            }
        });

        bolusUnitsView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            private Toast lastToast;

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editable.toString();
                if (str.isEmpty()) {
                    str = "0";
                }
                double bolusUnits = Double.parseDouble(str);
                Timber.i("bolusUnits changed: " + bolusUnits);
                int cartRemaining = bolusParameters.cartridgeRemainingInsulin;
                if (lastToast != null) {
                    lastToast.cancel();
                }
                if (bolusUnits > cartRemaining) {
                    String text = "INVALID: " + bolusUnits + " units is greater than cartridge amount (" + cartRemaining + ")";
                    Timber.w(text);
                    lastToast = Toast.makeText(mainAct, text, Toast.LENGTH_LONG);
                    lastToast.show();
                    return;
                }
                double maxBolus = InsulinUnit.from1000To1((long) bolusParameters.maxBolusAmount);
                if (bolusUnits > maxBolus) {
                    String text = "INVALID: " + bolusUnits + " units is greater than max bolus (" + maxBolus + ")";
                    Timber.w(text);
                    lastToast = Toast.makeText(mainAct, text, Toast.LENGTH_LONG);
                    lastToast.show();
                    return;
                }
            }
        });

        builder.show();
    }

    private final BroadcastReceiver gotBolusCalcResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String address = intent.getStringExtra("address");
            BluetoothPeripheral peripheral = getPeripheral(address);

            boolean carbEntryEnabled = intent.getBooleanExtra("carbEntryEnabled", false);
            long carbRatio = intent.getLongExtra("carbRatio", 0);
            long iob = intent.getLongExtra("iob", 0);
            int remainingInsulin = intent.getIntExtra("remainingInsulin", 0);
            int correctionFactor = intent.getIntExtra("correctionFactor", 0);
            int isf = intent.getIntExtra("isf", 0);
            boolean autopopAllowed = intent.getBooleanExtra("autopopAllowed", false);
            int targetBg = intent.getIntExtra("targetBg", 0);
            boolean exceeded = intent.getBooleanExtra("exceeded", false);
            int maxBolusAmount = intent.getIntExtra("maxBolusAmount", -1);

            Preconditions.checkState(bolusParameters != null);
            bolusParameters.fillBolusCalcDataSnapshot(carbEntryEnabled, carbRatio, iob, remainingInsulin, correctionFactor, isf, autopopAllowed, targetBg, exceeded, maxBolusAmount);
            bolusCalcDetails.setText("");
            carbsGramsView.setEnabled(bolusParameters.carbEntryEnabled);
            if (!bolusParameters.carbEntryEnabled) {
                carbsGramsView.setHint("Disabled");
            }
            if (bolusParameters.isAutopopAllowed) {
                Toast.makeText(getApplicationContext(), "Fetching latest BG...", Toast.LENGTH_SHORT).show();
                writePumpMessage(new LastBGRequest(), peripheral);
            } else {
                // ensure IOB is processed
                checkAutomaticCorrection();
            }
        }
    };


    private final BroadcastReceiver gotBolusCalcLastBgResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String address = intent.getStringExtra("address");
            BluetoothPeripheral peripheral = getPeripheral(address);

            long timestamp = intent.getLongExtra("timestamp", 0);
            int sourceId = intent.getIntExtra("sourceId", -1);
            int bgValue = intent.getIntExtra("bgValue", -1);

            LastBGResponse.BgSource bgSource = LastBGResponse.BgSource.fromId(sourceId);

            if (bgSource != LastBGResponse.BgSource.CGM) {
                Toast.makeText(getApplicationContext(), "Not filling BG in Bolus Calculator because source is not CGM (" + bgSource + ")", Toast.LENGTH_SHORT).show();

                // ensure IOB is processed
                checkAutomaticCorrection();
                return;
            }

            Instant time = Dates.fromJan12008EpochSecondsToDate(timestamp);
            Instant now = Instant.now();
            long recencyMins = ChronoUnit.MINUTES.between(time, now);
            if (recencyMins >= 15) {
                Toast.makeText(getApplicationContext(), "Not filling BG in Bolus Calculator because last BG is from " +recencyMins + " minutes ago", Toast.LENGTH_SHORT).show();

                // ensure IOB is processed
                checkAutomaticCorrection();
                return;
            }

            if (!bolusParameters.isAutopopAllowed) {
                Toast.makeText(getApplicationContext(), "Not filling BG in Bolus Calculator because autopopulation is disallowed", Toast.LENGTH_SHORT).show();

                // ensure IOB is processed
                checkAutomaticCorrection();
                return;
            }

            glucoseMgdlView.setText(String.valueOf(bgValue));
            bolusParameters.glucoseMgdl = bgValue;
            checkAutomaticCorrection();
        }
    };

    private final void checkAutomaticCorrection() {
        Preconditions.checkState(bolusUnitsView != null && carbsGramsView != null && glucoseMgdlView != null);

        String glucoseMgdlStr = glucoseMgdlView.getText().toString();
        if (Strings.isNullOrEmpty(glucoseMgdlStr)) {
            glucoseMgdlStr = "0";
        }
        int glucoseMgdl = Integer.parseInt(glucoseMgdlStr);

        if (bolusParameters.isf < 1) {
            Toast.makeText(getApplicationContext(), "No automatic correction can be made because there is no Correction Factor in your pump profile.", Toast.LENGTH_LONG).show();
            return;
        }

        if (glucoseMgdl <= 0) {
            return;
        }

        Context context = MainActivity.this;

        int bgDiff = glucoseMgdl - bolusParameters.targetBg;
        double addedInsulin = (1.0 * bgDiff) / bolusParameters.isf - bolusParameters.iob;
        Timber.i("addedInsulin: %.2f bgDiff: %d", addedInsulin, bgDiff);
        String suffix = "\n\nCurrent IOB: " + String.format("%.2f", bolusParameters.iob) + "u\nEntered BG: " + glucoseMgdlStr + "mg/dL";

        // TODO: rethink.
        if (addedInsulin > 0) {
            new AlertDialog.Builder(context)
                    .setTitle("Correction")
                    .setMessage("Your BG is above target. Add correction bolus?" + suffix)
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            bolusParameters.calculatedUnitsFromGlucose = addedInsulin;
                            updateBolusParameterUnits();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else if (bolusParameters.calculatedUnitsFromCarbs >= 0) {
            // If we can reduce the bolus
            if (bolusParameters.calculatedUnitsFromCarbs + addedInsulin > 0) {
                new AlertDialog.Builder(context)
                        .setTitle("Reduction")
                        .setMessage("Your BG is below target. Reduce bolus calculation?" + suffix)
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                bolusParameters.calculatedUnitsFromGlucose = addedInsulin;
                                updateBolusParameterUnits();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            } else {
                bolusParameters.calculatedUnitsFromGlucose = addedInsulin;
                updateBolusParameterUnits();

                new AlertDialog.Builder(context)
                        .setTitle("Your BG Is Low")
                        .setMessage("Due to your BG and IOB, no bolus has been calculated. Eat carbs and re-test BG." + suffix)
                        .setNegativeButton(android.R.string.ok, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        } else if (glucoseMgdl > bolusParameters.targetBg) {
            bolusParameters.calculatedUnitsFromGlucose = addedInsulin;
            updateBolusParameterUnits();

            new AlertDialog.Builder(context)
                    .setTitle("Your BG is Above Target")
                    .setMessage("Due to IOB, no correction will be calculated. Re-test BG as necessary." + suffix)
                    .setNegativeButton(android.R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else if (glucoseMgdl < bolusParameters.targetBg) {

        } else {
            bolusParameters.calculatedUnitsFromGlucose = addedInsulin;
            updateBolusParameterUnits();

            new AlertDialog.Builder(context)
                    .setTitle("Your BG Is Low")
                    .setMessage("Eat carbs and re-test BG." + suffix)
                    .setNegativeButton(android.R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    private void updateBolusParameterUnits() {
        bolusParameters.updateUnits();
        bolusUnitsView.setText(String.format("%.2f", bolusParameters.units));

        Timber.i("bolusParameters update: %s", bolusParameters);

        if (bolusParameters.calculatedUnitsFromGlucose > 0) {
            bolusCalcDetails.setText("Insulin units were increased due to BG above target");
        } else if (bolusParameters.calculatedUnitsFromGlucose < 0) {
            String reason = bolusParameters.glucoseMgdl < bolusParameters.targetBg ? "BG below target" : "IOB";
            if (bolusParameters.units == 0) {
                bolusCalcDetails.setText("Insulin units were set to 0 due to " + reason);
            } else {
                bolusCalcDetails.setText("Insulin units were decreased due to " + reason);
            }
        } else {
            bolusCalcDetails.setText("");
        }
    }

    private final BroadcastReceiver gotBolusPermissionResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String address = intent.getStringExtra("address");
            BluetoothPeripheral peripheral = getPeripheral(address);

            int bolusId = intent.getIntExtra("bolusId", -1);
            int status = intent.getIntExtra("status", -1);
            int nackReasonId = intent.getIntExtra("nackReasonId", -1);

            if (status != 0 || nackReasonId != 0) {
                new AlertDialog.Builder(context)
                        .setTitle("Bolus Error")
                        .setMessage("Unable to deliver bolus due to: " + BolusPermissionResponse.NackReason.fromId(nackReasonId) + " (status: " + status + ")")
                        .setNegativeButton(android.R.string.ok, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                tandemEventCallback.bolusInProgress = false;
                return;
            }

            Preconditions.checkState(bolusParameters != null && bolusParameters.hasData());

            long bolusMilliunits = InsulinUnit.from1To1000(bolusParameters.units);
            new AlertDialog.Builder(context)
                    .setTitle("Bolus Confirm (" + bolusId + ")")
                    .setMessage("Carbs: " + bolusParameters.carbsGrams + "g\n" +
                                "BG: " + (bolusParameters.glucoseMgdl == 0 ? "n/a" : bolusParameters.glucoseMgdl + "mg/dL") + "\n" +
                                "Units to deliver: " + bolusParameters.units + "u")
                    .setPositiveButton("Deliver Bolus", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            tandemEventCallback.lastBolusId = bolusId;
                            writePumpMessage(new InitiateBolusRequest(
                                    bolusMilliunits,
                                    bolusId,
                                    BolusDeliveryHistoryLog.BolusType.FOOD2.mask(),
                                    0,
                                    0,
                                    bolusParameters.carbsGrams,
                                    bolusParameters.glucoseMgdl,
                                    0
                            ), peripheral);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
        }
    };

    private final BroadcastReceiver gotInitiateBolusResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String address = intent.getStringExtra("address");
            BluetoothPeripheral peripheral = getPeripheral(address);

            int bolusId = intent.getIntExtra("bolusId", -1);
            int status = intent.getIntExtra("status", -1);
            int statusTypeId = intent.getIntExtra("statusTypeId", -1);
            String statusType = intent.getStringExtra("statusType");

            if (status != 0 || statusTypeId != 0) {
                new AlertDialog.Builder(context)
                        .setTitle("Bolus Error")
                        .setMessage("Bolus ID: " + bolusId + " returned status " + status +" and statusType " + statusType + " (" + statusTypeId + ")")
                        .setNegativeButton(android.R.string.ok, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                tandemEventCallback.bolusInProgress = false;
                return;
            }

            new AlertDialog.Builder(context)
                    .setTitle("Bolus Delivered")
                    .setMessage("Bolus is being delivered.\n\nBolus ID: " + bolusId)
                    .setPositiveButton("CANCEL DELIVERY", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            writePumpMessage(new CancelBolusRequest(bolusId), peripheral);
                        }
                    })
                    .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            tandemEventCallback.bolusInProgress = false;
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
        }
    };

    private final BroadcastReceiver gotCancelBolusResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String address = intent.getStringExtra("address");
            BluetoothPeripheral peripheral = getPeripheral(address);

            int bolusId = intent.getIntExtra("bolusId", -1);
            int status = intent.getIntExtra("status", -1);
            int reason = intent.getIntExtra("reason", -1);

            new AlertDialog.Builder(context)
                    .setTitle("Bolus Cancelled")
                    .setMessage("Bolus ID " + bolusId + " was cancelled.\n\nStatus: " + status + "\nReason: " + reason)
                    .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            writePumpMessage(new LastBolusStatusV2Request(), peripheral);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
        }
    };

    private final BroadcastReceiver gotLastBolusStatusResponseAfterCancelReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String address = intent.getStringExtra("address");
            BluetoothPeripheral peripheral = getPeripheral(address);

            int bolusId = intent.getIntExtra("bolusId", -1);
            String status = intent.getStringExtra("status");
            int statusId = intent.getIntExtra("statusId", -1);
            long deliveredVolume = intent.getLongExtra("deliveredVolume", -1);
            long requestedVolume = intent.getLongExtra("requestedVolume", -1);
            String source = intent.getStringExtra("source");
            int sourceId = intent.getIntExtra("sourceId", -1);

            new AlertDialog.Builder(context)
                    .setTitle("Bolus Status")
                    .setMessage("Bolus ID " + bolusId + " delivered " + InsulinUnit.from1000To1(deliveredVolume) + " / " + InsulinUnit.from1000To1(requestedVolume) + " units:\n\n" +
                            "Status: " + status + " (" + statusId + ")\n" +
                            "Delivered: " + InsulinUnit.from1000To1(deliveredVolume) + "u\n" +
                            "Requested: " + InsulinUnit.from1000To1(requestedVolume) + "u\n" +
                            "Source: " + source + " ( " + sourceId + ")")
                    .setNegativeButton(android.R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();

            tandemEventCallback.bolusInProgress = false;
        }
    };

    private final BroadcastReceiver gotBolusPermissionRevokedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String address = intent.getStringExtra("address");
            BluetoothPeripheral peripheral = getPeripheral(address);

            int bolusId = intent.getIntExtra("bolusId", -1);
            String changeReason = intent.getStringExtra("changeReason");
            int changeReasonId = intent.getIntExtra("changeReasonId", -1);
            boolean currentPermissionHolder = intent.getBooleanExtra("currentPermissionHolder", false);
            boolean isAcked = intent.getBooleanExtra("isAcked", false);

            new AlertDialog.Builder(context)
                    .setTitle("Bolus Permission Revoked")
                    .setMessage("A bolus with bolusId " + bolusId + " cannot be completed right now: " + changeReason + " (" + changeReasonId + ")\n\nAcked: " + isAcked + "\nCurrent permission holder: " + currentPermissionHolder)
                    .setNegativeButton(android.R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();

            tandemEventCallback.bolusInProgress = false;
        }
    };

    private void triggerImmediatePair(BluetoothPeripheral peripheral, String pairingCode, CentralChallengeResponse challenge) {
        PumpState.setPairingCode(getApplicationContext(), pairingCode);

        tandemEventCallback.pair(peripheral, challenge, pairingCode);
    }

    private static boolean isWear() {
        return Build.MODEL.toLowerCase().contains("watch") || Build.MODEL.toLowerCase().contains("wear");
    }
}
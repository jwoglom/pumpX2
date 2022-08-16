package com.jwoglom.pumpx2.example;

import static com.jwoglom.pumpx2.example.PumpX2TandemPump.GOT_HISTORY_LOG_STATUS_RECEIVER;
import static com.jwoglom.pumpx2.example.PumpX2TandemPump.GOT_HISTORY_LOG_STREAM_RECEIVER;
import static com.jwoglom.pumpx2.example.PumpX2TandemPump.PUMP_CONNECTED_COMPLETE_INTENT;
import static com.jwoglom.pumpx2.example.PumpX2TandemPump.PUMP_CONNECTED_STAGE3_INTENT;
import static com.jwoglom.pumpx2.example.PumpX2TandemPump.PUMP_CONNECTED_STAGE1_INTENT;
import static com.jwoglom.pumpx2.example.PumpX2TandemPump.PUMP_CONNECTED_STAGE2_INTENT;
import static com.jwoglom.pumpx2.example.PumpX2TandemPump.PUMP_INVALID_CHALLENGE_INTENT;
import static com.jwoglom.pumpx2.example.PumpX2TandemPump.UPDATE_TEXT_RECEIVER;
import static com.jwoglom.pumpx2.pump.messages.bluetooth.PumpStateSupplier.authenticationKey;
import static com.jwoglom.pumpx2.pump.messages.bluetooth.PumpStateSupplier.pumpTimeSinceReset;
import static java.util.stream.Collectors.toList;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.common.base.Strings;
import com.jwoglom.pumpx2.pump.PumpState;
import com.jwoglom.pumpx2.pump.bluetooth.TandemBluetoothHandler;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.Packetize;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.bluetooth.ServiceUUID;
import com.jwoglom.pumpx2.pump.messages.bluetooth.TronMessageWrapper;
import com.jwoglom.pumpx2.pump.messages.bluetooth.models.Packet;
import com.jwoglom.pumpx2.pump.messages.builders.CurrentBatteryBuilder;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.HistoryLogRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.HistoryLogStatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.IDPSegmentRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.IDPSettingsRequest;
import com.jwoglom.pumpx2.pump.messages.response.authentication.CentralChallengeResponse;
import com.jwoglom.pumpx2.pump.messages.util.MessageHelpers;
import com.jwoglom.pumpx2.shared.L;
import com.welie.blessed.BluetoothCentralManager;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.WriteType;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    static {
        L.getAndroidLogD = Log::d;
        L.getAndroidLogW = Log::w;
        L.getAndroidLogWThrowable = Log::w;
        L.getAndroidLogE = Log::e;
        L.getAndroidLogEThrowable = Log::e;
    }
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int ACCESS_LOCATION_REQUEST = 2;

    private TextView statusText;
    private Button retryConnectButton;
    private Spinner requestMessageSpinner;
    private Button requestSendButton;
    private Button batteryRequestButton;
    private Button recentHistoryLogsButton;
    private PumpX2TandemPump tandemEventCallback;
    private TandemBluetoothHandler bluetoothHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusText = findViewById(R.id.statusText);
        retryConnectButton = findViewById(R.id.retryConnect);
        retryConnectButton.setOnClickListener((view) -> resetBluetoothHandler().startScan());

        requestMessageSpinner = findViewById(R.id.request_message_spinner);

        List<String> requestMessages = MessageHelpers.getAllPumpRequestMessages()
                .stream().filter(m -> !m.startsWith("authentication.")).collect(toList());
        Timber.i("requestMessages: %s", requestMessages);
        ArrayAdapter<String> adapter  = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, requestMessages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        requestMessageSpinner.setAdapter(adapter);

        requestSendButton = findViewById(R.id.request_message_send);
        batteryRequestButton = findViewById(R.id.battery_request_button);
        recentHistoryLogsButton = findViewById(R.id.recent_history_logs);

        registerReceiver(pumpConnectedStage1Receiver, new IntentFilter(PUMP_CONNECTED_STAGE1_INTENT));
        registerReceiver(pumpConnectedStage2Receiver, new IntentFilter(PUMP_CONNECTED_STAGE2_INTENT));
        registerReceiver(pumpConnectedStage3Receiver, new IntentFilter(PUMP_CONNECTED_STAGE3_INTENT));
        registerReceiver(pumpConnectedCompleteReceiver, new IntentFilter(PUMP_CONNECTED_COMPLETE_INTENT));
        registerReceiver(updateTextReceiver, new IntentFilter(UPDATE_TEXT_RECEIVER));
        registerReceiver(gotHistoryLogStatusReceiver, new IntentFilter(GOT_HISTORY_LOG_STATUS_RECEIVER));
        registerReceiver(gotHistoryLogStreamReceiver, new IntentFilter(GOT_HISTORY_LOG_STREAM_RECEIVER));
        registerReceiver(pumpConnectedInvalidChallengeReceiver, new IntentFilter(PUMP_INVALID_CHALLENGE_INTENT));

        L.w("X2", "Build.MANUFACTURER=" + Build.MANUFACTURER+" Build.MODEL=" + Build.MODEL + " Build.SDK_INT=" + Build.VERSION.SDK_INT);
    }

    @Override
    protected void onResume() {
        super.onResume();

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

            requestMessageSpinner.setVisibility(View.VISIBLE);
            requestMessageSpinner.postInvalidate();


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
                writePumpMessage(CurrentBatteryBuilder.create(PumpState.getPumpAPIVersion(context)), peripheral);
            });


            recentHistoryLogsButton.setVisibility(View.VISIBLE);
            recentHistoryLogsButton.setOnClickListener((z) -> {
                waitingOnHistoryLogStatus = true;
                writePumpMessage(new HistoryLogStatusRequest(), peripheral);
            });
        }
    };

    private Queue<Integer> remainingSequenceNums = new LinkedList<>();
    private int remainingSequenceNumsInBatch = 0;

    private static int sequenceNumBatchSize = 3;

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
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("History Logs");
                builder.setMessage("Start number. " +
                        "Total count: "+numEntries+" from "+firstSequenceNum+" - "+lastSequenceNum);

                final EditText input = new EditText(context);
                input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int start = Integer.parseInt(input.getText().toString());
                        Timber.i("requested start: %d", start);

                        AlertDialog.Builder builder2 = new AlertDialog.Builder(context);

                        builder2.setTitle("History Logs");
                        builder2.setMessage("End number. " +
                                "Total count: "+numEntries+" from "+firstSequenceNum+" - "+lastSequenceNum);

                        final EditText input2 = new EditText(context);
                        input2.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                        builder2.setView(input2);

                        builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int end = Integer.parseInt(input2.getText().toString());
                                Timber.i("requested end: %d", end);

                                remainingSequenceNums = sequenceNumberList(start, end);
                                remainingSequenceNumsInBatch = sequenceNumBatchSize;

                                HistoryLogRequest req = new HistoryLogRequest(start, sequenceNumBatchSize);
                                Timber.d("Writing HistoryLogRequest: %s", req);
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

                        HistoryLogRequest req = new HistoryLogRequest(start, 50);
                        Timber.d("Writing HistoryLogRequest: %s", req);
                        writePumpMessage(req, peripheral);

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
            Timber.d("Got history logs count: %d", numHistoryLogs);
            for (int i=0; i<numHistoryLogs; i++) {
                remainingSequenceNums.poll();
                remainingSequenceNumsInBatch--;
            }

            if (remainingSequenceNumsInBatch > 0) {
                Timber.i("Missed %d sequence nums (peek: %d)", remainingSequenceNumsInBatch, remainingSequenceNums.peek());
                for (int i=0; i<remainingSequenceNumsInBatch; i++) {
                    Timber.i("MISSED SEQUENCE NUMBER %d", remainingSequenceNums.poll());
                    remainingSequenceNumsInBatch--;
                }
            }

            if (remainingSequenceNums.isEmpty()) {
                Timber.i("No sequence numbers remaining!");
                return;
            }

            int count = Math.min(remainingSequenceNums.size(), sequenceNumBatchSize);
            remainingSequenceNumsInBatch = sequenceNumBatchSize;
            HistoryLogRequest req = new HistoryLogRequest(remainingSequenceNums.peek(), count);
            Timber.d("Writing HistoryLogRequest: %s", req);
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

    // Adds the given pump message to the BT device's characteristic write queue.
    private void writePumpMessage(Message message, BluetoothPeripheral peripheral) {
        ArrayList<byte[]> authBytes = new ArrayList<>();
        {
            byte currentTxId = Packetize.txId.get();
            PumpState.pushRequestMessage(message, currentTxId);
            TronMessageWrapper wrapper = new TronMessageWrapper(message, currentTxId);
            Packetize.txId.increment();

            for (Packet packet : wrapper.packets()) {
                authBytes.add(packet.build());
            }
        }

        for (byte[] b : authBytes) {
            peripheral.writeCharacteristic(ServiceUUID.PUMP_SERVICE_UUID,
                    CharacteristicUUID.determine(message),
                    b,
                    WriteType.WITH_RESPONSE);
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

    private void triggerPairDialog(BluetoothPeripheral peripheral, String btAddress, CentralChallengeResponse challenge) {
        String btName = peripheral.getName();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter pairing code (case-sensitive)");
        builder.setMessage("Enter the pairing code from Bluetooth Settings > Pair Device to connect to:\n\n" + btName + " (" + btAddress + ")");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
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

// Set up the buttons
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

    private void triggerImmediatePair(BluetoothPeripheral peripheral, String pairingCode, CentralChallengeResponse challenge) {
        PumpState.setPairingCode(getApplicationContext(), pairingCode);

        tandemEventCallback.pair(peripheral, challenge, pairingCode);
    }
}
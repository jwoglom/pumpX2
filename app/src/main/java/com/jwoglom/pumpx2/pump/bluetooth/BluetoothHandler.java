package com.jwoglom.pumpx2.pump.bluetooth;

import static com.welie.blessed.BluetoothBytesParser.bytes2String;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Pair;

import com.jwoglom.pumpx2.pump.PumpState;
import com.jwoglom.pumpx2.pump.events.PumpResponseMessageEvent;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.request.UndefinedRequest;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.AlarmStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.AlertStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ApiVersionResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CGMHardwareInfoResponse;
import com.jwoglom.pumpx2.pump.messages.response.authentication.CentralChallengeResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ControlIQIOBResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.NonControlIQIOBResponse;
import com.jwoglom.pumpx2.pump.messages.response.authentication.PumpChallengeResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.PumpFeaturesV1Response;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.PumpGlobalsResponse;
import com.welie.blessed.BluetoothBytesParser;
import com.welie.blessed.BluetoothCentralManager;
import com.welie.blessed.BluetoothCentralManagerCallback;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.BluetoothPeripheralCallback;
import com.welie.blessed.ConnectionPriority;
import com.welie.blessed.GattStatus;
import com.welie.blessed.HciStatus;
import com.welie.blessed.ScanFailure;

import org.apache.commons.codec.binary.Hex;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import timber.log.Timber;
import com.jwoglom.pumpx2.util.timber.DebugTree;

public class BluetoothHandler {

    public static final String PUMP_CONNECTED_STAGE1_INTENT = "jwoglom.pumpx2.pumpconnected.stage1";
    public static final String PUMP_CONNECTED_STAGE2_INTENT = "jwoglom.pumpx2.pumpconnected.stage2";
    public static final String PUMP_CONNECTED_STAGE3_INTENT = "jwoglom.pumpx2.pumpconnected.stage3";
    public static final String PUMP_CONNECTED_STAGE4_INTENT = "jwoglom.pumpx2.pumpconnected.stage4";
    public static final String PUMP_CONNECTED_STAGE5_INTENT = "jwoglom.pumpx2.pumpconnected.stage5";
    public static final String UPDATE_TEXT_RECEIVER = "jwoglom.pumpx2.updatetextreceiver";
    public static final String PUMP_INVALID_CHALLENGE_INTENT = "jwoglom.pumpx2.invalidchallenge";


    public BluetoothCentralManager central;
    private static BluetoothHandler instance = null;
    private final Context context;
    private final Handler handler = new Handler();

    // Callback for peripherals
    private final BluetoothPeripheralCallback peripheralCallback = new BluetoothPeripheralCallback() {
        @Override
        public void onServicesDiscovered(@NotNull BluetoothPeripheral peripheral) {
            // Request a higher MTU, iOS always asks for 185
            peripheral.requestMtu(185);

            // Request a new connection priority
            peripheral.requestConnectionPriority(ConnectionPriority.HIGH);

            // Read manufacturer and model number from the Device Information Service
            peripheral.readCharacteristic(ServiceUUID.DIS_SERVICE_UUID, CharacteristicUUID.MANUFACTURER_NAME_CHARACTERISTIC_UUID);
            peripheral.readCharacteristic(ServiceUUID.DIS_SERVICE_UUID, CharacteristicUUID.MODEL_NUMBER_CHARACTERISTIC_UUID);

            // Try to turn on notifications for other characteristics
            CharacteristicUUID.ENABLED_NOTIFICATIONS.forEach(uuid -> {
                peripheral.setNotify(ServiceUUID.PUMP_SERVICE_UUID, uuid, true);
            });

            Timber.i("Connected to pump");

            Intent intent = new Intent(PUMP_CONNECTED_STAGE1_INTENT);
            intent.putExtra("address", peripheral.getAddress());
            intent.putExtra("name", peripheral.getName());
            context.sendBroadcast(intent);
        }

        @Override
        public void onNotificationStateUpdate(@NotNull BluetoothPeripheral peripheral, @NotNull BluetoothGattCharacteristic characteristic, @NotNull GattStatus status) {
            if (status == GattStatus.SUCCESS) {
                final boolean isNotifying = peripheral.isNotifying(characteristic);
                Timber.i("SUCCESS: Notify set to '%s' for %s", isNotifying, characteristic.getUuid());

            } else {
                Timber.e("ERROR: Changing notification state failed for %s (%s)", characteristic.getUuid(), status);
            }
        }

        @Override
        public void onCharacteristicWrite(@NotNull BluetoothPeripheral peripheral, @NotNull byte[] value, @NotNull BluetoothGattCharacteristic characteristic, @NotNull GattStatus status) {
            if (status == GattStatus.SUCCESS) {
                Timber.i("SUCCESS: Writing <%s> to <%s>", bytes2String(value), characteristic.getUuid());
            } else {
                Timber.e("ERROR: Failed writing <%s> to <%s> (%s)", bytes2String(value), characteristic.getUuid(), status);
            }
        }

        @Override
        public void onCharacteristicUpdate(@NotNull BluetoothPeripheral peripheral, @NotNull byte[] value, @NotNull BluetoothGattCharacteristic characteristic, @NotNull GattStatus status) {
            if (status != GattStatus.SUCCESS) return;

            UUID characteristicUUID = characteristic.getUuid();
            BluetoothBytesParser parser = new BluetoothBytesParser(value);

            if (characteristicUUID.equals(CharacteristicUUID.MANUFACTURER_NAME_CHARACTERISTIC_UUID)) {
                String manufacturer = parser.getStringValue(0);
                Timber.i("Received manufacturer: %s", manufacturer);
            } else if (characteristicUUID.equals(CharacteristicUUID.MODEL_NUMBER_CHARACTERISTIC_UUID)) {
                String modelNumber = parser.getStringValue(0);
                Timber.i("Received modelnumber: %s", modelNumber);
            } else if (characteristicUUID.equals(CharacteristicUUID.AUTHORIZATION_CHARACTERISTICS) ||
                    characteristicUUID.equals(CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS) ||
                    characteristicUUID.equals(CharacteristicUUID.HISTORY_LOG_CHARACTERISTICS))
            {
                if (characteristicUUID.equals(CharacteristicUUID.AUTHORIZATION_CHARACTERISTICS)) {
                    Timber.i("Received response with PUMP_AUTH_CHARACTERISTIC: %s", Hex.encodeHexString(parser.getValue()));
                } else if (characteristicUUID.equals(CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS)) {
                    Timber.i("Received response with CURRENT_STATUS_CHARACTERISTIC: %s", Hex.encodeHexString(parser.getValue()));
                } else if (characteristicUUID.equals(CharacteristicUUID.HISTORY_LOG_CHARACTERISTICS)) {
                    Timber.i("Received response with HISTORY_LOG_CHARACTERISTIC: %s", Hex.encodeHexString(parser.getValue()));
                }

                // Parse
                Pair<Message, Byte> pair = PumpState.popRequestMessage();
                Message requestMessage = new UndefinedRequest();
                Byte txId = (byte) 0;
                if (pair != null) {
                    requestMessage = pair.first;
                    txId = pair.second;
                } else if (!characteristicUUID.equals(CharacteristicUUID.HISTORY_LOG_CHARACTERISTICS)) {
                    Timber.e("There was no request message to pop from PumpState");
                }

                TronMessageWrapper wrapper = new TronMessageWrapper(requestMessage, txId);
                PumpResponseMessageEvent response = BTResponseParser.parse(wrapper, parser.getValue(), MessageType.RESPONSE, CharacteristicUUID.AUTHORIZATION_CHARACTERISTICS);

                Timber.i("Parsed response for %s: %s", Hex.encodeHexString(parser.getValue()), response.message());

                if (response.message().isPresent() && response.message().get() instanceof CentralChallengeResponse) {
                    CentralChallengeResponse resp = (CentralChallengeResponse) response.message().get();

                    // checkHmac(authKey, centralChallenge we sent, new byte[0])
                    // doHmacSha1(10 bytes from central challenge request, bytes from authKey/pairing code) == 2-22 of response
                    Intent intent = new Intent(PUMP_CONNECTED_STAGE3_INTENT);
                    intent.putExtra("address", peripheral.getAddress());
                    intent.putExtra("appInstanceId", resp.getByte0short());
                    intent.putExtra("pairingCode", PumpState.getPairingCode(context));
                    intent.putExtra("hmacKey", Hex.encodeHexString(resp.getBytes22to30()));
                    context.sendBroadcast(intent);
                } else if (response.message().isPresent() && response.message().get() instanceof PumpChallengeResponse) {
                    PumpChallengeResponse resp = (PumpChallengeResponse) response.message().get();
                    if (resp.getSuccess()) {
                        Timber.i("Response was SUCCESSFUL");
                        PumpState.setSavedBluetoothMAC(context, peripheral.getAddress());

                        Intent intent = new Intent(PUMP_CONNECTED_STAGE4_INTENT);
                        intent.putExtra("address", peripheral.getAddress());
                        intent.putExtra("appInstanceId", resp.getAppInstanceId());
                        context.sendBroadcast(intent);
                    } else {
                        Timber.i("Response was UNSUCCESSFUL");

                        Intent intent = new Intent(PUMP_INVALID_CHALLENGE_INTENT);
                        intent.putExtra("address", peripheral.getAddress());
                        intent.putExtra("appInstanceId", resp.getAppInstanceId());
                        context.sendBroadcast(intent);
                    }
                } else if (response.message().isPresent() && response.message().get() instanceof ApiVersionResponse) {
                    ApiVersionResponse resp = (ApiVersionResponse) response.message().get();
                    Timber.i("Got ApiVersionRequest: %s", resp);
                    PumpState.setPumpAPIVersion(context, resp.getApiVersion());
                    Intent intent = new Intent(PUMP_CONNECTED_STAGE5_INTENT);
                    intent.putExtra("address", peripheral.getAddress());
                    context.sendBroadcast(intent);
                } else if (response.message().isPresent() && response.message().get() instanceof AlarmStatusResponse) {
                    AlarmStatusResponse resp = (AlarmStatusResponse) response.message().get();
                    Intent intent = new Intent(UPDATE_TEXT_RECEIVER);
                    intent.putExtra("text", "Alarms: "+resp.getAlarms().toString());
                    context.sendBroadcast(intent);
                } else if (response.message().isPresent() && response.message().get() instanceof AlertStatusResponse) {
                    AlertStatusResponse resp = (AlertStatusResponse) response.message().get();
                    Intent intent = new Intent(UPDATE_TEXT_RECEIVER);
                    intent.putExtra("text", "Alerts: "+resp.getAlerts().toString());
                    context.sendBroadcast(intent);
                } else if (response.message().isPresent() && response.message().get() instanceof CGMHardwareInfoResponse) {
                    CGMHardwareInfoResponse resp = (CGMHardwareInfoResponse) response.message().get();
                    Intent intent = new Intent(UPDATE_TEXT_RECEIVER);
                    intent.putExtra("text", "CGMHardware: "+resp.getHardwareInfoString());
                    context.sendBroadcast(intent);
                } else if (response.message().isPresent() && response.message().get() instanceof ControlIQIOBResponse) {
                    ControlIQIOBResponse resp = (ControlIQIOBResponse) response.message().get();
                    Intent intent = new Intent(UPDATE_TEXT_RECEIVER);
                    intent.putExtra("text", "ControlIQIOB: "+resp);
                    context.sendBroadcast(intent);
                } else if (response.message().isPresent() && response.message().get() instanceof NonControlIQIOBResponse) {
                    NonControlIQIOBResponse resp = (NonControlIQIOBResponse) response.message().get();
                    Intent intent = new Intent(UPDATE_TEXT_RECEIVER);
                    intent.putExtra("text", "NonControlIQIOB: "+resp);
                    context.sendBroadcast(intent);
                } else if (response.message().isPresent() && response.message().get() instanceof PumpFeaturesV1Response) {
                    PumpFeaturesV1Response resp = (PumpFeaturesV1Response) response.message().get();
                    Intent intent = new Intent(UPDATE_TEXT_RECEIVER);
                    intent.putExtra("text", "Features: "+resp);
                    context.sendBroadcast(intent);
                } else if (response.message().isPresent() && response.message().get() instanceof PumpGlobalsResponse) {
                    PumpGlobalsResponse resp = (PumpGlobalsResponse) response.message().get();
                    Intent intent = new Intent(UPDATE_TEXT_RECEIVER);
                    intent.putExtra("text", "Globals: "+resp);
                    context.sendBroadcast(intent);
                } else {
                    Intent intent = new Intent(UPDATE_TEXT_RECEIVER);
                    intent.putExtra("text", response.message().get().toString());
                    context.sendBroadcast(intent);
                }
            } else {
                Timber.i("Received response to UUID %s: %s", characteristicUUID, Hex.encodeHexString(parser.getValue()));
            }
        }

        @Override
        public void onMtuChanged(@NotNull BluetoothPeripheral peripheral, int mtu, @NotNull GattStatus status) {
            Timber.i("new MTU set: %d", mtu);
        }
    };

    // Callback for central
    private final BluetoothCentralManagerCallback bluetoothCentralManagerCallback = new BluetoothCentralManagerCallback() {

        @Override
        public void onConnectedPeripheral(@NotNull BluetoothPeripheral peripheral) {
            Timber.i("connected to '%s'", peripheral.getName());
        }

        @Override
        public void onConnectionFailed(@NotNull BluetoothPeripheral peripheral, final @NotNull HciStatus status) {
            Timber.e("connection '%s' failed with status %s", peripheral.getName(), status);
        }

        @Override
        public void onDisconnectedPeripheral(@NotNull final BluetoothPeripheral peripheral, final @NotNull HciStatus status) {
            Timber.i("disconnected '%s' with status %s", peripheral.getName(), status);

            // Reconnect to this device when it becomes available again
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    central.autoConnectPeripheral(peripheral, peripheralCallback);
                }
            }, 5000);
        }

        @Override
        public void onDiscoveredPeripheral(@NotNull BluetoothPeripheral peripheral, @NotNull ScanResult scanResult) {
            Timber.i("Found peripheral '%s'", peripheral.getName());

//            if (peripheral.getAddress().equals(PumpState.getSavedBluetoothMAC(context))) {
//                Timber.d("Pump has same address as saved pump (%s), auto connecting", peripheral.getAddress());
//                central.stopScan();
//                central.connectPeripheral(peripheral, peripheralCallback);
//                return;
//            }

            central.stopScan();
            central.connectPeripheral(peripheral, peripheralCallback);
        }

        @Override
        public void onBluetoothAdapterStateChanged(int state) {
            Timber.i("bluetooth adapter changed state to %d", state);
            if (state == BluetoothAdapter.STATE_ON) {
                // Bluetooth is on now, start scanning again
                // Scan for peripherals with a certain service UUIDs
                central.startPairingPopupHack();
                startScan();
            }
        }

        @Override
        public void onScanFailed(@NotNull ScanFailure scanFailure) {
            Timber.i("scanning failed with error %s", scanFailure);
        }
    };

    public static synchronized BluetoothHandler getInstance(Context context) {
        if (instance == null) {
            instance = new BluetoothHandler(context.getApplicationContext());
        }
        return instance;
    }

    private BluetoothHandler(Context context) {
        this.context = context;

        // Plant a tree
        Timber.Tree tree = Timber.Tree.class.cast(new DebugTree());
        Timber.plant(tree);

        // Create BluetoothCentral
        central = new BluetoothCentralManager(context, bluetoothCentralManagerCallback, new Handler());

        // Scan for peripherals with a certain service UUIDs
        central.startPairingPopupHack();
        startScan();
    }

    public void startScan() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Timber.i("Scanning for peripherals");
                central.scanForPeripheralsWithServices(new UUID[]{ServiceUUID.PUMP_SERVICE_UUID});

//                central.scanForPeripheralsUsingFilters(
//                        Collections.singletonList(new ScanFilter.Builder()
//                        .setDeviceName(PumpConfig.bluetoothName())
//                        .build()));

            }
        },1000);
    }
}

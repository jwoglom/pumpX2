package com.jwoglom.pumpx2.blessedexample;

import static com.welie.blessed.BluetoothBytesParser.FORMAT_UINT8;
import static com.welie.blessed.BluetoothBytesParser.bytes2String;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Pair;

import com.jwoglom.pumpx2.pump.PumpConfig;
import com.jwoglom.pumpx2.pump.PumpState;
import com.jwoglom.pumpx2.pump.bluetooth.BTResponseParser;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.bluetooth.TronMessageWrapper;
import com.jwoglom.pumpx2.pump.events.PumpResponseMessageEvent;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.Packetize;
import com.jwoglom.pumpx2.pump.messages.TransactionId;
import com.jwoglom.pumpx2.pump.messages.request.CentralChallengeRequest;
import com.jwoglom.pumpx2.pump.messages.response.AlarmStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.AlertStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.ApiVersionResponse;
import com.jwoglom.pumpx2.pump.messages.response.CentralChallengeResponse;
import com.jwoglom.pumpx2.pump.messages.response.PumpChallengeResponse;
import com.welie.blessed.BluetoothBytesParser;
import com.welie.blessed.BluetoothCentralManager;
import com.welie.blessed.BluetoothCentralManagerCallback;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.BluetoothPeripheralCallback;
import com.welie.blessed.BondState;
import com.welie.blessed.ConnectionPriority;
import com.welie.blessed.GattStatus;
import com.welie.blessed.HciStatus;
import com.welie.blessed.ScanFailure;

import org.apache.commons.codec.binary.Hex;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.util.Collections;
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



    public static final UUID PUMP_SERVICE_UUID = UUID.fromString("0000fdfb-0000-1000-8000-00805f9b34fb");
    public static final UUID PUMP_AUTHORIZATION_CHARACTERISTICS = UUID.fromString("7B83FFF9-9F77-4E5C-8064-AAE2C24838B9");
    public static final UUID PUMP_CURRENT_STATUS_CHARACTERISTICS = UUID.fromString("7B83FFF6-9F77-4E5C-8064-AAE2C24838B9");

    // UUIDs for the Device Information service (DIS)
    private static final UUID DIS_SERVICE_UUID = UUID.fromString("0000180A-0000-1000-8000-00805f9b34fb");
    private static final UUID MANUFACTURER_NAME_CHARACTERISTIC_UUID = UUID.fromString("00002A29-0000-1000-8000-00805f9b34fb");
    private static final UUID MODEL_NUMBER_CHARACTERISTIC_UUID = UUID.fromString("00002A24-0000-1000-8000-00805f9b34fb");

    // UUIDs for the Battery Service (BAS)
    private static final UUID BTS_SERVICE_UUID = UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb");
    private static final UUID BATTERY_LEVEL_CHARACTERISTIC_UUID = UUID.fromString("00002A19-0000-1000-8000-00805f9b34fb");

    // Local variables
    public BluetoothCentralManager central;
    private static BluetoothHandler instance = null;
    private final Context context;
    private final Handler handler = new Handler();
    private int currentTimeCounter = 0;

    // Callback for peripherals
    private final BluetoothPeripheralCallback peripheralCallback = new BluetoothPeripheralCallback() {
        @Override
        public void onServicesDiscovered(@NotNull BluetoothPeripheral peripheral) {
            // Request a higher MTU, iOS always asks for 185
            peripheral.requestMtu(185);

            // Request a new connection priority
            peripheral.requestConnectionPriority(ConnectionPriority.HIGH);

            // Read manufacturer and model number from the Device Information Service
            peripheral.readCharacteristic(DIS_SERVICE_UUID, MANUFACTURER_NAME_CHARACTERISTIC_UUID);
            peripheral.readCharacteristic(DIS_SERVICE_UUID, MODEL_NUMBER_CHARACTERISTIC_UUID);

            // Try to turn on notifications for other characteristics

            peripheral.setNotify(PUMP_SERVICE_UUID, PUMP_AUTHORIZATION_CHARACTERISTICS, true);
            peripheral.setNotify(PUMP_SERVICE_UUID, PUMP_CURRENT_STATUS_CHARACTERISTICS, true);

            // E/BluetoothPeripheral: writing <2000005a4a> to characteristic <7b83fff6-9f77-4e5c-8064-aae2c24838b9> failed, status 'INSUFFICIENT_AUTHORIZATION'
//            try {
//                peripheral.writeCharacteristic(PUMP_SERVICE_UUID, PUMP_CURRENT_STATUS_CHARACTERISTICS, Hex.decodeHex("2000005a4a"), WriteType.WITH_RESPONSE);
//            } catch (DecoderException e) {
//                Timber.e(e);
//                e.printStackTrace();
//            }

//            try {
//                peripheral.writeCharacteristic(PUMP_SERVICE_UUID, PUMP_AUTHORIZATION_CHARACTERISTICS, Hex.decodeHex("000010000a000016e610b112d06b1a2751"), WriteType.WITH_RESPONSE);
//            } catch (DecoderException e) {
//                Timber.e(e);
//                e.printStackTrace();
//            }


            Timber.i("Connected to pump");


            Intent intent = new Intent(PUMP_CONNECTED_STAGE1_INTENT);
            intent.putExtra("address", peripheral.getAddress());
            context.sendBroadcast(intent);
//
//            try {
//                peripheral.writeCharacteristic(BluetoothHandler.PUMP_SERVICE_UUID, BluetoothHandler.PUMP_AUTHORIZATION_CHARACTERISTICS, Hex.decodeHex("000010000a00000001020304050607361a"), WriteType.WITH_RESPONSE);
//                peripheral.writeCharacteristic(BluetoothHandler.PUMP_SERVICE_UUID, BluetoothHandler.PUMP_AUTHORIZATION_CHARACTERISTICS, Hex.decodeHex("010112011600004dc561ac26081e7afa4f374196"), WriteType.WITH_RESPONSE);
//                peripheral.writeCharacteristic(BluetoothHandler.PUMP_SERVICE_UUID, BluetoothHandler.PUMP_AUTHORIZATION_CHARACTERISTICS, Hex.decodeHex("000115687fa4cb337ac44c"), WriteType.WITH_RESPONSE);
//
//            } catch (DecoderException e) {
//                e.printStackTrace();
//            }
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
                Timber.i("ERROR: Failed writing <%s> to <%s> (%s)", bytes2String(value), characteristic.getUuid(), status);
            }
        }

        @Override
        public void onCharacteristicUpdate(@NotNull BluetoothPeripheral peripheral, @NotNull byte[] value, @NotNull BluetoothGattCharacteristic characteristic, @NotNull GattStatus status) {
            if (status != GattStatus.SUCCESS) return;

            UUID characteristicUUID = characteristic.getUuid();
            BluetoothBytesParser parser = new BluetoothBytesParser(value);

            if (characteristicUUID.equals(BATTERY_LEVEL_CHARACTERISTIC_UUID)) {
                int batteryLevel = parser.getIntValue(FORMAT_UINT8);
                Timber.i("Received battery level %d%%", batteryLevel);
            } else if (characteristicUUID.equals(MANUFACTURER_NAME_CHARACTERISTIC_UUID)) {
                String manufacturer = parser.getStringValue(0);
                Timber.i("Received manufacturer: %s", manufacturer);
            } else if (characteristicUUID.equals(MODEL_NUMBER_CHARACTERISTIC_UUID)) {
                String modelNumber = parser.getStringValue(0);
                Timber.i("Received modelnumber: %s", modelNumber);
            } else if (characteristicUUID.equals(CharacteristicUUID.AUTHORIZATION_CHARACTERISTICS) || characteristicUUID.equals(CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS)) {
                if (characteristicUUID.equals(PUMP_AUTHORIZATION_CHARACTERISTICS)) {
                    Timber.i("Received PUMP_AUTH_CHARACTERISTIC response: %s", Hex.encodeHexString(parser.getValue()));
                } else if (characteristicUUID.equals(CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS)) {
                    Timber.i("Received CURRENT_STATUS_CHARACTERISTIC response: %s", Hex.encodeHexString(parser.getValue()));
                }

                // Parse
                Pair<Message, Byte> pair = PumpState.popRequestMessage();
                Message requestMessage = pair.first;
                Byte txId = pair.second;
                TronMessageWrapper wrapper = new TronMessageWrapper(requestMessage, txId);
                PumpResponseMessageEvent response = BTResponseParser.parse(wrapper, parser.getValue(), MessageType.RESPONSE, CharacteristicUUID.AUTHORIZATION_CHARACTERISTICS);

                Timber.i("Parsed response for %s: %s", Hex.encodeHexString(parser.getValue()), response.message());

                if (response.message().isPresent() && response.message().get() instanceof CentralChallengeResponse) {
                    CentralChallengeResponse resp = (CentralChallengeResponse) response.message().get();

                    // checkHmac(authKey, centralChallenge we sent, new byte[0])
                    // doHmacSha1(10 bytes from central challenge request, bytes from authKey/pairing code) == 2-22 of response
                    //Packetize.doHmacSha1(new byte[0], pairingChars.getBytes(Charset.forName("UTF-8"))
                    //resp.

                    Intent intent = new Intent(PUMP_CONNECTED_STAGE3_INTENT);
                    intent.putExtra("address", peripheral.getAddress());
                    intent.putExtra("appInstanceId", resp.getByte0short());
                    intent.putExtra("pairingCode", PumpState.pairingCode);
                    intent.putExtra("hmacKey", Hex.encodeHexString(resp.getBytes22to30()));
                    context.sendBroadcast(intent);
                }
                else if (response.message().isPresent() && response.message().get() instanceof PumpChallengeResponse) {
                    PumpChallengeResponse resp = (PumpChallengeResponse) response.message().get();
                    if (resp.getSuccess()) {
                        Timber.i("Response was SUCCESSFUL");
                        Intent intent = new Intent(PUMP_CONNECTED_STAGE4_INTENT);
                        intent.putExtra("address", peripheral.getAddress());
                        intent.putExtra("appInstanceId", resp.getAppInstanceId());
                        context.sendBroadcast(intent);
                    }
                }
                else if (response.message().isPresent() && response.message().get() instanceof ApiVersionResponse) {
                    ApiVersionResponse resp = (ApiVersionResponse) response.message().get();
                    Timber.i("Got ApiVersionRequest: %s", resp);
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
            central.stopScan();

            if (peripheral.getName().contains("Contour") && peripheral.getBondState() == BondState.NONE) {
                // Create a bond immediately to avoid double pairing popups
                central.createBond(peripheral, peripheralCallback);
            } else {
                central.connectPeripheral(peripheral, peripheralCallback);
            }
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
                //central.scanForPeripheralsWithServices(new UUID[]{BLP_SERVICE_UUID, HTS_SERVICE_UUID, HRS_SERVICE_UUID, PLX_SERVICE_UUID, WSS_SERVICE_UUID, GLUCOSE_SERVICE_UUID});
                //central.scanForPeripherals();
                central.scanForPeripheralsUsingFilters(
                        Collections.singletonList(new ScanFilter.Builder()
                        .setDeviceName(PumpConfig.bluetoothName())
                        .build()));
                //central.scanForPeripheralsWithServices(new UUID[]{PUMP_SERVICE_UUID});
            }
        },1000);
    }
}

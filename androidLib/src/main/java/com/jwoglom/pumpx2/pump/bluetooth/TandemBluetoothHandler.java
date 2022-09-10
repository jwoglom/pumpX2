package com.jwoglom.pumpx2.pump.bluetooth;

import static com.welie.blessed.BluetoothBytesParser.bytes2String;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Handler;
import android.util.Pair;

import com.jwoglom.pumpx2.pump.PumpState;
import com.jwoglom.pumpx2.pump.TandemError;
import com.jwoglom.pumpx2.pump.messages.Packetize;
import com.jwoglom.pumpx2.pump.messages.bluetooth.BTResponseParser;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.bluetooth.ServiceUUID;
import com.jwoglom.pumpx2.pump.messages.bluetooth.TronMessageWrapper;
import com.jwoglom.pumpx2.pump.messages.bluetooth.models.PumpResponseMessage;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.models.UnexpectedOpCodeException;
import com.jwoglom.pumpx2.pump.messages.models.UnexpectedTransactionIdException;
import com.jwoglom.pumpx2.pump.messages.request.historyLog.NonexistentHistoryLogStreamRequest;
import com.jwoglom.pumpx2.pump.messages.response.authentication.CentralChallengeResponse;
import com.jwoglom.pumpx2.pump.messages.response.authentication.PumpChallengeResponse;
import com.welie.blessed.BluetoothBytesParser;
import com.welie.blessed.BluetoothCentralManager;
import com.welie.blessed.BluetoothCentralManagerCallback;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.BluetoothPeripheralCallback;
import com.welie.blessed.GattStatus;
import com.welie.blessed.HciStatus;
import com.welie.blessed.ScanFailure;

import com.jwoglom.pumpx2.shared.Hex;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import timber.log.Timber;
import com.jwoglom.pumpx2.util.timber.DebugTree;

public class TandemBluetoothHandler {

    private final Context context;
    private TandemPump tandemPump;
    private TandemBluetoothHandler(Context context, TandemPump tandemPump, boolean initializeTimber) {
        this.context = context;
        this.tandemPump = tandemPump;

        if (initializeTimber) {
            // Plant a tree
            Timber.Tree tree = Timber.Tree.class.cast(new DebugTree());
            Timber.plant(tree);
        }

        // Create BluetoothCentral
        central = new BluetoothCentralManager(context, bluetoothCentralManagerCallback, new Handler());
    }

    private TandemBluetoothHandler(Context context, TandemPump tandemPump) {
        this(context, tandemPump, true);
    }


    public BluetoothCentralManager central;
    private static TandemBluetoothHandler instance = null;
    private final Handler handler = new Handler();

    // Callback for peripherals
    private final BluetoothPeripheralCallback peripheralCallback = new BluetoothPeripheralCallback() {
        @Override
        public void onServicesDiscovered(@NotNull BluetoothPeripheral peripheral) {
            Timber.i("TandemBluetoothHandler: services discovered, updating BT state");
            // Request a higher MTU, iOS always asks for 185
//            peripheral.requestMtu(185);

            // Request a new connection priority
//            peripheral.requestConnectionPriority(ConnectionPriority.HIGH);

            Timber.i("TandemBluetoothHandler: services discovered, configuring characteristics");

            // Read manufacturer and model number from the Device Information Service
            peripheral.readCharacteristic(ServiceUUID.DIS_SERVICE_UUID, CharacteristicUUID.MANUFACTURER_NAME_CHARACTERISTIC_UUID);
            //peripheral.readCharacteristic(ServiceUUID.DIS_SERVICE_UUID, CharacteristicUUID.MODEL_NUMBER_CHARACTERISTIC_UUID);

            // Try to turn on notifications for other characteristics
            CharacteristicUUID.ENABLED_NOTIFICATIONS.forEach(uuid -> {
                peripheral.setNotify(ServiceUUID.PUMP_SERVICE_UUID, uuid, true);
            });

            Timber.i("TandemBluetoothHandler: Initial pump connection established");
            tandemPump.onInitialPumpConnection(peripheral);
        }

        @Override
        public void onNotificationStateUpdate(@NotNull BluetoothPeripheral peripheral, @NotNull BluetoothGattCharacteristic characteristic, @NotNull GattStatus status) {
            if (status == GattStatus.SUCCESS) {
                final boolean isNotifying = peripheral.isNotifying(characteristic);
                Timber.i("SUCCESS: Notify set to '%s' for %s", isNotifying, characteristic.getUuid());

            } else {
                Timber.e("ERROR: Changing notification state failed for %s (%s)", characteristic.getUuid(), status);
                tandemPump.onPumpCriticalError(peripheral,
                        TandemError.NOTIFICATION_STATE_FAILED.withExtra("characteristic: " + characteristic.getUuid() + ", status: " + status));
            }
        }

        @Override
        public void onCharacteristicWrite(@NotNull BluetoothPeripheral peripheral, @NotNull byte[] value, @NotNull BluetoothGattCharacteristic characteristic, @NotNull GattStatus status) {
            if (status == GattStatus.SUCCESS) {
                Timber.i("SUCCESS: Writing <%s> to %s", bytes2String(value), CharacteristicUUID.which(characteristic.getUuid()));
            } else {
                Timber.e("ERROR: Failed writing <%s> to %s (%s)", bytes2String(value), CharacteristicUUID.which(characteristic.getUuid()), status);
                tandemPump.onPumpCriticalError(peripheral,
                        TandemError.CHARACTERISTIC_WRITE_FAILED.withExtra("characteristic: " + characteristic.getUuid() + ", value: " + Hex.encodeHexString(value) + ", status: " + status));
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
                    characteristicUUID.equals(CharacteristicUUID.HISTORY_LOG_CHARACTERISTICS) ||
                    characteristicUUID.equals(CharacteristicUUID.CONTROL_CHARACTERISTICS))
            {
                String uuidName = CharacteristicUUID.which(characteristicUUID);
                Timber.i("Received response with %s: %s", uuidName, Hex.encodeHexString(parser.getValue()));

                // Parse
                Pair<Message, Byte> pair = null;
                if (!characteristicUUID.equals(CharacteristicUUID.HISTORY_LOG_CHARACTERISTICS)) {
                    pair = PumpState.peekRequestMessage();
                }
                Message requestMessage = new NonexistentHistoryLogStreamRequest();
                Byte txId = (byte) 0;
                if (pair != null) {
                    requestMessage = pair.first;
                    txId = pair.second;
                } else if (!characteristicUUID.equals(CharacteristicUUID.HISTORY_LOG_CHARACTERISTICS)) {
                    Timber.e("There was no request message to pop from PumpState");
                }

                TronMessageWrapper wrapper = new TronMessageWrapper(requestMessage, txId);
                PumpResponseMessage response;
                try {
                    response = BTResponseParser.parse(wrapper, parser.getValue(), MessageType.RESPONSE, characteristicUUID);
                } catch (UnexpectedTransactionIdException e) {
                    Timber.w(e, "Unexpected transaction id in '%s'", Hex.encodeHexString(parser.getValue()));
                    if (txId == 0 && e.foundTxId > 0) {
                        Timber.i("Ignoring txId %d since current txId is 0", e.foundTxId);
                        return;
//                        Timber.i("Setting txId from %d to %d, since initial txId was not 0", txId, e.foundTxId);
//                        Packetize.txId.set(e.foundTxId);
//                        Timber.i("Re-evaluating message with expected txId");
//                        wrapper = new TronMessageWrapper(requestMessage, txId);
//                        response = BTResponseParser.parse(wrapper, parser.getValue(), MessageType.RESPONSE, characteristicUUID);
                    } else {
                        tandemPump.onPumpCriticalError(peripheral,
                                TandemError.UNEXPECTED_TRANSACTION_ID.withExtra("found TxID: " + e.foundTxId + ", expecting: " + txId));
                        throw e;
                    }
                } catch (UnexpectedOpCodeException e) {
                    Timber.i("Unexpected opcode %d, expected %d: ignoring queue", e.foundOpcode, requestMessage.getResponseOpCode());
                    Timber.i("Ignoring message %s", Hex.encodeHexString(parser.getValue()));
                    if (pair!= null) {
                        Timber.i("Re-sending last message in queue: %s", requestMessage);
                        PumpState.popRequestMessage();
                        PumpState.pushRequestMessage(requestMessage, txId);
                    }
                    return;
                } catch (Exception e) {
                    Timber.e(e, "Unable to parse pump response message '%s'", Hex.encodeHexString(parser.getValue()));
                    throw e;
                }

                Timber.i("Parsed response for %s: %s", Hex.encodeHexString(parser.getValue()), response.message());

                if (response.message().isPresent()) {
                    PumpState.popRequestMessage();
                } else {
                    Timber.i("Since we couldn't process the message, re-sending message");
                    Packetize.txId.set((int) txId);
                    tandemPump.sendCommand(peripheral, requestMessage);
                    return;
                }

                if (response.message().isPresent() && response.message().get() instanceof CentralChallengeResponse) {
                    CentralChallengeResponse resp = (CentralChallengeResponse) response.message().get();
                    tandemPump.onWaitingForPairingCode(peripheral, resp);
                } else if (response.message().isPresent() && response.message().get() instanceof PumpChallengeResponse) {
                    PumpChallengeResponse resp = (PumpChallengeResponse) response.message().get();
                    if (resp.getSuccess()) {
                        Timber.i("Response was SUCCESSFUL");
                        PumpState.setSavedBluetoothMAC(context, peripheral.getAddress());
                        tandemPump.onPumpConnected(peripheral);
                    } else {
                        Timber.i("Response was UNSUCCESSFUL");
                        tandemPump.onInvalidPairingCode(peripheral, resp);
                    }
                } else if (response.message().isPresent()) {
                    tandemPump.onReceiveMessage(peripheral, response.message().get());
                }
            } else {
                Timber.i("Received response to UUID %s: %s", characteristicUUID, Hex.encodeHexString(parser.getValue()));
            }
        }

        @Override
        public void onMtuChanged(@NotNull BluetoothPeripheral peripheral, int mtu, @NotNull GattStatus status) {
            Timber.i("new MTU set: %d", mtu);
        }

        @Override
        public void onConnectionUpdated(@NotNull BluetoothPeripheral peripheral, int interval, int latency, int timeout, @NotNull GattStatus status) {
            if (status != GattStatus.SUCCESS) {
                Timber.e("onConnectionUpdated %s", status);
                tandemPump.onPumpCriticalError(peripheral,
                        TandemError.CONNECTION_UPDATE_FAILED.withExtra("status: " + status));
            } else {
                Timber.i("onConnectionUpdated %s", status);
            }
        }
    };

    // Callback for central
    private final BluetoothCentralManagerCallback bluetoothCentralManagerCallback = new BluetoothCentralManagerCallback() {

        @Override
        public void onConnectedPeripheral(@NotNull BluetoothPeripheral peripheral) {
            Timber.i("TandemBluetoothHandler: connected to '%s'", peripheral.getName());
        }

        @Override
        public void onConnectionFailed(@NotNull BluetoothPeripheral peripheral, final @NotNull HciStatus status) {
            Timber.e("TandemBluetoothHandler: connection '%s' failed with status %s", peripheral.getName(), status);

            tandemPump.onPumpCriticalError(peripheral,
                    TandemError.BT_CONNECTION_FAILED.withExtra("status: " + status));
        }

        @Override
        public void onDisconnectedPeripheral(@NotNull final BluetoothPeripheral peripheral, final @NotNull HciStatus status) {
            Timber.i("TandemBluetoothHandler: disconnected '%s' with status %s", peripheral.getName(), status);
            if (tandemPump.onPumpDisconnected(peripheral, status)) {
                // Reconnect to this device when it becomes available again
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        central.autoConnectPeripheral(peripheral, peripheralCallback);
                    }
                }, 5000);
            }
        }

        @Override
        public void onDiscoveredPeripheral(@NotNull BluetoothPeripheral peripheral, @NotNull ScanResult scanResult) {
            Timber.i("TandemBluetoothHandler: Discovered peripheral '%s'", peripheral.getName());

//            if (peripheral.getAddress().equals(PumpState.getSavedBluetoothMAC(context))) {
//                Timber.d("Pump has same address as saved pump (%s), auto connecting", peripheral.getAddress());
//                central.stopScan();
//                central.connectPeripheral(peripheral, peripheralCallback);
//                return;
//            }

            if (tandemPump.onPumpDiscovered(peripheral, scanResult)) {
                Timber.i("TandemBluetoothHandler: stopping scan in preparation for pump peripheral connection");
                central.stopScan();
                Timber.i("TandemBluetoothHandler: connecting to pump " + peripheral.getName() + " ( " + peripheral.getAddress() + ")");
                central.connectPeripheral(peripheral, peripheralCallback);
            }
        }

        @Override
        public void onBluetoothAdapterStateChanged(int state) {
            Timber.d("TandemBluetoothHandler: bluetooth adapter changed state to %d", state);
            if (state == BluetoothAdapter.STATE_ON) {
                // Bluetooth is on now, start scanning again
                // Scan for peripherals with a certain service UUIDs
                startScan();
            }
        }

        @Override
        public void onScanFailed(@NotNull ScanFailure scanFailure) {
            Timber.i("TandemBluetoothHandler: scanning failed with error %s", scanFailure);
        }
    };

    public static synchronized TandemBluetoothHandler getInstance(Context context, TandemPump tandemPump) {
        if (instance == null) {
            instance = new TandemBluetoothHandler(context.getApplicationContext(), tandemPump);
        }
        return instance;
    }

    public void startScan() {
        Timber.i("TandemBluetoothHandler: startScan");
        // Scan for peripherals with a certain service UUIDs
        central.startPairingPopupHack();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (tandemPump.filterToBluetoothMac.isPresent()) {
                    String macAddress = tandemPump.filterToBluetoothMac.get();
                    Timber.i("TandemBluetoothHandler: Scanning for Tandem peripheral with MAC: %s", macAddress);
                    central.scanForPeripheralsWithAddresses(new String[]{macAddress});
                } else {
                    Timber.i("TandemBluetoothHandler: Scanning for all Tandem peripherals");
                    central.scanForPeripheralsWithServices(new UUID[]{ServiceUUID.PUMP_SERVICE_UUID});
                }
            }
        },1000);
    }

    public void stop() {
        central.stopScan();
        central.close();
    }
}

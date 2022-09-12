package com.jwoglom.pumpx2.pump.bluetooth;

import static com.welie.blessed.BluetoothBytesParser.bytes2String;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Handler;

import com.google.common.collect.ImmutableList;
import com.jwoglom.pumpx2.pump.PumpState;
import com.jwoglom.pumpx2.pump.TandemError;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.BTResponseParser;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.bluetooth.ServiceUUID;
import com.jwoglom.pumpx2.pump.messages.bluetooth.TronMessageWrapper;
import com.jwoglom.pumpx2.pump.messages.bluetooth.models.PumpResponseMessage;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.models.UnexpectedOpCodeException;
import com.jwoglom.pumpx2.pump.messages.models.UnexpectedTransactionIdException;
import com.jwoglom.pumpx2.pump.messages.request.controlStream.NonexistentPumpingStateStreamRequest;
import com.jwoglom.pumpx2.pump.messages.request.historyLog.NonexistentHistoryLogStreamRequest;
import com.jwoglom.pumpx2.pump.messages.response.authentication.CentralChallengeResponse;
import com.jwoglom.pumpx2.pump.messages.response.authentication.PumpChallengeResponse;
import com.jwoglom.pumpx2.pump.messages.response.controlStream.ControlStreamMessages;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ApiVersionResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.TimeSinceResetResponse;
import com.jwoglom.pumpx2.pump.messages.response.qualifyingEvent.QualifyingEvent;
import com.welie.blessed.BluetoothBytesParser;
import com.welie.blessed.BluetoothCentralManager;
import com.welie.blessed.BluetoothCentralManagerCallback;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.BluetoothPeripheralCallback;
import com.welie.blessed.ConnectionPriority;
import com.welie.blessed.GattStatus;
import com.welie.blessed.HciStatus;
import com.welie.blessed.ScanFailure;

import com.jwoglom.pumpx2.shared.Hex;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteOrder;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
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
        resetRemainingConnectionInitializationSteps();
    }

    private TandemBluetoothHandler(Context context, TandemPump tandemPump) {
        this(context, tandemPump, true);
    }


    public BluetoothCentralManager central;
    private static TandemBluetoothHandler instance = null;
    private final Handler handler = new Handler();

    private enum ConnectionInitializationStep {
        SERVICES_DISCOVERED,
        MTU_UPDATED,
        CONNECTION_UPDATED,
        CHARACTERISTIC_NOTIFICATIONS,

        ALREADY_INITIALIZED,
    }

    private final Set<ConnectionInitializationStep> remainingConnectionInitializationSteps = new HashSet<>();
    private final Set<UUID> remainingCharacteristicNotificationsInit = new HashSet<>();
    private synchronized void resetRemainingConnectionInitializationSteps() {
        remainingConnectionInitializationSteps.addAll(ImmutableList.copyOf(ConnectionInitializationStep.values()));
        remainingConnectionInitializationSteps.remove(ConnectionInitializationStep.ALREADY_INITIALIZED);
        remainingCharacteristicNotificationsInit.addAll(CharacteristicUUID.ENABLED_NOTIFICATIONS);
    }

    // Callback for peripherals
    private final BluetoothPeripheralCallback peripheralCallback = new BluetoothPeripheralCallback() {
        @Override
        public void onServicesDiscovered(@NotNull BluetoothPeripheral peripheral) {
            Timber.i("TandemBluetoothHandler: services discovered, updating BT state");
            // Request a higher MTU, iOS always asks for 185
            // NOTE: If this is removed or lowered, then pump request messages cannot be over a certain length
            // or they may not be able to be received. More pump response messages will also split over multiple packets.
            // If setting the MTU returns an error, the pump is likely not accepting connections and needs to be open
            // to the pairing menu.
            peripheral.requestMtu(185);

            // Request a new connection priority
            peripheral.requestConnectionPriority(ConnectionPriority.HIGH);

            Timber.i("TandemBluetoothHandler: services discovered, configuring characteristics");

            // Read manufacturer and model number from the Device Information Service
            peripheral.readCharacteristic(ServiceUUID.DIS_SERVICE_UUID, CharacteristicUUID.MANUFACTURER_NAME_CHARACTERISTIC_UUID);
            peripheral.readCharacteristic(ServiceUUID.DIS_SERVICE_UUID, CharacteristicUUID.MODEL_NUMBER_CHARACTERISTIC_UUID);

            // Try to turn on notifications for other characteristics
            CharacteristicUUID.ENABLED_NOTIFICATIONS.forEach(uuid -> {
                peripheral.setNotify(ServiceUUID.PUMP_SERVICE_UUID, uuid, true);
            });

            Timber.i("TandemBluetoothHandler: waiting for Bluetooth initialization callback");
            remainingConnectionInitializationSteps.remove(ConnectionInitializationStep.SERVICES_DISCOVERED);
            checkIfInitialPumpConnectionEstablished(peripheral);
        }

        private synchronized void checkIfInitialPumpConnectionEstablished(BluetoothPeripheral peripheral) {
            if (remainingConnectionInitializationSteps.isEmpty()) {
                Timber.i("TandemBluetoothHandler: initial pump connection established");
                tandemPump.onInitialPumpConnection(peripheral);
                remainingConnectionInitializationSteps.add(ConnectionInitializationStep.ALREADY_INITIALIZED);
            } else if (!remainingConnectionInitializationSteps.contains(ConnectionInitializationStep.ALREADY_INITIALIZED)) {
                Timber.i("TandemBluetoothHandler: initial pump connection is waiting for: %s", remainingConnectionInitializationSteps);
            }
        }

        @Override
        public void onNotificationStateUpdate(@NotNull BluetoothPeripheral peripheral, @NotNull BluetoothGattCharacteristic characteristic, @NotNull GattStatus status) {
            if (status == GattStatus.SUCCESS) {
                final boolean isNotifying = peripheral.isNotifying(characteristic);
                Timber.i("SUCCESS: Notify set to '%s' for %s (%s)", isNotifying, characteristic.getUuid(), CharacteristicUUID.which(characteristic.getUuid()));

                synchronized (remainingCharacteristicNotificationsInit) {
                    remainingCharacteristicNotificationsInit.remove(characteristic.getUuid());
                    if (remainingCharacteristicNotificationsInit.isEmpty()) {
                        remainingConnectionInitializationSteps.remove(ConnectionInitializationStep.CHARACTERISTIC_NOTIFICATIONS);
                    }
                }
                checkIfInitialPumpConnectionEstablished(peripheral);

            } else {
                Timber.e("ERROR: Changing notification state failed for %s (%s)", CharacteristicUUID.which(characteristic.getUuid()), status);
                TandemError error = TandemError.NOTIFICATION_STATE_FAILED.withExtra("characteristic: " + CharacteristicUUID.which(characteristic.getUuid()) + ", status: " + status);
                if (CharacteristicUUID.QUALIFYING_EVENTS_CHARACTERISTICS.equals(characteristic.getUuid())) {
                    error = TandemError.PAIRING_CANNOT_BEGIN.withCause(error);
                }
                tandemPump.onPumpCriticalError(peripheral, error);
            }
        }

        @Override
        public void onCharacteristicWrite(@NotNull BluetoothPeripheral peripheral, @NotNull byte[] value, @NotNull BluetoothGattCharacteristic characteristic, @NotNull GattStatus status) {
            if (status == GattStatus.SUCCESS) {
                Timber.i("SUCCESS: Writing <%s> to %s", bytes2String(value), CharacteristicUUID.which(characteristic.getUuid()));
            } else {
                Timber.e("ERROR: Failed writing <%s> to %s (%s)", bytes2String(value), CharacteristicUUID.which(characteristic.getUuid()), status);
                tandemPump.onPumpCriticalError(peripheral,
                        TandemError.CHARACTERISTIC_WRITE_FAILED.withExtra("characteristic: " + CharacteristicUUID.which(characteristic.getUuid()) + ", value: " + Hex.encodeHexString(value) + ", status: " + status));
            }
        }

        @Override
        public void onCharacteristicUpdate(@NotNull BluetoothPeripheral peripheral, @NotNull byte[] value, @NotNull BluetoothGattCharacteristic btCharacteristic, @NotNull GattStatus status) {
            if (status != GattStatus.SUCCESS) return;

            UUID characteristicUUID = btCharacteristic.getUuid();
            BluetoothBytesParser parser = new BluetoothBytesParser(value);

            if (characteristicUUID.equals(CharacteristicUUID.MANUFACTURER_NAME_CHARACTERISTIC_UUID)) {
                String manufacturer = parser.getStringValue(0);
                Timber.i("Received manufacturer: %s", manufacturer);
            } else if (characteristicUUID.equals(CharacteristicUUID.MODEL_NUMBER_CHARACTERISTIC_UUID)) {
                String modelNumber = parser.getStringValue(0);
                Timber.i("Received modelNumber: %s", modelNumber);
                tandemPump.onPumpModel(peripheral, modelNumber);
            } else if (characteristicUUID.equals(CharacteristicUUID.QUALIFYING_EVENTS_CHARACTERISTICS)) {
                // little-endian uint32: `struct.unpack("<I", bytes.fromhex("..."))`
                // Integer eventType = parser.getIntValue(20, ByteOrder.LITTLE_ENDIAN);
                tandemPump.onReceiveQualifyingEvent(peripheral, QualifyingEvent.fromRawBtBytes(value));
            } else if (characteristicUUID.equals(CharacteristicUUID.AUTHORIZATION_CHARACTERISTICS) ||
                    characteristicUUID.equals(CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS) ||
                    characteristicUUID.equals(CharacteristicUUID.HISTORY_LOG_CHARACTERISTICS) ||
                    characteristicUUID.equals(CharacteristicUUID.CONTROL_CHARACTERISTICS) ||
                    characteristicUUID.equals(CharacteristicUUID.CONTROL_STREAM_CHARACTERISTICS))
            {
                String uuidName = CharacteristicUUID.which(characteristicUUID);
                Characteristic characteristic = Characteristic.of(characteristicUUID);

                Byte txId = BTResponseParser.parseTxId(value);
                Timber.i("Received response with %s and txId %d: %s", uuidName, txId, Hex.encodeHexString(parser.getValue()));


                Message requestMessage = null;
                if (characteristicUUID.equals(CharacteristicUUID.HISTORY_LOG_CHARACTERISTICS)) {
                    requestMessage = new NonexistentHistoryLogStreamRequest();
                } else if (characteristicUUID.equals(CharacteristicUUID.CONTROL_STREAM_CHARACTERISTICS)) {
                    try {
                        requestMessage = ControlStreamMessages.determineRequestMessage(value);
                    } catch (InstantiationException | IllegalAccessException e) {
                        Timber.e("Could not handle control stream message: '%s'", Hex.encodeHexString(value));
                        Timber.e(e);
                        return;
                    }
                } else {
                    Optional<Message> opt = PumpState.readRequestMessage(characteristic, txId);
                    if (opt.isPresent()) {
                        requestMessage = opt.get();
                    } else {
                        Timber.e("There was no request message to pop from PumpState (characteristic: %s, txId: %d)", characteristic, txId);
                        return;
                    }
                }

                TronMessageWrapper wrapper = new TronMessageWrapper(requestMessage, txId);
                // For multi-packet messages, use the saved packet array list so we have existing state of the previous BT packets.
                PacketArrayList packetArrayList = PumpState.checkForSavedPacketArrayList(characteristic, txId)
                        .orElse(wrapper.buildPacketArrayList(MessageType.RESPONSE));
                PumpResponseMessage response;
                try {
                    response = BTResponseParser.parse(wrapper.message(), packetArrayList, parser.getValue(), characteristicUUID);
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
                    throw e;
                } catch (Exception e) {
                    Timber.e(e, "Unable to parse pump response message '%s'", Hex.encodeHexString(parser.getValue()));
                    throw e;
                }

                Timber.i("Parsed response for %s: %s", Hex.encodeHexString(parser.getValue()), response.message());

                if (response.message().isPresent()) {
                    if (!characteristicUUID.equals(CharacteristicUUID.HISTORY_LOG_CHARACTERISTICS) &&
                        !characteristicUUID.equals(CharacteristicUUID.CONTROL_STREAM_CHARACTERISTICS))
                    {
                        PumpState.finishRequestMessage(characteristic, txId);
                    }
                } else {
                    Timber.w("Couldn't process the response message for '%s' -- we likely need more packets. This should resolve when fetching the next BT packet.", Hex.encodeHexString(parser.getValue()));
                    PumpState.savePacketArrayList(characteristic, txId, packetArrayList);
                    return;
                }

                Message msg = response.message().get();

                if (msg instanceof CentralChallengeResponse) {
                    CentralChallengeResponse resp = (CentralChallengeResponse) response.message().get();
                    tandemPump.onWaitingForPairingCode(peripheral, resp);
                } else if (msg instanceof PumpChallengeResponse) {
                    PumpChallengeResponse resp = (PumpChallengeResponse) response.message().get();
                    if (resp.getSuccess()) {
                        Timber.i("Response was SUCCESSFUL");
                        PumpState.setSavedBluetoothMAC(context, peripheral.getAddress());
                        tandemPump.onPumpConnected(peripheral);
                    } else {
                        Timber.i("Response was UNSUCCESSFUL");
                        tandemPump.onInvalidPairingCode(peripheral, resp);
                    }
                } else {
                    if (msg instanceof ApiVersionResponse) {
                        PumpState.setPumpAPIVersion(((ApiVersionResponse) msg).getApiVersion());
                    } else if (msg instanceof TimeSinceResetResponse) {
                        PumpState.setPumpTimeSinceReset(((TimeSinceResetResponse) msg).getTimeSinceReset());
                    }
                    tandemPump.onReceiveMessage(peripheral, msg);
                }
            } else {
                Timber.i("Received response to UUID %s: %s", characteristicUUID, Hex.encodeHexString(parser.getValue()));
            }
        }

        @Override
        public void onMtuChanged(@NotNull BluetoothPeripheral peripheral, int mtu, @NotNull GattStatus status) {
            Timber.i("new MTU set: %d", mtu);
            remainingConnectionInitializationSteps.remove(ConnectionInitializationStep.MTU_UPDATED);
            checkIfInitialPumpConnectionEstablished(peripheral);
        }

        @Override
        public void onConnectionUpdated(@NotNull BluetoothPeripheral peripheral, int interval, int latency, int timeout, @NotNull GattStatus status) {
            if (status == GattStatus.SUCCESS) {
                Timber.i("onConnectionUpdated %s", status);

                remainingConnectionInitializationSteps.remove(ConnectionInitializationStep.CONNECTION_UPDATED);
                checkIfInitialPumpConnectionEstablished(peripheral);
            } else {
                Timber.e("onConnectionUpdated %s", status);
                TandemError error = TandemError.CONNECTION_UPDATE_FAILED.withExtra("status: " + status);
                if (status == GattStatus.VALUE_NOT_ALLOWED) {
                    error = TandemError.PAIRING_CANNOT_BEGIN.withCause(error);
                }
                tandemPump.onPumpCriticalError(peripheral, error);
            }
        }
    };

    // Callback for central
    private final BluetoothCentralManagerCallback bluetoothCentralManagerCallback = new BluetoothCentralManagerCallback() {

        @Override
        public void onConnectedPeripheral(@NotNull BluetoothPeripheral peripheral) {
            Timber.i("TandemBluetoothHandler: connected to '%s'", peripheral.getName());
            this.reconnectDelay = 5000;
        }

        @Override
        public void onConnectionFailed(@NotNull BluetoothPeripheral peripheral, final @NotNull HciStatus status) {
            Timber.e("TandemBluetoothHandler: connection '%s' failed with status %s", peripheral.getName(), status);

            tandemPump.onPumpCriticalError(peripheral,
                    TandemError.BT_CONNECTION_FAILED.withExtra("status: " + status));
        }

        private int reconnectDelay = 2500;
        @Override
        public void onDisconnectedPeripheral(@NotNull final BluetoothPeripheral peripheral, final @NotNull HciStatus status) {
            Timber.i("TandemBluetoothHandler: disconnected '%s' with status %s (reconnectDelay: %d ms)", peripheral.getName(), status, reconnectDelay);
            resetRemainingConnectionInitializationSteps();
            if (tandemPump.onPumpDisconnected(peripheral, status)) {
                // Reconnect to this device when it becomes available again
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        central.autoConnectPeripheral(peripheral, peripheralCallback);
                    }
                }, reconnectDelay);
                reconnectDelay *= 1.5;
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

package com.jwoglom.pumpx2.pump.bluetooth;

import static com.welie.blessed.BluetoothBytesParser.asHexString;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.jwoglom.pumpx2.pump.PumpState;
import com.jwoglom.pumpx2.pump.TandemError;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.Packetize;
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
import com.jwoglom.pumpx2.pump.messages.request.historyLog.NonexistentHistoryLogStreamRequest;
import com.jwoglom.pumpx2.pump.messages.response.ErrorResponse;
import com.jwoglom.pumpx2.pump.messages.response.authentication.CentralChallengeResponse;
import com.jwoglom.pumpx2.pump.messages.response.authentication.PumpChallengeResponse;
import com.jwoglom.pumpx2.pump.messages.response.controlStream.ControlStreamMessages;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ApiVersionResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.TimeSinceResetResponse;
import com.jwoglom.pumpx2.pump.messages.response.qualifyingEvent.QualifyingEvent;
import com.jwoglom.pumpx2.util.timber.LConfigurator;
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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import timber.log.Timber;

import com.jwoglom.pumpx2.util.timber.DebugTree;

/**
 * Handles the Bluetooth connection to a Tandem pump.
 * The back-of-house which allows {@link TandemPump}, the client-accessible frontend, to work.
 */
public class TandemBluetoothHandler {
    private final Context context;
    private TandemPump tandemPump;

    /**
     * Initializes PumpX2.
     *
     * @param context    Android context
     * @param tandemPump an instantiated version of your class which extends {@class TandemPump}
     * @param timberTree the {@link Timber.Tree} which is initialized for logging with Timber.
     *                   Timber initialization is skipped if null. See {@link LConfigurator}
     */
    public TandemBluetoothHandler(Context context, TandemPump tandemPump, @Nullable Timber.Tree timberTree) {
        this.context = context;
        this.tandemPump = tandemPump;

        if (timberTree != null) {
            // Plant a tree
            Timber.plant(timberTree);
            LConfigurator.enableTimber();
        } else {
            Timber.i("Skipped Timber tree initialization");
        }

        // Create BluetoothCentral
        central = new BluetoothCentralManager(context, bluetoothCentralManagerCallback, new Handler());
        resetRemainingConnectionInitializationSteps();
    }

    /**
     * Initializes PumpX2.
     *
     * @param context    Android context
     * @param tandemPump an instantiated version of your class which extends {@class TandemPump}
     */
    public TandemBluetoothHandler(Context context, TandemPump tandemPump) {
        this(context, tandemPump, Timber.Tree.class.cast(new DebugTree()));
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

    // Callback for peripheral-specific events
    private final BluetoothPeripheralCallback peripheralCallback = new BluetoothPeripheralCallback() {
        @Override
        public void onServicesDiscovered(@NotNull BluetoothPeripheral peripheral) {
            Timber.i("TandemBluetoothHandler: services discovered, updating BT state");
            // Request a higher MTU, iOS always asks for 185
            // NOTE: If this is removed or lowered, then pump request messages cannot be over a certain length
            // or they may not be able to be received. More pump response messages will also split over multiple packets.
            // If setting the MTU returns an error, the pump is likely not accepting connections and needs to be open
            // to the pairing menu.
            // TODO: the t:connect Android app sets a higher MTU of 240 on the connection
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

                remainingConnectionInitializationSteps.add(ConnectionInitializationStep.ALREADY_INITIALIZED);
                if (PumpState.tconnectAppAlreadyAuthenticated) {
                    Timber.i("TandemPump: tconnect app has likely already authenticated. Skipping onInitialPumpConnection callback and triggering onPumpConnected");
                    // IMPORTANT: the tconnect app has Bluetooth callbacks too! wait to avoid race conditions on the transaction ID
                    // since we'll immediately trigger api version / timesincereset.
                    PumpState.tconnectAppConnectionSharingIgnoreInitialFailingWrite = true;
                    AtomicBoolean sentOnPumpConnected = new AtomicBoolean(false);
                    // If at 500ms, 1000ms, or 1500ms the t:connect app has sent a message and we've
                    // received the response, then we've been able to sync the current opcode and
                    // can thus call the onPumpConnected callback.
                    for (int i = 500; i < 2000; i += 500) {
                        final int ii = i;
                        handler.postDelayed(() -> {
                            if (PumpState.processedResponseMessages > 0) {
                                if (sentOnPumpConnected.compareAndSet(false, true)) {
                                    Timber.i("AlreadyAuthenticated#%d: processed %d response messages, with set opcode, so triggering onPumpConnected", ii, PumpState.processedResponseMessages);
                                    tandemPump.onPumpConnected(peripheral);
                                } else {
                                    Timber.d("AlreadyAuthenticated#%d: sentOnPumpConnected", ii);
                                }
                            } else {
                                Timber.d("AlreadyAuthenticated#%d: 0 processed response messages", ii);
                            }
                        }, i);
                    }

                    // If the pump didn't send any messages in the first 2 seconds of connection,
                    // then trigger the onConnected callback anyway, and we will likely fail the
                    // first opcode write which we will then recover from and reconnect, allowing
                    // the first case to succeed where either the app will send some message which
                    // we'll be able to sync our opcode to, or the app won't and we'll send the first
                    // message successfully with opcode 0.
                    handler.postDelayed(() -> {
                        if (remainingConnectionInitializationSteps.contains(ConnectionInitializationStep.ALREADY_INITIALIZED)) {
                            if (sentOnPumpConnected.compareAndSet(false, true)) {
                                Timber.i("AlreadyAuthenticated#final: no response messages, but pump still initialized, so triggering onPumpConnected");
                                tandemPump.onPumpConnected(peripheral);
                            } else {
                                Timber.d("AlreadyAuthenticated#final: sentOnPumpConnected");
                            }
                        } else {
                            Timber.d("AlreadyAuthenticated#final: not already initialized: %s", remainingConnectionInitializationSteps);
                        }
                    }, 2000);
                    return;
                }

                tandemPump.onInitialPumpConnection(peripheral);
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
                Timber.i("SUCCESS: Writing <%s> to %s", asHexString(value), CharacteristicUUID.which(characteristic.getUuid()));
            } else {
                Timber.e("ERROR: Failed writing <%s> to %s (%s)", asHexString(value), CharacteristicUUID.which(characteristic.getUuid()), status);
                if (PumpState.tconnectAppConnectionSharing && CharacteristicUUID.AUTHORIZATION_CHARACTERISTICS.equals(characteristic.getUuid()) && status == GattStatus.ERROR) {
                    // When we receive an error on the authorization characteristic while t:connect app connection sharing is enabled,
                    // mark that we can ignore the authentication on the next connection since we trust the t:connect app will instead
                    Timber.i("Setting PumpState.tconnectAppAlreadyAuthenticated for retried connection (current state: " + PumpState.tconnectAppAlreadyAuthenticated + ")");
                    PumpState.tconnectAppAlreadyAuthenticated = true;
                    //
                } else if (PumpState.tconnectAppConnectionSharing && PumpState.tconnectAppConnectionSharingIgnoreInitialFailingWrite && !CharacteristicUUID.AUTHORIZATION_CHARACTERISTICS.equals(characteristic.getUuid()) && status == GattStatus.ERROR) {
                    // If we get an error on a non-authorization characteristic with t:connect app connection sharing enabled,
                    // after the authorization stage has already been skipped due to the running t:connect app,
                    // then the initial message we send will return an error because the opcode is wrong,
                    // resulting in a disconnection. This case will still trigger a disconnect but
                    // ignore the critical error popup on the first occurrence.
                    Timber.i("Ignoring popup on initial failing non-authorization write due to t:connect app connection sharing");
                    PumpState.tconnectAppConnectionSharingIgnoreInitialFailingWrite = false;
                } else {
                    Timber.d("characteristic write error. tconnectAppConnectionSharing=" + PumpState.tconnectAppConnectionSharing + " tconnectAppConnectionSharingIgnoreInitialFailingWrite=" + PumpState.tconnectAppConnectionSharingIgnoreInitialFailingWrite);
                    tandemPump.onPumpCriticalError(peripheral,
                            TandemError.CHARACTERISTIC_WRITE_FAILED.withExtra("characteristic: " + CharacteristicUUID.which(characteristic.getUuid()) + ", value: " + Hex.encodeHexString(value) + ", status: " + status));
                }
            }
        }

        @Override
        public void onCharacteristicUpdate(@NotNull BluetoothPeripheral peripheral, @NotNull byte[] value, @NotNull BluetoothGattCharacteristic btCharacteristic, @NotNull GattStatus status) {
            if (status != GattStatus.SUCCESS) {
                Timber.w("ERROR: Failed to receive update <%s> to %s (%s)", asHexString(value), CharacteristicUUID.which(btCharacteristic.getUuid()), status);
                return;
            }

            innerCharacteristicUpdate(peripheral, value, btCharacteristic, status);
        }

        private synchronized void innerCharacteristicUpdate(@NotNull BluetoothPeripheral peripheral, @NotNull byte[] value, @NotNull BluetoothGattCharacteristic btCharacteristic, @NotNull GattStatus status) {
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
                Set<QualifyingEvent> events = QualifyingEvent.fromRawBtBytes(value);
                Timber.i("QualifyingEvent response: %s", events);
                tandemPump.onReceiveQualifyingEvent(peripheral, events);
            } else if (characteristicUUID.equals(CharacteristicUUID.AUTHORIZATION_CHARACTERISTICS) ||
                    characteristicUUID.equals(CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS) ||
                    characteristicUUID.equals(CharacteristicUUID.HISTORY_LOG_CHARACTERISTICS) ||
                    characteristicUUID.equals(CharacteristicUUID.CONTROL_CHARACTERISTICS) ||
                    characteristicUUID.equals(CharacteristicUUID.CONTROL_STREAM_CHARACTERISTICS)) {
                String uuidName = CharacteristicUUID.which(characteristicUUID);
                Characteristic characteristic = Characteristic.of(characteristicUUID);

                Byte txId = BTResponseParser.parseTxId(value);
                PumpState.processedResponseMessages++;
                Timber.d("Received %s response, txId %d: %s (processedResponseMessages=%d)", uuidName, txId, Hex.encodeHexString(parser.getValue()), PumpState.processedResponseMessages);

                // Since we've already gotten at least this response, we can sync our opcodes.
                // PumpState.tconnectAppConnectionSharingIgnoreInitialFailingWrite = false;

                Message requestMessage = null;
                if (characteristicUUID.equals(CharacteristicUUID.HISTORY_LOG_CHARACTERISTICS)) {
                    requestMessage = new NonexistentHistoryLogStreamRequest();
                } else if (characteristicUUID.equals(CharacteristicUUID.CONTROL_STREAM_CHARACTERISTICS)) {
                    try {
                        requestMessage = ControlStreamMessages.determineRequestMessage(value);
                    } catch (InstantiationException | IllegalAccessException e) {
                        Timber.e(e, "Could not handle control stream message: '%s'", Hex.encodeHexString(value));
                        return;
                    }
                } else {
                    Optional<Message> opt = PumpState.readRequestMessage(characteristic, txId);
                    if (opt.isPresent()) {
                        Timber.d("requestMessage for txId=%d (sent by us) %s", txId, opt);
                        requestMessage = opt.get();
                    } else {
                        Timber.d("no requestMessage for txId=%d (sent by other client)", txId);
                        // If the initial pump connection has been established (BT notifications, MTU, etc)
                        PumpResponseMessage bestEffortParsedMsg = BTResponseParser.parseBestEffortForLogging(value, characteristicUUID);
                        if (remainingConnectionInitializationSteps.contains(ConnectionInitializationStep.ALREADY_INITIALIZED)) {
                            if (PumpState.relyOnConnectionSharingForAuthentication) {
                                Timber.d("Received first message reply to sync opcodes with relyOnConnectionSharingForAuthentication (characteristic: %s, txId: %d): %s", characteristic, txId, bestEffortParsedMsg);
                            } else if (!PumpState.tconnectAppAlreadyAuthenticated) {
                                Timber.d("Received request for message we didn't send: pump already initialized (characteristic: %s, txId: %d): %s", characteristic, txId, bestEffortParsedMsg);
                            } else {
                                Timber.d("Message likely sent by tconnect app (no request in PumpState; characteristic: %s, txId: %d): %s", characteristic, txId, bestEffortParsedMsg);
                            }

                            if (PumpState.sendSharedConnectionResponseMessages && bestEffortParsedMsg != null && bestEffortParsedMsg.message().isPresent()) {
                                tandemPump.onReceiveMessage(peripheral, bestEffortParsedMsg.message().get());
                            }
                        } else {
                            // If the initial pump connection has not been established, then this means we've received
                            // response messages to requests we did not send, so there is another app on the device
                            // active in the BT connection, most likely the official t:connect app.
                            // This check is gated behind us not having fully initialized the pump yet, because
                            // we know for certain that we didn't do anything wrong and there is definitely another
                            // actor on the connection, and we need to tell ourselves to NOT perform the authentication
                            // steps with the pairing code and etc. If we send a CentralChallenge then the pump closes
                            // the BT connection since it knows we've already authenticated. And we don't want to get
                            // into an authentication battle with the t:connect app anyway, we'll just let it handle auth.

                            Timber.d("Received request for message we didn't send: pump has not yet been initialized (characteristic: %s, txId: %d): %s", characteristic, txId, bestEffortParsedMsg);
                            if (!PumpState.tconnectAppAlreadyAuthenticated && !characteristicUUID.equals(CharacteristicUUID.AUTHORIZATION_CHARACTERISTICS)) {
                                Timber.d("Setting PumpState.tconnectAppAlreadyAuthenticated");

                                // If we are relying fully on tconnect app authentication, then we are only
                                // fully connected once we intercept the first message from their app with
                                // connection sharing enabled.
                                if (PumpState.relyOnConnectionSharingForAuthentication) {
                                    Timber.d("Scheduling delayed onPumpConnected for relyOnConnectionSharingForAuthentication");
                                    handler.postDelayed(() -> {
                                        Timber.d("Calling delayed onPumpConnected for relyOnConnectionSharingForAuthentication");
                                        tandemPump.onPumpConnected(peripheral);
                                    }, 500);
                                }
                                PumpState.tconnectAppAlreadyAuthenticated = true;
                            }

                        }

                        // It's probably important that we update the transaction ID on each message we receive.
                        // The txId we've seen was already used, and since Packetize.txId stores the next available txId,
                        // increment it by one.
                        Packetize.txId.set(1 + txId);
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
                    Timber.w(e, "Unexpected transaction id in '%s': %s", Hex.encodeHexString(parser.getValue()), BTResponseParser.parseBestEffortForLogging(value, characteristicUUID));
                    if (txId == 0 && e.foundTxId > 0) {
                        Timber.i("Ignoring txId %d since current txId is 0", e.foundTxId);
//                        Timber.i("Setting txId from %d to %d, since initial txId was not 0", txId, e.foundTxId);
//                        Packetize.txId.set(e.foundTxId);
//                        Timber.i("Re-evaluating message with expected txId");
//                        wrapper = new TronMessageWrapper(requestMessage, txId);
//                        response = BTResponseParser.parse(wrapper, parser.getValue(), MessageType.RESPONSE, characteristicUUID);
                    } else {
                        tandemPump.onPumpCriticalError(peripheral,
                                TandemError.UNEXPECTED_TRANSACTION_ID.withExtra("found TxID: " + e.foundTxId + ", expecting: " + txId));
                        Timber.e(e, "Raised UnexpectedTransactionIdException");
                    }
                    return;
                } catch (UnexpectedOpCodeException e) {
                    Timber.d("Unexpected opcode %d, expected %d for txId=%d: ignoring queue (which contained: %s): %s", e.foundOpcode, requestMessage.getResponseOpCode(), txId, requestMessage, e.toString());

                    if (PumpState.tconnectAppConnectionSharing || PumpState.tconnectAppAlreadyAuthenticated) {
                        Timber.d("Message likely sent by tconnect app (UnexpectedOpCodeException): %s", BTResponseParser.parseBestEffortForLogging(value, characteristicUUID));
                        if (Packetize.txId.get() < 1 + txId) {
                            Timber.i("updating txId from %d to %d", Packetize.txId.get(), 1 + txId);

                            // Update the transaction ID if the t:connect app is present.
                            Packetize.txId.set(1 + txId);
                        } else {
                            Timber.d("global txId=%d but self-assumed next txId+1=%d, so not incrementing", Packetize.txId.get(), 1 + txId);
                        }
                    } else {
                        // Raise the exception if we haven't detected another actor, since this is likely our fault
                        Timber.e(e, "Raised UnexpectedOpCodeException without connection sharing enabled");
                        tandemPump.onPumpCriticalError(peripheral, TandemError.UNEXPECTED_OPCODE_REPLY);
                    }

                    return;
                } catch (PacketArrayList.InvalidSignedMessageHMACSignatureException e) {
                    Timber.e(e, "Unable to parse pump response message '%s'", Hex.encodeHexString(parser.getValue()));
                    tandemPump.onPumpCriticalError(peripheral, TandemError.INVALID_SIGNED_HMAC_SIGNATURE.withExtra(
                            Strings.isNullOrEmpty(PumpState.getAuthenticationKey()) ?
                                    "pairing code not specified" : "provided pairing code is likely invalid"
                    ));
                    return;

                } catch (Exception e) {
                    Timber.e(e, "Unable to parse pump response message '%s'", Hex.encodeHexString(parser.getValue()));
                    throw e;
                }

                PumpState.processedResponseMessagesFromUs++;
                Timber.i("Processed %s response (%d): %s (%s) (%d processed total, %d from us)", characteristic, txId, response.message(), Hex.encodeHexString(parser.getValue()), PumpState.processedResponseMessages, PumpState.processedResponseMessagesFromUs);

                if (response.message().isPresent()) {
                    if (!characteristicUUID.equals(CharacteristicUUID.HISTORY_LOG_CHARACTERISTICS) &&
                            !characteristicUUID.equals(CharacteristicUUID.CONTROL_STREAM_CHARACTERISTICS)) {
                        if (response.message().get() instanceof ErrorResponse) {
                            if (!PumpState.tconnectAppConnectionSharing) {
                                tandemPump.onPumpCriticalError(peripheral, TandemError.ERROR_RESPONSE.withExtra("in response to " + requestMessage));
                            }
                            return;
                        } else {
                            PumpState.finishRequestMessage(characteristic, txId);
                        }
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
                        PumpState.setSavedBluetoothMAC(context, peripheral.getAddress());
                        tandemPump.onPumpConnected(peripheral);
                    } else {
                        Timber.w("Invalid pairing code: %s", resp);
                        tandemPump.onInvalidPairingCode(peripheral, resp);
                    }
                } else {
                    if (msg instanceof ApiVersionResponse) {
                        PumpState.setPumpAPIVersion(((ApiVersionResponse) msg).getApiVersion());
                    } else if (msg instanceof TimeSinceResetResponse) {
                        PumpState.setPumpTimeSinceReset(((TimeSinceResetResponse) msg).getCurrentTime());
                    }
                    tandemPump.onReceiveMessage(peripheral, msg);
                }
            } else {
                Timber.w("UNHANDLED RESPONSE to %s: %s", CharacteristicUUID.which(characteristicUUID), Hex.encodeHexString(parser.getValue()));
            }
        }

        @Override
        public void onMtuChanged(@NotNull BluetoothPeripheral peripheral, int mtu, @NotNull GattStatus status) {
            if (status == GattStatus.SUCCESS) {
                Timber.i("new MTU set: %d (%s)", mtu, status);
            } else if (status == GattStatus.INVALID_PDU) {
                // INVALID_PDU means that the MTU was attempted to be set more than once.
                // If we are connecting on top of the native t:connect app, which has already
                // set its MTU, then we want to ignore this error.
                Timber.i("MTU was already updated, likely by the t:connect app (is %d), ignoring error %s", mtu, status);
                if (!PumpState.tconnectAppConnectionSharing) {
                    tandemPump.onPumpCriticalError(peripheral,
                            TandemError.SHARING_CONNECTION_WITH_TCONNECT_APP
                                    .withCause(TandemError.SET_MTU_FAILED)
                                    .withExtra("mtu: " + mtu + " status: " + status));
                }
            } else {
                Timber.e("MTU could not be updated (is %d), received %s. Ignoring error.", mtu, status);

                tandemPump.onPumpCriticalError(peripheral,
                        TandemError.SET_MTU_FAILED.withExtra("mtu: " + mtu + " status: " + status));
            }
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

    // Callback for generic BT events
    private final BluetoothCentralManagerCallback bluetoothCentralManagerCallback = new BluetoothCentralManagerCallback() {

        @Override
        public void onConnectedPeripheral(@NotNull BluetoothPeripheral peripheral) {
            Timber.i("TandemBluetoothHandler: connected to '%s'", peripheral.getName());
            this.reconnectDelay = 250;
        }

        @Override
        public void onConnectionFailed(@NotNull BluetoothPeripheral peripheral, final @NotNull HciStatus status) {
            Timber.e("TandemBluetoothHandler: connection '%s' failed with status %s", peripheral.getName(), status);

            tandemPump.onPumpCriticalError(peripheral,
                    TandemError.BT_CONNECTION_FAILED.withExtra("status: " + status));
        }

        private int reconnectDelay = 250;

        @Override
        public void onDisconnectedPeripheral(@NotNull final BluetoothPeripheral peripheral, final @NotNull HciStatus status) {
            Timber.i("TandemBluetoothHandler: disconnected '%s' with status %s (reconnectDelay: %d ms)", peripheral.getName(), status, reconnectDelay);
            PumpState.clearRequestMessages();
            Packetize.txId.reset();
            resetRemainingConnectionInitializationSteps();
            if (tandemPump.onPumpDisconnected(peripheral, status)) {
                Timber.d("TandemBluetoothHandler: scheduling immediateConnectToPeripheral in %d ms", reconnectDelay);
                // Reconnect to this device when it becomes available again
                handler.postDelayed(TandemBluetoothHandler.this::immediateConnectToPeripheral, reconnectDelay);
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

    private Optional<BluetoothPeripheral> getAlreadyBondedPump() {
        // the BLESSED library doesn't include a function to get a list of all
        // bonded devices, and we want to check for a bonded Tandem device which
        // might have been done by the t:connect app itself outside of our knowledge.
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        @SuppressLint("MissingPermission")
        Set<BluetoothDevice> bondedDevices = adapter.getBondedDevices();
        for (BluetoothDevice device : bondedDevices) {
            @SuppressLint("MissingPermission")
            String name = device.getName();
            @SuppressLint("MissingPermission")
            String address = device.getAddress();
            Timber.d("TandemBluetoothHandler: bondedDevices on adapter includes: %s (%s)", name, address);
            if (!Strings.isNullOrEmpty(name) && name.startsWith("tslim X2")) {
                BluetoothPeripheral peripheral = central.getPeripheral(address);
                Timber.d("TandemBluetoothHandler: '%s' appears to be a Tandem device, returning", name);
                return Optional.of(peripheral);
            }
        }
        Timber.d("TandemBluetoothHandler: no bonded Tandem device found");
        return Optional.empty();
    }

    public static synchronized TandemBluetoothHandler getInstance(Context context, TandemPump tandemPump) {
        if (instance == null) {
            instance = new TandemBluetoothHandler(context.getApplicationContext(), tandemPump);
        }
        return instance;
    }

    public static synchronized TandemBluetoothHandler getInstance(Context context, TandemPump tandemPump, @Nullable Timber.Tree logTree) {
        if (instance == null) {
            instance = new TandemBluetoothHandler(context.getApplicationContext(), tandemPump, logTree);
        }
        return instance;
    }

    private void immediateConnectToPeripheral() {
        Timber.d("TandemBluetoothHandler: running immediateConnectToPeripheral");
        Optional<BluetoothPeripheral> alreadyBondedPump = getAlreadyBondedPump();
        if (alreadyBondedPump.isPresent()) {
            BluetoothPeripheral peripheral = alreadyBondedPump.get();
            Timber.i("TandemBluetoothHandler: Already bonded to Tandem peripheral: %s (%s)", peripheral.getName(), peripheral.getAddress());
            if (tandemPump.onPumpDiscovered(peripheral, null)) {
                central.autoConnectPeripheral(peripheral, peripheralCallback);
                return;
            } else {
                Timber.i("TandemBluetoothHandler: onPumpDiscovered callback said to skip bonded pump %s", peripheral);
            }
        }
        Timber.i("TandemBluetoothHandler: Scanning for all Tandem peripherals");
        central.scanForPeripheralsWithServices(new UUID[]{ServiceUUID.PUMP_SERVICE_UUID});
    }

    public void startScan() {
        Timber.i("TandemBluetoothHandler: startScan");
        // Scan for peripherals with a certain service UUIDs
        central.startPairingPopupHack();

        handler.postDelayed(this::immediateConnectToPeripheral, 1000);
    }

    public void stop() {
        central.stopScan();
        central.close();
    }
}

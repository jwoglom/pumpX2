package com.jwoglom.pumpx2.pump.bluetooth;

import android.bluetooth.le.ScanResult;
import android.content.Context;

import com.jwoglom.pumpx2.pump.PumpState;
import com.jwoglom.pumpx2.pump.TandemError;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.Packetize;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.bluetooth.ServiceUUID;
import com.jwoglom.pumpx2.pump.messages.bluetooth.TronMessageWrapper;
import com.jwoglom.pumpx2.pump.messages.bluetooth.models.Packet;
import com.jwoglom.pumpx2.pump.messages.builders.CentralChallengeRequestBuilder;
import com.jwoglom.pumpx2.pump.messages.builders.PumpChallengeRequestBuilder;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ApiVersionRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.TimeSinceResetRequest;
import com.jwoglom.pumpx2.pump.messages.response.authentication.CentralChallengeResponse;
import com.jwoglom.pumpx2.pump.messages.response.authentication.PumpChallengeResponse;
import com.jwoglom.pumpx2.pump.messages.response.qualifyingEvent.QualifyingEvent;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.HciStatus;
import com.welie.blessed.WriteType;

import com.jwoglom.pumpx2.shared.Hex;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import timber.log.Timber;

/**
 * An abstract class containing callback methods which are invoked when various Bluetooth events
 * occur related to the pump, as well as methods to manipulate the pump and receive responses.
 */
public abstract class TandemPump {
    public final Context context;
    final Optional<String> filterToBluetoothMac;
    public TandemPump(Context context, Optional<String> filterToBluetoothMac) {
        this.context = context;
        this.filterToBluetoothMac = filterToBluetoothMac;

        PumpState.savedAuthenticationKey = PumpState.getPairingCode(context);
    }

    public TandemPump(Context context) {
        this(context, Optional.empty());
    }

    /**
     * Callback invoked when the initial Bluetooth connection with the pump is established,
     * but before the pump authentication process begins. The default behavior is to trigger
     * a CentralChallenge which starts the authentication process.
     * @param peripheral the BluetoothPeripheral representing the connected pump
     */
    public void onInitialPumpConnection(BluetoothPeripheral peripheral) {
        Timber.i("TandemPump: onInitialPumpConnection");
        sendCommand(peripheral, CentralChallengeRequestBuilder.create(0));
    }

    /**
     * Sends a request message to the pump. Most requests have no arguments and can be initialized
     * with their no-arg constructor. The pump will send a response message to every request,
     * which you can retrieve the result of asynchronously via the onReceiveMessage() callback.
     * @param peripheral the BluetoothPeripheral representing the pump
     * @param message a request message from {@link com.jwoglom.pumpx2.pump.messages.request}
     */
    public void sendCommand(BluetoothPeripheral peripheral, Message message) {
        Timber.i("TandemPump: sendCommand(" + message + ")");
        ArrayList<byte[]> bytes = new ArrayList<>();
        byte currentTxId = Packetize.txId.get();
        PumpState.pushRequestMessage(message, currentTxId);
        TronMessageWrapper wrapper = new TronMessageWrapper(message, currentTxId);
        Packetize.txId.increment();

        for (Packet packet : wrapper.packets()) {
            bytes.add(packet.build());
        }

        for (byte[] b : bytes) {
            UUID uuid = CharacteristicUUID.determine(message);
            Timber.i("sendCommand to %s: %s", uuid, Hex.encodeHexString(b));
            peripheral.writeCharacteristic(ServiceUUID.PUMP_SERVICE_UUID,
                    uuid,
                    b,
                    WriteType.WITH_RESPONSE);
        }
    }

    /**
     * Callback invoked to notify on an invalid pump pairing code
     * @param peripheral the BluetoothPeripheral representing the pump
     * @param resp the PumpChallengeResponse which was just received from the pump
     */
    public void onInvalidPairingCode(BluetoothPeripheral peripheral, PumpChallengeResponse resp) {
        Timber.i("TandemPump: onInvalidPairingCode");
    }

    /**
     * Callback invoked when we receive the response to a command sent to the pump.
     * Note that authentication responses are not returned here, they are handled inside of
     * {@link TandemBluetoothHandler} and considered an implementation detail of the PumpX2 library.
     * @param peripheral the BluetoothPeripheral representing the pump
     * @param message the parsed message which extends the Message class. To identify what type of
     *                message was found, check with `if (message instanceof ApiVersionResponse) ..`
     */
    public abstract void onReceiveMessage(BluetoothPeripheral peripheral, Message message);

    /**
     * Callback invoked when we receive a qualifying event from the pump. When an event is received,
     * a bitmask of different parameters is received over the Bluetooth connection.
     * @param peripheral the BluetoothPeripheral representing the pump
     * @param events a set of QualifyingEvent enums representing the event(s) which occurred. Note
     *               that some of these enum values represent metadata about the event, rather than
     *               specifying that distinct events occurred.
     */
    public abstract void onReceiveQualifyingEvent(BluetoothPeripheral peripheral, Set<QualifyingEvent> events);

    /**
     * Callback invoked when a pump which we were connected to over Bluetooth is disconnected.
     * @param peripheral the BluetoothPeripheral representing the pump
     * @param status the Bluetooth reason for disconnection
     * @return true if we should attempt to re-connect to the device, false otherwise
     */
    public boolean onPumpDisconnected(BluetoothPeripheral peripheral, HciStatus status) {
        Timber.i("TandemPump: onPumpDisconnected with status '%s'", status);
        return true;
    }

    /**
     * Callback invoked when a pump is found during the Bluetooth search. Use to filter whether or
     * not to connect to the identified pump.
     * @param peripheral the BluetoothPeripheral representing the pump which was found
     * @param scanResult the result of the Bluetooth search, including e.g. connection strength
     * @return true if we should connect to the detected pump, false otherwise
     */
    public boolean onPumpDiscovered(BluetoothPeripheral peripheral, ScanResult scanResult) {
        Timber.i("TandemPump: onPumpDiscovered(" + scanResult + ")");
        if (filterToBluetoothMac.isPresent()) {
            return (filterToBluetoothMac.get().equals(peripheral.getAddress()));
        }
        return true;
    }

    /**
     * Called after authentication is complete and the pump can be connected to.
     * @param peripheral the BluetoothPeripheral representing the pump which was found
     */
    public void onPumpConnected(BluetoothPeripheral peripheral) {
        Timber.i("TandemPump: onPumpConnected");
        sendCommand(peripheral, new ApiVersionRequest());
        sendCommand(peripheral, new TimeSinceResetRequest());
    }

    /**
     * Called when the pump model number is returned from the Bluetooth DIS
     * @param peripheral the BluetoothPeripheral representing the pump which is connected
     * @param modelNumber the model number of the pump. Expected value is "X2" for the t:slim X2;
     *                    may be different in the future, e.g. for Mobi)
     */
    public void onPumpModel(BluetoothPeripheral peripheral, String modelNumber) {
        Timber.i("TandemPump: onPumpModel: %s", modelNumber);
    }

    /**
     * Callback invoked when the CentralChallengeResponse is received, and pair() can be invoked
     * with a pairing code displayed on the pump to instantiate the pump-to-device connection.
     * @param peripheral the BluetoothPeripheral representing the pump
     * @param centralChallenge the CentralChallengeResponse which should be passed to pair()
     */
    public abstract void onWaitingForPairingCode(BluetoothPeripheral peripheral, CentralChallengeResponse centralChallenge);


    /**
     * Invoked by the user to pair to the connected pump, with the CentralChallengeResponse received
     * from the onWaitingForPairingCode callback.
     * @param peripheral the BluetoothPeripheral representing the pump
     * @param centralChallenge the CentralChallengeResponse received from onWaitingForPairingCode
     * @param pairingCode the 16-character pairing code displayed on the pump screen. Any whitespace
     *                    or dashes will be removed.
     */
    public void pair(BluetoothPeripheral peripheral, CentralChallengeResponse centralChallenge, String pairingCode) {
        Timber.i("TandemPump: pair(" + pairingCode + ")");
        Message message = PumpChallengeRequestBuilder.create(centralChallenge.getAppInstanceId(), pairingCode, centralChallenge.getHmacKey());
        sendCommand(peripheral, message);
    }

    public void onPumpCriticalError(BluetoothPeripheral peripheral, TandemError reason) {
        Timber.e("Unable to connect to pump %s: %s", peripheral, reason);
    }
}

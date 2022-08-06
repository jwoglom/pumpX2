package com.jwoglom.pumpx2.pump.bluetooth;

import android.bluetooth.le.ScanResult;
import android.content.Context;

import com.jwoglom.pumpx2.pump.PumpState;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.Packetize;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.bluetooth.ServiceUUID;
import com.jwoglom.pumpx2.pump.messages.bluetooth.TronMessageWrapper;
import com.jwoglom.pumpx2.pump.messages.bluetooth.models.Packet;
import com.jwoglom.pumpx2.pump.messages.builders.CentralChallengeBuilder;
import com.jwoglom.pumpx2.pump.messages.builders.PumpChallengeBuilder;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ApiVersionRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.TimeSinceResetRequest;
import com.jwoglom.pumpx2.pump.messages.response.authentication.CentralChallengeResponse;
import com.jwoglom.pumpx2.pump.messages.response.authentication.PumpChallengeResponse;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.HciStatus;
import com.welie.blessed.WriteType;

import org.apache.commons.codec.binary.Hex;

import java.util.ArrayList;
import java.util.UUID;

import timber.log.Timber;

/**
 * An abstract class containing callback methods which are invoked when various Bluetooth events
 * occur related to the pump, as well as methods to manipulate the pump and receive responses.
 */
public abstract class TandemPump {
    public final Context context;
    public TandemPump(Context context) {
        this.context = context;

        PumpState.savedAuthenticationKey = PumpState.getPairingCode(context);
    }

    /**
     * Callback invoked when the initial Bluetooth connection with the pump is established,
     * but before the pump authentication process begins. The default behavior is to trigger
     * a CentralChallenge which starts the authentication process.
     * @param peripheral the BluetoothPeripheral representing the connected pump
     */
    public void onInitialPumpConnection(BluetoothPeripheral peripheral) {
        Timber.i("TandemPump: onInitialPumpConnection");
        sendCommand(peripheral, CentralChallengeBuilder.create(0));
    }

    /**
     *
     * @param peripheral
     * @param message
     */
    public void sendCommand(BluetoothPeripheral peripheral, Message message) {
        Timber.i("TandemPump: sendCommand(" + message + ")");
        ArrayList<byte[]> authBytes = new ArrayList<>();
        byte currentTxId = Packetize.txId.get();
        PumpState.pushRequestMessage(message, currentTxId);
        TronMessageWrapper wrapper = new TronMessageWrapper(message, currentTxId);
        Packetize.txId.increment();

        for (Packet packet : wrapper.packets()) {
            authBytes.add(packet.build());
        }

        for (byte[] b : authBytes) {
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
     * @param peripheral the BluetoothPeripheral representing the pump
     * @param message the parsed message which extends the Message class. To identify what type of
     *                message was found, check with `if (message instanceof ApiVersionResponse) ..`
     */
    public abstract void onReceiveMessage(BluetoothPeripheral peripheral, Message message);

    /**
     * Callback invoked when a pump which we were connected to over Bluetooth is disconnected.
     * @param peripheral the BluetoothPeripheral representing the pump
     * @param status the Bluetooth reason for disconnection
     * @return true if we should attempt to re-connect to the device, false otherwise
     */
    public boolean onPumpDisconnected(BluetoothPeripheral peripheral, HciStatus status) {
        Timber.i("TandemPump: onPumpDisconnected");
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
        Message message = PumpChallengeBuilder.create(centralChallenge.getAppInstanceId(), pairingCode, centralChallenge.getHmacKey());
        sendCommand(peripheral, message);
    }
}

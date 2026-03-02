package com.jwoglom.pumpx2.pump.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.util.SparseArray;

import androidx.annotation.Nullable;

import com.jwoglom.pumpx2.pump.messages.bluetooth.BluetoothConstants;
import com.jwoglom.pumpx2.pump.messages.bluetooth.ServiceUUID;
import com.jwoglom.pumpx2.util.timber.DebugTree;
import com.jwoglom.pumpx2.util.timber.LConfigurator;
import com.welie.blessed.BluetoothCentralManager;
import com.welie.blessed.BluetoothCentralManagerCallback;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.ScanFailure;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * An abstract class containing callback methods which are invoked when a Tandem pump is found.
 * Should be used for situations where a list of available devices is listed before connecting
 * to one of them.
 */
public abstract class TandemPumpFinder {
    private final Context context;
    private final Handler handler;

    /**
     * Initializes TandemPumpFinder.
     *
     * @param context    Android context
     * @param timberTree the {@link Timber.Tree} which is initialized for logging with Timber.
     *                   Timber initialization is skipped if null. See {@link LConfigurator}
     */
    public TandemPumpFinder(Context context, @Nullable Timber.Tree timberTree) {
        this.context = context;
        this.handler = new Handler(Looper.getMainLooper());

        if (timberTree != null) {
            // Plant a tree
            Timber.plant(timberTree);
        } else {
            Timber.d("Skipped Timber tree initialization");
        }

        // Create BluetoothCentral
        central = new BluetoothCentralManager(context, bluetoothCentralManagerCallback, new Handler());
    }

    /**
     * Initializes TandemPumpFinder.
     *
     * @param context    Android context
     */
    public TandemPumpFinder(Context context) {
        this(context, Timber.Tree.class.cast(new DebugTree()));
    }

    /**
     * When a pump is discovered, this callback is invoked. It may be invoked multiple times for
     * the same bluetooth device.
     *
     * @param peripheral the BluetoothPeripheral for the discovered pump
     * @param scanResult the ScanResult for the discovered pump
     */
    public abstract void onDiscoveredPump(BluetoothPeripheral peripheral, ScanResult scanResult, PumpReadyState readyState);
    public abstract void onBluetoothState(boolean isBluetoothEnabled);

    private BluetoothCentralManager central;
    private final TandemPumpFinder instance = this;
    private final Map<String, PumpReadyState> pumpReadyStateByAddress = new HashMap<>();

    // Callback for generic BT events
    private final BluetoothCentralManagerCallback bluetoothCentralManagerCallback = new BluetoothCentralManagerCallback() {

        @Override
        public void onDiscoveredPeripheral(@NotNull BluetoothPeripheral peripheral, @NotNull ScanResult scanResult) {
            Timber.i("PUMP-FINDER-DISCOVERED(%s): addr=%s connState=%s bondState=%s", peripheral.getName(), peripheral.getAddress(), peripheral.getState(), peripheral.getBondState());
            PumpReadyState readyState = parsePumpReadyState(scanResult);
            Timber.i("PUMP-FINDER-READY-STATE(%s): addr=%s readyState=%s", peripheral.getName(), peripheral.getAddress(), readyState);
            instance.onDiscoveredPump(peripheral, scanResult, readyState);
        }

        @Override
        public void onBluetoothAdapterStateChanged(int state) {
            Timber.d("TandemPumpFinder: bluetooth adapter changed state to %d", state);
            if (state == BluetoothAdapter.STATE_ON) {
                // Bluetooth is on now, start scanning again
                // Scan for peripherals with a certain service UUIDs
                startScan();
                instance.onBluetoothState(true);
            } else {
                instance.onBluetoothState(false);
            }
        }

        @Override
        public void onScanFailed(@NotNull ScanFailure scanFailure) {
            Timber.i("TandemPumpFinder: scanning failed with error %s", scanFailure);
        }
    };

    public Optional<BluetoothPeripheral> getAlreadyBondedPump() {
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
            if (StringUtils.isNotBlank(name) && BluetoothConstants.isTandemBluetoothDevice(name)) {
                BluetoothPeripheral peripheral = central.getPeripheral(address);
                Timber.d("TandemPumpFinder: bondedDevice on adapter '%s' (%s) appears to be a Tandem device, returning", name, address);
                return Optional.of(peripheral);
            }
        }
        Timber.d("TandemPumpFinder: no bonded Tandem device found");
        return Optional.empty();
    }

    private void immediateScanForPeripherals() {
        Timber.d("TandemPumpFinder: Scanning for all Tandem peripherals");
        Optional<BluetoothPeripheral> alreadyBondedPump = getAlreadyBondedPump();
        ArrayList<ScanFilter> scanFilters = new ArrayList<>();
        scanFilters.add(new ScanFilter.Builder()
                .setServiceUuid(ParcelUuid.fromString(ServiceUUID.PUMP_SERVICE_UUID.toString()))
                .build());
        alreadyBondedPump.ifPresent(alreadyBonded -> {
            scanFilters.add(new ScanFilter.Builder()
                    .setDeviceName(alreadyBonded.getName())
                    .build());
            scanFilters.add(new ScanFilter.Builder()
                    .setDeviceAddress(alreadyBonded.getAddress())
                    .build());
        });
        Timber.i("TandemPumpFinder: using ScanFilters: %s", scanFilters);
        central.scanForPeripheralsUsingFilters(scanFilters);
    }

    public void startScan() {
        Timber.i("TandemPumpFinder: startScan");
        synchronized (pumpReadyStateByAddress) {
            pumpReadyStateByAddress.clear();
        }
        // Scan for peripherals with a certain service UUIDs
        central.startPairingPopupHack();

        handler.postDelayed(this::immediateScanForPeripherals, 1000);
    }


    /**
     * Stops the pump finder operation. Should be completed before continuing to connect to the pump
     * using TandemPump.
     */
    public void stop() {
        central.stopScan();
        central.close();
    }

    private PumpReadyState parsePumpReadyState(@Nullable ScanResult scanResult) {
        if (scanResult == null || scanResult.getScanRecord() == null) {
            return PumpReadyState.UNKNOWN;
        }

        SparseArray<byte[]> manufacturerSpecificData = scanResult.getScanRecord().getManufacturerSpecificData();
        if (manufacturerSpecificData == null || manufacturerSpecificData.size() == 0) {
            return PumpReadyState.UNKNOWN;
        }

        for (int i = 0; i < manufacturerSpecificData.size(); i++) {
            byte[] payload = manufacturerSpecificData.valueAt(i);
            if (payload == null || payload.length == 0) {
                continue;
            }
            PumpReadyState state = decodePumpReadyStateFromPayload(payload);
            if (state != PumpReadyState.UNKNOWN) {
                return state;
            }
        }
        return PumpReadyState.UNKNOWN;
    }

    private PumpReadyState decodePumpReadyStateFromPayload(byte[] payload) {
        // Primary assumption (matches iOS path): state byte is at end of manufacturer payload.
        PumpReadyState state = PumpReadyState.fromManufacturerStateByte(payload[payload.length - 1]);
        if (state != PumpReadyState.UNKNOWN) {
            return state;
        }

        // Fallback: some Android stacks/carriers can shift payload fields; scan for known values.
        for (int i = payload.length - 1; i >= 0; i--) {
            state = PumpReadyState.fromManufacturerStateByte(payload[i]);
            if (state != PumpReadyState.UNKNOWN) {
                return state;
            }
        }

        return PumpReadyState.UNKNOWN;
    }
}

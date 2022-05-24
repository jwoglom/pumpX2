package com.jwoglom.pumpx2.pump.bluetooth;

//import android.Manifest;
//import android.app.Activity;
//import android.bluetooth.BluetoothAdapter;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.os.Message;
//import android.util.Log;
//
//import androidx.annotation.RequiresApi;
//import androidx.core.app.ActivityCompat;
//
//import com.polidea.rxandroidble2.RxBleClient;
//import com.polidea.rxandroidble2.RxBleConnection;
//import com.polidea.rxandroidble2.RxBleDevice;
//import com.polidea.rxandroidble2.scan.ScanFilter;
//import com.polidea.rxandroidble2.scan.ScanSettings;
//import com.jwoglom.pumpx2.pump.events.*;
//import com.jwoglom.pumpx2.pump.messages.*;
import com.jwoglom.pumpx2.shared.EventService;
//import com.jwoglom.pumpx2.shared.L;
//
//import org.apache.commons.codec.binary.Hex;
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Observable;
//import java.util.UUID;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//
//import io.reactivex.*;
//import io.reactivex.disposables.Disposable;
//import io.reactivex.schedulers.Schedulers;

public class PumpService extends EventService {
//    private final EventBus BUS = EventBus.getDefault();
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        EventBus.getDefault().register(this);
//    }
//
//    @Override
//    public void onDestroy() {
//        EventBus.getDefault().unregister(this);
//        super.onDestroy();
//    }
//
//    public PumpService() {}
//
//    private RxBleClient rxBleClient;
//    private Disposable scanSubscription;
//    private Disposable stateSubscription;
//    private Disposable connectionSubscription;
//
//
//    @Subscribe
//    public void handleTriggerConnectionInitEvent(TriggerConnectionInitEvent event) {
//        BUS.post(new BTRequestPermissionsEvent(0, event.activity()));
//    }
//
//    @Subscribe
//    public void handleRequestPermissionsEvent(BTRequestPermissionsEvent event) {
//        Activity activity = event.activity();
//
//        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//        int REQUEST_ENABLE_BT = 1;
//        activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//
//        int permCoarse = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
//        int permFine = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
//
//        if (permCoarse != PackageManager.PERMISSION_GRANTED || permFine != PackageManager.PERMISSION_GRANTED) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                activity.requestPermissions(new String[]{
//                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
//                }, 1);
//            }
//
//            BUS.post(new BTRequestPermissionsEvent(event.attempt() + 1, event.activity()));
//        } else {
//            this.rxBleClient = RxBleClient.create(event.activity());
//            BUS.post(new BTPermissionsGrantedEvent());
//        }
//    }
//
//    @Subscribe
//    public void handleScanForPumpEvent(BTScanForPumpEvent event) {
//        L.w(TAG, "ScanForPumpEvent start");
//
//        ScanFilter filter = new ScanFilter.Builder()
//                .setDeviceName(PumpConfig.bluetoothName())
//                //.setDeviceAddress(PumpConfig.pumpMAC)
//                .build();
//
//        L.w(TAG, "Running scanBleDevices");
//        scanSubscription = rxBleClient.scanBleDevices(
//            new ScanSettings.Builder()
//                    .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
//                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
//                    .build(),
//
//            filter
//            // add filters if needed
//        )
//        .subscribeOn(Schedulers.io())
//        .subscribe(
//            scanResult -> {
//                // Process scan result here.
//                final int deviceRssi = scanResult.getRssi();
//                final String deviceName = scanResult.getBleDevice().getName();
//                final String deviceAddr = scanResult.getBleDevice().getMacAddress();
//                L.w(TAG, "scanResult: name "+deviceName+" addr "+deviceAddr+" rssi "+deviceRssi);
//
//                if (deviceName.equals(PumpConfig.bluetoothName())) {
//                    BUS.post(new BTConnectToPumpEvent(deviceName, deviceAddr));
//                }
//            },
//            throwable -> {
//                // Handle an error here.
//                L.w(TAG, "scanThrowable: "+throwable);
//            }
//        );
//    }
//
//    @Subscribe
//    public void handleBTConnectToPumpEvent(BTConnectToPumpEvent event) {
//        L.w(TAG, "ConnectToPumpEvent start:" + event);
//        scanSubscription.dispose();
//
//        RxBleDevice bleDevice = rxBleClient.getBleDevice(event.macAddress());
//
//        /// / Listen for connection state changes
//        stateSubscription = bleDevice.observeConnectionStateChanges()
//                .subscribeOn(Schedulers.io())
//                .subscribe(newState -> {
//                    BUS.post(new BTConnStateChangedEvent(newState));
//                }, throwable -> {
//                    Log.e(TAG, "error during state subscription: " + throwable);
//                });
//
//
//        connectionSubscription = bleDevice.establishConnection(true)
//                .timeout(30, TimeUnit.SECONDS)
//                .subscribeOn(Schedulers.io())
//                .subscribe(conn -> {
//                    L.w(TAG, "connectionSubscription connected: "+conn);
//                    BUS.post(new BTConnectedToPumpEvent(bleDevice));
//                    connectionSubscription.dispose();
//                }, throwable -> {
//                    L.w(TAG, "connectionThrowable: "+throwable);
//                });
//
//    }
//
//    @Subscribe
//    public void handleBTConnStateChangedEvent(BTConnStateChangedEvent event) {
//        L.w(TAG, "onConnectionStateChange: "+event.state());
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    @Subscribe
//    public void handleSendPumpMessageEvent(SendPumpMessageEvent event) {
//        L.w(TAG, "SendPumpMessageEvent: "+event);
//        Message message = event.message();
//
//        byte currentTxId = Packetize.txId.get();
//        TronMessageWrapper wrapper = new TronMessageWrapper(message, currentTxId);
//        Packetize.txId.increment();
//        UUID uuid = CharacteristicUUID.determine(message);
//
//        boolean multiplePackets = wrapper.packets().size() > 1;
//        L.w(TAG, "SendPumpMessage packets: "+wrapper.packets());
//
//        BTProcessGattOperationEvent btEvent = null;
//        List<byte[]> writeBytes = new ArrayList<>();
//        List<BTProcessGattOperationEvent> events = new ArrayList<>();
//        for (Packet packet : wrapper.packets()) {
//            L.w(TAG, "SendPumpMessage packet: "+packet);
//            btEvent = new BTProcessGattOperationEvent(uuid, packet.build(), multiplePackets);
//            writeBytes.add(btEvent.data());
//            events.add(btEvent);
//        }
//
//        // Close...
//        Disposable d = PumpState.device().establishConnection(false) // establish the connection
//                .flatMap(rxBleConnection -> rxBleConnection.setupNotification(uuid) // once the connection is available setup the notification
//                        .flatMap(logDataObservable -> {
//                            List<Observable<byte[]>> observables = new ArrayList<>();
//                            for (BTProcessGattOperationEvent evt : events) {
//                                observables.add(rxBleConnection.writeCharacteristic(evt.uuid(), evt.data()).ignoreElement().toObservable().map(
//                                        e -> new byte[0]
//                                ));
//                            }
//                            observables.add(logDataObservable);
//                            return Observable.merge(observables);
//                        })
//                )
//                .subscribe(
//                        bytes -> {
//                            Log.d(TAG, "DATA READ:" + Hex.encodeHexString(bytes));
//                            //BUS.post(BTResponseParser.parse(wrapper, bytes, MessageType.RESPONSE, uuid));
//                        },
//                        throwable -> {
//                            Log.d(TAG, "Error in BT write:", throwable);
//                        }
//                );
//
////        PumpState.device().establishConnection(false) // establish the connection
////                .flatMap(rxBleConnection -> rxBleConnection.discoverServices().toObservable(),  // once the connection is available discover the services of the peripheral
////                        (rxBleConnection, rxBleDeviceServices) ->
////                                rxBleConnection.getCharacteristic(uuid) // we get the log characteristic (on which we will setup the notification and write the descriptor)
////                                        .map(uuidCharacteristic -> // once the log characteristic is retrieved
////                                                rxBleConnection.setupNotification(uuidCharacteristic, NotificationSetupMode.COMPAT) // we setup the notification on it in the COMPAT mode (without writing the CCC descriptor)
////                                                        .flatMap(logDataObservable -> {
////                                                            List<Observable<byte[]>> observables = new ArrayList<>();
////                                                            for (BTProcessGattOperationEvent evt : events) {
////                                                                observables.add(rxBleConnection.writeCharacteristic(evt.uuid(), evt.data()).ignoreElement().toObservable().map(
////                                                                        e -> new byte[0]
////                                                                ));
////                                                            }
////                                                            observables.add(logDataObservable);
////                                                            return Observable.merge(observables);
////                                                        }))
////                )
////                .map(obj -> obj.blockingGet())
////                .map(obj -> obj.blockingFirst())
////                .subscribe(
////                        bytes -> {
////                            System.out.println(">>> data from device " + Hex.encodeHexString(bytes));
////                            BUS.post(BTResponseParser.parse(wrapper, bytes, MessageType.RESPONSE, uuid));
////                        },
////                        throwable -> {
////                            System.out.println("error");
////                            System.out.println(throwable);
////                        }
////                );
//
////        Disposable d = PumpState.device().establishConnection(true).flatMap(rxBleConnection ->
////                writeAndReadOnNotification(uuid, uuid, writeBytes, false, rxBleConnection))
////                .flatMap(readBytes -> {
////                    Log.w(TAG, "ReadChar bytes: "+Hex.encodeHexString(readBytes));
////                    return null;
////                }).subscribe();
//
//
////        Disposable disp = PumpState.device().establishConnection(true)
////                .compose(ReplayingShare.instance())
////                .flatMapSingle(rxBleConnection -> {
////                    Single<byte[]> single = rxBleConnection.writeCharacteristic(events.get(0).uuid(), events.get(0).data());
////                    for (int i=1; i<events.size(); i++) {
////                        final BTProcessGattOperationEvent evt = events.get(i);
////                        single = single.flatMap(bytes -> rxBleConnection.writeCharacteristic(evt.uuid(), evt.data()));
////                    }
////                    return single.flatMap(bytes -> rxBleConnection.readCharacteristic(uuid).doOnSuccess(readBytes -> {
////                        Log.w(TAG, "ReadChar bytes: "+Hex.encodeHexString(readBytes));
////                    }));
////                })
////                .subscribe(
////                        writeBytes -> {
////                            Log.w(TAG, "WriteBytes: "+Hex.encodeHexString(writeBytes));
////                        },
////                        throwable -> {
////                            Log.w(TAG, "WriteThrowable:", throwable);
////                        }
////                );
//
//
////
////        Collections.reverse(events);
////
////        Observable<RxBleConnection> conn = PumpState.device().establishConnection(true)
////                .compose(ReplayingShare.instance());
////        Disposable subscribe = conn.flatMapSingle(rxBleConnection -> {
////                    Function<byte[], Single<byte[]>> func = null;
////                    for (BTProcessGattOperationEvent e : events) {
////                        Function<byte[], Single<byte[]>> finalFunc = func;
////                        func = output -> {
////                            Log.w(TAG, "Previous write output: " + Hex.encodeHexString(output));
////                            Log.w(TAG, "Performing write: "+e.uuid()+" "+Hex.encodeHexString(e.data()));
////                            Single<byte[]> s = rxBleConnection.writeCharacteristic(e.uuid(), e.data());
////                            if (finalFunc == null) {
////                                return s;
////                            }
////                            return s.flatMap(finalFunc);
////                        };
////                    }
////                    return func.apply(new byte[0]);
////                }).delaySubscription(500, TimeUnit.MILLISECONDS)
////                .subscribe(writeOutput -> {
////                    // subscribe
////                    Log.w(TAG, "WriteOutput done: "+uuid);
////
////                    Disposable d = conn.flatMapSingle(rxBleConnection -> {
////                            Log.w(TAG, "Performing read: "+uuid);
////                            return rxBleConnection.readCharacteristic(uuid).doOnSuccess(readOutput -> {
////                                L.w(TAG, "read: " + Hex.encodeHexString(readOutput));
////                                BUS.post(BTResponseParser.parse(wrapper, readOutput, MessageType.RESPONSE, uuid));
////                            });
////                    }).subscribe(output -> {
////                        L.w(TAG, "SendMessage read output: "+Hex.encodeHexString(output));
////
////                    }, error -> {
////                        L.w(TAG, "SendMessage read error: "+error);
////
////                    });
////
////                }, error -> {
////                    // error
////                    L.w(TAG, "SendMessage error: "+error);
////                }, () -> {
////                    L.w(TAG, "SendMessage complete");
////                });
//
//
////                .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(characteristicUuid)
////                        .doOnSuccess(writtenBytes -> {
////                            // Process written data.
////                        })
////                        .flatMap(writtenBytes -> rxBleConnection.writeCharacteristic(characteristicUuid, bytesToWrite).doOnSuccess(newlyWrittenBytes -> {
////                            // Process newly written data.
////                        }).flatMap())
//
//    }
//
//
//    /*
//    private Single<byte[]> runBTProcessGattOperationEvent(BTProcessGattOperationEvent event, TronMessageWrapper wrapper) {
//        L.w(TAG, "BTProcessGattOperationEvent: "+event+" wrapper "+wrapper);
//        L.w(TAG, "BT WRITE uuid: "+event.uuid()+" data: " + Hex.encodeHexString(event.data()));
//        return PumpState.connection()
//                .writeCharacteristic(event.uuid(), event.data());
//                //.subscribeOn(Schedulers.io());
////                .subscribe(output -> {
////                    L.w(TAG, "BT WRITE completed: "+ Hex.encodeHexString(output)+" from event "+event);
////                }, error -> {
////                    L.w(TAG, "BTProcessGattOperationEvent: error during write from event "+event);
////                    L.w(TAG, error);
////                });
//    }
//
//    private Single<byte[]> runBTReadEvent(BTProcessGattOperationEvent event, TronMessageWrapper wrapper) {
//        return PumpState.connection()
//                .readCharacteristic(event.uuid());
////                .subscribe(read -> {
////                    L.w(TAG, "BT READ output: "+ Hex.encodeHexString(read)+" from event "+event);
////                    BUS.post(BTResponseParser.parse(wrapper, read, MessageType.RESPONSE, event.uuid()));
////                }, error -> {
////                    L.w(TAG, "runBTReadEvent: error during READ from event "+event);
////                    L.w(TAG, error);
////                });
////        return disp;
//    }
//    */
//
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    private Observable<byte[]> writeAndReadOnNotification(UUID writeTo, UUID readOn,
//                                                          List<byte[]> writes,
//                                                          boolean isIndication,
//                                                          RxBleConnection rxBleConnection) {
//        Observable<Observable<byte[]>> notifObservable =
//                isIndication ?
//                        rxBleConnection.setupIndication(readOn) :
//                        rxBleConnection.setupNotification(readOn);
//        return notifObservable.flatMap(
//                        (notificationObservable) -> Observable.combineLatest(
//                                Observable.fromIterable(writes.stream().map(write -> rxBleConnection.writeCharacteristic(writeTo, write).toObservable()).collect(Collectors.toList())),
//                                notificationObservable.take(1),
//                                (writtenBytes, responseBytes) -> responseBytes
//                        )
//                ).take(1)
//                .doOnError(exc -> {
//                    Log.w(TAG, "writeAndRead exception", exc);
//                });
//    }
//    @Subscribe
//    public void handlePumpResponseMessageEvent(PumpResponseMessageEvent event) {
//        L.w(TAG, "handlePumpResponseMessageEvent: "+event);
//    }
}

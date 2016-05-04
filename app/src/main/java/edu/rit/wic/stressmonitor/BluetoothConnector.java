package edu.rit.wic.stressmonitor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.annimon.stream.Exceptional;
import com.annimon.stream.Optional;
import com.annimon.stream.function.ThrowableSupplier;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import edu.rit.wic.stressmonitor.exceptions.BluetoothException;

/**
 * Created by Matthew on 5/4/2016.
 */
public class BluetoothConnector {

    // UUIDs for UAT service and associated characteristics.
    public static final UUID UART_UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    public static final UUID TX_UUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    public static final UUID RX_UUID = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");
    // UUID for the BTLE client characteristic which is necessary for notifications.
    public static UUID CLIENT_UUID = UUID.fromString("00002902-0000-1000-8000-00805F9B34FB");

    private static final String TAG = "BluetoothConnector";
    private static final char DELIMITER = '#';

    public static BluetoothConnector INSTANCE = null;

    public static Optional<BluetoothConnector> getInstance(final BluetoothManager manager) {
        return Exceptional.of(new ThrowableSupplier<BluetoothConnector, Throwable>() {
            @Override
            public BluetoothConnector get() throws Throwable {
                return INSTANCE == null ? INSTANCE = new BluetoothConnector(manager) : INSTANCE;
            }
        }).getOptional();
    }

    public static Optional<BluetoothConnector> getInstance() {
        return Optional.ofNullable(INSTANCE);
    }

    private final BluetoothAdapter adapter;

    private BluetoothGatt gatt;
    private BluetoothGattCharacteristic tx, rx;

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);


        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }
    };

    private final BluetoothAdapter.LeScanCallback scanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

        }
    };

    private BluetoothConnector(BluetoothManager manager) {
        adapter = manager.getAdapter();
    }

    public void startScan() {

    }

    public void stopScan() {

    }

}

package edu.rit.wic.stressmonitor.bluefruit;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.annimon.stream.Optional;
import com.orhanobut.logger.Logger;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

/**
 *  Service for handling communication with an Adafruit Bluefruit device communicating with a
 *  Stress Monitor device. Handling all incoming and outgoing data and notifying all Activities bound
 *  to this service of data changes. Assumes Bluetooth LE Service.
 *
 *  @author Matthew Crocco
 */
public class BluefruitService extends Service {

    enum ConnectionState {
        CONNECTED,
        CONNECTING,
        DISCONNECT
    }

    private final IBinder binder = new LocalBinder();

    private BluetoothManager manager;
    private BluetoothAdapter adapter;
    private BluetoothGatt gatt;
    private String deviceAddress;

    private ConnectionState state = ConnectionState.DISCONNECT;

    private final BluetoothGattCallback gattCallback;

    /**
     * Broadcasts a specific Bluefruit action as a string. Broadcast to bound activities.
     *
     * @param action Name of action
     */
    private void broadcastUpdate(final String action ) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    /**
     * Broadcasts a specific Bluefruit action along with the value of a Characteristic to bound
     * activities.
     *
     * @param action Name of action
     * @param characteristic Characteristic to read data from
     */
    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic){
        final Intent intent = new Intent(action);

        final byte[] data = characteristic.getValue();
        if(data != null && data.length > 0) {
            final StringBuilder sb = new StringBuilder();
            for(byte b : data)
                sb.append(String.format("%02X", b));
            intent.putExtra(BluefruitUtils.EXTRA_DATA, new String(data, StandardCharsets.UTF_8) + "\n"  + sb.toString());
        }

        sendBroadcast(intent);
    }

    // Used to retrieve this instance later
    public class LocalBinder extends Binder {
        public BluefruitService getService() {
            return BluefruitService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Logger.d("Received Binding Request");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    public boolean initialize() {
        Logger.i("Bluefruit is Initializing...");

        if(manager == null) {
            manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if(manager == null) {
                Logger.e("Unable to obtain a BluetoothManager");
                return false;
            }
        }

        Logger.d("Acquiring adapter...");
        adapter = manager.getAdapter();
        if(adapter == null) {
            Logger.e("Unable to obtain a bluetooth adapter");
            return false;
        }

        Logger.d("Complete Initialization");
        return true;
    }

    public boolean connect(final String address) {
        if(adapter == null || address == null) {
            Logger.w("BluetoothAdapter is not initialized or unspecified address");
            return false;
        }

        Logger.i("Attempting to connect to %s", address);
        if(deviceAddress != null && address.equals(deviceAddress) && gatt != null){
            Logger.d("Trying to reconnect to previous device...");
            if(gatt.connect()) {
                state = ConnectionState.CONNECTING;
                return true;
            } else
                return false;
        }

        Logger.i("Getting Device from Adapter...");
        final BluetoothDevice device = adapter.getRemoteDevice(address);
        if(device == null) {
            Logger.w("Device not found. Unable to Connect.");
            return false;
        }

        Logger.i("Connecting to GATT...");
        gatt = device.connectGatt(this, false, gattCallback);
        Logger.d("Trying to create a new connection...");
        state = ConnectionState.CONNECTING;
        return true;
    }

    public void disconnect() {
        if(adapter == null || gatt == null)
            Logger.w("Bluetooth Adapter not initialized");
        else
            gatt.disconnect();
    }

    public void close() {
        if(gatt == null)
            return;

        gatt.close();
        gatt = null;
    }

    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        gatt.writeCharacteristic(characteristic);
    }

    public void writeTo(BluetoothGattCharacteristic characteristic, String data) {
        characteristic.setValue(data.getBytes(StandardCharsets.UTF_8));
    }

    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if(adapter == null || gatt == null)
            Logger.w("Bluetooth Adapter not initialized");
        else
            gatt.readCharacteristic(characteristic);
    }

    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled){
        if(adapter == null || gatt == null)
            Logger.w("Bluetooth Adapter not initialized");
        else {
            gatt.setCharacteristicNotification(characteristic, enabled);
        }
    }

    public boolean writeDescriptor(BluetoothGattDescriptor descriptor) {
        return gatt.writeDescriptor(descriptor);
    }

    public Optional<BluetoothGattService> getGattService(UUID serviceUuid) {
        return Optional.ofNullable(gatt.getService(serviceUuid));
    }

    public List<BluetoothGattService> getGattServices() {
        if(gatt == null)
            return null;

        return gatt.getServices();
    }

    {
        gattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                String intentAction;

                if(newState == BluetoothProfile.STATE_CONNECTED) {
                    intentAction = BluefruitUtils.ACTION_GATT_CONNECTED;
                    state = ConnectionState.CONNECTED;
                    broadcastUpdate(intentAction);

                    Logger.i("Connected to GATT server.");
                    Logger.i("Attempting to start service discovery: %s", gatt.discoverServices());
                } else if(newState == BluetoothProfile.STATE_DISCONNECTED) {
                    intentAction = BluefruitUtils.ACTION_GATT_DISCONNECTED;
                    state = ConnectionState.DISCONNECT;
                    Logger.i("Disconnected from GATT server.");
                    broadcastUpdate(intentAction);
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);

                if(status == BluetoothGatt.GATT_SUCCESS)
                    broadcastUpdate(BluefruitUtils.ACTION_GATT_SERVICES_DISCOVERED);
                else
                    Logger.w("onServicesDiscovered received: %d", status);

            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);

                Logger.i("CHARACTERISTIC READ: %s -- %d", characteristic.getUuid().toString(), status);
                if(status == BluetoothGatt.GATT_SUCCESS)
                    broadcastUpdate(BluefruitUtils.ACTION_DATA_AVAILABLE, characteristic);
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);

                Logger.i("CHARACTERISTIC CHANGE: %s", characteristic.getUuid().toString());
                broadcastUpdate(BluefruitUtils.ACTION_DATA_AVAILABLE, characteristic);
            }
        };
    }
}

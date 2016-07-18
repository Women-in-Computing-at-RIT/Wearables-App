package edu.rit.wic.stressmonitor.bluefruit;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import edu.rit.wic.stressmonitor.R;
import edu.rit.wic.stressmonitor.TelemetryActivity;

/**
 * Activity that actively scans for Bluetooth LE Devices (like the adafruit Bluefruit) and lists
 * found, unpaired, devices. The activity handles retrieving information and forwarding over to
 * {@link TelemetryActivity} to read the information streamed from a Stress Monitor device. Will also
 * handle requesting Bluetooth capability if currently unavailable.
 *
 * @author Matthew Crocco
 */
public class BluefruitScanActivity extends ListActivity {

    private BluetoothListAdapter deviceAdapter;
    private BluefruitAdapter adapter;

    private static final int REQUEST_ENABLE_BT = 1; // Request Enable Bluetooth ID
    private static final long SCAN_PERIOD = 10000;  // Scan for this many milliseconds before stopping

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO Proper handling, not just exiting.
        // Handling Bluetooth being entirely unavailable, not a Bluetooth capable device!
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            finish();
        }

        // Get Bluetooth Adapter from Manager System Service. This is available in later android versions we are targeting
        final BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = manager.getAdapter();

        // Same as above, being unavailable entirely.
        if(bluetoothAdapter == null){
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            this.adapter = new BluefruitAdapter(bluetoothAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bt_menu, menu);
        MenuItem stop, scan, refresh;

        stop = menu.findItem(R.id.menu_stop);
        scan = menu.findItem(R.id.menu_scan);
        refresh = menu.findItem(R.id.menu_refresh);

        if(adapter.isScanning()) {
            stop.setVisible(true);
            scan.setVisible(false);
            refresh.setActionView(R.layout.actionbar_indeterminate_progress);
        } else {
            stop.setVisible(false);
            scan.setVisible(true);
            refresh.setActionView(null);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_scan:
                deviceAdapter.clear();
                enableDeviceScan();
                break;
            case R.id.menu_stop:
                disableDeviceScan();
                break;
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
            If Bluetooth is disabled, request it be enabled using an intent and getting the result
            of said Intent. See onActivityResult
         */
        if(!adapter.isEnabled()) {
            Intent enableBtIntent = new Intent((BluetoothAdapter.ACTION_REQUEST_ENABLE));
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // Re-initialize Bluetooth Device List and re-scan
        deviceAdapter = new BluetoothListAdapter(this);
        setListAdapter(deviceAdapter);
        enableDeviceScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED)
            finish(); // Bluetooth Not Enabled //TODO Better Handling
        else
            super.onActivityResult(requestCode, resultCode, data); // Pass on, Bluetooth is now on
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Stop scan and clear List
        disableDeviceScan();
        deviceAdapter.clear();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // We are selecting a device
        final BluetoothDevice device = deviceAdapter.getDevice(position);

        if(device == null)
            return;

        // Disable scan if still ongoing
        if(adapter.isScanning())
            disableDeviceScan();

        // Transfer to TelemetryActivity with Device Name and Device Address in Intent
        final Intent intent = new Intent(this, TelemetryActivity.class);
        intent.putExtra(BluefruitConstants.EXTRAS_DEVICE_NAME, device.getName());
        intent.putExtra(BluefruitConstants.EXTRAS_DEVICE_ADDRESS, device.getAddress());
        startActivity(intent);
    }

    // Scan Callback just to add devices to list and update
    private final BluetoothAdapter.LeScanCallback scanCallback = (device, rssi, scanRecord) -> {
        runOnUiThread(() -> {
            deviceAdapter.addDevice(device);
            deviceAdapter.notifyDataSetChanged();
        });
    };

    private void enableDeviceScan() {
        setDeviceScan(true);
    }

    private void disableDeviceScan() {
        setDeviceScan(false);
    }

    private void setDeviceScan(boolean enable) {
        if(enable)
            adapter.startScan(SCAN_PERIOD, this::invalidateOptionsMenu, scanCallback);
        else
            adapter.stopScan(scanCallback);

        invalidateOptionsMenu();
    }
}

package edu.rit.wic.stressmonitor.bluefruit;

import android.bluetooth.BluetoothAdapter;
import android.os.Handler;

/**
 * A wrapping interface around a {@link BluetoothAdapter} to provide some useful state management and
 * to handle some tiny details like Scan Periods and Scanning State.
 *
 * @author Matthew Crocco
 */
public class BluefruitAdapter {

    private final BluetoothAdapter adapter;
    private boolean scanning = false;

    private Handler handler = new Handler();    // On bt_menu looper

    public BluefruitAdapter(BluetoothAdapter adapter) {
        this.adapter = adapter;
    }

    /**
     * Runs a Bluetooth LE Scan for the given scanning period. Once the scan period is elapsed, the
     * Scan is stopped and the "onElapsed" callback is called. The LE Scan Callback is passed to
     * {@link BluetoothAdapter#startLeScan(BluetoothAdapter.LeScanCallback)}.
     *
     * @param scanPeriod Period on which to allow scanning to occur, once elapsed the scan ends. In milliseconds.
     * @param onElapsed Callback executed when the scanPeriod elapses
     * @param callback Callback used by {@link BluetoothAdapter#startLeScan(BluetoothAdapter.LeScanCallback)}
     */
    public void startScan(long scanPeriod, Runnable onElapsed, BluetoothAdapter.LeScanCallback callback) {
        handler.postDelayed(() -> {
            BluefruitAdapter.this.stopScan(callback);
            if(onElapsed != null)
                onElapsed.run();
        }, scanPeriod);

        scanning = true;
        adapter.startLeScan(callback);
    }

    /**
     * Runs a Bluetooth LE Scan for the given scanning period. Once the scan period is elapsed the Scan will
     * be stopped. The LE Scan Callback is passed to {@link BluetoothAdapter#startLeScan(BluetoothAdapter.LeScanCallback)}.
     *
     * @param scanPeriod Period on which to allow scanning to occur, once elapsed the scan ends. In milliseconds.
     * @param callback Callback used by {@link BluetoothAdapter#startLeScan(BluetoothAdapter.LeScanCallback)}
     */
    public void startScan(long scanPeriod, BluetoothAdapter.LeScanCallback callback) {
        startScan(scanPeriod, null, callback);
    }

    /**
     * Stops a Bluetooth LE Scan assuming it was already started.
     * @param callback Callback to pass to stop scan
     */
    public void stopScan(BluetoothAdapter.LeScanCallback callback) {
        scanning = false;
        adapter.stopLeScan(callback);
    }

    public boolean isScanning() {
        return scanning;
    }

    public boolean isEnabled() {
        return adapter.isEnabled();
    }
}

/**
 * File: MainActivity.java
 * @author Cara Steinberg
 */
package edu.rit.wic.stressmonitor;


import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.annimon.stream.Optional;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.orhanobut.logger.Logger;

import org.joda.time.Instant;

import java.util.Locale;

import edu.rit.wic.stressmonitor.bluefruit.BluefruitConstants;
import edu.rit.wic.stressmonitor.bluefruit.BluefruitScanActivity;
import edu.rit.wic.stressmonitor.bluefruit.BluefruitService;
import edu.rit.wic.stressmonitor.bluefruit.BluefruitUtils;


/**
 * Activity displays heart rate readings and connects the Adafruit device via bluetooth.
 */
public class MainActivity extends AppCompatActivity {
    TextView textView;
    ImageView imageView;
    Drawer nav_drawer;
    Toolbar toolbar;
    ActionBar actionBar;

    // Data Set Name Constants
    private static final String BPM_DATA_NAME = "BPM",
            TREND_DATA_NAME = "Average",
            IBI_DATA_NAME = "IBI";

    private TextView bpmAverageField;
    private TextView bpmMedianField;

    private LineChart bpmChart;

    private BluefruitService bluefruitService;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // Obtain Service from Anonymous Method
            bluefruitService = ((BluefruitService.LocalBinder) service).getService();

            Logger.i("Attempting to Initialize Bluefruit Service...");
            if(!bluefruitService.initialize()) {
                Logger.e("Unable to initialize blutooth");
                finish();
            } else
                Logger.i("Success!");

            Logger.i("Connecting to %s : %s...", deviceAddress, deviceName);
            bluefruitService.connect(deviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Logger.w("Bluefruit Service Disconnected!");
            bluefruitService = null;
        }
    };

    private String deviceName;
    private String deviceAddress;
    private boolean connected = false;

    private Optional<BluetoothGattCharacteristic> txGatt = Optional.empty(), rxGatt = Optional.empty();
    private final BroadcastReceiver gattUpdaterReceiver = new BroadcastReceiver() {

        private static final int SAMPLES = 30;          // Number of BPM Readings to Sample
        private int[] sampleValues = new int[SAMPLES];  // Container for BPM Readings
        private int valueCounter = 0;                   // Index Counter

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(BluefruitUtils.ACTION_GATT_CONNECTED.equals(action)) {
                connected = true;
                Logger.d("Connected: " + connected);
                invalidateOptionsMenu();
            } else if(BluefruitUtils.ACTION_GATT_DISCONNECTED.equals(action)) {
                // No longer connected, free references to TX and RX Characteristics
                connected = false;
                txGatt = Optional.empty();
                rxGatt = Optional.empty();
                invalidateOptionsMenu();
                Logger.d("Connected: " + connected);
            } else if(BluefruitUtils.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Optional<BluetoothGattService> uartOption = bluefruitService.getGattService(BluefruitConstants.UUID_UART);
                Logger.d("Connected: " + connected);
                if(uartOption.isPresent()) {

                    /*
                        If we detect a UART Service, we are going to determine if RX and TX are
                        available Characteristics of that service. The Flora provides a UART
                        service with access to these characteristics (Receiving and Transmitting)
                     */

                    BluetoothGattService uart = uartOption.get();

                    for(BluetoothGattCharacteristic c : uart.getCharacteristics())
                        Logger.i("UART CHARACTERISTIC FOUND: %s", c.getUuid().toString());

                    Logger.d("Getting TX and RX characteristics...");
                    txGatt = Optional.of(uart.getCharacteristic(BluefruitConstants.UUID_TX));
                    rxGatt = Optional.of(uart.getCharacteristic(BluefruitConstants.UUID_RX));

                    // Set Characteristic Notification to RX
                    bluefruitService.setCharacteristicNotification(rxGatt.get(), true);

                    BluetoothGattDescriptor rxDescriptor = rxGatt.get().getDescriptor(BluefruitConstants.UUID_CLIENT);
                    if(rxDescriptor != null) {
                        rxDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        if(!bluefruitService.writeDescriptor(rxDescriptor))
                            Logger.w("Couldn't write RX Client Descriptor Value!");
                    }

                    Logger.i("UART is supported by device!");
                } else {
                    Logger.w("Device does not support UART!");
                }

            } else if(BluefruitUtils.ACTION_DATA_AVAILABLE.equals(action)){
                // Extra Data in this case is going to be whatever data the RX Characteristic gave us
                String extraData = intent.getStringExtra(BluefruitUtils.EXTRA_DATA);

                String[] bits = extraData.split(" ");
                int ibi = 0, bpm = 0;

                // Retrieve BPM and IBI Reading if possible based on format:
                // B<BPM> Q<IBI>
                try {
                    for (String s : bits)
                        if (s.startsWith("B"))
                            bpm = Integer.parseInt(s.substring(1, 4));
                        else if (s.startsWith("Q"))
                            ibi = Integer.parseInt(s.substring(1, 4));

                    sampleValues[valueCounter++] = bpm;

                    if (valueCounter == sampleValues.length) {
                        addDataPoint(sampleValues);
                        valueCounter = 0;
                    }
                } catch(Exception e){
                    e.printStackTrace();
                }

                Logger.i("Received: %s, BPM: %d, IBI: %d", extraData, bpm, ibi);
            }

            imageView = (ImageView) findViewById(R.id.bt_icon);
            if (connected) {
                imageView.setImageResource(R.drawable.bt_connected_green);
            } else {
                imageView.setImageResource(R.drawable.bt_disconnected_gray);
            }

//            textView = (TextView) findViewById(R.id.link_devices);
//            if (connected) {
//                textView.setText(R.string.device_connected);
//            } else {
//                textView.setText(R.string.device_disconnected);
//            }

        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the toolbar view inside the activity layout
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        // Creates navigation drawer
        createNavDrawer();

        // Retrieve Views from Layout
        this.bpmAverageField = (TextView) findViewById(R.id.bpm_average);
        this.bpmMedianField = (TextView) findViewById(R.id.bpm_median);
        this.bpmChart = (LineChart) findViewById(R.id.bpm_chart);

        // Get Device Name and Address for connecting from Intent
        final Intent intent = getIntent();
        deviceName = intent.getStringExtra(BluefruitConstants.EXTRAS_DEVICE_NAME);
        deviceAddress = intent.getStringExtra(BluefruitConstants.EXTRAS_DEVICE_ADDRESS);

        // Start Bluefruit Service ASAP, this is done by sending an intent and binding the service
        // to this activity via our ServiceConnection
        Logger.i("Sending Bluefruit Service intent...");
        Intent gattServiceIntent = new Intent(this, BluefruitService.class);
        bindService(gattServiceIntent, serviceConnection, BIND_AUTO_CREATE);

        // Chart Formatting/Display Options
        bpmChart.setDrawGridBackground(false);
        bpmChart.setDescription("BPM Values");
        bpmChart.setNoDataTextDescription("No BPM Yet!");

        bpmChart.setTouchEnabled(true);

        bpmChart.setDragEnabled(true);
        bpmChart.setScaleEnabled(true);

        bpmChart.setPinchZoom(false);
        bpmChart.setPinchZoom(false);

        LineData data = new LineData();
        bpmChart.setData(data);

        Legend legend = bpmChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextColor(Color.BLACK);

        XAxis xaxis = bpmChart.getXAxis();
        xaxis.setDrawGridLines(false);
        xaxis.setAvoidFirstLastClipping(true);
        xaxis.setSpaceBetweenLabels(5);
        xaxis.setEnabled(true);

        YAxis yaxis = bpmChart.getAxisLeft();
        yaxis.setAxisMaxValue(200.f);
        yaxis.setAxisMinValue(0.f);
        yaxis.setDrawGridLines(true);

        bpmChart.getAxisRight().setEnabled(false);

        bpmChart.getData().addDataSet(createSet(BPM_DATA_NAME));
        bpmChart.getData().addDataSet(createTrendSet());
        bpmChart.invalidate();

        Logger.i("Finished initializing TelemetryActivity");

//        imageView = (ImageView) findViewById(R.id.bt_icon);
//        if (connected) {
//            imageView.setImageResource(R.drawable.bt_connected_green);
//        } else {
//            imageView.setImageResource(R.drawable.bt_disconnected_gray);
//        }
//
//        textView = (TextView) findViewById(R.id.link_devices);
//        if (connected) {
//            textView.setText(R.string.device_connected);
//        } else {
//            textView.setText(R.string.device_disconnected);
//        }
    }

    public boolean isConnected() {
        Logger.d("Connected: " + connected);
        return connected;
    }



    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Use our receiver to receive bluetooth data asynchronously, while also handle updating
        // the chart.
        registerReceiver(gattUpdaterReceiver, BluefruitUtils.makeGattUpdateIntentFilter());

        Logger.d("BluefruitService = %s", String.valueOf(bluefruitService));
        if(bluefruitService != null) {
            final boolean result = bluefruitService.connect(deviceAddress);
            Logger.d("Connect request result = %s", result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(gattUpdaterReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.w("Unbinding service, destroying...");
        unbindService(serviceConnection);
        bluefruitService = null;
    }

    private LineDataSet createSet(String name) {
        LineDataSet set = new LineDataSet(null, name);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(Color.MAGENTA);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextSize(9f);
        set.setDrawValues(false);

        return set;
    }

    private LineDataSet createTrendSet() {
        LineDataSet set = new LineDataSet(null, TREND_DATA_NAME);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.rgb("4D1F5B"));
        set.setLineWidth(1f);
        set.setCircleRadius(0);
        set.setFillAlpha(75);
        set.setFillColor(ColorTemplate.rgb("4D1F5B"));
        set.setHighlightEnabled(false);
        set.setDrawValues(false);

        return set;
    }

    private static final int AVERAGE_THRESHOLD = 5;
    private int averageBpm = 0;
    private void addDataPoint(int[] bpmValues) {

        if(!AppUtils.isOnUiThread()) {
            // Make sure we run on the UI Thread Only!
            runOnUiThread(() -> addDataPoint(bpmValues));
            return;
        }

        // Get all of our data so we can manipulate the data sets
        LineData data = bpmChart.getData();

        if(data != null) {
            ILineDataSet set = data.getDataSetByLabel(BPM_DATA_NAME, true);

            // If the BPM Set does not exist, create it and add it as a data set.
            if(set == null) {
                set = createSet(BPM_DATA_NAME);
                data.addDataSet(set);
            }

            // Find median and add it to the data with the entry time being this instant
            long median = Math.round(MathUtils.findMedian(bpmValues));
            data.addXValue(Instant.now().toDateTime().toString("k:mm:ss,SSS"));
            data.addEntry(new Entry((float) median, set.getEntryCount()), 0);

            // TODO Use a weighted average of some sort so data points lose impact over time
            // TODO Determine if this is statistically inaccurate, we need to find a better statistical modle
            // Determine our current average, will not be calculated until a threshold is reached,
            // then an initial average will be established, anything proceeding it is added to a
            // moving average as a Simple Cumulative Average of Medians
            if(set.getEntryCount() > AVERAGE_THRESHOLD){
                if(averageBpm == 0) {
                    for(int i = 0; i < set.getEntryCount(); i++)
                        averageBpm += Math.round(set.getEntryForIndex(i).getVal());

                    averageBpm /= set.getEntryCount();
                } else {
                    averageBpm += (median - averageBpm)/set.getEntryCount();
                }
            }

            // Show 40 values at a time
            bpmChart.setVisibleXRange(0, 40);
            bpmChart.moveViewToX(data.getXValCount() - 41);

            // Set Text Fields
            bpmAverageField.setText(String.format(Locale.US, "%d", averageBpm));
            bpmMedianField.setText(String.format(Locale.US, "%d", median));

            // Update Chart
            bpmChart.notifyDataSetChanged();

            // Create Average line (this is less a trend line and more of just a horizontal marker
            // it is a data set of two elements at the extreme left and right of the visible graph
            if(averageBpm > 0) {
                ILineDataSet trendLine = data.getDataSetByLabel(TREND_DATA_NAME, true);

                trendLine.clear();
                trendLine.addEntry(new Entry(averageBpm, bpmChart.getLowestVisibleXIndex()));
                trendLine.addEntry(new Entry(averageBpm, bpmChart.getHighestVisibleXIndex()));
            }

            // Update chart again, the previous update was to make sure our data was complete
            // before creating the average line
            bpmChart.notifyDataSetChanged();
        }
    }

    public Drawer createNavDrawer() {
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.home_drawer);
        PrimaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName(R.string.profile_drawer);
        PrimaryDrawerItem item3 = new SecondaryDrawerItem().withIdentifier(3).withName(R.string.contact_drawer);
        nav_drawer = new DrawerBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(false)
                .withActionBarDrawerToggle(true)
                .withToolbar(toolbar)
                .addDrawerItems(
                        item1,
                        item2,
                        new DividerDrawerItem(),
                        item3
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {

                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem == item1) {
                            nav_drawer.closeDrawer();
                        } else if (drawerItem == item2) {
                            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                        } else if (drawerItem == item3) {
                            startActivity(new Intent(MainActivity.this, ContactActivity.class));
                        }
                        return true;
                    }
                })
                .build();

        //Back arrow
        nav_drawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        // Hamburger icon
        actionBar.setDisplayHomeAsUpEnabled(false);
        nav_drawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
        nav_drawer.setSelection(1);
        return nav_drawer;
    }

    @Override
    protected void onPostResume() {
        textView = (TextView) findViewById(R.id.link_devices);
        textView.setText(R.string.device_connected);
        textView.setOnClickListener(
                (v) -> startActivity(new Intent(MainActivity.this, BluefruitScanActivity.class))
        );
        super.onPostResume();
    }
}

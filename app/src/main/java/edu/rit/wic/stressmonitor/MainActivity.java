package edu.rit.wic.stressmonitor;


import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.annimon.stream.Optional;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.orhanobut.logger.Logger;

import edu.rit.wic.stressmonitor.bluefruit.BluefruitConstants;
import edu.rit.wic.stressmonitor.bluefruit.BluefruitScanActivity;
import edu.rit.wic.stressmonitor.bluefruit.BluefruitService;
import edu.rit.wic.stressmonitor.bluefruit.BluefruitUtils;


public class MainActivity extends AppCompatActivity {
    TextView textView;
    ImageView imageView;
    Drawer nav_drawer;
    Toolbar toolbar;
    ActionBar actionBar;

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

        imageView = (ImageView) findViewById(R.id.bt_icon);
        if (isConnected()) {
            imageView.setImageResource(R.drawable.bt_connected_green);
        } else {
            imageView.setImageResource(R.drawable.bt_disconnected_gray);
        }

        textView = (TextView) findViewById(R.id.link_devices);
        if (isConnected()) {
            textView.setText(R.string.device_connected);
        } else {
            textView.setText(R.string.device_disconnected);
        }
    }
    private BluefruitService bluefruitService;
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
                invalidateOptionsMenu();
            } else if(BluefruitUtils.ACTION_GATT_DISCONNECTED.equals(action)) {
                // No longer connected, free references to TX and RX Characteristics
                connected = false;
                txGatt = Optional.empty();
                rxGatt = Optional.empty();
                invalidateOptionsMenu();
            } else if(BluefruitUtils.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Optional<BluetoothGattService> uartOption = bluefruitService.getGattService(BluefruitConstants.UUID_UART);

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
            }

        }
    };

    public boolean isConnected() {
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
        textView.setOnClickListener(
                (v) -> startActivity(new Intent(MainActivity.this, BluefruitScanActivity.class))
        );
        super.onPostResume();
    }
}

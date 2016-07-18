package edu.rit.wic.stressmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        startActivity(new Intent(this, LoginActivity.class));

        Toolbar menu = (Toolbar) findViewById(R.id.menu_bar);
        setSupportActionBar(menu);

//        If connected to device via Bluetooth
        findViewById(R.id.link_connected_devices).setVisibility(View.VISIBLE);
        findViewById(R.id.bt_icon_green).setVisibility(View.VISIBLE);
        findViewById(R.id.link_disconnected_devices).setVisibility(View.INVISIBLE);
        findViewById(R.id.bt_icon_gray).setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}

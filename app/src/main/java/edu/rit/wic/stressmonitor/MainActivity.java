package edu.rit.wic.stressmonitor;

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

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import edu.rit.wic.stressmonitor.bluefruit.BluefruitScanActivity;

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

        boolean connected = false;
        imageView = (ImageView) findViewById(R.id.bt_icon);
        if (connected) {
            imageView.setImageResource(R.drawable.bt_connected_green);
        } else {
            imageView.setImageResource(R.drawable.bt_disconnected_gray);
        }

        textView = (TextView) findViewById(R.id.link_devices);
        if (connected) {
            textView.setText(R.string.device_connected);
        } else {
            textView.setText(R.string.device_disconnected);
        }
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

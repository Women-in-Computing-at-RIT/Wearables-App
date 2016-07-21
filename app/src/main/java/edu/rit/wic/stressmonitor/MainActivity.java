package edu.rit.wic.stressmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import edu.rit.wic.stressmonitor.bluefruit.BluefruitScanActivity;


public class MainActivity extends AppCompatActivity {
    TextView connected_link;
    TextView disconnected_link;
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

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(),
                MainActivity.this));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        connected_link = (TextView) findViewById(R.id.link_connected_devices);
        disconnected_link = (TextView) findViewById(R.id.link_disconnected_devices);

//        If connected to device via Bluetooth
        findViewById(R.id.link_connected_devices).setVisibility(View.VISIBLE);
        findViewById(R.id.bt_icon_green).setVisibility(View.VISIBLE);
        findViewById(R.id.link_disconnected_devices).setVisibility(View.INVISIBLE);
        findViewById(R.id.bt_icon_gray).setVisibility(View.INVISIBLE);
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
        connected_link.setOnClickListener(
                (v) -> startActivity(new Intent(MainActivity.this, BluefruitScanActivity.class))
        );
        super.onPostResume();
    }
}

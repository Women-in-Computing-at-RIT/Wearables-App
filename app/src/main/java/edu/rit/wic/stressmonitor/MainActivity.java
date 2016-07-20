package edu.rit.wic.stressmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
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
    ImageButton menu;
    Drawer nav_drawer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        LayoutInflater li = LayoutInflater.from(this);
        View customView = li.inflate(R.layout.custom_bar_view, null);
        if (actionBar != null) {
            actionBar.setCustomView(customView);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        menu = (ImageButton) customView.findViewById(R.id.menu_ic);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nav_drawer.openDrawer();
            }
        });

        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.profile_drawer);
        PrimaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName(R.string.contact_drawer);
        nav_drawer = new DrawerBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(false)
                .withActionBarDrawerToggle(false)
                .addDrawerItems(
                        item1,
                        new DividerDrawerItem(),
                        item2,
                        new SecondaryDrawerItem().withName("Contact Support")
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                   @Override
                   public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem == item1) {
                            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                        }
                       return true;
                   }
                })
                .build();



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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.layout.custom_bar_view:
//                nav_drawer.openDrawer();
//                return true;
//
//            case R.id.action_settings:
//                // User chose the "Settings" item, show the app settings UI...
//                nav_drawer.openDrawer();
//                return true;
//
//            default:
//                // If we got here, the user's action was not recognized.
//                // Invoke the superclass to handle it.
//                return super.onOptionsItemSelected(item);
//
//        }
//    }

    @Override
    protected void onPostResume() {


        connected_link.setOnClickListener(
                (v) -> startActivity(new Intent(MainActivity.this, BluefruitScanActivity.class))
        );
        super.onPostResume();
    }
}

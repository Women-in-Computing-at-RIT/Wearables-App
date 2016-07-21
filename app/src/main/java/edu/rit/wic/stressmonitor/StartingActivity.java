package edu.rit.wic.stressmonitor;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.orhanobut.logger.Logger;

public class StartingActivity extends AppCompatActivity {
    Button loginBtn;
    Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        loginBtn = (Button) findViewById(R.id.btn_go_login);
        registerBtn = (Button) findViewById(R.id.btn_go_register);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        Logger.init("WiC");

        Logger.i(String.valueOf(getApplicationInfo().name) + " Initialized!");
        Thread.setDefaultUncaughtExceptionHandler((t, ex) -> {
            ex.printStackTrace();
            Logger.e(ex, "Unhandled Exception");
        });
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onPostResume() {
        loginBtn.setOnClickListener(
                (v) -> startActivity(new Intent(StartingActivity.this, LoginActivity.class))
        );
        registerBtn.setOnClickListener(
                (v) -> startActivity(new Intent(StartingActivity.this, RegisterActivity.class))
        );
        super.onPostResume();
    }
}

package edu.rit.wic.stressmonitor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.orhanobut.logger.Logger;

public class StartActivity extends AppCompatActivity {

    Button startButton;
    Button loginButton;
    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);
        loginButton = (Button) findViewById(R.id.btn_go_login);
        registerButton = (Button) findViewById(R.id.btn_go_register);
//        startButton = (Button) findViewById(R.id.button_start_scan);

        Logger.init("WiC");

        Logger.i(String.valueOf(getApplicationInfo().name) + " Initialized!");
        Thread.setDefaultUncaughtExceptionHandler((t, ex) -> {
            ex.printStackTrace();
            Logger.e(ex, "Unhandled Exception");
        });
    }

    @Override
    protected void onPostResume() {
//        startButton.setOnClickListener(
//                (v) -> startActivity(new Intent(StartActivity.this, BluefruitScanActivity.class))
//        );
        loginButton.setOnClickListener(
                (v) -> startActivity(new Intent(StartActivity.this, LoginActivity.class))
        );
        registerButton.setOnClickListener(
                (v) -> startActivity(new Intent(this, RegisterActivity.class))
        );
        super.onPostResume();
    }
}

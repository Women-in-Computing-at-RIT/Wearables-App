package edu.rit.wic.stressmonitor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.orhanobut.logger.Logger;

public class StartingActivity extends AppCompatActivity {
    Button startButton;
    Button loginBtn;
    Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        loginBtn = (Button) findViewById(R.id.btn_go_login);
        registerBtn = (Button) findViewById(R.id.btn_go_register);
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
//                (v) -> startActivity(new Intent(StartingActivity.this, BluefruitScanActivity.class))
//        );
        loginBtn.setOnClickListener(
                (v) -> startActivity(new Intent(StartingActivity.this, LoginActivity.class))
        );
        registerBtn.setOnClickListener(
                (v) -> startActivity(new Intent(StartingActivity.this, RegisterActivity.class))
        );
        super.onPostResume();
    }
}

package edu.rit.wic.stressmonitor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.orhanobut.logger.Logger;
import edu.rit.wic.stressmonitor.bluefruit.BluefruitScanActivity;

public class MainActivity extends AppCompatActivity {

    Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        startButton = (Button) findViewById(R.id.button_start_scan);

        Logger.init("WiC");

        Logger.i(String.valueOf(getApplicationInfo().name) + " Initialized!");
        Thread.setDefaultUncaughtExceptionHandler((t, ex) -> {
            ex.printStackTrace();
            Logger.e(ex, "Unhandled Exception");
        });
    }

    @Override
    protected void onPostResume() {
        startButton.setOnClickListener(
                (v) -> startActivity(new Intent(MainActivity.this, BluefruitScanActivity.class))
        );
        super.onPostResume();
    }
}

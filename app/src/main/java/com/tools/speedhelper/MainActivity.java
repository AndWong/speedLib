package com.tools.speedhelper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.tools.speedlib.SpeedManager;
import com.tools.speedlib.listener.SpeedListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SpeedManager speedManager = new SpeedManager.Builder()
                .setSpeedCount(6)
                .setDownloadListener(new SpeedListener() {
                    @Override
                    public void speeding(double speed) {
                        Toast.makeText(MainActivity.this,"test : " + speed ,Toast.LENGTH_LONG);
                    }

                    @Override
                    public void finishSpeed(double finalSpeed) {

                    }
                }).builder();
        speedManager.startSpeed();
    }
}

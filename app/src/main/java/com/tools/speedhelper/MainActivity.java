package com.tools.speedhelper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.tools.speedlib.SpeedManager;
import com.tools.speedlib.listener.NetDelayListener;
import com.tools.speedlib.listener.SpeedListener;
import com.tools.speedlib.utils.ConverUtil;
import com.tools.speedlib.views.PointerSpeedView;

import static com.tools.speedlib.utils.ConverUtil.fomartSpeed;


public class MainActivity extends AppCompatActivity {
    private PointerSpeedView speedometer;
    private TextView tx_delay;
    private TextView tx_down;
    private TextView tx_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        speedometer = (PointerSpeedView) findViewById(R.id.speedometer);
        tx_delay = (TextView) findViewById(R.id.tx_delay);
        tx_down = (TextView) findViewById(R.id.tx_down);
        tx_up = (TextView) findViewById(R.id.tx_up);

        SpeedManager speedManager = new SpeedManager.Builder()
                .setNetDelayListener(new NetDelayListener() {
                    @Override
                    public void result(String delay) {
                        tx_delay.setText(delay);
                    }
                })
                .setDownloadListener(new SpeedListener() {
                    @Override
                    public void speeding(double speed) {
                        String[] result = ConverUtil.fomartSpeed(speed);
                        tx_down.setText(result[0] + result[1]);
                        setSpeedView(speed, result);
                    }

                    @Override
                    public void finishSpeed(double finalSpeed) {
                        String[] result = ConverUtil.fomartSpeed(finalSpeed);
                        tx_down.setText(result[0] + result[1]);
                        setSpeedView(finalSpeed, result);
                    }
                })
                .setUpLoadListener(new SpeedListener() {
                    @Override
                    public void speeding(double speed) {
                        String[] result = ConverUtil.fomartSpeed(speed);
                        tx_up.setText(result[0] + result[1]);
                    }

                    @Override
                    public void finishSpeed(double finalSpeed) {
                        String[] result = ConverUtil.fomartSpeed(finalSpeed);
                        tx_up.setText(result[0] + result[1]);
                    }
                })
                .setSpeedCount(6)
                .builder();
        speedManager.startSpeed();
    }

    private void setSpeedView(double speed, String[] result) {
        if (null != result && 2 == result.length) {
            speedometer.setCurrentSpeed(result[0]);
            speedometer.setUnit(result[1]);
            speedometer.speedPercentTo(ConverUtil.getSpeedPercent(speed));
        }
    }
}

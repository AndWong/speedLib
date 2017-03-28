package com.tools.speedhelper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.tools.speedlib.SpeedManager;
import com.tools.speedlib.listener.NetDelayListener;
import com.tools.speedlib.listener.SpeedListener;
import com.tools.speedlib.views.PointerSpeedView;

public class MainActivity extends AppCompatActivity {
    private static final double ONE_128KB = 128 * 1024; //128kb
    private static final double TWO_256KB = 256 * 1024; //256kb
    private static final double THREE_512KB = 512 * 1024; //512kb
    private static final double FOUR_1MB = 1024 * 1024; //1MB
    private static final double FIVE_2MB = 2 * 1024 * 1024; //2MB
    private static final double SIX_5MB = 5 * 1024 * 1024; //5MB
    private static final double SEVEN_10MB = 10 * 1024 * 1024; //10MB
    private static final double EIGHT_20MB = 20 * 1024 * 1024; //20MB
    private static final double NINE_50MB = 50 * 1024 * 1024; //50MB
    private static final double TEN_100MB = 100 * 1024 * 1024; //100MB

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
                        String[] result = fomartSpeed(speed);
                        tx_down.setText(result[0] + result[1]);
                        setSpeedView(speed, result);
                    }

                    @Override
                    public void finishSpeed(double finalSpeed) {
                        String[] result = fomartSpeed(finalSpeed);
                        tx_down.setText(result[0] + result[1]);
                        setSpeedView(finalSpeed, result);
                    }
                })
                .setUpLoadListener(new SpeedListener() {
                    @Override
                    public void speeding(double speed) {
                        String[] result = fomartSpeed(speed);
                        tx_up.setText(result[0] + result[1]);
                    }

                    @Override
                    public void finishSpeed(double finalSpeed) {
                        String[] result = fomartSpeed(finalSpeed);
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
            speedometer.speedPercentTo(getSpeedPercent(speed));
        }
    }

    private int getSpeedPercent(double speed) {
        if (speed >= 0 && speed < ONE_128KB) {
            return conver(speed * 10 / ONE_128KB);
        } else if (speed >= ONE_128KB && speed < TWO_256KB) {
            return conver(speed * 10 / TWO_256KB) + 10;
        } else if (speed >= TWO_256KB && speed < THREE_512KB) {
            return conver(speed * 10 / THREE_512KB) + 20;
        } else if (speed >= THREE_512KB && speed < FOUR_1MB) {
            return conver(speed * 10 / FOUR_1MB) + 30;
        } else if (speed >= FOUR_1MB && speed < FIVE_2MB) {
            return conver(speed * 10 / FIVE_2MB) + 40;
        } else if (speed >= FIVE_2MB && speed < SIX_5MB) {
            return conver(speed * 10 / SIX_5MB) + 50;
        } else if (speed >= SIX_5MB && speed < SEVEN_10MB) {
            return conver(speed * 10 / SEVEN_10MB) + 60;
        } else if (speed >= SEVEN_10MB && speed < EIGHT_20MB) {
            return conver(speed * 10 / EIGHT_20MB) + 70;
        } else if (speed >= EIGHT_20MB && speed < NINE_50MB) {
            return conver(speed * 10 / NINE_50MB) + 80;
        } else {
            return conver(speed * 10 / TEN_100MB) + 90;
        }
    }

    private int conver(double originData) {
        return Integer.parseInt(new java.text.DecimalFormat("0").format(originData));
    }

    private String[] fomartSpeed(double speed) {
        final long UNIT_KB = 1024;
        final long UNIT_MB = UNIT_KB * 1024;
        final long UNIT_GB = UNIT_MB * 1024;
        int unit = 0;
        long temp = (long) speed; //unit B
        while (temp / 1024 > 0) {
            temp = temp / 1024;
            unit++;
        }
        String floatPart = null;
        switch (unit) {
            case 0: //unit B
                return new String[]{temp + "", "B/s"};
            case 1: //unit KB
                floatPart = (long) speed % UNIT_KB + "";
                if (floatPart.length() >= 2) {
                    floatPart = floatPart.substring(0, 2);
                }
                return new String[]{temp + "." + floatPart, "KB/S"};
            case 2: //unit MB
                floatPart = (long) speed % UNIT_MB + "";
                if (floatPart.length() >= 2) {
                    floatPart = floatPart.substring(0, 2);
                }
                return new String[]{temp + "." + floatPart, "MB/S"};
            case 3: //unit GB
                floatPart = (long) speed % UNIT_GB + "";
                if (floatPart.length() >= 2) {
                    floatPart = floatPart.substring(0, 2);
                }
                return new String[]{temp + "." + floatPart, "GB/S"};
            default:
                return new String[]{"0", "B/S"};
        }
    }
}

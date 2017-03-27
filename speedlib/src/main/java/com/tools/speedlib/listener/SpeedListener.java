package com.tools.speedlib.listener;

/**
 * Created by wong on 17-3-27.
 */
public interface SpeedListener {
    void speeding(double speed);
    void finishSpeed(double finalSpeed);
}

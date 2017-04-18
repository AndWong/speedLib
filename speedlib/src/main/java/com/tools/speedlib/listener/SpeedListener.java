package com.tools.speedlib.listener;

/**
 * 上传和下载速度
 * Created by wong on 17-3-27.
 */
public interface SpeedListener {
    void speeding(double downSpeed, double upSpeed);

    void finishSpeed(double finalDownSpeed, double finalUpSpeed);
}

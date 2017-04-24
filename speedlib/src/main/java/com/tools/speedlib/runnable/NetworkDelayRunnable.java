package com.tools.speedlib.runnable;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by wong on 17-4-24.
 */
public class NetworkDelayRunnable implements Runnable {
    private String cmd;
    private boolean isPingSucc;
    private String delayTime;

    public NetworkDelayRunnable(String cmd) {
        this.cmd = cmd;
    }

    public boolean isPingSucc() {
        return isPingSucc;
    }

    public void setPingSucc(boolean pingSucc) {
        isPingSucc = pingSucc;
    }

    public String getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(String delayTime) {
        this.delayTime = delayTime;
    }

    @Override
    public void run() {
        try {
            Process p = Runtime.getRuntime().exec(this.cmd);// -c ping次数
            BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String content = "";
            while ((content = buf.readLine()) != null) {
                // rtt min/avg/max/mdev = 32.745/78.359/112.030/33.451 ms
                if (content.contains("avg")) {
                    String[] delays = content.split("/");
                    setDelayTime(delays[4] + "ms");
                    break;
                }
            }
            // PING的状态
            int status = p.waitFor();
            setPingSucc(0 == status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

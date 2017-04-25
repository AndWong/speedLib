package com.tools.speedlib.utils;

import android.os.Handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 时间任务类
 * Created by wong on 17-4-14.
 */
public class TimerTaskUtil {
    private static Map<HandlerMsg, Timer> timerMap = new HashMap<>();

    /**
     * 设置计时器
     *
     * @param handler
     * @param what
     * @param delay
     * @param period
     */
    public static void setTimer(final Handler handler, final int what, long delay, long period) {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (null != handler) {
                    handler.sendEmptyMessage(what);
                }
            }
        };
        timer.schedule(timerTask, delay, period);

        HandlerMsg handlerMsg = new HandlerMsg(handler, what);
        timerMap.put(handlerMsg, timer);
    }

    /**
     * 设置计时器
     *
     * @param handler
     * @param what
     * @param delay
     */
    public static void setTimer(final Handler handler, final int what, long delay) {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (null != handler) {
                    handler.sendEmptyMessage(what);
                }
            }
        };
        timer.schedule(timerTask, delay);

        HandlerMsg handlerMsg = new HandlerMsg(handler, what);
        timerMap.put(handlerMsg, timer);
    }

    /**
     * 取消任务
     *
     * @param handler
     * @param what
     */
    public static void cacleTimer(Handler handler, int what) {
        if (null != handler) {
            HandlerMsg handlerMsg = new HandlerMsg(handler, what);
            Timer timer = timerMap.get(handlerMsg);
            if (null != timer) {
                timer.cancel();
            }
            timerMap.remove(handlerMsg);
        }
    }

    static class HandlerMsg {
        public Handler msgHandler;
        public int msgWhat;

        public HandlerMsg(Handler msgHandler, int msgWhat) {
            this.msgHandler = msgHandler;
            this.msgWhat = msgWhat;
        }
    }
}
